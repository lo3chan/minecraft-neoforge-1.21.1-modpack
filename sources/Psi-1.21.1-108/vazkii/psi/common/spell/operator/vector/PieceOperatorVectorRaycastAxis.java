package vazkii.psi.common.spell.operator.vector;

import net.minecraft.core.Direction;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellHelpers;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceOperator;

public class PieceOperatorVectorRaycastAxis extends PieceOperator {
   SpellParam<Vector3> origin;
   SpellParam<Vector3> ray;
   SpellParam<Number> max;

   public PieceOperatorVectorRaycastAxis(Spell spell) {
      super(spell);
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
         Vector3 end = originVal.copy().add(rayVal.copy().normalize().multiply(maxLen));
         BlockHitResult pos = context.caster
            .getCommandSenderWorld()
            .clip(new ClipContext(originVal.toVec3D(), end.toVec3D(), Block.OUTLINE, Fluid.NONE, context.caster));
         if (pos.getType() == Type.MISS) {
            throw new SpellRuntimeException("psi.spellerror.nullvector");
         } else {
            Direction facing = pos.getDirection();
            return new Vector3(facing.getStepX(), facing.getStepY(), facing.getStepZ());
         }
      } else {
         throw new SpellRuntimeException("psi.spellerror.nullvector");
      }
   }

   @Override
   public Class<?> getEvaluationType() {
      return Vector3.class;
   }
}
