package net.blay09.mods.balm.core;

import net.minecraft.resources.ResourceLocation;

public interface CustomRegistryBuilder<T> {
   CustomRegistryBuilder<T> defaultKey(ResourceLocation var1);

   CustomRegistryBuilder<T> sync();
}
