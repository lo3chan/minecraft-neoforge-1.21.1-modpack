package at.petrak.hexcasting.common.items.pigment;

import at.petrak.hexcasting.api.addldata.ADPigment;
import at.petrak.hexcasting.api.item.PigmentItem;
import at.petrak.hexcasting.api.pigment.ColorProvider;
import java.util.UUID;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.phys.Vec3;

public class ItemAmethystAndCopperPigment extends Item implements PigmentItem {
   protected ItemAmethystAndCopperPigment.MyColorProvider colorProvider = new ItemAmethystAndCopperPigment.MyColorProvider();

   public ItemAmethystAndCopperPigment(Properties pProperties) {
      super(pProperties);
   }

   @Override
   public ColorProvider provideColor(ItemStack stack, UUID owner) {
      return this.colorProvider;
   }

   protected class MyColorProvider extends ColorProvider {
      private static final int[] COLORS = new int[]{-11257462, -3170061, -78874, -3170061, -1606570};

      @Override
      protected int getRawColor(float time, Vec3 position) {
         return ADPigment.morphBetweenColors(COLORS, new Vec3(0.1, 0.1, 0.1), time / 600.0F, position);
      }
   }
}
