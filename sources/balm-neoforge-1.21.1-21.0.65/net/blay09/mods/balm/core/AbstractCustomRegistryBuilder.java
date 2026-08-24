package net.blay09.mods.balm.core;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractCustomRegistryBuilder<T> implements CustomRegistryBuilder<T> {
   @Nullable
   private ResourceLocation defaultKey;
   private boolean sync;

   @Override
   public CustomRegistryBuilder<T> defaultKey(ResourceLocation defaultKey) {
      this.defaultKey = defaultKey;
      return this;
   }

   @Override
   public CustomRegistryBuilder<T> sync() {
      this.sync = true;
      return this;
   }

   @Nullable
   public ResourceLocation getDefaultKey() {
      return this.defaultKey;
   }

   public boolean shouldSync() {
      return this.sync;
   }
}
