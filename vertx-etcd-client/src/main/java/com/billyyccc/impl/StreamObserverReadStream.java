package com.billyyccc.impl;

import io.grpc.stub.StreamObserver;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.streams.ReadStream;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

class StreamObserverReadStream<T> implements ReadStream<T>, StreamObserver<T> {

  private final Context context;
  private Handler<T> handler = event -> {};
  private Handler<Void> endHandler = v -> {};
  private Handler<Throwable> exceptionHandler = t -> {};

  private final Consumer<StreamObserverReadStream<T>> operation;
  private final AtomicBoolean paused = new AtomicBoolean(true);

  StreamObserverReadStream(Context context, Consumer<StreamObserverReadStream<T>> operation) {
    Objects.requireNonNull(operation);
    this.context = context;
    this.operation = operation;
  }

  @Override
  public StreamObserverReadStream<T> exceptionHandler(Handler<Throwable> exceptionHandler) {
    Objects.requireNonNull(exceptionHandler, "ExceptionHandler can not be null");
    this.exceptionHandler = exceptionHandler;
    return this;
  }

  @Override
  public StreamObserverReadStream<T> handler(Handler<T> handler) {
    Objects.requireNonNull(handler, "Handler can not be null");
    this.handler = handler;
    return this;
  }

  @Override
  public StreamObserverReadStream<T> pause() {
    throw new UnsupportedOperationException();
  }

  @Override
  public StreamObserverReadStream<T> resume() {
    if (paused.compareAndSet(true, false)) {
      operation.accept(this);
      return this;
    } else {
      throw new IllegalStateException("The stream has been resumed");
    }
  }

  @Override
  public StreamObserverReadStream<T> fetch(long amount) {
    throw new UnsupportedOperationException();
  }

  @Override
  public StreamObserverReadStream<T> endHandler(Handler<Void> endHandler) {
    Objects.requireNonNull(endHandler, "EndHandler can not be null");
    this.endHandler = endHandler;
    return this;
  }

  @Override
  public void onNext(T event) {
    context.runOnContext(v -> handler.handle(event));
  }

  @Override
  public void onError(Throwable throwable) {
    context.runOnContext(v -> exceptionHandler.handle(throwable));
  }

  @Override
  public void onCompleted() {
    context.runOnContext(v -> endHandler.handle(null));
  }

}
