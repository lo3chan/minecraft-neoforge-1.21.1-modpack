package vazkii.psi.common.spell.trick.block;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent.BreakEvent;
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

public class PieceTrickMoveBlockSequence extends PieceTrick {
   SpellParam<Vector3> position;
   SpellParam<Vector3> target;
   SpellParam<Vector3> direction;
   SpellParam<Number> maxBlocks;

   public PieceTrickMoveBlockSequence(Spell spell) {
      super(spell);
      this.setStatLabel(EnumSpellStat.POTENCY, new StatLabel("psi.spellparam.max", true).mul(10.0));
      this.setStatLabel(EnumSpellStat.COST, new StatLabel("psi.spellparam.max", true).sub(1.0).parenthesize().mul(10.5).add(18.0).floor());
   }

   @Override
   public void initParams() {
      this.addParam(this.position = new ParamVector("psi.spellparam.position", 2774482, false, false));
      this.addParam(this.target = new ParamVector("psi.spellparam.target", 13814826, false, false));
      this.addParam(this.maxBlocks = new ParamNumber("psi.spellparam.max", 13773354, false, true));
      this.addParam(this.direction = new ParamVector("psi.spellparam.direction", 4117034, false, false));
   }

   @Override
   public void addToMetadata(SpellMetadata meta) throws SpellCompilationException, ArithmeticException {
      super.addToMetadata(meta);
      double maxBlocksVal = SpellHelpers.ensurePositiveAndNonzero(this, this.maxBlocks);
      meta.addStat(EnumSpellStat.POTENCY, (int)(maxBlocksVal * 10.0));
      meta.addStat(EnumSpellStat.COST, (int)(18.0 + (maxBlocksVal - 1.0) * 10.5));
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      Vector3 directionVal = SpellHelpers.getVector3(this, context, this.direction, false, true);
      Vector3 positionVal = SpellHelpers.getVector3(this, context, this.position, true, false);
      Vector3 targetVal = SpellHelpers.getVector3(this, context, this.target, false, false);
      int maxBlocksVal = this.getParamValue(context, this.maxBlocks).intValue();
      Level world = context.focalPoint.level();
      Map<BlockPos, BlockState> toSet = new HashMap<>();
      Map<BlockPos, BlockState> toRemove = new HashMap<>();
      Vector3 directNorm = directionVal.copy().normalize();
      Vector3 targetNorm = targetVal.copy().normalize();
      LinkedHashSet<BlockPos> positions = MathHelper.getBlocksAlongRay(
         positionVal.toVec3D(), positionVal.copy().add(targetNorm.copy().multiply(maxBlocksVal)).toVec3D(), maxBlocksVal
      );
      LinkedHashSet<BlockPos> moveableBlocks = new LinkedHashSet<>();
      LinkedHashSet<BlockPos> immovableBlocks = new LinkedHashSet<>();
      if (context.positionBroken != null) {
         immovableBlocks.add(context.positionBroken.getBlockPos());
      }

      for (BlockPos blockPos : positions) {
         BlockState state = world.getBlockState(blockPos);
         if (!world.isEmptyBlock(blockPos)) {
            if (world.getBlockEntity(blockPos) == null
               && state.getPistonPushReaction() == PushReaction.NORMAL
               && state.getDestroySpeed(world, blockPos) != -1.0F
               && PieceTrickBreakBlock.canHarvestBlock(state, context.caster, world, blockPos, context.getHarvestTool())
               && SpellHelpers.isBlockPosInRadius(context, blockPos)
               && world.mayInteract(context.caster, blockPos)) {
               BlockPos pushToPos = blockPos.offset((int)directNorm.x, (int)directNorm.y, (int)directNorm.z);
               boolean isOffWorld = pushToPos.getY() < 0 || pushToPos.getY() > 256;
               if (isOffWorld) {
                  immovableBlocks.add(blockPos);
               } else {
                  BreakEvent event = new BreakEvent(world, blockPos, state, context.caster);
                  if (((BreakEvent)NeoForge.EVENT_BUS.post(event)).isCanceled()) {
                     immovableBlocks.add(blockPos);
                  } else {
                     moveableBlocks.add(blockPos);
                  }
               }
            } else {
               immovableBlocks.add(blockPos);
            }
         }
      }

      label97:
      for (BlockPos blockPosx : moveableBlocks) {
         BlockState state = world.getBlockState(blockPosx);
         BlockPos pushToPos = blockPosx.offset((int)directNorm.x, (int)directNorm.y, (int)directNorm.z);
         BlockState pushToState = world.getBlockState(pushToPos);
         if (!immovableBlocks.contains(pushToPos) && !immovableBlocks.contains(blockPosx)) {
            label92:
            if (moveableBlocks.contains(pushToPos)) {
               BlockPos nextPos = pushToPos;

               while (moveableBlocks.contains(nextPos)) {
                  BlockPos nextPosPushPos = nextPos.offset((int)directNorm.x, (int)directNorm.y, (int)directNorm.z);
                  BlockState nextPosPushPosState = world.getBlockState(nextPosPushPos);
                  if (!moveableBlocks.contains(nextPosPushPos)) {
                     if (immovableBlocks.contains(nextPosPushPos) || !world.isEmptyBlock(nextPosPushPos) && !nextPosPushPosState.canBeReplaced()) {
                        continue label97;
                     }
                     break;
                  }

                  nextPos = nextPosPushPos;
               }
            } else {
               if (world.isEmptyBlock(pushToPos) || pushToState.canBeReplaced()) {
                  break label92;
               }
               continue;
            }

            toRemove.put(blockPosx, state);
            toSet.put(pushToPos, state);
         }
      }

      for (Entry<BlockPos, BlockState> pairtoRemove : toRemove.entrySet()) {
         context.focalPoint.level().removeBlock(pairtoRemove.getKey(), true);
         context.focalPoint.level().levelEvent(2001, pairtoRemove.getKey(), Block.getId(pairtoRemove.getValue()));
      }

      for (Entry<BlockPos, BlockState> pairToSet : toSet.entrySet()) {
         context.focalPoint.level().setBlockAndUpdate(pairToSet.getKey(), pairToSet.getValue());
      }

      return null;
   }
}
