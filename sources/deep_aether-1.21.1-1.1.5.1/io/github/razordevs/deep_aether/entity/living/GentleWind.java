package io.github.razordevs.deep_aether.entity.living;

import com.aetherteam.nitrogen.attachment.INBTSynchable.Direction;
import io.github.razordevs.deep_aether.entity.projectile.WindCrystal;
import io.github.razordevs.deep_aether.init.DAEntities;
import io.github.razordevs.deep_aether.init.DASounds;
import io.github.razordevs.deep_aether.item.component.DADataComponentTypes;
import io.github.razordevs.deep_aether.item.component.FloatyScarf;
import io.github.razordevs.deep_aether.item.gear.DAEquipmentUtil;
import io.github.razordevs.deep_aether.networking.attachment.DAAttachments;
import io.github.razordevs.deep_aether.networking.attachment.DAPlayerAttachment;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class GentleWind extends FlyingMob {
   private static final EntityDataAccessor<Integer> COLOR_0 = SynchedEntityData.defineId(GentleWind.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> COLOR_1 = SynchedEntityData.defineId(GentleWind.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> COLOR_2 = SynchedEntityData.defineId(GentleWind.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> COLOR_3 = SynchedEntityData.defineId(GentleWind.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> COLOR_4 = SynchedEntityData.defineId(GentleWind.class, EntityDataSerializers.INT);
   private static final int RIDE_COOLDOWN = 300;
   private int rideCooldownCounter;
   private static final EntityDataAccessor<Integer> DATA_OWNER_ID = SynchedEntityData.defineId(GentleWind.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> IS_ON_NECK = SynchedEntityData.defineId(GentleWind.class, EntityDataSerializers.BOOLEAN);

   public GentleWind(EntityType<? extends FlyingMob> type, Level level) {
      super(type, level);
      this.moveControl = new GentleWind.GentleWindMoveControl(this);
      this.lookControl = new GentleWind.GentleWindLookControl(this);
   }

   public GentleWind(Level level, Player owner) {
      this((EntityType<? extends FlyingMob>)DAEntities.GENTLE_WIND.get(), level);
      this.setOwner(owner);
      this.setPos(owner.position());
      this.setEntityAroundNeck();
      level.addFreshEntity(this);
   }

   public GentleWind(Level level, Player owner, List<Integer> colors) {
      this(level, owner);
      this.setColors(colors);
   }

   public int getFromColor(int index) {
      return ARGB32.opaque(this.getColors()[index]);
   }

   public static int getFromColor(List<Integer> list, int index) {
      return ARGB32.opaque(list.get(index));
   }

   public boolean canBeHitByProjectile() {
      return false;
   }

   public static Builder createMobAttributes() {
      return FlyingMob.createMobAttributes().add(Attributes.MAX_HEALTH, 1.0).add(Attributes.MOVEMENT_SPEED, 10.0);
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DATA_OWNER_ID, 0);
      builder.define(IS_ON_NECK, false);
      builder.define(COLOR_0, -1);
      builder.define(COLOR_1, -1);
      builder.define(COLOR_2, -1);
      builder.define(COLOR_3, -1);
      builder.define(COLOR_4, -1);
   }

   private void setColors(List<Integer> colors) {
      this.entityData.set(COLOR_0, colors.get(0));
      this.entityData.set(COLOR_1, colors.get(1));
      this.entityData.set(COLOR_2, colors.get(2));
      this.entityData.set(COLOR_3, colors.get(3));
      this.entityData.set(COLOR_4, colors.get(4));
   }

   private int[] getColors() {
      return new int[]{
         (Integer)this.entityData.get(COLOR_0),
         (Integer)this.entityData.get(COLOR_1),
         (Integer)this.entityData.get(COLOR_2),
         (Integer)this.entityData.get(COLOR_3),
         (Integer)this.entityData.get(COLOR_4)
      };
   }

   protected void registerGoals() {
      this.targetSelector.addGoal(1, new GentleWind.GentleWindOwnerHurtByTargetGoal(this));
      this.targetSelector.addGoal(2, new GentleWind.GentleWindOwnerHurtTargetGoal(this));
      this.goalSelector.addGoal(2, new GentleWind.GentleWindAirChargeGoal(this));
      this.goalSelector.addGoal(3, new GentleWind.WrapAroundPlayerGoal(this));
      this.goalSelector.addGoal(4, new GentleWind.FollowPlayerGoal(this));
   }

   public boolean canBeSeenAsEnemy() {
      return false;
   }

   protected void pushEntities() {
   }

   protected boolean canRide(Entity vehicle) {
      return false;
   }

   public boolean hurt(DamageSource source, float damage) {
      return false;
   }

   @Nullable
   public Player getOwner() {
      return (Player)this.level().getEntity((Integer)this.getEntityData().get(DATA_OWNER_ID));
   }

   @Nullable
   public ServerPlayer getServerOwner() {
      return (ServerPlayer)this.level().getEntity((Integer)this.getEntityData().get(DATA_OWNER_ID));
   }

   public void setOwner(Player entity) {
      this.getEntityData().set(DATA_OWNER_ID, entity.getId());
   }

   private void followOwner() {
      Player player = this.getOwner();
      if (player != null) {
         float distance = this.distanceTo(player);
         if (distance > 50.0F) {
            this.setPos(player.position().add(0.0, 1.2, 0.0));
         } else if (distance < 5.0) {
            this.moveControl
               .setWantedPosition(player.getX() + this.getDeltaMovement().x * 2.0, player.getY() + 2.0, player.getZ() + this.getDeltaMovement().z * 2.0, 1.0);
         } else {
            this.moveControl.setWantedPosition(player.getX(), player.getY() + 2.0, player.getZ(), 1.0);
         }
      }
   }

   public boolean wantsToAttack(LivingEntity target, LivingEntity owner) {
      if (target.is(owner)) {
         return false;
      } else if (target instanceof Creeper || target instanceof Ghast || target instanceof ArmorStand || target instanceof GentleWind) {
         return false;
      } else if (target instanceof Wolf wolf) {
         return !wolf.isTame() || wolf.getOwner() != owner;
      } else if (target instanceof Player player && owner instanceof Player player1 && !player1.canHarmPlayer(player)) {
         return false;
      } else {
         return target instanceof AbstractHorse abstracthorse && abstracthorse.isTamed()
            ? false
            : !(target instanceof TamableAnimal tamableanimal && tamableanimal.isTame());
      }
   }

   public boolean canUsePortal(boolean use) {
      return false;
   }

   public void tick() {
      if (this.isWrappedAroundNeck() && this.getOwner() != null) {
         this.setPos(this.getOwner().getX(), this.getOwner().getY() + 50.0, this.getOwner().getZ());
         this.getNavigation().stop();
      }

      this.rideCooldownCounter++;
      super.tick();
   }

   public boolean displayFireAnimation() {
      return false;
   }

   public boolean isWrappedAroundNeck() {
      return (Boolean)this.entityData.get(IS_ON_NECK);
   }

   public void setEntityAroundNeck() {
      if (!this.isWrappedAroundNeck()) {
         this.entityData.set(IS_ON_NECK, true);
         if (!this.level().isClientSide()) {
            Player owner = this.getOwner();
            if (owner != null && owner.hasData(DAAttachments.PLAYER)) {
               DAPlayerAttachment attachment = (DAPlayerAttachment)owner.getData(DAAttachments.PLAYER);
               attachment.setSynched(owner.getId(), Direction.CLIENT, "setFloatyScarfWrappedAroundNeck", true);
            }
         }
      }
   }

   public void removeEntityAroundNeck() {
      this.rideCooldownCounter = 0;
      if (this.isWrappedAroundNeck()) {
         this.entityData.set(IS_ON_NECK, false);
         if (!this.level().isClientSide()) {
            Player owner = this.getOwner();
            if (owner != null) {
               this.setPos(owner.getX(), this.getOwner().getY() + 1.2, this.getOwner().getZ());
               if (owner.hasData(DAAttachments.PLAYER)) {
                  DAPlayerAttachment attachment = (DAPlayerAttachment)owner.getData(DAAttachments.PLAYER);
                  attachment.setSynched(owner.getId(), Direction.CLIENT, "setFloatyScarfWrappedAroundNeck", false);
               }
            }
         }
      }
   }

   public static class FollowPlayerGoal extends Goal {
      private final GentleWind eots;

      public FollowPlayerGoal(GentleWind eots) {
         this.eots = eots;
         this.setFlags(EnumSet.of(Flag.TARGET));
      }

      public boolean canUse() {
         LivingEntity livingentity = this.eots.getOwner();
         if (livingentity == null) {
            this.eots.discard();
            return false;
         } else {
            SlotEntryReference reference = DAEquipmentUtil.getFloatyScarf(livingentity);
            if (reference == null) {
               this.eots.discard();
               return false;
            } else {
               FloatyScarf scarf = (FloatyScarf)reference.stack().get(DADataComponentTypes.FLOATY_SCARF);
               if (scarf != null && scarf.uuid() == this.eots.getId()) {
                  return !this.eots.isWrappedAroundNeck();
               } else {
                  this.eots.discard();
                  return false;
               }
            }
         }
      }

      public boolean canContinueToUse() {
         return false;
      }

      public void start() {
         this.eots.followOwner();
      }
   }

   protected static class GentleWindAirChargeGoal extends Goal {
      GentleWind gentleWind;
      private int attackTimer = 10;
      private int attackDelay = 10;
      private int numberOfAttacks = 0;

      public GentleWindAirChargeGoal(GentleWind gentleWind) {
         this.gentleWind = gentleWind;
      }

      public boolean canUse() {
         if (this.gentleWind.getTarget() == null || !this.gentleWind.getTarget().isAlive() || !this.gentleWind.hasLineOfSight(this.gentleWind.getTarget())) {
            return false;
         } else if (this.attackTimer > 0) {
            this.attackTimer--;
            return false;
         } else {
            this.attackTimer = 2 + this.gentleWind.getRandom().nextInt(10);
            return true;
         }
      }

      public boolean canContinueToUse() {
         if (this.attackDelay < -2) {
            return false;
         } else {
            LivingEntity livingentity = this.gentleWind.getTarget();
            return livingentity != null && livingentity.isAlive();
         }
      }

      public void start() {
         this.gentleWind.removeEntityAroundNeck();
         this.attackDelay = 10;
         this.numberOfAttacks = this.gentleWind.random.nextInt(2);
         super.start();
      }

      public void tick() {
         if (this.gentleWind.getTarget() != null) {
            this.lookAt(this.gentleWind.getTarget());
            if (this.attackDelay <= 0) {
               new WindCrystal(
                  this.gentleWind.level(),
                  this.gentleWind,
                  this.gentleWind.getLookAngle().multiply(0.699999988079071, 0.699999988079071, 0.699999988079071).offsetRandom(this.gentleWind.random, 0.15F),
                  true
               );
               this.gentleWind
                  .level()
                  .playSound(null, this.gentleWind.getX(), this.gentleWind.getY(), this.gentleWind.getZ(), DASounds.EOTS_SHOOT, SoundSource.HOSTILE, 1.0F, 2.0F);
               if (this.numberOfAttacks > 0) {
                  this.numberOfAttacks--;
                  this.attackDelay = 9;
               } else {
                  this.attackDelay = -3;
               }
            } else {
               this.attackDelay--;
            }
         }
      }

      private void lookAt(LivingEntity target) {
         double d0 = target.getX() - this.gentleWind.getX();
         double d1 = target.getEyeY() - this.gentleWind.getEyeY();
         double d2 = target.getZ() - this.gentleWind.getZ();
         double d3 = Math.sqrt(d0 * d0 + d2 * d2);
         this.gentleWind.setXRot(Mth.wrapDegrees((float)(-(Mth.atan2(d1, d3) * 180.0 / 3.1415927410125732))));
         this.gentleWind.setYRot(Mth.wrapDegrees((float)(Mth.atan2(d2, d0) * 180.0 / 3.1415927410125732) - 90.0F));
         this.gentleWind.setYHeadRot(this.gentleWind.getYRot());
         this.gentleWind.xRotO = this.gentleWind.getXRot();
         this.gentleWind.yRotO = this.gentleWind.getYRot();
      }
   }

   protected static class GentleWindLookControl extends LookControl {
      public GentleWindLookControl(GentleWind gentleWind) {
         super(gentleWind);
      }

      public void tick() {
      }
   }

   public static class GentleWindMoveControl extends MoveControl {
      private float speed = 0.1F;
      private final GentleWind gentleWind;

      public GentleWindMoveControl(GentleWind gentleWind) {
         super(gentleWind);
         this.gentleWind = gentleWind;
      }

      public void tick() {
         if (this.gentleWind.horizontalCollision) {
            this.gentleWind.setYRot(this.gentleWind.getYRot() + 180.0F);
            this.speed = 0.1F;
         }

         double d0 = this.getWantedX() - this.gentleWind.getX();
         double d1 = this.getWantedY() - this.gentleWind.getY();
         double d2 = this.getWantedZ() - this.gentleWind.getZ();
         double d3 = Math.sqrt(d0 * d0 + d2 * d2);
         if (Math.abs(d3) > 9.999999747378752E-6) {
            double d4 = 1.0 - Math.abs(d1 * 0.699999988079071) / d3;
            d0 *= d4;
            d2 *= d4;
            d3 = Math.sqrt(d0 * d0 + d2 * d2);
            double d5 = Math.sqrt(d0 * d0 + d2 * d2 + d1 * d1);
            float f = this.gentleWind.getYRot();
            float f1 = (float)Mth.atan2(d2, d0);
            float f2 = Mth.wrapDegrees(this.gentleWind.getYRot() + 90.0F);
            float f3 = Mth.wrapDegrees(f1 * 57.295776F);
            this.gentleWind.setYRot(Mth.approachDegrees(f2, f3, 4.0F) - 90.0F);
            this.gentleWind.yBodyRot = this.gentleWind.getYRot();
            if (Mth.degreesDifferenceAbs(f, this.gentleWind.getYRot()) < 3.0F) {
               this.speed = Mth.approach(this.speed, 1.8F, 0.005F * (1.8F / this.speed));
            } else {
               this.speed = Mth.approach(this.speed, 0.2F, 0.025F);
            }

            float f4 = (float)(-(Mth.atan2(-d1, d3) * 180.0 / 3.1415927410125732));
            this.gentleWind.setXRot(f4);
            float f5 = this.gentleWind.getYRot() + 90.0F;
            double d6 = this.speed * Mth.cos(f5 * 0.017453292F) * Math.abs(d0 / d5);
            double d7 = this.speed * Mth.sin(f5 * 0.017453292F) * Math.abs(d2 / d5);
            double d8 = this.speed * Mth.sin(f4 * 0.017453292F) * Math.abs(d1 / d5);
            Vec3 vec3 = this.gentleWind.getDeltaMovement();
            this.gentleWind.setDeltaMovement(vec3.add(new Vec3(d6, d8, d7).subtract(vec3).scale(0.2)));
         }
      }
   }

   public static class GentleWindOwnerHurtByTargetGoal extends TargetGoal {
      private final GentleWind gentleWind;
      private LivingEntity ownerLastHurtBy;
      private int timestamp;

      public GentleWindOwnerHurtByTargetGoal(GentleWind gentleWind) {
         super(gentleWind, false);
         this.gentleWind = gentleWind;
         this.setFlags(EnumSet.of(Flag.TARGET));
      }

      public boolean canUse() {
         LivingEntity livingentity = this.gentleWind.getOwner();
         if (livingentity == null) {
            return false;
         } else {
            this.ownerLastHurtBy = livingentity.getLastHurtByMob();
            int i = livingentity.getLastHurtByMobTimestamp();
            return i != this.timestamp
               && this.canAttack(this.ownerLastHurtBy, TargetingConditions.DEFAULT)
               && this.gentleWind.wantsToAttack(this.ownerLastHurtBy, livingentity);
         }
      }

      public void start() {
         this.gentleWind.removeEntityAroundNeck();
         this.mob.setTarget(this.ownerLastHurtBy);
         LivingEntity livingentity = this.gentleWind.getOwner();
         if (livingentity != null) {
            this.timestamp = livingentity.getLastHurtByMobTimestamp();
         }

         super.start();
      }
   }

   public static class GentleWindOwnerHurtTargetGoal extends TargetGoal {
      private final GentleWind gentleWind;
      private LivingEntity ownerLastHurt;
      private int timestamp;

      public GentleWindOwnerHurtTargetGoal(GentleWind gentleWind) {
         super(gentleWind, false);
         this.gentleWind = gentleWind;
         this.setFlags(EnumSet.of(Flag.TARGET));
      }

      public boolean canUse() {
         LivingEntity livingentity = this.gentleWind.getOwner();
         if (livingentity == null) {
            return false;
         } else {
            this.ownerLastHurt = livingentity.getLastHurtMob();
            int i = livingentity.getLastHurtMobTimestamp();
            return i != this.timestamp
               && this.canAttack(this.ownerLastHurt, TargetingConditions.DEFAULT)
               && this.gentleWind.wantsToAttack(this.ownerLastHurt, livingentity);
         }
      }

      public void start() {
         this.gentleWind.removeEntityAroundNeck();
         this.mob.setTarget(this.ownerLastHurt);
         LivingEntity livingentity = this.gentleWind.getOwner();
         if (livingentity != null) {
            this.timestamp = livingentity.getLastHurtMobTimestamp();
         }

         super.start();
      }
   }

   public static class WrapAroundPlayerGoal extends Goal {
      private final GentleWind eots;
      private ServerPlayer owner;

      public WrapAroundPlayerGoal(GentleWind eots) {
         this.eots = eots;
         this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
      }

      public boolean canContinueToUse() {
         return this.eots.isWrappedAroundNeck() && this.eots.getTarget() == null;
      }

      public boolean canUse() {
         ServerPlayer serverplayer = this.eots.getServerOwner();
         if (serverplayer == null) {
            return false;
         } else {
            boolean flag = !serverplayer.isSpectator() && !serverplayer.isInWater() && !serverplayer.isInPowderSnow;
            return flag && !this.eots.isWrappedAroundNeck() && this.eots.rideCooldownCounter > 300 && this.eots.getTarget() == null;
         }
      }

      public void start() {
         this.owner = this.eots.getServerOwner();
      }

      public void stop() {
         this.eots.removeEntityAroundNeck();
      }

      public void tick() {
         if (!this.eots.isWrappedAroundNeck() && this.eots.getBoundingBox().intersects(this.owner.getBoundingBox().inflate(1.3, 2.0, 1.3))) {
            this.eots.setEntityAroundNeck();
         }
      }
   }
}
