package net.joefoxe.hexerei.tileentity;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.joefoxe.hexerei.data.recipes.ModRecipeTypes;
import net.joefoxe.hexerei.data.recipes.PestleAndMortarRecipe;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.message.TESyncPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Clearable;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class PestleAndMortarTile extends RandomizableContainerBlockEntity implements WorldlyContainer, Clearable, MenuProvider {
   public final ItemStackHandler itemHandler = this.createHandler();
   protected NonNullList<ItemStack> items = NonNullList.withSize(6, ItemStack.EMPTY);
   public int craftDelay;
   public static final int craftDelayMax = 100;
   public boolean crafted = false;
   public boolean crafting = false;
   public boolean grindSoundPlayed = false;
   public int grindingTimeMax = 200;
   public int grindingTime = 200;
   public ItemStack output = ItemStack.EMPTY;

   public PestleAndMortarTile(BlockEntityType<?> tileEntityTypeIn, BlockPos blockPos, BlockState blockState) {
      super(tileEntityTypeIn, blockPos, blockState);
   }

   private ItemStackHandler createHandler() {
      return new ItemStackHandler(6) {
         protected void onContentsChanged(int slot) {
            PestleAndMortarTile.this.sync();
         }

         public int getSlotLimit(int slot) {
            return slot != 5 ? 1 : 64;
         }
      };
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

   public Item getItemInSlot(int slot) {
      return ((ItemStack)this.items.get(slot)).getItem();
   }

   public ItemStack getItemStackInSlot(int slot) {
      return (ItemStack)this.items.get(slot);
   }

   public void onLoad() {
      super.onLoad();
   }

   public PestleAndMortarTile(BlockPos blockPos, BlockState blockState) {
      this((BlockEntityType<?>)ModTileEntities.PESTLE_AND_MORTAR_TILE.get(), blockPos, blockState);
   }

   public void setItem(int index, ItemStack stack) {
      if (index >= 0 && index < this.items.size()) {
         ItemStack itemStack = stack.copy();
         this.items.set(index, itemStack);
         if (index != 5) {
            this.grindingTime = this.grindingTimeMax;
         }
      }

      this.setChanged();
   }

   public ItemStack removeItem(int index, int p_59614_) {
      this.unpackLootTable(null);
      ItemStack itemstack = ContainerHelper.removeItem(this.getItems(), index, p_59614_);
      if (!itemstack.isEmpty()) {
         this.setChanged();
      }

      this.setChanged();
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
      CraftingContainer inv = makeContainer(5, 1, NonNullList.copyOf(this.items.stream().limit(5L).toList()));
      Optional<RecipeHolder<PestleAndMortarRecipe>> recipe = this.level
         .getRecipeManager()
         .getRecipeFor((RecipeType)ModRecipeTypes.PESTLE_AND_MORTAR_TYPE.get(), inv.asCraftInput(), this.level);
      BlockEntity blockEntity = this.level.getBlockEntity(this.worldPosition);
      AtomicBoolean matches = new AtomicBoolean(false);
      if (blockEntity instanceof PestleAndMortarTile pestleAndMortarTile) {
         recipe.ifPresent(iRecipe -> {
            this.output = ((PestleAndMortarRecipe)iRecipe.value()).getResultItem(this.level.registryAccess());
            matches.set(true);
            if (pestleAndMortarTile.getItemInSlot(5) == Items.AIR && !this.crafting) {
               this.crafting = true;
               this.grindingTimeMax = ((PestleAndMortarRecipe)iRecipe.value()).getGrindingTime();
               this.grindingTime = this.grindingTimeMax;
               this.setChanged();
            }
         });
      }

      if (!matches.get() && this.crafting) {
         this.crafting = false;
         this.setChanged();
      }
   }

   private void craftTheItem(ItemStack output) {
      this.items.set(0, ItemStack.EMPTY);
      this.items.set(1, ItemStack.EMPTY);
      this.items.set(2, ItemStack.EMPTY);
      this.items.set(3, ItemStack.EMPTY);
      this.items.set(4, ItemStack.EMPTY);
      this.items.set(5, output);
   }

   protected void loadAdditional(CompoundTag tag, Provider registries) {
      this.itemHandler.deserializeNBT(registries, tag.getCompound("inv"));
      this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      if (!this.tryLoadLootTable(tag)) {
         ContainerHelper.loadAllItems(tag, this.items, registries);
      }

      if (tag.contains("grindingTime", 3)) {
         this.grindingTime = tag.getInt("grindingTime");
      }

      if (tag.contains("grindingTimeMax", 3)) {
         this.grindingTimeMax = tag.getInt("grindingTimeMax");
      }

      if (tag.contains("crafting", 3)) {
         this.crafting = tag.getInt("crafting") == 1;
      }

      if (tag.contains("crafted", 3)) {
         this.crafted = tag.getInt("crafted") == 1;
      }

      super.loadAdditional(tag, registries);
   }

   protected Component getDefaultName() {
      return Component.translatable("container.hexerei.pestle_and_mortar");
   }

   protected AbstractContainerMenu createMenu(int p_58627_, Inventory p_58628_) {
      return null;
   }

   public void saveAdditional(CompoundTag compound, Provider registries) {
      super.saveAdditional(compound, registries);
      ContainerHelper.saveAllItems(compound, this.items, registries);
      compound.put("inv", this.itemHandler.serializeNBT(registries));
      compound.putInt("grindingTime", this.grindingTime);
      compound.putInt("grindingTimeMax", this.grindingTimeMax);
      compound.putInt("crafted", this.crafted ? 1 : 0);
      compound.putInt("crafting", this.crafting ? 1 : 0);
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this, (tag, registryAccess) -> this.getUpdateTag(registryAccess));
   }

   public CompoundTag getUpdateTag(Provider registries) {
      CompoundTag tag = super.getUpdateTag(registries);
      this.saveAdditional(tag, registries);
      return tag;
   }

   public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, Provider lookupProvider) {
      super.onDataPacket(net, pkt, lookupProvider);
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
         stack1.setCount(1);
         this.items.set(slot, stack1);
         this.grindingTime = this.grindingTimeMax;
         this.setChanged();
         stack.shrink(1);
         this.level.playSound(null, this.worldPosition, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, rand.nextFloat() * 0.4F + 1.0F);
         return 1;
      } else {
         return 0;
      }
   }

   public int interactPestleAndMortar(Player player, BlockHitResult hit) {
      if (this.level == null) {
         return 0;
      } else {
         if (!player.isShiftKeyDown()) {
            if (!this.level.isClientSide) {
               if (!((ItemStack)this.items.get(5)).isEmpty()) {
                  player.getInventory().placeItemBackInInventory(((ItemStack)this.items.get(5)).copy());
                  this.level
                     .playSound(
                        null, this.worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.4F + 1.0F
                     );
                  this.items.set(5, ItemStack.EMPTY);
                  this.setChanged();
               } else if (!player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && !this.level.isClientSide) {
                  new Random();
                  if (((ItemStack)this.items.get(0)).isEmpty()) {
                     this.putItems(0, player.getItemInHand(InteractionHand.MAIN_HAND));
                     return 1;
                  }

                  if (((ItemStack)this.items.get(1)).isEmpty()) {
                     this.putItems(1, player.getItemInHand(InteractionHand.MAIN_HAND));
                     return 1;
                  }

                  if (((ItemStack)this.items.get(2)).isEmpty()) {
                     this.putItems(2, player.getItemInHand(InteractionHand.MAIN_HAND));
                     return 1;
                  }

                  if (((ItemStack)this.items.get(3)).isEmpty()) {
                     this.putItems(3, player.getItemInHand(InteractionHand.MAIN_HAND));
                     return 1;
                  }

                  if (((ItemStack)this.items.get(4)).isEmpty()) {
                     this.putItems(4, player.getItemInHand(InteractionHand.MAIN_HAND));
                     return 1;
                  }
               }
            }
         } else if (!this.level.isClientSide) {
            if (!((ItemStack)this.items.get(5)).isEmpty()) {
               player.getInventory().placeItemBackInInventory(((ItemStack)this.items.get(5)).copy());
               this.level
                  .playSound(
                     null, this.worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.4F + 1.0F
                  );
               this.items.set(5, ItemStack.EMPTY);
            }

            if (!this.crafting) {
               if (!((ItemStack)this.items.get(0)).isEmpty()) {
                  player.getInventory().placeItemBackInInventory(((ItemStack)this.items.get(0)).copy());
                  this.level
                     .playSound(
                        null, this.worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.4F + 1.0F
                     );
                  this.items.set(0, ItemStack.EMPTY);
                  this.output = ItemStack.EMPTY;
               }

               if (!((ItemStack)this.items.get(1)).isEmpty()) {
                  player.getInventory().placeItemBackInInventory(((ItemStack)this.items.get(1)).copy());
                  this.level
                     .playSound(
                        null, this.worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.4F + 1.0F
                     );
                  this.items.set(1, ItemStack.EMPTY);
                  this.output = ItemStack.EMPTY;
               }

               if (!((ItemStack)this.items.get(2)).isEmpty()) {
                  player.getInventory().placeItemBackInInventory(((ItemStack)this.items.get(2)).copy());
                  this.level
                     .playSound(
                        null, this.worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.4F + 1.0F
                     );
                  this.items.set(2, ItemStack.EMPTY);
                  this.output = ItemStack.EMPTY;
               }

               if (!((ItemStack)this.items.get(3)).isEmpty()) {
                  player.getInventory().placeItemBackInInventory(((ItemStack)this.items.get(3)).copy());
                  this.level
                     .playSound(
                        null, this.worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.4F + 1.0F
                     );
                  this.items.set(3, ItemStack.EMPTY);
                  this.output = ItemStack.EMPTY;
               }

               if (!((ItemStack)this.items.get(4)).isEmpty()) {
                  player.getInventory().placeItemBackInInventory(((ItemStack)this.items.get(4)).copy());
                  this.level
                     .playSound(
                        null, this.worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.4F + 1.0F
                     );
                  this.items.set(4, ItemStack.EMPTY);
                  this.output = ItemStack.EMPTY;
               }

               this.setChanged();
            }
         }

         return 0;
      }
   }

   public void tick() {
      if (this.level instanceof ServerLevel) {
         this.craft();
      }

      if (this.crafting) {
         if (this.grindingTime <= 0) {
            new Random();
            if (this.level instanceof ServerLevel) {
               this.craftTheItem(this.output);
            }

            this.crafted = true;
            this.crafting = false;
            this.setChanged();
         } else {
            this.grindingTime--;
            Random rand = new Random();
            float craftPercent2 = (this.grindingTimeMax - this.grindingTime) / 100.0F;
            double pestleYOffset = Math.pow(Mth.sin(craftPercent2 * 3.14F * 5.0F - 1.2F), 4.0) / 4.0;
            if (pestleYOffset < 0.1 && this.level != null) {
               if (!this.grindSoundPlayed) {
                  this.level
                     .playSound(null, this.worldPosition, SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS, 0.1F, this.level.random.nextFloat() * 0.4F + 2.1F);
                  this.grindSoundPlayed = true;
               }

               if (!((ItemStack)this.items.get(0)).isEmpty() && rand.nextInt(4) == 0) {
                  this.level
                     .addParticle(
                        new ItemParticleOption(ParticleTypes.ITEM, (ItemStack)this.items.get(0)),
                        this.worldPosition.getX() + 0.45F + rand.nextFloat() * 0.1F,
                        this.worldPosition.getY() + 0.2,
                        this.worldPosition.getZ() + 0.45F + rand.nextFloat() * 0.1F,
                        (rand.nextDouble() - 0.5) / 15.0,
                        (rand.nextDouble() + 0.5) * 0.15,
                        (rand.nextDouble() - 0.5) / 15.0
                     );
               }

               if (!((ItemStack)this.items.get(1)).isEmpty() && rand.nextInt(4) == 0) {
                  this.level
                     .addParticle(
                        new ItemParticleOption(ParticleTypes.ITEM, (ItemStack)this.items.get(1)),
                        this.worldPosition.getX() + 0.45F + rand.nextFloat() * 0.1F,
                        this.worldPosition.getY() + 0.2,
                        this.worldPosition.getZ() + 0.45F + rand.nextFloat() * 0.1F,
                        (rand.nextDouble() - 0.5) / 15.0,
                        (rand.nextDouble() + 0.5) * 0.15,
                        (rand.nextDouble() - 0.5) / 15.0
                     );
               }

               if (!((ItemStack)this.items.get(2)).isEmpty() && rand.nextInt(4) == 0) {
                  this.level
                     .addParticle(
                        new ItemParticleOption(ParticleTypes.ITEM, (ItemStack)this.items.get(2)),
                        this.worldPosition.getX() + 0.45F + rand.nextFloat() * 0.1F,
                        this.worldPosition.getY() + 0.2,
                        this.worldPosition.getZ() + 0.45F + rand.nextFloat() * 0.1F,
                        (rand.nextDouble() - 0.5) / 15.0,
                        (rand.nextDouble() + 0.5) * 0.15,
                        (rand.nextDouble() - 0.5) / 15.0
                     );
               }

               if (!((ItemStack)this.items.get(3)).isEmpty() && rand.nextInt(4) == 0) {
                  this.level
                     .addParticle(
                        new ItemParticleOption(ParticleTypes.ITEM, (ItemStack)this.items.get(3)),
                        this.worldPosition.getX() + 0.45F + rand.nextFloat() * 0.1F,
                        this.worldPosition.getY() + 0.2,
                        this.worldPosition.getZ() + 0.45F + rand.nextFloat() * 0.1F,
                        (rand.nextDouble() - 0.5) / 15.0,
                        (rand.nextDouble() + 0.5) * 0.15,
                        (rand.nextDouble() - 0.5) / 15.0
                     );
               }

               if (!((ItemStack)this.items.get(4)).isEmpty() && rand.nextInt(4) == 0) {
                  this.level
                     .addParticle(
                        new ItemParticleOption(ParticleTypes.ITEM, (ItemStack)this.items.get(4)),
                        this.worldPosition.getX() + 0.45F + rand.nextFloat() * 0.1F,
                        this.worldPosition.getY() + 0.2,
                        this.worldPosition.getZ() + 0.45F + rand.nextFloat() * 0.1F,
                        (rand.nextDouble() - 0.5) / 15.0,
                        (rand.nextDouble() + 0.5) * 0.15,
                        (rand.nextDouble() - 0.5) / 15.0
                     );
               }
            } else {
               this.grindSoundPlayed = false;
            }
         }
      }
   }

   public int[] getSlotsForFace(Direction direction) {
      return direction == Direction.DOWN ? new int[]{5} : new int[]{0, 1, 2, 3, 4};
   }

   public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, @Nullable Direction direction) {
      return index != 5 && this.canPlaceItem(index, itemStackIn);
   }

   public boolean canPlaceItem(int index, ItemStack stack) {
      return ((ItemStack)this.items.get(index)).isEmpty();
   }

   public boolean canTakeItemThroughFace(int index, ItemStack p_19240_, Direction p_19241_) {
      return index == 5;
   }

   public int getContainerSize() {
      return this.items.size();
   }
}
