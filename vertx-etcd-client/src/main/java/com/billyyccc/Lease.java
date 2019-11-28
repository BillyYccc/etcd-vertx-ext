package com.billyyccc;

import io.etcd.jetcd.lease.LeaseGrantResponse;
import io.etcd.jetcd.lease.LeaseKeepAliveResponse;
import io.etcd.jetcd.lease.LeaseRevokeResponse;
import io.etcd.jetcd.lease.LeaseTimeToLiveResponse;
import io.etcd.jetcd.options.LeaseOption;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.streams.ReadStream;

public interface Lease {
  Lease grant(long ttl, Handler<AsyncResult<LeaseGrantResponse>> resultHandler);

  Lease revoke(long leaseId, Handler<AsyncResult<LeaseRevokeResponse>> resultHandler);

  Lease keepAliveOnce(long leaseId, Handler<AsyncResult<LeaseKeepAliveResponse>> resultHandler);

  Lease timeToLive(long leaseId, LeaseOption leaseOption, Handler<AsyncResult<LeaseTimeToLiveResponse>> resultHandler);

  ReadStream<LeaseKeepAliveResponse> keepAlive(long leaseId);

  void close();
}
