package com.billyyccc.impl;

import com.billyyccc.EtcdClient;
import com.billyyccc.KV;
import com.billyyccc.Lease;
import io.etcd.jetcd.Client;
import io.vertx.core.Vertx;

public class EtcdClientImpl implements EtcdClient {
  private Vertx vertx;
  private Client delegate;

  public EtcdClientImpl(Vertx vertx, Client delegate) {
    this.vertx = vertx;
    this.delegate = delegate;
  }

  @Override
  public KV getKVClient() {
    return new KVImpl(vertx, delegate.getKVClient());
  }

  @Override
  public Lease getLeaseClient() {
    return new LeaseImpl(vertx, delegate.getLeaseClient());
  }

  @Override
  public void close() {
    delegate.close();
  }
}
