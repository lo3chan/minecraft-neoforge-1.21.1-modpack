package at.petrak.hexcasting.common.casting.actions.stack

import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.castables.SpecialHandler
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.math.HexAngle
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.utils.HexUtils
import at.petrak.hexcasting.common.lib.hex.HexSpecialHandlers
import at.petrak.hexcasting.xplat.IXplatAbstractions
import it.unimi.dsi.fastutil.booleans.BooleanArrayList
import it.unimi.dsi.fastutil.booleans.BooleanList
import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation

@SourceDebugExtension(["SMAP\nSpecialHandlerMask.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SpecialHandlerMask.kt\nat/petrak/hexcasting/common/casting/actions/stack/SpecialHandlerMask\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,88:1\n1563#2:89\n1634#2,3:90\n*S KotlinDebug\n*F\n+ 1 SpecialHandlerMask.kt\nat/petrak/hexcasting/common/casting/actions/stack/SpecialHandlerMask\n*L\n28#1:89\n28#1:90,3\n*E\n"])
public class SpecialHandlerMask(mask: BooleanList) : SpecialHandler {
   public final val mask: BooleanList

   init {
      this.mask = mask;
   }

   public override fun act(): Action {
      return new SpecialHandlerMask.InnerAction(this.mask);
   }

   public override fun getName(): Component {
      var var10000: Any = IXplatAbstractions.INSTANCE.getSpecialHandlerRegistry().getResourceKey(HexSpecialHandlers.MASK).get();
      val key: ResourceKey = var10000 as ResourceKey;
      val `$this$map$iv`: java.lang.Iterable = this.mask as java.lang.Iterable;
      val `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(this.mask as java.lang.Iterable, 10));

      for (Object item$iv$iv : $this$map$iv) {
         `destination$iv$iv`.add(Character.valueOf((char)(if (`item$iv$iv` as java.lang.Boolean) '-' else 'v')));
      }

      val fingerprint: java.lang.String = CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "", null, null, 0, null, null, 62, null);
      var10000 = HexAPI.instance().getSpecialHandlerI18nKey(key);
      return HexUtils.getLightPurple(HexUtils.asTranslatedComponent((java.lang.String)var10000, fingerprint)) as Component;
   }

   @JvmStatic
   fun {
      val var10000: ResourceLocation = HexAPI.modLoc("mask");
      NAME = var10000;
   }

   public companion object {
      public final val NAME: ResourceLocation
   }

   public class Factory : SpecialHandler.Factory<SpecialHandlerMask> {
      public open fun tryMatch(pat: HexPattern): SpecialHandlerMask? {
         val directions: java.util.List = pat.directions();
         var flatDir: HexDir = pat.getStartDir();
         if (!pat.getAngles().isEmpty() && pat.getAngles().get(0) === HexAngle.LEFT_BACK) {
            flatDir = (directions.get(0) as HexDir).rotatedBy(HexAngle.LEFT);
         }

         val mask: BooleanArrayList = new BooleanArrayList();
         var i: Int = 0;

         while (i < directions.size()) {
            val angle: HexAngle = (directions.get(i) as HexDir).angleFrom(flatDir);
            if (angle != HexAngle.FORWARD) {
               if (i >= directions.size() - 1) {
                  return null;
               }

               if (angle != HexAngle.RIGHT || (directions.get(i + 1) as HexDir).angleFrom(flatDir) != HexAngle.LEFT) {
                  return null;
               }

               mask.add(false);
               i += 2;
            } else {
               mask.add(true);
               i++;
            }
         }

         return new SpecialHandlerMask(mask as BooleanList);
      }
   }

   public class InnerAction(mask: BooleanList) : ConstMediaAction {
      public final val mask: BooleanList

      public open val argc: Int
         public open get() {
            return this.mask.size();
         }


      init {
         this.mask = mask;
      }

      public override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
         val out: ArrayList = new ArrayList(this.mask.size());
         val var4: java.util.Iterator = (this.mask as java.lang.Iterable).iterator();
         var var5: Int = 0;

         while (var4.hasNext()) {
            val i: Int = var5++;
            if (var4.next() as java.lang.Boolean) {
               out.add(args.get(i));
            }
         }

         return out;
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
