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
import at.petrak.hexcasting.api.mod.HexConfig
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.core.BlockPos
import net.minecraft.core.Position
import net.minecraft.core.Vec3i
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3

public object OpBreakBlock : SpellAction {
   public open val argc: Int
      public open get() {
         return 1;
      }


   public override fun execute(args: List<Iota>, env: CastingEnvironment): Result {
      val pos: BlockPos = BlockPos.containing(OperatorUtils.getVec3(args, 0, this.getArgc()) as Position);
      env.assertPosInRangeForEditing(pos);
      val var10002: RenderedSpell = new OpBreakBlock.Spell(pos);
      val var10004: ParticleSpray.Companion = ParticleSpray.Companion;
      val var10005: Vec3 = Vec3.atCenterOf(pos as Vec3i);
      return new SpellAction.Result(
         var10002, 1250L, CollectionsKt.listOf(ParticleSpray.Companion.burst$default(var10004, var10005, 1.0, 0, 4, null)), 0L, 8, null
      );
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
         val blockstate: BlockState = env.getWorld().getBlockState(this.pos);
         if (!blockstate.isAir()
            && blockstate.getDestroySpeed(env.getWorld() as BlockGetter, this.pos) >= 0.0F
            && IXplatAbstractions.INSTANCE.isCorrectTierForDrops(HexConfig.server().opBreakHarvestLevel(), blockstate)) {
            env.getWorld().destroyBlock(this.pos, true, env.getCaster() as Entity);
         }
      }

      public operator fun component1(): BlockPos {
         return this.pos;
      }

      public fun copy(pos: BlockPos = this.pos): at.petrak.hexcasting.common.casting.actions.spells.OpBreakBlock.Spell {
         return new OpBreakBlock.Spell(pos);
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
         } else if (other !is OpBreakBlock.Spell) {
            return false;
         } else {
            return this.pos == (other as OpBreakBlock.Spell).pos;
         }
      }

      override fun cast(env: CastingEnvironment, image: CastingImage): CastingImage? {
         return RenderedSpell.DefaultImpls.cast(this, env, image);
      }
   }
}
