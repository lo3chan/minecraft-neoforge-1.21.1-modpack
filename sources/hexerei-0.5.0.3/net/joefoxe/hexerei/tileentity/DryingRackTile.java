package net.joefoxe.hexerei.tileentity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.joefoxe.hexerei.data.recipes.DryingRackRecipe;
import net.joefoxe.hexerei.data.recipes.ModRecipeTypes;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.message.TESyncPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
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
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class DryingRackTile extends RandomizableContainerBlockEntity implements WorldlyContainer, Clearable, MenuProvider {
   protected NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
   public boolean[] crafted = new boolean[]{false, false, false};
   public boolean[] crafting = new boolean[]{false, false, false};
   public int[] dryingTimeMax = new int[]{200, 200, 200};
   public int[] dryingTime = new int[]{200, 200, 200};
   public int[] placedTime = new int[]{0, 0, 0};
   public ItemStack[] output = new ItemStack[]{ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY};

   public DryingRackTile(BlockEntityType<?> tileEntityTypeIn, BlockPos blockPos, BlockState blockState) {
      super(tileEntityTypeIn, blockPos, blockState);
   }

   public NonNullList<ItemStack> getItems() {
      return this.items;
   }

   public void setItems(NonNullList<ItemStack> itemsIn) {
      this.items = itemsIn;
   }

   public int getMaxStackSize() {
      return 3;
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

   public Item getItemInSlot(int slot) {
      return ((ItemStack)this.items.get(slot)).getItem();
   }

   public int getNumberOfItems() {
      int num = 0;

      for (int i = 0; i < 8; i++) {
         if (this.items.get(i) != ItemStack.EMPTY) {
            num++;
         }
      }

      return num;
   }

   public void onLoad() {
      super.onLoad();
   }

   public DryingRackTile(BlockPos blockPos, BlockState blockState) {
      this((BlockEntityType<?>)ModTileEntities.DRYING_RACK_TILE.get(), blockPos, blockState);
   }

   public void setItem(int index, ItemStack stack) {
      if (index >= 0 && index < this.items.size()) {
         ItemStack itemStack = stack.copy();
         this.items.set(index, itemStack);
         if (index == 0) {
            this.dryingTime[0] = this.dryingTimeMax[0];
         }

         if (index == 1) {
            this.dryingTime[1] = this.dryingTimeMax[1];
         }

         if (index == 2) {
            this.dryingTime[2] = this.dryingTimeMax[2];
         }

         this.level.playSound(null, this.worldPosition, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.4F + 1.0F);
      }

      this.sync();
   }

   public ItemStack removeItem(int index, int p_59614_) {
      this.unpackLootTable(null);
      ItemStack itemstack = ContainerHelper.removeItem(this.getItems(), index, p_59614_);
      if (!itemstack.isEmpty()) {
         this.setChanged();
         if (index == 0) {
            this.crafted[0] = false;
            this.sync();
         }

         if (index == 0) {
            this.crafted[1] = false;
            this.sync();
         }

         if (index == 0) {
            this.crafted[2] = false;
            this.sync();
         }
      }

      return itemstack;
   }

   public void craft() {
      List<CraftingInput> inv = new ArrayList<>();
      inv.add(CraftingInput.of(1, 1, List.of((ItemStack)this.items.get(0))));
      inv.add(CraftingInput.of(1, 1, List.of((ItemStack)this.items.get(1))));
      inv.add(CraftingInput.of(1, 1, List.of((ItemStack)this.items.get(2))));

      for (int i = 0; i < 3; i++) {
         Optional<RecipeHolder<DryingRackRecipe>> recipe = this.level
            .getRecipeManager()
            .getRecipeFor((RecipeType)ModRecipeTypes.DRYING_RACK_TYPE.get(), inv.get(i), this.level);
         BlockEntity blockEntity = this.level.getBlockEntity(this.worldPosition);
         if (blockEntity instanceof DryingRackTile) {
            int j = i;
            recipe.ifPresent(iRecipe -> {
               ItemStack recipeOutput = ((DryingRackRecipe)iRecipe.value()).getResultItem(this.level.registryAccess());
               ItemStack input = ((Ingredient)((DryingRackRecipe)iRecipe.value()).getIngredients().get(0)).getItems()[0];
               if (input.getItem() == ((ItemStack)this.items.get(j)).getItem()) {
                  if (!this.crafting[j]) {
                     this.crafting[j] = true;
                     this.output[j] = recipeOutput.copy();
                     this.dryingTimeMax[j] = ((DryingRackRecipe)iRecipe.value()).getDryingTime();
                     this.dryingTime[j] = this.dryingTimeMax[j];
                     this.sync();
                  }
               } else if (this.crafting[j]) {
                  this.crafting[j] = false;
                  this.sync();
               }
            });
         }
      }
   }

   private void craftTheItem(ItemStack output, int slot) {
      output.setCount(((ItemStack)this.items.get(slot)).getCount());
      this.setItem(slot, output);
   }

   protected void loadAdditional(CompoundTag tag, Provider registries) {
      this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      if (!this.tryLoadLootTable(tag)) {
         ContainerHelper.loadAllItems(tag, this.items, registries);
      }

      if (tag.contains("dryingTime[0]", 3)) {
         this.dryingTime[0] = tag.getInt("dryingTime[0]");
      }

      if (tag.contains("dryingTime[1]", 3)) {
         this.dryingTime[1] = tag.getInt("dryingTime[1]");
      }

      if (tag.contains("dryingTime[2]", 3)) {
         this.dryingTime[2] = tag.getInt("dryingTime[2]");
      }

      if (tag.contains("crafting[0]", 3)) {
         this.crafting[0] = tag.getInt("crafting[0]") == 1;
      }

      if (tag.contains("crafting[1]", 3)) {
         this.crafting[1] = tag.getInt("crafting[1]") == 1;
      }

      if (tag.contains("crafting[2]", 3)) {
         this.crafting[2] = tag.getInt("crafting[2]") == 1;
      }

      if (tag.contains("crafted[0]", 3)) {
         this.crafted[0] = tag.getInt("crafted[0]") == 1;
      }

      if (tag.contains("crafted[1]", 3)) {
         this.crafted[1] = tag.getInt("crafted[1]") == 1;
      }

      if (tag.contains("crafted[2]", 3)) {
         this.crafted[2] = tag.getInt("crafted[2]") == 1;
      }

      if (tag.contains("placedTime[0]", 3)) {
         this.placedTime[0] = tag.getInt("placedTime[0]");
      }

      if (tag.contains("placedTime[1]", 3)) {
         this.placedTime[1] = tag.getInt("placedTime[1]");
      }

      if (tag.contains("placedTime[2]", 3)) {
         this.placedTime[2] = tag.getInt("placedTime[2]");
      }

      if (tag.contains("output[0]")) {
         this.output[0] = ItemStack.parseOptional(registries, tag.getCompound("output[0]"));
      }

      if (tag.contains("output[1]")) {
         this.output[1] = ItemStack.parseOptional(registries, tag.getCompound("output[1]"));
      }

      if (tag.contains("output[2]")) {
         this.output[2] = ItemStack.parseOptional(registries, tag.getCompound("output[2]"));
      }

      super.loadAdditional(tag, registries);
   }

   protected Component getDefaultName() {
      return Component.translatable("container.hexerei.dipper");
   }

   protected AbstractContainerMenu createMenu(int p_58627_, Inventory p_58628_) {
      return null;
   }

   public void saveAdditional(CompoundTag compound, Provider registries) {
      super.saveAdditional(compound, registries);
      ContainerHelper.saveAllItems(compound, this.items, registries);
      compound.putInt("dryingTime[0]", this.dryingTime[0]);
      compound.putInt("dryingTime[1]", this.dryingTime[1]);
      compound.putInt("dryingTime[2]", this.dryingTime[2]);
      compound.putInt("crafted[0]", this.crafted[0] ? 1 : 0);
      compound.putInt("crafted[1]", this.crafted[1] ? 1 : 0);
      compound.putInt("crafted[2]", this.crafted[2] ? 1 : 0);
      compound.putInt("crafting[0]", this.crafting[0] ? 1 : 0);
      compound.putInt("crafting[1]", this.crafting[1] ? 1 : 0);
      compound.putInt("crafting[2]", this.crafting[2] ? 1 : 0);
      compound.putInt("placedTime[0]", this.placedTime[0]);
      compound.putInt("placedTime[1]", this.placedTime[1]);
      compound.putInt("placedTime[2]", this.placedTime[2]);
      if (!this.output[0].isEmpty()) {
         compound.put("output[0]", this.output[0].save(registries, new CompoundTag()));
      }

      if (!this.output[1].isEmpty()) {
         compound.put("output[1]", this.output[1].save(registries, new CompoundTag()));
      }

      if (!this.output[2].isEmpty()) {
         compound.put("output[2]", this.output[2].save(registries, new CompoundTag()));
      }
   }

   public CompoundTag save(CompoundTag compound, Provider registries) {
      this.saveAdditional(compound, registries);
      return compound;
   }

   public CompoundTag getUpdateTag(Provider registries) {
      return this.save(new CompoundTag(), registries);
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this, (tag, registryAccess) -> this.getUpdateTag(registryAccess));
   }

   public static double getDistanceToEntity(Entity entity, BlockPos pos) {
      double deltaX = entity.position().x() - pos.getX() - 0.5;
      double deltaY = entity.position().y() - pos.getY() - 0.5;
      double deltaZ = entity.position().z() - pos.getZ() - 0.5;
      return Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
   }

   public static double getDistance(float x1, float y1, float x2, float y2) {
      double deltaX = x2 - x1;
      double deltaY = y2 - y1;
      return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
   }

   public float getAngle(Vec3 pos) {
      float angle = (float)Math.toDegrees(Math.atan2(pos.z() - this.getBlockPos().getZ() - 0.5, pos.x() - this.getBlockPos().getX() - 0.5));
      if (angle < 0.0F) {
         angle += 360.0F;
      }

      return angle;
   }

   public float getSpeed(double pos, double posTo) {
      return (float)(0.009999999776482582 + 0.10000000149011612 * (Math.abs(pos - posTo) / 3.0));
   }

   public Vec3 rotateAroundVec(Vec3 vector3dCenter, float rotation, Vec3 vector3d) {
      Vec3 newVec = vector3d.subtract(vector3dCenter);
      newVec = newVec.yRot(rotation / 180.0F * 3.1415927F);
      return newVec.add(vector3dCenter);
   }

   public int putItems(int slot, @Nonnull ItemStack stack) {
      ItemStack stack1 = stack.copy();
      Random rand = new Random();
      if (((ItemStack)this.items.get(slot)).isEmpty()) {
         if (stack1.getCount() > 2) {
            stack1.setCount(3);
            this.items.set(slot, stack1);
            this.sync();
            stack.shrink(3);

            for (int i = 0; i < 3; i++) {
               this.level.playSound(null, this.worldPosition, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, rand.nextFloat() * 0.4F + 1.0F);
            }
         } else {
            this.items.set(slot, stack1);
            this.sync();
            stack.shrink(stack.getCount());

            for (int i = 0; i < stack1.getCount(); i++) {
               this.level.playSound(null, this.worldPosition, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, rand.nextFloat() * 0.4F + 1.0F);
            }
         }

         return 1;
      } else if (!ItemStack.isSameItemSameComponents(stack, (ItemStack)this.items.get(slot))) {
         return 0;
      } else {
         int count = ((ItemStack)this.items.get(slot)).getCount();
         if (stack1.getCount() > 2 - count) {
            stack1.setCount(3);
            this.items.set(slot, stack1);
            this.sync();
            stack.shrink(3 - count);

            for (int i = 0; i < 3 - count; i++) {
               this.level.playSound(null, this.worldPosition, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, rand.nextFloat() * 0.4F + 1.0F);
            }
         } else {
            stack1.setCount(count + stack1.getCount());
            this.items.set(slot, stack1);
            this.sync();
            stack.shrink(stack.getCount());

            for (int i = 0; i < stack1.getCount(); i++) {
               this.level.playSound(null, this.worldPosition, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, rand.nextFloat() * 0.4F + 1.0F);
            }
         }

         return 1;
      }
   }

   public int interactDryingRack(Player player, BlockHitResult hit) {
      if (this.level == null) {
         return 0;
      } else {
         if (!player.isShiftKeyDown()) {
            if (!player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
               new Random();
               if (((ItemStack)this.items.get(0)).isEmpty() || player.getItemInHand(InteractionHand.MAIN_HAND) == this.items.get(0)) {
                  this.putItems(0, player.getItemInHand(InteractionHand.MAIN_HAND));
                  this.dryingTime[0] = this.dryingTimeMax[0];
                  this.crafted[0] = false;
                  return 1;
               }

               if (ItemStack.isSameItemSameComponents(player.getItemInHand(InteractionHand.MAIN_HAND), (ItemStack)this.items.get(0))
                  && ((ItemStack)this.items.get(0)).getCount() < this.getMaxStackSize()) {
                  this.putItems(0, player.getItemInHand(InteractionHand.MAIN_HAND));
               } else {
                  if (((ItemStack)this.items.get(1)).isEmpty()
                     || player.getItemInHand(InteractionHand.MAIN_HAND) == this.items.get(1)
                        && ((ItemStack)this.items.get(1)).getCount() < this.getMaxStackSize()) {
                     this.putItems(1, player.getItemInHand(InteractionHand.MAIN_HAND));
                     this.dryingTime[1] = this.dryingTimeMax[1];
                     this.crafted[1] = false;
                     return 1;
                  }

                  if (ItemStack.isSameItemSameComponents(player.getItemInHand(InteractionHand.MAIN_HAND), (ItemStack)this.items.get(1))
                     && ((ItemStack)this.items.get(1)).getCount() < this.getMaxStackSize()) {
                     this.putItems(1, player.getItemInHand(InteractionHand.MAIN_HAND));
                  } else {
                     if (((ItemStack)this.items.get(2)).isEmpty()
                        || player.getItemInHand(InteractionHand.MAIN_HAND) == this.items.get(2)
                           && ((ItemStack)this.items.get(2)).getCount() < this.getMaxStackSize()) {
                        this.putItems(2, player.getItemInHand(InteractionHand.MAIN_HAND));
                        this.dryingTime[2] = this.dryingTimeMax[2];
                        this.crafted[2] = false;
                        return 1;
                     }

                     if (ItemStack.isSameItemSameComponents(player.getItemInHand(InteractionHand.MAIN_HAND), (ItemStack)this.items.get(2))
                        && ((ItemStack)this.items.get(2)).getCount() < this.getMaxStackSize()) {
                        this.putItems(2, player.getItemInHand(InteractionHand.MAIN_HAND));
                     }
                  }
               }
            }

            if (this.crafted[0]) {
               this.crafted[0] = false;
               this.dryingTime[0] = this.dryingTimeMax[0];
               player.getInventory().placeItemBackInInventory(((ItemStack)this.items.get(0)).copy());
               this.level
                  .playSound(
                     null, this.worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.4F + 1.0F
                  );
               this.items.set(0, ItemStack.EMPTY);
               this.output[0] = ItemStack.EMPTY;
            }

            if (this.crafted[1]) {
               this.crafted[1] = false;
               this.dryingTime[1] = this.dryingTimeMax[1];
               player.getInventory().placeItemBackInInventory(((ItemStack)this.items.get(1)).copy());
               this.level
                  .playSound(
                     null, this.worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.4F + 1.0F
                  );
               this.items.set(1, ItemStack.EMPTY);
               this.output[1] = ItemStack.EMPTY;
            }

            if (this.crafted[2]) {
               this.crafted[2] = false;
               this.dryingTime[2] = this.dryingTimeMax[2];
               player.getInventory().placeItemBackInInventory(((ItemStack)this.items.get(2)).copy());
               this.level
                  .playSound(
                     null, this.worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.4F + 1.0F
                  );
               this.items.set(2, ItemStack.EMPTY);
               this.output[2] = ItemStack.EMPTY;
            }

            this.sync();
         } else {
            if (!this.crafting[0]) {
               this.crafted[0] = false;
               this.dryingTime[0] = this.dryingTimeMax[0];
               player.getInventory().placeItemBackInInventory(((ItemStack)this.items.get(0)).copy());
               this.level
                  .playSound(
                     null, this.worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.4F + 1.0F
                  );
               this.items.set(0, ItemStack.EMPTY);
               this.output[0] = ItemStack.EMPTY;
               this.sync();
            }

            if (!((ItemStack)this.items.get(1)).isEmpty() && !this.crafting[1]) {
               this.crafted[1] = false;
               this.dryingTime[1] = this.dryingTimeMax[1];
               player.getInventory().placeItemBackInInventory(((ItemStack)this.items.get(1)).copy());
               this.level
                  .playSound(
                     null, this.worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.4F + 1.0F
                  );
               this.items.set(1, ItemStack.EMPTY);
               this.output[1] = ItemStack.EMPTY;
               this.sync();
            }

            if (!((ItemStack)this.items.get(2)).isEmpty() && !this.crafting[2]) {
               this.crafted[2] = false;
               this.dryingTime[2] = this.dryingTimeMax[2];
               player.getInventory().placeItemBackInInventory(((ItemStack)this.items.get(2)).copy());
               this.level
                  .playSound(
                     null, this.worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.4F + 1.0F
                  );
               this.items.set(2, ItemStack.EMPTY);
               this.output[2] = ItemStack.EMPTY;
               this.sync();
            }
         }

         return 0;
      }
   }

   public int interactWithoutItem(Player player, BlockHitResult hit) {
      if (this.level == null) {
         return 0;
      } else {
         if (!player.isShiftKeyDown()) {
            if (this.crafted[0]) {
               this.crafted[0] = false;
               this.dryingTime[0] = this.dryingTimeMax[0];
               player.getInventory().placeItemBackInInventory(((ItemStack)this.items.get(0)).copy());
               this.level
                  .playSound(
                     null, this.worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.4F + 1.0F
                  );
               this.items.set(0, ItemStack.EMPTY);
               this.output[0] = ItemStack.EMPTY;
            }

            if (this.crafted[1]) {
               this.crafted[1] = false;
               this.dryingTime[1] = this.dryingTimeMax[1];
               player.getInventory().placeItemBackInInventory(((ItemStack)this.items.get(1)).copy());
               this.level
                  .playSound(
                     null, this.worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.4F + 1.0F
                  );
               this.items.set(1, ItemStack.EMPTY);
               this.output[1] = ItemStack.EMPTY;
            }

            if (this.crafted[2]) {
               this.crafted[2] = false;
               this.dryingTime[2] = this.dryingTimeMax[2];
               player.getInventory().placeItemBackInInventory(((ItemStack)this.items.get(2)).copy());
               this.level
                  .playSound(
                     null, this.worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.4F + 1.0F
                  );
               this.items.set(2, ItemStack.EMPTY);
               this.output[2] = ItemStack.EMPTY;
            }

            this.sync();
         } else {
            if (!this.crafting[0]) {
               this.crafted[0] = false;
               this.dryingTime[0] = this.dryingTimeMax[0];
               player.getInventory().placeItemBackInInventory(((ItemStack)this.items.get(0)).copy());
               this.level
                  .playSound(
                     null, this.worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.4F + 1.0F
                  );
               this.items.set(0, ItemStack.EMPTY);
               this.output[0] = ItemStack.EMPTY;
               this.sync();
            }

            if (!((ItemStack)this.items.get(1)).isEmpty() && !this.crafting[1]) {
               this.crafted[1] = false;
               this.dryingTime[1] = this.dryingTimeMax[1];
               player.getInventory().placeItemBackInInventory(((ItemStack)this.items.get(1)).copy());
               this.level
                  .playSound(
                     null, this.worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.4F + 1.0F
                  );
               this.items.set(1, ItemStack.EMPTY);
               this.output[1] = ItemStack.EMPTY;
               this.sync();
            }

            if (!((ItemStack)this.items.get(2)).isEmpty() && !this.crafting[2]) {
               this.crafted[2] = false;
               this.dryingTime[2] = this.dryingTimeMax[2];
               player.getInventory().placeItemBackInInventory(((ItemStack)this.items.get(2)).copy());
               this.level
                  .playSound(
                     null, this.worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.4F + 1.0F
                  );
               this.items.set(2, ItemStack.EMPTY);
               this.output[2] = ItemStack.EMPTY;
               this.sync();
            }
         }

         return 0;
      }
   }

   public void tick() {
      if (this.level instanceof ServerLevel) {
         this.craft();

         for (int i = 0; i < 3; i++) {
            if (((ItemStack)this.items.get(i)).isEmpty()) {
               this.placedTime[i] = 0;
            } else {
               this.placedTime[i]++;
            }

            if (this.crafting[i]) {
               if (this.dryingTime[i] > 0) {
                  this.dryingTime[i]--;
               }

               if (this.dryingTime[i] == 0) {
                  this.crafted[i] = true;
                  this.crafting[i] = false;
                  this.craftTheItem(this.output[i], i);
               }
            }
         }
      }
   }

   public int[] getSlotsForFace(Direction p_19238_) {
      return new int[]{0, 1, 2};
   }

   public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, @Nullable Direction direction) {
      return this.canPlaceItem(index, itemStackIn);
   }

   public boolean canPlaceItem(int index, ItemStack stack) {
      return ((ItemStack)this.items.get(index)).isEmpty() || ((ItemStack)this.items.get(index)).getCount() < this.getMaxStackSize();
   }

   public boolean canTakeItemThroughFace(int p_19239_, ItemStack p_19240_, Direction p_19241_) {
      return !this.crafting[p_19239_] && this.placedTime[p_19239_] >= 20;
   }

   public int getContainerSize() {
      return this.items.size();
   }
}
