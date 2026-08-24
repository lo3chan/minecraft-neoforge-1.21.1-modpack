package net.mehvahdjukaar.moonlight.api.platform.configs.options;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public abstract class ConfigNode {
   private final Component title;
   @Nullable
   private Component description;
   @Nullable
   private ConfigCategory parent;
   @Nullable
   private ResourceLocation icon;

   protected ConfigNode(Component title, @Nullable Component description) {
      this.title = title;
      this.description = description;
   }

   @Internal
   public void setParent(ConfigCategory parent) {
      this.parent = parent;
   }

   @Nullable
   public ConfigCategory parent() {
      return this.parent;
   }

   @Internal
   public void setDescription(@Nullable Component description) {
      this.description = description;
   }

   @Internal
   public void setIcon(@Nullable ResourceLocation icon) {
      this.icon = icon;
   }

   @Nullable
   public ResourceLocation icon() {
      return this.icon;
   }

   public Component title() {
      return this.title;
   }

   @Nullable
   public Component description() {
      return this.description;
   }
}
