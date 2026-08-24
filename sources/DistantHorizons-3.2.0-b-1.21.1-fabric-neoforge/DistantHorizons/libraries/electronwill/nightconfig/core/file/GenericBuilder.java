package DistantHorizons.libraries.electronwill.nightconfig.core.file;

import DistantHorizons.libraries.electronwill.nightconfig.core.Config;
import DistantHorizons.libraries.electronwill.nightconfig.core.ConfigFormat;
import DistantHorizons.libraries.electronwill.nightconfig.core.concurrent.StampedConfig;
import DistantHorizons.libraries.electronwill.nightconfig.core.concurrent.SynchronizedConfig;
import DistantHorizons.libraries.electronwill.nightconfig.core.io.ConfigParser;
import DistantHorizons.libraries.electronwill.nightconfig.core.io.ConfigWriter;
import DistantHorizons.libraries.electronwill.nightconfig.core.io.ParsingMode;
import DistantHorizons.libraries.electronwill.nightconfig.core.io.WritingException;
import DistantHorizons.libraries.electronwill.nightconfig.core.io.WritingMode;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public abstract class GenericBuilder<Base extends Config, Result extends FileConfig> {
   protected final Path file;
   protected final ConfigFormat<? extends Base> format;
   protected final ConfigWriter writer;
   protected final ConfigParser<? extends Base> parser;
   protected Charset charset = StandardCharsets.UTF_8;
   protected WritingMode writingMode = WritingMode.REPLACE;
   protected ParsingMode parsingMode = ParsingMode.REPLACE;
   protected FileNotFoundAction nefAction = FileNotFoundAction.CREATE_EMPTY;
   protected boolean sync = false;
   protected boolean autosave = false;
   protected boolean atomicMove = false;
   protected FileWatcher autoreloadFileWatcher = null;
   protected boolean preserveInsertionOrder = Config.isInsertionOrderPreserved();
   protected Supplier<Map<String, Object>> mapCreator = null;
   private ConfigLoadFilter loadFilter;
   protected Runnable loadListener;
   protected Runnable saveListener;
   protected Runnable autoLoadListener;
   protected Runnable autoSaveListener;
   private Duration debounceTime = AsyncFileConfig.DEFAULT_WRITE_DEBOUNCE_TIME;

   GenericBuilder(Path file, ConfigFormat<? extends Base> format) {
      this.file = file;
      this.format = format;
      this.writer = format.createWriter();
      this.parser = format.createParser();
   }

   public GenericBuilder<Base, Result> charset(Charset charset) {
      this.charset = charset;
      return this;
   }

   public GenericBuilder<Base, Result> writingMode(WritingMode writingMode) {
      this.writingMode = writingMode;
      return this;
   }

   public GenericBuilder<Base, Result> parsingMode(ParsingMode parsingMode) {
      this.parsingMode = parsingMode;
      return this;
   }

   public GenericBuilder<Base, Result> onFileNotFound(FileNotFoundAction notFoundAction) {
      this.nefAction = notFoundAction;
      return this;
   }

   public GenericBuilder<Base, Result> defaultResource(String resourcePath) {
      return this.onFileNotFound(FileNotFoundAction.copyResource(resourcePath));
   }

   public GenericBuilder<Base, Result> defaultData(File file) {
      return this.onFileNotFound(FileNotFoundAction.copyData(file));
   }

   public GenericBuilder<Base, Result> defaultData(Path file) {
      return this.onFileNotFound(FileNotFoundAction.copyData(file));
   }

   public GenericBuilder<Base, Result> defaultData(URL url) {
      return this.onFileNotFound(FileNotFoundAction.copyData(url));
   }

   public GenericBuilder<Base, Result> sync() {
      this.sync = true;
      return this;
   }

   public GenericBuilder<Base, Result> async() {
      this.sync = false;
      return this;
   }

   public GenericBuilder<Base, Result> asyncWithDebouncing(Duration debounceTime) {
      this.sync = false;
      this.debounceTime = debounceTime;
      return this;
   }

   public GenericBuilder<Base, Result> autosave() {
      this.autosave = true;
      return this;
   }

   public GenericBuilder<Base, Result> autoreload() {
      return this.autoreload(FileWatcher.defaultInstance());
   }

   public GenericBuilder<Base, Result> autoreload(FileWatcher fileWatcher) {
      this.autoreloadFileWatcher = fileWatcher;
      return this;
   }

   public GenericBuilder<Base, Result> onAutoReload(Runnable listener) {
      this.autoLoadListener = listener;
      return this;
   }

   public GenericBuilder<Base, Result> onAutoSave(Runnable listener) {
      this.autoSaveListener = listener;
      return this;
   }

   public GenericBuilder<Base, Result> onLoad(Runnable listener) {
      this.loadListener = listener;
      return this;
   }

   public GenericBuilder<Base, Result> onSave(Runnable listener) {
      this.saveListener = listener;
      return this;
   }

   public GenericBuilder<Base, Result> onLoadFilter(ConfigLoadFilter filter) {
      this.loadFilter = filter;
      return this;
   }

   @Deprecated
   public GenericBuilder<Base, Result> concurrent() {
      return this;
   }

   public GenericBuilder<Base, Result> preserveInsertionOrder() {
      this.preserveInsertionOrder = true;
      return this;
   }

   public GenericBuilder<Base, Result> backingMapCreator(Supplier<Map<String, Object>> s) {
      this.mapCreator = s;
      return this;
   }

   private Runnable runnableOrNothing(Runnable r) {
      return r == null ? () -> {} : r;
   }

   public Result build() {
      if (this.mapCreator == null) {
         this.mapCreator = this.preserveInsertionOrder ? LinkedHashMap::new : HashMap::new;
      }

      this.saveListener = this.runnableOrNothing(this.saveListener);
      this.loadListener = this.runnableOrNothing(this.loadListener);
      this.autoSaveListener = this.runnableOrNothing(this.autoSaveListener);
      this.autoLoadListener = this.runnableOrNothing(this.autoLoadListener);
      if (this.autoreloadFileWatcher != null && Files.notExists(this.file)) {
         try {
            this.nefAction.run(this.file, this.format);
         } catch (IOException var4) {
            String msg = "An exception occured while executing the FileNotFoundAction for file: " + this.file;
            throw new WritingException(msg, var4);
         }
      }

      CommentedFileConfig fileConfig;
      if (this.sync) {
         SynchronizedConfig config = new SynchronizedConfig(this.format, this.mapCreator);
         fileConfig = new SyncFileConfig(
            config,
            this.file,
            this.charset,
            this.writer,
            this.writingMode,
            this.parser,
            this.parsingMode,
            this.nefAction,
            this.loadFilter,
            this.saveListener,
            this.loadListener
         );
      } else {
         StampedConfig config = new StampedConfig(this.format, this.mapCreator);
         fileConfig = new AsyncFileConfig(
            config,
            this.file,
            this.charset,
            this.writer,
            this.writingMode,
            this.parser,
            this.parsingMode,
            this.nefAction,
            false,
            this.loadFilter,
            this.saveListener,
            this.loadListener,
            this.debounceTime
         );
      }

      if (this.autoreloadFileWatcher != null) {
         fileConfig = new AutoreloadFileConfig<>(fileConfig, this.autoreloadFileWatcher, this.autoLoadListener);
      }

      return this.autosave ? this.buildAutosave(fileConfig) : this.buildNormal(fileConfig);
   }

   protected Result buildAutosave(CommentedFileConfig chain) {
      return (Result)(new AutosaveCommentedFileConfig(chain, this.autoSaveListener));
   }

   protected Result buildNormal(CommentedFileConfig chain) {
      return (Result)chain;
   }
}
