package com.aetherteam.aether.entity.passive;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import com.aetherteam.aether.client.AetherSoundEvents;
import com.aetherteam.aether.entity.EntityUtil;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.MoveControl.Operation;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Aerwhale extends FlyingMob {
   private static final EntityDataAccessor<Float> DATA_X_ROT_O_ID = SynchedEntityData.defineId(Aerwhale.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> DATA_X_ROT_ID = SynchedEntityData.defineId(Aerwhale.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> DATA_Y_ROT_ID = SynchedEntityData.defineId(Aerwhale.class, EntityDataSerializers.FLOAT);

   public Aerwhale(EntityType<? extends Aerwhale> type, Level level) {
      super(type, level);
      this.lookControl = new Aerwhale.BlankLookControl(this);
      this.moveControl = new Aerwhale.AerwhaleMoveControl(this);
   }

   public void registerGoals() {
      this.goalSelector.addGoal(1, new Aerwhale.SetTravelCourseGoal(this));
   }

   public static Builder createMobAttributes() {
      return FlyingMob.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0).add(Attributes.FLYING_SPEED, 0.2).add(Attributes.STEP_HEIGHT, 0.4);
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DATA_X_ROT_O_ID, this.getXRot());
      builder.define(DATA_X_ROT_ID, this.getXRot());
      builder.define(DATA_Y_ROT_ID, this.getYRot());
   }

   public static boolean checkAerwhaleSpawnRules(
      EntityType<? extends Aerwhale> aerwhale, LevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random
   ) {
      return Mob.checkMobSpawnRules(aerwhale, level, reason, pos, random)
         && level.getFluidState(pos).is(Fluids.EMPTY)
         && level.getRawBrightness(pos, 0) > 8
         && EntityUtil.wholeHitboxCanSeeSky(level, pos, 1)
         && (reason != MobSpawnType.NATURAL || random.nextInt(40) == 0);
   }

   public void aiStep() {
      super.aiStep();
      this.setXRot(this.getXRotData());
      this.setYRot(this.getYRotData());
      this.setYBodyRot(this.getYRotData());
      this.setYHeadRot(this.getYRotData());
   }

   public void travel(Vec3 vector) {
      if (this.isEffectiveAi() || this.isControlledByLocalInstance()) {
         List<Entity> passengers = this.getPassengers();
         if (!passengers.isEmpty()) {
            Entity entity = (Entity)passengers.getFirst();
            if (entity instanceof Player player) {
               this.setYRot(player.getYRot() + 90.0F);
               this.yRotO = this.getYRot();
               this.setXRot(-player.getXRot());
               this.xRotO = this.getXRot() * 0.5F;
               this.setYHeadRot(player.yHeadRot);
               vector = new Vec3(player.xxa, 0.0, player.zza <= 0.0F ? player.zza * 0.25F : player.zza);
               if (((AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER)).isJumping()) {
                  this.setDeltaMovement(new Vec3(0.0, 0.0, 0.0));
               } else {
                  double d0 = Math.toRadians(this.getYRot());
                  double d1 = Math.toRadians(-player.getXRot());
                  double d2 = Math.cos(d1);
                  this.setDeltaMovement(
                     0.98 * (this.getDeltaMovement().x() + 0.05 * Math.cos(d0) * d2),
                     0.98 * (this.getDeltaMovement().y() + 0.02 * Math.sin(d1)),
                     0.98 * (this.getDeltaMovement().z() + 0.05 * Math.sin(d0) * d2)
                  );
               }

               if (!this.level().isClientSide()) {
                  super.travel(vector);
               }

               double d0 = this.getX() - this.xo;
               double d1 = this.getZ() - this.zo;
               float f4 = 4.0F * Mth.sqrt((float)(d0 * d0 + d1 * d1));
               if (f4 > 1.0F) {
                  f4 = 1.0F;
               }

               this.walkAnimation.update(f4, 0.4F);
            }
         } else {
            super.travel(vector);
         }
      }
   }

   public void tick() {
      this.setXRotOData(this.getXRotData());
      super.tick();
   }

   protected InteractionResult mobInteract(Player player, InteractionHand hand) {
      if (player.getUUID().equals(UUID.fromString("031025bd-0a15-439b-9c55-06a20d0de76f"))) {
         player.startRiding(this);
         if (!this.level().isClientSide()) {
            MutableComponent msg = Component.literal("Serenity is the queen of W(h)ales!!");
            player.level().players().forEach(p -> p.sendSystemMessage(msg));
         }

         return InteractionResult.sidedSuccess(this.level().isClientSide());
      } else {
         return super.mobInteract(player, hand);
      }
   }

   public float getXRotOData() {
      return (Float)this.getEntityData().get(DATA_X_ROT_O_ID);
   }

   public void setXRotOData(float rot) {
      this.getEntityData().set(DATA_X_ROT_O_ID, Mth.wrapDegrees(rot));
   }

   public float getXRotData() {
      return (Float)this.getEntityData().get(DATA_X_ROT_ID);
   }

   public void setXRotData(float rot) {
      this.getEntityData().set(DATA_X_ROT_ID, Mth.wrapDegrees(rot));
   }

   public float getYRotData() {
      return (Float)this.getEntityData().get(DATA_Y_ROT_ID);
   }

   public void setYRotData(float rot) {
      this.getEntityData().set(DATA_Y_ROT_ID, Mth.wrapDegrees(rot));
   }

   protected SoundEvent getAmbientSound() {
      return (SoundEvent)AetherSoundEvents.ENTITY_AERWHALE_AMBIENT.get();
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return (SoundEvent)AetherSoundEvents.ENTITY_AERWHALE_DEATH.get();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)AetherSoundEvents.ENTITY_AERWHALE_DEATH.get();
   }

   protected float getSoundVolume() {
      return 2.0F;
   }

   protected float getFlyingSpeed() {
      return this.isVehicle() ? this.getSpeed() * 0.6F : 0.02F;
   }

   public int getBaseExperienceReward() {
      return 1 + this.level().getRandom().nextInt(3);
   }

   public AABB getBoundingBoxForCulling() {
      return this.getBoundingBox().inflate(3.0);
   }

   protected boolean shouldStayCloseToLeashHolder() {
      return true;
   }

   protected double followLeashSpeed() {
      return 1.0;
   }

   public int getMaxSpawnClusterSize() {
      return 1;
   }

   public boolean isOnFire() {
      return false;
   }

   public static class AerwhaleMoveControl extends MoveControl {
      protected final Aerwhale mob;

      public AerwhaleMoveControl(Aerwhale pMob) {
         super(pMob);
         this.mob = pMob;
      }

      public void tick() {
         if (!this.mob.isVehicle()) {
            double x = this.getWantedX() - this.mob.getX();
            double y = this.getWantedY() - this.mob.getY();
            double z = this.getWantedZ() - this.mob.getZ();
            double distance = Math.sqrt(x * x + z * z);
            if (this.isColliding(new Vec3(x, y, z).normalize())) {
               this.operation = Operation.WAIT;
            }

            float xRotTarget = (float)(Mth.atan2(y, distance) * 57.2957763671875);
            float xRot = Mth.wrapDegrees(this.mob.getXRot());
            xRot = Mth.approachDegrees(xRot, xRotTarget, 0.2F);
            this.mob.setXRot(xRot);
            this.mob.setXRotData(this.mob.getXRot());
            float yRotTarget = Mth.wrapDegrees((float)Mth.atan2(z, x) * 57.295776F);
            float yRot = Mth.wrapDegrees(this.mob.getYRot() + 90.0F);
            yRot = Mth.approachDegrees(yRot, yRotTarget, 0.5F);
            this.mob.setYRot(yRot - 90.0F);
            this.mob.setYRotData(this.mob.getYRot());
            this.mob.setYBodyRot(yRot);
            this.mob.setYHeadRot(yRot);
            x = this.mob.getAttributeValue(Attributes.FLYING_SPEED) * Mth.cos(yRot * 0.017453292F);
            y = this.mob.getAttributeValue(Attributes.FLYING_SPEED) * Mth.sin(xRot * 0.017453292F);
            z = this.mob.getAttributeValue(Attributes.FLYING_SPEED) * Mth.sin(yRot * 0.017453292F);
            Vec3 motion = new Vec3(x, y, z);
            this.mob.setDeltaMovement(motion);
            Entity entity = this.mob.getLeashHolder();
            if (entity != null && entity.level() == this.mob.level()) {
               this.mob.restrictTo(entity.blockPosition(), 5);
               float f = this.mob.distanceTo(entity);
               if (f > 10.0F) {
                  this.mob.dropLeash(true, true);
                  this.mob.goalSelector.disableControlFlag(Flag.MOVE);
               } else if (f > 6.0F) {
                  double d0 = (entity.getX() - this.mob.getX()) / f;
                  double d1 = (entity.getY() - this.mob.getY()) / f;
                  double d2 = (entity.getZ() - this.mob.getZ()) / f;
                  this.mob
                     .setDeltaMovement(
                        this.mob.getDeltaMovement().add(Math.copySign(d0 * d0 * 0.4, d0), Math.copySign(d1 * d1 * 0.4, d1), Math.copySign(d2 * d2 * 0.4, d2))
                     );
                  this.mob.checkSlowFallDistance();
               } else if (this.mob.shouldStayCloseToLeashHolder()) {
                  this.mob.goalSelector.enableControlFlag(Flag.MOVE);
                  Vec3 vec3 = new Vec3(entity.getX() - this.mob.getX(), entity.getY() - this.mob.getY(), entity.getZ() - this.mob.getZ())
                     .normalize()
                     .scale(Math.max(f - 2.0F, 0.0F));
                  this.mob.getNavigation().moveTo(this.mob.getX() + vec3.x, this.mob.getY() + vec3.y, this.mob.getZ() + vec3.z, this.mob.followLeashSpeed());
               }
            }
         }
      }

      private boolean isColliding(Vec3 pos) {
         AABB axisalignedbb = this.mob.getBoundingBox();

         for (int i = 1; i < 7; i++) {
            axisalignedbb = axisalignedbb.move(pos);
            if (!this.mob.level().noCollision(this.mob, axisalignedbb)) {
               return true;
            }
         }

         return false;
      }
   }

   public static class BlankLookControl extends LookControl {
      public BlankLookControl(Mob pMob) {
         super(pMob);
      }

      public void tick() {
      }
   }

   public static class SetTravelCourseGoal extends Goal {
      private final Mob mob;

      public SetTravelCourseGoal(Mob mob) {
         this.mob = mob;
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean canUse() {
         MoveControl moveControl = this.mob.getMoveControl();
         if (!moveControl.hasWanted()) {
            return true;
         } else {
            double d0 = moveControl.getWantedX() - this.mob.getX();
            double d1 = moveControl.getWantedY() - this.mob.getY();
            double d2 = moveControl.getWantedZ() - this.mob.getZ();
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            return d3 < 1.0;
         }
      }

      public boolean canContinueToUse() {
         return false;
      }

      public void start() {
         RandomSource random = this.mob.getRandom();
         double x = (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
         double z = (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
         double y = this.mob.getY() + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
         x = x >= 0.0 ? x + 32.0 : x - 32.0;
         z = z >= 0.0 ? z + 32.0 : z - 32.0;
         x += this.mob.getX();
         z += this.mob.getZ();
         y = Mth.clamp(y, this.mob.level().getMinBuildHeight(), this.mob.level().getMaxBuildHeight());
         this.mob.getMoveControl().setWantedPosition(x, y, z, 1.0);
      }
   }
}
