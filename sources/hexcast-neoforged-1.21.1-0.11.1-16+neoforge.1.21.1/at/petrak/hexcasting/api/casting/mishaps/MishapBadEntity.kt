package at.petrak.hexcasting.api.casting.mishaps

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap.Context
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.api.utils.HexUtils
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.DyeColor

public class MishapBadEntity(entity: Entity, wanted: Component) : Mishap {
   public final val entity: Entity
   public final val wanted: Component

   init {
      this.entity = entity;
      this.wanted = wanted;
   }

   public override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment {
      return this.dyeColor(DyeColor.BROWN);
   }

   public override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
      env.getMishapEnvironment().yeetHeldItemsTowards(this.entity.position());
   }

   protected override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context): Component {
      val var3: Array<Any> = new Object[]{this.wanted, null};
      var var10004: Component = this.entity.getDisplayName();
      if (var10004 == null) {
         var10004 = this.entity.getName();
      }

      val var4: MutableComponent = var10004.plainCopy();
      var3[1] = HexUtils.getAqua(var4);
      return this.error("bad_entity", var3);
   }

   public companion object {
      public fun of(entity: Entity, stub: String): Mishap {
         val component: MutableComponent = HexUtils.getAsTranslatedComponent("hexcasting.mishap.bad_item.$stub");
         return if (entity is ItemEntity)
            new MishapBadItem(entity as ItemEntity, component as Component)
            else
            new MishapBadEntity(entity, component as Component);
      }
   }
}
