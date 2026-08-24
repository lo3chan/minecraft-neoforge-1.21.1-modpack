package com.sonicether.soundphysics.config.blocksound;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class BlockIdDefinition extends BlockDefinition {
   private final Block block;

   public BlockIdDefinition(Block block) {
      this.block = block;
   }

   @Override
   public String getConfigString() {
      return BuiltInRegistries.BLOCK.getKey(this.block).toString();
   }

   @Nullable
   @Override
   public String getConfigComment() {
      return this.getName().getString();
   }

   @Override
   public Component getName() {
      return this.block.getName().append(Component.literal(" (Block)"));
   }

   public Block getBlock() {
      return this.block;
   }

   @Nullable
   public static BlockIdDefinition fromConfigString(String configString) {
      if (!configString.contains(":")) {
         return null;
      } else {
         ResourceLocation resourceLocation = ResourceLocation.tryParse(configString);
         return resourceLocation == null ? null : new BlockIdDefinition((Block)BuiltInRegistries.BLOCK.get(resourceLocation));
      }
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         BlockIdDefinition that = (BlockIdDefinition)o;
         return Objects.equals(this.block, that.block);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.block != null ? this.block.hashCode() : 0;
   }
}
