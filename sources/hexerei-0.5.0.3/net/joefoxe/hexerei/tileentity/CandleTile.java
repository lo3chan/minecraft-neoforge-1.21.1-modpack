package net.joefoxe.hexerei.tileentity;

import java.util.Random;
import javax.annotation.Nullable;
import net.joefoxe.hexerei.block.custom.Candle;
import net.joefoxe.hexerei.data.candle.CandleData;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;

public class CandleTile extends BlockEntity {
   public NonNullList<CandleData> candles = NonNullList.withSize(4, new CandleData());
   public boolean litStateOld;
   public int redstoneAnalogSignal;
   public int redstoneBases;
   private boolean startupFlag;
   public Component customName;
   public int tickCount = 0;

   public CandleTile(BlockEntityType<?> tileEntityTypeIn, BlockPos blockPos, BlockState blockState) {
      super(tileEntityTypeIn, blockPos, blockState);
      this.candles.replaceAll(ignored -> new CandleData());
      this.startupFlag = false;
      this.litStateOld = false;
   }

   public CandleTile(BlockPos blockPos, BlockState blockState) {
      this((BlockEntityType<?>)ModTileEntities.CANDLE_TILE.get(), blockPos, blockState);
   }

   protected void loadAdditional(CompoundTag tag, Provider registries) {
      if (tag.contains("candle0", 10)) {
         if (tag.contains("candle0")) {
            ((CandleData)this.candles.get(0)).load(tag.getCompound("candle0"), registries);
         }

         if (tag.contains("candle1")) {
            ((CandleData)this.candles.get(1)).load(tag.getCompound("candle1"), registries);
         }

         if (tag.contains("candle2")) {
            ((CandleData)this.candles.get(2)).load(tag.getCompound("candle2"), registries);
         }

         if (tag.contains("candle3")) {
            ((CandleData)this.candles.get(3)).load(tag.getCompound("candle3"), registries);
         }
      }

      this.setOffsetPos(true);
      super.loadAdditional(tag, registries);
   }

   public void saveAdditional(CompoundTag compound, Provider registries) {
      super.saveAdditional(compound, registries);
      compound.putInt("effectCooldown", ((CandleData)this.candles.get(0)).cooldown);
      compound.put("candle0", ((CandleData)this.candles.get(0)).save(registries));
      compound.put("candle1", ((CandleData)this.candles.get(1)).save(registries));
      compound.put("candle2", ((CandleData)this.candles.get(2)).save(registries));
      compound.put("candle3", ((CandleData)this.candles.get(3)).save(registries));
   }

   public int getNumberOfCandles() {
      int num = 0;

      for (CandleData candleData : this.candles) {
         if (candleData.hasCandle) {
            num++;
         }
      }

      return num;
   }

   public Component getCustomName() {
      return this.getCustomName(0);
   }

   public Component getCustomName(int slot) {
      return ((CandleData)this.candles.get(slot)).customName;
   }

   public int getDyeColor(int slot) {
      return ((CandleData)this.candles.get(slot)).dyeColor;
   }

   public int getDyeColor() {
      return this.getDyeColor(0);
   }

   public boolean hasCustomName() {
      return this.customName != null;
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

   public static double getDistanceToEntity(Entity entity, BlockPos pos) {
      double deltaX = entity.getX() - pos.getX();
      double deltaY = entity.getY() - pos.getY();
      double deltaZ = entity.getZ() - pos.getZ();
      return Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
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

   public void setDyeColor(int dyeColor) {
      ((CandleData)this.candles.get(0)).dyeColor = dyeColor;
   }

   public void setHeight(int height) {
      ((CandleData)this.candles.get(0)).height = height;
   }

   public void setDyeColor(int candle, int dyeColor) {
      ((CandleData)this.candles.get(Math.max(0, Math.min(candle, 3)))).dyeColor = dyeColor;
   }

   public void setHeight(int candle, int height) {
      ((CandleData)this.candles.get(Math.max(0, Math.min(candle, 3)))).height = height;
   }

   public void setChanged() {
      super.setChanged();
      this.sync();
   }

   public int updateAnalog() {
      int temp = 0;
      int level_of_candles = 0;

      for (int i = 0; i < 4; i++) {
         if (((CandleData)this.candles.get(i)).hasCandle) {
            level_of_candles += ((CandleData)this.candles.get(i)).height;
         }
      }

      float candles = level_of_candles;
      float max = 28.0F;
      float percent = candles / max;
      temp += (int)Math.ceil(percent * 15.0F);
      if (this.redstoneAnalogSignal != temp) {
         this.redstoneAnalogSignal = temp;

         for (Direction direction : Direction.values()) {
            this.level.updateNeighborsAt(this.getBlockPos().relative(direction), this.getBlockState().getBlock());
         }
      }

      if (this.level != null) {
         this.level.sendBlockUpdated(this.worldPosition, this.level.getBlockState(this.worldPosition), this.level.getBlockState(this.worldPosition), 2);
      }

      return temp;
   }

   public void entityInside(Entity entity) {
      BlockPos blockpos = this.getBlockPos();
      if (entity instanceof Projectile projectile
         && Shapes.joinIsNotEmpty(
            Shapes.create(entity.getBoundingBox().move(-blockpos.getX(), -blockpos.getY(), -blockpos.getZ())),
            Candle.getShape(this.getBlockState()),
            BooleanOp.AND
         )
         && projectile.isOnFire()
         && this.level != null) {
         if (((CandleData)this.candles.get(0)).hasCandle) {
            ((CandleData)this.candles.get(0)).lit = true;
         }

         if (((CandleData)this.candles.get(1)).hasCandle) {
            ((CandleData)this.candles.get(1)).lit = true;
         }

         if (((CandleData)this.candles.get(2)).hasCandle) {
            ((CandleData)this.candles.get(2)).lit = true;
         }

         if (((CandleData)this.candles.get(3)).hasCandle) {
            ((CandleData)this.candles.get(3)).lit = true;
         }
      }
   }

   public void tick() {
      Random random = new Random();
      int candlesLit = 0;
      this.tickCount++;

      for (CandleData candleData : this.candles) {
         candleData.setOldPos();
         candleData.move();
         if (candleData.getEffect() != null) {
            candleData.getEffect().tick(this.level, this, candleData);
         }

         if (candleData.lit) {
            candlesLit++;
         }
      }

      BlockState state = this.level.getBlockState(this.worldPosition);
      if (state.hasProperty(Candle.CANDLES_LIT)) {
         this.level
            .setBlock(this.worldPosition, (BlockState)((BlockState)state.setValue(Candle.CANDLES_LIT, candlesLit)).setValue(Candle.LIT, candlesLit > 0), 3);
      }

      int temp = 0;
      int level_of_candles = 0;

      for (int i = 0; i < 4; i++) {
         if (((CandleData)this.candles.get(i)).hasCandle) {
            level_of_candles += ((CandleData)this.candles.get(i)).height;
         }
      }

      float candles = level_of_candles;
      float max = 28.0F;
      float percent = candles / max;
      temp += (int)Math.ceil(percent * 15.0F);
      if (this.redstoneAnalogSignal != temp) {
         this.redstoneAnalogSignal = temp;

         for (Direction direction : Direction.values()) {
            this.level.updateNeighborsAt(this.getBlockPos().relative(direction), this.getBlockState().getBlock());
         }
      }

      temp = 0;

      for (int ix = 0; ix < 4; ix++) {
         if (((CandleData)this.candles.get(ix)).base.layer != null
            && ((CandleData)this.candles.get(ix)).base.layer.toString().equals("minecraft:redstone_block")) {
            temp++;
         }
      }

      if (this.redstoneBases != temp) {
         this.redstoneBases = temp;
         float percentx = temp / 4.0F;
         int redstoneValue = (int)Math.ceil(percentx * 15.0F);
         this.level.setBlock(this.worldPosition, (BlockState)state.setValue(Candle.POWER, redstoneValue), 3);

         for (Direction direction : Direction.values()) {
            this.level.updateNeighborsAt(this.getBlockPos().relative(direction), this.getBlockState().getBlock());
         }
      }

      if (!this.startupFlag) {
         if (!this.getBlockState().getBlock().asItem().equals(ModItems.CANDLE.get())) {
            ((CandleData)this.candles.get(0)).height = 7;
            ((CandleData)this.candles.get(0)).hasCandle = true;
         }

         ((CandleData)this.candles.get(0)).hasCandle = true;
         ((CandleData)this.candles.get(0)).meltTimer = CandleData.meltTimerMAX;
         this.startupFlag = true;

         for (int ixx = 0; ixx < 4; ixx++) {
            CandleData candleData = (CandleData)this.candles.get(ixx);
            if (candleData.returnToBlock) {
               candleData.moveInstantlyToTarget();
            }
         }
      }

      this.setOffsetPos();
      if (this.level.isClientSide) {
         for (CandleData candleData : this.candles) {
            if (candleData.hasCandle && candleData.lit) {
               if (random.nextInt(40) == 0 && candleData.getEffect() != null && candleData.getEffect().getParticleType() != null) {
                  this.level
                     .addParticle(
                        (ParticleOptions)(candleData.getEffect().getParticleType() != null ? candleData.getEffect().getParticleType() : ParticleTypes.FLAME),
                        this.worldPosition.getX() + 0.5F + candleData.x,
                        this.worldPosition.getY() + 0.1875F + candleData.height / 16.0F + candleData.y + candleData.baseHeight / 16.0F,
                        this.worldPosition.getZ() + 0.5F + candleData.z,
                        (random.nextDouble() - 0.5) / 100.0,
                        (random.nextDouble() + 0.5) * 0.015,
                        (random.nextDouble() - 0.5) / 100.0
                     );
               }

               if (random.nextInt(10) == 0) {
                  this.level
                     .addParticle(
                        ParticleTypes.FLAME,
                        this.worldPosition.getX() + 0.5F + candleData.x,
                        this.worldPosition.getY() + 0.1875F + candleData.height / 16.0F + candleData.y + candleData.baseHeight / 16.0F,
                        this.worldPosition.getZ() + 0.5F + candleData.z,
                        (random.nextDouble() - 0.5) / 50.0,
                        (random.nextDouble() + 0.5) * 0.015,
                        (random.nextDouble() - 0.5) / 50.0
                     );
               }

               if (random.nextInt(10) == 0) {
                  this.level
                     .addParticle(
                        ParticleTypes.SMOKE,
                        this.worldPosition.getX() + 0.5F + candleData.x,
                        this.worldPosition.getY() + 0.1875F + candleData.height / 16.0F + candleData.y + candleData.baseHeight / 16.0F,
                        this.worldPosition.getZ() + 0.5F + candleData.z,
                        (random.nextDouble() - 0.5) / 50.0,
                        (random.nextDouble() + 0.5) * 0.045,
                        (random.nextDouble() - 0.5) / 50.0
                     );
               }
            }
         }
      } else {
         boolean shouldSync = false;

         for (CandleData candleDatax : this.candles) {
            if (candleDatax.hasCandle && candleDatax.lit) {
               candleDatax.meltTimer = candleDatax.meltTimer - candleDatax.getMeltingSpeedMultiplier();
               if (candleDatax.meltTimer <= 0.0F) {
                  candleDatax.meltTimer = CandleData.meltTimerMAX;
                  candleDatax.height--;
                  if (candleDatax.height <= 0) {
                     candleDatax.hasCandle = false;
                     this.updateCandleSlots();
                     BlockState blockstate = this.getLevel().getBlockState(this.getBlockPos());
                     if (!this.level.isClientSide()) {
                        this.getLevel()
                           .setBlock(
                              this.getBlockPos(),
                              (BlockState)this.getBlockState().setValue(Candle.CANDLES, Math.max(1, (Integer)blockstate.getValue(Candle.CANDLES) - 1)),
                              1
                           );
                     }

                     this.level.playSound(null, this.worldPosition, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 1.0F);
                  }

                  shouldSync = true;
               }
            }
         }

         if (shouldSync) {
            this.sync();
         }
      }

      for (CandleData candleDataxx : this.candles) {
         if (this.tickCount - candleDataxx.returnToBlockLastTick > 10) {
            candleDataxx.returnToBlock = true;
         }
      }

      if (this.candles.stream().allMatch(candleDataxxx -> !candleDataxxx.hasCandle) && this.getLevel() != null) {
         this.getLevel().destroyBlock(this.getBlockPos(), false);
      }

      this.litStateOld = (Boolean)this.getBlockState().getValue(Candle.LIT);
   }

   public void setOffsetPos(int index) {
      switch (index) {
         case 0:
            if (((CandleData)this.candles.get(0)).hasCandle) {
               float xOffset = 0.0F;
               float zOffset = 0.0F;
               if (this.getNumberOfCandles() == 4) {
                  if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
                     xOffset = 0.1875F;
                     zOffset = 0.1875F;
                  }

                  if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
                     xOffset = -0.125F;
                     zOffset = -0.125F;
                  }

                  if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
                     xOffset = -0.125F;
                     zOffset = 0.1875F;
                  }

                  if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
                     xOffset = 0.125F;
                     zOffset = -0.1875F;
                  }
               } else if (this.getNumberOfCandles() == 3) {
                  if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
                     xOffset = -0.0625F;
                     zOffset = 0.1875F;
                  }

                  if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
                     xOffset = 0.0625F;
                     zOffset = -0.1875F;
                  }

                  if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
                     xOffset = -0.1875F;
                     zOffset = -0.0625F;
                  }

                  if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
                     xOffset = 0.1875F;
                     zOffset = 0.0625F;
                  }
               } else if (this.getNumberOfCandles() == 2) {
                  if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
                     xOffset = 0.1875F;
                     zOffset = -0.125F;
                  }

                  if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
                     xOffset = -0.1875F;
                     zOffset = 0.125F;
                  }

                  if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
                     xOffset = 0.125F;
                     zOffset = 0.1875F;
                  }

                  if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
                     xOffset = -0.125F;
                     zOffset = -0.1875F;
                  }
               } else if (this.getNumberOfCandles() == 1) {
                  xOffset = 0.0F;
                  zOffset = 0.0F;
               }

               ((CandleData)this.candles.get(0)).xTarget = xOffset;
               ((CandleData)this.candles.get(0)).yTarget = 0.0F;
               ((CandleData)this.candles.get(0)).zTarget = zOffset;
            }
         case 1:
            if (((CandleData)this.candles.get(1)).hasCandle) {
               float xOffset = 0.0F;
               float zOffset = 0.0F;
               if (this.getNumberOfCandles() == 4) {
                  if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
                     xOffset = -0.125F;
                     zOffset = -0.1875F;
                  }

                  if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
                     xOffset = 0.1875F;
                     zOffset = 0.1875F;
                  }

                  if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
                     xOffset = 0.1875F;
                     zOffset = -0.125F;
                  }

                  if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
                     xOffset = -0.1875F;
                     zOffset = 0.125F;
                  }
               } else if (this.getNumberOfCandles() == 3) {
                  if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
                     xOffset = 0.1875F;
                     zOffset = 0.0625F;
                  }

                  if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
                     xOffset = -0.1875F;
                     zOffset = -0.0625F;
                  }

                  if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
                     xOffset = -0.0625F;
                     zOffset = 0.1875F;
                  }

                  if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
                     xOffset = 0.0625F;
                     zOffset = -0.1875F;
                  }
               } else if (this.getNumberOfCandles() == 2) {
                  if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
                     xOffset = -0.1875F;
                     zOffset = 0.1875F;
                  }

                  if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
                     xOffset = 0.1875F;
                     zOffset = -0.1875F;
                  }

                  if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
                     xOffset = -0.1875F;
                     zOffset = -0.1875F;
                  }

                  if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
                     xOffset = 0.1875F;
                     zOffset = 0.1875F;
                  }
               } else if (this.getNumberOfCandles() == 1) {
                  xOffset = 0.0F;
                  zOffset = 0.0F;
               }

               ((CandleData)this.candles.get(1)).xTarget = xOffset;
               ((CandleData)this.candles.get(1)).yTarget = 0.0F;
               ((CandleData)this.candles.get(1)).zTarget = zOffset;
            }
         case 2:
            if (((CandleData)this.candles.get(2)).hasCandle) {
               float xOffset = 0.0F;
               float zOffset = 0.0F;
               if (this.getNumberOfCandles() == 4) {
                  if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
                     xOffset = -0.125F;
                     zOffset = 0.125F;
                  }

                  if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
                     xOffset = 0.1875F;
                     zOffset = -0.125F;
                  }

                  if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
                     xOffset = 0.125F;
                     zOffset = 0.125F;
                  }

                  if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
                     xOffset = -0.125F;
                     zOffset = -0.125F;
                  }
               } else if (this.getNumberOfCandles() == 3) {
                  if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
                     xOffset = -0.125F;
                     zOffset = -0.1875F;
                  }

                  if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
                     xOffset = 0.125F;
                     zOffset = 0.1875F;
                  }

                  if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
                     xOffset = 0.1875F;
                     zOffset = -0.125F;
                  }

                  if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
                     xOffset = -0.1875F;
                     zOffset = 0.125F;
                  }
               }

               ((CandleData)this.candles.get(2)).xTarget = xOffset;
               ((CandleData)this.candles.get(2)).yTarget = 0.0F;
               ((CandleData)this.candles.get(2)).zTarget = zOffset;
            }
         case 3:
            if (((CandleData)this.candles.get(3)).hasCandle) {
               float xOffset = 0.0F;
               float zOffset = 0.0F;
               if (this.getNumberOfCandles() == 4) {
                  if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
                     xOffset = 0.1875F;
                     zOffset = -0.125F;
                  }

                  if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
                     xOffset = -0.1875F;
                     zOffset = 0.125F;
                  }

                  if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
                     xOffset = -0.125F;
                     zOffset = -0.1875F;
                  }

                  if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
                     xOffset = 0.125F;
                     zOffset = 0.1875F;
                  }
               }

               ((CandleData)this.candles.get(3)).xTarget = xOffset;
               ((CandleData)this.candles.get(3)).yTarget = 0.0F;
               ((CandleData)this.candles.get(3)).zTarget = zOffset;
            }
      }
   }

   public void setOffsetPos() {
      this.setOffsetPos(false);
   }

   public void setOffsetPos(boolean force) {
      if (((CandleData)this.candles.get(0)).hasCandle) {
         float xOffset = 0.0F;
         float zOffset = 0.0F;
         if (this.getNumberOfCandles() == 4) {
            if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
               xOffset = 0.1875F;
               zOffset = 0.1875F;
            }

            if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
               xOffset = -0.125F;
               zOffset = -0.125F;
            }

            if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
               xOffset = -0.125F;
               zOffset = 0.1875F;
            }

            if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
               xOffset = 0.125F;
               zOffset = -0.1875F;
            }
         } else if (this.getNumberOfCandles() == 3) {
            if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
               xOffset = -0.0625F;
               zOffset = 0.1875F;
            }

            if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
               xOffset = 0.0625F;
               zOffset = -0.1875F;
            }

            if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
               xOffset = -0.1875F;
               zOffset = -0.0625F;
            }

            if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
               xOffset = 0.1875F;
               zOffset = 0.0625F;
            }
         } else if (this.getNumberOfCandles() == 2) {
            if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
               xOffset = 0.1875F;
               zOffset = -0.125F;
            }

            if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
               xOffset = -0.1875F;
               zOffset = 0.125F;
            }

            if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
               xOffset = 0.125F;
               zOffset = 0.1875F;
            }

            if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
               xOffset = -0.125F;
               zOffset = -0.1875F;
            }
         } else if (this.getNumberOfCandles() == 1) {
            xOffset = 0.0F;
            zOffset = 0.0F;
         }

         if (((CandleData)this.candles.get(0)).returnToBlock || force) {
            ((CandleData)this.candles.get(0)).xTarget = xOffset;
            ((CandleData)this.candles.get(0)).yTarget = 0.0F;
            ((CandleData)this.candles.get(0)).zTarget = zOffset;
         }
      }

      if (((CandleData)this.candles.get(1)).hasCandle) {
         float xOffsetx = 0.0F;
         float zOffsetx = 0.0F;
         if (this.getNumberOfCandles() == 4) {
            if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
               xOffsetx = -0.125F;
               zOffsetx = -0.1875F;
            }

            if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
               xOffsetx = 0.1875F;
               zOffsetx = 0.1875F;
            }

            if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
               xOffsetx = 0.1875F;
               zOffsetx = -0.125F;
            }

            if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
               xOffsetx = -0.1875F;
               zOffsetx = 0.125F;
            }
         } else if (this.getNumberOfCandles() == 3) {
            if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
               xOffsetx = 0.1875F;
               zOffsetx = 0.0625F;
            }

            if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
               xOffsetx = -0.1875F;
               zOffsetx = -0.0625F;
            }

            if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
               xOffsetx = -0.0625F;
               zOffsetx = 0.1875F;
            }

            if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
               xOffsetx = 0.0625F;
               zOffsetx = -0.1875F;
            }
         } else if (this.getNumberOfCandles() == 2) {
            if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
               xOffsetx = -0.1875F;
               zOffsetx = 0.1875F;
            }

            if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
               xOffsetx = 0.1875F;
               zOffsetx = -0.1875F;
            }

            if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
               xOffsetx = -0.1875F;
               zOffsetx = -0.1875F;
            }

            if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
               xOffsetx = 0.1875F;
               zOffsetx = 0.1875F;
            }
         } else if (this.getNumberOfCandles() == 1) {
            xOffsetx = 0.0F;
            zOffsetx = 0.0F;
         }

         if (((CandleData)this.candles.get(1)).returnToBlock || force) {
            ((CandleData)this.candles.get(1)).xTarget = xOffsetx;
            ((CandleData)this.candles.get(1)).yTarget = 0.0F;
            ((CandleData)this.candles.get(1)).zTarget = zOffsetx;
         }
      }

      if (((CandleData)this.candles.get(2)).hasCandle) {
         float xOffsetxx = 0.0F;
         float zOffsetxx = 0.0F;
         if (this.getNumberOfCandles() == 4) {
            if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
               xOffsetxx = -0.125F;
               zOffsetxx = 0.125F;
            }

            if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
               xOffsetxx = 0.1875F;
               zOffsetxx = -0.125F;
            }

            if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
               xOffsetxx = 0.125F;
               zOffsetxx = 0.125F;
            }

            if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
               xOffsetxx = -0.125F;
               zOffsetxx = -0.125F;
            }
         } else if (this.getNumberOfCandles() == 3) {
            if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
               xOffsetxx = -0.125F;
               zOffsetxx = -0.1875F;
            }

            if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
               xOffsetxx = 0.125F;
               zOffsetxx = 0.1875F;
            }

            if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
               xOffsetxx = 0.1875F;
               zOffsetxx = -0.125F;
            }

            if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
               xOffsetxx = -0.1875F;
               zOffsetxx = 0.125F;
            }
         }

         if (((CandleData)this.candles.get(2)).returnToBlock || force) {
            ((CandleData)this.candles.get(2)).xTarget = xOffsetxx;
            ((CandleData)this.candles.get(2)).yTarget = 0.0F;
            ((CandleData)this.candles.get(2)).zTarget = zOffsetxx;
         }
      }

      if (((CandleData)this.candles.get(3)).hasCandle) {
         float xOffsetxxx = 0.0F;
         float zOffsetxxx = 0.0F;
         if (this.getNumberOfCandles() == 4) {
            if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
               xOffsetxxx = 0.1875F;
               zOffsetxxx = -0.125F;
            }

            if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
               xOffsetxxx = -0.1875F;
               zOffsetxxx = 0.125F;
            }

            if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
               xOffsetxxx = -0.125F;
               zOffsetxxx = -0.1875F;
            }

            if (this.getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
               xOffsetxxx = 0.125F;
               zOffsetxxx = 0.1875F;
            }
         }

         if (((CandleData)this.candles.get(3)).returnToBlock || force) {
            ((CandleData)this.candles.get(3)).xTarget = xOffsetxxx;
            ((CandleData)this.candles.get(3)).yTarget = 0.0F;
            ((CandleData)this.candles.get(3)).zTarget = zOffsetxxx;
         }
      }
   }

   public void updateCandleSlots() {
      if (!((CandleData)this.candles.get(0)).hasCandle) {
         this.updateCandleSlot(0);
      }

      if (!((CandleData)this.candles.get(1)).hasCandle) {
         this.updateCandleSlot(1);
      }

      if (!((CandleData)this.candles.get(2)).hasCandle) {
         this.updateCandleSlot(2);
      }
   }

   public void updateCandleSlot(int slot) {
      CandleData newData = new CandleData();
      newData.load(((CandleData)this.candles.get(slot + 1)).save(this.level.registryAccess()), this.level.registryAccess());
      this.candles.set(slot, newData);
      this.candles.set(slot + 1, new CandleData());
   }
}
