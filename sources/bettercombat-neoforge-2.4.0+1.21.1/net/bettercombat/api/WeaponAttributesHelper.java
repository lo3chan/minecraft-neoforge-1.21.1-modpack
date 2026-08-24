package net.bettercombat.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import java.io.InvalidObjectException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import net.bettercombat.api.fx.ConditionalTrailAppearance;

public class WeaponAttributesHelper {
   private static Type attributesContainerFileFormat = (new TypeToken<AttributesContainer>() {}).getType();

   public static WeaponAttributes override(WeaponAttributes a, WeaponAttributes b) {
      double attackRange = b.attackRange() > 0.0 ? b.attackRange() : a.attackRange();
      double rangeBonus = b.rangeBonus() != 0.0 ? b.rangeBonus() : a.rangeBonus();
      String pose = b.pose() != null ? b.pose() : a.pose();
      String off_hand_pose = b.offHandPose() != null ? b.offHandPose() : a.offHandPose();
      Boolean isTwoHanded = b.two_handed() != null ? b.two_handed() : a.two_handed();
      String category = b.category() != null ? b.category() : a.category();
      WeaponAttributes.Attack[] attacks = a.attacks();
      if (b.attacks() != null && b.attacks().length > 0) {
         ArrayList<WeaponAttributes.Attack> overrideAttacks = new ArrayList<>();

         for (int i = 0; i < b.attacks().length; i++) {
            WeaponAttributes.Attack base = a.attacks() != null && a.attacks().length > i ? a.attacks()[i] : WeaponAttributes.Attack.empty();
            WeaponAttributes.Attack override = b.attacks()[i];
            WeaponAttributes.Attack attack = new WeaponAttributes.Attack(
               override.conditions() != null ? override.conditions() : base.conditions(),
               override.hitbox() != null ? override.hitbox() : base.hitbox(),
               override.damageMultiplier() != 0.0 ? override.damageMultiplier() : base.damageMultiplier(),
               override.movementSpeedMultiplier() != 0.0F ? override.movementSpeedMultiplier() : base.movementSpeedMultiplier(),
               override.rangeMultiplier() != 0.0F ? override.rangeMultiplier() : base.rangeMultiplier(),
               override.angle() != 0.0 ? override.angle() : base.angle(),
               override.upswing() != 0.0 ? override.upswing() : base.upswing(),
               override.animation() != null ? override.animation() : base.animation(),
               override.swingSound() != null ? override.swingSound() : base.swingSound(),
               override.impactSound() != null ? override.impactSound() : base.impactSound(),
               override.trailParticles() != null && !override.trailParticles().isEmpty() ? override.trailParticles() : base.trailParticles()
            );
            overrideAttacks.add(attack);
         }

         attacks = overrideAttacks.toArray(new WeaponAttributes.Attack[0]);
      }

      ConditionalTrailAppearance trailAppearance = b.trailAppearance() != null ? b.trailAppearance() : a.trailAppearance();
      return new WeaponAttributes(attackRange, rangeBonus, pose, off_hand_pose, isTwoHanded, category, attacks, trailAppearance);
   }

   public static void validate(WeaponAttributes attributes) throws Exception {
      if (attributes.attacks() != null) {
         if (attributes.attacks().length > 0) {
            int index = 0;

            for (WeaponAttributes.Attack attack : attributes.attacks()) {
               try {
                  validate(attack);
               } catch (InvalidObjectException var8) {
                  String message = "Invalid attack at index:" + index + " - " + var8.getMessage();
                  throw new InvalidObjectException(message);
               }

               index++;
            }
         }
      }
   }

   private static void validate(WeaponAttributes.Attack attack) throws InvalidObjectException {
      if (attack.hitbox() == null) {
         throw new InvalidObjectException("Undefined `hitbox`");
      } else if (attack.damageMultiplier() < 0.0) {
         throw new InvalidObjectException("Invalid `damage_multiplier`");
      } else if (attack.angle() < 0.0) {
         throw new InvalidObjectException("Invalid `angle`");
      } else if (attack.upswing() < 0.0) {
         throw new InvalidObjectException("Invalid `upswing`");
      } else if (attack.animation() == null || attack.animation().length() == 0) {
         throw new InvalidObjectException("Undefined `animation`");
      }
   }

   public static AttributesContainer decode(Reader reader) {
      Gson gson = new Gson();
      return (AttributesContainer)gson.fromJson(reader, attributesContainerFileFormat);
   }

   public static AttributesContainer decode(JsonReader json) {
      Gson gson = new Gson();
      return (AttributesContainer)gson.fromJson(json, attributesContainerFileFormat);
   }

   public static String encode(AttributesContainer container) {
      Gson gson = new Gson();
      return gson.toJson(container);
   }
}
