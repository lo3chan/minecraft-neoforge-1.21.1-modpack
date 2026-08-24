package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIFleeAdult;
import com.github.alexthe666.alexsmobs.entity.ai.CreatureAITargetItems;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.entity.ai.KomodoDragonAIBreed;
import com.github.alexthe666.alexsmobs.entity.ai.KomodoDragonAIJostle;
import com.github.alexthe666.alexsmobs.entity.ai.TameableAIFollowOwner;
import com.github.alexthe666.alexsmobs.entity.ai.TameableAITempt;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.Entity.MoveFunction;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;

public class EntityKomodoDragon extends TamableAnimal implements ITargetsDroppedItems, IFollower {
   private static final Supplier<Ingredient> TEMPTATION_ITEMS = AMCompat.lazyIngredient(() -> AMCompat.ingredientOf(AMTagRegistry.KOMODO_DRAGON_TAMEABLES));
   public int slaughterCooldown = 0;
   public int timeUntilSpit = this.random.nextInt(12000) + 24000;
   public float nextJostleAngleFromServer;
   private int riderAttackCooldown = 0;
   public static final Predicate<EntityKomodoDragon> HURT_OR_BABY = p_213616_0_ -> p_213616_0_.isBaby()
      || p_213616_0_.getHealth() <= 0.7F * p_213616_0_.getMaxHealth();
   protected static final EntityDimensions JOSTLING_SIZE = EntityDimensions.scalable(1.35F, 1.85F);
   private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.defineId(EntityKomodoDragon.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> JOSTLING = SynchedEntityData.defineId(EntityKomodoDragon.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Float> JOSTLE_ANGLE = SynchedEntityData.defineId(EntityKomodoDragon.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Optional<UUID>> JOSTLER_UUID = SynchedEntityData.defineId(
      EntityKomodoDragon.class, EntityDataSerializers.OPTIONAL_UUID
   );
   private static final EntityDataAccessor<Boolean> SADDLED = SynchedEntityData.defineId(EntityKomodoDragon.class, EntityDataSerializers.BOOLEAN);
   public float prevJostleAngle;
   public float prevJostleProgress;
   public float jostleProgress;
   public float prevSitProgress;
   public float sitProgress;
   public boolean jostleDirection;
   public int jostleTimer = 0;
   public boolean instantlyTriggerJostleAI = false;
   public int jostleCooldown = 100 + this.random.nextInt(40);
   private boolean hasJostlingSize;

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(COMMAND, 0);
      builder.define(JOSTLING, false);
      builder.define(SADDLED, false);
      builder.define(JOSTLE_ANGLE, 0.0F);
      builder.define(JOSTLER_UUID, Optional.empty());
   }

   public int getCommand() {
      return (Integer)this.entityData.get(COMMAND);
   }

   public void setCommand(int command) {
      this.entityData.set(COMMAND, command);
   }

   public static <T extends Mob> boolean canKomodoDragonSpawn(
      EntityType<? extends Animal> animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random
   ) {
      boolean spawnBlock = worldIn.getBlockState(pos.below()).is(AMTagRegistry.KOMODO_DRAGON_SPAWNS);
      return spawnBlock && worldIn.getRawBrightness(pos, 0) > 8;
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.komodoDragonSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
      this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 2.0, false));
      this.goalSelector.addGoal(3, new TameableAIFollowOwner(this, 1.2, 6.0F, 3.0F, false));
      this.goalSelector.addGoal(4, new KomodoDragonAIJostle(this));
      this.goalSelector.addGoal(5, new TameableAITempt(this, 1.1, TEMPTATION_ITEMS.get(), false));
      this.goalSelector.addGoal(5, new AnimalAIFleeAdult(this, 1.25, 32.0));
      this.goalSelector.addGoal(6, new KomodoDragonAIBreed(this, 1.0));
      this.goalSelector.addGoal(6, new RandomStrollGoal(this, 1.0, 50));
      this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
      this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
      this.targetSelector.addGoal(3, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(4, new CreatureAITargetItems(this, false));
      this.targetSelector.addGoal(6, new NearestAttackableTargetGoal(this, EntityKomodoDragon.class, 50, true, false, AMCompat.selector(HURT_OR_BABY)));
      this.targetSelector.addGoal(7, new NearestAttackableTargetGoal(this, Player.class, 150, true, true, null));
      this.targetSelector
         .addGoal(
            8,
            new EntityAINearestTarget3D<LivingEntity>(
               this, LivingEntity.class, 180, false, true, AMEntityRegistry.buildPredicateFromTag(AMTagRegistry.KOMODO_DRAGON_TARGETS)
            )
         );
   }

   protected Vec3 getRiddenInput(Player player, Vec3 deltaIn) {
      if (player.zza != 0.0F) {
         float f = player.zza < 0.0F ? 0.5F : 1.0F;
         return new Vec3(player.xxa * 0.25F, 0.0, player.zza * 0.5F * f);
      } else {
         this.setSprinting(false);
         return Vec3.ZERO;
      }
   }

   protected void tickRidden(Player player, Vec3 vec3) {
      super.tickRidden(player, vec3);
      if (player.zza != 0.0F || player.xxa != 0.0F) {
         this.setRot(player.getYRot(), player.getXRot() * 0.25F);
         this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
         AMCompat.setMaxUpStep(this, 1.0F);
         this.getNavigation().stop();
         this.setTarget(null);
         this.setSprinting(true);
      }
   }

   protected float getRiddenSpeed(Player rider) {
      return (float)(this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 2.0);
   }

   public boolean hurt(DamageSource source, float amount) {
      if (AMCompat.isInvulnerableTo(this, source)) {
         return false;
      } else {
         Entity entity = source.getEntity();
         this.setOrderedToSit(false);
         if (entity != null && this.isTame() && !(entity instanceof Player) && !(entity instanceof AbstractArrow)) {
            amount = (amount + 1.0F) / 3.0F;
         }

         return super.hurt(source, amount);
      }
   }

   protected SoundEvent getAmbientSound() {
      return AMSoundRegistry.KOMODO_DRAGON_IDLE.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.KOMODO_DRAGON_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.KOMODO_DRAGON_HURT.get();
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      if (AMCompat.contains(compound, "SpitTime")) {
         this.timeUntilSpit = AMCompat.getInt(compound, "SpitTime");
      }

      this.setCommand(AMCompat.getInt(compound, "KomodoCommand"));
      this.jostleCooldown = AMCompat.getInt(compound, "JostlingCooldown");
      this.setSaddled(AMCompat.getBoolean(compound, "Saddle"));
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putInt("SpitTime", this.timeUntilSpit);
      compound.putInt("KomodoCommand", this.getCommand());
      compound.putBoolean("Saddle", this.isSaddled());
      compound.putInt("JostlingCooldown", this.jostleCooldown);
   }

   public boolean isFood(ItemStack stack) {
      Item item = stack.getItem();
      return this.isTame() && stack.is(AMTagRegistry.KOMODO_DRAGON_BREEDABLES);
   }

   public void tick() {
      this.prevJostleAngle = this.getJostleAngle();
      super.tick();
      this.prevJostleProgress = this.jostleProgress;
      this.prevSitProgress = this.sitProgress;
      if (this.slaughterCooldown > 0) {
         this.slaughterCooldown--;
      }

      if (!this.level().isClientSide() && this.isAlive() && !this.isBaby() && --this.timeUntilSpit <= 0) {
         AMCompat.spawnAtLocation(this, (ItemLike)AMItemRegistry.KOMODO_SPIT.get());
         this.timeUntilSpit = this.random.nextInt(12000) + 24000;
      }

      if (this.riderAttackCooldown > 0) {
         this.riderAttackCooldown--;
      }

      if (this.getControllingPassenger() != null && this.getControllingPassenger() instanceof Player) {
         Player rider = (Player)this.getControllingPassenger();
         if (rider.getLastHurtMob() != null && this.distanceTo(rider.getLastHurtMob()) < this.getBbWidth() + 3.0F && !this.isAlliedTo(rider.getLastHurtMob())) {
            UUID preyUUID = rider.getLastHurtMob().getUUID();
            if (!this.getUUID().equals(preyUUID) && this.riderAttackCooldown == 0) {
               AMCompat.doHurtTarget(this, rider.getLastHurtMob());
               this.riderAttackCooldown = 20;
            }
         }
      }

      if (!this.hasJostlingSize && this.isJostling()) {
         this.refreshDimensions();
         this.hasJostlingSize = true;
      }

      if (this.hasJostlingSize && !this.isJostling()) {
         this.refreshDimensions();
         this.hasJostlingSize = false;
      }

      if (this.isJostling()) {
         if (this.jostleProgress < 5.0F) {
            this.jostleProgress++;
         }
      } else if (this.jostleProgress > 0.0F) {
         this.jostleProgress--;
      }

      if (this.isOrderedToSit()) {
         if (this.sitProgress < 5.0F) {
            this.sitProgress++;
         }
      } else if (this.sitProgress > 0.0F) {
         this.sitProgress--;
      }

      if (this.getCommand() == 2 && !this.isVehicle()) {
         this.setOrderedToSit(true);
      } else {
         this.setOrderedToSit(false);
      }

      if (this.jostleCooldown > 0) {
         this.jostleCooldown--;
      }

      if (!this.level().isClientSide()) {
         if (this.getJostleAngle() < this.nextJostleAngleFromServer) {
            this.setJostleAngle(this.getJostleAngle() + 1.0F);
         }

         if (this.getJostleAngle() > this.nextJostleAngleFromServer) {
            this.setJostleAngle(this.getJostleAngle() - 1.0F);
         }
      }
   }

   public EntityDimensions getDefaultDimensions(Pose poseIn) {
      return this.isJostling() && !this.isBaby() ? JOSTLING_SIZE.scale(this.getScale()) : super.getDefaultDimensions(poseIn);
   }

   public boolean isAlliedTo(Entity entityIn) {
      if (this.isTame()) {
         LivingEntity livingentity = this.getOwner();
         if (entityIn == livingentity) {
            return true;
         }

         if (entityIn instanceof TamableAnimal) {
            return ((TamableAnimal)entityIn).isOwnedBy(livingentity);
         }

         if (livingentity != null) {
            return livingentity.isAlliedTo(entityIn);
         }
      }

      return super.isAlliedTo(entityIn);
   }

   public boolean doHurtTarget(Entity entityIn) {
      if (super.doHurtTarget(entityIn)) {
         if (entityIn instanceof LivingEntity) {
            int i = 5;
            if (this.level().getDifficulty() == Difficulty.NORMAL) {
               i = 10;
            } else if (this.level().getDifficulty() == Difficulty.HARD) {
               i = 20;
            }

            ((LivingEntity)entityIn).addEffect(new MobEffectInstance(MobEffects.POISON, i * 20, 0));
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean canBeAffected(MobEffectInstance potioneffectIn) {
      return potioneffectIn.getEffect() == MobEffects.POISON ? false : super.canBeAffected(potioneffectIn);
   }

   @Nullable
   public LivingEntity getControllingPassenger() {
      for (Entity passenger : this.getPassengers()) {
         if (passenger instanceof Player player) {
            return player;
         }
      }

      return null;
   }

   public void positionRider(Entity passenger, MoveFunction moveFunc) {
      if (this.hasPassenger(passenger)) {
         float radius = 0.0F;
         float angle = 0.017453292F * this.yBodyRot;
         double extraX = radius * Mth.sin(3.1415927F + angle);
         double extraZ = radius * Mth.cos(angle);
         passenger.setPos(
            this.getX() + extraX, this.getY() + this.getPassengersRidingOffset() + AMPlatform.myRidingOffset(passenger, this), this.getZ() + extraZ
         );
      }
   }

   public double getPassengersRidingOffset() {
      float f = Math.min(0.25F, this.walkAnimation.speed());
      float f1 = this.walkAnimation.position();
      return this.getBbHeight() - 0.2 + 0.12F * Mth.cos(f1 * 0.7F) * 0.7F * f;
   }

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      InteractionResult type = super.mobInteract(player, hand);
      if (itemstack.is(AMTagRegistry.KOMODO_DRAGON_TAMEABLES)) {
         if (!this.isTame()) {
            int size = itemstack.getCount();
            int tameAmount = 58 + this.random.nextInt(16);
            if (size > tameAmount) {
               this.tame(player);
            }

            itemstack.shrink(size);
            return InteractionResult.SUCCESS;
         }

         if (this.getHealth() <= this.getMaxHealth()) {
            this.usePlayerItem(player, hand, itemstack);
            this.heal(10.0F);
            return InteractionResult.SUCCESS;
         }
      }

      InteractionResult interactionresult = itemstack.interactLivingEntity(player, this, hand);
      if (interactionresult == InteractionResult.SUCCESS || type == InteractionResult.SUCCESS || !this.isTame() || !this.isOwnedBy(player)) {
         return type;
      } else if (this.isFood(itemstack)) {
         this.setInLoveTime(600);
         this.usePlayerItem(player, hand, itemstack);
         return InteractionResult.SUCCESS;
      } else if (itemstack.getItem() == Items.SADDLE && !this.isSaddled()) {
         this.usePlayerItem(player, hand, itemstack);
         this.setSaddled(true);
         return InteractionResult.SUCCESS;
      } else if (itemstack.is(net.neoforged.neoforge.common.Tags.Items.TOOLS_SHEAR) && this.isSaddled()) {
         this.setSaddled(false);
         AMCompat.spawnAtLocation(this, Items.SADDLE);
         return InteractionResult.SUCCESS;
      } else if (!player.isShiftKeyDown() && !this.isBaby() && this.isSaddled()) {
         player.startRiding(this);
         return InteractionResult.SUCCESS;
      } else {
         this.setCommand((this.getCommand() + 1) % 3);
         if (this.getCommand() == 3) {
            this.setCommand(0);
         }

         player.displayClientMessage(Component.translatable("entity.alexsmobs.all.command_" + this.getCommand(), new Object[]{this.getName()}), true);
         boolean sit = this.getCommand() == 2;
         if (sit) {
            this.setOrderedToSit(true);
            return InteractionResult.SUCCESS;
         } else {
            this.setOrderedToSit(false);
            return InteractionResult.SUCCESS;
         }
      }
   }

   protected EntityKomodoDragon(EntityType type, Level worldIn) {
      super(type, worldIn);
   }

   protected float getWaterSlowDown() {
      return 0.98F;
   }

   public void setTarget(@Nullable LivingEntity entitylivingbaseIn) {
      if (!this.isBaby() || this.slaughterCooldown > 0) {
         super.setTarget(entitylivingbaseIn);
      }
   }

   public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 30.0)
         .add(Attributes.ARMOR, 0.0)
         .add(Attributes.ATTACK_DAMAGE, 4.0)
         .add(Attributes.MOVEMENT_SPEED, 0.23000000417232513);
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel p_241840_1_, AgeableMob p_241840_2_) {
      return AMCompat.create(AMEntityRegistry.KOMODO_DRAGON.get(), p_241840_1_);
   }

   @Override
   public boolean canTargetItem(ItemStack stack) {
      return stack.is(AMTagRegistry.KOMODO_DRAGON_TAMEABLES) || AMCompat.getFoodProperties(stack.getItem()) != null && AMCompat.isMeat(stack.getItem());
   }

   public boolean isSaddled() {
      return (Boolean)this.entityData.get(SADDLED);
   }

   public void setSaddled(boolean saddled) {
      this.entityData.set(SADDLED, saddled);
   }

   public boolean isJostling() {
      return (Boolean)this.entityData.get(JOSTLING);
   }

   public void setJostling(boolean jostle) {
      this.entityData.set(JOSTLING, jostle);
   }

   public float getJostleAngle() {
      return (Float)this.entityData.get(JOSTLE_ANGLE);
   }

   public void setJostleAngle(float scale) {
      this.entityData.set(JOSTLE_ANGLE, scale);
   }

   @Nullable
   public UUID getJostlingPartnerUUID() {
      return (UUID)((Optional)this.entityData.get(JOSTLER_UUID)).orElse(null);
   }

   public void setJostlingPartnerUUID(@Nullable UUID uniqueId) {
      this.entityData.set(JOSTLER_UUID, Optional.ofNullable(uniqueId));
   }

   @Nullable
   public Entity getJostlingPartner() {
      UUID id = this.getJostlingPartnerUUID();
      return id != null && !this.level().isClientSide() ? ((ServerLevel)this.level()).getEntity(id) : null;
   }

   public void setJostlingPartner(@Nullable Entity jostlingPartner) {
      if (jostlingPartner == null) {
         this.setJostlingPartnerUUID(null);
      } else {
         this.setJostlingPartnerUUID(jostlingPartner.getUUID());
      }
   }

   public void pushBackJostling(EntityKomodoDragon entityMoose, float strength) {
      this.applyKnockbackFromMoose(strength, entityMoose.getX() - this.getX(), entityMoose.getZ() - this.getZ());
   }

   private void applyKnockbackFromMoose(float strength, double ratioX, double ratioZ) {
      LivingKnockBackEvent event = CommonHooks.onLivingKnockBack(this, strength, ratioX, ratioZ);
      if (!event.isCanceled()) {
         strength = event.getStrength();
         ratioX = event.getRatioX();
         ratioZ = event.getRatioZ();
         if (!(strength <= 0.0F)) {
            this.hasImpulse = true;
            Vec3 vector3d = this.getDeltaMovement();
            Vec3 vector3d1 = new Vec3(ratioX, 0.0, ratioZ).normalize().scale(strength);
            this.setDeltaMovement(vector3d.x / 2.0 - vector3d1.x, 0.30000001192092896, vector3d.z / 2.0 - vector3d1.z);
         }
      }
   }

   public boolean canJostleWith(EntityKomodoDragon moose) {
      return !moose.isOrderedToSit() && !moose.isVehicle() && !moose.isBaby() && moose.getJostlingPartnerUUID() == null && moose.jostleCooldown == 0;
   }

   public void playJostleSound() {
   }

   protected void dropEquipment() {
      super.dropEquipment();
      if (this.isSaddled() && !this.level().isClientSide()) {
         AMCompat.spawnAtLocation(this, Items.SADDLE);
      }

      this.setSaddled(false);
   }

   @Override
   public void onGetItem(ItemEntity e) {
      this.heal(10.0F);
   }

   @Override
   public boolean shouldFollow() {
      return this.getCommand() == 1;
   }

   public boolean isMaid() {
      String s = ChatFormatting.stripFormatting(this.getName().getString());
      return s != null && (s.toLowerCase().contains("maid") || s.toLowerCase().contains("coda"));
   }
}
