package at.petrak.hexcasting.common.casting.actions.spells

import at.petrak.hexcasting.api.casting.OperatorUtils
import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.castables.SpellAction.Result
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock
import at.petrak.hexcasting.common.misc.AkashicTreeGrower
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.core.BlockPos
import net.minecraft.core.Position
import net.minecraft.core.Vec3i
import net.minecraft.nbt.CompoundTag
import net.minecraft.tags.BlockTags
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3

public object OpEdifySapling : SpellAction {
   public open val argc: Int = 1

   public override fun execute(args: List<Iota>, env: CastingEnvironment): Result {
      val pos: BlockPos = BlockPos.containing(OperatorUtils.getVec3(args, 0, this.getArgc()) as Position);
      env.assertPosInRangeForEditing(pos);
      if (!env.getWorld().getBlockState(pos).is(BlockTags.SAPLINGS)) {
         val var10000: MishapBadBlock.Companion = MishapBadBlock.Companion;
         throw var10000.of(pos, "sapling");
      } else {
         val var10002: RenderedSpell = new OpEdifySapling.Spell(pos);
         val var10006: Vec3 = Vec3.atCenterOf(pos as Vec3i);
         return new SpellAction.Result(
            var10002, 100000L, CollectionsKt.listOf(new ParticleSpray(var10006, new Vec3(0.0, 2.0, 0.0), 0.1, 0.7853981633974483, 100)), 0L, 8, null
         );
      }
   }

   override fun hasCastingSound(ctx: CastingEnvironment): Boolean {
      return SpellAction.DefaultImpls.hasCastingSound(this, ctx);
   }

   override fun awardsCastingStat(ctx: CastingEnvironment): Boolean {
      return SpellAction.DefaultImpls.awardsCastingStat(this, ctx);
   }

   override fun executeWithUserdata(args: MutableList<Iota>, env: CastingEnvironment, userData: CompoundTag): SpellAction.Result {
      return SpellAction.DefaultImpls.executeWithUserdata(this, args, env, userData);
   }

   override fun operate(env: CastingEnvironment, image: CastingImage, continuation: SpellContinuation): OperationResult {
      return SpellAction.DefaultImpls.operate(this, env, image, continuation);
   }

   private data class Spell(pos: BlockPos) : RenderedSpell {
      public final val pos: BlockPos

      init {
         this.pos = pos;
      }

      public override fun cast(env: CastingEnvironment) {
         if (env.canEditBlockAt(this.pos)
            && IXplatAbstractions.INSTANCE.isBreakingAllowed(env.getWorld(), this.pos, env.getWorld().getBlockState(this.pos), env.getCaster() as Player)) {
            val bs: BlockState = env.getWorld().getBlockState(this.pos);

            for (int i = 0; i < 8; i++) {
               if (AkashicTreeGrower.INSTANCE
                  .growTree(env.getWorld(), env.getWorld().getChunkSource().getGenerator(), this.pos, bs, env.getWorld().getRandom())) {
                  break;
               }
            }
         }
      }

      public operator fun component1(): BlockPos {
         return this.pos;
      }

      public fun copy(pos: BlockPos = this.pos): at.petrak.hexcasting.common.casting.actions.spells.OpEdifySapling.Spell {
         return new OpEdifySapling.Spell(pos);
      }

      public override fun toString(): String {
         return "Spell(pos=${this.pos})";
      }

      public override fun hashCode(): Int {
         return this.pos.hashCode();
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is OpEdifySapling.Spell) {
            return false;
         } else {
            return this.pos == (other as OpEdifySapling.Spell).pos;
         }
      }

      override fun cast(env: CastingEnvironment, image: CastingImage): CastingImage? {
         return RenderedSpell.DefaultImpls.cast(this, env, image);
      }
   }
}
