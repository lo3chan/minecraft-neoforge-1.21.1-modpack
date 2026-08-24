package net.astralya.hexalia.entity.custom;

import net.astralya.hexalia.gameplay.cacofey.ai.CacofeyAnchorHoverGoal;
import net.astralya.hexalia.gameplay.cacofey.ai.CacofeyHarvestGoal;
import net.astralya.hexalia.gameplay.cacofey.ai.CacofeyStealGoal;
import net.astralya.hexalia.gameplay.moths.ai.DriftFlyGoal;
import net.astralya.hexalia.gameplay.moths.ai.UnstuckNudgeGoal;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.item.custom.HexFocusItem;
import net.astralya.hexalia.particle.ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.Animation.LoopType;

public class CacofeyEntity extends TamableAnimal implements GeoEntity {
   private static final EntityDataAccessor<ItemStack> HELD_ITEM = SynchedEntityData.defineId(CacofeyEntity.class, EntityDataSerializers.ITEM_STACK);
   private static final EntityDataAccessor<Boolean> INSPECTING = SynchedEntityData.defineId(CacofeyEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Byte> CACOFEY_MODE = SynchedEntityData.defineId(CacofeyEntity.class, EntityDataSerializers.BYTE);
   private static final String TAG_STEAL_COOLDOWN = "StealCooldown";
   private static final String TAG_HELD_ITEM = "HeldItem";
   private static final String TAG_MODE = "CacofeyMode";
   private static final String TAG_ANCHOR_X = "AnchorX";
   private static final String TAG_ANCHOR_Y = "AnchorY";
   private static final String TAG_ANCHOR_Z = "AnchorZ";
   public int stealCooldown = 0;
   @Nullable
   private BlockPos anchorPos = null;
   private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

   public CacofeyEntity(EntityType<? extends TamableAnimal> type, Level level) {
      super(type, level);
      this.moveControl = new FlyingMoveControl(this, 20, true);
      this.setPathfindingMalus(PathType.DANGER_FIRE, -1.0F);
      this.setPathfindingMalus(PathType.WATER, -1.0F);
      this.setPathfindingMalus(PathType.WATER_BORDER, 16.0F);
      this.setPathfindingMalus(PathType.FENCE, -1.0F);
   }

   public static AttributeSupplier setAttributes() {
      return TamableAnimal.createMobAttributes()
         .add(Attributes.MAX_HEALTH, 10.0)
         .add(Attributes.FLYING_SPEED, 0.699999988079071)
         .add(Attributes.MOVEMENT_SPEED, 0.5)
         .build();
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
      this.goalSelector.addGoal(2, new TemptGoal(this, 1.1, Ingredient.of(new ItemLike[]{(ItemLike)ModItems.GALEBERRIES_COOKIE.get()}), false));
      this.goalSelector.addGoal(3, new CacofeyStealGoal(this));
      this.goalSelector.addGoal(4, new CacofeyHarvestGoal(this));
      this.goalSelector.addGoal(5, new CacofeyAnchorHoverGoal(this));
      this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0, 5.0F, 2.0F) {
         public boolean canUse() {
            return CacofeyEntity.this.getMode() == CacofeyMode.FOLLOW && super.canUse();
         }

         public boolean canContinueToUse() {
            return CacofeyEntity.this.getMode() == CacofeyMode.FOLLOW && super.canContinueToUse();
         }
      });
      this.goalSelector.addGoal(7, new DriftFlyGoal(this, 0.6) {
         @Override
         public boolean canUse() {
            return CacofeyEntity.this.getMode() != CacofeyMode.STAY && super.canUse();
         }

         public boolean canContinueToUse() {
            return CacofeyEntity.this.getMode() != CacofeyMode.STAY && super.canContinueToUse();
         }

         public void tick() {
            if (CacofeyEntity.this.getMode() != CacofeyMode.STAY) {
               super.tick();
            }
         }

         public void stop() {
            super.stop();
            CacofeyEntity.this.getNavigation().stop();
         }
      });
      this.goalSelector.addGoal(8, new UnstuckNudgeGoal(this) {
         @Override
         public boolean canUse() {
            return CacofeyEntity.this.getMode() != CacofeyMode.STAY && super.canUse();
         }

         @Override
         public boolean canContinueToUse() {
            return CacofeyEntity.this.getMode() != CacofeyMode.STAY && super.canContinueToUse();
         }

         @Override
         public void tick() {
            if (CacofeyEntity.this.getMode() != CacofeyMode.STAY) {
               super.tick();
            }
         }

         public void stop() {
            super.stop();
            CacofeyEntity.this.getNavigation().stop();
         }
      });
   }

   protected PathNavigation createNavigation(Level level) {
      FlyingPathNavigation navigation = new FlyingPathNavigation(this, level) {
         public boolean isStableDestination(BlockPos pos) {
            return !this.level.getBlockState(pos.below()).isAir();
         }
      };
      navigation.setCanOpenDoors(false);
      navigation.setCanFloat(false);
      navigation.setCanPassDoors(true);
      return navigation;
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(HELD_ITEM, ItemStack.EMPTY);
      builder.define(INSPECTING, false);
      builder.define(CACOFEY_MODE, (byte)CacofeyMode.FOLLOW.ordinal());
   }

   public CacofeyMode getMode() {
      int ordinal = (Byte)this.entityData.get(CACOFEY_MODE);
      return ordinal >= 0 && ordinal < CacofeyMode.values().length ? CacofeyMode.values()[ordinal] : CacofeyMode.FOLLOW;
   }

   public void setMode(CacofeyMode mode) {
      this.entityData.set(CACOFEY_MODE, (byte)mode.ordinal());
      this.setOrderedToSit(mode == CacofeyMode.STAY);
      if (mode == CacofeyMode.STAY) {
         this.getNavigation().stop();
         this.setDeltaMovement(Vec3.ZERO);
      }
   }

   @Nullable
   public BlockPos getAnchorPos() {
      return this.anchorPos;
   }

   public void setAnchorPos(@Nullable BlockPos pos) {
      this.anchorPos = pos;
   }

   public ItemStack getHeldItem() {
      return (ItemStack)this.entityData.get(HELD_ITEM);
   }

   public void setHeldItem(ItemStack stack) {
      this.entityData.set(HELD_ITEM, stack.copy());
   }

   public boolean isHoldingItem() {
      return !this.getHeldItem().isEmpty();
   }

   public boolean isInspecting() {
      return (Boolean)this.entityData.get(INSPECTING);
   }

   public void setInspecting(boolean inspecting) {
      this.entityData.set(INSPECTING, inspecting);
   }

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      if (stack.is((Item)ModItems.GALEBERRIES_COOKIE.get())) {
         if (!this.isTame()) {
            if (!this.level().isClientSide) {
               if (!player.getAbilities().instabuild) {
                  stack.shrink(1);
               }

               if (this.random.nextInt(3) == 0) {
                  this.tame(player);
                  this.setOrderedToSit(false);
                  this.level().broadcastEntityEvent(this, (byte)7);
               } else {
                  this.level().broadcastEntityEvent(this, (byte)6);
               }
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
         } else {
            return InteractionResult.PASS;
         }
      } else if (!this.isTame() || !this.isOwnedBy(player)) {
         return super.mobInteract(player, hand);
      } else if (stack.getItem() instanceof HexFocusItem) {
         if (!this.level().isClientSide) {
            HexFocusItem.attuneToEntity(stack, this.getUUID());
            player.displayClientMessage(Component.translatable("message.hexalia.cacofey.attuned", new Object[]{this.getName()}), true);
         }

         return InteractionResult.sidedSuccess(this.level().isClientSide);
      } else {
         if (!this.level().isClientSide) {
            CacofeyMode next = this.getMode().next();
            this.setMode(next);

            player.displayClientMessage(switch (next) {
               case STAY -> Component.translatable("message.hexalia.cacofey.stay");
               case FOLLOW -> Component.translatable("message.hexalia.cacofey.follow");
               case WANDER -> Component.translatable("message.hexalia.cacofey.wander");
            }, true);
         }

         return InteractionResult.sidedSuccess(this.level().isClientSide);
      }
   }

   public ItemStack getPickResult() {
      return new ItemStack((ItemLike)ModItems.CACOFEY_SPAWN_EGG.get());
   }

   public boolean isFood(ItemStack stack) {
      return false;
   }

   @Nullable
   public CacofeyEntity getBreedOffspring(ServerLevel level, AgeableMob other) {
      return null;
   }

   public void aiStep() {
      super.aiStep();
      if (this.getMode() == CacofeyMode.STAY) {
         this.getNavigation().stop();
         this.setDeltaMovement(Vec3.ZERO);
      } else if (!this.onGround() && this.getDeltaMovement().y < 0.0) {
         this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.6, 1.0));
      }

      if (!this.level().isClientSide && this.stealCooldown > 0) {
         this.stealCooldown--;
      }

      if (this.level().isClientSide && this.tickCount % 3 == 0) {
         Vec3 motion = this.getDeltaMovement();
         if (motion.horizontalDistanceSqr() > 0.001 || Math.abs(motion.y) > 0.001) {
            double trailX = this.getX() - motion.x * 0.5 + (this.random.nextDouble() - 0.5) * 0.15;
            double trailY = this.getY() + 0.3 + (this.random.nextDouble() - 0.5) * 0.1;
            double trailZ = this.getZ() - motion.z * 0.5 + (this.random.nextDouble() - 0.5) * 0.15;
            this.level().addParticle((ParticleOptions)ModParticleTypes.CACOFEY_DUST.get(), trailX, trailY, trailZ, 0.0, 0.003, 0.0);
         }
      }
   }

   public Vec3 getLeashOffset() {
      return new Vec3(0.0, 0.5F * this.getEyeHeight(), this.getBbWidth() * 0.2F);
   }

   public boolean isPushable() {
      return false;
   }

   protected boolean isFlapping() {
      return !this.onGround();
   }

   protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
   }

   public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
      return false;
   }

   public void addAdditionalSaveData(CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      tag.putInt("StealCooldown", this.stealCooldown);
      tag.putByte("CacofeyMode", (byte)this.getMode().ordinal());
      if (this.isHoldingItem()) {
         tag.put("HeldItem", this.getHeldItem().save(this.registryAccess()));
      }

      if (this.anchorPos != null) {
         tag.putInt("AnchorX", this.anchorPos.getX());
         tag.putInt("AnchorY", this.anchorPos.getY());
         tag.putInt("AnchorZ", this.anchorPos.getZ());
      }
   }

   public void readAdditionalSaveData(CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      if (tag.contains("StealCooldown")) {
         this.stealCooldown = tag.getInt("StealCooldown");
      }

      if (tag.contains("CacofeyMode")) {
         int ordinal = tag.getByte("CacofeyMode");
         if (ordinal >= 0 && ordinal < CacofeyMode.values().length) {
            this.setMode(CacofeyMode.values()[ordinal]);
         }
      }

      if (tag.contains("HeldItem")) {
         this.setHeldItem(ItemStack.parseOptional(this.registryAccess(), tag.getCompound("HeldItem")));
      }

      if (tag.contains("AnchorX") && tag.contains("AnchorY") && tag.contains("AnchorZ")) {
         this.anchorPos = new BlockPos(tag.getInt("AnchorX"), tag.getInt("AnchorY"), tag.getInt("AnchorZ"));
      }
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(new AnimationController(this, "controller", 2, this::predicate));
   }

   private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> state) {
      RawAnimation anim;
      if (this.isInspecting()) {
         anim = RawAnimation.begin().then("animation.cacofey.inspecting", LoopType.LOOP);
      } else if (this.onGround()) {
         anim = RawAnimation.begin().then("animation.cacofey.idle", LoopType.LOOP);
      } else {
         anim = RawAnimation.begin().then("animation.cacofey.flying", LoopType.LOOP);
      }

      state.getController().setAnimation(anim);
      return PlayState.CONTINUE;
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
