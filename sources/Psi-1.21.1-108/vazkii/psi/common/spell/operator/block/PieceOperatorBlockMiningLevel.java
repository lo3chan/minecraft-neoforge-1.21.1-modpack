package vazkii.psi.common.spell.operator.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellHelpers;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceOperator;
import vazkii.psi.common.spell.trick.block.PieceTrickBreakBlock;

public class PieceOperatorBlockMiningLevel extends PieceOperator {
   SpellParam<Vector3> position;

   public PieceOperatorBlockMiningLevel(Spell spell) {
      super(spell);
   }

   @Override
   public void initParams() {
      this.addParam(this.position = new ParamVector("psi.spellparam.position", 2774482, false, false));
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      BlockPos pos = SpellHelpers.getBlockPos(this, context, this.position, false, false);
      BlockState state = context.focalPoint.level().getBlockState(pos);
      return PieceTrickBreakBlock.getHarvestLevel(state);
   }

   @Override
   public Class<?> getEvaluationType() {
      return Double.class;
   }
}
