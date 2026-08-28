/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.util.concurrent.ThreadFactoryBuilder
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.common.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import mezz.jei.common.util.DelegatedFuture;
import mezz.jei.common.util.IDelayedExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public final class DelayedExecutor
implements IDelayedExecutor {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ScheduledExecutorService service;
    private final Duration shutdownTimeout;
    private final Set<ScheduledTask> scheduledTasks = ConcurrentHashMap.newKeySet();

    public DelayedExecutor(Duration shutdownTimeout) {
        this(shutdownTimeout, DelayedExecutor.createDefaultService());
    }

    public DelayedExecutor(Duration shutdownTimeout, ScheduledExecutorService service) {
        this.service = service;
        this.shutdownTimeout = shutdownTimeout;
    }

    static ScheduledThreadPoolExecutor createDefaultService() {
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("JEI Delayed Executor %d").build();
        ScheduledThreadPoolExecutor service = new ScheduledThreadPoolExecutor(1, threadFactory);
        service.setRemoveOnCancelPolicy(true);
        service.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        return service;
    }

    @Override
    public Future<?> schedule(Runnable command, Duration delay) {
        ScheduledTask scheduledTask = new ScheduledTask(command);
        this.scheduledTasks.add(scheduledTask);
        try {
            ScheduledFuture<?> future = this.service.schedule(scheduledTask, delay.toMillis(), TimeUnit.MILLISECONDS);
            scheduledTask.setFuture(future);
            return new TrackedFuture(future, scheduledTask);
        }
        catch (RuntimeException e) {
            this.scheduledTasks.remove(scheduledTask);
            throw e;
        }
    }

    public void shutdown() {
        this.runScheduledTasksImmediately();
        this.service.shutdown();
        try {
            if (!this.service.awaitTermination(this.shutdownTimeout.toMillis(), TimeUnit.MILLISECONDS)) {
                this.forceShutdown("Timed out waiting for delayed tasks to finish.");
            }
        }
        catch (InterruptedException ignored) {
            this.forceShutdown("Interrupted while waiting for delayed tasks to finish.");
            Thread.currentThread().interrupt();
        }
    }

    private void runScheduledTasksImmediately() {
        for (ScheduledTask scheduledTask : this.scheduledTasks) {
            this.scheduledTasks.remove(scheduledTask);
            if (!scheduledTask.cancel()) continue;
            try {
                this.service.execute(() -> this.runScheduledTaskDuringShutdown(scheduledTask));
            }
            catch (RejectedExecutionException e) {
                LOGGER.error("Failed to execute delayed task during shutdown.", (Throwable)e);
            }
        }
    }

    private void runScheduledTaskDuringShutdown(ScheduledTask scheduledTask) {
        try {
            scheduledTask.command().run();
        }
        catch (LinkageError | RuntimeException e) {
            LOGGER.error("Failed to execute delayed task during shutdown.", e);
        }
    }

    private void forceShutdown(String message) {
        List<Runnable> droppedTasks = this.service.shutdownNow();
        if (droppedTasks.isEmpty()) {
            LOGGER.error("{} Forcing shutdown.", (Object)message);
        } else {
            LOGGER.error("{} Forcing shutdown. {} delayed tasks never started.", (Object)message, (Object)droppedTasks.size());
        }
    }

    private final class ScheduledTask
    implements Runnable {
        private final Runnable command;
        @Nullable
        private volatile Future<?> future;

        private ScheduledTask(Runnable command) {
            this.command = command;
        }

        @Override
        public void run() {
            try {
                this.command.run();
            }
            finally {
                DelayedExecutor.this.scheduledTasks.remove(this);
            }
        }

        public Runnable command() {
            return this.command;
        }

        public void setFuture(Future<?> future) {
            this.future = future;
        }

        public boolean cancel() {
            Future<?> future = this.future;
            if (future == null) {
                return true;
            }
            future.cancel(false);
            return future.isCancelled();
        }
    }

    private final class TrackedFuture<T>
    implements DelegatedFuture<T> {
        private final Future<T> delegate;
        private final ScheduledTask scheduledTask;

        private TrackedFuture(Future<T> delegate, ScheduledTask scheduledTask) {
            this.delegate = delegate;
            this.scheduledTask = scheduledTask;
        }

        @Override
        public Future<T> getDelegate() {
            return this.delegate;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            boolean canceled = this.delegate.cancel(mayInterruptIfRunning);
            if (canceled) {
                DelayedExecutor.this.scheduledTasks.remove(this.scheduledTask);
            }
            return canceled;
        }
    }
}

