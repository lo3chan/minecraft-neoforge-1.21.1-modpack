package com.aetherteam.aether.entity.passive;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.api.registers.MoaType;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import com.aetherteam.aether.client.AetherSoundEvents;
import com.aetherteam.aether.client.renderer.AetherOverlays;
import com.aetherteam.aether.data.resources.registries.AetherMoaTypes;
import com.aetherteam.aether.effect.AetherEffects;
import com.aetherteam.aether.entity.EntityUtil;
import com.aetherteam.aether.entity.WingedBird;
import com.aetherteam.aether.entity.ai.attribute.AetherAttributes;
import com.aetherteam.aether.entity.ai.goal.ContinuousMeleeAttackGoal;
import com.aetherteam.aether.entity.ai.goal.FallingRandomStrollGoal;
import com.aetherteam.aether.entity.ai.goal.MoaFollowGoal;
import com.aetherteam.aether.entity.ai.navigator.FallPathNavigation;
import com.aetherteam.aether.entity.monster.AechorPlant;
import com.aetherteam.aether.entity.monster.Swet;
import com.aetherteam.aether.event.AetherEventDispatch;
import com.aetherteam.aether.event.EggLayEvent;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.item.miscellaneous.MoaEggItem;
import com.aetherteam.aether.network.packet.clientbound.MoaInteractPacket;
import com.aetherteam.aether.perk.data.ServerPerkData;
import com.aetherteam.aether.perk.types.MoaData;
import com.aetherteam.nitrogen.attachment.INBTSynchable.Direction;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.AgeableMob.AgeableMobGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Swim;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

public class Moa extends MountableAnimal implements WingedBird {
   private static final EntityDataAccessor<Optional<UUID>> DATA_MOA_UUID_ID = SynchedEntityData.defineId(Moa.class, EntityDataSerializers.OPTIONAL_UUID);
   private static final EntityDataAccessor<String> DATA_MOA_TYPE_ID = SynchedEntityData.defineId(Moa.class, EntityDataSerializers.STRING);
   private static final EntityDataAccessor<Optional<UUID>> DATA_RIDER_UUID = SynchedEntityData.defineId(Moa.class, EntityDataSerializers.OPTIONAL_UUID);
   private static final EntityDataAccessor<Optional<UUID>> DATA_LAST_RIDER_UUID = SynchedEntityData.defineId(Moa.class, EntityDataSerializers.OPTIONAL_UUID);
   private static final EntityDataAccessor<Integer> DATA_REMAINING_JUMPS_ID = SynchedEntityData.defineId(Moa.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> DATA_HUNGRY_ID = SynchedEntityData.defineId(Moa.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> DATA_AMOUNT_FED_ID = SynchedEntityData.defineId(Moa.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> DATA_PLAYER_GROWN_ID = SynchedEntityData.defineId(Moa.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_SITTING_ID = SynchedEntityData.defineId(Moa.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Optional<UUID>> DATA_FOLLOWING_ID = SynchedEntityData.defineId(Moa.class, EntityDataSerializers.OPTIONAL_UUID);
   private static final HashMap<ResourceLocation, ResourceLocation> ID_TEXTURE_MAP = new HashMap<>();
   private float wingRotation;
   private float prevWingRotation;
   private float destPos;
   private float prevDestPos;
   private int jumpCooldown;
   private int flapCooldown;
   private int eggTime = this.getEggTime();

   public Moa(EntityType<? extends Moa> type, Level level) {
      super(type, level);
      this.setPathfindingMalus(PathType.DANGER_FIRE, -1.0F);
      this.setPathfindingMalus(PathType.DAMAGE_FIRE, -1.0F);
      this.setPathfindingMalus(PathType.DANGER_POWDER_SNOW, -1.0F);
      this.setPathfindingMalus(PathType.POWDER_SNOW, -1.0F);
      this.setPathfindingMalus(PathType.DANGER_OTHER, -1.0F);
      this.setPathfindingMalus(PathType.DAMAGE_OTHER, -1.0F);
      this.setPathfindingMalus(PathType.LAVA, -1.0F);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new PanicGoal(this, 0.65));
      this.goalSelector.addGoal(2, new MoaFollowGoal(this, 1.0));
      this.goalSelector.addGoal(3, new ContinuousMeleeAttackGoal(this, 1.0, true));
      this.goalSelector.addGoal(4, new FallingRandomStrollGoal(this, 0.35));
      this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
      this.targetSelector
         .addGoal(
            1,
            new NearestAttackableTargetGoal(
               this,
               Swet.class,
               false,
               livingEntity -> this.getFollowing() == null && this.isPlayerGrown() && !this.isBaby() && livingEntity instanceof Swet swet && !swet.isFriendly()
            )
         );
      this.targetSelector
         .addGoal(
            2,
            new NearestAttackableTargetGoal(
               this, AechorPlant.class, false, livingEntity -> this.getFollowing() == null && this.isPlayerGrown() && !this.isBaby()
            )
         );
   }

   protected PathNavigation createNavigation(Level level) {
      return new FallPathNavigation(this, level);
   }

   public static Builder createMobAttributes() {
      return Mob.createMobAttributes()
         .add(Attributes.MAX_HEALTH, 35.0)
         .add(Attributes.MOVEMENT_SPEED, 1.0)
         .add(Attributes.FOLLOW_RANGE, 16.0)
         .add(Attributes.ATTACK_DAMAGE, 5.0)
         .add(AetherAttributes.MOA_MAX_JUMPS, -1.0);
   }

   @Override
   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DATA_MOA_UUID_ID, Optional.empty());
      builder.define(DATA_MOA_TYPE_ID, "");
      builder.define(DATA_RIDER_UUID, Optional.empty());
      builder.define(DATA_LAST_RIDER_UUID, Optional.empty());
      builder.define(DATA_REMAINING_JUMPS_ID, 0);
      builder.define(DATA_HUNGRY_ID, false);
      builder.define(DATA_AMOUNT_FED_ID, 0);
      builder.define(DATA_PLAYER_GROWN_ID, false);
      builder.define(DATA_SITTING_ID, false);
      builder.define(DATA_FOLLOWING_ID, Optional.empty());
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData) {
      this.generateMoaUUID();
      if (spawnData == null) {
         spawnData = new AgeableMobGroupData(false);
      }

      if (this.getMoaType() == null) {
         MoaType moaType = AetherMoaTypes.getWeightedChance(level.registryAccess(), this.getRandom());
         ResourceKey<MoaType> moaTypeKey = AetherMoaTypes.getResourceKey(level.registryAccess(), moaType);
         if (moaTypeKey != null) {
            this.setMoaTypeByKey(moaTypeKey);
         }
      }

      if (this.getMoaType() == null) {
         this.setMoaTypeByKey(AetherMoaTypes.BLUE);
      }

      return super.finalizeSpawn(level, difficulty, reason, spawnData);
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> dataAccessor) {
      if (DATA_SITTING_ID.equals(dataAccessor)) {
         this.refreshDimensions();
      }

      super.onSyncedDataUpdated(dataAccessor);
   }

   public void aiStep() {
      super.aiStep();
      this.animateWings();
   }

   @Override
   public void tick() {
      super.tick();
      AttributeInstance gravity = this.getAttribute(Attributes.GRAVITY);
      if (gravity != null) {
         double max = this.isVehicle() ? -0.5 : -0.1;
         double fallSpeed = Math.max(gravity.getValue() * -1.25, max);
         if (this.getDeltaMovement().y() < fallSpeed && !this.playerTriedToCrouch()) {
            this.setDeltaMovement(this.getDeltaMovement().x(), fallSpeed, this.getDeltaMovement().z());
            this.hasImpulse = true;
            this.setEntityOnGround(false);
         }
      }

      if (this.onGround()) {
         this.setRemainingJumps(this.getMaxJumps());
      }

      if (this.getJumpCooldown() > 0) {
         this.setJumpCooldown(this.getJumpCooldown() - 1);
         this.setPlayerJumped(false);
      } else if (this.getJumpCooldown() == 0) {
         this.setMountJumping(false);
      }

      if (!this.level().isClientSide() && this.isAlive()) {
         if (this.getRandom().nextInt(900) == 0 && this.deathTime == 0) {
            this.heal(1.0F);
         }

         if (!this.isBaby() && this.getPassengers().isEmpty() && --this.eggTime <= 0) {
            MoaType moaType = this.getMoaType();
            if (moaType != null) {
               EggLayEvent eggLayEvent = AetherEventDispatch.onLayEgg(
                  this,
                  (SoundEvent)AetherSoundEvents.ENTITY_MOA_EGG.get(),
                  1.0F,
                  (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.2F + 1.0F,
                  this.getMoaType().egg()
               );
               if (!eggLayEvent.isCanceled()) {
                  if (eggLayEvent.getSound() != null) {
                     this.playSound(eggLayEvent.getSound(), eggLayEvent.getVolume(), eggLayEvent.getPitch());
                  }

                  if (eggLayEvent.getItem() != null) {
                     this.spawnAtLocation(eggLayEvent.getItem());
                  }
               }
            }

            this.eggTime = this.getEggTime();
         }
      }

      if (this.isBaby()) {
         if (!this.isHungry()) {
            if (!this.level().isClientSide() && this.getRandom().nextInt(2000) == 0) {
               this.setHungry(true);
            }
         } else if (this.getRandom().nextInt(10) == 0) {
            this.level()
               .addParticle(
                  ParticleTypes.ANGRY_VILLAGER,
                  this.getX() + (this.getRandom().nextDouble() - 0.5) * this.getBbWidth(),
                  this.getY() + 1.0,
                  this.getZ() + (this.getRandom().nextDouble() - 0.5) * this.getBbWidth(),
                  0.0,
                  0.0,
                  0.0
               );
         }
      } else {
         this.setHungry(false);
         this.setAmountFed(0);
      }

      if (this.getControllingPassenger() instanceof Player player) {
         if (this.getRider() == null) {
            this.setRider(player.getUUID());
         }
      } else if (this.getRider() != null) {
         this.setRider(null);
      }

      if (this.getFlapCooldown() > 0) {
         this.setFlapCooldown(this.getFlapCooldown() - 1);
      } else if (this.getFlapCooldown() == 0 && !this.onGround() && !Swim.shouldSwim(this)) {
         this.level()
            .playSound(
               null,
               this,
               (SoundEvent)AetherSoundEvents.ENTITY_MOA_FLAP.get(),
               SoundSource.NEUTRAL,
               0.15F,
               Mth.clamp(this.getRandom().nextFloat(), 0.7F, 1.0F) + Mth.clamp(this.getRandom().nextFloat(), 0.0F, 0.3F)
            );
         this.setFlapCooldown(15);
      }

      this.checkSlowFallDistance();
   }

   protected void addPassenger(Entity passenger) {
      if (passenger instanceof Player player) {
         this.generateMoaUUID();
         if (this.getLastRider() == null || this.getLastRider() != player.getUUID()) {
            this.setLastRider(player.getUUID());
         }

         if (!player.level().isClientSide()) {
            ((AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER))
               .setSynched(player.getId(), Direction.CLIENT, "setLastRiddenMoa", this.getMoaUUID());
            Map<UUID, MoaData> userSkinsData = ServerPerkData.MOA_SKIN_INSTANCE.getServerPerkData(player.getServer());
            if (userSkinsData.containsKey(this.getLastRider())) {
               ServerPerkData.MOA_SKIN_INSTANCE
                  .applyPerkWithVerification(
                     player.getServer(), this.getLastRider(), new MoaData(this.getMoaUUID(), userSkinsData.get(this.getLastRider()).moaSkin())
                  );
            }
         }
      }

      super.addPassenger(passenger);
   }

   @Override
   public void travel(Vec3 vector) {
      if (!this.isSitting()) {
         super.travel(vector);
      } else if (this.isAlive()) {
         LivingEntity entity = this.getControllingPassenger();
         if (this.isVehicle() && this.isSaddled() && entity != null) {
            EntityUtil.copyRotations(this, entity);
            if (this.isControlledByLocalInstance()) {
               this.travelWithInput(new Vec3(0.0, vector.y(), 0.0));
               this.lerpSteps = 0;
            } else {
               this.calculateEntityAnimation(false);
               this.setDeltaMovement(Vec3.ZERO);
            }
         } else {
            this.travelWithInput(new Vec3(0.0, vector.y(), 0.0));
         }
      }
   }

   @Override
   public void onJump(Mob mob) {
      super.onJump(mob);
      this.setJumpCooldown(10);
      if (!this.onGround() && !Swim.shouldSwim(this)) {
         this.setRemainingJumps(this.getRemainingJumps() - 1);
         this.spawnExplosionParticle();
      }

      this.setFlapCooldown(0);
   }

   @Override
   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack itemStack = player.getItemInHand(hand);
      if (this.isPlayerGrown() && itemStack.is((Item)AetherItems.NATURE_STAFF.get())) {
         if (player.isShiftKeyDown()) {
            if (this.getFollowing() == null) {
               this.setFollowing(player.getUUID());
               this.navigation.recomputePath();
            } else {
               this.setFollowing(null);
            }
         } else {
            itemStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
            this.setSitting(!this.isSitting());
            this.spawnExplosionParticle();
         }

         return InteractionResult.sidedSuccess(this.level().isClientSide());
      } else if (!this.level().isClientSide()
         && this.isPlayerGrown()
         && this.isBaby()
         && this.isHungry()
         && this.getAmountFed() < 3
         && itemStack.is(AetherTags.Items.MOA_FOOD_ITEMS)) {
         if (!player.getAbilities().instabuild) {
            itemStack.shrink(1);
         }

         this.setAmountFed(this.getAmountFed() + 1);
         switch (this.getAmountFed()) {
            case 0:
               this.setAge(-24000);
               break;
            case 1:
               this.setAge(-16000);
               break;
            case 2:
               this.setAge(-8000);
               break;
            case 3:
               this.setBaby(false);
         }

         if (this.getAmountFed() > 3 && !this.isBaby()) {
            this.setBaby(false);
         }

         this.setHungry(false);
         PacketDistributor.sendToAllPlayers(new MoaInteractPacket(player.getId(), hand == InteractionHand.MAIN_HAND), new CustomPacketPayload[0]);
         return InteractionResult.CONSUME;
      } else if (this.isPlayerGrown() && !this.isBaby() && this.getHealth() < this.getMaxHealth() && itemStack.is(AetherTags.Items.MOA_FOOD_ITEMS)) {
         if (!player.getAbilities().instabuild) {
            itemStack.shrink(1);
         }

         this.heal(5.0F);
         return InteractionResult.sidedSuccess(this.level().isClientSide());
      } else {
         return super.mobInteract(player, hand);
      }
   }

   public void spawnExplosionParticle() {
      for (int i = 0; i < 20; i++) {
         EntityUtil.spawnMovementExplosionParticles(this);
      }
   }

   public void generateMoaUUID() {
      if (this.getMoaUUID() == null) {
         this.setMoaUUID(UUID.randomUUID());
      }
   }

   @Nullable
   public UUID getMoaUUID() {
      return (UUID)((Optional)this.getEntityData().get(DATA_MOA_UUID_ID)).orElse(null);
   }

   private void setMoaUUID(@Nullable UUID uuid) {
      this.getEntityData().set(DATA_MOA_UUID_ID, Optional.ofNullable(uuid));
   }

   @Nullable
   public MoaType getMoaType() {
      return AetherMoaTypes.getMoaType(this.level().registryAccess(), (String)this.getEntityData().get(DATA_MOA_TYPE_ID));
   }

   @Nullable
   public ResourceKey<MoaType> getMoaTypeKey() {
      return AetherMoaTypes.getResourceKey(this.level().registryAccess(), (String)this.getEntityData().get(DATA_MOA_TYPE_ID));
   }

   public void setMoaTypeByKey(ResourceKey<MoaType> moaType) {
      this.getEntityData().set(DATA_MOA_TYPE_ID, moaType.location().toString());
   }

   @Nullable
   public UUID getRider() {
      return (UUID)((Optional)this.getEntityData().get(DATA_RIDER_UUID)).orElse(null);
   }

   public void setRider(@Nullable UUID uuid) {
      this.getEntityData().set(DATA_RIDER_UUID, Optional.ofNullable(uuid));
   }

   @Nullable
   public UUID getLastRider() {
      return (UUID)((Optional)this.getEntityData().get(DATA_LAST_RIDER_UUID)).orElse(null);
   }

   public void setLastRider(@Nullable UUID uuid) {
      this.getEntityData().set(DATA_LAST_RIDER_UUID, Optional.ofNullable(uuid));
   }

   public int getRemainingJumps() {
      return (Integer)this.getEntityData().get(DATA_REMAINING_JUMPS_ID);
   }

   public void setRemainingJumps(int remainingJumps) {
      this.getEntityData().set(DATA_REMAINING_JUMPS_ID, remainingJumps);
   }

   public boolean isHungry() {
      return (Boolean)this.getEntityData().get(DATA_HUNGRY_ID);
   }

   public void setHungry(boolean hungry) {
      this.getEntityData().set(DATA_HUNGRY_ID, hungry);
   }

   public int getAmountFed() {
      return (Integer)this.getEntityData().get(DATA_AMOUNT_FED_ID);
   }

   public void setAmountFed(int amountFed) {
      this.getEntityData().set(DATA_AMOUNT_FED_ID, amountFed);
   }

   public boolean isPlayerGrown() {
      return (Boolean)this.getEntityData().get(DATA_PLAYER_GROWN_ID);
   }

   public void setPlayerGrown(boolean playerGrown) {
      this.getEntityData().set(DATA_PLAYER_GROWN_ID, playerGrown);
   }

   public boolean isSitting() {
      return (Boolean)this.getEntityData().get(DATA_SITTING_ID);
   }

   public void setSitting(boolean isSitting) {
      this.getEntityData().set(DATA_SITTING_ID, isSitting);
   }

   @Nullable
   public UUID getFollowing() {
      return (UUID)((Optional)this.getEntityData().get(DATA_FOLLOWING_ID)).orElse(null);
   }

   public void setFollowing(@Nullable UUID uuid) {
      this.getEntityData().set(DATA_FOLLOWING_ID, Optional.ofNullable(uuid));
   }

   @Override
   public float getWingRotation() {
      return this.wingRotation;
   }

   @Override
   public void setWingRotation(float rotation) {
      this.wingRotation = rotation;
   }

   @Override
   public float getPrevWingRotation() {
      return this.prevWingRotation;
   }

   @Override
   public void setPrevWingRotation(float rotation) {
      this.prevWingRotation = rotation;
   }

   @Override
   public float getWingDestPos() {
      return this.destPos;
   }

   @Override
   public void setWingDestPos(float pos) {
      this.destPos = pos;
   }

   @Override
   public float getPrevWingDestPos() {
      return this.prevDestPos;
   }

   @Override
   public void setPrevWingDestPos(float pos) {
      this.prevDestPos = pos;
   }

   public int getJumpCooldown() {
      return this.jumpCooldown;
   }

   public void setJumpCooldown(int jumpCooldown) {
      this.jumpCooldown = jumpCooldown;
   }

   public int getFlapCooldown() {
      return this.flapCooldown;
   }

   public void setFlapCooldown(int flapCooldown) {
      this.flapCooldown = flapCooldown;
   }

   public static void registerJumpOverlayTextureOverride(ResourceLocation attributeId, ResourceLocation location) {
      ID_TEXTURE_MAP.put(attributeId, location);
   }

   public static void registerJumpOverlayTextureOverride(AttributeModifier attribute, ResourceLocation location) {
      registerJumpOverlayTextureOverride(attribute.id(), location);
   }

   public ResourceLocation getOverlayTexture(ResourceLocation attributeId) {
      ResourceLocation location = ID_TEXTURE_MAP.get(attributeId);
      return location == null ? AetherOverlays.getDefaultJumpsTexture(this.getMoaType()) : location;
   }

   protected SoundEvent getAmbientSound() {
      return (SoundEvent)AetherSoundEvents.ENTITY_MOA_AMBIENT.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return (SoundEvent)AetherSoundEvents.ENTITY_MOA_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)AetherSoundEvents.ENTITY_MOA_DEATH.get();
   }

   @Override
   protected SoundEvent getSaddledSound() {
      return (SoundEvent)AetherSoundEvents.ENTITY_MOA_SADDLE.get();
   }

   protected void playStepSound(BlockPos pos, BlockState state) {
      this.playSound((SoundEvent)AetherSoundEvents.ENTITY_MOA_STEP.get(), 0.15F, 1.0F);
   }

   public int getMaxJumps() {
      AttributeInstance attribute = this.getAttribute(AetherAttributes.MOA_MAX_JUMPS);
      int defaultValue = this.getMoaType() != null ? this.getMoaType().maxJumps() : 3;
      if (attribute != null) {
         if ((int)attribute.getBaseValue() != defaultValue) {
            attribute.setBaseValue(defaultValue);
         }

         return (int)attribute.getValue();
      } else {
         return defaultValue;
      }
   }

   public int getEggTime() {
      return this.random.nextInt(6000) + 6000;
   }

   public boolean isFood(ItemStack stack) {
      return false;
   }

   public boolean canBeAffected(MobEffectInstance effect) {
      return (effect.getEffect().value() != AetherEffects.INEBRIATION.get() || !this.isPlayerGrown()) && super.canBeAffected(effect);
   }

   public float getSpeed() {
      if (this.isVehicle() && this.isSaddled()) {
         return this.getSteeringSpeed();
      } else {
         MoaType moaType = this.getMoaType();
         return moaType != null ? (float)(this.getAttributeValue(Attributes.MOVEMENT_SPEED) * moaType.speed()) : 0.155F;
      }
   }

   @Override
   public boolean canJump() {
      return this.getRemainingJumps() > 0 && this.getJumpCooldown() == 0 || this.onGround() || Swim.shouldSwim(this);
   }

   @Override
   public boolean isSaddleable() {
      return super.isSaddleable() && this.isPlayerGrown();
   }

   @Override
   public double getMountJumpStrength() {
      return this.onGround() ? 0.95 : (Swim.shouldSwim(this) ? 0.35 : 0.9);
   }

   @Override
   public float getSteeringSpeed() {
      MoaType moaType = this.getMoaType();
      return moaType != null ? (float)(this.getAttributeValue(Attributes.MOVEMENT_SPEED) * moaType.speed()) : 0.155F;
   }

   @Override
   public float getFlyingSpeed() {
      return this.isVehicle() && this.isSaddled() ? this.getSteeringSpeed() * 0.45F : this.getSteeringSpeed() * 0.025F;
   }

   public int getMaxFallDistance() {
      return this.onGround() ? super.getMaxFallDistance() : 14;
   }

   public Vec3 getPassengerRidingPosition(Entity entity) {
      return this.isSitting() ? super.getPassengerRidingPosition(entity).add(0.0, -0.575, 0.0) : super.getPassengerRidingPosition(entity).add(0.0, -0.65, 0.0);
   }

   public float getScale() {
      return 1.0F;
   }

   public EntityDimensions getDefaultDimensions(Pose pose) {
      EntityDimensions dimensions = this.getType().getDimensions();
      if (this.isSitting()) {
         dimensions = dimensions.scale(1.0F, 0.5F);
      }

      if (this.isBaby()) {
         dimensions = dimensions.scale(1.0F, 0.5F);
      }

      return dimensions;
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob entity) {
      return null;
   }

   public boolean canBreed() {
      return false;
   }

   public void setAge(int age) {
      if (age % -8000 == 0 || age == 0 && this.getAmountFed() >= 3) {
         super.setAge(age);
      }
   }

   @Nullable
   public ItemStack getPickResult() {
      MoaEggItem moaEggItem = MoaEggItem.byId(this.getMoaTypeKey());
      return moaEggItem == null ? null : new ItemStack(moaEggItem);
   }

   @Override
   public void readAdditionalSaveData(CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      if (tag.contains("MoaUUID")) {
         this.setMoaUUID(tag.getUUID("MoaUUID"));
      }

      if (tag.contains("IsBaby")) {
         this.setBaby(tag.getBoolean("IsBaby"));
      }

      ResourceKey<MoaType> moaTypeKey = AetherMoaTypes.getResourceKey(this.level().registryAccess(), tag.getString("MoaType"));
      if (tag.contains("MoaType") && moaTypeKey != null) {
         this.setMoaTypeByKey(moaTypeKey);
      } else {
         MoaType moaType = AetherMoaTypes.getWeightedChance(this.level().registryAccess(), this.getRandom());
         ResourceKey<MoaType> randomMoaTypeKey = AetherMoaTypes.getResourceKey(this.level().registryAccess(), moaType);
         this.setMoaTypeByKey(Objects.requireNonNullElse(randomMoaTypeKey, AetherMoaTypes.BLUE));
      }

      if (tag.hasUUID("Rider")) {
         this.setRider(tag.getUUID("Rider"));
      }

      if (tag.hasUUID("LastRider")) {
         this.setLastRider(tag.getUUID("LastRider"));
      }

      if (tag.contains("RemainingJumps")) {
         this.setRemainingJumps(tag.getInt("RemainingJumps"));
      }

      if (tag.contains("Hungry")) {
         this.setHungry(tag.getBoolean("Hungry"));
      }

      if (tag.contains("AmountFed")) {
         this.setAmountFed(tag.getInt("AmountFed"));
      }

      if (tag.contains("PlayerGrown")) {
         this.setPlayerGrown(tag.getBoolean("PlayerGrown"));
      }

      if (tag.contains("Sitting")) {
         this.setSitting(tag.getBoolean("Sitting"));
      }

      if (tag.contains("Following")) {
         this.setFollowing(tag.getUUID("Following"));
      }
   }

   @Override
   public void addAdditionalSaveData(CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      if (this.getMoaUUID() != null) {
         tag.putUUID("MoaUUID", this.getMoaUUID());
      }

      tag.putBoolean("IsBaby", this.isBaby());
      tag.putString("MoaType", Objects.requireNonNullElse(this.getMoaTypeKey(), AetherMoaTypes.BLUE).location().toString());
      if (this.getRider() != null) {
         tag.putUUID("Rider", this.getRider());
      }

      if (this.getLastRider() != null) {
         tag.putUUID("LastRider", this.getLastRider());
      }

      tag.putInt("RemainingJumps", this.getRemainingJumps());
      tag.putBoolean("Hungry", this.isHungry());
      tag.putInt("AmountFed", this.getAmountFed());
      tag.putBoolean("PlayerGrown", this.isPlayerGrown());
      tag.putBoolean("Sitting", this.isSitting());
      if (this.getFollowing() != null) {
         tag.putUUID("Following", this.getFollowing());
      }
   }
}
