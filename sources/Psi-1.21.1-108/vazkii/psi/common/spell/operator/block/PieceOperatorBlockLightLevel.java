package vazkii.psi.common.spell.operator.block;

import net.minecraft.core.BlockPos;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellHelpers;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceOperator;

public class PieceOperatorBlockLightLevel extends PieceOperator {
   SpellParam<Vector3> target;

   public PieceOperatorBlockLightLevel(Spell spell) {
      super(spell);
   }

   @Override
   public void initParams() {
      this.addParam(this.target = new ParamVector("psi.spellparam.target", 13773354, false, false));
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      BlockPos pos = SpellHelpers.getBlockPos(this, context, this.target, false, false);
      int j = context.focalPoint.level().getMaxLocalRawBrightness(pos);
      return j * 1.0;
   }

   @Override
   public Class<?> getEvaluationType() {
      return Double.class;
   }
}
