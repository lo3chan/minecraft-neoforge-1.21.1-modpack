package com.aetherteam.aether.entity.monster.dungeon;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.entity.EntityUtil;
import com.aetherteam.aether.entity.NotGrounded;
import com.aetherteam.aether.entity.ai.goal.MostDamageTargetGoal;
import com.aetherteam.aether.event.AetherEventDispatch;
import com.aetherteam.aether.event.ValkyrieTeleportEvent;
import java.util.EnumSet;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.MoveControl.Operation;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractValkyrie extends Monster implements NotGrounded {
   private static final EntityDataAccessor<Boolean> DATA_ENTITY_ON_GROUND_ID = SynchedEntityData.defineId(AbstractValkyrie.class, EntityDataSerializers.BOOLEAN);
   private MostDamageTargetGoal mostDamageTargetGoal;
   private int lungeCooldown = 0;
   protected double lastMotionY;

   public AbstractValkyrie(EntityType<? extends AbstractValkyrie> type, Level level) {
      super(type, level);
      this.moveControl = new AbstractValkyrie.ValkyrieMoveControl(this);
   }

   public void registerGoals() {
      this.goalSelector.addGoal(1, new AbstractValkyrie.ValkyrieTeleportGoal(this));
      this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 0.65, true));
      this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.5));
      this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F, 8.0F));
      this.mostDamageTargetGoal = new MostDamageTargetGoal(this);
      this.targetSelector.addGoal(1, this.mostDamageTargetGoal);
      this.targetSelector.addGoal(2, new HurtByTargetGoal(this, new Class[0]));
   }

   public static Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.5);
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DATA_ENTITY_ON_GROUND_ID, true);
   }

   public void tick() {
      super.tick();
      if (this.onGround()) {
         this.setEntityOnGround(true);
      }

      if (!this.level().isClientSide() && this.lungeCooldown > 0) {
         this.lungeCooldown--;
      }
   }

   public void travel(Vec3 motion) {
      this.lastMotionY = this.getDeltaMovement().y();
      super.travel(motion);
   }

   public void jumpFromGround() {
      super.jumpFromGround();
      this.setEntityOnGround(false);
   }

   public boolean hurt(DamageSource source, float amount) {
      boolean result = super.hurt(source, amount);
      if (!this.level().isClientSide() && result && source.getEntity() instanceof LivingEntity living) {
         this.mostDamageTargetGoal.addAggro(living, amount);
      }

      return result;
   }

   protected boolean teleportAroundTarget(Entity target) {
      Vec2 targetVec = new Vec2(this.getRandom().nextFloat() - 0.5F, this.getRandom().nextFloat() - 0.5F).normalized();
      double x = target.getX() + targetVec.x * 3.0F;
      double y = target.getY();
      double z = target.getZ() + targetVec.y * 3.0F;
      MutableBlockPos mutableBlockPos = new MutableBlockPos(x, y, z);

      for (int i = 0; mutableBlockPos.getY() > this.level().getMinBuildHeight() && !this.level().getBlockState(mutableBlockPos).blocksMotion() && i <= 4; i++) {
         mutableBlockPos.move(Direction.DOWN);
      }

      BlockState blockState = this.level().getBlockState(mutableBlockPos);
      boolean isValidSpot = blockState.is(AetherTags.Blocks.VALKYRIE_TELEPORTABLE_ON);
      return isValidSpot && this.teleport(x, y, z);
   }

   protected boolean teleport(double x, double y, double z) {
      ValkyrieTeleportEvent event = AetherEventDispatch.onValkyrieTeleport(this, x, y, z);
      if (event.isCanceled()) {
         return false;
      } else {
         boolean flag = this.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), false);
         if (flag) {
            this.spawnExplosionParticles();
         }

         return flag;
      }
   }

   public void spawnExplosionParticles() {
      if (!this.level().isClientSide()) {
         this.level().broadcastEntityEvent(this, (byte)70);
      }
   }

   protected void chat(Player player, Component message, boolean sound) {
      player.sendSystemMessage(message);
   }

   @Override
   public boolean isEntityOnGround() {
      return (Boolean)this.getEntityData().get(DATA_ENTITY_ON_GROUND_ID);
   }

   @Override
   public void setEntityOnGround(boolean onGround) {
      this.getEntityData().set(DATA_ENTITY_ON_GROUND_ID, onGround);
   }

   protected float getFlyingSpeed() {
      return this.getSpeed() * 0.216F;
   }

   protected boolean canRide(Entity vehicle) {
      return false;
   }

   protected boolean shouldDespawnInPeaceful() {
      return false;
   }

   public void handleEntityEvent(byte id) {
      if (id == 70) {
         for (int i = 0; i < 5; i++) {
            EntityUtil.spawnMovementExplosionParticles(this);
         }
      } else {
         super.handleEntityEvent(id);
      }
   }

   public static class LungeGoal extends Goal {
      private final AbstractValkyrie valkyrie;
      private final double speedModifier;
      private final int lungeCooldownMax;
      private int flyingTicks;

      public LungeGoal(AbstractValkyrie valkyrie, double speedModifier, int lungeCooldownMax) {
         this.valkyrie = valkyrie;
         this.speedModifier = speedModifier;
         this.lungeCooldownMax = lungeCooldownMax;
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public boolean canUse() {
         return !this.valkyrie.onGround() && this.valkyrie.lungeCooldown <= 0;
      }

      public void tick() {
         LivingEntity target = this.valkyrie.getTarget();
         double motionY = this.valkyrie.getDeltaMovement().y();
         if (target != null) {
            if (motionY < 0.2 && this.valkyrie.lastMotionY >= 0.2 && this.valkyrie.distanceTo(target) <= 16.0F) {
               double x = target.getX() - this.valkyrie.getX();
               double z = target.getZ() - this.valkyrie.getZ();
               motionY -= 0.1;
               double angle = Math.atan2(x, z);
               this.valkyrie.setDeltaMovement(Math.sin(angle) * 0.3, motionY, Math.cos(angle) * 0.3);
               this.valkyrie.setYRot((float)angle * 57.295776F);
               this.flyingTicks = 8;
            }

            if (this.flyingTicks > 0) {
               this.flyingTicks--;
               AttributeInstance gravity = this.valkyrie.getAttribute(Attributes.GRAVITY);
               double fallSpeed;
               if (gravity != null) {
                  fallSpeed = Math.max(gravity.getValue() * -0.625, -0.275);
               } else {
                  fallSpeed = -0.275;
               }

               if (motionY < fallSpeed) {
                  this.valkyrie.setDeltaMovement(this.valkyrie.getDeltaMovement().x(), fallSpeed, this.valkyrie.getDeltaMovement().z());
                  this.valkyrie.setEntityOnGround(false);
               }
            }

            Vec3 position = target.position();
            this.valkyrie.getMoveControl().setWantedPosition(position.x(), position.y(), position.z(), this.speedModifier);
         }
      }

      public boolean requiresUpdateEveryTick() {
         return true;
      }

      public void stop() {
         this.valkyrie.lungeCooldown = this.lungeCooldownMax;
      }
   }

   public static class ValkyrieMoveControl extends MoveControl {
      public ValkyrieMoveControl(Mob pMob) {
         super(pMob);
      }

      public void tick() {
         if (this.operation == Operation.JUMPING) {
            this.operation = Operation.MOVE_TO;
         }

         super.tick();
      }
   }

   public static class ValkyrieTeleportGoal extends Goal {
      private final AbstractValkyrie valkyrie;
      protected int teleportTimer;

      public ValkyrieTeleportGoal(AbstractValkyrie valkyrie) {
         this.valkyrie = valkyrie;
         this.teleportTimer = this.valkyrie.getRandom().nextInt(200);
      }

      public boolean canUse() {
         return true;
      }

      public void tick() {
         if (this.teleportTimer++ >= 450) {
            if (this.valkyrie.getTarget() != null && this.valkyrie.teleportAroundTarget(this.valkyrie.getTarget())) {
               this.teleportTimer = this.valkyrie.getRandom().nextInt(40);
            } else {
               this.teleportTimer -= 20;
            }
         }
      }

      public boolean requiresUpdateEveryTick() {
         return true;
      }
   }
}
