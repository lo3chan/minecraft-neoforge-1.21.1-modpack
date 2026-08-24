package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.entity.ai.DirectPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.MimiCubeAIRangedAttack;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.MoveControl.Operation;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class EntityMimicube extends Monster implements RangedAttackMob {
   private static final EntityDataAccessor<Integer> ATTACK_TICK = SynchedEntityData.defineId(EntityMimicube.class, EntityDataSerializers.INT);
   private final MimiCubeAIRangedAttack aiArrowAttack = new MimiCubeAIRangedAttack(this, 1.0, 10, 15.0F);
   private final MeleeAttackGoal aiAttackOnCollide = new MeleeAttackGoal(this, 1.2, false);
   public float squishAmount;
   public float squishFactor;
   public float prevSquishFactor;
   public float leftSwapProgress = 0.0F;
   public float prevLeftSwapProgress = 0.0F;
   public float rightSwapProgress = 0.0F;
   public float prevRightSwapProgress = 0.0F;
   public float helmetSwapProgress = 0.0F;
   public float prevHelmetSwapProgress = 0.0F;
   public float prevAttackProgress;
   public float attackProgress;
   private boolean wasOnGround;
   private int eatingTicks;

   protected EntityMimicube(EntityType type, Level world) {
      super(type, world);
      this.moveControl = new EntityMimicube.MimicubeMoveHelper(this);
      this.navigation = new DirectPathNavigator(this, world);
      this.setCombatTask();
   }

   public static Builder bakeAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 30.0)
         .add(Attributes.FOLLOW_RANGE, 32.0)
         .add(Attributes.ATTACK_DAMAGE, 2.0)
         .add(Attributes.MOVEMENT_SPEED, 0.44999998807907104);
   }

   public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn) {
      return AMEntityRegistry.rollSpawn(AMConfig.mimicubeSpawnRolls, this.getRandom(), spawnReasonIn);
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(ATTACK_TICK, 0);
   }

   public boolean doHurtTarget(Entity entityIn) {
      this.entityData.set(ATTACK_TICK, 5);
      return true;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new AnimalAIWanderRanged(this, 60, 1.0, 10, 7));
      this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(2, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, AbstractVillager.class, true));
   }

   public void setCombatTask() {
      if (this.level() != null && !this.level().isClientSide()) {
         this.goalSelector.removeGoal(this.aiAttackOnCollide);
         this.goalSelector.removeGoal(this.aiArrowAttack);
         ItemStack itemstack = this.getMainHandItem();
         if (!(itemstack.getItem() instanceof ProjectileWeaponItem) && !(itemstack.getItem() instanceof TridentItem)) {
            this.goalSelector.addGoal(4, this.aiAttackOnCollide);
         } else {
            int i = 10;
            if (this.level().getDifficulty() != Difficulty.HARD) {
               i = 30;
            }

            this.aiArrowAttack.setAttackCooldown(i);
            this.goalSelector.addGoal(4, this.aiArrowAttack);
         }
      }
   }

   public void attackEntityWithRangedAttackTrident(LivingEntity target, float distanceFactor) {
      ThrownTrident tridententity = new ThrownTrident(this.level(), this, new ItemStack(Items.TRIDENT));
      double d0 = target.getX() - this.getX();
      double d1 = target.getY(0.3333333333333333) - tridententity.getY();
      double d2 = target.getZ() - this.getZ();
      double d3 = Mth.sqrt((float)(d0 * d0 + d2 * d2));
      tridententity.shoot(d0, d1 + d3 * 0.20000000298023224, d2, 1.6F, 14 - this.level().getDifficulty().getId() * 4);
      this.gameEvent(GameEvent.PROJECTILE_SHOOT);
      this.playSound(SoundEvents.DROWNED_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
      this.level().addFreshEntity(tridententity);
   }

   public void performRangedAttack(LivingEntity target, float distanceFactor) {
      if (this.getMainHandItem().getItem() instanceof TridentItem) {
         this.attackEntityWithRangedAttackTrident(target, distanceFactor);
      } else {
         ItemStack itemstack = this.getProjectile(this.getMainHandItem());
         AbstractArrow abstractarrowentity = this.fireArrow(itemstack, distanceFactor);
         if (this.getMainHandItem().getItem() instanceof BowItem) {
            abstractarrowentity = ((BowItem)this.getMainHandItem().getItem()).customArrow(abstractarrowentity, itemstack, this.getMainHandItem());
         }

         double d0 = target.getX() - this.getX();
         double d1 = target.getY(0.3333333333333333) - abstractarrowentity.getY();
         double d2 = target.getZ() - this.getZ();
         double d3 = Mth.sqrt((float)(d0 * d0 + d2 * d2));
         abstractarrowentity.shoot(d0, d1 + d3 * 0.20000000298023224, d2, 1.6F, 14 - this.level().getDifficulty().getId() * 4);
         this.gameEvent(GameEvent.PROJECTILE_SHOOT);
         this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
         this.level().addFreshEntity(abstractarrowentity);
      }
   }

   protected AbstractArrow fireArrow(ItemStack arrowStack, float distanceFactor) {
      return ProjectileUtil.getMobArrow(this, arrowStack, distanceFactor, this.getMainHandItem());
   }

   public boolean canFireProjectileWeapon(ProjectileWeaponItem p_230280_1_) {
      return p_230280_1_ == Items.BOW;
   }

   public void setItemSlot(EquipmentSlot slotIn, ItemStack stack) {
      switch (slotIn) {
         case HEAD:
            if (!ItemStack.isSameItem(stack, this.getItemBySlot(EquipmentSlot.HEAD))) {
               this.helmetSwapProgress = 5.0F;
               this.level().broadcastEntityEvent(this, (byte)45);
            }
            break;
         case MAINHAND:
            if (!ItemStack.isSameItem(stack, this.getItemBySlot(EquipmentSlot.MAINHAND))) {
               this.rightSwapProgress = 5.0F;
               this.level().broadcastEntityEvent(this, (byte)46);
            }
            break;
         case OFFHAND:
            if (!ItemStack.isSameItem(stack, this.getItemBySlot(EquipmentSlot.OFFHAND))) {
               this.leftSwapProgress = 5.0F;
               this.level().broadcastEntityEvent(this, (byte)47);
            }
      }

      super.setItemSlot(slotIn, stack);
      if (!this.level().isClientSide()) {
         this.setCombatTask();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte id) {
      super.handleEntityEvent(id);
      switch (id) {
         case 45:
            this.helmetSwapProgress = 5.0F;
            break;
         case 46:
            this.rightSwapProgress = 5.0F;
            break;
         case 47:
            this.leftSwapProgress = 5.0F;
      }
   }

   public boolean isBlocking() {
      return AMCompat.canShieldBlock(this.getMainHandItem()) || AMCompat.canShieldBlock(this.getOffhandItem());
   }

   public boolean hurt(DamageSource source, float amount) {
      Entity trueSource = source.getEntity();
      if (trueSource != null && trueSource instanceof LivingEntity attacker) {
         if (!attacker.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
            this.setItemSlot(EquipmentSlot.HEAD, this.mimicStack(attacker.getItemBySlot(EquipmentSlot.HEAD)));
         }

         if (!attacker.getItemBySlot(EquipmentSlot.OFFHAND).isEmpty()) {
            this.setItemSlot(EquipmentSlot.OFFHAND, this.mimicStack(attacker.getItemBySlot(EquipmentSlot.OFFHAND)));
         }

         if (!attacker.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
            this.setItemSlot(EquipmentSlot.MAINHAND, this.mimicStack(attacker.getItemBySlot(EquipmentSlot.MAINHAND)));
         }
      }

      return super.hurt(source, amount);
   }

   private ItemStack mimicStack(ItemStack stack) {
      ItemStack copy = stack.copy();
      if (copy.isDamageableItem()) {
         copy.setDamageValue(copy.getMaxDamage());
      }

      return copy;
   }

   public void tick() {
      super.tick();
      this.squishFactor = this.squishFactor + (this.squishAmount - this.squishFactor) * 0.5F;
      this.prevSquishFactor = this.squishFactor;
      this.prevHelmetSwapProgress = this.helmetSwapProgress;
      this.prevRightSwapProgress = this.rightSwapProgress;
      this.prevLeftSwapProgress = this.leftSwapProgress;
      this.prevAttackProgress = this.attackProgress;
      if (this.rightSwapProgress > 0.0F) {
         this.rightSwapProgress -= 0.5F;
      }

      if (this.leftSwapProgress > 0.0F) {
         this.leftSwapProgress -= 0.5F;
      }

      if (this.helmetSwapProgress > 0.0F) {
         this.helmetSwapProgress -= 0.5F;
      }

      if (this.onGround() && !this.wasOnGround) {
         for (int j = 0; j < 8; j++) {
            float f = this.random.nextFloat() * 6.2831855F;
            float f1 = this.random.nextFloat() * 0.5F + 0.5F;
            float f2 = Mth.sin(f) * 0.5F * f1;
            float f3 = Mth.cos(f) * 0.5F * f1;
            this.level()
               .addParticle(
                  new ItemParticleOption(ParticleTypes.ITEM, new ItemStack((ItemLike)AMItemRegistry.MIMICREAM.get())),
                  this.getX() + f2,
                  this.getY(),
                  this.getZ() + f3,
                  0.0,
                  0.0,
                  0.0
               );
         }

         this.playSound(this.getSquishSound(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) / 0.8F);
         this.squishAmount = -0.35F;
      } else if (!this.onGround() && this.wasOnGround) {
         this.squishAmount = 2.0F;
      }

      if (this.isInWater()) {
         this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.05, 0.0));
      }

      if (AMCompat.isEdible(this.getOffhandItem().getItem()) && this.getHealth() < this.getMaxHealth()) {
         if (this.eatingTicks < 100) {
            for (int i = 0; i < 3; i++) {
               double d2 = this.random.nextGaussian() * 0.02;
               double d0 = this.random.nextGaussian() * 0.02;
               double d1 = this.random.nextGaussian() * 0.02;
               this.level()
                  .addParticle(
                     new ItemParticleOption(ParticleTypes.ITEM, this.getItemInHand(InteractionHand.OFF_HAND)),
                     this.getX() + this.random.nextFloat() * this.getBbWidth() - this.getBbWidth() * 0.5,
                     this.getY() + this.getBbHeight() * 0.5F + this.random.nextFloat() * this.getBbHeight() * 0.5F,
                     this.getZ() + this.random.nextFloat() * this.getBbWidth() - this.getBbWidth() * 0.5,
                     d0,
                     d1,
                     d2
                  );
            }

            if (this.eatingTicks % 6 == 0) {
               this.gameEvent(GameEvent.EAT);
               this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), this.getVoicePitch());
            }

            this.eatingTicks++;
         }

         if (this.eatingTicks == 100) {
            this.gameEvent(GameEvent.EAT);
            this.playSound(SoundEvents.PLAYER_BURP, this.getSoundVolume(), this.getVoicePitch());
            this.getOffhandItem().shrink(1);
            this.heal(5.0F);
            this.eatingTicks = 0;
         }
      } else if (AMCompat.isEdible(this.getMainHandItem().getItem()) && this.getHealth() < this.getMaxHealth()) {
         if (this.eatingTicks < 100) {
            for (int i = 0; i < 3; i++) {
               double d2 = this.random.nextGaussian() * 0.02;
               double d0 = this.random.nextGaussian() * 0.02;
               double d1 = this.random.nextGaussian() * 0.02;
               this.level()
                  .addParticle(
                     new ItemParticleOption(ParticleTypes.ITEM, this.getItemInHand(InteractionHand.MAIN_HAND)),
                     this.getX() + this.random.nextFloat() * this.getBbWidth() - this.getBbWidth() * 0.5,
                     this.getY() + this.getBbHeight() * 0.5F + this.random.nextFloat() * this.getBbHeight() * 0.5F,
                     this.getZ() + this.random.nextFloat() * this.getBbWidth() - this.getBbWidth() * 0.5,
                     d0,
                     d1,
                     d2
                  );
            }

            this.gameEvent(GameEvent.EAT);
            this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), this.getVoicePitch());
            if (this.eatingTicks % 6 == 0) {
               this.gameEvent(GameEvent.EAT);
               this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), this.getVoicePitch());
            }

            this.eatingTicks++;
         }

         if (this.eatingTicks == 100) {
            this.gameEvent(GameEvent.EAT);
            this.playSound(SoundEvents.PLAYER_BURP, this.getSoundVolume(), this.getVoicePitch());
            this.getMainHandItem().shrink(1);
            this.heal(5.0F);
         }
      } else {
         this.eatingTicks = 0;
      }

      this.wasOnGround = this.onGround();
      this.alterSquishAmount();
      LivingEntity livingentity = this.getTarget();
      if (livingentity != null && this.distanceToSqr(livingentity) < 144.0) {
         this.moveControl.setWantedPosition(livingentity.getX(), livingentity.getY(), livingentity.getZ(), this.moveControl.getSpeedModifier());
         this.wasOnGround = true;
      }

      if ((Integer)this.entityData.get(ATTACK_TICK) > 0) {
         if ((Integer)this.entityData.get(ATTACK_TICK) == 2 && this.getTarget() != null && this.distanceTo(this.getTarget()) < 2.3) {
            super.doHurtTarget(this.getTarget());
         }

         this.entityData.set(ATTACK_TICK, (Integer)this.entityData.get(ATTACK_TICK) - 1);
         if (this.attackProgress < 3.0F) {
            this.attackProgress++;
         }
      } else if (this.attackProgress > 0.0F) {
         this.attackProgress--;
      }
   }

   protected float getEquipmentDropChance(EquipmentSlot slotIn) {
      return 0.0F;
   }

   private SoundEvent getSquishSound() {
      return AMSoundRegistry.MIMICUBE_JUMP.get();
   }

   private SoundEvent getJumpSound() {
      return AMSoundRegistry.MIMICUBE_JUMP.get();
   }

   public void jumpFromGround() {
      Vec3 vector3d = this.getDeltaMovement();
      this.setDeltaMovement(vector3d.x, this.getJumpPower(), vector3d.z);
      this.hasImpulse = true;
   }

   protected int getJumpDelay() {
      return this.random.nextInt(20) + 10;
   }

   protected void alterSquishAmount() {
      this.squishAmount *= 0.6F;
   }

   public boolean shouldShoot() {
      return this.getMainHandItem().getItem() instanceof ProjectileWeaponItem || this.getMainHandItem().getItem() instanceof TridentItem;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return AMSoundRegistry.MIMICUBE_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return AMSoundRegistry.MIMICUBE_HURT.get();
   }

   private static class MimicubeMoveHelper extends MoveControl {
      private final EntityMimicube slime;
      private float yRot;
      private int jumpDelay;
      private boolean isAggressive;

      public MimicubeMoveHelper(EntityMimicube slimeIn) {
         super(slimeIn);
         this.slime = slimeIn;
         this.yRot = 180.0F * slimeIn.getYRot() / 3.1415927F;
      }

      public void setDirection(float yRotIn, boolean aggressive) {
         this.yRot = yRotIn;
         this.isAggressive = aggressive;
      }

      public void setSpeed(double speedIn) {
         this.speedModifier = speedIn;
         this.operation = Operation.MOVE_TO;
      }

      public void tick() {
         if (this.mob.onGround()) {
            this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
            if (this.jumpDelay-- <= 0 && this.operation != Operation.WAIT) {
               this.jumpDelay = this.slime.getJumpDelay();
               if (this.mob.getTarget() != null) {
                  this.jumpDelay /= 3;
               }

               this.slime.getJumpControl().jump();
               this.slime.playSound(this.slime.getJumpSound(), this.slime.getSoundVolume(), this.slime.getVoicePitch());
            } else {
               this.slime.xxa = 0.0F;
               this.slime.zza = 0.0F;
               this.mob.setSpeed(0.0F);
            }
         }

         super.tick();
      }
   }
}
