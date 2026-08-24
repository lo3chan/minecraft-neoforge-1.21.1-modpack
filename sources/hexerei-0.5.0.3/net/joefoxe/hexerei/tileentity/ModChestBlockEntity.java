package net.joefoxe.hexerei.tileentity;

import net.joefoxe.hexerei.block.custom.ModChest;
import net.joefoxe.hexerei.sounds.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;

public class ModChestBlockEntity extends ChestBlockEntity {
   private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
      protected void onOpen(Level pLevel, BlockPos pPos, BlockState pState) {
         boolean flag = false;
         if (pLevel.getBlockEntity(pPos) instanceof ModChestBlockEntity blockEntity
            && pState.hasProperty(ModChest.TYPE)
            && pState.getValue(ModChest.TYPE) == ChestType.SINGLE
            && ModChestBlockEntity.this.hasCustomName()
            && ModChestBlockEntity.this.getCustomName().getString().equals("Hooty")) {
            flag = true;
         }

         ModChestBlockEntity.playSound(pLevel, pPos, pState, flag ? (SoundEvent)ModSounds.HOOTSIFER.get() : SoundEvents.CHEST_OPEN);
      }

      protected void onClose(Level p_155367_, BlockPos p_155368_, BlockState p_155369_) {
         ModChestBlockEntity.playSound(p_155367_, p_155368_, p_155369_, SoundEvents.CHEST_CLOSE);
      }

      protected void openerCountChanged(Level p_155361_, BlockPos p_155362_, BlockState p_155363_, int p_155364_, int p_155365_) {
         ModChestBlockEntity.this.signalOpenCount(p_155361_, p_155362_, p_155363_, p_155364_, p_155365_);
      }

      protected boolean isOwnContainer(Player p_155355_) {
         if (!(p_155355_.containerMenu instanceof ChestMenu menu)) {
            return false;
         } else {
            Container container = menu.getContainer();
            return container == ModChestBlockEntity.this || container instanceof CompoundContainer cc && cc.contains(ModChestBlockEntity.this);
         }
      }
   };

   public ModChestBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
      super(pType, pPos, pBlockState);
      super.openersCounter = this.openersCounter;
   }

   public ModChestBlockEntity(BlockPos pPos, BlockState pBlockState) {
      this((BlockEntityType<?>)ModTileEntities.CHEST_TILE.get(), pPos, pBlockState);
   }

   static void playSound(Level pLevel, BlockPos pPos, BlockState pState, SoundEvent pSound) {
      ChestType chesttype = (ChestType)pState.getValue(ModChest.TYPE);
      if (chesttype != ChestType.LEFT) {
         double d0 = pPos.getX() + 0.5;
         double d1 = pPos.getY() + 0.5;
         double d2 = pPos.getZ() + 0.5;
         if (chesttype == ChestType.RIGHT) {
            Direction direction = ModChest.getConnectedDirection(pState);
            d0 += direction.getStepX() * 0.5;
            d2 += direction.getStepZ() * 0.5;
         }

         pLevel.playSound(null, d0, d1, d2, pSound, SoundSource.BLOCKS, 0.5F, pLevel.random.nextFloat() * 0.1F + 0.9F);
      }
   }

   public void startOpen(Player pPlayer) {
      if (!this.remove && !pPlayer.isSpectator()) {
         this.openersCounter.incrementOpeners(pPlayer, this.getLevel(), this.getBlockPos(), this.getBlockState());
      }
   }

   public void stopOpen(Player pPlayer) {
      if (!this.remove && !pPlayer.isSpectator()) {
         this.openersCounter.decrementOpeners(pPlayer, this.getLevel(), this.getBlockPos(), this.getBlockState());
      }
   }

   public CompoundTag getUpdateTag(Provider registries) {
      CompoundTag returnValue = new CompoundTag();
      this.saveAdditional(returnValue, registries);
      return returnValue;
   }
}
