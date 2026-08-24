package vazkii.psi.common.spell.trick;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.StatLabel;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceTrick;

public class PieceTrickExplode extends PieceTrick {
   SpellParam<Vector3> position;
   SpellParam<Number> power;

   public PieceTrickExplode(Spell spell) {
      super(spell);
      this.setStatLabel(EnumSpellStat.POTENCY, new StatLabel("psi.spellparam.power", true).max(0.5).mul(70.0).floor());
      this.setStatLabel(EnumSpellStat.COST, new StatLabel("psi.spellparam.power", true).max(0.5).mul(210.0).floor());
   }

   private static boolean isLiquid(BlockState pState) {
      return pState == Blocks.WATER.defaultBlockState() || pState == Blocks.LAVA.defaultBlockState();
   }

   @Override
   public void initParams() {
      this.addParam(this.position = new ParamVector("psi.spellparam.position", 2774482, false, false));
      this.addParam(this.power = new ParamNumber("psi.spellparam.power", 13773354, false, true));
   }

   @Override
   public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
      super.addToMetadata(meta);
      Double powerVal = this.getParamEvaluation(this.power);
      if (powerVal != null && !(powerVal <= 0.0)) {
         powerVal = Math.max(0.5, powerVal);
         meta.addStat(EnumSpellStat.POTENCY, (int)(powerVal * 70.0));
         meta.addStat(EnumSpellStat.COST, (int)(powerVal * 210.0));
      } else {
         throw new SpellCompilationException("psi.spellerror.nonpositivevalue", this.x, this.y);
      }
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      Vector3 positionVal = this.getParamValue(context, this.position);
      double powerVal = this.getParamValue(context, this.power).doubleValue();
      if (positionVal == null) {
         throw new SpellRuntimeException("psi.spellerror.nullvector");
      } else if (!context.isInRadius(positionVal)) {
         throw new SpellRuntimeException("psi.spellerror.outsideradius");
      } else {
         BlockPos pos = positionVal.toBlockPos();
         BlockState state = context.focalPoint.getCommandSenderWorld().getBlockState(pos);
         context.focalPoint
            .getCommandSenderWorld()
            .explode(
               context.focalPoint,
               positionVal.x,
               positionVal.y,
               positionVal.z,
               (float)powerVal,
               isLiquid(state) ? ExplosionInteraction.NONE : ExplosionInteraction.TNT
            );
         return null;
      }
   }
}
