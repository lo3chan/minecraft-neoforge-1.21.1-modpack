package net.joefoxe.hexerei.tileentity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import net.joefoxe.hexerei.particle.ModParticleTypes;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.util.TreeCutter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.shapes.CollisionContext;

public class CuttingCrystalTile extends BlockEntity {
   public List<BlockPos> boundPos = new ArrayList<>();
   public boolean isParent;
   public static final AtomicInteger NEXT_BREAKER_ID = new AtomicInteger();
   protected int ticksUntilNextProgress;
   protected float destroyProgress;
   protected int breakerId = -NEXT_BREAKER_ID.incrementAndGet();
   protected BlockPos breakingPos;

   public CuttingCrystalTile(BlockEntityType<?> tileEntityTypeIn, BlockPos blockPos, BlockState blockState) {
      super(tileEntityTypeIn, blockPos, blockState);
   }

   public void cutTree(Level level, BlockPos breakingPos) {
      TreeCutter.findTree(level, breakingPos).destroyBlocks(level, null, this::dropItemFromCutTree);
   }

   public void dropItemFromCutTree(BlockPos pos, ItemStack stack) {
      float distance = (float)Math.sqrt(pos.distSqr(this.breakingPos));
      Vec3 dropPos = new Vec3(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F);
      ItemEntity entity = new ItemEntity(this.level, dropPos.x, dropPos.y, dropPos.z, stack);
      this.level.addFreshEntity(entity);
   }

   protected boolean shouldRun() {
      return this.isParent ? true : !this.boundPos.isEmpty() && this.boundPos.get(0) != null;
   }

   protected BlockPos getBreakingPos() {
      return this.getBlockPos().relative((Direction)this.getBlockState().getValue(HorizontalDirectionalBlock.FACING));
   }

   public void destroyNextTick() {
      this.ticksUntilNextProgress = 1;
   }

   private boolean posEquals(BlockPos pos, BlockPos pos2) {
      return pos.getX() == pos2.getX() && pos.getY() == pos2.getY() && pos.getZ() == pos2.getZ();
   }

   public void tick() {
      if (this.shouldRun() && this.ticksUntilNextProgress < 0) {
         this.destroyNextTick();
      }

      if (this.shouldRun()) {
         BlockPos lastPos = this.breakingPos;
         this.breakingPos = null;
         if (this.ticksUntilNextProgress >= 0) {
            if (this.ticksUntilNextProgress-- <= 0) {
               BlockPos thisPos = this.getBlockPos();

               for (BlockPos pos : this.boundPos) {
                  if (!this.posEquals(thisPos, pos)) {
                     Vec3 vec3_1 = HexereiUtil.getCenterOf(thisPos);
                     Vec3 vec3_2 = HexereiUtil.getCenterOf(pos);
                     Vec3 vec3_3 = vec3_2.subtract(vec3_1).normalize();
                     Vec3 vec3_4 = vec3_1.subtract(vec3_2).normalize();
                     BlockHitResult result = this.level
                        .clip(
                           new ClipContext(
                              vec3_1.add(vec3_3), HexereiUtil.getCenterOf(pos).subtract(vec3_4), Block.OUTLINE, Fluid.NONE, CollisionContext.empty()
                           )
                        );
                     if (result.getType() == Type.BLOCK && !this.posEquals(result.getBlockPos(), pos) && !this.posEquals(result.getBlockPos(), thisPos)) {
                        this.breakingPos = result.getBlockPos();
                        this.level
                           .addParticle(
                              (ParticleOptions)ModParticleTypes.EXTINGUISH.get(),
                              thisPos.getX() + 0.5F,
                              thisPos.getY() + 0.5F,
                              thisPos.getZ() + 0.5F,
                              (pos.getX() - thisPos.getX()) / 50.0F,
                              (pos.getY() - thisPos.getY()) / 50.0F,
                              (pos.getZ() - thisPos.getZ()) / 50.0F
                           );
                     }

                     if (!this.posEquals(pos, thisPos)) {
                        this.level
                           .addParticle(
                              (ParticleOptions)ModParticleTypes.EXTINGUISH.get(),
                              thisPos.getX() + 0.5F,
                              thisPos.getY() + 0.5F,
                              thisPos.getZ() + 0.5F,
                              (pos.getX() - thisPos.getX()) / 50.0F,
                              (pos.getY() - thisPos.getY()) / 50.0F,
                              (pos.getZ() - thisPos.getZ()) / 50.0F
                           );
                     }
                  }
               }

               if (this.breakingPos == null) {
                  this.destroyProgress = 0.0F;
               } else {
                  BlockState stateToBreak = this.level.getBlockState(this.breakingPos);
                  float blockHardness = stateToBreak.getDestroySpeed(this.level, this.breakingPos);
                  if (!this.canBreak(stateToBreak, blockHardness)) {
                     if (this.destroyProgress != 0.0F) {
                        this.destroyProgress = 0.0F;
                        if (!this.level.isClientSide) {
                           this.level.destroyBlockProgress(this.breakerId, this.breakingPos, -1);
                        }
                     }
                  } else {
                     float breakSpeed = this.getBreakSpeed();
                     this.destroyProgress = this.destroyProgress + Mth.clamp(breakSpeed / blockHardness, 0.0F, 10.0F - this.destroyProgress);
                     if (!this.level.isClientSide) {
                        this.level.playSound(null, this.worldPosition, stateToBreak.getSoundType().getHitSound(), SoundSource.NEUTRAL, 0.25F, 1.0F);
                     }

                     if (this.destroyProgress >= 10.0F) {
                        if (!this.level.isClientSide) {
                           this.onBlockBroken(stateToBreak);
                        }

                        this.destroyProgress = 0.0F;
                        this.ticksUntilNextProgress = -1;
                        if (!this.level.isClientSide) {
                           this.level.destroyBlockProgress(this.breakerId, this.breakingPos, -1);
                        }
                     } else {
                        this.ticksUntilNextProgress = (int)(blockHardness / breakSpeed);
                        if (!this.level.isClientSide) {
                           this.level.destroyBlockProgress(this.breakerId, this.breakingPos, (int)this.destroyProgress);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public boolean canBreak(BlockState stateToBreak, float blockHardness) {
      return isBreakable(stateToBreak, blockHardness);
   }

   public static boolean isBreakable(BlockState stateToBreak, float blockHardness) {
      return !(stateToBreak.getBlock() instanceof AirBlock) && blockHardness != -1.0F && (stateToBreak.is(BlockTags.LOGS) || stateToBreak.is(BlockTags.LEAVES));
   }

   public void onBlockBroken(BlockState stateToBreak) {
      BlockPos pos = this.getBlockPos();
      Vec3 vec = HexereiUtil.offsetRandomly(HexereiUtil.getCenterOf(this.breakingPos), this.level.random, 0.125F);
      HexereiUtil.destroyBlock(this.level, this.breakingPos, 1.0F, stack -> {
         if (!stack.isEmpty()) {
            if (this.level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
               if (!this.level.restoringBlockSnapshots) {
                  ItemEntity itementity = new ItemEntity(this.level, vec.x, vec.y, vec.z, stack);
                  itementity.setDefaultPickUpDelay();
                  itementity.setDeltaMovement(Vec3.ZERO);
                  this.level.addFreshEntity(itementity);
               }
            }
         }
      });
      if (stateToBreak.is(BlockTags.LOGS)) {
         TreeCutter.findTree(this.level, this.breakingPos).destroyBlocks(this.level, null, this::dropItemFromCutTree);
      }
   }

   protected float getBreakSpeed() {
      return Math.abs(1.0F);
   }

   public void setChanged() {
      super.setChanged();
   }

   public CuttingCrystalTile(BlockPos blockPos, BlockState blockState) {
      this((BlockEntityType<?>)ModTileEntities.CUTTING_CRYSTAL_TILE.get(), blockPos, blockState);
   }

   public CompoundTag getUpdateTag(Provider registries) {
      return super.getUpdateTag(registries);
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

   public Vec3 rotateAroundVec(Vec3 vector3dCenter, float rotation, Vec3 vector3d) {
      Vec3 newVec = vector3d.subtract(vector3dCenter);
      newVec = newVec.yRot(rotation / 180.0F * 3.1415927F);
      return newVec.add(vector3dCenter);
   }
}
