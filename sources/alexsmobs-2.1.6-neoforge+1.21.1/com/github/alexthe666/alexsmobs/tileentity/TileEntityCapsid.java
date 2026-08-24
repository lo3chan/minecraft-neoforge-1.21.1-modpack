package com.github.alexthe666.alexsmobs.tileentity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.block.BlockCapsid;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityEnderiophage;
import com.github.alexthe666.alexsmobs.message.MessageUpdateCapsid;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMItemHandlers;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.CapsidRecipe;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EndRodBlock;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class TileEntityCapsid extends BaseContainerBlockEntity implements WorldlyContainer {
   private static final int[] slotsTop = new int[]{0};
   public int ticksExisted;
   public float prevFloatUpProgress;
   public float floatUpProgress;
   public float prevYawSwitchProgress;
   public float yawSwitchProgress;
   public boolean vibratingThisTick = false;
   private float yawTarget = 0.0F;
   private int transformTime = 0;
   private boolean fnaf = false;
   private CapsidRecipe lastRecipe = null;
   private NonNullList<ItemStack> stacks = NonNullList.withSize(1, ItemStack.EMPTY);

   public TileEntityCapsid(BlockPos pos, BlockState state) {
      super(AMTileEntityRegistry.CAPSID.get(), pos, state);
   }

   public static void commonTick(Level level, BlockPos pos, BlockState state, TileEntityCapsid entity) {
      entity.tick();
   }

   public void tick() {
      this.prevFloatUpProgress = this.floatUpProgress;
      this.prevYawSwitchProgress = this.yawSwitchProgress;
      this.ticksExisted++;
      this.vibratingThisTick = false;
      if (!this.getItem(0).isEmpty()) {
         BlockEntity up = this.level.getBlockEntity(this.worldPosition.above());
         if (up instanceof Container) {
            if (this.floatUpProgress >= 1.0F) {
               IItemHandler handler = AMItemHandlers.find(this.level.getBlockEntity(this.worldPosition.above()), Direction.UP);
               if (handler != null && ItemHandlerHelper.insertItem(handler, this.getItem(0), true).isEmpty()) {
                  ItemHandlerHelper.insertItem(handler, this.getItem(0).copy(), false);
                  this.setItem(0, ItemStack.EMPTY);
               }

               this.yawTarget = 0.0F;
               this.floatUpProgress = 0.0F;
               this.yawSwitchProgress = 0.0F;
            } else {
               if (up instanceof TileEntityCapsid) {
                  this.yawTarget = Mth.wrapDegrees(((TileEntityCapsid)up).getBlockAngle() - this.getBlockAngle());
               } else {
                  this.yawTarget = 0.0F;
               }

               if (this.yawTarget < this.yawSwitchProgress) {
                  this.yawSwitchProgress = this.yawSwitchProgress + this.yawTarget * 0.1F;
               } else if (this.yawTarget > this.yawSwitchProgress) {
                  this.yawSwitchProgress = this.yawSwitchProgress + this.yawTarget * 0.1F;
               }

               this.floatUpProgress += 0.05F;
            }
         } else {
            this.floatUpProgress = 0.0F;
         }

         if (this.getItem(0).getItem() == Items.ENDER_EYE
            && this.level.getBlockState(this.getBlockPos().below()).getBlock() == Blocks.END_ROD
            && ((Direction)this.level.getBlockState(this.getBlockPos().below()).getValue(EndRodBlock.FACING)).getAxis() == Axis.Y) {
            this.vibratingThisTick = true;
            if (this.transformTime > 20) {
               this.setItem(0, ItemStack.EMPTY);
               this.level.destroyBlock(this.getBlockPos(), false);
               this.level.destroyBlock(this.getBlockPos().below(), false);
               EntityEnderiophage phage = AMCompat.create(AMEntityRegistry.ENDERIOPHAGE.get(), this.level);
               phage.setPos(this.getBlockPos().getX() + 0.5F, this.getBlockPos().getY() - 1.0F, this.getBlockPos().getZ() + 0.5F);
               phage.setVariant(0);
               if (!this.level.isClientSide()) {
                  this.level.addFreshEntity(phage);
               }
            }
         } else if (!this.getItem(0).isEmpty()
            && this.level.getBlockState(this.getBlockPos().above()).getBlock() != this.getBlockState().getBlock()
            && this.lastRecipe != null
            && this.lastRecipe.matches(this.getItem(0))) {
            this.floatUpProgress = 0.0F;
            this.vibratingThisTick = true;
            if (this.transformTime == 1 && (AlexsMobs.isAprilFools() || new Random().nextInt(100) == 0)) {
               this.fnaf = true;
               this.level.playSound(null, this.getBlockPos(), AMSoundRegistry.MOSQUITO_CAPSID_CONVERT.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            }

            if (this.transformTime > (this.fnaf ? Math.max(160, this.lastRecipe.getTime()) : this.lastRecipe.getTime())) {
               ItemStack current = this.getItem(0).copy();
               current.shrink(1);
               this.fnaf = false;
               if (!current.isEmpty()) {
                  ItemEntity itemEntity = new ItemEntity(
                     this.level, this.getBlockPos().getX() + 0.5F, this.getBlockPos().getY() + 0.5F, this.getBlockPos().getZ() + 0.5F, current
                  );
                  if (!this.level.isClientSide()) {
                     this.level.addFreshEntity(itemEntity);
                  }
               }

               this.setItem(0, this.lastRecipe.getResult().copy());
            }
         }
      }

      if (!this.vibratingThisTick) {
         this.transformTime = 0;
      } else {
         this.transformTime++;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public AABB getRenderBoundingBox() {
      return AMPlatform.encapsulating(this.worldPosition, this.worldPosition.offset(1, 2, 1));
   }

   public int getContainerSize() {
      return this.stacks.size();
   }

   public ItemStack getItem(int index) {
      return (ItemStack)this.stacks.get(index);
   }

   public ItemStack removeItem(int index, int count) {
      if (!((ItemStack)this.stacks.get(index)).isEmpty()) {
         if (((ItemStack)this.stacks.get(index)).getCount() <= count) {
            ItemStack itemstack = (ItemStack)this.stacks.get(index);
            this.stacks.set(index, ItemStack.EMPTY);
            return itemstack;
         } else {
            ItemStack itemstack = ((ItemStack)this.stacks.get(index)).split(count);
            if (((ItemStack)this.stacks.get(index)).isEmpty()) {
               this.stacks.set(index, ItemStack.EMPTY);
            }

            return itemstack;
         }
      } else {
         return ItemStack.EMPTY;
      }
   }

   public ItemStack getStackInSlotOnClosing(int index) {
      if (!((ItemStack)this.stacks.get(index)).isEmpty()) {
         ItemStack itemstack = (ItemStack)this.stacks.get(index);
         this.stacks.set(index, itemstack);
         return itemstack;
      } else {
         return ItemStack.EMPTY;
      }
   }

   public void setItem(int index, ItemStack stack) {
      if (!stack.isEmpty() && ItemStack.isSameItemSameComponents(stack, (ItemStack)this.stacks.get(index))) {
         boolean var4 = true;
      } else {
         boolean var10000 = false;
      }

      this.stacks.set(index, stack);
      if (!stack.isEmpty() && stack.getCount() > this.getMaxStackSize()) {
         stack.setCount(this.getMaxStackSize());
      }

      this.lastRecipe = AlexsMobs.PROXY.getCapsidRecipeManager().getRecipeFor(stack);
      if (this.level != null) {
         this.saveAdditional(new CompoundTag(), this.level.registryAccess());
      }

      if (!this.level.isClientSide()) {
         AlexsMobs.sendMSGToAll(new MessageUpdateCapsid(this.getBlockPos().asLong(), (ItemStack)this.stacks.get(0)));
      }
   }

   protected void loadAdditional(CompoundTag compound, Provider provider) {
      super.loadAdditional(compound, provider);
      this.stacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      AMCompat.loadAllItems(provider, compound, this.stacks);
   }

   protected void saveAdditional(CompoundTag compound, Provider provider) {
      super.saveAdditional(compound, provider);
      AMCompat.saveAllItems(provider, compound, this.stacks);
   }

   public void startOpen(Player player) {
   }

   public void stopOpen(Player player) {
   }

   public boolean canPlaceItemThroughFace(int index, ItemStack stack, Direction direction) {
      return true;
   }

   public int getMaxStackSize() {
      return 64;
   }

   public boolean stillValid(Player player) {
      return true;
   }

   public void clearContent() {
      this.stacks.clear();
   }

   public int[] getSlotsForFace(Direction side) {
      return slotsTop;
   }

   public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
      return false;
   }

   public boolean hasCustomName() {
      return false;
   }

   public boolean canPlaceItem(int index, ItemStack stack) {
      return true;
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet, Provider provider) {
      if (packet != null && packet.getTag() != null) {
         this.stacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
         AMCompat.loadAllItems(provider, packet.getTag(), this.stacks);
      }
   }

   public CompoundTag getUpdateTag(Provider provider) {
      return this.saveWithoutMetadata(provider);
   }

   protected NonNullList<ItemStack> getItems() {
      return this.stacks;
   }

   protected void setItems(NonNullList<ItemStack> items) {
      this.stacks = items;
   }

   public ItemStack removeItemNoUpdate(int index) {
      ItemStack lvt_2_1_ = (ItemStack)this.stacks.get(index);
      if (lvt_2_1_.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         this.stacks.set(index, ItemStack.EMPTY);
         return lvt_2_1_;
      }
   }

   public Component getDisplayName() {
      return this.getDefaultName();
   }

   protected Component getDefaultName() {
      return Component.translatable("block.alexsmobs.capsid");
   }

   protected AbstractContainerMenu createMenu(int id, Inventory player) {
      return null;
   }

   public boolean isEmpty() {
      for (int i = 0; i < this.getContainerSize(); i++) {
         if (!this.getItem(i).isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public float getBlockAngle() {
      if (this.getBlockState().getBlock() instanceof BlockCapsid) {
         Direction dir = (Direction)this.getBlockState().getValue(BlockCapsid.HORIZONTAL_FACING);
         return dir.toYRot();
      } else {
         return 0.0F;
      }
   }
}
