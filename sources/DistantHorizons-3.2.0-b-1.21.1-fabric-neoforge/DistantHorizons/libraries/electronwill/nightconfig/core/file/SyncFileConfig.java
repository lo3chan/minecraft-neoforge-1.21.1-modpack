package DistantHorizons.libraries.electronwill.nightconfig.core.file;

import DistantHorizons.libraries.electronwill.nightconfig.core.CommentedConfig;
import DistantHorizons.libraries.electronwill.nightconfig.core.Config;
import DistantHorizons.libraries.electronwill.nightconfig.core.concurrent.SynchronizedConfig;
import DistantHorizons.libraries.electronwill.nightconfig.core.io.ConfigParser;
import DistantHorizons.libraries.electronwill.nightconfig.core.io.ConfigWriter;
import DistantHorizons.libraries.electronwill.nightconfig.core.io.ParsingMode;
import DistantHorizons.libraries.electronwill.nightconfig.core.io.WritingMode;
import DistantHorizons.libraries.electronwill.nightconfig.core.utils.ConcurrentCommentedConfigWrapper;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Path;

final class SyncFileConfig extends ConcurrentCommentedConfigWrapper<SynchronizedConfig> implements CommentedFileConfig {
   private final Path nioPath;
   private final Charset charset;
   private volatile boolean closed;
   private final ConfigWriter writer;
   private final WritingMode writingMode;
   private final ConfigParser<?> parser;
   private final FileNotFoundAction nefAction;
   private final ParsingMode parsingMode;
   private final ConfigLoadFilter reloadFilter;
   private final Runnable saveListener;
   private final Runnable loadListener;

   SyncFileConfig(
      SynchronizedConfig config,
      Path nioPath,
      Charset charset,
      ConfigWriter writer,
      WritingMode writingMode,
      ConfigParser<?> parser,
      ParsingMode parsingMode,
      FileNotFoundAction nefAction,
      ConfigLoadFilter reloadFilter,
      Runnable saveListener,
      Runnable loadListener
   ) {
      super(config);
      this.nioPath = nioPath;
      this.charset = charset;
      this.writer = writer;
      this.parser = parser;
      this.parsingMode = parsingMode;
      this.nefAction = nefAction;
      this.writingMode = writingMode;
      this.reloadFilter = reloadFilter;
      this.saveListener = saveListener;
      this.loadListener = loadListener;
   }

   @Override
   public File getFile() {
      return this.nioPath.toFile();
   }

   @Override
   public Path getNioPath() {
      return this.nioPath;
   }

   @Override
   public void save() {
      if (this.closed) {
         throw new IllegalStateException("This FileConfig is closed, cannot save().");
      } else {
         this.config.bulkCommentedRead(config -> this.writer.write(config, this.nioPath, this.writingMode, this.charset));
         this.saveListener.run();
      }
   }

   @Override
   public void load() {
      if (this.closed) {
         throw new IllegalStateException("This FileConfig is closed, cannot load().");
      } else {
         if (this.reloadFilter == null) {
            this.config.bulkCommentedUpdate(view -> this.parser.parse(this.nioPath, view, this.parsingMode, this.nefAction, this.charset));
         } else {
            Config newConfig = this.parser.parse(this.nioPath, this.nefAction, this.charset);
            CommentedConfig newCC = CommentedConfig.fake(newConfig);
            if (!this.reloadFilter.acceptNewVersion(newCC)) {
               return;
            }

            switch (this.parsingMode) {
               case REPLACE:
                  this.config.replaceContentBy(newCC);
                  break;
               default:
                  AsyncFileConfig.putWithParsingMode(this.parsingMode, newCC, this.config);
            }
         }

         this.loadListener.run();
      }
   }

   @Override
   public void close() {
      this.closed = true;
   }
}
