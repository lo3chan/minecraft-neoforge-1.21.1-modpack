package net.joefoxe.hexerei.tileentity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.joefoxe.hexerei.data.recipes.DipperRecipe;
import net.joefoxe.hexerei.data.recipes.ModRecipeTypes;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.util.message.EmitParticlesPacket;
import net.joefoxe.hexerei.util.message.TESyncPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class CandleDipperTile extends RandomizableContainerBlockEntity implements WorldlyContainer, Clearable, MenuProvider {
   protected NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
   public float numberOfCandles;
   public static int DRYING_START_TICKS = 60;
   public List<CandleDipperTile.DipperSlot> dipperSlots = new ArrayList<>(3);

   public CandleDipperTile(BlockEntityType<?> tileEntityTypeIn, BlockPos blockPos, BlockState blockState) {
      super(tileEntityTypeIn, blockPos, blockState);
      this.dipperSlots
         .add(new CandleDipperTile.DipperSlot(0, new Vec3(0.5, 0.4000000059604645, 0.5), CandleDipperTile.DipperState.NON, 200, 60, 3, 100, ItemStack.EMPTY));
      this.dipperSlots
         .add(new CandleDipperTile.DipperSlot(1, new Vec3(0.5, 0.4000000059604645, 0.5), CandleDipperTile.DipperState.NON, 200, 60, 3, 100, ItemStack.EMPTY));
      this.dipperSlots
         .add(new CandleDipperTile.DipperSlot(2, new Vec3(0.5, 0.4000000059604645, 0.5), CandleDipperTile.DipperState.NON, 200, 60, 3, 100, ItemStack.EMPTY));
   }

   public NonNullList<ItemStack> getItems() {
      return this.items;
   }

   public void setItems(NonNullList<ItemStack> itemsIn) {
      this.items = itemsIn;
   }

   public int getMaxStackSize() {
      return 1;
   }

   public void setChanged() {
      super.setChanged();
      this.sync();
   }

   public void sync() {
      if (this.level != null) {
         if (!this.level.isClientSide) {
            CompoundTag tag = new CompoundTag();
            this.saveAdditional(tag, this.level.registryAccess());
            HexereiPacketHandler.sendToNearbyClient(this.level, this.worldPosition, new TESyncPacket(this.worldPosition, tag));
         }

         if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.level.getBlockState(this.worldPosition), this.level.getBlockState(this.worldPosition), 2);
         }
      }
   }

   public void onLoad() {
      super.onLoad();
   }

   public CandleDipperTile(BlockPos blockPos, BlockState blockState) {
      this((BlockEntityType<?>)ModTileEntities.CANDLE_DIPPER_TILE.get(), blockPos, blockState);
   }

   public void setItem(int index, ItemStack stack) {
      if (index >= 0 && index < this.items.size()) {
         ItemStack itemStack = stack.copy();
         itemStack.setCount(1);
         this.items.set(index, itemStack);
         this.dipperSlots.get(index).dryingTicks = DRYING_START_TICKS;
         this.level.playSound(null, this.worldPosition, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.4F + 1.0F);
      }

      this.setChanged();
   }

   public ItemStack removeItem(int index, int p_59614_) {
      this.unpackLootTable(null);
      ItemStack itemstack = ContainerHelper.removeItem(this.getItems(), index, p_59614_);
      if (!itemstack.isEmpty()) {
         this.dipperSlots.get(index).state = CandleDipperTile.DipperState.NON;
         this.setChanged();
      }

      return itemstack;
   }

   private static CraftingContainer makeContainer(int width, int height, NonNullList<ItemStack> items) {
      return new TransientCraftingContainer(new AbstractContainerMenu(null, -1) {
         @NotNull
         public ItemStack quickMoveStack(@NotNull Player p_218264_, int p_218265_) {
            return ItemStack.EMPTY;
         }

         public boolean stillValid(@NotNull Player p_29888_) {
            return false;
         }
      }, width, height, items);
   }

   public void craft() {
      CraftingContainer container = makeContainer(3, 1, this.items);
      BlockEntity blockEntity = this.level.getBlockEntity(this.worldPosition.below());
      AtomicBoolean[] matchesRecipe = new AtomicBoolean[3];

      for (int i = 0; i < matchesRecipe.length; i++) {
         matchesRecipe[i] = new AtomicBoolean(false);
      }

      if (blockEntity instanceof MixingCauldronTile mixingCauldronTile) {
         List<DipperRecipe> recipes = this.level
            .getRecipeManager()
            .getRecipesFor((RecipeType)ModRecipeTypes.DIPPER_TYPE.get(), container.asCraftInput(), this.level)
            .stream()
            .filter(dipperRecipe -> {
               FluidStack tileFluid = mixingCauldronTile.getFluidStack();
               FluidStack recipeFluid = ((DipperRecipe)dipperRecipe.value()).getLiquid();
               return FluidStack.isSameFluidSameComponents(tileFluid, recipeFluid);
            })
            .<DipperRecipe>map(RecipeHolder::value)
            .toList();
         recipes.forEach(
            iRecipe -> {
               ItemStack output = iRecipe.getResultItem(this.level.registryAccess());
               ItemStack input = ((Ingredient)iRecipe.getIngredients().getFirst()).getItems()[0];
               boolean matchesFluid = FluidStack.isSameFluidSameComponents(iRecipe.getLiquid(), mixingCauldronTile.getFluidStack())
                  && iRecipe.getFluidLevelsConsumed() <= mixingCauldronTile.getFluidStack().getAmount();
               boolean useInputItemAsOutput = iRecipe.getUseInputItemAsOutput();

               for (int ix = 0; ix < matchesRecipe.length; ix++) {
                  boolean same = ItemStack.isSameItemSameComponents(input, (ItemStack)this.items.get(ix));
                  CandleDipperTile.DipperSlot dipperSlot = this.dipperSlots.get(ix);
                  if (same && !matchesRecipe[ix].get()) {
                     if (matchesFluid) {
                        matchesRecipe[ix].set(true);
                        if (dipperSlot.isNon()) {
                           dipperSlot.state = CandleDipperTile.DipperState.DRYING;
                           dipperSlot.output = output.copy();
                           if (useInputItemAsOutput) {
                              ItemStack stack = ((ItemStack)this.items.get(ix)).copy();
                              DataComponentMap map = DataComponentMap.composite(stack.getComponents(), output.getComponents());
                              stack.applyComponents(map);
                              dipperSlot.output = stack;
                           }

                           dipperSlot.fluidConsumptionAmount = iRecipe.getFluidLevelsConsumed();
                           dipperSlot.timesDipped = 0;
                           dipperSlot.timesDippedMax = iRecipe.getNumberOfDips();
                           dipperSlot.dryingTicksMax = iRecipe.getDryingTime();
                           dipperSlot.dryingTicks = DRYING_START_TICKS;
                           dipperSlot.dippingTicksMax = iRecipe.getDippingTime();
                           dipperSlot.dippingTicks = dipperSlot.dippingTicksMax;
                           this.setChanged();
                        }
                     }
                  } else if (matchesFluid && dipperSlot.isCrafting()) {
                     dipperSlot.state = CandleDipperTile.DipperState.NON;
                     this.setChanged();
                  }
               }
            }
         );

         for (int i = 0; i < matchesRecipe.length; i++) {
            if (!matchesRecipe[i].get() && this.dipperSlots.get(i).isCrafting()) {
               this.dipperSlots.get(i).state = CandleDipperTile.DipperState.NON;
               this.dipperSlots.get(i).dryingTicks = DRYING_START_TICKS;
               this.setChanged();
            }
         }

         for (CandleDipperTile.DipperSlot slot : this.dipperSlots) {
            if (slot.isDrying() && slot.timesDipped < slot.timesDippedMax) {
               slot.dryingTicks--;
               if (slot.dryingTicks <= 0) {
                  slot.dryingTicks = slot.dryingTicksMax;
                  slot.state = CandleDipperTile.DipperState.DUNKING;
                  this.setChanged();
               }
            } else if (slot.isDunking()) {
               if (mixingCauldronTile.getFluidStack().getAmount() > 0) {
                  slot.dippingTicks--;
               }

               if (slot.dippingTicks <= 0) {
                  slot.dippingTicks = slot.dippingTicksMax;
                  slot.state = CandleDipperTile.DipperState.DRYING;
                  slot.dryingTicks = slot.dryingTicksMax;
                  slot.timesDipped++;
                  this.decreaseFluid(slot.fluidConsumptionAmount);
                  if (slot.timesDipped >= slot.timesDippedMax) {
                     slot.state = CandleDipperTile.DipperState.FINISHED;
                     slot.timesDipped = 0;
                     slot.dippingTicks = slot.dippingTicksMax;
                     slot.dryingTicks = slot.dryingTicksMax;
                     this.level
                        .playSound(
                           null, this.worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.4F + 1.0F
                        );
                     this.items.set(slot.index, slot.output);
                  }

                  this.setChanged();
               }
            }
         }
      }
   }

   protected void loadAdditional(CompoundTag tag, Provider registries) {
      this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      if (!this.tryLoadLootTable(tag)) {
         ContainerHelper.loadAllItems(tag, this.items, registries);
      }

      if (tag.contains("slot0")) {
         this.dipperSlots.get(0).load(tag.getCompound("slot0"), registries);
      }

      if (tag.contains("slot1")) {
         this.dipperSlots.get(1).load(tag.getCompound("slot1"), registries);
      }

      if (tag.contains("slot2")) {
         this.dipperSlots.get(2).load(tag.getCompound("slot2"), registries);
      }

      super.loadAdditional(tag, registries);
   }

   protected Component getDefaultName() {
      return Component.translatable("container.hexerei.dipper");
   }

   protected AbstractContainerMenu createMenu(int p_58627_, Inventory p_58628_) {
      return null;
   }

   protected void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      ContainerHelper.saveAllItems(tag, this.items, registries);
      tag.put("slot0", this.dipperSlots.get(0).save(registries));
      tag.put("slot1", this.dipperSlots.get(1).save(registries));
      tag.put("slot2", this.dipperSlots.get(2).save(registries));
   }

   public CompoundTag getUpdateTag(Provider registries) {
      CompoundTag tag = new CompoundTag();
      this.saveAdditional(tag, registries);
      return tag;
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this, (tag, registryAccess) -> this.getUpdateTag(registryAccess));
   }

   public float getAngle(Vec3 pos) {
      float angle = (float)Math.toDegrees(Math.atan2(pos.z() - this.getBlockPos().getZ() - 0.5, pos.x() - this.getBlockPos().getX() - 0.5));
      if (angle < 0.0F) {
         angle += 360.0F;
      }

      return angle;
   }

   public float getSpeed(double pos, double posTo) {
      return (float)(9.999999747378752E-5 + 0.15000000596046448 * Math.abs(pos - posTo));
   }

   public Vec3 rotateAroundVec(Vec3 vector3dCenter, float rotation, Vec3 vector3d) {
      Vec3 newVec = vector3d.subtract(vector3dCenter);
      newVec = newVec.yRot(rotation / 180.0F * 3.1415927F);
      return newVec.add(vector3dCenter);
   }

   public int putItems(int slot, @Nonnull ItemStack stack) {
      if (((ItemStack)this.items.get(slot)).isEmpty()) {
         ItemStack stack1 = stack.copy();
         stack1.setCount(1);
         this.items.set(slot, stack1);
         this.setChanged();
         stack.shrink(1);
         return 1;
      } else {
         return !ItemStack.isSameItemSameComponents(stack, (ItemStack)this.items.get(slot)) ? 0 : 1;
      }
   }

   public InteractionResult interactWithoutItem(Player player) {
      if (player.isShiftKeyDown()) {
         boolean flag = false;

         for (int i = 0; i < 3; i++) {
            CandleDipperTile.DipperSlot dipperSlot = this.dipperSlots.get(i);
            if (!((ItemStack)this.items.get(i)).isEmpty() && !dipperSlot.isCrafting()) {
               dipperSlot.timesDipped = 0;
               dipperSlot.dippingTicks = dipperSlot.dippingTicksMax;
               dipperSlot.state = CandleDipperTile.DipperState.NON;
               dipperSlot.dryingTicks = dipperSlot.dryingTicksMax;
               player.getInventory().placeItemBackInInventory(((ItemStack)this.items.get(i)).copy());
               this.level
                  .playSound(
                     null, this.worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.4F + 1.0F
                  );
               this.items.set(i, ItemStack.EMPTY);
               dipperSlot.output = ItemStack.EMPTY;
               flag = true;
            }
         }

         if (flag) {
            return InteractionResult.SUCCESS;
         }
      }

      return InteractionResult.PASS;
   }

   public ItemInteractionResult interactWithItem(Player player) {
      if (player.isShiftKeyDown()) {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      } else {
         if (!player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
            Random rand = new Random();

            for (int i = 0; i < 3; i++) {
               if (((ItemStack)this.items.get(i)).isEmpty()) {
                  this.putItems(i, player.getItemInHand(InteractionHand.MAIN_HAND));
                  this.dipperSlots.get(i).dryingTicks = DRYING_START_TICKS;
                  this.level.playSound(null, this.worldPosition, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, rand.nextFloat() * 0.4F + 1.0F);
                  return ItemInteractionResult.SUCCESS;
               }
            }
         }

         boolean flag = false;

         for (int ix = 0; ix < 3; ix++) {
            CandleDipperTile.DipperSlot dipperSlot = this.dipperSlots.get(ix);
            if (dipperSlot.isFinished()) {
               dipperSlot.timesDipped = 0;
               dipperSlot.dippingTicks = dipperSlot.dippingTicksMax;
               dipperSlot.state = CandleDipperTile.DipperState.NON;
               dipperSlot.dryingTicks = dipperSlot.dryingTicksMax;
               player.getInventory().placeItemBackInInventory(((ItemStack)this.items.get(ix)).copy());
               this.level
                  .playSound(
                     null, this.worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.4F + 1.0F
                  );
               this.items.set(ix, ItemStack.EMPTY);
               dipperSlot.output = ItemStack.EMPTY;
               flag = true;
            }
         }

         return flag ? ItemInteractionResult.SUCCESS : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }
   }

   public void tick() {
      if (this.level instanceof ServerLevel) {
         this.craft();
      }

      for (CandleDipperTile.DipperSlot slot : this.dipperSlots) {
         slot.posLast = slot.pos;
      }

      this.numberOfCandles = 0.0F;
      Vec3[] targetPos = new Vec3[]{new Vec3(0.25, 0.0, 0.0625), new Vec3(0.5, 0.0, 0.0625), new Vec3(0.75, 0.0, 0.0625)};
      if (this.level != null && this.level.getBlockEntity(this.worldPosition.below()) instanceof MixingCauldronTile cauldronTile) {
         float fillPercentage = 0.0F;
         FluidStack fluidStack = cauldronTile.getFluidInTank(0);
         if (!fluidStack.isEmpty()) {
            fillPercentage = Math.min(1.0F, (float)fluidStack.getAmount() / cauldronTile.getTankCapacity(0));
         }

         float height = 0.25F + 0.6875F * fillPercentage - 1.0F + 0.0625F;
         CandleDipperTile.DipperSlot dipperSlot = this.dipperSlots.get(0);
         if (dipperSlot.isDrying() || !((ItemStack)this.items.get(0)).isEmpty()) {
            targetPos[0] = new Vec3(targetPos[0].x(), 0.3125 + Math.sin((float)this.level.getGameTime() / 16.0F) / 32.0, 0.5);
         }

         if (dipperSlot.isDunking()) {
            targetPos[0] = new Vec3(targetPos[0].x(), height + Math.sin((float)this.level.getGameTime() / 16.0F) / 32.0, 0.5);
         }

         if (dipperSlot.isFinished()) {
            targetPos[0] = new Vec3(targetPos[0].x(), 0.625 + Math.sin((float)this.level.getGameTime() / 16.0F) / 32.0, 0.5);
         }

         dipperSlot = this.dipperSlots.get(1);
         if (dipperSlot.isDrying() || !((ItemStack)this.items.get(1)).isEmpty()) {
            targetPos[1] = new Vec3(targetPos[1].x(), 0.3125 + Math.sin(((float)this.level.getGameTime() + 20.0F) / 14.0F) / 32.0, 0.5);
         }

         if (dipperSlot.isDunking()) {
            targetPos[1] = new Vec3(targetPos[1].x(), height + Math.sin(((float)this.level.getGameTime() + 20.0F) / 14.0F) / 32.0, 0.5);
         }

         if (dipperSlot.isFinished()) {
            targetPos[1] = new Vec3(targetPos[1].x(), 0.625 + Math.sin(((float)this.level.getGameTime() + 20.0F) / 14.0F) / 32.0, 0.5);
         }

         dipperSlot = this.dipperSlots.get(2);
         if (dipperSlot.isDrying() || !((ItemStack)this.items.get(2)).isEmpty()) {
            targetPos[2] = new Vec3(targetPos[2].x(), 0.3125 + Math.sin(((float)this.level.getGameTime() + 40.0F) / 15.0F) / 32.0, 0.5);
         }

         if (dipperSlot.isDunking()) {
            targetPos[2] = new Vec3(targetPos[2].x(), height + Math.sin(((float)this.level.getGameTime() + 40.0F) / 15.0F) / 32.0, 0.5);
         }

         if (dipperSlot.isFinished()) {
            targetPos[2] = new Vec3(targetPos[2].x(), 0.625 + Math.sin(((float)this.level.getGameTime() + 40.0F) / 15.0F) / 32.0, 0.5);
         }

         Direction dir = (Direction)this.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
         int rot = dir == Direction.NORTH ? 180 : (dir == Direction.SOUTH ? 0 : (dir == Direction.EAST ? 90 : 270));

         for (int i = 0; i < this.dipperSlots.size(); i++) {
            targetPos[i] = this.rotateAroundVec(new Vec3(0.5, 0.0, 0.5), rot, targetPos[i]);
            CandleDipperTile.DipperSlot slot = this.dipperSlots.get(i);
            slot.pos = new Vec3(
               HexereiUtil.moveTo((float)slot.pos.x, (float)targetPos[i].x(), this.getSpeed((float)slot.pos.x, targetPos[i].x())),
               HexereiUtil.moveTo((float)slot.pos.y, (float)targetPos[i].y(), 0.75F * this.getSpeed((float)slot.pos.y, targetPos[i].y())),
               HexereiUtil.moveTo((float)slot.pos.z, (float)targetPos[i].z(), this.getSpeed((float)slot.pos.z, targetPos[i].z()))
            );
         }
      }
   }

   private void decreaseFluid(int amount) {
      if (this.level.getBlockEntity(this.worldPosition.below()) instanceof MixingCauldronTile cauldronTile && !this.level.isClientSide()) {
         cauldronTile.getFluidStack().shrink(amount);
         cauldronTile.setChanged();
         HexereiPacketHandler.sendToNearbyClient(this.level, cauldronTile.getPos(), new EmitParticlesPacket(cauldronTile.getPos(), 10, false));
      }
   }

   public int[] getSlotsForFace(Direction p_19238_) {
      return new int[]{0, 1, 2};
   }

   public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, @Nullable Direction direction) {
      return this.canPlaceItem(index, itemStackIn);
   }

   public boolean canPlaceItem(int index, ItemStack stack) {
      return ((ItemStack)this.items.get(index)).isEmpty();
   }

   public boolean canTakeItemThroughFace(int index, ItemStack p_19240_, Direction p_19241_) {
      return !this.dipperSlots.get(index).isCrafting();
   }

   public int getContainerSize() {
      return this.items.size();
   }

   public static class DipperSlot {
      public int index;
      public Vec3 pos;
      public Vec3 posLast;
      public CandleDipperTile.DipperState state;
      public int dippingTicks;
      public int dippingTicksMax;
      public int dryingTicks;
      public int dryingTicksMax;
      public int timesDipped;
      public int timesDippedMax;
      public int fluidConsumptionAmount;
      public ItemStack output;

      public DipperSlot(
         int index,
         Vec3 pos,
         CandleDipperTile.DipperState state,
         int dippingTicksMax,
         int dryingTicksMax,
         int timesDippedMax,
         int fluidConsumptionAmount,
         ItemStack output
      ) {
         this.index = index;
         this.pos = pos;
         this.posLast = pos;
         this.state = state;
         this.dippingTicks = dippingTicksMax;
         this.dippingTicksMax = dippingTicksMax;
         this.dryingTicks = dryingTicksMax;
         this.dryingTicksMax = dryingTicksMax;
         this.timesDipped = 0;
         this.timesDippedMax = timesDippedMax;
         this.fluidConsumptionAmount = fluidConsumptionAmount;
         this.output = output;
      }

      public boolean isCrafting() {
         return this.state != CandleDipperTile.DipperState.NON && this.state != CandleDipperTile.DipperState.FINISHED;
      }

      public boolean isDrying() {
         return this.state == CandleDipperTile.DipperState.DRYING;
      }

      public boolean isDunking() {
         return this.state == CandleDipperTile.DipperState.DUNKING;
      }

      public boolean isFinished() {
         return this.state == CandleDipperTile.DipperState.FINISHED;
      }

      public boolean isNon() {
         return this.state == CandleDipperTile.DipperState.NON;
      }

      public CompoundTag save(Provider registries) {
         CompoundTag tag = new CompoundTag();
         tag.putInt("state", this.state.ordinal());
         tag.putInt("dippingTicks", this.dippingTicks);
         tag.putInt("dippingTicksMax", this.dippingTicksMax);
         tag.putInt("dryingTicks", this.dryingTicks);
         tag.putInt("dryingTicksMax", this.dryingTicksMax);
         tag.putInt("timesDipped", this.timesDipped);
         tag.putInt("timesDippedMax", this.timesDippedMax);
         tag.putInt("fluidConsumptionAmount", this.fluidConsumptionAmount);
         if (!this.output.isEmpty()) {
            tag.put("output", this.output.save(registries));
         }

         return tag;
      }

      public void load(CompoundTag tag, Provider registries) {
         this.state = CandleDipperTile.DipperState.byId(tag.getInt("state"));
         this.dippingTicks = tag.getInt("dippingTicks");
         this.dippingTicksMax = tag.getInt("dippingTicksMax");
         this.dryingTicks = tag.getInt("dryingTicks");
         this.dryingTicksMax = tag.getInt("dryingTicksMax");
         this.timesDipped = tag.getInt("timesDipped");
         this.timesDippedMax = tag.getInt("timesDippedMax");
         this.fluidConsumptionAmount = tag.getInt("fluidConsumptionAmount");
         if (tag.contains("output")) {
            this.output = ItemStack.parse(registries, tag.getCompound("output")).orElse(ItemStack.EMPTY);
         }
      }
   }

   public static enum DipperState {
      DRYING,
      DUNKING,
      FINISHED,
      NON;

      public static CandleDipperTile.DipperState byId(int id) {
         CandleDipperTile.DipperState[] type = values();
         return type[id >= 0 && id < type.length ? id : 0];
      }
   }
}
