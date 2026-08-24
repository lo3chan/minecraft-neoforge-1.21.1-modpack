package vectorwing.farmersdelight.common.block.entity;

import java.util.function.BooleanSupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import vectorwing.farmersdelight.common.block.BasketBlock;
import vectorwing.farmersdelight.common.block.entity.inventory.BasketInvWrapper;
import vectorwing.farmersdelight.common.registry.ModBlockEntityTypes;
import vectorwing.farmersdelight.common.utility.TextUtils;

@EventBusSubscriber(
   modid = "farmersdelight"
)
public class BasketBlockEntity extends RandomizableContainerBlockEntity implements Basket {
   private NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);
   private int transferCooldown = -1;

   public BasketBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)ModBlockEntityTypes.BASKET.get(), pos, state);
   }

   @SubscribeEvent
   public static void registerCapabilities(RegisterCapabilitiesEvent event) {
      event.registerBlockEntity(ItemHandler.BLOCK, (BlockEntityType)ModBlockEntityTypes.BASKET.get(), (be, context) -> new BasketInvWrapper(be));
   }

   protected void loadAdditional(CompoundTag compound, Provider registries) {
      super.loadAdditional(compound, registries);
      this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      if (!this.tryLoadLootTable(compound)) {
         ContainerHelper.loadAllItems(compound, this.items, registries);
      }

      this.transferCooldown = compound.getInt("TransferCooldown");
   }

   public void saveAdditional(CompoundTag compound, Provider registries) {
      super.saveAdditional(compound, registries);
      if (!this.trySaveLootTable(compound)) {
         ContainerHelper.saveAllItems(compound, this.items, registries);
      }

      compound.putInt("TransferCooldown", this.transferCooldown);
   }

   public int getContainerSize() {
      return this.items.size();
   }

   public ItemStack removeItem(int index, int count) {
      this.unpackLootTable(null);
      return ContainerHelper.removeItem(this.getItems(), index, count);
   }

   public void setItem(int index, ItemStack stack) {
      this.unpackLootTable(null);
      this.getItems().set(index, stack);
      if (stack.getCount() > this.getMaxStackSize()) {
         stack.setCount(this.getMaxStackSize());
      }
   }

   protected Component getDefaultName() {
      return TextUtils.container("basket");
   }

   protected NonNullList<ItemStack> getItems() {
      return this.items;
   }

   protected void setItems(NonNullList<ItemStack> itemsIn) {
      this.items = itemsIn;
   }

   protected AbstractContainerMenu createMenu(int id, Inventory player) {
      return ChestMenu.threeRows(id, player, this);
   }

   @Override
   public void setCooldown(int ticks) {
      this.transferCooldown = ticks;
   }

   @Override
   public boolean isOnCooldown() {
      return this.transferCooldown > 0;
   }

   @Override
   public boolean isOnCustomCooldown() {
      return this.transferCooldown > 8;
   }

   @Override
   public void tryTransfer(BooleanSupplier transfer) {
      if (this.level != null && !this.level.isClientSide && !this.isOnCooldown() && (Boolean)this.getBlockState().getValue(BlockStateProperties.ENABLED)) {
         boolean flag = false;
         if (!this.isFull()) {
            flag = transfer.getAsBoolean();
         }

         if (flag) {
            this.setCooldown(8);
            this.setChanged();
         }
      }
   }

   protected boolean isFull() {
      for (ItemStack itemstack : this.items) {
         if (itemstack.isEmpty() || itemstack.getCount() != itemstack.getMaxStackSize()) {
            return false;
         }
      }

      return true;
   }

   @Override
   public double getLevelX() {
      return this.worldPosition.getX() + 0.5;
   }

   @Override
   public double getLevelY() {
      return this.worldPosition.getY() + 0.5;
   }

   @Override
   public double getLevelZ() {
      return this.worldPosition.getZ() + 0.5;
   }

   public static void pushItemsTick(Level level, BlockPos pos, BlockState state, BasketBlockEntity blockEntity) {
      blockEntity.transferCooldown--;
      if (!blockEntity.isOnCooldown()) {
         blockEntity.setCooldown(0);
         int facing = ((Direction)state.getValue(BasketBlock.FACING)).get3DDataValue();
         blockEntity.tryTransfer(() -> blockEntity.collectItems(level, facing));
      }
   }
}
