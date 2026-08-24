package com.seibel.distanthorizons.core.util.objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;

public class RunOnThisThreadExecutorService implements ExecutorService {
   private boolean shutdownCalled = false;

   @Override
   public void execute(Runnable command) {
      command.run();
   }

   @Override
   public void shutdown() {
      this.shutdownCalled = true;
   }

   @NotNull
   @Override
   public List<Runnable> shutdownNow() {
      this.shutdownCalled = true;
      return new ArrayList<>();
   }

   @Override
   public boolean isShutdown() {
      return this.shutdownCalled;
   }

   @Override
   public boolean isTerminated() {
      return this.shutdownCalled;
   }

   @Override
   public boolean awaitTermination(long timeout, TimeUnit unit) {
      this.shutdownCalled = true;
      return true;
   }

   @Override
   public <T> Future<T> submit(Callable<T> task) {
      try {
         return CompletableFuture.completedFuture(task.call());
      } catch (Throwable var3) {
         return CompletableFuture.supplyAsync(() -> {
            throw new CompletionException(var3);
         }, Runnable::run);
      }
   }

   @Override
   public <T> Future<T> submit(Runnable task, T result) {
      try {
         task.run();
         return CompletableFuture.completedFuture(result);
      } catch (Throwable var4) {
         return CompletableFuture.supplyAsync(() -> {
            throw new CompletionException(var4);
         }, Runnable::run);
      }
   }

   @Override
   public Future<?> submit(Runnable task) {
      try {
         task.run();
         return CompletableFuture.completedFuture(null);
      } catch (Throwable var3) {
         return CompletableFuture.supplyAsync(() -> {
            throw new CompletionException(var3);
         }, Runnable::run);
      }
   }

   @Override
   public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) {
      List<Future<T>> futures = new ArrayList<>(tasks.size());

      for (Callable<T> t : tasks) {
         futures.add(this.submit(t));
      }

      return futures;
   }

   @Override
   public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) {
      return this.invokeAll(tasks);
   }

   @Override
   public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws ExecutionException {
      Throwable latestE = null;

      for (Callable<T> t : tasks) {
         try {
            return t.call();
         } catch (Throwable var6) {
            latestE = var6;
         }
      }

      throw new ExecutionException(latestE);
   }

   @Override
   public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws ExecutionException {
      return this.invokeAny(tasks);
   }
}
