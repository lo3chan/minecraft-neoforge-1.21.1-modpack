package vazkii.psi.common.spell.trick.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent.BreakEvent;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.StatLabel;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceTrick;

public class PieceTrickCollapseBlock extends PieceTrick {
   SpellParam<Vector3> position;

   public PieceTrickCollapseBlock(Spell spell) {
      super(spell);
      this.setStatLabel(EnumSpellStat.POTENCY, new StatLabel(80.0));
      this.setStatLabel(EnumSpellStat.COST, new StatLabel(125.0));
   }

   @Override
   public void initParams() {
      this.addParam(this.position = new ParamVector("psi.spellparam.position", 2774482, false, false));
   }

   @Override
   public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
      super.addToMetadata(meta);
      meta.addStat(EnumSpellStat.POTENCY, 80);
      meta.addStat(EnumSpellStat.COST, 125);
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      ItemStack tool = context.getHarvestTool();
      Vector3 positionVal = this.getParamValue(context, this.position);
      if (positionVal == null) {
         throw new SpellRuntimeException("psi.spellerror.nullvector");
      } else if (!context.isInRadius(positionVal)) {
         throw new SpellRuntimeException("psi.spellerror.outsideradius");
      } else {
         Level world = context.focalPoint.getCommandSenderWorld();
         BlockPos pos = positionVal.toBlockPos();
         BlockPos posDown = pos.below();
         BlockState state = world.getBlockState(pos);
         BlockState stateDown = world.getBlockState(posDown);
         if (!world.mayInteract(context.caster, pos)) {
            return null;
         } else {
            if (stateDown.isAir()
               && state.getDestroySpeed(world, pos) != -1.0F
               && PieceTrickBreakBlock.canHarvestBlock(state, context.caster, world, pos, tool)
               && world.getBlockEntity(pos) == null) {
               BreakEvent event = PieceTrickBreakBlock.createBreakEvent(state, context.caster, world, pos);
               NeoForge.EVENT_BUS.post(event);
               if (event.isCanceled()) {
                  return null;
               }

               FallingBlockEntity.fall(world, pos, state);
            }

            return null;
         }
      }
   }
}
