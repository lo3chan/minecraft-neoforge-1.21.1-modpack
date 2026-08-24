package at.petrak.hexcasting.api.casting.mishaps

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap.Context
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.api.utils.HexUtils
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.DyeColor

public class MishapImmuneEntity(entity: Entity) : Mishap {
   public final val entity: Entity

   init {
      this.entity = entity;
   }

   public override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment {
      return this.dyeColor(DyeColor.BLUE);
   }

   public override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
      env.getMishapEnvironment().yeetHeldItemsTowards(this.entity.position());
   }

   protected override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context): Component {
      val var3: Array<Any> = new Object[1];
      var var10004: Component = this.entity.getDisplayName();
      if (var10004 == null) {
         var10004 = this.entity.getName();
      }

      val var4: MutableComponent = var10004.plainCopy();
      var3[0] = HexUtils.getAqua(var4);
      return this.error("immune_entity", var3);
   }
}
