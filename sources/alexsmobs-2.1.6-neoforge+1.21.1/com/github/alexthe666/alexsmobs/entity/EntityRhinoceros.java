package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.citadel.animation.Animation;
import com.github.alexthe666.alexsmobs.citadel.animation.AnimationHandler;
import com.github.alexthe666.alexsmobs.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AdvancedPathNavigateNoTeleport;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIHurtByTargetNotBaby;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIPanicBaby;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class EntityRhinoceros extends Animal implements IAnimatedEntity {
   public static final Animation ANIMATION_FLICK_EARS = Animation.create(20);
   public static final Animation ANIMATION_EAT_GRASS = Animation.create(35);
   public static final Animation ANIMATION_FLING = Animation.create(15);
   public static final Animation ANIMATION_SLASH = Animation.create(30);
   private static final EntityDataAccessor<String> APPLIED_POTION = SynchedEntityData.defineId(EntityRhinoceros.class, EntityDataSerializers.STRING);
   private static final EntityDataAccessor<Integer> POTION_LEVEL = SynchedEntityData.defineId(EntityRhinoceros.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> INFLICTED_COUNT = SynchedEntityData.defineId(EntityRhinoceros.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> POTION_DURATION = SynchedEntityData.defineId(EntityRhinoceros.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Optional<UUID>> DATA_TRUSTED_ID_0 = SynchedEntityData.defineId(
      EntityRhinoceros.class, EntityDataSerializers.OPTIONAL_UUID
   );
   private static final EntityDataAccessor<Optional<UUID>> DATA_TRUSTED_ID_1 = SynchedEntityData.defineId(
      EntityRhinoceros.class, EntityDataSerializers.OPTIONAL_UUID
   );
   private static final EntityDataAccessor<Boolean> ANGRY = SynchedEntityData.defineId(EntityRhinoceros.class, EntityDataSerializers.BOOLEAN);
   private static final Object2IntMap<String> potionToColor = new Object2IntOpenHashMap();
   private int animationTick;
   private Animation currentAnimation;

   protected EntityRhinoceros(EntityType type, Level level) {
      super(type, level);
      AMCompat.setMaxUpStep(this, 1.1F);
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 60.0)
         .add(Attributes.ATTACK_DAMAGE, 8.0)
         .add(Attributes.FOLLOW_RANGE, 32.0)
         .add(Attributes.MOVEMENT_SPEED, 0.25)
         .add(Attributes.ARMOR, 12.0)
         .add(Attributes.ARMOR_TOUGHNESS, 4.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.9)
         .add(Attributes.ATTACK_KNOCKBACK, 2.0);
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DATA_TRUSTED_ID_0, Optional.empty());
      builder.define(DATA_TRUSTED_ID_1, Optional.empty());
      builder.define(APPLIED_POTION, "");
      builder.define(POTION_LEVEL, 0);
      builder.define(INFLICTED_COUNT, 0);
      builder.define(POTION_DURATION, 0);
      builder.define(ANGRY, false);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.4, true));
      this.goalSelector.addGoal(2, new AnimalAIPanicBaby(this, 1.25));
      this.goalSelector.addGoal(3, new BreedGoal(this, 1.0));
      this.goalSelector
         .addGoal(4, new TemptGoal(this, 1.0, AMCompat.ingredientOfTags(AMTagRegistry.RHINOCEROS_FOODSTUFFS, AMTagRegistry.RHINOCEROS_BREEDABLES), false));
      this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.1));
      this.goalSelector.addGoal(6, new AnimalAIWanderRanged(this, 90, 1.0, 18, 7));
      this.goalSelector.addGoal(7, new EntityRhinoceros.StrollGoal(200));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 15.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new EntityRhinoceros.DefendTrustedTargetGoal(LivingEntity.class, false, false, entity -> !this.trusts(entity.getUUID())));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<Raider>(this, Raider.class, 50, true, true, null) {
         public boolean canUse() {
            return super.canUse() && !EntityRhinoceros.this.isBaby();
         }
      });
      this.targetSelector.addGoal(3, new EntityRhinoceros.AIAttackNearPlayers());
      this.targetSelector.addGoal(4, new AnimalAIHurtByTargetNotBaby(this));
   }

   protected PathNavigation createNavigation(Level worldIn) {
      return new AdvancedPathNavigateNoTeleport(this, worldIn, true);
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.rhinocerosSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   public void tick() {
      super.tick();
      AnimationHandler.INSTANCE.updateAnimations(this);
      if (!this.level().isClientSide()) {
         if (this.getAnimation() == NO_ANIMATION && (this.getTarget() == null || !this.getTarget().isAlive())) {
            if (this.getDeltaMovement().lengthSqr() < 0.03
               && this.getRandom().nextInt(500) == 0
               && this.level().getBlockState(this.blockPosition().below()).is(Blocks.GRASS_BLOCK)) {
               this.setAnimation(ANIMATION_EAT_GRASS);
            } else if (this.getRandom().nextInt(200) == 0) {
               this.setAnimation(ANIMATION_FLICK_EARS);
            }
         }

         if (this.getAnimation() == ANIMATION_EAT_GRASS
            && this.getAnimationTick() == 30
            && this.level().getBlockState(this.blockPosition().below()).is(Blocks.GRASS_BLOCK)) {
            BlockPos down = this.blockPosition().below();
            this.level().levelEvent(2001, down, Block.getId(Blocks.GRASS_BLOCK.defaultBlockState()));
            this.level().setBlock(down, Blocks.DIRT.defaultBlockState(), 2);
            this.heal(10.0F);
         }

         LivingEntity target = this.getTarget();
         if (target != null && target.isAlive()) {
            this.setAngry(this.distanceTo(target) < 20.0F);
            double dist = this.distanceTo(target);
            if (this.hasLineOfSight(target)) {
               this.lookAt(target, 30.0F, 30.0F);
               this.yBodyRot = this.getYRot();
            }

            if (dist < this.getBbWidth() + 3.0F) {
               if (this.getAnimation() == NO_ANIMATION) {
                  this.setAnimation(this.random.nextBoolean() ? ANIMATION_SLASH : ANIMATION_FLING);
               }

               if (dist < this.getBbWidth() + 1.5F && this.hasLineOfSight(target)) {
                  if (this.getAnimation() == ANIMATION_FLING && this.getAnimationTick() >= 5 && this.getAnimationTick() <= 8) {
                     float dmg = (float)this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue();
                     if (target instanceof Raider) {
                        dmg = 10.0F;
                     }

                     this.attackWithPotion(target, dmg);
                     this.launch(target, 0.0F, 1.0F);

                     for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(1.0))) {
                        if (!(entity instanceof Animal) && !this.trusts(entity.getUUID()) && entity != target) {
                           this.attackWithPotion(entity, Math.max(dmg - 5.0F, 1.0F));
                           this.launch(entity, 0.0F, 0.5F);
                        }
                     }
                  }

                  if (this.getAnimation() == ANIMATION_SLASH
                     && (this.getAnimationTick() >= 9 && this.getAnimationTick() <= 11 || this.getAnimationTick() >= 19 && this.getAnimationTick() <= 21)) {
                     float dmg = (float)this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue();
                     if (target instanceof Raider) {
                        dmg = 10.0F;
                     }

                     this.attackWithPotion(target, dmg);
                     this.launch(target, this.getAnimationTick() <= 15 ? -90.0F : 90.0F, 1.0F);

                     for (LivingEntity entityx : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(1.0))) {
                        if (!(entityx instanceof Animal) && !this.trusts(entityx.getUUID()) && entityx != target) {
                           this.attackWithPotion(entityx, Math.max(dmg - 5.0F, 1.0F));
                           this.launch(entityx, this.getAnimationTick() <= 15 ? -90.0F : 90.0F, 0.5F);
                        }
                     }
                  }
               }
            }
         } else {
            this.setAngry(false);
         }
      }
   }

   protected void playStepSound(BlockPos pos, BlockState state) {
      if (!this.isBaby()) {
         this.playSound(AMSoundRegistry.ELEPHANT_WALK.get(), 0.2F, 1.2F);
      } else {
         super.playStepSound(pos, state);
      }
   }

   protected SoundEvent getAmbientSound() {
      return AMSoundRegistry.RHINOCEROS_IDLE.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.RHINOCEROS_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.RHINOCEROS_HURT.get();
   }

   public boolean isFood(ItemStack stack) {
      return stack.is(AMTagRegistry.RHINOCEROS_BREEDABLES);
   }

   public String getAppliedPotionId() {
      return (String)this.entityData.get(APPLIED_POTION);
   }

   public void setAppliedPotionId(String potionId) {
      this.entityData.set(APPLIED_POTION, potionId);
   }

   public int getPotionColor() {
      String s = this.getAppliedPotionId();
      if (s.isEmpty()) {
         return -1;
      } else if (!potionToColor.containsKey(s)) {
         MobEffect effect = this.getPotionEffect();
         if (effect != null) {
            int color = effect.getColor();
            potionToColor.put(s, color);
            return color;
         } else {
            return -1;
         }
      } else {
         return potionToColor.getInt(s);
      }
   }

   public MobEffect getPotionEffect() {
      return (MobEffect)BuiltInRegistries.MOB_EFFECT.get(AMCompat.rl(this.getAppliedPotionId()));
   }

   public int getPotionDuration() {
      return (Integer)this.entityData.get(POTION_DURATION);
   }

   public void setPotionDuration(int time) {
      this.entityData.set(POTION_DURATION, time);
   }

   public int getPotionLevel() {
      return (Integer)this.entityData.get(POTION_LEVEL);
   }

   public void setPotionLevel(int time) {
      this.entityData.set(POTION_LEVEL, time);
   }

   public int getInflictedCount() {
      return (Integer)this.entityData.get(INFLICTED_COUNT);
   }

   public void setInflictedCount(int count) {
      this.entityData.set(INFLICTED_COUNT, count);
   }

   public void resetPotion() {
      this.setAppliedPotionId("");
      this.setPotionDuration(0);
      this.setPotionLevel(0);
      this.setInflictedCount(0);
   }

   private List<UUID> getTrustedUUIDs() {
      List<UUID> list = Lists.newArrayList();
      list.add((UUID)((Optional)this.entityData.get(DATA_TRUSTED_ID_0)).orElse((UUID)null));
      list.add((UUID)((Optional)this.entityData.get(DATA_TRUSTED_ID_1)).orElse((UUID)null));
      return list;
   }

   private void addTrustedUUID(@Nullable UUID p_28516_) {
      if (((Optional)this.entityData.get(DATA_TRUSTED_ID_0)).isPresent()) {
         this.entityData.set(DATA_TRUSTED_ID_1, Optional.ofNullable(p_28516_));
      } else {
         this.entityData.set(DATA_TRUSTED_ID_0, Optional.ofNullable(p_28516_));
      }
   }

   private void launch(Entity launch, float angle, float scale) {
      float rot = 180.0F + angle + this.getYRot();
      float hugeScale = 1.0F + this.random.nextFloat() * 0.5F * scale;
      float strength = (float)(hugeScale * (1.0 - ((LivingEntity)launch).getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)));
      float rotRad = rot * 0.017453292F;
      float x = Mth.sin(rotRad);
      float z = -Mth.cos(rotRad);
      launch.hasImpulse = true;
      Vec3 vec3 = this.getDeltaMovement();
      Vec3 vec31 = vec3.add(new Vec3(x, 0.0, z).normalize().scale(strength));
      launch.setDeltaMovement(vec31.x, hugeScale * 0.3F, vec31.z);
      launch.setOnGround(false);
   }

   @Override
   public int getAnimationTick() {
      return this.animationTick;
   }

   @Override
   public void setAnimationTick(int i) {
      this.animationTick = i;
   }

   @Override
   public Animation getAnimation() {
      return this.currentAnimation;
   }

   @Override
   public void setAnimation(Animation animation) {
      this.currentAnimation = animation;
   }

   private boolean trusts(UUID uuid) {
      return this.getTrustedUUIDs().contains(uuid);
   }

   @Override
   public Animation[] getAnimations() {
      return new Animation[]{ANIMATION_FLICK_EARS, ANIMATION_EAT_GRASS, ANIMATION_FLING, ANIMATION_SLASH};
   }

   @org.jetbrains.annotations.Nullable
   public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
      return AMCompat.create(AMEntityRegistry.RHINOCEROS.get(), serverLevel);
   }

   public boolean isAngry() {
      return (Boolean)this.entityData.get(ANGRY);
   }

   public void setAngry(boolean angry) {
      this.entityData.set(ANGRY, angry);
   }

   private void attackWithPotion(LivingEntity target, float dmg) {
      MobEffect potion = this.getPotionEffect();
      target.hurt(this.damageSources().mobAttack(this), dmg);
      if (potion != null) {
         MobEffectInstance instance = new MobEffectInstance(AMCompat.effect(potion), this.getPotionDuration(), this.getPotionLevel());
         if (!target.hasEffect(AMCompat.effect(potion)) && target.addEffect(instance)) {
            this.setInflictedCount(this.getInflictedCount() + 1);
         }
      }

      if (this.getInflictedCount() > 15 && this.random.nextInt(3) == 0 || this.getInflictedCount() > 20) {
         this.resetPotion();
      }
   }

   public boolean doHurtTarget(Entity entity) {
      if (this.getAnimation() == NO_ANIMATION) {
         this.setAnimation(this.random.nextBoolean() ? ANIMATION_SLASH : ANIMATION_FLING);
         return true;
      } else {
         return false;
      }
   }

   public boolean isAlliedTo(Entity entityIn) {
      return entityIn instanceof TamableAnimal tamableAnimal
            && AMCompat.getOwnerUUID(tamableAnimal) != null
            && this.trusts(AMCompat.getOwnerUUID(tamableAnimal))
         ? true
         : super.isAlliedTo(entityIn) || this.trusts(entityIn.getUUID());
   }

   public void addAdditionalSaveData(CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      List<UUID> list = this.getTrustedUUIDs();
      ListTag listtag = new ListTag();

      for (UUID uuid : list) {
         if (uuid != null) {
            listtag.add(AMCompat.createUUID(uuid));
         }
      }

      AMCompat.put(tag, "Trusted", listtag);
      tag.putBoolean("Sleeping", this.isSleeping());
      tag.putString("PotionName", this.getAppliedPotionId());
      tag.putInt("PotionLevel", this.getPotionLevel());
      tag.putInt("PotionDuration", this.getPotionDuration());
      tag.putInt("InflictedCount", this.getInflictedCount());
   }

   public void readAdditionalSaveData(CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      ListTag listtag = AMCompat.getList(tag, "Trusted", 11);

      for (int i = 0; i < listtag.size(); i++) {
         this.addTrustedUUID(AMCompat.loadUUID(listtag.get(i)));
      }

      this.setAppliedPotionId(AMCompat.getString(tag, "PotionName"));
      this.setPotionLevel(AMCompat.getInt(tag, "PotionLevel"));
      this.setPotionDuration(AMCompat.getInt(tag, "PotionDuration"));
      this.setInflictedCount(AMCompat.getInt(tag, "InflictedCount"));
   }

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      InteractionResult type = super.mobInteract(player, hand);
      if (!this.isBaby()
         && (itemstack.getItem() == Items.POTION || itemstack.getItem() == Items.SPLASH_POTION || itemstack.getItem() == Items.LINGERING_POTION)) {
         Potion contained = getContainedPotion(itemstack);
         if (contained != null && this.applyPotion(contained)) {
            this.gameEvent(GameEvent.ENTITY_INTERACT);
            this.playSound(SoundEvents.DYE_USE);
            this.usePlayerItem(player, hand, itemstack);
            ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
            if (!player.addItem(bottle)) {
               player.drop(bottle, false);
            }

            return InteractionResult.SUCCESS;
         }
      } else if (itemstack.is(AMTagRegistry.RHINOCEROS_FOODSTUFFS) && !this.trusts(player.getUUID())) {
         this.addTrustedUUID(player.getUUID());
         this.usePlayerItem(player, hand, itemstack);
         this.gameEvent(GameEvent.EAT);
         this.playSound(SoundEvents.HORSE_EAT);
         return InteractionResult.SUCCESS;
      }

      return type;
   }

   private static Potion getContainedPotion(ItemStack stack) {
      PotionContents contents = (PotionContents)stack.get(DataComponents.POTION_CONTENTS);
      return contents == null ? null : contents.potion().<Potion>map(Holder::value).orElse(null);
   }

   public boolean applyPotion(Potion potion) {
      if (potion != null && potion != Potions.WATER.value()) {
         if (potion.getEffects().size() >= 1) {
            MobEffectInstance first = (MobEffectInstance)potion.getEffects().get(0);
            ResourceLocation loc = BuiltInRegistries.MOB_EFFECT.getKey(AMCompat.rawEffect(first));
            if (loc != null) {
               this.setAppliedPotionId(loc.toString());
               this.setPotionLevel(first.getAmplifier());
               this.setPotionDuration(first.getDuration());
               this.setInflictedCount(0);
               return true;
            }
         }

         return false;
      } else {
         this.resetPotion();
         return true;
      }
   }

   private boolean trustsAny() {
      return ((Optional)this.entityData.get(DATA_TRUSTED_ID_0)).isPresent() || ((Optional)this.entityData.get(DATA_TRUSTED_ID_1)).isPresent();
   }

   class AIAttackNearPlayers extends NearestAttackableTargetGoal<Player> {
      public AIAttackNearPlayers() {
         super(EntityRhinoceros.this, Player.class, 80, true, true, null);
      }

      public boolean canUse() {
         return !EntityRhinoceros.this.isBaby() && !EntityRhinoceros.this.isInLove() && !EntityRhinoceros.this.trustsAny() ? super.canUse() : false;
      }

      protected double getFollowDistance() {
         return 3.0;
      }
   }

   class DefendTrustedTargetGoal extends NearestAttackableTargetGoal<LivingEntity> {
      private LivingEntity trustedLastHurtBy;
      private LivingEntity trustedLastHurt;
      private LivingEntity trusted;
      private int timestamp;

      public DefendTrustedTargetGoal(Class<LivingEntity> entities, boolean b, @Nullable boolean b2, Predicate<LivingEntity> pred) {
         super(EntityRhinoceros.this, entities, 10, b, b2, AMCompat.selector(pred));
      }

      public boolean canUse() {
         if ((this.randomInterval <= 0 || this.mob.getRandom().nextInt(this.randomInterval) == 0) && !this.mob.isBaby()) {
            for (UUID uuid : EntityRhinoceros.this.getTrustedUUIDs()) {
               if (uuid != null
                  && EntityRhinoceros.this.level() instanceof ServerLevel
                  && ((ServerLevel)EntityRhinoceros.this.level()).getEntity(uuid) instanceof LivingEntity livingentity) {
                  this.trusted = livingentity;
                  this.trustedLastHurtBy = livingentity.getLastHurtByMob();
                  this.trustedLastHurt = livingentity.getLastHurtMob();
                  int i = livingentity.getLastHurtByMobTimestamp();
                  int j = livingentity.getLastHurtMobTimestamp();
                  if (i != this.timestamp && this.canAttack(this.trustedLastHurtBy, this.targetConditions)) {
                     return true;
                  }

                  if (j != this.timestamp && this.canAttack(this.trustedLastHurt, this.targetConditions)) {
                     return true;
                  }
               }
            }

            return false;
         } else {
            return false;
         }
      }

      public void start() {
         if (this.trustedLastHurtBy != null) {
            this.setTarget(this.trustedLastHurtBy);
            this.target = this.trustedLastHurtBy;
            if (this.trusted != null) {
               this.timestamp = this.trusted.getLastHurtByMobTimestamp();
            }
         } else {
            this.setTarget(this.trustedLastHurt);
            this.target = this.trustedLastHurt;
            if (this.trusted != null) {
               this.timestamp = this.trusted.getLastHurtMobTimestamp();
            }
         }

         super.start();
      }
   }

   class StrollGoal extends MoveThroughVillageGoal {
      public StrollGoal(int timr) {
         super(EntityRhinoceros.this, 1.0, true, timr, () -> false);
      }

      public void start() {
         super.start();
      }

      public boolean canUse() {
         return super.canUse() && this.canRhinoWander();
      }

      public boolean canContinueToUse() {
         return super.canContinueToUse() && this.canRhinoWander();
      }

      private boolean canRhinoWander() {
         return !EntityRhinoceros.this.getTrustedUUIDs().isEmpty();
      }
   }
}
