package com.iafenvoy.jupiter.config.entry;

import com.iafenvoy.jupiter.config.interfaces.ConfigEntry;
import com.iafenvoy.jupiter.config.interfaces.TextFieldConfigEntry;
import com.iafenvoy.jupiter.config.type.ConfigType;
import com.iafenvoy.jupiter.config.type.ConfigTypes;
import com.iafenvoy.jupiter.util.Comment;
import com.mojang.serialization.Codec;
import java.util.Objects;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ResourceLocationEntry extends BaseEntry<ResourceLocation> implements TextFieldConfigEntry {
   protected ResourceLocationEntry(ResourceLocationEntry.Builder builder) {
      super(builder);
   }

   @Deprecated(
      forRemoval = true
   )
   @Comment("Use builder instead")
   public ResourceLocationEntry(String nameKey, ResourceLocation defaultValue) {
      super(nameKey, defaultValue);
   }

   @Override
   public ConfigType<ResourceLocation> getType() {
      return ConfigTypes.RESOURCE_LOCATION;
   }

   @Override
   public ConfigEntry<ResourceLocation> newInstance() {
      return new ResourceLocationEntry.Builder(this).build();
   }

   @Override
   public Codec<ResourceLocation> getCodec() {
      return ResourceLocation.CODEC;
   }

   @Override
   public String valueAsString() {
      return this.getValue().toString();
   }

   @Override
   public void setValueFromString(String s) {
      this.setValue(Objects.requireNonNull(ResourceLocation.tryParse(s)));
   }

   public static ResourceLocationEntry.Builder builder(Component name, ResourceLocation defaultValue) {
      return new ResourceLocationEntry.Builder(name, defaultValue);
   }

   public static ResourceLocationEntry.Builder builder(String nameKey, ResourceLocation defaultValue) {
      return new ResourceLocationEntry.Builder(nameKey, defaultValue);
   }

   public static class Builder extends BaseEntry.Builder<ResourceLocation, ResourceLocationEntry, ResourceLocationEntry.Builder> {
      public Builder(Component name, ResourceLocation defaultValue) {
         super(name, defaultValue);
      }

      public Builder(String nameKey, ResourceLocation defaultValue) {
         super(nameKey, defaultValue);
      }

      public Builder(ResourceLocationEntry parent) {
         super(parent);
      }

      public ResourceLocationEntry.Builder self() {
         return this;
      }

      protected ResourceLocationEntry buildInternal() {
         return new ResourceLocationEntry(this);
      }
   }
}
