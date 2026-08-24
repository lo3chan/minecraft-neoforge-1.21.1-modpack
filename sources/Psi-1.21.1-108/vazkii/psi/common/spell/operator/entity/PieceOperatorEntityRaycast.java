package vazkii.psi.common.spell.operator.entity;

import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.ISpellImmune;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellHelpers;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceOperator;

public class PieceOperatorEntityRaycast extends PieceOperator {
   SpellParam<Vector3> origin;
   SpellParam<Vector3> ray;
   SpellParam<Number> max;

   public PieceOperatorEntityRaycast(Spell spell) {
      super(spell);
   }

   public static Entity rayTraceEntities(Level world, Vec3 positionVector, Vec3 lookVector, Predicate<Entity> predicate, double maxDistance) {
      double distance = maxDistance;
      Entity entity = null;
      Vec3 reachVector = positionVector.add(lookVector.scale(maxDistance));
      AABB aabb = new AABB(positionVector.x, positionVector.y, positionVector.z, reachVector.x, reachVector.y, reachVector.z).inflate(1.0, 1.0, 1.0);

      for (Entity entity1 : world.getEntities((Entity)null, aabb, predicate)) {
         float collisionBorderSize = entity1.getPickRadius();
         AABB axisalignedbb = entity1.getBoundingBox().inflate(collisionBorderSize);
         Optional<Vec3> optional = axisalignedbb.clip(positionVector, reachVector);
         if (axisalignedbb.contains(positionVector)) {
            if (0.0 < distance || distance == 0.0) {
               entity = entity1;
               distance = 0.0;
            }
         } else if (optional.isPresent()) {
            double distanceTo = positionVector.distanceTo(optional.get());
            if (distanceTo < distance) {
               entity = entity1;
               distance = distanceTo;
            }
         }
      }

      return entity;
   }

   @Override
   public void initParams() {
      this.addParam(this.origin = new ParamVector("psi.spellparam.position", 2774482, false, false));
      this.addParam(this.ray = new ParamVector("psi.spellparam.ray", 4117034, false, false));
      this.addParam(this.max = new ParamNumber("psi.spellparam.max", 7678674, true, false));
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      Vector3 originVal = this.getParamValue(context, this.origin);
      Vector3 rayVal = this.getParamValue(context, this.ray);
      if (originVal != null && rayVal != null) {
         double maxLen = SpellHelpers.rangeLimitParam(this, context, this.max, 32.0);
         Entity entity = rayTraceEntities(
            context.focalPoint.level(),
            originVal.toVec3D(),
            rayVal.toVec3D(),
            pred -> !pred.isSpectator() && pred.isAlive() && pred.isPickable() && !(pred instanceof ISpellImmune),
            maxLen
         );
         if (entity == null) {
            throw new SpellRuntimeException("psi.spellerror.nulltarget");
         } else {
            return entity;
         }
      } else {
         throw new SpellRuntimeException("psi.spellerror.nullvector");
      }
   }

   @Override
   public Class<?> getEvaluationType() {
      return Entity.class;
   }
}
