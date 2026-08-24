package at.petrak.hexcasting.api.casting.mishaps

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap.Context
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.api.utils.HexUtils
import java.util.Arrays
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack

public class MishapBadOffhandItem(item: ItemStack, hand: InteractionHand?, wanted: Component) : Mishap {
   public final val item: ItemStack
   public final val hand: InteractionHand?
   public final val wanted: Component

   init {
      this.item = item;
      this.hand = hand;
      this.wanted = wanted;
   }

   public override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment {
      return this.dyeColor(DyeColor.BROWN);
   }

   public override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
      env.getMishapEnvironment().dropHeldItems();
   }

   protected override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context): Component {
      return if (this.item.isEmpty())
         this.error("no_item.offhand", new Object[]{this.wanted})
         else
         this.error("bad_item.offhand", new Object[]{this.wanted, this.item.getCount(), this.item.getDisplayName()});
   }

   public companion object {
      public fun of(item: ItemStack, hand: InteractionHand?, stub: String, vararg args: Any): MishapBadOffhandItem {
         return new MishapBadOffhandItem(
            item, hand, HexUtils.asTranslatedComponent("hexcasting.mishap.bad_item.$stub", Arrays.copyOf(args, args.length)) as Component
         );
      }
   }
}
