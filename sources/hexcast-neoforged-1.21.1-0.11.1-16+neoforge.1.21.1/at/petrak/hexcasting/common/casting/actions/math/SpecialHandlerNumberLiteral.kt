package at.petrak.hexcasting.common.casting.actions.math

import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.castables.SpecialHandler
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.utils.HexUtils
import at.petrak.hexcasting.common.lib.hex.HexSpecialHandlers
import at.petrak.hexcasting.xplat.IXplatAbstractions
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey

public class SpecialHandlerNumberLiteral(x: Double) : SpecialHandler {
   public final val x: Double

   init {
      this.x = x;
   }

   public override fun act(): Action {
      return new SpecialHandlerNumberLiteral.InnerAction(this.x);
   }

   public override fun getName(): Component {
      var var10000: Any = IXplatAbstractions.INSTANCE.getSpecialHandlerRegistry().getResourceKey(HexSpecialHandlers.NUMBER).get();
      var10000 = HexAPI.instance().getSpecialHandlerI18nKey(var10000 as ResourceKey<SpecialHandlerFactory<?>>);
      return HexUtils.getLightPurple(HexUtils.asTranslatedComponent((java.lang.String)var10000, Action.Companion.getDOUBLE_FORMATTER().format(this.x))) as Component;
   }

   public class Factory : SpecialHandler.Factory<SpecialHandlerNumberLiteral> {
      public open fun tryMatch(pat: HexPattern): SpecialHandlerNumberLiteral? {
         val sig: java.lang.String = pat.anglesSignature();
         if (!StringsKt.startsWith$default(sig, "aqaa", false, 2, null) && !StringsKt.startsWith$default(sig, "dedd", false, 2, null)) {
            return null;
         } else {
            val negate: Boolean = StringsKt.startsWith$default(sig, "dedd", false, 2, null);
            var accumulator: Double = 0.0;
            val var10000: java.lang.String = sig.substring(4);
            val var6: java.lang.String = var10000;
            var var7: Int = 0;

            for (int var8 = var10000.length(); var7 < var8; var7++) {
               switch (var6.charAt(var7)) {
                  case 'a':
                     accumulator *= 2;
                     break;
                  case 'd':
                     accumulator /= 2;
                     break;
                  case 'e':
                     accumulator += 10;
                     break;
                  case 'q':
                     accumulator += 5;
                  case 's':
                     break;
                  case 'w':
                     accumulator += 1;
                     break;
                  default:
                     return null;
               }
            }

            if (negate) {
               accumulator = -accumulator;
            }

            return new SpecialHandlerNumberLiteral(accumulator);
         }
      }
   }

   @SourceDebugExtension(["SMAP\nSpecialHandlerNumberLiteral.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SpecialHandlerNumberLiteral.kt\nat/petrak/hexcasting/common/casting/actions/math/SpecialHandlerNumberLiteral$InnerAction\n+ 2 ActionUtils.kt\nat/petrak/hexcasting/api/casting/OperatorUtils\n*L\n1#1,79:1\n300#2:80\n*S KotlinDebug\n*F\n+ 1 SpecialHandlerNumberLiteral.kt\nat/petrak/hexcasting/common/casting/actions/math/SpecialHandlerNumberLiteral$InnerAction\n*L\n32#1:80\n*E\n"])
   public class InnerAction(x: Double) : ConstMediaAction {
      public final val x: Double
      public open val argc: Int

      init {
         this.x = x;
      }

      public override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
         return CollectionsKt.listOf(new DoubleIota(this.x));
      }

      override fun getMediaCost(): Long {
         return ConstMediaAction.DefaultImpls.getMediaCost(this);
      }

      override fun executeWithOpCount(args: MutableList<Iota>, env: CastingEnvironment): ConstMediaAction.CostMediaActionResult {
         return ConstMediaAction.DefaultImpls.executeWithOpCount(this, args, env);
      }

      override fun operate(env: CastingEnvironment, image: CastingImage, continuation: SpellContinuation): OperationResult {
         return ConstMediaAction.DefaultImpls.operate(this, env, image, continuation);
      }
   }
}
