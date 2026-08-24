package net.cibernet.alchemancy.entity;

import com.mojang.logging.LogUtils;
import java.util.List;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.mixin.accessors.FallingBlockEntityAccessor;
import net.cibernet.alchemancy.registries.AlchemancyBlocks;
import net.cibernet.alchemancy.registries.AlchemancyEntities;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ConcretePowderBlock;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.slf4j.Logger;

public class CustomFallingBlock extends FallingBlockEntity {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final EntityDataAccessor<ItemStack> DATA_ITEM_STACK = SynchedEntityData.defineId(CustomFallingBlock.class, EntityDataSerializers.ITEM_STACK);
   private static final EntityDataAccessor<Float> GRAVITY = SynchedEntityData.defineId(CustomFallingBlock.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Boolean> HAS_COLLISION = SynchedEntityData.defineId(CustomFallingBlock.class, EntityDataSerializers.BOOLEAN);
   private static final float CONST = 1000.0F;

   public CustomFallingBlock(EntityType<? extends FallingBlockEntity> entityType, Level level) {
      super(entityType, level);
   }

   private CustomFallingBlock(Level level, double x, double y, double z, BlockState state, ItemStack item) {
      this((EntityType<? extends FallingBlockEntity>)AlchemancyEntities.FALLING_BLOCK.get(), level);
      ((FallingBlockEntityAccessor)this).setBlockState(state);
      this.blocksBuilding = true;
      this.setPos(x, y, z);
      this.setDeltaMovement(Vec3.ZERO);
      this.xo = x;
      this.yo = y;
      this.zo = z;
      this.setStartPos(this.blockPosition());
      this.setItem(item);
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(GRAVITY, 0.04F);
      builder.define(HAS_COLLISION, false);
      builder.define(DATA_ITEM_STACK, ItemStack.EMPTY);
   }

   public void setItem(ItemStack stack) {
      this.getEntityData().set(DATA_ITEM_STACK, stack.copyWithCount(1));
   }

   public ItemStack getItem() {
      return (ItemStack)this.getEntityData().get(DATA_ITEM_STACK);
   }

   public void move(MoverType type, Vec3 pos) {
      super.move(type, pos);
   }

   public boolean canCollideWith(Entity entity) {
      return false;
   }

   protected void applyGravity() {
      super.applyGravity();
   }

   public void tick() {
      if (this.blockState.isAir()) {
         this.discard();
      } else {
         Block block = this.blockState.getBlock();
         this.time++;
         this.applyGravity();
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.handlePortal();
         if (!this.level().isClientSide && (this.isAlive() || this.forceTickAfterTeleportToDuplicate)) {
            BlockPos blockpos = this.blockPosition();
            boolean isConcretePowder = this.blockState.getBlock() instanceof ConcretePowderBlock;
            boolean canBeHydrated = isConcretePowder && this.blockState.canBeHydrated(this.level(), blockpos, this.level().getFluidState(blockpos), blockpos);
            double d0 = this.getDeltaMovement().lengthSqr();
            if (isConcretePowder && d0 > 1.0) {
               BlockHitResult blockhitresult = this.level()
                  .clip(
                     new ClipContext(
                        new Vec3(this.xo, this.yo, this.zo), this.position(), net.minecraft.world.level.ClipContext.Block.COLLIDER, Fluid.SOURCE_ONLY, this
                     )
                  );
               if (blockhitresult.getType() != Type.MISS
                  && this.blockState
                     .canBeHydrated(this.level(), blockpos, this.level().getFluidState(blockhitresult.getBlockPos()), blockhitresult.getBlockPos())) {
                  blockpos = blockhitresult.getBlockPos();
                  canBeHydrated = true;
               }
            }

            if (!this.verticalCollision && !canBeHydrated) {
               if (!this.level().isClientSide
                  && (
                     this.time > 100 && (blockpos.getY() <= this.level().getMinBuildHeight() || blockpos.getY() > this.level().getMaxBuildHeight())
                        || this.time > 600
                  )) {
                  if (this.dropItem && this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                     this.dropItem();
                  }

                  this.callOnBrokenAfterFall(block, blockpos);
                  this.discard();
               }
            } else {
               BlockState blockstate = this.level().getBlockState(blockpos);
               this.setDeltaMovement(this.getDeltaMovement().multiply(0.7, -0.5, 0.7));
               if (!blockstate.is(Blocks.MOVING_PISTON)) {
                  if (this.cancelDrop) {
                     this.discard();
                     this.callOnBrokenAfterFall(block, blockpos);
                  } else {
                     boolean canReplace = blockstate.canBeReplaced(
                        new DirectionalPlaceContext(this.level(), blockpos, Direction.DOWN, ItemStack.EMPTY, Direction.UP)
                     );
                     boolean canPlace = this.isFree() && (!isConcretePowder || !canBeHydrated);
                     boolean canPlacedSurvive = this.blockState.canSurvive(this.level(), blockpos) && !canPlace;
                     if (canReplace && canPlacedSurvive) {
                        if (this.blockState.hasProperty(BlockStateProperties.WATERLOGGED) && this.level().getFluidState(blockpos).getType() == Fluids.WATER) {
                           this.blockState = (BlockState)this.blockState.setValue(BlockStateProperties.WATERLOGGED, true);
                        }

                        if (this.level().setBlock(blockpos, this.blockState, 3)) {
                           ((ServerLevel)this.level())
                              .getChunkSource()
                              .chunkMap
                              .broadcast(this, new ClientboundBlockUpdatePacket(blockpos, this.level().getBlockState(blockpos)));
                           this.discard();
                           if (block instanceof Fallable) {
                              ((Fallable)block).onLand(this.level(), blockpos, this.blockState, blockstate, this);
                           }

                           if (this.blockData != null && this.blockState.hasBlockEntity()) {
                              BlockEntity blockentity = this.level().getBlockEntity(blockpos);
                              if (blockentity != null) {
                                 CompoundTag compoundtag = blockentity.saveWithoutMetadata(this.level().registryAccess());

                                 for (String s : this.blockData.getAllKeys()) {
                                    compoundtag.put(s, this.blockData.get(s).copy());
                                 }

                                 try {
                                    blockentity.loadWithComponents(compoundtag, this.level().registryAccess());
                                 } catch (Exception var15) {
                                    LOGGER.error("Failed to load block entity from falling block", var15);
                                 }

                                 blockentity.setChanged();
                              }
                           }
                        } else if (this.dropItem && this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                           this.discard();
                           this.callOnBrokenAfterFall(block, blockpos);
                           this.dropItem();
                        }
                     } else {
                        this.discard();
                        this.callOnBrokenAfterFall(block, blockpos);
                        if (this.dropItem && this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                           this.dropItem();
                        }
                     }
                  }
               }
            }
         }

         this.setDeltaMovement(this.getDeltaMovement().scale(0.98));
      }

      if (this.blockState.is(AlchemancyBlocks.PHANTOM_MEMBRANE_BLOCK) || InfusedPropertiesHelper.hasProperty(this.getItem(), AlchemancyProperties.LAZY)) {
         if (this.getDefaultGravity() < 0.0) {
            this.setGravity((float)(this.getDefaultGravity() * 0.949999988079071));
         }

         this.setDeltaMovement(this.getDeltaMovement().scale(0.9583799839019775));
      }

      this.pushEntities();
   }

   public void dropItem() {
      this.spawnAtLocation(this.getItem().copy());
   }

   public boolean isAttackable() {
      return true;
   }

   public boolean hurt(DamageSource source, float amount) {
      if (!this.isInvulnerableTo(source) && !this.level().isClientSide()) {
         if (this.dropItem && this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            this.dropItem();
         }

         this.callOnBrokenAfterFall(
            this.blockState.getBlock(), new BlockPos(this.blockPosition().getX(), (int)Math.round(this.position().y()), this.blockPosition().getZ())
         );
         this.discard();
      }

      return false;
   }

   public InteractionResult interactAt(Player player, Vec3 vec, InteractionHand hand) {
      if (!this.blockState.is(AlchemancyBlocks.PHANTOM_MEMBRANE_BLOCK)) {
         return super.interactAt(player, vec, hand);
      } else {
         Direction direction = null;
         if (vec.x() > 0.4) {
            direction = Direction.EAST;
         } else if (vec.x() < -0.4) {
            direction = Direction.WEST;
         } else if (vec.z() > 0.4) {
            direction = Direction.SOUTH;
         } else if (vec.z() < -0.4) {
            direction = Direction.NORTH;
         } else if (vec.y() > 0.9) {
            direction = Direction.UP;
         } else if (vec.y() < 0.1) {
            direction = Direction.DOWN;
         }

         if (direction == null) {
            return InteractionResult.PASS;
         } else {
            ItemStack stack = player.getItemInHand(hand);
            return stack.useOn(
               new UseOnContext(
                  this.level(),
                  player,
                  hand,
                  stack,
                  new BlockHitResult(
                     this.position().add(vec),
                     direction,
                     new BlockPos(this.blockPosition().getX(), (int)this.getY(0.5), this.blockPosition().getZ()).relative(direction),
                     false
                  )
               )
            );
         }
      }
   }

   private boolean isFree() {
      double direction = this.getGravity() == 0.0 ? this.getDeltaMovement().y() : this.getGravity();
      return FallingBlock.isFree(this.level().getBlockState(direction < 0.0 ? this.blockPosition().above() : this.blockPosition().below()));
   }

   private void pushEntities() {
      if (this.level().isClientSide()) {
         Vec3 delta = this.getDeltaMovement();
         boolean sturdy = this.canBeCollidedWith();
         AABB aabb = this.getBoundingBox().move(this.getPosition(0.0F).subtract(this.position())).inflate(0.1);
         List<Entity> list = this.level().getEntities(this, aabb);
         if (!list.isEmpty()) {
            for (Entity entity : list) {
               if (entity.getPistonPushReaction() != PushReaction.IGNORE && !entity.getType().equals(EntityType.FISHING_BOBBER)) {
                  boolean isVertical = !(Math.max(Math.abs(entity.getX() - this.getX()), Math.abs(entity.getZ() - this.getZ())) > this.getBbWidth() * 0.5F);
                  Vec3 relativePosition = entity.position().subtract(this.position());
                  entity.move(
                     MoverType.SHULKER,
                     new Vec3(
                        !isVertical && Math.signum(relativePosition.x()) != Math.signum(delta.x()) ? 0.0 : delta.x(),
                        isVertical
                           ? (
                              Math.signum(relativePosition.y()) == Math.signum(delta.y())
                                 ? Math.max(0.0, this.getY(Math.max(0.0, Math.signum(relativePosition.y()))) - entity.getY())
                                 : 0.0
                           )
                           : 0.0,
                        !isVertical && Math.signum(relativePosition.z()) != Math.signum(delta.z()) ? 0.0 : delta.z()
                     )
                  );
                  if (sturdy && entity.position().y >= this.position().y + delta.y) {
                     entity.setOnGround(true);
                     Vec3 entityDelta = entity.getDeltaMovement();
                     entity.setDeltaMovement(entityDelta.x, Math.max(0.0, entityDelta.y), entityDelta.z);
                  }
               }
            }
         }
      }
   }

   public static CustomFallingBlock fall(Level level, BlockPos pos, BlockState blockState, ItemStack item) {
      CustomFallingBlock fallingblockentity = new CustomFallingBlock(
         level,
         pos.getX() + 0.5,
         pos.getY(),
         pos.getZ() + 0.5,
         blockState.hasProperty(BlockStateProperties.WATERLOGGED) ? (BlockState)blockState.setValue(BlockStateProperties.WATERLOGGED, false) : blockState,
         item
      );
      level.setBlock(pos, blockState.getFluidState().createLegacyBlock(), 3);
      level.addFreshEntity(fallingblockentity);
      return fallingblockentity;
   }

   public void setDeltaMovement(Vec3 deltaMovement) {
      if (!this.getDeltaMovement().scale(0.98).equals(deltaMovement)) {
         super.setDeltaMovement(deltaMovement);
      }
   }

   public boolean canBeCollidedWith() {
      return (Boolean)this.entityData.get(HAS_COLLISION);
   }

   public double getDefaultGravity() {
      return ((Float)this.entityData.get(GRAVITY)).floatValue();
   }

   public void setGravity(float v) {
      this.entityData.set(GRAVITY, v);
   }

   public void setHasCollision(boolean v) {
      this.entityData.set(HAS_COLLISION, v);
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putFloat("gravity", (float)this.getDefaultGravity());
      compound.putBoolean("has_collision", this.canBeCollidedWith());
      if (!this.getItem().isEmpty()) {
         compound.put("Item", this.getItem().save(this.registryAccess()));
      }
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      if (compound.contains("gravity", 99)) {
         this.setGravity(compound.getFloat("gravity"));
      }

      this.setHasCollision(compound.getBoolean("has_collision"));
      if (compound.contains("Item", 10)) {
         this.setItem(ItemStack.parse(this.registryAccess(), compound.getCompound("Item")).orElseGet(() -> new ItemStack(this.getDefaultItem())));
      } else {
         this.setItem(new ItemStack(this.getDefaultItem()));
      }
   }

   private ItemLike getDefaultItem() {
      return this.getBlockState().getBlock();
   }
}
