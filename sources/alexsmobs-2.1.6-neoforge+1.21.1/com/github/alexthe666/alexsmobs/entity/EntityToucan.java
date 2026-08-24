package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.DirectPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.FlightMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.FlyingAITargetDroppedItems;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class EntityToucan extends Animal implements ITargetsDroppedItems {
   private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(EntityToucan.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> PECK_TICK = SynchedEntityData.defineId(EntityToucan.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(EntityToucan.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> GOLDEN_TIME = SynchedEntityData.defineId(EntityToucan.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> ENCHANTED = SynchedEntityData.defineId(EntityToucan.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Optional<BlockState>> SAPLING_STATE = SynchedEntityData.defineId(
      EntityToucan.class, EntityDataSerializers.OPTIONAL_BLOCK_STATE
   );
   private static final EntityDataAccessor<Integer> SAPLING_TIME = SynchedEntityData.defineId(EntityToucan.class, EntityDataSerializers.INT);
   private static final HashMap<String, String> FEEDING_DATA = new HashMap<>();
   private static final List<ItemStack> FEEDING_STACKS = new ArrayList<>();
   private static boolean initFeedingData = false;
   public float prevFlyProgress;
   public float flyProgress;
   public float prevPeckProgress;
   public float peckProgress;
   private boolean isLandNavigator;
   private int timeFlying;
   private int heldItemTime;
   private boolean aiItemFlag;

   protected EntityToucan(EntityType type, Level worldIn) {
      super(type, worldIn);
      initFeedingData();
      this.setPathfindingMalus(PathType.DANGER_FIRE, -1.0F);
      this.setPathfindingMalus(PathType.DAMAGE_FIRE, -1.0F);
      this.setPathfindingMalus(PathType.COCOA, -1.0F);
      this.setPathfindingMalus(PathType.LEAVES, 0.0F);
      this.switchNavigator(true);
   }

   public static boolean canToucanSpawn(EntityType type, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource randomIn) {
      return true;
   }

   private static void initFeedingData() {
      if (!initFeedingData || FEEDING_DATA.isEmpty()) {
         initFeedingData = true;

         for (String str : AMConfig.toucanFruitMatches) {
            String[] split = str.split("\\|");
            if (split.length >= 2) {
               FEEDING_DATA.put(split[0], split[1]);
               FEEDING_STACKS.add(new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(AMCompat.rl(split[0]))));
            }
         }
      }
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 6.0).add(Attributes.MOVEMENT_SPEED, 0.20000000298023224);
   }

   protected SoundEvent getAmbientSound() {
      return AMSoundRegistry.TOUCAN_IDLE.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.TOUCAN_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.TOUCAN_HURT.get();
   }

   public boolean checkSpawnObstruction(LevelReader p_29005_) {
      if (p_29005_.isUnobstructed(this) && !p_29005_.containsAnyLiquid(this.getBoundingBox())) {
         BlockPos blockpos = this.blockPosition();
         if (blockpos.getY() < p_29005_.getSeaLevel()) {
            return false;
         } else {
            BlockState blockstate2 = p_29005_.getBlockState(blockpos.below());
            return blockstate2.is(Blocks.GRASS_BLOCK) || blockstate2.is(BlockTags.LEAVES);
         }
      } else {
         return false;
      }
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.toucanSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   @Nullable
   private BlockState getSaplingFor(ItemStack stack) {
      ResourceLocation name = BuiltInRegistries.ITEM.getKey(stack.getItem());
      if (!stack.isEmpty() && name != null && FEEDING_DATA.containsKey(name.toString())) {
         String str = FEEDING_DATA.get(name.toString());
         Block block = (Block)BuiltInRegistries.BLOCK.get(AMCompat.rl(str));
         if (block != null) {
            return block.defaultBlockState();
         }
      }

      return null;
   }

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      InteractionResult type = super.mobInteract(player, hand);
      if (this.getSaplingFor(itemstack) != null && this.getSaplingTime() <= 0 && this.getMainHandItem().isEmpty()) {
         this.peck();
         ItemStack duplicate = itemstack.copy();
         duplicate.setCount(1);
         this.setItemInHand(InteractionHand.MAIN_HAND, duplicate);
         this.usePlayerItem(player, hand, itemstack);
         return InteractionResult.SUCCESS;
      } else {
         return type;
      }
   }

   protected void registerGoals() {
      super.registerGoals();
      initFeedingData();
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new PanicGoal(this, 1.3));
      this.goalSelector.addGoal(2, new EntityToucan.AIPlantTrees());
      this.goalSelector.addGoal(3, new BreedGoal(this, 1.0));
      this.goalSelector.addGoal(4, new TemptGoal(this, 1.0, Ingredient.of(FEEDING_STACKS.stream()), false) {
         public boolean canUse() {
            return !EntityToucan.this.aiItemFlag && super.canUse();
         }
      });
      this.goalSelector.addGoal(5, new EntityToucan.AIWanderIdle());
      this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, PathfinderMob.class, 6.0F));
      this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new FlyingAITargetDroppedItems(this, false, false, 15, 16));
   }

   @Override
   public void setItemFlag(boolean itemAIFlag) {
      this.aiItemFlag = itemAIFlag;
   }

   public boolean isFood(ItemStack stack) {
      Item item = stack.getItem();
      return stack.is(AMTagRegistry.TOUCAN_BREEDABLES);
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(SAPLING_STATE, Optional.empty());
      builder.define(FLYING, false);
      builder.define(PECK_TICK, 0);
      builder.define(VARIANT, 0);
      builder.define(GOLDEN_TIME, 0);
      builder.define(SAPLING_TIME, 0);
      builder.define(ENCHANTED, false);
   }

   public boolean onClimbable() {
      return false;
   }

   public void tick() {
      super.tick();
      this.prevFlyProgress = this.flyProgress;
      this.prevPeckProgress = this.peckProgress;
      if (this.getGoldenTime() > 0 && !this.level().isClientSide()) {
         this.setGoldenTime(this.getGoldenTime() - 1);
      }

      boolean flying = this.isFlying();
      if (flying) {
         if (this.flyProgress < 5.0F) {
            this.flyProgress++;
         }
      } else if (this.flyProgress > 0.0F) {
         this.flyProgress--;
      }

      if (!this.level().isClientSide()) {
         if (flying) {
            if (this.isLandNavigator) {
               this.switchNavigator(false);
            }
         } else if (!this.isLandNavigator) {
            this.switchNavigator(true);
         }

         if (flying) {
            this.setNoGravity(true);
            if (this.isFlying() && !this.onGround() && !this.isInWaterOrBubble()) {
               this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.6000000238418579, 1.0));
            }

            this.timeFlying++;
         } else {
            this.setNoGravity(false);
            this.timeFlying = 0;
         }
      }

      if ((Integer)this.entityData.get(PECK_TICK) > 0) {
         this.entityData.set(PECK_TICK, (Integer)this.entityData.get(PECK_TICK) - 1);
         if (this.peckProgress < 5.0F) {
            this.peckProgress++;
         }
      } else if (this.peckProgress > 0.0F) {
         this.peckProgress--;
      }

      if (this.peckProgress >= 5.0F && this.getMainHandItem().isEmpty() && this.getSaplingState() != null) {
         this.peckBlockEffect();
      }

      if (!this.getMainHandItem().isEmpty()) {
         this.heldItemTime++;
         if (this.heldItemTime > 10 && this.canTargetItem(this.getMainHandItem())) {
            this.heldItemTime = 0;
            this.heal(4.0F);
            this.gameEvent(GameEvent.EAT);
            this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), this.getVoicePitch());
            if (AMCompat.hasCraftingRemainder(this.getMainHandItem())) {
               AMCompat.spawnAtLocation(this, AMCompat.craftingRemainder(this.getMainHandItem()));
            }

            ItemStack mainHandItem = this.getMainHandItem();
            if (mainHandItem.is(AMTagRegistry.TOUCAN_GOLDEN_FOODS)) {
               this.setGoldenTime(12000);
            } else if (mainHandItem.is(AMTagRegistry.TOUCAN_ENCHANTED_GOLDEN_FOODS)) {
               this.setGoldenTime(-1);
               this.setEnchanted(true);
            }

            this.setSaplingState(this.getSaplingFor(this.getMainHandItem()));
            this.eatItemEffect(this.getMainHandItem());
            this.getMainHandItem().shrink(1);
         }
      } else {
         this.heldItemTime = 0;
      }

      if (this.isFlying() && this.getBlockStateOn().is(Blocks.VINE)) {
         float f = this.getYRot() * 0.017453292F;
         this.setDeltaMovement(this.getDeltaMovement().add(-Mth.sin(f) * 0.2F, 0.4000000059604645, Mth.cos(f) * 0.2F));
      }
   }

   public boolean causeFallDamage(float distance, float damageMultiplier) {
      return false;
   }

   protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
   }

   @Override
   public boolean isFlying() {
      return (Boolean)this.entityData.get(FLYING);
   }

   @Override
   public void setFlying(boolean flying) {
      if (!flying || !this.isBaby()) {
         this.entityData.set(FLYING, flying);
      }
   }

   @Override
   public void peck() {
      if (this.peckProgress == 0.0F) {
         this.entityData.set(PECK_TICK, 7);
      }
   }

   public Vec3 getBlockInViewAway(Vec3 fleePos, float radiusAdd) {
      float radius = 7.0F + radiusAdd + this.getRandom().nextInt(8);
      float neg = this.getRandom().nextBoolean() ? 1.0F : -1.0F;
      float renderYawOffset = this.yBodyRot;
      float angle = 0.017453292F * renderYawOffset + 3.15F + this.getRandom().nextFloat() * neg;
      double extraX = radius * Mth.sin(3.1415927F + angle);
      double extraZ = radius * Mth.cos(angle);
      BlockPos radialPos = new BlockPos((int)(fleePos.x() + extraX), 0, (int)(fleePos.z() + extraZ));
      BlockPos ground = this.getToucanGround(radialPos);
      int distFromGround = (int)this.getY() - ground.getY();
      int flightHeight = 8 + this.getRandom().nextInt(4);
      int j = this.getRandom().nextInt(6) + 18;
      BlockPos newPos = ground.above(distFromGround > 9 ? flightHeight : j);
      if (this.level().getBlockState(ground).is(BlockTags.LEAVES)) {
         newPos = ground.above(1 + this.getRandom().nextInt(3));
      }

      return !this.isTargetBlocked(Vec3.atCenterOf(newPos)) && this.distanceToSqr(Vec3.atCenterOf(newPos)) > 1.0 ? Vec3.atCenterOf(newPos) : null;
   }

   public BlockPos getToucanGround(BlockPos in) {
      BlockPos position = new BlockPos(in.getX(), (int)this.getY(), in.getZ());

      while (position.getY() < 320 && !this.level().getFluidState(position).isEmpty()) {
         position = position.above();
      }

      while (position.getY() > -64 && !this.level().getBlockState(position).isSolid() && this.level().getFluidState(position).isEmpty()) {
         position = position.below();
      }

      return position;
   }

   public Vec3 getBlockGrounding(Vec3 fleePos) {
      float radius = 10 + this.getRandom().nextInt(15);
      float neg = this.getRandom().nextBoolean() ? 1.0F : -1.0F;
      float renderYawOffset = this.yBodyRot;
      float angle = 0.017453292F * renderYawOffset + 3.15F + this.getRandom().nextFloat() * neg;
      double extraX = radius * Mth.sin(3.1415927F + angle);
      double extraZ = radius * Mth.cos(angle);
      BlockPos radialPos = AMBlockPos.fromCoords(fleePos.x() + extraX, this.getY(), fleePos.z() + extraZ);
      BlockPos ground = this.getToucanGround(radialPos);
      if (ground.getY() == -64) {
         return this.position();
      } else {
         ground = this.blockPosition();

         while (ground.getY() > -62 && !this.level().getBlockState(ground).isSolid()) {
            ground = ground.below();
         }

         return !this.isTargetBlocked(Vec3.atCenterOf(ground.above())) ? Vec3.atCenterOf(ground) : null;
      }
   }

   public boolean isTargetBlocked(Vec3 target) {
      Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
      return this.level().clip(new ClipContext(Vector3d, target, net.minecraft.world.level.ClipContext.Block.COLLIDER, Fluid.NONE, this)).getType()
         != Type.MISS;
   }

   private void switchNavigator(boolean onLand) {
      if (onLand) {
         this.moveControl = new MoveControl(this);
         this.navigation = new GroundPathNavigation(this, this.level());
         this.isLandNavigator = true;
      } else {
         this.moveControl = new FlightMoveController(this, 0.6F, false, true);
         this.navigation = new DirectPathNavigator(this, this.level());
         this.isLandNavigator = false;
      }
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      BlockState blockstate = this.getSaplingState();
      if (blockstate != null) {
         AMCompat.put(compound, "SaplingState", NbtUtils.writeBlockState(blockstate));
      }

      compound.putInt("Variant", this.getVariant());
      compound.putInt("GoldenTime", this.getGoldenTime());
      compound.putBoolean("Enchanted", this.isEnchanted());
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      BlockState blockstate = null;
      if (AMCompat.contains(compound, "SaplingState", 10)) {
         blockstate = NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), AMCompat.getCompound(compound, "SaplingState"));
         if (blockstate.isAir()) {
            blockstate = null;
         }
      }

      this.setSaplingState(blockstate);
      this.setVariant(AMCompat.getInt(compound, "Variant"));
      this.setGoldenTime(AMCompat.getInt(compound, "GoldenTime"));
      this.setEnchanted(AMCompat.getBoolean(compound, "Enchanted"));
   }

   public boolean isSam() {
      String s = ChatFormatting.stripFormatting(this.getName().getString());
      return s != null && s.toLowerCase().contains("sam");
   }

   public int getVariant() {
      return (Integer)this.entityData.get(VARIANT);
   }

   public void setVariant(int variant) {
      this.entityData.set(VARIANT, variant);
   }

   public int getSaplingTime() {
      return (Integer)this.entityData.get(SAPLING_TIME);
   }

   public void setSaplingTime(int time) {
      this.entityData.set(SAPLING_TIME, time);
   }

   public boolean isGolden() {
      return this.getGoldenTime() > 0 || this.getGoldenTime() == -1 || this.isEnchanted();
   }

   public int getGoldenTime() {
      return (Integer)this.entityData.get(GOLDEN_TIME);
   }

   public void setGoldenTime(int goldenTime) {
      this.entityData.set(GOLDEN_TIME, goldenTime);
   }

   public boolean isEnchanted() {
      return (Boolean)this.entityData.get(ENCHANTED);
   }

   public void setEnchanted(boolean enchanted) {
      this.entityData.set(ENCHANTED, enchanted);
   }

   @Nullable
   public BlockState getSaplingState() {
      return (BlockState)((Optional)this.entityData.get(SAPLING_STATE)).orElse(null);
   }

   public void setSaplingState(@Nullable BlockState state) {
      this.entityData.set(SAPLING_STATE, Optional.ofNullable(state));
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn) {
      this.setVariant(this.getRandom().nextInt(4));
      return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn);
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob parent) {
      EntityToucan toucan = AMCompat.create(AMEntityRegistry.TOUCAN.get(), this.level());
      toucan.setVariant(this.getVariant());
      return toucan;
   }

   public float getWalkTargetValue(BlockPos pos, LevelReader worldIn) {
      return worldIn.getBlockState(pos).is(BlockTags.LEAVES) ? 10.0F : super.getWalkTargetValue(pos, worldIn);
   }

   private boolean isOverWaterOrVoid() {
      BlockPos position = this.blockPosition();

      while (position.getY() > -62 && this.level().isEmptyBlock(position)) {
         position = position.below();
      }

      return !this.level().getFluidState(position).isEmpty() || this.level().getBlockState(position).is(Blocks.VINE) || position.getY() <= 0;
   }

   private boolean isOverLeaves() {
      BlockPos position = this.blockPosition();

      while (position.getY() > -62 && this.level().isEmptyBlock(position)) {
         position = position.below();
      }

      return this.level().getBlockState(position).is(BlockTags.LEAVES) || this.level().getBlockState(position).is(Blocks.VINE);
   }

   @Override
   public boolean canTargetItem(ItemStack stack) {
      return this.getSaplingTime() <= 0 && this.getSaplingFor(stack) != null;
   }

   private void peckBlockEffect() {
      BlockState beneath = this.getBlockStateOn();
      if (this.level().isClientSide() && !beneath.isAir() && beneath.getFluidState().isEmpty()) {
         for (int i = 0; i < 2 + this.random.nextInt(2); i++) {
            double d2 = this.random.nextGaussian() * 0.02;
            double d0 = this.random.nextGaussian() * 0.02;
            double d1 = this.random.nextGaussian() * 0.02;
            float radius = this.getBbWidth() * 0.65F;
            float angle = 0.017453292F * this.yBodyRot;
            double extraX = radius * Mth.sin(3.1415927F + angle);
            double extraZ = radius * Mth.cos(angle);
            ParticleOptions data = new BlockParticleOption(ParticleTypes.BLOCK, beneath);
            this.level().addParticle(data, this.getX() + extraX, this.getY() + 0.10000000149011612, this.getZ() + extraZ, d0, d1, d2);
         }
      }
   }

   private void eatItemEffect(ItemStack heldItemMainhand) {
      for (int i = 0; i < 2 + this.random.nextInt(2); i++) {
         double d2 = this.random.nextGaussian() * 0.02;
         double d0 = this.random.nextGaussian() * 0.02;
         double d1 = this.random.nextGaussian() * 0.02;
         float radius = this.getBbWidth() * 0.65F;
         float angle = 0.017453292F * this.yBodyRot;
         double extraX = radius * Mth.sin(3.1415927F + angle);
         double extraZ = radius * Mth.cos(angle);
         ParticleOptions data = new ItemParticleOption(ParticleTypes.ITEM, heldItemMainhand);
         if (heldItemMainhand.getItem() instanceof BlockItem) {
            data = new BlockParticleOption(ParticleTypes.BLOCK, ((BlockItem)heldItemMainhand.getItem()).getBlock().defaultBlockState());
         }

         this.level().addParticle(data, this.getX() + extraX, this.getY() + this.getBbHeight() * 0.6F, this.getZ() + extraZ, d0, d1, d2);
      }
   }

   @Override
   public void onGetItem(ItemEntity e) {
      ItemStack duplicate = e.getItem().copy();
      duplicate.setCount(1);
      if (!this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && !this.level().isClientSide()) {
         AMCompat.spawnAtLocation(this, this.getItemInHand(InteractionHand.MAIN_HAND), 0.0F);
      }

      this.peck();
      this.setFlying(true);
      this.setItemInHand(InteractionHand.MAIN_HAND, duplicate);
   }

   private boolean hasLineOfSightSapling(BlockPos destinationBlock) {
      Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());
      Vec3 blockVec = Vec3.atCenterOf(destinationBlock);
      BlockHitResult result = this.level().clip(new ClipContext(Vector3d, blockVec, net.minecraft.world.level.ClipContext.Block.COLLIDER, Fluid.NONE, this));
      return result.getBlockPos().equals(destinationBlock);
   }

   private class AIPlantTrees extends Goal {
      protected final EntityToucan toucan;
      protected BlockPos pos;
      private int runCooldown = 0;
      private int encircleTime = 0;
      private int plantTime = 0;
      private boolean clockwise;

      public AIPlantTrees() {
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
         this.toucan = EntityToucan.this;
      }

      public boolean canUse() {
         if (this.toucan.getSaplingState() != null && this.runCooldown-- <= 0) {
            BlockPos target = this.getSaplingPlantPos();
            this.runCooldown = this.resetCooldown();
            if (target != null) {
               this.pos = target;
               this.clockwise = EntityToucan.this.random.nextBoolean();
               this.encircleTime = (this.toucan.isGolden() ? 20 : 100) + EntityToucan.this.random.nextInt(100);
               return true;
            }
         }

         return false;
      }

      private int resetCooldown() {
         return this.toucan.isGolden() && !this.toucan.isEnchanted() ? 50 + EntityToucan.this.random.nextInt(40) : 200 + EntityToucan.this.random.nextInt(200);
      }

      public void tick() {
         this.toucan.aiItemFlag = true;
         double up = 3.0;
         if (this.encircleTime > 0) {
            this.encircleTime--;
         }

         if (this.isWithinXZDist(this.pos, this.toucan.position(), 5.0) && this.encircleTime <= 0) {
            up = 0.0;
         }

         if (this.toucan.distanceToSqr(Vec3.atCenterOf(this.pos)) < 3.0) {
            this.toucan.setFlying(false);
            this.toucan.peck();
            this.plantTime++;
            if (this.plantTime > 60) {
               BlockState state = this.toucan.getSaplingState();
               if (state != null && state.canSurvive(this.toucan.level(), this.pos) && this.toucan.level().getBlockState(this.pos).canBeReplaced()) {
                  this.toucan.level().setBlockAndUpdate(this.pos, state);
                  if (!this.toucan.isEnchanted()) {
                     this.toucan.setSaplingState(null);
                  }
               }

               this.stop();
            }
         } else {
            BlockPos moveTo = this.pos;
            if (this.encircleTime > 0) {
               moveTo = this.getVultureCirclePos(this.pos, 3.0F, up);
            }

            if (moveTo != null) {
               if (this.encircleTime <= 0 && !this.toucan.hasLineOfSightSapling(this.pos)) {
                  this.toucan.setFlying(false);
                  this.toucan.getNavigation().moveTo(moveTo.getX() + 0.5F, moveTo.getY() + up + 0.5, moveTo.getZ() + 0.5F, 1.0);
               } else {
                  this.toucan.setFlying(true);
                  this.toucan.getMoveControl().setWantedPosition(moveTo.getX() + 0.5F, moveTo.getY() + up + 0.5, moveTo.getZ() + 0.5F, 1.0);
               }
            }
         }
      }

      public BlockPos getVultureCirclePos(BlockPos target, float circleDistance, double yLevel) {
         float angle = 0.13962634F * (this.clockwise ? -this.encircleTime : this.encircleTime);
         double extraX = circleDistance * Mth.sin(angle);
         double extraZ = circleDistance * Mth.cos(angle);
         BlockPos pos = new BlockPos((int)(target.getX() + 0.5F + extraX), (int)(target.getY() + 1 + yLevel), (int)(target.getZ() + 0.5F + extraZ));
         return this.toucan.level().isEmptyBlock(pos) ? pos : null;
      }

      public void stop() {
         this.toucan.aiItemFlag = false;
         this.pos = null;
         this.plantTime = 0;
         this.encircleTime = 0;
      }

      public boolean canContinueToUse() {
         return this.pos != null && this.toucan.getSaplingState() != null;
      }

      private boolean isWithinXZDist(BlockPos blockpos, Vec3 positionVec, double distance) {
         return blockpos.distSqr(new BlockPos((int)positionVec.x(), blockpos.getY(), (int)positionVec.z())) < distance * distance;
      }

      private BlockPos getSaplingPlantPos() {
         BlockState state = this.toucan.getSaplingState();
         if (state != null) {
            for (int i = 0; i < 15; i++) {
               BlockPos pos = this.toucan
                  .blockPosition()
                  .offset(EntityToucan.this.random.nextInt(10) - 8, EntityToucan.this.random.nextInt(8) - 4, EntityToucan.this.random.nextInt(16) - 8);
               if (state.canSurvive(this.toucan.level(), pos) && this.toucan.level().isEmptyBlock(pos.above()) && this.toucan.hasLineOfSightSapling(pos)) {
                  return pos;
               }
            }
         }

         return null;
      }
   }

   private class AIWanderIdle extends Goal {
      protected final EntityToucan toucan;
      protected double x;
      protected double y;
      protected double z;
      private boolean flightTarget = false;

      public AIWanderIdle() {
         this.setFlags(EnumSet.of(Flag.MOVE));
         this.toucan = EntityToucan.this;
      }

      public boolean canUse() {
         if (this.toucan.isVehicle()
            || this.toucan.getSaplingState() != null
            || EntityToucan.this.aiItemFlag
            || this.toucan.getTarget() != null && this.toucan.getTarget().isAlive()
            || this.toucan.isPassenger()) {
            return false;
         } else if (this.toucan.getRandom().nextInt(45) != 0 && !this.toucan.isFlying()) {
            return false;
         } else {
            if (this.toucan.onGround()) {
               this.flightTarget = EntityToucan.this.random.nextInt(6) == 0;
            } else {
               this.flightTarget = EntityToucan.this.random.nextInt(5) != 0 && this.toucan.timeFlying < 200;
            }

            Vec3 lvt_1_1_ = this.getPosition();
            if (lvt_1_1_ == null) {
               return false;
            } else {
               this.x = lvt_1_1_.x;
               this.y = lvt_1_1_.y;
               this.z = lvt_1_1_.z;
               return true;
            }
         }
      }

      public void tick() {
         if (this.flightTarget) {
            this.toucan.getMoveControl().setWantedPosition(this.x, this.y, this.z, 1.0);
         } else {
            this.toucan.getNavigation().moveTo(this.x, this.y, this.z, 1.0);
         }

         if (!this.flightTarget && EntityToucan.this.isFlying() && this.toucan.onGround()) {
            this.toucan.setFlying(false);
         }

         if (EntityToucan.this.isFlying() && this.toucan.onGround() && this.toucan.timeFlying > 10) {
            this.toucan.setFlying(false);
         }
      }

      @Nullable
      protected Vec3 getPosition() {
         Vec3 vector3d = this.toucan.position();
         if (this.toucan.isOverWaterOrVoid()) {
            this.flightTarget = true;
         }

         if (this.flightTarget) {
            if (this.toucan.timeFlying > 50 && this.toucan.isOverLeaves() && !this.toucan.onGround()) {
               return this.toucan.getBlockGrounding(vector3d);
            } else {
               return this.toucan.timeFlying >= 200 && !this.toucan.isOverWaterOrVoid()
                  ? this.toucan.getBlockGrounding(vector3d)
                  : this.toucan.getBlockInViewAway(vector3d, 0.0F);
            }
         } else if (!this.toucan.onGround()) {
            return this.toucan.getBlockGrounding(vector3d);
         } else {
            if (this.toucan.isOverLeaves()) {
               for (int i = 0; i < 15; i++) {
                  BlockPos pos = this.toucan
                     .blockPosition()
                     .offset(EntityToucan.this.random.nextInt(16) - 8, EntityToucan.this.random.nextInt(8) - 4, EntityToucan.this.random.nextInt(16) - 8);
                  if (!this.toucan.level().getBlockState(pos.above()).isSolid()
                     && this.toucan.level().getBlockState(pos).isSolid()
                     && this.toucan.getWalkTargetValue(pos) >= 0.0F) {
                     return Vec3.atBottomCenterOf(pos);
                  }
               }
            }

            return LandRandomPos.getPos(this.toucan, 16, 7);
         }
      }

      public boolean canContinueToUse() {
         if (this.toucan.aiItemFlag) {
            return false;
         } else {
            return this.flightTarget
               ? this.toucan.isFlying() && this.toucan.distanceToSqr(this.x, this.y, this.z) > 2.0
               : !this.toucan.getNavigation().isDone() && !this.toucan.isVehicle();
         }
      }

      public void start() {
         if (this.flightTarget) {
            this.toucan.setFlying(true);
            this.toucan.getMoveControl().setWantedPosition(this.x, this.y, this.z, 1.0);
         } else {
            this.toucan.getNavigation().moveTo(this.x, this.y, this.z, 1.0);
         }
      }

      public void stop() {
         this.toucan.getNavigation().stop();
         super.stop();
      }
   }
}
