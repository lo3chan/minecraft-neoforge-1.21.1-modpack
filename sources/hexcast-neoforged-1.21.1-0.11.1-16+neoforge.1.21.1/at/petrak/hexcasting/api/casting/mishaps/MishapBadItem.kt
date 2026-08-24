package at.petrak.hexcasting.api.casting.mishaps

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap.Context
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.api.utils.HexUtils
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.DyeColor

public class MishapBadItem(item: ItemEntity, wanted: Component) : Mishap {
   public final val item: ItemEntity
   public final val wanted: Component

   init {
      this.item = item;
      this.wanted = wanted;
   }

   public override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment {
      return this.dyeColor(DyeColor.BROWN);
   }

   public override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
      this.item.setDeltaMovement(this.item.getDeltaMovement().add((Math.random() - 0.5) * 0.05, 0.75, (Math.random() - 0.5) * 0.05));
   }

   protected override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context): Component {
      return if (this.item.getItem().isEmpty())
         this.error("no_item", new Object[]{this.wanted})
         else
         this.error("bad_item", new Object[]{this.wanted, this.item.getItem().getCount(), this.item.getItem().getDisplayName()});
   }

   public companion object {
      public fun of(item: ItemEntity, stub: String): MishapBadItem {
         return new MishapBadItem(item, HexUtils.getAsTranslatedComponent("hexcasting.mishap.bad_item.$stub") as Component);
      }
   }
}
