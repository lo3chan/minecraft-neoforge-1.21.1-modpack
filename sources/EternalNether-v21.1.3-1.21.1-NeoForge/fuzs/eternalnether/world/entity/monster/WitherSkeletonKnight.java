package fuzs.eternalnether.world.entity.monster;

import fuzs.eternalnether.EternalNether;
import fuzs.eternalnether.services.CommonAbstractions;
import fuzs.eternalnether.world.entity.ai.goal.ShieldDefenseGoal;
import fuzs.puzzleslib.api.item.v2.ToolTypeHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class WitherSkeletonKnight extends WitherSkeleton implements ShieldedMob {
   private static final EntityDataAccessor<Boolean> DATA_IS_SHIELDED = SynchedEntityData.defineId(WitherSkeletonKnight.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_SHIELD_HAND = SynchedEntityData.defineId(WitherSkeletonKnight.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> DATA_SHIELD_COOLDOWN = SynchedEntityData.defineId(WitherSkeletonKnight.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> DATA_IS_DISARMORED = SynchedEntityData.defineId(WitherSkeletonKnight.class, EntityDataSerializers.BOOLEAN);
   private static final ResourceLocation SPEED_MODIFIER_BLOCKING_ID = EternalNether.id("blocking");
   private static final AttributeModifier SPEED_MODIFIER_BLOCKING = new AttributeModifier(SPEED_MODIFIER_BLOCKING_ID, -0.1, Operation.ADD_VALUE);
   private static final ResourceLocation SPEED_MODIFIER_DISARMOURED_ID = ResourceLocation.withDefaultNamespace("disarmoured");
   private static final AttributeModifier SPEED_MODIFIER_DISARMOURED = new AttributeModifier(
      SPEED_MODIFIER_DISARMOURED_ID, 0.3499999940395355, Operation.ADD_MULTIPLIED_TOTAL
   );
   private static final float BREAK_HEALTH_PERCENT = 0.35F;

   public WitherSkeletonKnight(EntityType<? extends WitherSkeleton> entityType, Level level) {
      super(entityType, level);
   }

   public static Builder createAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MOVEMENT_SPEED, 0.2)
         .add(Attributes.MAX_HEALTH, 60.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.5)
         .add(Attributes.ARMOR, 2.0);
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DATA_IS_DISARMORED, false);
      builder.define(DATA_IS_SHIELDED, false);
      builder.define(DATA_SHIELD_HAND, false);
      builder.define(DATA_SHIELD_COOLDOWN, 0);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(1, new ShieldDefenseGoal(this, Player.class));
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide) {
         this.decrementShieldCooldown();
      }
   }

   public void addAdditionalSaveData(CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      tag.putBoolean("Disarmored", this.isDisarmored());
   }

   public void readAdditionalSaveData(CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      this.setDisarmored(tag.getBoolean("Disarmored"));
   }

   public boolean isDisarmored() {
      return (Boolean)this.entityData.get(DATA_IS_DISARMORED);
   }

   private void setDisarmored(boolean disarmored) {
      this.entityData.set(DATA_IS_DISARMORED, disarmored);
   }

   public void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
      super.populateDefaultEquipmentSlots(random, difficulty);
      this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.SHIELD));
   }

   public boolean hurt(DamageSource damageSource, float damageAmount) {
      if (super.hurt(damageSource, damageAmount)) {
         if (!this.isDisarmored() && this.getHealth() / this.getMaxHealth() < 0.35F) {
            this.setDisarmored(true);
            this.playSound(SoundEvents.SHIELD_BREAK, 1.2F, 0.8F + this.level().random.nextFloat() * 0.4F);
            AttributeInstance attribute = this.getAttribute(Attributes.MOVEMENT_SPEED);
            if (attribute != null && !attribute.hasModifier(SPEED_MODIFIER_DISARMOURED_ID)) {
               attribute.addPermanentModifier(SPEED_MODIFIER_DISARMOURED);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public void knockback(double strength, double x, double z) {
      if (!this.isUsingShield()) {
         super.knockback(strength, x, z);
      } else {
         this.playSound(SoundEvents.SHIELD_BLOCK, 1.0F, 0.8F + this.level().random.nextFloat() * 0.4F);
      }
   }

   protected void blockUsingShield(LivingEntity attacker) {
      super.blockUsingShield(attacker);
      if (CommonAbstractions.INSTANCE.canDisableShield(attacker.getMainHandItem(), this.useItem, this, attacker)) {
         this.disableShield();
      }
   }

   private void disableShield() {
      this.setShieldCooldown(60);
      this.stopUsingShield();
      this.level().broadcastEntityEvent(this, (byte)30);
      this.playSound(SoundEvents.SHIELD_BREAK, 0.8F, 0.8F + this.level().random.nextFloat() * 0.4F);
   }

   @Override
   public boolean isShieldDisabled() {
      return this.getShieldCooldown() > 0;
   }

   @Override
   public void startUsingShield() {
      if (!this.isUsingShield() && !this.isShieldDisabled()) {
         for (InteractionHand interactionHand : InteractionHand.values()) {
            if (ToolTypeHelper.INSTANCE.isShield(this.getItemInHand(interactionHand))) {
               this.startUsingItem(interactionHand);
               this.setUsingShield(true);
               this.setShieldMainHand(interactionHand == InteractionHand.MAIN_HAND);
               AttributeInstance attributeInstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
               if (attributeInstance != null && !attributeInstance.hasModifier(SPEED_MODIFIER_BLOCKING_ID)) {
                  attributeInstance.addTransientModifier(SPEED_MODIFIER_BLOCKING);
               }
            }
         }
      }
   }

   @Override
   public void stopUsingShield() {
      if (this.isUsingShield()) {
         for (InteractionHand interactionHand : InteractionHand.values()) {
            if (ToolTypeHelper.INSTANCE.isShield(this.getItemInHand(interactionHand))) {
               this.stopUsingItem();
               this.setUsingShield(false);
               AttributeInstance attributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
               if (attributeinstance != null) {
                  attributeinstance.removeModifier(SPEED_MODIFIER_BLOCKING);
               }
            }
         }
      }
   }

   public boolean isUsingShield() {
      return (Boolean)this.entityData.get(DATA_IS_SHIELDED);
   }

   public void setUsingShield(boolean isShielded) {
      this.entityData.set(DATA_IS_SHIELDED, isShielded);
   }

   private boolean isShieldMainHand() {
      return (Boolean)this.entityData.get(DATA_SHIELD_HAND);
   }

   private void setShieldMainHand(boolean isShieldedMainHand) {
      this.entityData.set(DATA_SHIELD_HAND, isShieldedMainHand);
   }

   private int getShieldCooldown() {
      return (Integer)this.entityData.get(DATA_SHIELD_COOLDOWN);
   }

   private void setShieldCooldown(int shieldCooldown) {
      this.entityData.set(DATA_SHIELD_COOLDOWN, shieldCooldown);
   }

   private void decrementShieldCooldown() {
      this.setShieldCooldown(Math.max(this.getShieldCooldown() - 1, 0));
   }

   public InteractionHand getShieldHand() {
      return this.isUsingShield() ? (this.isShieldMainHand() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND) : null;
   }
}
