package vazkii.psi.common.spell.operator.entity;

import java.util.List;
import java.util.Optional;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamEntity;
import vazkii.psi.api.spell.piece.PieceOperator;
import vazkii.psi.common.spell.operator.vector.PieceOperatorVectorRaycast;

public class PieceOperatorFocusedEntity extends PieceOperator {
   SpellParam<Entity> target;

   public PieceOperatorFocusedEntity(Spell spell) {
      super(spell);
   }

   public static Entity getEntityLookedAt(Entity e) {
      Entity foundEntity = null;
      double finalDistance = 32.0;
      HitResult pos = PieceOperatorVectorRaycast.raycast(e, 32.0);
      Vec3 positionVector = e.position();
      if (e instanceof Player) {
         positionVector = positionVector.add(0.0, e.getEyeHeight(), 0.0);
      }

      double distance = pos.getLocation().distanceTo(positionVector);
      Vec3 lookVector = e.getLookAngle();
      Vec3 reachVector = positionVector.add(lookVector.x * 32.0, lookVector.y * 32.0, lookVector.z * 32.0);
      Entity lookedEntity = null;
      List<Entity> entitiesInBoundingBox = e.getCommandSenderWorld()
         .getEntities(e, e.getBoundingBox().inflate(lookVector.x * 32.0, lookVector.y * 32.0, lookVector.z * 32.0).inflate(1.0, 1.0, 1.0));
      double minDistance = distance;

      for (Entity entity : entitiesInBoundingBox) {
         if (entity.isPickable()) {
            float collisionBorderSize = entity.getPickRadius();
            AABB hitbox = entity.getBoundingBox().inflate(collisionBorderSize, collisionBorderSize, collisionBorderSize);
            Optional<Vec3> interceptPosition = hitbox.clip(positionVector, reachVector);
            if (hitbox.contains(positionVector)) {
               if (0.0 < minDistance || minDistance == 0.0) {
                  lookedEntity = entity;
                  minDistance = 0.0;
               }
            } else if (interceptPosition.isPresent()) {
               double distanceToEntity = positionVector.distanceTo(interceptPosition.get());
               if (distanceToEntity < minDistance || minDistance == 0.0) {
                  lookedEntity = entity;
                  minDistance = distanceToEntity;
               }
            }
         }

         if (lookedEntity != null && minDistance < distance) {
            foundEntity = lookedEntity;
         }
      }

      return foundEntity;
   }

   @Override
   public void initParams() {
      this.addParam(this.target = new ParamEntity("psi.spellparam.target", 13814826, false, false));
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      Entity e = this.getParamValue(context, this.target);
      if (e == null) {
         throw new SpellRuntimeException("psi.spellerror.nulltarget");
      } else {
         Entity looked = getEntityLookedAt(e);
         if (looked == null) {
            throw new SpellRuntimeException("psi.spellerror.nulltarget");
         } else {
            return looked;
         }
      }
   }

   @Override
   public Class<?> getEvaluationType() {
      return Entity.class;
   }
}
