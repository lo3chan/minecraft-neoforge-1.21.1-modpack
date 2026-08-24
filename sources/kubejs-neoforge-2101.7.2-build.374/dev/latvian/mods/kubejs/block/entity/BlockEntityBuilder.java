package dev.latvian.mods.kubejs.block.entity;

import dev.latvian.mods.kubejs.registry.BuilderBase;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;

public class BlockEntityBuilder extends BuilderBase<BlockEntityType<?>> {
   public BlockEntityInfo info;

   public BlockEntityBuilder(ResourceLocation i, BlockEntityInfo info) {
      super(i);
      this.info = info;
   }

   public BlockEntityType<?> createObject() {
      this.info.entityType = Builder.of(this.info::createBlockEntity, new Block[]{this.info.blockBuilder.get()}).build(null);
      return this.info.entityType;
   }
}
