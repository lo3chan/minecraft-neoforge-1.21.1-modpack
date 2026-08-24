package vectorwing.farmersdelight.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import vectorwing.farmersdelight.common.block.CabinetBlock;
import vectorwing.farmersdelight.common.registry.ModBlockEntityTypes;
import vectorwing.farmersdelight.common.registry.ModSounds;
import vectorwing.farmersdelight.common.utility.TextUtils;

@EventBusSubscriber(
   modid = "farmersdelight"
)
public class CabinetBlockEntity extends RandomizableContainerBlockEntity {
   private NonNullList<ItemStack> contents = NonNullList.withSize(27, ItemStack.EMPTY);
   private ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
      protected void onOpen(Level level, BlockPos pos, BlockState state) {
         CabinetBlockEntity.this.playSound(state, ModSounds.BLOCK_CABINET_OPEN.get());
         CabinetBlockEntity.this.updateBlockState(state, true);
      }

      protected void onClose(Level level, BlockPos pos, BlockState state) {
         CabinetBlockEntity.this.playSound(state, ModSounds.BLOCK_CABINET_CLOSE.get());
         CabinetBlockEntity.this.updateBlockState(state, false);
      }

      protected void openerCountChanged(Level level, BlockPos pos, BlockState sta, int arg1, int arg2) {
      }

      protected boolean isOwnContainer(Player p_155060_) {
         if (p_155060_.containerMenu instanceof ChestMenu) {
            Container container = ((ChestMenu)p_155060_.containerMenu).getContainer();
            return container == CabinetBlockEntity.this;
         } else {
            return false;
         }
      }
   };

   public CabinetBlockEntity(BlockPos pos, BlockState state) {
      super(ModBlockEntityTypes.CABINET.get(), pos, state);
   }

   @SubscribeEvent
   public static void registerCapabilities(RegisterCapabilitiesEvent event) {
      event.registerBlockEntity(ItemHandler.BLOCK, ModBlockEntityTypes.CABINET.get(), (be, context) -> new InvWrapper(be));
   }

   public void saveAdditional(CompoundTag compound, Provider registries) {
      super.saveAdditional(compound, registries);
      if (!this.trySaveLootTable(compound)) {
         ContainerHelper.saveAllItems(compound, this.contents, registries);
      }
   }

   public void loadAdditional(CompoundTag compound, Provider registries) {
      super.loadAdditional(compound, registries);
      this.contents = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      if (!this.tryLoadLootTable(compound)) {
         ContainerHelper.loadAllItems(compound, this.contents, registries);
      }
   }

   public int getContainerSize() {
      return 27;
   }

   protected NonNullList<ItemStack> getItems() {
      return this.contents;
   }

   protected void setItems(NonNullList<ItemStack> itemsIn) {
      this.contents = itemsIn;
   }

   protected Component getDefaultName() {
      return TextUtils.container("cabinet");
   }

   protected AbstractContainerMenu createMenu(int id, Inventory player) {
      return ChestMenu.threeRows(id, player, this);
   }

   public void startOpen(Player pPlayer) {
      if (this.level != null && !this.remove && !pPlayer.isSpectator()) {
         this.openersCounter.incrementOpeners(pPlayer, this.level, this.getBlockPos(), this.getBlockState());
      }
   }

   public void stopOpen(Player pPlayer) {
      if (this.level != null && !this.remove && !pPlayer.isSpectator()) {
         this.openersCounter.decrementOpeners(pPlayer, this.level, this.getBlockPos(), this.getBlockState());
      }
   }

   public void recheckOpen() {
      if (this.level != null && !this.remove) {
         this.openersCounter.recheckOpeners(this.level, this.getBlockPos(), this.getBlockState());
      }
   }

   void updateBlockState(BlockState state, boolean open) {
      if (this.level != null) {
         this.level.setBlock(this.getBlockPos(), (BlockState)state.setValue(CabinetBlock.OPEN, open), 3);
      }
   }

   private void playSound(BlockState state, SoundEvent sound) {
      if (this.level != null) {
         Vec3i cabinetFacingVector = ((Direction)state.getValue(CabinetBlock.FACING)).getNormal();
         double x = this.worldPosition.getX() + 0.5 + cabinetFacingVector.getX() / 2.0;
         double y = this.worldPosition.getY() + 0.5 + cabinetFacingVector.getY() / 2.0;
         double z = this.worldPosition.getZ() + 0.5 + cabinetFacingVector.getZ() / 2.0;
         this.level.playSound(null, x, y, z, sound, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
      }
   }
}
