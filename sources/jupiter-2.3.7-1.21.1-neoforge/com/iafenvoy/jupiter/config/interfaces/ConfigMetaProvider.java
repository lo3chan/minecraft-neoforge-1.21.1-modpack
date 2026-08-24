package com.iafenvoy.jupiter.config.interfaces;

import com.iafenvoy.jupiter.ServerConfigManager;
import com.iafenvoy.jupiter.config.ConfigSide;
import com.iafenvoy.jupiter.config.ConfigSource;
import com.iafenvoy.jupiter.config.container.AbstractConfigContainer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface ConfigMetaProvider {
   ResourceLocation getConfigId();

   String getPath();

   default ConfigSource getSource() {
      return ConfigSource.NONE;
   }

   default ConfigSide getSide() {
      return this instanceof AbstractConfigContainer container && ServerConfigManager.getServerConfigs().contains(container)
         ? ConfigSide.COMMON
         : ConfigSide.CLIENT;
   }

   public record SimpleProvider(ResourceLocation id, String path, boolean client) implements ConfigMetaProvider {
      @Override
      public ResourceLocation getConfigId() {
         return this.id;
      }

      @Override
      public String getPath() {
         return this.path;
      }

      @Override
      public ConfigSide getSide() {
         return this.client ? ConfigSide.CLIENT : ConfigSide.COMMON;
      }
   }
}
