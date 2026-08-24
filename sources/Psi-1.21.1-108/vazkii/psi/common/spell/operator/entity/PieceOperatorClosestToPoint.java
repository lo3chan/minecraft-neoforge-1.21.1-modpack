package vazkii.psi.common.spell.operator.entity;

import net.minecraft.world.entity.Entity;
import vazkii.psi.api.internal.MathHelper;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamEntityListWrapper;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceOperator;
import vazkii.psi.api.spell.wrapper.EntityListWrapper;

public class PieceOperatorClosestToPoint extends PieceOperator {
   SpellParam<Vector3> position;
   SpellParam<EntityListWrapper> list;

   public PieceOperatorClosestToPoint(Spell spell) {
      super(spell);
   }

   public static Entity closestToPoint(Vector3 position, Iterable<Entity> list) throws SpellRuntimeException {
      double closest = 1.7976931348623157E308;
      Entity closestEntity = null;

      for (Entity e : list) {
         double dist = MathHelper.pointDistanceSpace(position.x, position.y, position.z, e.getX(), e.getY(), e.getZ());
         if (dist < closest) {
            closest = dist;
            closestEntity = e;
         }
      }

      if (closestEntity == null) {
         throw new SpellRuntimeException("psi.spellerror.nulltarget");
      } else {
         return closestEntity;
      }
   }

   @Override
   public void initParams() {
      this.addParam(this.position = new ParamVector("psi.spellparam.position", 2774482, false, false));
      this.addParam(this.list = new ParamEntityListWrapper("psi.spellparam.target", 13814826, false, false));
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      EntityListWrapper listVal = this.getParamValue(context, this.list);
      Vector3 positionVal = this.getParamValue(context, this.position);
      return closestToPoint(positionVal, listVal);
   }

   @Override
   public Class<?> getEvaluationType() {
      return Entity.class;
   }
}
