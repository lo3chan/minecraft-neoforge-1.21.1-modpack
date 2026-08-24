package net.bettercombat.client.collision;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import net.bettercombat.BetterCombatMod;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.api.client.AttackRangeExtensions;
import net.bettercombat.logic.TargetHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class TargetFinder {
   public static TargetFinder.TargetResult findAttackTargetResult(Player player, Entity cursorTarget, WeaponAttributes.Attack attack, double attackRange) {
      Vec3 origin = getInitialTracingPoint(player);
      List<Entity> entities = getInitialTargets(player, cursorTarget, attackRange);
      if (!AttackRangeExtensions.sources().isEmpty()) {
         attackRange = applyAttackRangeModifiers(player, attackRange);
      }

      boolean isSpinAttack = attack.angle() > 180.0;
      Vec3 size = WeaponHitBoxes.createHitbox(attack.hitbox(), attackRange, isSpinAttack);
      OrientedBoundingBox obb = new OrientedBoundingBox(origin, size, player.getXRot(), player.getYRot());
      if (!isSpinAttack) {
         obb = obb.offsetAlongAxisZ(size.z / 2.0);
      }

      obb.updateVertex();
      TargetFinder.CollisionFilter collisionFilter = new TargetFinder.CollisionFilter(obb);
      entities = collisionFilter.filter(entities);
      TargetFinder.RadialFilter radialFilter = new TargetFinder.RadialFilter(origin, obb.axisZ, attackRange, attack.angle());
      entities = radialFilter.filter(entities);
      return new TargetFinder.TargetResult(cursorTarget, entities, obb);
   }

   private static double applyAttackRangeModifiers(Player player, double attackRange) {
      AttackRangeExtensions.Context context = new AttackRangeExtensions.Context(player, attackRange);
      List<AttackRangeExtensions.Modifier> modifiers = AttackRangeExtensions.sources()
         .stream()
         .map(function -> function.apply(context))
         .sorted(Comparator.comparingInt(AttackRangeExtensions.Modifier::operationOrder))
         .toList();
      double result = attackRange;

      for (AttackRangeExtensions.Modifier modifier : modifiers) {
         switch (modifier.operation()) {
            case ADD:
               result += modifier.value();
               break;
            case MULTIPLY:
               result *= modifier.value();
         }
      }

      return result;
   }

   public static List<Entity> findAttackTargets(Player player, Entity cursorTarget, WeaponAttributes.Attack attack, double attackRange) {
      return findAttackTargetResult(player, cursorTarget, attack, attackRange).entities;
   }

   public static Vec3 getInitialTracingPoint(Player player) {
      double shoulderHeight = player.getBbHeight() * 0.15 * player.getAgeScale();
      return player.getEyePosition().subtract(0.0, shoulderHeight, 0.0);
   }

   public static List<Entity> getInitialTargets(Player player, Entity cursorTarget, double attackRange) {
      AABB box = player.getBoundingBox().inflate(attackRange * BetterCombatMod.config.target_search_range_multiplier + 1.0);
      List<Entity> entities = player.level()
         .getEntities(player, box, entity -> !entity.isSpectator() && entity.isPickable())
         .stream()
         .filter(
            entity -> entity != player
               && entity.isAttackable()
               && entity != cursorTarget
               && TargetHelper.isHitAllowed(false, TargetHelper.getRelation(player, entity))
               && (!entity.equals(player.getVehicle()) || TargetHelper.isAttackableMount(entity))
         )
         .collect(Collectors.toList());
      if (cursorTarget != null && cursorTarget.isAttackable()) {
         entities.add(cursorTarget);
      }

      return entities;
   }

   public static class CollisionFilter implements TargetFinder.Filter {
      private OrientedBoundingBox obb;

      public CollisionFilter(OrientedBoundingBox obb) {
         this.obb = obb;
      }

      @Override
      public List<Entity> filter(List<Entity> entities) {
         return entities.stream()
            .filter(
               entity -> this.obb.intersects(entity.getBoundingBox().inflate(entity.getPickRadius()))
                  || this.obb.contains(entity.position().add(0.0, entity.getBbHeight() / 2.0F, 0.0))
            )
            .collect(Collectors.toList());
      }
   }

   public interface Filter {
      List<Entity> filter(List<Entity> var1);
   }

   public static class RadialFilter implements TargetFinder.Filter {
      private final Vec3 origin;
      private final Vec3 orientation;
      private final double attackRange;
      private final double attackAngle;

      public RadialFilter(Vec3 origin, Vec3 orientation, double attackRange, double attackAngle) {
         this.origin = origin;
         this.orientation = orientation;
         this.attackRange = attackRange;
         this.attackAngle = Mth.clamp(attackAngle, 0.0, 360.0);
      }

      @Override
      public List<Entity> filter(List<Entity> entities) {
         return entities.stream()
            .filter(
               entity -> {
                  double maxAngleDif = this.attackAngle / 2.0;
                  Vec3 distanceVector = CollisionHelper.distanceVector(this.origin, entity.getBoundingBox());
                  Vec3 positionVector = entity.position().add(0.0, entity.getBbHeight() / 2.0F, 0.0).subtract(this.origin);
                  return distanceVector.length() <= this.attackRange
                     && (
                        this.attackAngle == 0.0
                           || CollisionHelper.angleBetween(positionVector, this.orientation) <= maxAngleDif
                           || CollisionHelper.angleBetween(distanceVector, this.orientation) <= maxAngleDif
                     )
                     && (
                        BetterCombatMod.config.allow_attacking_thru_walls
                           || rayContainsNoObstacle(this.origin, this.origin.add(distanceVector))
                           || rayContainsNoObstacle(this.origin, this.origin.add(positionVector))
                     );
               }
            )
            .collect(Collectors.toList());
      }

      private static boolean rayContainsNoObstacle(Vec3 start, Vec3 end) {
         Minecraft client = Minecraft.getInstance();
         BlockHitResult hit = null;
         if (client.level != null) {
            hit = client.level.clip(new ClipContext(start, end, Block.COLLIDER, Fluid.NONE, client.player));
         }

         return hit != null ? hit.getType() != Type.BLOCK : false;
      }
   }

   public static class TargetResult {
      public Entity cursorTarget;
      public List<Entity> entities;
      public OrientedBoundingBox obb;

      public TargetResult(Entity cursorTarget, List<Entity> entities, OrientedBoundingBox obb) {
         this.entities = entities;
         this.obb = obb;
      }
   }
}
