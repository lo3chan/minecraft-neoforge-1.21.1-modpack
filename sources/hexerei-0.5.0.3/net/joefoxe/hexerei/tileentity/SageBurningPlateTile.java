package net.joefoxe.hexerei.tileentity;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.custom.SageBurningPlate;
import net.joefoxe.hexerei.config.HexConfig;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.particle.ModParticleTypes;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.message.EmitExtinguishParticlesPacket;
import net.joefoxe.hexerei.util.message.TESyncPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class SageBurningPlateTile extends RandomizableContainerBlockEntity implements WorldlyContainer, Clearable, MenuProvider {
   protected NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
   public int burnTimeMax = 20;
   public int burnTime = 20;

   public SageBurningPlateTile(BlockEntityType<?> tileEntityTypeIn, BlockPos blockPos, BlockState blockState) {
      super(tileEntityTypeIn, blockPos, blockState);
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
   }

   public void sync() {
      this.setChanged();
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

   public void onLoad() {
      if (Hexerei.sageBurningPlateTileList.contains(this.worldPosition)) {
         Hexerei.sageBurningPlateTileList.remove(this.worldPosition);
      }

      Hexerei.sageBurningPlateTileList.add(this.worldPosition);
      super.onLoad();
   }

   public SageBurningPlateTile(BlockPos blockPos, BlockState blockState) {
      this((BlockEntityType<?>)ModTileEntities.SAGE_BURNING_PLATE_TILE.get(), blockPos, blockState);
   }

   public void setItem(int index, ItemStack stack) {
      if (index >= 0 && index < this.items.size()) {
         ItemStack itemStack = stack.copy();
         this.items.set(index, itemStack);
         this.burnTime = this.burnTimeMax;
      }

      this.sync();
   }

   public ItemStack removeItem(int index, int p_59614_) {
      this.unpackLootTable(null);
      if ((Boolean)this.level.getBlockState(this.worldPosition).getValue(BlockStateProperties.LIT)) {
         Random random = new Random();
         this.level.setBlock(this.worldPosition, (BlockState)this.level.getBlockState(this.worldPosition).setValue(BlockStateProperties.LIT, false), 11);
         this.level.playSound(null, this.worldPosition, SoundEvents.CANDLE_EXTINGUISH, SoundSource.PLAYERS, 1.0F, random.nextFloat() * 0.4F + 1.0F);
         this.sync();
         if (!this.level.isClientSide) {
            HexereiPacketHandler.sendToNearbyClient(this.level, this.worldPosition, new EmitExtinguishParticlesPacket(this.worldPosition));
         }
      }

      ItemStack itemstack = ContainerHelper.removeItem(this.getItems(), index, p_59614_);
      this.sync();
      return itemstack;
   }

   protected void loadAdditional(CompoundTag nbt, Provider registries) {
      this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      if (!this.tryLoadLootTable(nbt)) {
         ContainerHelper.loadAllItems(nbt, this.items, registries);
      }

      if (nbt.contains("burnTime", 3)) {
         this.burnTime = nbt.getInt("burnTime");
      }

      if (nbt.contains("burnTimeMax", 3)) {
         this.burnTimeMax = nbt.getInt("burnTimeMax");
      }

      super.loadAdditional(nbt, registries);
   }

   protected Component getDefaultName() {
      return Component.translatable("container.hexerei.sage_burning_plate");
   }

   protected AbstractContainerMenu createMenu(int p_58627_, Inventory p_58628_) {
      return null;
   }

   public void saveAdditional(CompoundTag compound, Provider registries) {
      ContainerHelper.saveAllItems(compound, this.items, registries);
      compound.putInt("burnTime", this.burnTime);
      compound.putInt("burnTimeMax", this.burnTimeMax);
   }

   public CompoundTag save(CompoundTag compound, Provider registries) {
      super.saveAdditional(compound, registries);
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
      if (this.level == null) {
         return 0;
      } else {
         ItemStack stack1 = stack.copy();
         Random rand = new Random();
         if (((ItemStack)this.items.get(slot)).isEmpty() && this.canPlaceItem(slot, stack)) {
            stack1.setCount(1);
            this.items.set(slot, stack1);
            this.burnTime = this.burnTimeMax;
            this.sync();
            stack.shrink(1);
            this.level.playSound(null, this.worldPosition, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, rand.nextFloat() * 0.4F + 1.0F);
            return 1;
         } else {
            return 0;
         }
      }
   }

   public int interactSageBurningPlate(Player player, BlockHitResult hit) {
      if (!player.isShiftKeyDown()) {
         if (!player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
            new Random();
            if (((ItemStack)this.items.get(0)).isEmpty()) {
               this.putItems(0, player.getItemInHand(InteractionHand.MAIN_HAND));
               return 1;
            }
         }
      } else if (!((ItemStack)this.items.get(0)).isEmpty()) {
         player.getInventory().placeItemBackInInventory(((ItemStack)this.items.get(0)).copy());
         this.level
            .playSound(null, this.worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.4F + 1.0F);
         this.removeItem(0, 1);
      }

      return 0;
   }

   public void extinguishParticles() {
      Random rand = new Random();
      if (this.level != null) {
         float offsetX = 0.0F;
         float offsetZ = 0.0F;
         float damageOutOf5 = (float)(((ItemStack)this.getItems().get(0)).getMaxDamage() - ((ItemStack)this.getItems().get(0)).getDamageValue())
            / ((ItemStack)this.getItems().get(0)).getMaxDamage()
            * 5.0F;
         switch ((Direction)this.getBlockState().getValue(HorizontalDirectionalBlock.FACING)) {
            case NORTH:
               offsetX = -0.25F;
               if (damageOutOf5 <= 4.0F && damageOutOf5 > 3.0F) {
                  offsetX = (float)(offsetX + 0.09);
               }

               if (damageOutOf5 <= 3.0F && damageOutOf5 > 2.0F) {
                  offsetX = (float)(offsetX + 0.18);
               }

               if (damageOutOf5 <= 2.0F && damageOutOf5 > 1.0F) {
                  offsetX = (float)(offsetX + 0.25);
               }

               if (damageOutOf5 <= 1.0F && damageOutOf5 >= 0.0F) {
                  offsetX = (float)(offsetX + 0.33);
               }
               break;
            case SOUTH:
               offsetX = 0.25F;
               if (damageOutOf5 <= 4.0F && damageOutOf5 > 3.0F) {
                  offsetX = (float)(offsetX - 0.09);
               }

               if (damageOutOf5 <= 3.0F && damageOutOf5 > 2.0F) {
                  offsetX = (float)(offsetX - 0.18);
               }

               if (damageOutOf5 <= 2.0F && damageOutOf5 > 1.0F) {
                  offsetX = (float)(offsetX - 0.25);
               }

               if (damageOutOf5 <= 1.0F && damageOutOf5 >= 0.0F) {
                  offsetX = (float)(offsetX - 0.33);
               }
               break;
            case WEST:
               offsetZ = 0.25F;
               if (damageOutOf5 <= 4.0F && damageOutOf5 > 3.0F) {
                  offsetZ = (float)(offsetZ - 0.09);
               }

               if (damageOutOf5 <= 3.0F && damageOutOf5 > 2.0F) {
                  offsetZ = (float)(offsetZ - 0.18);
               }

               if (damageOutOf5 <= 2.0F && damageOutOf5 > 1.0F) {
                  offsetZ = (float)(offsetZ - 0.25);
               }

               if (damageOutOf5 <= 1.0F && damageOutOf5 >= 0.0F) {
                  offsetZ = (float)(offsetZ - 0.33);
               }
               break;
            case EAST:
               offsetZ = -0.25F;
               if (damageOutOf5 <= 4.0F && damageOutOf5 > 3.0F) {
                  offsetZ = (float)(offsetZ + 0.09);
               }

               if (damageOutOf5 <= 3.0F && damageOutOf5 > 2.0F) {
                  offsetZ = (float)(offsetZ + 0.18);
               }

               if (damageOutOf5 <= 2.0F && damageOutOf5 > 1.0F) {
                  offsetZ = (float)(offsetZ + 0.25);
               }

               if (damageOutOf5 <= 1.0F && damageOutOf5 >= 0.0F) {
                  offsetZ = (float)(offsetZ + 0.33);
               }
         }

         this.level
            .addParticle(
               ParticleTypes.LARGE_SMOKE,
               this.worldPosition.getX() + 0.5F + offsetX,
               this.worldPosition.getY() + 0.25F,
               this.worldPosition.getZ() + 0.5F + offsetZ,
               (rand.nextDouble() - 0.5) / 50.0,
               (rand.nextDouble() + 0.5) * 0.045,
               (rand.nextDouble() - 0.5) / 50.0
            );
         this.level
            .addParticle(
               ParticleTypes.CAMPFIRE_COSY_SMOKE,
               this.worldPosition.getX() + 0.5F + offsetX,
               this.worldPosition.getY() + 0.25F,
               this.worldPosition.getZ() + 0.5F + offsetZ,
               (rand.nextDouble() - 0.5) / 50.0,
               (rand.nextDouble() + 0.5) * 0.045,
               (rand.nextDouble() - 0.5) / 50.0
            );
         this.level
            .addParticle(
               ParticleTypes.SMOKE,
               this.worldPosition.getX() + 0.5F + offsetX,
               this.worldPosition.getY() + 0.25F,
               this.worldPosition.getZ() + 0.5F + offsetZ,
               (rand.nextDouble() - 0.5) / 50.0,
               (rand.nextDouble() + 0.5) * 0.045,
               (rand.nextDouble() - 0.5) / 50.0
            );
         this.level
            .addParticle(
               ParticleTypes.SMOKE,
               this.worldPosition.getX() + 0.5F + offsetX,
               this.worldPosition.getY() + 0.25F,
               this.worldPosition.getZ() + 0.5F + offsetZ,
               (rand.nextDouble() - 0.5) / 50.0,
               (rand.nextDouble() + 0.5) * 0.045,
               (rand.nextDouble() - 0.5) / 50.0
            );
         this.level
            .addParticle(
               ParticleTypes.SMOKE,
               this.worldPosition.getX() + 0.5F + offsetX,
               this.worldPosition.getY() + 0.25F,
               this.worldPosition.getZ() + 0.5F + offsetZ,
               (rand.nextDouble() - 0.5) / 50.0,
               (rand.nextDouble() + 0.5) * 0.045,
               (rand.nextDouble() - 0.5) / 50.0
            );
      }
   }

   public void emitParticles() {
      Random rand = new Random();
      if (this.level != null) {
         if (rand.nextInt(4) == 0 && this.level.isClientSide) {
            float offsetX = 0.0F;
            float offsetZ = 0.0F;
            float damageOutOf5 = (float)(((ItemStack)this.getItems().get(0)).getMaxDamage() - ((ItemStack)this.getItems().get(0)).getDamageValue())
               / ((ItemStack)this.getItems().get(0)).getMaxDamage()
               * 5.0F;
            switch ((Direction)this.getBlockState().getValue(HorizontalDirectionalBlock.FACING)) {
               case NORTH:
                  offsetX = -0.25F;
                  if (damageOutOf5 <= 4.0F && damageOutOf5 > 3.0F) {
                     offsetX = (float)(offsetX + 0.09);
                  }

                  if (damageOutOf5 <= 3.0F && damageOutOf5 > 2.0F) {
                     offsetX = (float)(offsetX + 0.18);
                  }

                  if (damageOutOf5 <= 2.0F && damageOutOf5 > 1.0F) {
                     offsetX = (float)(offsetX + 0.25);
                  }

                  if (damageOutOf5 <= 1.0F && damageOutOf5 > 0.0F) {
                     offsetX = (float)(offsetX + 0.33);
                  }
                  break;
               case SOUTH:
                  offsetX = 0.25F;
                  if (damageOutOf5 <= 4.0F && damageOutOf5 > 3.0F) {
                     offsetX = (float)(offsetX - 0.09);
                  }

                  if (damageOutOf5 <= 3.0F && damageOutOf5 > 2.0F) {
                     offsetX = (float)(offsetX - 0.18);
                  }

                  if (damageOutOf5 <= 2.0F && damageOutOf5 > 1.0F) {
                     offsetX = (float)(offsetX - 0.25);
                  }

                  if (damageOutOf5 <= 1.0F && damageOutOf5 > 0.0F) {
                     offsetX = (float)(offsetX - 0.33);
                  }
                  break;
               case WEST:
                  offsetZ = 0.25F;
                  if (damageOutOf5 <= 4.0F && damageOutOf5 > 3.0F) {
                     offsetZ = (float)(offsetZ - 0.09);
                  }

                  if (damageOutOf5 <= 3.0F && damageOutOf5 > 2.0F) {
                     offsetZ = (float)(offsetZ - 0.18);
                  }

                  if (damageOutOf5 <= 2.0F && damageOutOf5 > 1.0F) {
                     offsetZ = (float)(offsetZ - 0.25);
                  }

                  if (damageOutOf5 <= 1.0F && damageOutOf5 > 0.0F) {
                     offsetZ = (float)(offsetZ - 0.33);
                  }
                  break;
               case EAST:
                  offsetZ = -0.25F;
                  if (damageOutOf5 <= 4.0F && damageOutOf5 > 3.0F) {
                     offsetZ = (float)(offsetZ + 0.09);
                  }

                  if (damageOutOf5 <= 3.0F && damageOutOf5 > 2.0F) {
                     offsetZ = (float)(offsetZ + 0.18);
                  }

                  if (damageOutOf5 <= 2.0F && damageOutOf5 > 1.0F) {
                     offsetZ = (float)(offsetZ + 0.25);
                  }

                  if (damageOutOf5 <= 1.0F && damageOutOf5 > 0.0F) {
                     offsetZ = (float)(offsetZ + 0.33);
                  }
            }

            this.level
               .addParticle(
                  ParticleTypes.SMOKE,
                  this.worldPosition.getX() + 0.5F + offsetX,
                  this.worldPosition.getY() + 0.25F,
                  this.worldPosition.getZ() + 0.5F + offsetZ,
                  (rand.nextDouble() - 0.5) / 50.0,
                  (rand.nextDouble() + 0.5) * 0.045,
                  (rand.nextDouble() - 0.5) / 50.0
               );
            if (rand.nextInt(10) == 0) {
               this.level
                  .addParticle(
                     ParticleTypes.CAMPFIRE_COSY_SMOKE,
                     this.worldPosition.getX() + 0.5F + offsetX,
                     this.worldPosition.getY() + 0.25F,
                     this.worldPosition.getZ() + 0.5F + offsetZ,
                     (rand.nextDouble() - 0.5) / 50.0,
                     (rand.nextDouble() + 0.5) * 0.045,
                     (rand.nextDouble() - 0.5) / 50.0
                  );
            }
         }

         if ((Integer)this.getBlockState().getValue(SageBurningPlate.MODE) != 3) {
            for (int i = 0; i < 360; i++) {
               Vec3 vec = new Vec3(
                  Mth.sin(i / 360.0F * 6.2831855F) * (rand.nextFloat() * ((Integer)HexConfig.SAGE_BURNING_PLATE_RANGE.get()).intValue()),
                  Mth.sin(rand.nextInt(360) / 360.0F * 6.2831855F) * (rand.nextFloat() * ((Integer)HexConfig.SAGE_BURNING_PLATE_RANGE.get()).intValue()),
                  Mth.cos(i / 360.0F * 6.2831855F) * (rand.nextFloat() * ((Integer)HexConfig.SAGE_BURNING_PLATE_RANGE.get()).intValue())
               );
               Vec3 vec2 = new Vec3(
                  Mth.sin(i / 360.0F * 6.2831855F) * ((Integer)HexConfig.SAGE_BURNING_PLATE_RANGE.get()).intValue(),
                  0.0,
                  Mth.cos(i / 360.0F * 6.2831855F) * ((Integer)HexConfig.SAGE_BURNING_PLATE_RANGE.get()).intValue()
               );
               BlockPos pos2 = BlockPos.containing(
                  this.worldPosition.getX() + (int)(0.5 + vec2.x()),
                  this.worldPosition.getY() + (int)(0.25 + vec2.y()),
                  this.worldPosition.getZ() + (int)(0.5 + vec2.z())
               );
               if (rand.nextInt(40) == 0
                  && ((Integer)this.getBlockState().getValue(SageBurningPlate.MODE) == 0 || (Integer)this.getBlockState().getValue(SageBurningPlate.MODE) == 1)
                  )
                {
                  BlockPos pos = new BlockPos(
                     this.worldPosition.getX() + (int)(0.5 + vec.x()),
                     this.worldPosition.getY() + (int)(0.25 + vec.y()),
                     this.worldPosition.getZ() + (int)(0.5 + vec.z())
                  );
                  if ((!this.level.getBlockState(pos.below()).isAir() || !this.level.getBlockState(pos.below().below()).isAir())
                     && this.level.getBlockState(pos).isAir()) {
                     this.level
                        .addParticle(
                           (ParticleOptions)ModParticleTypes.FOG.get(),
                           true,
                           pos.getX(),
                           pos.getY(),
                           pos.getZ(),
                           (rand.nextDouble() - 0.5) / 15.0,
                           (rand.nextDouble() + 0.5) * 0.015,
                           (rand.nextDouble() - 0.5) / 15.0
                        );
                  }
               }

               if (rand.nextInt(160) == 0
                  && ((Integer)this.getBlockState().getValue(SageBurningPlate.MODE) == 1 || (Integer)this.getBlockState().getValue(SageBurningPlate.MODE) == 2)
                  )
                {
                  this.level
                     .addParticle(
                        (ParticleOptions)ModParticleTypes.FOG.get(),
                        true,
                        pos2.getX(),
                        pos2.getY(),
                        pos2.getZ(),
                        (rand.nextDouble() - 0.5) / 15.0,
                        (rand.nextDouble() + 0.5) * 0.015,
                        (rand.nextDouble() - 0.5) / 15.0
                     );
               }
            }
         }
      }
   }

   public void tick() {
      if ((Boolean)this.getBlockState().getValue(SageBurningPlate.LIT)) {
         if (this.burnTime <= 0) {
            if (!this.level.isClientSide) {
               ((ItemStack)this.items.getFirst()).setDamageValue(((ItemStack)this.items.getFirst()).getDamageValue() + 1);
               if (((ItemStack)this.items.getFirst()).getDamageValue() >= ((ItemStack)this.items.getFirst()).getMaxDamage()) {
                  this.removeItem(0, 1);
               } else {
                  this.sync();
               }

               this.burnTime = this.burnTimeMax;
            }
         } else {
            this.burnTime--;
         }

         if (this.level.isClientSide) {
            this.emitParticles();
         }
      }
   }

   public int[] getSlotsForFace(Direction p_19238_) {
      return new int[]{0};
   }

   public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, @Nullable Direction direction) {
      return this.canPlaceItem(index, itemStackIn);
   }

   public boolean canPlaceItem(int index, ItemStack stack) {
      return ((ItemStack)this.items.get(index)).isEmpty() && stack.is((Item)ModItems.DRIED_SAGE_BUNDLE.get());
   }

   public boolean canTakeItemThroughFace(int index, ItemStack p_19240_, Direction p_19241_) {
      return true;
   }

   public int getContainerSize() {
      return this.items.size();
   }
}
