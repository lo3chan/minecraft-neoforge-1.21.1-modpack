package vazkii.psi.common.spell.operator.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellHelpers;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamEntityListWrapper;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceOperator;
import vazkii.psi.api.spell.wrapper.EntityListWrapper;

public class PieceOperatorClosestToLine extends PieceOperator {
   SpellParam<Vector3> rayStartParam;
   SpellParam<Vector3> rayEndParam;
   SpellParam<EntityListWrapper> entList;

   public PieceOperatorClosestToLine(Spell spell) {
      super(spell);
   }

   public static Entity closestToLineSegment(Vector3 a, Vector3 b, Iterable<Entity> list) throws SpellRuntimeException {
      if (a.equals(b)) {
         return PieceOperatorClosestToPoint.closestToPoint(a, list);
      } else {
         Vec3 start = a.toVec3D();
         Vec3 end = b.toVec3D();
         Vec3 diff = end.subtract(start).normalize();
         double minDot = diff.dot(start);
         double maxDot = diff.dot(end);
         double minDist = 1.7976931348623157E308;
         Entity found = null;

         for (Entity e : list) {
            Vec3 pos = e.position();
            double dot = diff.dot(pos);
            double dist;
            if (dot <= minDot) {
               dist = pos.subtract(start).length();
            } else if (dot >= maxDot) {
               dist = pos.subtract(end).length();
            } else {
               dist = pos.subtract(start).cross(diff).length();
            }

            if (dist < minDist) {
               minDist = dist;
               found = e;
            }
         }

         if (found == null) {
            throw new SpellRuntimeException("psi.spellerror.nulltarget");
         } else {
            return found;
         }
      }
   }

   @Override
   public void initParams() {
      super.initParams();
      this.addParam(this.rayStartParam = new ParamVector("psi.spellparam.ray_start", 13773354, false, false));
      this.addParam(this.rayEndParam = new ParamVector("psi.spellparam.ray_end", 2774482, false, false));
      this.addParam(this.entList = new ParamEntityListWrapper("psi.spellparam.list", 4117034, false, false));
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      Vector3 rayStart = SpellHelpers.getVector3(this, context, this.rayStartParam, false, false);
      Vector3 rayEnd = SpellHelpers.getVector3(this, context, this.rayEndParam, false, false);
      EntityListWrapper list = this.getNotNullParamValue(context, this.entList);
      return list.size() == 0 ? null : closestToLineSegment(rayStart, rayEnd, list);
   }

   @Override
   public Class<?> getEvaluationType() {
      return Entity.class;
   }
}
