package vazkii.psi.common.spell.trick.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
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

public class PieceTrickMoveBlock extends PieceTrick {
   SpellParam<Vector3> position;
   SpellParam<Vector3> target;

   public PieceTrickMoveBlock(Spell spell) {
      super(spell);
      this.setStatLabel(EnumSpellStat.POTENCY, new StatLabel(10.0));
      this.setStatLabel(EnumSpellStat.COST, new StatLabel(15.0));
   }

   @Override
   public void initParams() {
      this.addParam(this.position = new ParamVector("psi.spellparam.position", 2774482, false, false));
      this.addParam(this.target = new ParamVector("psi.spellparam.target", 4117034, false, false));
   }

   @Override
   public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
      super.addToMetadata(meta);
      meta.addStat(EnumSpellStat.POTENCY, 10);
      meta.addStat(EnumSpellStat.COST, 15);
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      ItemStack tool = context.getHarvestTool();
      Vector3 positionVal = this.getParamValue(context, this.position);
      Vector3 targetVal = this.getParamValue(context, this.target);
      if (positionVal == null) {
         throw new SpellRuntimeException("psi.spellerror.nullvector");
      } else if (!context.isInRadius(positionVal)) {
         throw new SpellRuntimeException("psi.spellerror.outsideradius");
      } else {
         Level world = context.focalPoint.getCommandSenderWorld();
         BlockPos pos = positionVal.toBlockPos();
         if (context.positionBroken != null && context.positionBroken.getBlockPos().equals(pos)) {
            return null;
         } else {
            BlockState state = world.getBlockState(pos);
            if (world.getBlockEntity(pos) == null
               && state.getPistonPushReaction() == PushReaction.NORMAL
               && state.getDestroySpeed(world, pos) != -1.0F
               && PieceTrickBreakBlock.canHarvestBlock(state, context.caster, world, pos, tool)) {
               BreakEvent event = PieceTrickBreakBlock.createBreakEvent(state, context.caster, world, pos);
               NeoForge.EVENT_BUS.post(event);
               if (event.isCanceled()) {
                  return null;
               } else if (targetVal.isAxial() && !targetVal.isZero()) {
                  Vector3 axis = targetVal.normalize();
                  int x = pos.getX() + (int)axis.x;
                  int y = pos.getY() + (int)axis.y;
                  int z = pos.getZ() + (int)axis.z;
                  BlockPos pos1 = new BlockPos(x, y, z);
                  BlockState state1 = world.getBlockState(pos1);
                  if (world.mayInteract(context.caster, pos) && world.mayInteract(context.caster, pos1)) {
                     if (state1.isAir() || state1.canBeReplaced()) {
                        world.setBlock(pos1, state, 3);
                        world.removeBlock(pos, false);
                        world.levelEvent(2001, pos, Block.getId(state));
                     }

                     return null;
                  } else {
                     return null;
                  }
               } else {
                  return null;
               }
            } else {
               return null;
            }
         }
      }
   }
}
