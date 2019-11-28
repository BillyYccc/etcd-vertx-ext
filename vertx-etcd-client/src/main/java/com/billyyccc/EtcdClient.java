package com.billyyccc;

import com.billyyccc.impl.EtcdClientImpl;
import io.etcd.jetcd.Client;
import io.vertx.core.Vertx;

public interface EtcdClient {
  static EtcdClient create(Vertx vertx, Client client) {
    return new EtcdClientImpl(vertx, client);
  }

  KV getKVClient();

  Lease getLeaseClient();

  void close();
}
