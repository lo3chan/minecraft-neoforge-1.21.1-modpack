package DistantHorizons.libraries.electronwill.nightconfig.core.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public final class FileWatcher {
   private static final AtomicInteger instanceCount = new AtomicInteger(0);
   private static volatile FileWatcher DEFAULT_INSTANCE = null;
   private static final Duration DEFAULT_SERVICE_POLL_TIMEOUT = Duration.ofMillis(200L);
   private static final Duration DEFAULT_DEBOUNCE_TIME = Duration.ofMillis(500L);
   private final ThreadGroup threadGroup;
   private final AtomicInteger threadCount = new AtomicInteger(0);
   private final ConcurrentMap<FileSystem, FileWatcher.FsWatcher> watchers = new ConcurrentHashMap<>();
   private final Consumer<Throwable> exceptionHandler;
   private final Duration debounceTime;
   private final long servicePollTimeoutNanos;
   private final int instanceId;
   private volatile boolean running = true;

   public static synchronized FileWatcher defaultInstance() {
      if (DEFAULT_INSTANCE == null || !DEFAULT_INSTANCE.running) {
         DEFAULT_INSTANCE = new FileWatcher();
      }

      return DEFAULT_INSTANCE;
   }

   public FileWatcher() {
      this(DEFAULT_DEBOUNCE_TIME);
   }

   public FileWatcher(Consumer<Exception> exceptionHandler) {
      this(DEFAULT_DEBOUNCE_TIME, t -> {
         if (t instanceof Exception) {
            exceptionHandler.accept((Exception)t);
         } else {
            exceptionHandler.accept(new RuntimeException(t));
         }
      });
   }

   public FileWatcher(Duration debounceTime) {
      this(debounceTime, Throwable::printStackTrace);
   }

   public FileWatcher(Duration debounceTime, Consumer<Throwable> exceptionHandler) {
      this(debounceTime, DEFAULT_SERVICE_POLL_TIMEOUT, exceptionHandler);
   }

   FileWatcher(Duration debounceTime, Duration servicePollTimeout, Consumer<Throwable> exceptionHandler) {
      this.instanceId = instanceCount.getAndIncrement();
      this.debounceTime = debounceTime;
      this.servicePollTimeoutNanos = servicePollTimeout.toNanos();
      this.exceptionHandler = exceptionHandler;
      this.threadGroup = new ThreadGroup("watchers-" + this.instanceId);
   }

   public void addWatch(File file, Runnable changeHandler) {
      this.addWatch(file.toPath(), changeHandler);
   }

   public void addWatch(Path file, Runnable changeHandler) {
      this.addOrPutWatch(file, changeHandler, FileWatcher.ControlMessageKind.ADD, null);
   }

   public CompletableFuture<Void> addWatchFuture(Path file, Runnable changeHandler) {
      this.failIfStopped();
      CompletableFuture<Void> future = new CompletableFuture<>();

      try {
         this.addOrPutWatch(file, changeHandler, FileWatcher.ControlMessageKind.ADD, future);
      } catch (Exception var5) {
         future.completeExceptionally(var5);
      }

      return future;
   }

   public void setWatch(File file, Runnable changeHandler) {
      this.setWatch(file.toPath(), changeHandler);
   }

   public void setWatch(Path file, Runnable changeHandler) {
      this.addOrPutWatch(file, changeHandler, FileWatcher.ControlMessageKind.PUT, null);
   }

   public CompletableFuture<Void> setWatchFuture(Path file, Runnable changeHandler) {
      this.failIfStopped();
      CompletableFuture<Void> future = new CompletableFuture<>();

      try {
         this.addOrPutWatch(file, changeHandler, FileWatcher.ControlMessageKind.PUT, future);
      } catch (Exception var5) {
         future.completeExceptionally(var5);
      }

      return future;
   }

   private void addOrPutWatch(Path file, Runnable changeHandler, FileWatcher.ControlMessageKind kind, CompletableFuture<Void> future) {
      this.failIfStopped();

      try {
         if (Files.exists(file) && Files.readAttributes(file, BasicFileAttributes.class).isDirectory()) {
            throw new IllegalArgumentException("FileWatcher is designed to watch files but this path is a directory, not a file: " + file);
         }
      } catch (IOException var9) {
         throw new FileWatcher.WatchingException("Failed to get information about path: " + file, var9);
      }

      FileWatcher.CanonicalPath canon = FileWatcher.CanonicalPath.from(file);
      FileSystem fs = canon.parentDirectory.getFileSystem();

      try {
         FileWatcher.FsWatcher watcher = this.watchers.computeIfAbsent(fs, k -> {
            try {
               WatchService service = fs.newWatchService();
               FileWatcher.FsWatcher w = new FileWatcher.FsWatcher(this.exceptionHandler, this.debounceTime, this.servicePollTimeoutNanos, service);
               String threadName = "config-file-watcher-" + this.instanceId + "-" + this.threadCount.getAndIncrement();
               Thread t = new Thread(this.threadGroup, w, threadName);
               t.setDaemon(true);
               t.start();
               return w;
            } catch (IOException var8x) {
               throw new FileWatcher.WatchingException("Failed to start a new watcher thread for directory " + canon.parentDirectory, var8x);
            }
         });
         watcher.send(FileWatcher.ControlMessage.addOrPut(kind, canon, changeHandler, future));
      } catch (Exception var8) {
         throw new FileWatcher.WatchingException("Failed to watch path '" + file + "', canonical path '" + canon + "'", var8);
      }
   }

   public void removeWatch(File file) {
      this.removeWatch(file.toPath());
   }

   public void removeWatch(Path file) {
      this.failIfStopped();
      this.removeWatch(file, null);
   }

   public CompletableFuture<Void> removeWatchFuture(Path file) {
      this.failIfStopped();
      CompletableFuture<Void> future = new CompletableFuture<>();

      try {
         this.removeWatch(file, future);
      } catch (Exception var4) {
         future.completeExceptionally(var4);
      }

      return future;
   }

   private void removeWatch(Path file, CompletableFuture<Void> future) {
      FileWatcher.CanonicalPath canon = FileWatcher.CanonicalPath.from(file);
      FileSystem fs = canon.parentDirectory.getFileSystem();
      FileWatcher.FsWatcher watcher = this.watchers.get(fs);
      if (watcher != null) {
         watcher.send(FileWatcher.ControlMessage.remove(canon, future));
      }
   }

   public void stop() {
      this.running = false;

      for (FileWatcher.FsWatcher watcher : this.watchers.values()) {
         watcher.send(FileWatcher.ControlMessage.poison(null));
      }

      this.threadGroup.interrupt();
   }

   public CompletableFuture<Void> stopFuture() {
      this.running = false;
      Collection<FileWatcher.FsWatcher> allWatchers = this.watchers.values();
      if (allWatchers.size() == 0) {
         return CompletableFuture.completedFuture(null);
      } else {
         CompletableFuture<Void> main = new CompletableFuture<>();
         AtomicInteger remainingChildCount = new AtomicInteger(allWatchers.size());

         for (FileWatcher.FsWatcher watcher : allWatchers) {
            CompletableFuture<Void> f = new CompletableFuture<>();
            f.handle((ok, err) -> {
               int remaining = remainingChildCount.decrementAndGet();
               if (remaining == 0) {
                  if (err == null) {
                     main.complete(null);
                  } else {
                     main.completeExceptionally(err);
                  }
               }

               return null;
            });
            watcher.send(FileWatcher.ControlMessage.poison(f));
         }

         this.threadGroup.interrupt();
         return main;
      }
   }

   private void failIfStopped() {
      if (!this.running) {
         throw new IllegalStateException("FileWatcher " + this.instanceId + " has been stopped and cannot be used anymore.");
      }
   }

   private static class CanonicalPath {
      public final Path parentDirectory;
      public final Path fileName;

      private CanonicalPath(Path parentDirectory, Path fileName) {
         this.parentDirectory = parentDirectory;
         this.fileName = fileName;
      }

      public static FileWatcher.CanonicalPath from(Path fullFilePath) {
         try {
            Path dir;
            Path fileName;
            try {
               Path realFile = fullFilePath.toRealPath();
               dir = realFile.getParent();
               fileName = realFile.getFileName();
            } catch (NoSuchFileException var4) {
               dir = fullFilePath.getParent().toRealPath();
               fileName = fullFilePath.getFileName();
            }

            return new FileWatcher.CanonicalPath(dir, fileName);
         } catch (IOException var5) {
            throw new FileWatcher.WatchingException(
               "Failed to determine the canonical path of: " + fullFilePath + "\nHint: make sure that all parent directories exist.", var5
            );
         }
      }

      @Override
      public String toString() {
         return this.parentDirectory + "/" + this.fileName;
      }
   }

   private static final class ControlMessage {
      private final FileWatcher.ControlMessageKind kind;
      private final FileWatcher.CanonicalPath path;
      private final Runnable handler;
      private final CompletableFuture<Void> future;

      private ControlMessage(FileWatcher.ControlMessageKind kind, FileWatcher.CanonicalPath path, Runnable handler, CompletableFuture<Void> future) {
         this.path = path;
         this.kind = kind;
         this.handler = handler;
         this.future = future;
      }

      static FileWatcher.ControlMessage addOrPut(
         FileWatcher.ControlMessageKind kind, FileWatcher.CanonicalPath path, Runnable handler, CompletableFuture<Void> future
      ) {
         if (kind != FileWatcher.ControlMessageKind.ADD && kind != FileWatcher.ControlMessageKind.PUT) {
            throw new IllegalArgumentException("Unexpected message kind " + kind);
         } else {
            return new FileWatcher.ControlMessage(kind, path, handler, future);
         }
      }

      static FileWatcher.ControlMessage remove(FileWatcher.CanonicalPath path, CompletableFuture<Void> future) {
         return new FileWatcher.ControlMessage(FileWatcher.ControlMessageKind.REMOVE, path, null, future);
      }

      static FileWatcher.ControlMessage poison(CompletableFuture<Void> future) {
         return new FileWatcher.ControlMessage(FileWatcher.ControlMessageKind.POISON, null, null, future);
      }

      @Override
      public String toString() {
         return "ControlMessage[kind=" + this.kind + ", path=" + this.path + ", handler=" + this.handler + ", future=" + this.future + "]";
      }
   }

   private static enum ControlMessageKind {
      PUT,
      ADD,
      REMOVE,
      POISON;
   }

   private static final class FsWatcher implements Runnable {
      private final Consumer<Throwable> exceptionHandler;
      private final Duration debounceTime;
      private final long servicePollTimeoutNanos;
      private final WatchService watchService;
      private final Map<Path, FileWatcher.WatchedDirectory> watchedDirectories = new HashMap<>();
      private final ConcurrentLinkedQueue<FileWatcher.ControlMessage> controlMessages = new ConcurrentLinkedQueue<>();

      FsWatcher(Consumer<Throwable> exceptionHandler, Duration debounceTime, long servicePollTimeoutNanos, WatchService watchService) {
         this.exceptionHandler = exceptionHandler;
         this.debounceTime = debounceTime;
         this.servicePollTimeoutNanos = servicePollTimeoutNanos;
         this.watchService = watchService;
      }

      void send(FileWatcher.ControlMessage msg) {
         this.controlMessages.add(msg);
      }

      private FileWatcher.WatchedDirectory watchDirectory(Path dir, CompletableFuture<Void> future) {
         return this.watchedDirectories.computeIfAbsent(dir, k -> {
            try {
               WatchKey key = dir.register(this.watchService, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_CREATE);
               return new FileWatcher.WatchedDirectory(key, new HashMap<>(8));
            } catch (Exception var6) {
               if (future != null) {
                  future.completeExceptionally(var6);
               } else {
                  this.exceptionHandler.accept(var6);
               }

               return null;
            }
         });
      }

      @Override
      public void run() {
         ThreadFactory threadFactory = new FileWatcher.NamedDaemonThreadFactory("FileWatcher-", "-thread-");
         ScheduledExecutorService executor = Executors.newScheduledThreadPool(1, threadFactory);
         CompletableFuture<Void> shutdownFuture = null;

         while (true) {
            FileWatcher.ControlMessage msg;
            label89:
            while ((msg = this.controlMessages.poll()) == null) {
               WatchKey key = null;

               try {
                  key = this.watchService.poll(this.servicePollTimeoutNanos, TimeUnit.NANOSECONDS);
               } catch (InterruptedException var16) {
                  continue;
               }

               if (key != null) {
                  Path dir = (Path)key.watchable();
                  FileWatcher.WatchedDirectory w = this.watchedDirectories.get(dir);

                  for (WatchEvent<?> evt : key.pollEvents()) {
                     Kind<?> kind = evt.kind();
                     if (kind == StandardWatchEventKinds.OVERFLOW) {
                        this.exceptionHandler.accept(new FileWatcher.WatchingException("Got watch event OVERFLOW"));
                     } else {
                        Path file = (Path)evt.context();
                        DebouncedRunnable changeHandler = w.fileChangeHandlers.get(file);
                        if (changeHandler != null) {
                           try {
                              changeHandler.run(executor);
                           } catch (Exception var14) {
                              this.exceptionHandler.accept(var14);
                           }
                        }
                     }

                     if (Thread.interrupted()) {
                        continue label89;
                     }
                  }

                  boolean valid = key.reset();
                  if (!valid) {
                     this.watchedDirectories.remove(dir);
                  }
               }
            }

            FileWatcher.CanonicalPath path = msg.path;
            CompletableFuture<Void> future = msg.future;
            switch (msg.kind) {
               case ADD:
                  Path dirxx = path.parentDirectory;
                  Path fileNamexx = path.fileName;
                  FileWatcher.WatchedDirectory wxx = this.watchDirectory(dirxx, future);
                  if (wxx != null) {
                     DebouncedRunnable existingHandler = wxx.fileChangeHandlers.get(fileNamexx);
                     DebouncedRunnable newHandler;
                     if (existingHandler != null) {
                        newHandler = existingHandler.andThen(msg.handler);
                     } else {
                        newHandler = new DebouncedRunnable(msg.handler, this.debounceTime);
                     }

                     wxx.fileChangeHandlers.put(fileNamexx, newHandler);
                  }
                  break;
               case PUT:
                  Path dirx = path.parentDirectory;
                  Path fileNamex = path.fileName;
                  FileWatcher.WatchedDirectory wx = this.watchDirectory(dirx, future);
                  if (wx != null) {
                     DebouncedRunnable newHandler = new DebouncedRunnable(msg.handler, this.debounceTime);
                     wx.fileChangeHandlers.put(fileNamex, newHandler);
                  }
                  break;
               case REMOVE:
                  Path dir = path.parentDirectory;
                  Path fileName = path.fileName;
                  FileWatcher.WatchedDirectory w = this.watchedDirectories.get(dir);
                  if (w != null) {
                     w.fileChangeHandlers.remove(fileName);
                     if (w.fileChangeHandlers.isEmpty()) {
                        w.key.cancel();
                     }
                  }
                  break;
               case POISON:
                  try {
                     executor.shutdown();
                     this.watchService.close();
                     this.watchedDirectories.clear();
                  } catch (Exception var15) {
                     if (future != null) {
                        future.completeExceptionally(var15);
                     } else {
                        this.exceptionHandler.accept(var15);
                     }
                  }

                  if (future != null) {
                     future.complete(null);
                  }

                  return;
            }

            if (future != null) {
               future.complete(null);
            }
         }
      }
   }

   private static class NamedDaemonThreadFactory implements ThreadFactory {
      private static final AtomicInteger FACTORY_NUMBER = new AtomicInteger(1);
      private final AtomicInteger threadNumber = new AtomicInteger(1);
      private final String namePrefix;

      NamedDaemonThreadFactory(String prefix, String suffix) {
         this.namePrefix = prefix + FACTORY_NUMBER.getAndIncrement() + suffix;
      }

      @Override
      public Thread newThread(Runnable r) {
         Thread t = new Thread(r, this.namePrefix + this.threadNumber.getAndIncrement());
         t.setDaemon(true);
         t.setPriority(5);
         return t;
      }
   }

   private static final class WatchedDirectory {
      private final WatchKey key;
      private final Map<Path, DebouncedRunnable> fileChangeHandlers;

      WatchedDirectory(WatchKey key, Map<Path, DebouncedRunnable> fileChangeHandlers) {
         this.key = Objects.requireNonNull(key);
         this.fileChangeHandlers = Objects.requireNonNull(fileChangeHandlers);
      }
   }

   public static class WatchingException extends RuntimeException {
      public WatchingException(String message, Throwable cause) {
         super(message, cause);
      }

      public WatchingException(Throwable cause) {
         super(cause);
      }

      public WatchingException(String message) {
         super(message);
      }
   }
}
