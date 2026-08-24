package at.petrak.hexcasting.api.casting.mishaps

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.GarbageIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap.Context
import at.petrak.hexcasting.api.pigment.FrozenPigment
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.network.chat.Component
import net.minecraft.world.item.DyeColor

@SourceDebugExtension(["SMAP\nMishapNotEnoughArgs.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MishapNotEnoughArgs.kt\nat/petrak/hexcasting/api/casting/mishaps/MishapNotEnoughArgs\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,23:1\n1#2:24\n*E\n"])
public class MishapNotEnoughArgs(expected: Int, got: Int) : Mishap {
   public final val expected: Int
   public final val got: Int

   init {
      this.expected = expected;
      this.got = got;
   }

   public override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment {
      return this.dyeColor(DyeColor.LIGHT_GRAY);
   }

   public override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
      val var4: Int = this.expected - this.got;

      for (int var5 = 0; var5 < var4; var5++) {
         stack.add(new GarbageIota());
      }
   }

   protected override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context): Component {
      return if (this.got == 0) this.error("no_args", new Object[]{this.expected}) else this.error("not_enough_args", new Object[]{this.expected, this.got});
   }
}
