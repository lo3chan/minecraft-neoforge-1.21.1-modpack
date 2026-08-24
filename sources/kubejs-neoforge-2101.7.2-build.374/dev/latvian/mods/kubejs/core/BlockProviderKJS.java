package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

@RemapPrefixForJS("kjs$")
public interface BlockProviderKJS extends RegistryObjectKJS<Block> {
   Block kjs$getBlock();

   @Override
   default ResourceKey<Registry<Block>> kjs$getRegistryId() {
      return Registries.BLOCK;
   }

   @Override
   default Registry<Block> kjs$getRegistry() {
      return BuiltInRegistries.BLOCK;
   }

   @Override
   default Holder<Block> kjs$asHolder() {
      return this.kjs$getBlock().builtInRegistryHolder();
   }

   @Override
   default ResourceKey<Block> kjs$getKey() {
      return this.kjs$getBlock().builtInRegistryHolder().key();
   }

   @Override
   default String kjs$getId() {
      return this.kjs$getBlock().kjs$getId();
   }

   default Map<String, Object> kjs$getTypeData() {
      return this.kjs$getBlock().kjs$getTypeData();
   }
}
