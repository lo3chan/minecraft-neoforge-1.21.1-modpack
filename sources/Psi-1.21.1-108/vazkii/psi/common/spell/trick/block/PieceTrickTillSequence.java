package vazkii.psi.common.spell.trick.block;

import net.minecraft.core.BlockPos;
import vazkii.psi.api.internal.MathHelper;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellHelpers;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.StatLabel;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceTrick;

public class PieceTrickTillSequence extends PieceTrick {
   SpellParam<Vector3> position;
   SpellParam<Vector3> target;
   SpellParam<Number> maxBlocks;

   public PieceTrickTillSequence(Spell spell) {
      super(spell);
      this.setStatLabel(EnumSpellStat.POTENCY, new StatLabel("psi.spellparam.max", true).mul(10.0));
      this.setStatLabel(EnumSpellStat.COST, new StatLabel("psi.spellparam.max", true).sub(1.0).parenthesize().mul(7.0).add(12.0));
   }

   @Override
   public void initParams() {
      this.addParam(this.position = new ParamVector("psi.spellparam.position", 2774482, false, false));
      this.addParam(this.target = new ParamVector("psi.spellparam.target", 4117034, false, false));
      this.addParam(this.maxBlocks = new ParamNumber("psi.spellparam.max", 13773354, false, true));
   }

   @Override
   public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
      super.addToMetadata(meta);
      double maxBlocksVal = SpellHelpers.ensurePositiveAndNonzero(this, this.maxBlocks);
      meta.addStat(EnumSpellStat.POTENCY, (int)(maxBlocksVal * 10.0));
      meta.addStat(EnumSpellStat.COST, (int)(12.0 + (maxBlocksVal - 1.0) * 7.0));
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      Vector3 positionVal = SpellHelpers.getVector3(this, context, this.position, true, false);
      Vector3 targetVal = SpellHelpers.getVector3(this, context, this.target, false, false);
      int maxBlocksInt = this.getParamValue(context, this.maxBlocks).intValue();
      Vector3 targetNorm = targetVal.copy().normalize();

      for (BlockPos blockPos : MathHelper.getBlocksAlongRay(
         positionVal.toVec3D(), positionVal.copy().add(targetNorm.copy().multiply(maxBlocksInt)).toVec3D(), maxBlocksInt
      )) {
         if (SpellHelpers.isBlockPosInRadius(context, blockPos)) {
            PieceTrickTill.tillBlock(context.caster, context.focalPoint.level(), blockPos);
         }
      }

      return null;
   }
}
