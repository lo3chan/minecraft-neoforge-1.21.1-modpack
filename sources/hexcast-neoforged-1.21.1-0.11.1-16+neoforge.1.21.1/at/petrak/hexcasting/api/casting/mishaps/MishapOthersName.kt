package at.petrak.hexcasting.api.casting.mishaps

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap.Context
import at.petrak.hexcasting.api.pigment.FrozenPigment
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.DyeColor

public class MishapOthersName(confidant: Player) : Mishap {
   public final val confidant: Player

   init {
      this.confidant = confidant;
   }

   public override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment {
      return this.dyeColor(DyeColor.BLACK);
   }

   public override fun execute(ctx: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
      ctx.getMishapEnvironment().blind((if (this.confidant == ctx.getCaster()) 5 else 60) * 20);
   }

   protected override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context): Component {
      return if (this.confidant == ctx.getCaster())
         this.error("others_name.self", new Object[0])
         else
         this.error("others_name", new Object[]{this.confidant.getName()});
   }

   @SourceDebugExtension(["SMAP\nMishapOthersName.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MishapOthersName.kt\nat/petrak/hexcasting/api/casting/mishaps/MishapOthersName$Companion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,58:1\n1#2:59\n*E\n"])
   public companion object {
      public fun getTrueNameFromDatum(datum: Iota, caster: Player?): Player? {
         val poolToSearch: ArrayDeque = new ArrayDeque();
         poolToSearch.addLast(datum);

         while (!((java.util.Collection)poolToSearch).isEmpty()) {
            val datumToCheck: Iota = poolToSearch.removeFirst() as Iota;
            if (datumToCheck is EntityIota && (datumToCheck as EntityIota).getEntity() is Player && !((datumToCheck as EntityIota).getEntity() == caster)) {
               val var10000: Entity = (datumToCheck as EntityIota).getEntity();
               return var10000 as Player;
            }

            val datumSubIotas: java.lang.Iterable = datumToCheck.subIotas();
            if (datumSubIotas != null) {
               CollectionsKt.addAll(poolToSearch as java.util.Collection, datumSubIotas);
            }
         }

         return null;
      }

      public fun getTrueNameFromArgs(datums: List<Iota>, caster: Player?): Player? {
         val var3: java.util.Iterator = datums.iterator();

         var var10000: Player;
         while (true) {
            if (var3.hasNext()) {
               val var6: Player = MishapOthersName.Companion.getTrueNameFromDatum(var3.next() as Iota, caster);
               if (var6 == null) {
                  continue;
               }

               var10000 = var6;
               break;
            }

            var10000 = null;
            break;
         }

         return var10000;
      }
   }
}
