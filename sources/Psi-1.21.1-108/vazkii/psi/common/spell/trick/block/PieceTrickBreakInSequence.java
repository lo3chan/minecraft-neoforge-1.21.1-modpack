package vazkii.psi.common.spell.trick.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import vazkii.psi.api.internal.MathHelper;
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

public class PieceTrickBreakInSequence extends PieceTrick {
   SpellParam<Vector3> position;
   SpellParam<Vector3> target;
   SpellParam<Number> maxBlocks;

   public PieceTrickBreakInSequence(Spell spell) {
      super(spell);
      this.setStatLabel(EnumSpellStat.POTENCY, new StatLabel("psi.spellparam.max", true).times().append(20.0).floor());
      this.setStatLabel(EnumSpellStat.COST, new StatLabel("psi.spellparam.max", true).sub(1.0).parenthesize().mul(35.0).add(60.0));
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
      Double maxBlocksVal = this.getParamEvaluation(this.maxBlocks);
      if (maxBlocksVal != null && !(maxBlocksVal <= 0.0)) {
         meta.addStat(EnumSpellStat.POTENCY, (int)(maxBlocksVal * 20.0));
         meta.addStat(EnumSpellStat.COST, (int)(60.0 + (maxBlocksVal - 1.0) * 35.0));
      } else {
         throw new SpellCompilationException("psi.spellerror.nonpositivevalue", this.x, this.y);
      }
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      ItemStack tool = context.getHarvestTool();
      Vector3 positionVal = this.getParamValue(context, this.position);
      Vector3 targetVal = this.getParamValue(context, this.target);
      int maxBlocksInt = this.getParamValue(context, this.maxBlocks).intValue();
      if (positionVal == null) {
         throw new SpellRuntimeException("psi.spellerror.nullvector");
      } else {
         Vector3 targetNorm = targetVal.copy().normalize();

         for (BlockPos blockPos : MathHelper.getBlocksAlongRay(
            positionVal.toVec3D(), positionVal.copy().add(targetNorm.copy().multiply(maxBlocksInt)).toVec3D(), maxBlocksInt
         )) {
            if (!context.isInRadius(Vector3.fromBlockPos(blockPos))) {
               throw new SpellRuntimeException("psi.spellerror.outsideradius");
            }

            PieceTrickBreakBlock.removeBlockWithDrops(context, context.caster, context.focalPoint.getCommandSenderWorld(), tool, blockPos, v -> true);
         }

         return null;
      }
   }
}
