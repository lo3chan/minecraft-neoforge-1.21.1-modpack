package mezz.jei.common.config.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import mezz.jei.common.config.ConfigManager;
import mezz.jei.common.util.DeduplicatingRunner;
import net.minecraft.locale.Language;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Unmodifiable;

public class ConfigSchema implements IConfigSchema {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Duration SAVE_DELAY_TIME = Duration.ofSeconds(2L);
   private final Path path;
   @Unmodifiable
   private final List<ConfigCategory> categories;
   private final AtomicBoolean needsLoad = new AtomicBoolean(true);
   private final DeduplicatingRunner delayedSave = new DeduplicatingRunner(SAVE_DELAY_TIME);

   public ConfigSchema(Path path, List<ConfigCategoryBuilder> categoryBuilders) {
      this.path = path;
      this.categories = categoryBuilders.stream().map(b -> b.build(this)).toList();
   }

   @Override
   public void loadIfNeeded() {
      if (this.needsLoad.compareAndSet(true, false)) {
         if (Files.exists(this.path)) {
            try {
               ConfigSerializer.load(this.path, this.categories);
            } catch (IOException var2) {
               LOGGER.error("Failed to load config schema for: {}", this.path, var2);
            }
         }
      }
   }

   private void onFileChanged() {
      this.needsLoad.set(true);
   }

   @Override
   public void register(FileWatcher fileWatcher, ConfigManager configManager) {
      if (Files.exists(this.path)) {
         this.loadIfNeeded();
      }

      this.save();
      fileWatcher.addCallback(this.path, this::onFileChanged);
      configManager.registerConfigFile(this);
   }

   private void save() {
      if (!Language.getInstance().has("jei.config")) {
         LOGGER.debug("Localization has not loaded yet, waiting to save the config file until JEI starts.");
      } else {
         try {
            ConfigSerializer.save(this.path, this.categories);
         } catch (IOException var2) {
            LOGGER.error("Failed to save config file: '{}'", this.path, var2);
         }
      }
   }

   @Override
   public void markDirty() {
      this.delayedSave.run(this::save);
   }

   @Override
   public void clearListeners() {
      for (ConfigCategory configCategory : this.categories) {
         configCategory.clearListeners();
      }
   }

   @Unmodifiable
   @Override
   public List<ConfigCategory> getCategories() {
      return this.categories;
   }

   @Override
   public Path getPath() {
      return this.path;
   }
}
