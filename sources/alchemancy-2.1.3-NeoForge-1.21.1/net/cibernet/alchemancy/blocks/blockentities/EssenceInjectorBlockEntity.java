package net.cibernet.alchemancy.blocks.blockentities;

import net.cibernet.alchemancy.blocks.EssenceInjectorBlock;
import net.cibernet.alchemancy.registries.AlchemancyBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class EssenceInjectorBlockEntity extends BlockEntity implements IEssenceHolder {
   public final EssenceContainer essence = new EssenceContainer(1000);

   public EssenceInjectorBlockEntity(BlockPos pos, BlockState blockState) {
      super((BlockEntityType)AlchemancyBlockEntities.ESSENCE_INJECTOR.get(), pos, blockState);
   }

   public static void serverTick(Level level, BlockPos pos, BlockState state, EssenceInjectorBlockEntity blockEntity) {
      for (Direction direction : Direction.values()) {
         if (!((Direction)state.getValue(EssenceInjectorBlock.FACING)).equals(direction)
            && level.getBlockEntity(pos.relative(direction)) instanceof IEssenceHolder holder
            && holder.canTransferFromDirection(direction)) {
            holder.getEssenceContainer().transferTo(blockEntity.getEssenceContainer(), 100, false);
         }
      }
   }

   @Override
   public EssenceContainer getEssenceContainer() {
      return this.essence;
   }

   protected void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      tag.put("essence", this.essence.saveToTag(new CompoundTag()));
   }

   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.essence.loadFromTag(tag.getCompound("essence"));
   }
}
