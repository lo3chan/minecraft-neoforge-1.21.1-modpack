package net.astralya.hexalia.block.entity.custom;

import net.astralya.hexalia.block.entity.ModBlockEntityTypes;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class DreamcatcherBlockEntity extends BlockEntity {
   private static final int TICKS_PER_NODE = 30000;
   private int fuelTicks;

   public DreamcatcherBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)ModBlockEntityTypes.DREAMCATCHER.get(), pos, state);
   }

   public static void tick(Level level, BlockPos pos, BlockState state, DreamcatcherBlockEntity dreamcatcher) {
      if (!level.isClientSide() && level.isNight() && dreamcatcher.hasFuel()) {
         dreamcatcher.fuelTicks--;
         if (dreamcatcher.fuelTicks < 0) {
            dreamcatcher.fuelTicks = 0;
         }

         dreamcatcher.setChanged();
      }
   }

   public boolean hasFuel() {
      return this.fuelTicks > 0;
   }

   public InteractionResult tryInsertFuel(Player player, ItemStack held) {
      if (!held.is((Item)ModItems.FIRE_NODE.get())) {
         return InteractionResult.PASS;
      } else if (this.fuelTicks > 0) {
         return InteractionResult.FAIL;
      } else {
         if (!player.isCreative()) {
            held.shrink(1);
         }

         this.fuelTicks = 30000;
         this.setChangedAndSync();
         return InteractionResult.SUCCESS;
      }
   }

   public ItemStack tryExtractFuel() {
      if (this.fuelTicks <= 0) {
         return ItemStack.EMPTY;
      } else {
         this.fuelTicks = 0;
         this.setChangedAndSync();
         return new ItemStack((ItemLike)ModItems.FIRE_NODE.get());
      }
   }

   public void spawnActiveParticles(Level level, BlockPos pos, RandomSource random) {
      double cx = pos.getX() + 0.5;
      double cy = pos.getY() + 0.5;
      double cz = pos.getZ() + 0.5;

      for (int i = 0; i < 2; i++) {
         double x = cx + (random.nextDouble() - 0.5) * 0.4;
         double y = cy + random.nextDouble() * 0.3;
         double z = cz + (random.nextDouble() - 0.5) * 0.4;
         level.addParticle(ParticleTypes.FLAME, x, y, z, 0.0, 0.02, 0.0);
         level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 0.01, 0.0);
      }
   }

   private void setChangedAndSync() {
      this.setChanged();
      if (this.level != null && !this.level.isClientSide()) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
      }
   }

   protected void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      tag.putInt("FuelTicks", this.fuelTicks);
   }

   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.fuelTicks = tag.getInt("FuelTicks");
   }

   public CompoundTag getUpdateTag(Provider registries) {
      CompoundTag tag = super.getUpdateTag(registries);
      tag.putInt("FuelTicks", this.fuelTicks);
      return tag;
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }
}
