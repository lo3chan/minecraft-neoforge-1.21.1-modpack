package vazkii.psi.common.spell.operator.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComparatorBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellHelpers;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceOperator;

public class PieceOperatorBlockComparatorStrength extends PieceOperator {
   SpellParam<Vector3> axisParam;
   SpellParam<Vector3> target;

   public PieceOperatorBlockComparatorStrength(Spell spell) {
      super(spell);
   }

   @Override
   public void initParams() {
      this.addParam(this.target = new ParamVector("psi.spellparam.target", 13773354, false, false));
      this.addParam(this.axisParam = new ParamVector("psi.spellparam.vector", 2774482, false, false));
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      BlockPos pos = SpellHelpers.getBlockPos(this, context, this.target, false, false);
      Direction whichWay = SpellHelpers.getFacing(this, context, this.axisParam);
      if (whichWay != Direction.UP && whichWay != Direction.DOWN) {
         BlockState state = (BlockState)Blocks.COMPARATOR.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, whichWay.getOpposite());
         return ((ComparatorBlock)Blocks.COMPARATOR).getInputSignal(context.focalPoint.level(), pos.relative(whichWay), state) * 1.0;
      } else {
         throw new SpellRuntimeException("psi.spellerror.comparator");
      }
   }

   @Override
   public Class<Double> getEvaluationType() {
      return Double.class;
   }
}
