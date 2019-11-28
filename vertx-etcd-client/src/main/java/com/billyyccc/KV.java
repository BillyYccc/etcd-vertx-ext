package com.billyyccc;

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
import io.vertx.core.Handler;

public interface KV {
  KV put(ByteSequence key, ByteSequence value, Handler<AsyncResult<PutResponse>> resultHandler);

  KV put(ByteSequence key, ByteSequence value,
         PutOption option, Handler<AsyncResult<PutResponse>> resultHandler);

  KV get(ByteSequence key, Handler<AsyncResult<GetResponse>> resultHandler);

  KV get(ByteSequence key, GetOption option, Handler<AsyncResult<GetResponse>> resultHandler);

  KV delete(ByteSequence key, Handler<AsyncResult<DeleteResponse>> resultHandler);

  KV delete(ByteSequence key, DeleteOption option, Handler<AsyncResult<DeleteResponse>> resultHandler);

  KV compact(long rev, Handler<AsyncResult<CompactResponse>> resultHandler);

  KV compact(long rev, CompactOption option, Handler<AsyncResult<CompactResponse>> resultHandler);

  Txn txn();

  void close();
}
