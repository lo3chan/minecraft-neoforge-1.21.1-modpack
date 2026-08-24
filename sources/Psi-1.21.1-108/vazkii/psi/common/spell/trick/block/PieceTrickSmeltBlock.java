package vazkii.psi.common.spell.trick.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
import vazkii.psi.common.spell.selector.entity.PieceSelectorNearbySmeltables;

public class PieceTrickSmeltBlock extends PieceTrick {
   SpellParam<Vector3> position;

   public PieceTrickSmeltBlock(Spell spell) {
      super(spell);
      this.setStatLabel(EnumSpellStat.POTENCY, new StatLabel(20.0));
      this.setStatLabel(EnumSpellStat.COST, new StatLabel(80.0));
   }

   @Override
   public void initParams() {
      this.addParam(this.position = new ParamVector("psi.spellparam.position", 2774482, false, false));
   }

   @Override
   public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
      super.addToMetadata(meta);
      meta.addStat(EnumSpellStat.POTENCY, 20);
      meta.addStat(EnumSpellStat.COST, 80);
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
         if (!context.focalPoint.getCommandSenderWorld().mayInteract(context.caster, pos)) {
            return null;
         } else {
            BlockState state = context.focalPoint.getCommandSenderWorld().getBlockState(pos);
            Block block = state.getBlock();
            ItemStack stack = new ItemStack(block);
            BreakEvent event = PieceTrickBreakBlock.createBreakEvent(state, context.caster, context.focalPoint.level(), pos);
            NeoForge.EVENT_BUS.post(event);
            if (event.isCanceled()) {
               return null;
            } else {
               ItemStack result = PieceSelectorNearbySmeltables.simulateSmelt(context.focalPoint.getCommandSenderWorld(), stack);
               if (!result.isEmpty()) {
                  Item item = result.getItem();
                  Block block1 = Block.byItem(item);
                  if (block1 != Blocks.AIR) {
                     context.focalPoint.getCommandSenderWorld().setBlockAndUpdate(pos, block1.defaultBlockState());
                     context.focalPoint.getCommandSenderWorld().levelEvent(2001, pos, Block.getId(block1.defaultBlockState()));
                  }
               }

               return null;
            }
         }
      }
   }
}
