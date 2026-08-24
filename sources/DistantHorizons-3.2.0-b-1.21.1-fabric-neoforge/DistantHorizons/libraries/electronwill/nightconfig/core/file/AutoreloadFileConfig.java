package DistantHorizons.libraries.electronwill.nightconfig.core.file;

import DistantHorizons.libraries.electronwill.nightconfig.core.utils.ConcurrentCommentedConfigWrapper;
import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

final class AutoreloadFileConfig<C extends CommentedFileConfig> extends ConcurrentCommentedConfigWrapper<C> implements CommentedFileConfig {
   private final FileWatcher watcher;
   private final Runnable autoListener;

   AutoreloadFileConfig(C config, FileWatcher watcher, Runnable autoreloadListener) {
      super(config);
      this.watcher = watcher;
      this.autoListener = autoreloadListener;
      watcher.addWatch(config.getNioPath(), this::autoReload);
   }

   private void autoReload() {
      this.load();
      this.autoListener.run();
   }

   @Override
   public File getFile() {
      return this.config.getFile();
   }

   @Override
   public Path getNioPath() {
      return this.config.getNioPath();
   }

   @Override
   public void save() {
      this.config.save();
   }

   @Override
   public void load() {
      this.config.load();
   }

   @Override
   public void close() {
      try {
         this.watcher.removeWatchFuture(this.config.getNioPath()).get(5L, TimeUnit.SECONDS);
      } catch (Exception var5) {
         throw new RuntimeException(var5);
      } finally {
         this.config.close();
      }
   }
}
