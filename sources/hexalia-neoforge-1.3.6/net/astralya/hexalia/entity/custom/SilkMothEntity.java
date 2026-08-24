package net.astralya.hexalia.entity.custom;

import net.astralya.hexalia.block.ModBlocks;
import net.astralya.hexalia.component.ModComponents;
import net.astralya.hexalia.component.item.MothData;
import net.astralya.hexalia.entity.ModEntities;
import net.astralya.hexalia.entity.custom.variant.SilkMothVariant;
import net.astralya.hexalia.gameplay.moths.ai.DriftFlyGoal;
import net.astralya.hexalia.gameplay.moths.ai.HoverAroundLightGoal;
import net.astralya.hexalia.gameplay.moths.ai.LayEggOnLeavesGoal;
import net.astralya.hexalia.gameplay.moths.ai.UnstuckNudgeGoal;
import net.astralya.hexalia.item.ModItems;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
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

public class SilkMothEntity extends Animal implements GeoEntity {
   private static final String TAG_VARIANT = "SilkMothVariant";
   private static final String TAG_NAME = "MothName";
   private static final String TAG_EGG_READY = "EggReady";
   private static final String TAG_EGG_COOLDOWN = "EggCooldownTicks";
   private static final String TAG_EGG_POS = "EggLayPos";
   private static final int DEFAULT_EGG_COOLDOWN_TICKS = 400;
   private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT = SynchedEntityData.defineId(SilkMothEntity.class, EntityDataSerializers.INT);
   private boolean eggReady;
   private int eggCooldownTicks;
   private BlockPos eggLayPos;
   private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

   public SilkMothEntity(EntityType<? extends Animal> type, Level level) {
      super(type, level);
      this.moveControl = new FlyingMoveControl(this, 20, true);
      this.setPathfindingMalus(PathType.DANGER_FIRE, -1.0F);
      this.setPathfindingMalus(PathType.WATER, -1.0F);
      this.setPathfindingMalus(PathType.WATER_BORDER, 16.0F);
      this.setPathfindingMalus(PathType.COCOA, -1.0F);
      this.setPathfindingMalus(PathType.FENCE, -1.0F);
   }

   public static AttributeSupplier setAttributes() {
      return Animal.createMobAttributes()
         .add(Attributes.MAX_HEALTH, 2.0)
         .add(Attributes.FLYING_SPEED, 0.30000001192092896)
         .add(Attributes.MOVEMENT_SPEED, 0.30000001192092896)
         .build();
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FleeSunGoal(this, 1.25));
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
      this.goalSelector.addGoal(3, new LayEggOnLeavesGoal(this, 1.0));
      this.goalSelector.addGoal(4, new TemptGoal(this, 1.1, Ingredient.of(new ItemLike[]{(ItemLike)ModItems.FRAGRANT_NECTAR.get()}), false));
      this.goalSelector.addGoal(5, new HoverAroundLightGoal(this, 1.0));
      this.goalSelector.addGoal(6, new DriftFlyGoal(this, 0.55));
      this.goalSelector.addGoal(7, new UnstuckNudgeGoal(this));
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

   public boolean isFood(ItemStack stack) {
      return stack.is((Item)ModItems.FRAGRANT_NECTAR.get());
   }

   public void aiStep() {
      super.aiStep();
      if (!this.onGround() && this.getDeltaMovement().y < 0.0) {
         this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.6, 1.0));
      }

      if (!this.level().isClientSide && this.eggCooldownTicks > 0) {
         this.eggCooldownTicks--;
      }
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob other) {
      return (AgeableMob)((EntityType)ModEntities.SILK_MOTH.get()).create(serverLevel);
   }

   public void spawnChildFromBreeding(ServerLevel level, Animal partner) {
      this.setAge(6000);
      partner.setAge(6000);
      this.resetLove();
      partner.resetLove();
      if (this.eggCooldownTicks > 0) {
         level.broadcastEntityEvent(this, (byte)18);
      } else {
         SilkMothEntity eggLayer = this.pickEggLayer(partner);
         if (eggLayer == null) {
            level.broadcastEntityEvent(this, (byte)18);
         } else {
            BlockPos target = eggLayer.findEggLayPos(level);
            if (target != null) {
               eggLayer.eggReady = true;
               eggLayer.eggCooldownTicks = 400;
               eggLayer.eggLayPos = target;
               level.playSound(null, eggLayer.blockPosition(), SoundEvents.TURTLE_LAY_EGG, SoundSource.NEUTRAL, 1.0F, 1.0F);
            }

            level.broadcastEntityEvent(this, (byte)18);
            level.broadcastEntityEvent(partner, (byte)18);
         }
      }
   }

   @Nullable
   private SilkMothEntity pickEggLayer(Animal partner) {
      if (partner instanceof SilkMothEntity other) {
         return this.random.nextBoolean() ? this : other;
      } else {
         return null;
      }
   }

   @Nullable
   private BlockPos findEggLayPos(ServerLevel level) {
      BlockPos base = this.blockPosition();
      int radius = 6;
      int down = 10;
      int up = 6;
      BlockPos preferred = this.findEggLayPosAboveNesting(level, base, radius, down, up);
      return preferred != null ? preferred : this.findEggLayPosAboveLeaves(level, base, radius, down, up);
   }

   @Nullable
   private BlockPos findEggLayPosAboveNesting(ServerLevel level, BlockPos base, int radius, int down, int up) {
      int validCount = 0;
      BlockPos chosen = null;

      for (int dy = -down; dy <= up; dy++) {
         for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
               BlockPos belowPos = base.offset(dx, dy, dz);
               if (level.getBlockState(belowPos).is((Block)ModBlocks.NESTING_BLOCK.get())) {
                  BlockPos placePos = belowPos.above();
                  if (level.getBlockState(placePos).isAir()) {
                     if (chosen == null || this.random.nextInt(++validCount) == 0) {
                        chosen = placePos;
                     }
                  }
               }
            }
         }
      }

      return chosen;
   }

   @Nullable
   private BlockPos findEggLayPosAboveLeaves(ServerLevel level, BlockPos base, int radius, int down, int up) {
      int validCount = 0;
      BlockPos chosen = null;

      for (int dy = -down; dy <= up; dy++) {
         for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
               BlockPos belowPos = base.offset(dx, dy, dz);
               if (level.getBlockState(belowPos).is(BlockTags.LEAVES)) {
                  BlockPos placePos = belowPos.above();
                  if (level.getBlockState(placePos).isAir()) {
                     if (chosen == null || this.random.nextInt(++validCount) == 0) {
                        chosen = placePos;
                     }
                  }
               }
            }
         }
      }

      return chosen;
   }

   public boolean isValidEggTarget(ServerLevel level, BlockPos pos) {
      if (!level.getBlockState(pos).isAir()) {
         return false;
      } else {
         BlockState below = level.getBlockState(pos.below());
         return below.is(BlockTags.LEAVES) || below.is((Block)ModBlocks.NESTING_BLOCK.get());
      }
   }

   public boolean isEggReady() {
      return this.eggReady;
   }

   @Nullable
   public BlockPos getEggLayPos() {
      return this.eggLayPos;
   }

   public void clearEggTarget() {
      this.eggReady = false;
      this.eggLayPos = null;
   }

   public void finishEggLaying() {
      this.eggReady = false;
      this.eggLayPos = null;
   }

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack heldItem = player.getItemInHand(hand);
      InteractionResult bottleResult = this.tryBottle(player, heldItem);
      return bottleResult.consumesAction() ? bottleResult : super.mobInteract(player, hand);
   }

   private InteractionResult tryBottle(Player player, ItemStack heldItem) {
      if (!heldItem.is((Item)ModItems.RUSTIC_BOTTLE.get())) {
         return InteractionResult.PASS;
      } else if (this.level().isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         ItemStack bottledMoth = new ItemStack((ItemLike)ModItems.BOTTLED_MOTH.get());
         String name = this.hasCustomName() ? this.getCustomName().getString() : "";
         int variantId = this.getVariant().getId();
         bottledMoth.set((DataComponentType)ModComponents.MOTH.get(), new MothData(name, variantId));
         this.remove(RemovalReason.DISCARDED);
         if (!player.getInventory().add(bottledMoth)) {
            this.spawnAtLocation(bottledMoth);
         }

         this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.PLAYERS, 1.0F, 1.0F);
         if (!player.getAbilities().instabuild) {
            heldItem.shrink(1);
         }

         return InteractionResult.SUCCESS;
      }
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DATA_ID_TYPE_VARIANT, 0);
   }

   public void setVariant(SilkMothVariant variant) {
      this.entityData.set(DATA_ID_TYPE_VARIANT, variant.getId() & 0xFF);
   }

   public SilkMothVariant getVariant() {
      return SilkMothVariant.byId((Integer)this.entityData.get(DATA_ID_TYPE_VARIANT) & 0xFF);
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnGroupData) {
      this.setVariant((SilkMothVariant)Util.getRandom(SilkMothVariant.values(), this.random));
      return super.finalizeSpawn(level, difficulty, reason, spawnGroupData);
   }

   public void addAdditionalSaveData(CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      tag.putInt("SilkMothVariant", this.getVariant().getId());
      tag.putBoolean("EggReady", this.eggReady);
      tag.putInt("EggCooldownTicks", this.eggCooldownTicks);
      if (this.eggLayPos != null) {
         tag.putLong("EggLayPos", this.eggLayPos.asLong());
      }

      if (this.hasCustomName()) {
         tag.putString("MothName", this.getCustomName().getString());
      }
   }

   public void readAdditionalSaveData(CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      if (tag.contains("SilkMothVariant")) {
         this.setVariant(SilkMothVariant.byId(tag.getInt("SilkMothVariant")));
      }

      if (tag.contains("EggReady")) {
         this.eggReady = tag.getBoolean("EggReady");
      }

      if (tag.contains("EggCooldownTicks")) {
         this.eggCooldownTicks = tag.getInt("EggCooldownTicks");
      }

      if (tag.contains("EggLayPos")) {
         this.eggLayPos = BlockPos.of(tag.getLong("EggLayPos"));
      } else {
         this.eggLayPos = null;
      }

      if (tag.contains("MothName")) {
         this.setCustomName(Component.literal(tag.getString("MothName")));
      }
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(new AnimationController(this, "controller", 2, this::predicate));
   }

   private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> state) {
      state.getController().setAnimation(RawAnimation.begin().then("animation.silkmoth.flying", LoopType.LOOP));
      return PlayState.CONTINUE;
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
