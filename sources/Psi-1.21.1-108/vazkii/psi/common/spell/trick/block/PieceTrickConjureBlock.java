package vazkii.psi.common.spell.trick.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.jetbrains.annotations.Nullable;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.EnumCADComponent;
import vazkii.psi.api.cad.ICAD;
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
import vazkii.psi.common.block.BlockConjured;
import vazkii.psi.common.block.base.ModBlocks;
import vazkii.psi.common.block.tile.TileConjured;

public class PieceTrickConjureBlock extends PieceTrick {
   SpellParam<Vector3> position;
   SpellParam<Number> time;

   public PieceTrickConjureBlock(Spell spell) {
      super(spell);
      this.setStatLabel(EnumSpellStat.POTENCY, new StatLabel(15.0));
      this.setStatLabel(EnumSpellStat.COST, new StatLabel(20.0));
   }

   public static void conjure(SpellContext context, @Nullable Number timeVal, BlockPos pos, Level world, BlockState state) {
      if (world.getBlockState(pos).getBlock() != state.getBlock() && conjure(world, pos, context.caster, state)) {
         if (timeVal != null && timeVal.intValue() > 0) {
            int val = timeVal.intValue();
            world.scheduleTick(pos, state.getBlock(), val);
         }

         BlockEntity tile = world.getBlockEntity(pos);
         ItemStack cad = PsiAPI.getPlayerCAD(context.caster);
         if (tile instanceof TileConjured && !cad.isEmpty()) {
            ((TileConjured)tile).colorizer = ((ICAD)cad.getItem()).getComponentInSlot(cad, EnumCADComponent.DYE);
         }
      }
   }

   public static boolean conjure(Level world, BlockPos pos, Player player, BlockState state) {
      if (world.getChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()), ChunkStatus.FULL, false) != null
         && world.mayInteract(player, pos)) {
         BlockState inWorld = world.getBlockState(pos);
         return !inWorld.isAir() && !inWorld.canBeReplaced() ? false : world.setBlockAndUpdate(pos, state);
      } else {
         return false;
      }
   }

   @Override
   public void initParams() {
      this.addParam(this.position = new ParamVector("psi.spellparam.position", 2774482, false, false));
      this.addParam(this.time = new ParamNumber("psi.spellparam.time", 13773354, true, false));
   }

   @Override
   public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
      super.addToMetadata(meta);
      this.addStats(meta);
   }

   public void addStats(SpellMetadata meta) throws SpellCompilationException {
      meta.addStat(EnumSpellStat.POTENCY, 15);
      meta.addStat(EnumSpellStat.COST, 20);
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      Vector3 positionVal = this.getParamValue(context, this.position);
      Number timeVal = this.getParamValue(context, this.time);
      if (positionVal == null) {
         throw new SpellRuntimeException("psi.spellerror.nullvector");
      } else if (!context.isInRadius(positionVal)) {
         throw new SpellRuntimeException("psi.spellerror.outsideradius");
      } else {
         BlockPos pos = positionVal.toBlockPos();
         Level world = context.focalPoint.getCommandSenderWorld();
         if (!world.mayInteract(context.caster, pos)) {
            return null;
         } else {
            conjure(context, timeVal, pos, world, this.messWithState(((BlockConjured)ModBlocks.conjured.get()).defaultBlockState()));
            return null;
         }
      }
   }

   public BlockState messWithState(BlockState state) {
      return (BlockState)state.setValue(BlockConjured.SOLID, true);
   }
}
