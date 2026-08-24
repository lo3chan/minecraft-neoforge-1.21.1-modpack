package vazkii.psi.common.spell.trick.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent.BreakEvent;
import vazkii.psi.api.PsiAPI;
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

public class PieceTrickCollapseBlockSequence extends PieceTrick {
   SpellParam<Vector3> position;
   SpellParam<Vector3> target;
   SpellParam<Number> maxBlocks;

   public PieceTrickCollapseBlockSequence(Spell spell) {
      super(spell);
      this.setStatLabel(EnumSpellStat.POTENCY, new StatLabel("psi.spellparam.max", true).mul(20.0));
      this.setStatLabel(EnumSpellStat.COST, new StatLabel("psi.spellparam.max", true).sub(1.0).parenthesize().mul(150.0).add(100.0));
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
         meta.addStat(EnumSpellStat.COST, (int)(150.0 + (maxBlocksVal - 1.0) * 100.0));
      } else {
         throw new SpellCompilationException("psi.spellerror.nonpositivevalue", this.x, this.y);
      }
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      Vector3 positionVal = this.getParamValue(context, this.position);
      Vector3 targetVal = this.getParamValue(context, this.target);
      int maxBlocksInt = this.getParamValue(context, this.maxBlocks).intValue();
      if (positionVal == null) {
         throw new SpellRuntimeException("psi.spellerror.nullvector");
      } else {
         ItemStack tool = context.tool;
         if (tool.isEmpty()) {
            tool = PsiAPI.getPlayerCAD(context.caster);
         }

         Level world = context.focalPoint.level();
         Vector3 targetNorm = targetVal.copy().normalize();

         for (BlockPos blockPos : MathHelper.getBlocksAlongRay(
            positionVal.toVec3D(), positionVal.copy().add(targetNorm.copy().multiply(maxBlocksInt)).toVec3D(), maxBlocksInt
         )) {
            if (!context.isInRadius(Vector3.fromBlockPos(blockPos))) {
               throw new SpellRuntimeException("psi.spellerror.outsideradius");
            }

            BlockPos posDown = blockPos.below();
            BlockState state = world.getBlockState(blockPos);
            BlockState stateDown = world.getBlockState(posDown);
            if (!world.mayInteract(context.caster, blockPos)) {
               return null;
            }

            if (stateDown.isAir()
               && state.getDestroySpeed(world, blockPos) != -1.0F
               && PieceTrickBreakBlock.canHarvestBlock(state, context.caster, world, blockPos, tool)
               && world.getBlockEntity(blockPos) == null) {
               BreakEvent event = PieceTrickBreakBlock.createBreakEvent(state, context.caster, world, blockPos);
               NeoForge.EVENT_BUS.post(event);
               if (event.isCanceled()) {
                  return null;
               }

               FallingBlockEntity.fall(world, blockPos, state);
            }
         }

         return null;
      }
   }
}
