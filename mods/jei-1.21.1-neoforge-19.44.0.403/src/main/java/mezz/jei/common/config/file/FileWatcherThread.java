/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.concurrent.ThreadSafe
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package mezz.jei.common.config.file;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.concurrent.ThreadSafe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ThreadSafe
public class FileWatcherThread
extends Thread {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int quietTimeMs = 500;
    private static final int recheckDirectoriesMs = 60000;
    private final WatchService watchService;
    private final Map<Path, Runnable> callbacks;
    private final Set<Path> directoriesToWatch;
    private final Map<WatchKey, Path> watchedDirectories = new HashMap<WatchKey, Path>();
    private final Set<Path> changedPaths = new HashSet<Path>();
    private long nextDirectoryCheckTime = System.currentTimeMillis();

    public FileWatcherThread(String name) throws IOException {
        super(name);
        this.setDaemon(true);
        this.callbacks = new HashMap<Path, Runnable>();
        this.directoriesToWatch = new HashSet<Path>();
        FileSystem fileSystem = FileSystems.getDefault();
        this.watchService = fileSystem.newWatchService();
    }

    public synchronized void addCallback(Path path, Runnable callback) {
        this.callbacks.put(path, callback);
        if (this.directoriesToWatch.add(path.getParent())) {
            this.nextDirectoryCheckTime = System.currentTimeMillis();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        try (WatchService watchService = this.watchService;){
            while (!Thread.currentThread().isInterrupted()) {
                this.runIteration();
            }
        }
        catch (InterruptedException ignored) {
            LOGGER.info("FileWatcher was interrupted, stopping.");
        }
        catch (IOException e) {
            LOGGER.error("FileWatcher encountered an unhandled IOException, stopping.", (Throwable)e);
        }
        finally {
            this.watchedDirectories.keySet().forEach(WatchKey::cancel);
        }
    }

    private void runIteration() throws InterruptedException {
        WatchKey watchKey;
        long time = System.currentTimeMillis();
        if (time > this.nextDirectoryCheckTime) {
            this.nextDirectoryCheckTime = time + 60000L;
            this.watchDirectories();
        }
        if ((watchKey = this.watchService.poll(500L, TimeUnit.MILLISECONDS)) != null) {
            this.pollWatchKey(watchKey);
        } else {
            this.notifyChanges();
        }
    }

    private synchronized void pollWatchKey(WatchKey watchKey) throws InterruptedException {
        Path watchedDirectory = this.watchedDirectories.get(watchKey);
        if (watchedDirectory == null) {
            return;
        }
        List<WatchEvent<?>> events = watchKey.pollEvents();
        for (WatchEvent<?> event : events) {
            Path eventPath;
            Path fullPath;
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException();
            }
            if (event.kind() == StandardWatchEventKinds.OVERFLOW) {
                this.callbacks.keySet().stream().filter(path -> path.getParent().equals(watchedDirectory)).forEach(this.changedPaths::add);
                break;
            }
            Object obj = event.context();
            if (!(obj instanceof Path) || !this.callbacks.containsKey(fullPath = watchedDirectory.resolve(eventPath = (Path)obj))) continue;
            this.changedPaths.add(fullPath);
        }
        if (!watchKey.reset()) {
            LOGGER.info("Failed to re-watch directory {}. It may have been deleted.", (Object)watchedDirectory);
            this.watchedDirectories.remove(watchKey);
        }
    }

    private synchronized void notifyChanges() {
        if (this.changedPaths.isEmpty()) {
            return;
        }
        LOGGER.debug("Detected changes in files:\n{}", (Object)this.changedPaths.stream().map(Path::toString).collect(Collectors.joining("\n")));
        List<Runnable> runnables = this.changedPaths.stream().map(this.callbacks::get).filter(Objects::nonNull).toList();
        this.changedPaths.clear();
        CallbackRunner runThread = new CallbackRunner(runnables);
        runThread.start();
    }

    private synchronized void watchDirectories() {
        for (Path directory : this.directoriesToWatch) {
            if (Thread.currentThread().isInterrupted()) {
                return;
            }
            if (this.watchedDirectories.containsValue(directory) || !Files.isDirectory(directory, new LinkOption[0])) continue;
            try {
                WatchKey key = directory.register(this.watchService, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.OVERFLOW);
                this.watchedDirectories.put(key, directory);
            }
            catch (IOException e) {
                LOGGER.error("Failed to watch directory: {}", (Object)directory, (Object)e);
            }
        }
    }

    private static class CallbackRunner
    extends Thread {
        private final List<Runnable> runnables;

        public CallbackRunner(List<Runnable> runnables) {
            super("JEI File Watcher Callback Runner");
            this.runnables = List.copyOf(runnables);
        }

        @Override
        public void run() {
            this.runnables.forEach(Runnable::run);
        }
    }
}

