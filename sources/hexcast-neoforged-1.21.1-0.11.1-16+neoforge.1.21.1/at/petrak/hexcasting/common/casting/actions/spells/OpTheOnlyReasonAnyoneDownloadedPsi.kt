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
import at.petrak.hexcasting.api.utils.PublicUseOnContext
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Vec3i
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3

public object OpTheOnlyReasonAnyoneDownloadedPsi : SpellAction {
   public open val argc: Int = 1

   public override fun execute(args: List<Iota>, env: CastingEnvironment): Result {
      val target: BlockPos = OperatorUtils.getBlockPos(args, 0, this.getArgc());
      env.assertPosInRangeForEditing(target);
      val var10002: RenderedSpell = new OpTheOnlyReasonAnyoneDownloadedPsi.Spell(target);
      val var10004: ParticleSpray.Companion = ParticleSpray.Companion;
      val var10005: Vec3 = Vec3.atCenterOf((new BlockPos(target as Vec3i)) as Vec3i);
      return new SpellAction.Result(
         var10002, 11250L, CollectionsKt.listOf(ParticleSpray.Companion.burst$default(var10004, var10005, 1.0, 0, 4, null)), 0L, 8, null
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
         Items.BONE_MEAL
            .useOn(
               new PublicUseOnContext(
                  env.getWorld() as Level,
                  env.getCaster() as Player,
                  InteractionHand.MAIN_HAND,
                  new ItemStack(Items.BONE_MEAL as ItemLike),
                  new BlockHitResult(Vec3.ZERO, Direction.UP, this.pos, false)
               )
            );
      }

      public operator fun component1(): BlockPos {
         return this.pos;
      }

      public fun copy(pos: BlockPos = this.pos): at.petrak.hexcasting.common.casting.actions.spells.OpTheOnlyReasonAnyoneDownloadedPsi.Spell {
         return new OpTheOnlyReasonAnyoneDownloadedPsi.Spell(pos);
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
         } else if (other !is OpTheOnlyReasonAnyoneDownloadedPsi.Spell) {
            return false;
         } else {
            return this.pos == (other as OpTheOnlyReasonAnyoneDownloadedPsi.Spell).pos;
         }
      }

      override fun cast(env: CastingEnvironment, image: CastingImage): CastingImage? {
         return RenderedSpell.DefaultImpls.cast(this, env, image);
      }
   }
}
