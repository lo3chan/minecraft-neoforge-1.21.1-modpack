package vazkii.psi.common.spell.trick.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

public class PieceTrickPlaceInSequence extends PieceTrick {
   SpellParam<Vector3> position;
   SpellParam<Vector3> target;
   SpellParam<Number> maxBlocks;
   SpellParam<Vector3> direction;

   public PieceTrickPlaceInSequence(Spell spell) {
      super(spell);
      this.setStatLabel(EnumSpellStat.POTENCY, new StatLabel("psi.spellparam.max", true).mul(8.0));
      this.setStatLabel(EnumSpellStat.COST, new StatLabel("psi.spellparam.max", true).sub(1.0).parenthesize().mul(5.6).add(9.6).floor());
   }

   @Override
   public void initParams() {
      this.addParam(this.position = new ParamVector("psi.spellparam.position", 2774482, false, false));
      this.addParam(this.target = new ParamVector("psi.spellparam.target", 4117034, false, false));
      this.addParam(this.maxBlocks = new ParamNumber("psi.spellparam.max", 13773354, false, true));
      this.addParam(this.direction = new ParamVector("psi.spellparam.direction", 2805970, true, false));
   }

   @Override
   public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
      super.addToMetadata(meta);
      Double maxBlocksVal = this.getParamEvaluation(this.maxBlocks);
      if (maxBlocksVal != null && !(maxBlocksVal <= 0.0)) {
         meta.addStat(EnumSpellStat.POTENCY, (int)(maxBlocksVal * 8.0));
         meta.addStat(EnumSpellStat.COST, (int)(9.6 + (maxBlocksVal - 1.0) * 5.6));
      } else {
         throw new SpellCompilationException("psi.spellerror.nonpositivevalue", this.x, this.y);
      }
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      Vector3 positionVal = this.getParamValue(context, this.position);
      Vector3 targetVal = this.getParamValue(context, this.target);
      Number maxBlocksVal = this.getParamValue(context, this.maxBlocks);
      int maxBlocksInt = maxBlocksVal.intValue();
      Vector3 directionVal = this.getParamValue(context, this.direction);
      Direction direction = Direction.NORTH;
      Direction horizontalFacing = Direction.NORTH;
      if (directionVal != null) {
         direction = Direction.getNearest(directionVal.x, directionVal.y, directionVal.z);
         horizontalFacing = Direction.getNearest(directionVal.x, 0.0, directionVal.z);
      }

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

            PieceTrickPlaceBlock.placeBlock(
               context.caster, context.focalPoint.getCommandSenderWorld(), blockPos, context.getTargetSlot(), false, direction, horizontalFacing
            );
         }

         return null;
      }
   }
}
