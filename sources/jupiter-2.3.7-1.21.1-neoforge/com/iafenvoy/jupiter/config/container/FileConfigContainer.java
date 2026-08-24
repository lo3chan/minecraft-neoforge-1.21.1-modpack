package com.iafenvoy.jupiter.config.container;

import com.iafenvoy.jupiter.Jupiter;
import com.iafenvoy.jupiter.config.ConfigSource;
import com.iafenvoy.jupiter.util.TextUtil;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.io.FileUtils;

public abstract class FileConfigContainer extends AbstractConfigContainer {
   protected final String path;

   public FileConfigContainer(ResourceLocation id, String titleKey, String path) {
      this(id, TextUtil.translatable(titleKey), path);
   }

   public FileConfigContainer(ResourceLocation id, Component title, String path) {
      super(id, title);
      this.path = path;
   }

   @Override
   public String getPath() {
      return Path.of(this.path).getFileName().toString();
   }

   @Override
   public void load() {
      try {
         this.deserialize(FileUtils.readFileToString(new File(this.path), StandardCharsets.UTF_8));
      } catch (Exception var2) {
         Jupiter.LOGGER.error("Failed to load config: {}", this.path, var2);
         this.save();
      }
   }

   @Override
   public void save() {
      try {
         FileUtils.write(new File(this.path), this.serialize(), StandardCharsets.UTF_8);
      } catch (Exception var2) {
         Jupiter.LOGGER.error("Failed to save config: {}", this.path, var2);
      }
   }

   @Override
   public ConfigSource getSource() {
      return ConfigSource.JUPITER;
   }
}
