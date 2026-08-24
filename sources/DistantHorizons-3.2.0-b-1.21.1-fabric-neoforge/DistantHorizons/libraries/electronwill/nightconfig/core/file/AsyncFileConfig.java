package DistantHorizons.libraries.electronwill.nightconfig.core.file;

import DistantHorizons.libraries.electronwill.nightconfig.core.CommentedConfig;
import DistantHorizons.libraries.electronwill.nightconfig.core.Config;
import DistantHorizons.libraries.electronwill.nightconfig.core.UnmodifiableCommentedConfig;
import DistantHorizons.libraries.electronwill.nightconfig.core.UnmodifiableConfig;
import DistantHorizons.libraries.electronwill.nightconfig.core.concurrent.ConcurrentCommentedConfig;
import DistantHorizons.libraries.electronwill.nightconfig.core.concurrent.StampedConfig;
import DistantHorizons.libraries.electronwill.nightconfig.core.io.ConfigParser;
import DistantHorizons.libraries.electronwill.nightconfig.core.io.ConfigWriter;
import DistantHorizons.libraries.electronwill.nightconfig.core.io.IoUtils;
import DistantHorizons.libraries.electronwill.nightconfig.core.io.ParsingMode;
import DistantHorizons.libraries.electronwill.nightconfig.core.io.WritingException;
import DistantHorizons.libraries.electronwill.nightconfig.core.io.WritingMode;
import DistantHorizons.libraries.electronwill.nightconfig.core.utils.ConcurrentCommentedConfigWrapper;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

final class AsyncFileConfig extends ConcurrentCommentedConfigWrapper<StampedConfig> implements CommentedFileConfig {
   static final Duration DEFAULT_WRITE_DEBOUNCE_TIME = Duration.ofSeconds(1L);
   private final boolean asyncLoad;
   private volatile boolean closed;
   private final Path nioPath;
   private final DebouncedRunnable saveTask;
   private final ConfigWriter configWriter;
   private final WritingMode writingMode;
   private final ConfigParser<?> configParser;
   private final ParsingMode parsingMode;
   private final FileNotFoundAction notFoundAction;
   private final Charset charset;
   private final ConfigLoadFilter reloadFilter;
   private final Runnable saveListener;
   private final Runnable loadListener;

   AsyncFileConfig(
      StampedConfig config,
      Path nioPath,
      Charset charset,
      ConfigWriter writer,
      WritingMode writingMode,
      ConfigParser<?> parser,
      ParsingMode parsingMode,
      FileNotFoundAction notFoundAction,
      boolean asyncLoad,
      ConfigLoadFilter reloadFilter,
      Runnable saveListener,
      Runnable loadListener,
      Duration debounceTime
   ) {
      super(config);
      this.asyncLoad = asyncLoad;
      this.nioPath = nioPath;
      this.writingMode = writingMode;
      this.configWriter = writer;
      this.saveTask = new DebouncedRunnable(this::saveNow, debounceTime);
      this.configParser = parser;
      this.parsingMode = parsingMode;
      this.notFoundAction = notFoundAction;
      this.charset = charset;
      this.reloadFilter = reloadFilter;
      this.saveListener = saveListener;
      this.loadListener = loadListener;
   }

   private void saveNow() {
      UnmodifiableConfig copy = this.config.newAccumulatorCopy();
      synchronized (this) {
         if (this.writingMode == WritingMode.REPLACE_ATOMIC) {
            Path tmp = this.nioPath.resolveSibling(IoUtils.tempConfigFileName(this.nioPath));

            try (BufferedWriter writer = Files.newBufferedWriter(
                  tmp, this.charset, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING
               )) {
               this.configWriter.write(copy, writer);
            } catch (IOException var27) {
               String msg = String.format("Failed to write (%s) the config to: %s", this.writingMode.toString(), tmp.toString());
               throw new WritingException(msg, var27);
            }

            try {
               IoUtils.retryIfAccessDenied("move", () -> Files.move(tmp, this.nioPath, StandardCopyOption.ATOMIC_MOVE));
            } catch (AtomicMoveNotSupportedException var21) {
               String msg = String.format(
                  "Failed to atomically move the config from '%s' to '%s': WritingMode.REPLACE_ATOMIC is not supported for this path, use WritingMode.REPLACE instead.\n%s",
                  tmp.toString(),
                  this.nioPath.toString(),
                  "Note: you may see *.new.tmp files after this error, they contain the \"new version\" of your configurations and can be safely removed.If you want, you can manually copy their content into your regular configuration files (replacing the old config)."
               );
               throw new WritingException(msg, var21);
            } catch (IOException var22) {
               String msgx = String.format("Failed to atomically write (%s) the config to: %s", this.writingMode.toString(), tmp.toString());
               throw new WritingException(msgx, var22);
            }
         } else {
            OpenOption lastOption = this.writingMode == WritingMode.APPEND ? StandardOpenOption.APPEND : StandardOpenOption.TRUNCATE_EXISTING;

            BufferedWriter fileWriter;
            try {
               fileWriter = Files.newBufferedWriter(this.nioPath, this.charset, StandardOpenOption.WRITE, StandardOpenOption.CREATE, lastOption);
            } catch (IOException var24) {
               throw new WritingException("Failed to open a BufferedWriter on: " + this.nioPath, var24);
            }

            this.configWriter.write(copy, fileWriter);

            try {
               if (this.closed) {
                  fileWriter.close();
               } else {
                  fileWriter.flush();
               }
            } catch (IOException var25) {
               String op = this.closed ? "close" : "flush";
               String msg = String.format("Buffer %s failed while saving asynchronous FileConfig.", op);
               throw new WritingException(msg, var25);
            }
         }
      }

      this.saveListener.run();
   }

   private void loadNow() {
      Config newConfig = this.configParser.parse(this.nioPath, this.notFoundAction, this.charset);
      CommentedConfig newCC = CommentedConfig.fake(newConfig);
      if (this.reloadFilter == null || this.reloadFilter.acceptNewVersion(newCC)) {
         switch (this.parsingMode) {
            case REPLACE:
               StampedConfig newSafeContent = this.config.createSubConfig();
               newSafeContent.putAll(newCC);
               newSafeContent.putAllComments(newCC);
               this.config.replaceContentBy(newSafeContent);
               break;
            default:
               putWithParsingMode(this.parsingMode, newCC, this.config);
         }

         this.loadListener.run();
      }
   }

   static void putWithParsingMode(ParsingMode parsingMode, CommentedConfig newCC, ConcurrentCommentedConfig config) {
      config.bulkCommentedUpdate(view -> {
         for (CommentedConfig.Entry entry : newCC.entrySet()) {
            List<String> key = Collections.singletonList(entry.getKey());
            Object value = entry.getRawValue();
            if (value instanceof UnmodifiableConfig && value.getClass() != config.getClass()) {
               ConcurrentCommentedConfig newSafeContent = config.createSubConfig();
               newSafeContent.putAll((UnmodifiableConfig)value);
               if (value instanceof UnmodifiableCommentedConfig) {
                  newSafeContent.putAllComments((UnmodifiableCommentedConfig)value);
               }

               value = newSafeContent;
            }

            parsingMode.put(view, key, value);
         }
      });
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
         this.saveTask.run(AsyncFileConfig.LazyExecutorHolder.sharedExecutor);
      }
   }

   public void asyncLoad() {
      AsyncFileConfig.LazyExecutorHolder.sharedExecutor.execute(() -> this.loadNow());
   }

   @Override
   public void load() {
      if (this.closed) {
         throw new IllegalStateException("This FileConfig is closed, cannot load().");
      } else {
         if (this.asyncLoad) {
            this.asyncLoad();
         } else {
            this.loadNow();
         }
      }
   }

   @Override
   public void close() {
      this.closed = true;
   }

   private static final class LazyExecutorHolder {
      private static final ScheduledExecutorService sharedExecutor;

      static {
         int poolSize = Runtime.getRuntime().availableProcessors();
         ThreadFactory defaultFactory = Executors.defaultThreadFactory();
         ThreadFactory factory = r -> {
            Thread t = defaultFactory.newThread(r);
            t.setDaemon(true);
            return t;
         };
         sharedExecutor = Executors.newScheduledThreadPool(poolSize, factory);
      }
   }
}
