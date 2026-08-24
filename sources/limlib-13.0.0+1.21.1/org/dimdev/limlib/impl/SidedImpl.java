package org.dimdev.limlib.impl;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import java.nio.file.Path;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.dimdev.limlib.api.ISided;
import org.dimdev.limlib.api.ModCommon;

public abstract class SidedImpl<V extends SidedImpl<V, T>, T extends ModCommon<? super V>> implements ISided<V> {
   protected final T common;
   protected final Multimap<CreativeModeTab, ItemStack> APPENDS = MultimapBuilder.hashKeys().arrayListValues().build();

   public SidedImpl(T common) {
      this.common = common;
   }

   @Override
   public void appendStack(CreativeModeTab tab, ItemStack item) {
      this.APPENDS.put(tab, item);
   }

   @Override
   public <T, V extends T> V register(ResourceKey<Registry<T>> key, String id, V obj) {
      return this.register(key, ResourceLocation.fromNamespaceAndPath(this.common.getModId(), id), obj);
   }

   @Override
   public <T, V extends T> Holder<T> registerHolder(ResourceKey<Registry<T>> key, String id, V obj) {
      return this.registerHolder(key, ResourceLocation.fromNamespaceAndPath(this.common.getModId(), id), obj);
   }

   @Override
   public Path configPath() {
      return this.getConfigRoot().resolve(this.common.getModId() + "-config.json");
   }
}
