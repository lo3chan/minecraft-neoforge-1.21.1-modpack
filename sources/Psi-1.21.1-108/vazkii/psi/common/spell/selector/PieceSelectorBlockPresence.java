package vazkii.psi.common.spell.selector;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceSelector;

public class PieceSelectorBlockPresence extends PieceSelector {
   SpellParam<Vector3> position;

   public PieceSelectorBlockPresence(Spell spell) {
      super(spell);
   }

   @Override
   public void initParams() {
      this.addParam(this.position = new ParamVector("psi.spellparam.position", 2774482, false, false));
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      Vector3 positionVal = this.getParamValue(context, this.position);
      if (positionVal == null) {
         throw new SpellRuntimeException("psi.spellerror.nullvector");
      } else {
         BlockPos pos = positionVal.toBlockPos();
         BlockState state = context.caster.getCommandSenderWorld().getBlockState(pos);
         Block block = state.getBlock();
         if (!state.isAir() && !state.canBeReplaced()) {
            return state.getCollisionShape(context.caster.getCommandSenderWorld(), pos, CollisionContext.of(context.caster)).isEmpty() ? 1.0 : 2.0;
         } else {
            return 0.0;
         }
      }
   }

   @Override
   public Class<?> getEvaluationType() {
      return Double.class;
   }
}
