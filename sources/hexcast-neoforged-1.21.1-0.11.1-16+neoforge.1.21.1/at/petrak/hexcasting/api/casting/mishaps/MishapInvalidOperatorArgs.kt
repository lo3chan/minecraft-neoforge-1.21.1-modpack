package at.petrak.hexcasting.api.casting.mishaps

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.GarbageIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.casting.mishaps.Mishap.Context
import at.petrak.hexcasting.api.pigment.FrozenPigment
import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentUtils
import net.minecraft.world.item.DyeColor

@SourceDebugExtension(["SMAP\nMishapInvalidIotas.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MishapInvalidIotas.kt\nat/petrak/hexcasting/api/casting/mishaps/MishapInvalidOperatorArgs\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,38:1\n1563#2:39\n1634#2,3:40\n*S KotlinDebug\n*F\n+ 1 MishapInvalidIotas.kt\nat/petrak/hexcasting/api/casting/mishaps/MishapInvalidOperatorArgs\n*L\n33#1:39\n33#1:40,3\n*E\n"])
public class MishapInvalidOperatorArgs(perpetrators: List<Iota>, operator: HexPattern) : Mishap {
   public final val perpetrators: List<Iota>
   public final val operator: HexPattern

   init {
      this.perpetrators = perpetrators;
      this.operator = operator;
   }

   public override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment {
      return this.dyeColor(DyeColor.GRAY);
   }

   public override fun execute(ctx: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
      var i: Int = 0;

      for (int var5 = this.perpetrators.size(); i < var5; i++) {
         stack.set(stack.size() - 1 - i, new GarbageIota());
      }
   }

   protected override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context): Component {
      var var10000: Component;
      switch (this.perpetrators.size()) {
         case 0:
            var10000 = Component.literal("[]") as Component;
            break;
         case 1:
            var10000 = this.perpetrators.get(0).display();
            break;
         default:
            val `$this$map$iv`: java.lang.Iterable = this.perpetrators;
            val `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(this.perpetrators, 10));

            for (Object item$iv$iv : $this$map$iv) {
               `destination$iv$iv`.add((`item$iv$iv` as Iota).display());
            }

            var10000 = ComponentUtils.formatList(`destination$iv$iv` as java.util.List, Component.literal(", ") as Component);
      }

      return this.error("invalid_operator_args", new Object[]{this.operator, var10000});
   }
}
