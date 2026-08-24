package vazkii.psi.common.spell.trick;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
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
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceTrick;

public class PieceTrickOvergrow extends PieceTrick {
   SpellParam<Vector3> position;

   public PieceTrickOvergrow(Spell spell) {
      super(spell);
      this.setStatLabel(EnumSpellStat.POTENCY, new StatLabel(100.0));
      this.setStatLabel(EnumSpellStat.COST, new StatLabel(200.0));
   }

   @Override
   public void initParams() {
      this.addParam(this.position = new ParamVector("psi.spellparam.position", 2774482, false, false));
   }

   @Override
   public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
      super.addToMetadata(meta);
      meta.addStat(EnumSpellStat.POTENCY, 100);
      meta.addStat(EnumSpellStat.COST, 200);
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      BlockPos pos = SpellHelpers.getBlockPos(this, context, this.position, true, false);
      return this.bonemeal(context.caster, context.focalPoint.level(), pos);
   }

   public InteractionResult bonemeal(Player player, Level world, BlockPos pos) {
      if (world.getChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()), ChunkStatus.FULL, false) != null
         && world.mayInteract(player, pos)) {
         BlockHitResult hit = new BlockHitResult(Vec3.ZERO, Direction.UP, pos, false);
         ItemStack save = player.getItemInHand(InteractionHand.MAIN_HAND);
         player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BONE_MEAL));
         UseOnContext fakeContext = new UseOnContext(player, InteractionHand.MAIN_HAND, hit);
         player.setItemInHand(InteractionHand.MAIN_HAND, save);
         return Items.BONE_MEAL.useOn(fakeContext);
      } else {
         return InteractionResult.PASS;
      }
   }
}
