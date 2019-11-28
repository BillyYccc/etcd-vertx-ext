package com.billyyccc;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.launcher.junit5.EtcdClusterExtension;
import io.etcd.jetcd.lease.LeaseKeepAliveResponse;
import io.etcd.jetcd.options.LeaseOption;
import io.etcd.jetcd.options.PutOption;
import io.vertx.core.Vertx;
import io.vertx.core.streams.ReadStream;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;

@ExtendWith(VertxExtension.class)
class EtcdTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(EtcdTest.class);

  @RegisterExtension
  public static EtcdClusterExtension cluster = new EtcdClusterExtension("etcd-kv", 3, false);

  private static KV kv;
  private static Lease lease;

  @BeforeAll
  static void setUp(Vertx vertx) throws Exception {
    Client client = Client.builder().endpoints(cluster.getClientEndpoints()).build();
    EtcdClient etcdClient = EtcdClient.create(vertx, client);
    kv = etcdClient.getKVClient();
    lease = etcdClient.getLeaseClient();
  }

  @AfterAll
  static void tearDown() {
    cluster.close();
  }

  @Test
  @DisplayName("KV put/get operations test")
  void testKvOperation(VertxTestContext ctx) {
    kv.put(ByteSequence.from("test_key1", StandardCharsets.UTF_8), ByteSequence.from("test_value1", StandardCharsets.UTF_8), ctx.succeeding(putResp -> {
      // we're on Vert.x eventloop :-)
      LOGGER.info("KV PUT SUCCESS");
      boolean hasPrev = putResp.hasPrevKv();
      ctx.verify(() -> Assert.assertFalse(hasPrev));

      kv.get(ByteSequence.from("test_key1", StandardCharsets.UTF_8), ctx.succeeding(getResp -> {
        LOGGER.info("KV GET SUCCESS");
        long count = getResp.getCount();
        ctx.verify(() -> Assert.assertEquals(1, count));
        List<KeyValue> keyValues = getResp.getKvs();
        ctx.verify(() -> Assert.assertEquals(1, keyValues.size()));
        KeyValue keyValue = keyValues.get(0);
        ctx.verify(() -> {
          Assert.assertEquals("test_key1", keyValue.getKey().toString(StandardCharsets.UTF_8));
          Assert.assertEquals("test_value1", keyValue.getValue().toString(StandardCharsets.UTF_8));
        });
        ctx.completeNow();
      }));
    }));
  }

  @Test
  @DisplayName("Lease test")
  void testLease(VertxTestContext ctx) {
    lease.grant(20, ctx.succeeding(leaseGrantResp -> {
      ctx.verify(() -> Assert.assertEquals(20, leaseGrantResp.getTTL()));
      final long leaseId = leaseGrantResp.getID();

      ReadStream<LeaseKeepAliveResponse> keepAliveResponseReadStream = lease.keepAlive(leaseId);
      keepAliveResponseReadStream.exceptionHandler(Throwable::printStackTrace);
      keepAliveResponseReadStream.handler(resp -> ctx.verify(() -> {
        LOGGER.info("Receive keepalive response");
        Assert.assertEquals(leaseId, leaseGrantResp.getID());
      }));

      keepAliveResponseReadStream.resume(); // must call this to get the stream started

      kv.put(ByteSequence.from("lease_key", StandardCharsets.UTF_8), ByteSequence.from("lease_value", StandardCharsets.UTF_8), PutOption.newBuilder().withLeaseId(leaseId).build(),
        ctx.succeeding(putResp -> {
          lease.timeToLive(leaseId, LeaseOption.newBuilder().withAttachedKeys().build(), ctx.succeeding(leaseTimeToLiveResp1 -> {
            ctx.verify(() -> Assert.assertEquals(1, leaseTimeToLiveResp1.getKeys().size()));
            lease.revoke(leaseId, ctx.succeeding(leaseRevokeResp -> {
              LOGGER.info("revoke success...");
              lease.timeToLive(leaseId, LeaseOption.newBuilder().withAttachedKeys().build(), ctx.succeeding(leaseTimeToLiveResp2 -> {
                ctx.verify(() -> Assert.assertTrue(leaseTimeToLiveResp2.getKeys().isEmpty()));
                ctx.completeNow();
              }));
            }));
          }));
        }));
    }));
  }
}
