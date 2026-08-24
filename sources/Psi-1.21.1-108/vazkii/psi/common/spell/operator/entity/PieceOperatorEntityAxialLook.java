package vazkii.psi.common.spell.operator.entity;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamEntity;
import vazkii.psi.api.spell.piece.PieceOperator;

public class PieceOperatorEntityAxialLook extends PieceOperator {
   SpellParam<Entity> target;

   public PieceOperatorEntityAxialLook(Spell spell) {
      super(spell);
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
         Vec3 look = e.getViewVector(1.0F);
         Direction facing = Direction.getNearest((float)look.x, (float)look.y, (float)look.z);
         return new Vector3(facing.getStepX(), facing.getStepY(), facing.getStepZ());
      }
   }

   @Override
   public Class<?> getEvaluationType() {
      return Vector3.class;
   }
}
