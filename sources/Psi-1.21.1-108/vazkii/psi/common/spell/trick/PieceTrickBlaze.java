package vazkii.psi.common.spell.trick;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.level.BlockEvent.EntityPlaceEvent;
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

public class PieceTrickBlaze extends PieceTrick {
   SpellParam<Vector3> position;

   public PieceTrickBlaze(Spell spell) {
      super(spell);
      this.setStatLabel(EnumSpellStat.POTENCY, new StatLabel(20.0));
      this.setStatLabel(EnumSpellStat.COST, new StatLabel(40.0));
   }

   @Override
   public void initParams() {
      this.addParam(this.position = new ParamVector("psi.spellparam.position", 2774482, false, false));
   }

   @Override
   public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
      super.addToMetadata(meta);
      meta.addStat(EnumSpellStat.POTENCY, 20);
      meta.addStat(EnumSpellStat.COST, 40);
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      Vector3 positionVal = this.getParamValue(context, this.position);
      if (positionVal == null) {
         throw new SpellRuntimeException("psi.spellerror.nullvector");
      } else if (!context.isInRadius(positionVal)) {
         throw new SpellRuntimeException("psi.spellerror.outsideradius");
      } else {
         BlockPos pos = positionVal.toBlockPos();
         pos = pos.below();
         BlockState state = context.focalPoint.getCommandSenderWorld().getBlockState(pos);
         EntityPlaceEvent placeEvent = new EntityPlaceEvent(
            BlockSnapshot.create(context.focalPoint.getCommandSenderWorld().dimension(), context.focalPoint.getCommandSenderWorld(), pos),
            context.focalPoint.getCommandSenderWorld().getBlockState(pos.relative(Direction.UP)),
            context.caster
         );
         NeoForge.EVENT_BUS.post(placeEvent);
         if (placeEvent.isCanceled()) {
            return null;
         } else {
            if (!state.isAir() && !state.canBeReplaced()) {
               pos = pos.above();
               state = context.focalPoint.getCommandSenderWorld().getBlockState(pos);
               if (state.isAir() || state.canBeReplaced()) {
                  context.focalPoint.getCommandSenderWorld().setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
               }
            } else {
               context.focalPoint.getCommandSenderWorld().setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
            }

            return null;
         }
      }
   }
}
