package com.billyyccc.impl;

import com.billyyccc.Lease;
import io.etcd.jetcd.lease.LeaseGrantResponse;
import io.etcd.jetcd.lease.LeaseKeepAliveResponse;
import io.etcd.jetcd.lease.LeaseRevokeResponse;
import io.etcd.jetcd.lease.LeaseTimeToLiveResponse;
import io.etcd.jetcd.options.LeaseOption;
import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.streams.ReadStream;

public class LeaseImpl implements Lease {
  private Vertx vertx;
  private io.etcd.jetcd.Lease delegate;

  public LeaseImpl(Vertx vertx, io.etcd.jetcd.Lease delegate) {
    this.vertx = vertx;
    this.delegate = delegate;
  }

  @Override
  public Lease grant(long ttl, Handler<AsyncResult<LeaseGrantResponse>> resultHandler) {
    Context context = vertx.getOrCreateContext();
    Util.executeInContext(delegate.grant(ttl), context, resultHandler);
    return this;
  }

  @Override
  public Lease revoke(long leaseId, Handler<AsyncResult<LeaseRevokeResponse>> resultHandler) {
    Context context = vertx.getOrCreateContext();
    Util.executeInContext(delegate.revoke(leaseId), context, resultHandler);
    return this;
  }

  @Override
  public Lease keepAliveOnce(long leaseId, Handler<AsyncResult<LeaseKeepAliveResponse>> resultHandler) {
    Context context = vertx.getOrCreateContext();
    Util.executeInContext(delegate.keepAliveOnce(leaseId), context, resultHandler);
    return this;
  }

  @Override
  public Lease timeToLive(long leaseId, LeaseOption leaseOption, Handler<AsyncResult<LeaseTimeToLiveResponse>> resultHandler) {
    Context context = vertx.getOrCreateContext();
    Util.executeInContext(delegate.timeToLive(leaseId, leaseOption), context, resultHandler);
    return this;
  }

  @Override
  public ReadStream<LeaseKeepAliveResponse> keepAlive(long leaseId) {
    Context context = vertx.getOrCreateContext();
    StreamObserverReadStream<LeaseKeepAliveResponse> streamObserverReadStream = new StreamObserverReadStream<>(context, (leaseKeepAliveResponseStreamObserverReadStream) -> delegate.keepAlive(leaseId, leaseKeepAliveResponseStreamObserverReadStream));
    return streamObserverReadStream;
  }

  @Override
  public void close() {
    delegate.close();
  }
}
