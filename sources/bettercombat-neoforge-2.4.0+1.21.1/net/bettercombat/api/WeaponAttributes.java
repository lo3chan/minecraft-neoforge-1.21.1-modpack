package net.bettercombat.api;

import java.util.List;
import java.util.Objects;
import net.bettercombat.api.fx.ConditionalTrailAppearance;
import net.bettercombat.api.fx.ParticlePlacement;
import org.jetbrains.annotations.Nullable;

public final class WeaponAttributes {
   private final double attack_range;
   private final double range_bonus;
   @Nullable
   private final String pose;
   @Nullable
   private final String off_hand_pose;
   private final Boolean two_handed;
   @Nullable
   private final String category;
   @Nullable
   private final WeaponAttributes.Attack[] attacks;
   @Nullable
   private ConditionalTrailAppearance trail_appearance;

   public static WeaponAttributes empty() {
      return new WeaponAttributes(0.0, 0.0, null, null, false, null, null, null);
   }

   public WeaponAttributes(
      double attack_range,
      double range_bonus,
      @Nullable String pose,
      @Nullable String off_hand_pose,
      Boolean isTwoHanded,
      String category,
      WeaponAttributes.Attack[] attacks,
      ConditionalTrailAppearance trail_appearance
   ) {
      this.attack_range = attack_range;
      this.range_bonus = range_bonus;
      this.pose = pose;
      this.off_hand_pose = off_hand_pose;
      this.two_handed = isTwoHanded;
      this.category = category;
      this.attacks = attacks;
      this.trail_appearance = trail_appearance;
   }

   public double attackRange() {
      return this.attack_range;
   }

   public double rangeBonus() {
      return this.range_bonus;
   }

   @Nullable
   public String pose() {
      return this.pose;
   }

   @Nullable
   public String offHandPose() {
      return this.off_hand_pose;
   }

   @Nullable
   public String category() {
      return this.category;
   }

   public boolean isTwoHanded() {
      return this.two_handed != null ? this.two_handed : false;
   }

   public Boolean two_handed() {
      return this.two_handed;
   }

   public WeaponAttributes.Attack[] attacks() {
      return this.attacks;
   }

   @Nullable
   public ConditionalTrailAppearance trailAppearance() {
      return this.trail_appearance;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (obj != null && obj.getClass() == this.getClass()) {
         WeaponAttributes that = (WeaponAttributes)obj;
         return Double.doubleToLongBits(this.attack_range) == Double.doubleToLongBits(that.attack_range)
            && Objects.equals(this.pose, that.pose)
            && Objects.equals(this.two_handed, that.two_handed)
            && Objects.equals(this.attacks, that.attacks);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.attack_range, this.two_handed, this.attacks);
   }

   @Override
   public String toString() {
      return "WeaponAttributes[attack_range="
         + this.attack_range
         + ", pose="
         + this.pose
         + ", isTwoHanded="
         + this.two_handed
         + ", attacks="
         + this.attacks
         + "]";
   }

   public static final class Attack {
      private WeaponAttributes.Condition[] conditions;
      private WeaponAttributes.HitBoxShape hitbox;
      private double damage_multiplier = 1.0;
      private float movement_speed_multiplier = 1.0F;
      private float range_multiplier = 1.0F;
      private double angle = 0.0;
      private double upswing = 0.0;
      private String animation = null;
      private WeaponAttributes.Sound swing_sound = null;
      private WeaponAttributes.Sound impact_sound = null;
      private List<ParticlePlacement> trail_particles = List.of();

      public Attack() {
      }

      public Attack(
         WeaponAttributes.Condition[] conditions,
         WeaponAttributes.HitBoxShape hitbox,
         double damage_multiplier,
         float movement_speed_multiplier,
         float range_multiplier,
         double angle,
         double upswing,
         String animation,
         WeaponAttributes.Sound swing_sound,
         WeaponAttributes.Sound impact_sound,
         List<ParticlePlacement> trail_particles
      ) {
         this.conditions = conditions;
         this.hitbox = hitbox;
         this.damage_multiplier = damage_multiplier;
         this.movement_speed_multiplier = movement_speed_multiplier;
         this.range_multiplier = range_multiplier;
         this.angle = angle;
         this.upswing = upswing;
         this.animation = animation;
         this.swing_sound = swing_sound;
         this.impact_sound = impact_sound;
         this.trail_particles = trail_particles;
      }

      public static WeaponAttributes.Attack empty() {
         return new WeaponAttributes.Attack(null, null, 0.0, 0.0F, 0.0F, 0.0, 0.0, null, null, null, List.of());
      }

      @Nullable
      public WeaponAttributes.Condition[] conditions() {
         return this.conditions;
      }

      public WeaponAttributes.HitBoxShape hitbox() {
         return this.hitbox;
      }

      public double damageMultiplier() {
         return this.damage_multiplier;
      }

      public float movementSpeedMultiplier() {
         return this.movement_speed_multiplier;
      }

      public float rangeMultiplier() {
         return this.range_multiplier;
      }

      public double angle() {
         return this.angle;
      }

      public double upswing() {
         return this.upswing;
      }

      public String animation() {
         return this.animation;
      }

      public WeaponAttributes.Sound swingSound() {
         return this.swing_sound;
      }

      public List<ParticlePlacement> trailParticles() {
         return this.trail_particles;
      }

      public WeaponAttributes.Sound impactSound() {
         return this.impact_sound;
      }

      @Override
      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (obj != null && obj.getClass() == this.getClass()) {
            WeaponAttributes.Attack that = (WeaponAttributes.Attack)obj;
            return Objects.equals(this.hitbox, that.hitbox)
               && Double.doubleToLongBits(this.damage_multiplier) == Double.doubleToLongBits(that.damage_multiplier)
               && Double.doubleToLongBits(this.movement_speed_multiplier) == Double.doubleToLongBits(that.movement_speed_multiplier)
               && Double.doubleToLongBits(this.range_multiplier) == Double.doubleToLongBits(that.range_multiplier)
               && Double.doubleToLongBits(this.angle) == Double.doubleToLongBits(that.angle)
               && Double.doubleToLongBits(this.upswing) == Double.doubleToLongBits(that.upswing)
               && Objects.equals(this.animation, that.animation)
               && Objects.equals(this.swing_sound, that.swing_sound)
               && Objects.equals(this.impact_sound, that.impact_sound);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.hitbox, this.damage_multiplier, this.angle, this.upswing, this.animation, this.swing_sound, this.impact_sound);
      }

      @Override
      public String toString() {
         return "Attack[hitbox="
            + this.hitbox
            + ", damage_multiplier="
            + this.damage_multiplier
            + ", angle="
            + this.angle
            + ", upswing="
            + this.upswing
            + ", animation="
            + this.animation
            + ", swing_sound="
            + this.swing_sound
            + ", impact_sound="
            + this.impact_sound
            + "]";
      }
   }

   public static enum Condition {
      NOT_DUAL_WIELDING,
      DUAL_WIELDING_ANY,
      DUAL_WIELDING_SAME,
      DUAL_WIELDING_SAME_CATEGORY,
      NO_OFFHAND_ITEM,
      OFF_HAND_SHIELD,
      MAIN_HAND_ONLY,
      OFF_HAND_ONLY,
      MOUNTED,
      NOT_MOUNTED;
   }

   public static enum HitBoxShape {
      FORWARD_BOX,
      VERTICAL_PLANE,
      HORIZONTAL_PLANE;
   }

   public static final class Sound {
      private String id = null;
      private float volume = 1.0F;
      private float pitch = 1.0F;
      private float randomness = 0.1F;

      public Sound() {
      }

      public Sound(String id) {
         this.id = id;
      }

      public String id() {
         return this.id;
      }

      public float volume() {
         return this.volume;
      }

      public float pitch() {
         return this.pitch;
      }

      public float randomness() {
         return this.randomness;
      }

      @Override
      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (obj != null && obj.getClass() == this.getClass()) {
            WeaponAttributes.Sound that = (WeaponAttributes.Sound)obj;
            return Objects.equals(this.id, that.id)
               && Float.floatToIntBits(this.volume) == Float.floatToIntBits(that.volume)
               && Float.floatToIntBits(this.pitch) == Float.floatToIntBits(that.pitch)
               && Float.floatToIntBits(this.randomness) == Float.floatToIntBits(that.randomness);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.id, this.volume, this.pitch, this.randomness);
      }

      @Override
      public String toString() {
         return "SoundV2[id=" + this.id + ", volume=" + this.volume + ", pitch=" + this.pitch + ", randomness=" + this.randomness + "]";
      }
   }
}
