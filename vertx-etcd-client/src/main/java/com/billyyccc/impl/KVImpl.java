package com.billyyccc.impl;

import com.billyyccc.KV;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Txn;
import io.etcd.jetcd.kv.CompactResponse;
import io.etcd.jetcd.kv.DeleteResponse;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.kv.PutResponse;
import io.etcd.jetcd.options.CompactOption;
import io.etcd.jetcd.options.DeleteOption;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

public class KVImpl implements KV {
  private Vertx vertx;
  private io.etcd.jetcd.KV delegate;

  public KVImpl(Vertx vertx, io.etcd.jetcd.KV delegate) {
    this.vertx = vertx;
    this.delegate = delegate;
  }

  @Override
  public KV put(ByteSequence key, ByteSequence value, Handler<AsyncResult<PutResponse>> resultHandler) {
    Context context = vertx.getOrCreateContext();
    Util.executeInContext(delegate.put(key, value), context, resultHandler);
    return this;
  }

  @Override
  public KV put(ByteSequence key, ByteSequence value, PutOption option, Handler<AsyncResult<PutResponse>> resultHandler) {
    Context context = vertx.getOrCreateContext();
    Util.executeInContext(delegate.put(key, value, option), context, resultHandler);
    return this;
  }

  @Override
  public KV get(ByteSequence key, Handler<AsyncResult<GetResponse>> resultHandler) {
    Context context = vertx.getOrCreateContext();
    Util.executeInContext(delegate.get(key), context, resultHandler);
    return this;
  }

  @Override
  public KV get(ByteSequence key, GetOption option, Handler<AsyncResult<GetResponse>> resultHandler) {
    Context context = vertx.getOrCreateContext();
    Util.executeInContext(delegate.get(key, option), context, resultHandler);
    return this;
  }

  @Override
  public KV delete(ByteSequence key, Handler<AsyncResult<DeleteResponse>> resultHandler) {
    Context context = vertx.getOrCreateContext();
    Util.executeInContext(delegate.delete(key), context, resultHandler);
    return this;
  }

  @Override
  public KV delete(ByteSequence key, DeleteOption option, Handler<AsyncResult<DeleteResponse>> resultHandler) {
    Context context = vertx.getOrCreateContext();
    Util.executeInContext(delegate.delete(key, option), context, resultHandler);
    return this;
  }

  @Override
  public KV compact(long rev, Handler<AsyncResult<CompactResponse>> resultHandler) {
    Context context = vertx.getOrCreateContext();
    Util.executeInContext(delegate.compact(rev), context, resultHandler);
    return this;
  }

  @Override
  public KV compact(long rev, CompactOption option, Handler<AsyncResult<CompactResponse>> resultHandler) {
    Context context = vertx.getOrCreateContext();
    Util.executeInContext(delegate.compact(rev, option), context, resultHandler);
    return this;
  }

  @Override
  public Txn txn() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void close() {
    delegate.close();
  }
}
