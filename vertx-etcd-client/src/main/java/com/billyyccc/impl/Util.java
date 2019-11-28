package com.billyyccc.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import java.util.concurrent.CompletableFuture;

final class Util {
  static <T> void executeInContext(CompletableFuture<T> completableFuture, Context context, Handler<AsyncResult<T>> resultHandler) {
    completableFuture.whenCompleteAsync((result, throwable) -> {
      context.runOnContext(v -> {
        if (throwable == null) {
          resultHandler.handle(Future.succeededFuture(result));
        } else {
          resultHandler.handle(Future.failedFuture(throwable));
        }
      });
    });
  }
}
