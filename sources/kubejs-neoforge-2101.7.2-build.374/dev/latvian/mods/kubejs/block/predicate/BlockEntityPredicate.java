package dev.latvian.mods.kubejs.block.predicate;

import dev.latvian.mods.kubejs.level.LevelBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BlockEntityPredicate implements BlockPredicate {
   private final ResourceLocation id;
   private BlockEntityPredicateDataCheck checkData;

   public BlockEntityPredicate(ResourceLocation i) {
      this.id = i;
   }

   public BlockEntityPredicate data(BlockEntityPredicateDataCheck cd) {
      this.checkData = cd;
      return this;
   }

   @Override
   public boolean check(LevelBlock block) {
      BlockEntity tileEntity = block.getEntity();
      return tileEntity != null
         && this.id.equals(BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(tileEntity.getType()))
         && (this.checkData == null || this.checkData.checkData(block.getEntityData()));
   }

   @Override
   public String toString() {
      return "{entity=" + this.id + "}";
   }
}
