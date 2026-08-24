package at.petrak.hexcasting.common.casting.actions.spells.sentinel

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.castables.SpellAction.Result
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapLocationInWrongDimension
import at.petrak.hexcasting.api.player.Sentinel
import at.petrak.hexcasting.xplat.IXplatAbstractions
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3

@SourceDebugExtension(["SMAP\nOpDestroySentinel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OpDestroySentinel.kt\nat/petrak/hexcasting/common/casting/actions/spells/sentinel/OpDestroySentinel\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,40:1\n1#2:41\n*E\n"])
public object OpDestroySentinel : SpellAction {
   public open val argc: Int

   public override fun execute(args: List<Iota>, env: CastingEnvironment): Result {
      val sentinel: Sentinel = IXplatAbstractions.INSTANCE.getSentinel(env.getCaster() as Player);
      val dim: ResourceKey = if (sentinel != null) sentinel.dimension() else null;
      if (dim != null && !(dim == env.getWorld().dimension())) {
         val var10002: ResourceLocation = dim.location();
         throw new MishapLocationInWrongDimension(var10002);
      } else {
         var var8: java.util.List;
         label23: {
            if (sentinel != null) {
               val var10000: Vec3 = sentinel.position();
               if (var10000 != null) {
                  var8 = CollectionsKt.listOf(ParticleSpray.Companion.cloud$default(ParticleSpray.Companion, var10000, 2.0, 0, 4, null));
                  if (var8 != null) {
                     break label23;
                  }
               }
            }

            var8 = CollectionsKt.emptyList();
         }

         return new SpellAction.Result(OpDestroySentinel.Spell.INSTANCE, 1000L, var8, 0L, 8, null);
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

   private object Spell : RenderedSpell {
      public override fun cast(env: CastingEnvironment) {
         IXplatAbstractions.INSTANCE.setSentinel(env.getCaster() as Player, null);
      }

      override fun cast(env: CastingEnvironment, image: CastingImage): CastingImage? {
         return RenderedSpell.DefaultImpls.cast(this, env, image);
      }
   }
}
