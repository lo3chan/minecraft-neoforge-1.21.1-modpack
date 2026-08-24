package mezz.jei.common.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.Future.State;

public interface DelegatedFuture<T> extends Future<T> {
   Future<T> getDelegate();

   @Override
   default boolean cancel(boolean mayInterruptIfRunning) {
      return this.getDelegate().cancel(mayInterruptIfRunning);
   }

   @Override
   default boolean isCancelled() {
      return this.getDelegate().isCancelled();
   }

   @Override
   default boolean isDone() {
      return this.getDelegate().isDone();
   }

   @Override
   default T get() throws InterruptedException, ExecutionException {
      return this.getDelegate().get();
   }

   @Override
   default T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
      return this.getDelegate().get(timeout, unit);
   }

   default T resultNow() {
      return (T)this.getDelegate().resultNow();
   }

   default Throwable exceptionNow() {
      return this.getDelegate().exceptionNow();
   }

   default State state() {
      return this.getDelegate().state();
   }
}
