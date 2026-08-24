package at.petrak.hexcasting.common.items.pigment;

import at.petrak.hexcasting.api.addldata.ADPigment;
import at.petrak.hexcasting.api.item.PigmentItem;
import at.petrak.hexcasting.api.pigment.ColorProvider;
import java.awt.Color;
import java.util.Random;
import java.util.UUID;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.phys.Vec3;

public class ItemUUIDPigment extends Item implements PigmentItem {
   public ItemUUIDPigment(Properties pProperties) {
      super(pProperties);
   }

   @Override
   public ColorProvider provideColor(ItemStack stack, UUID owner) {
      return new ItemUUIDPigment.MyColorProvider(owner);
   }

   protected static class MyColorProvider extends ColorProvider {
      private final int[] colors;

      MyColorProvider(UUID owner) {
         Random rand = new Random(owner.getLeastSignificantBits() ^ owner.getMostSignificantBits());
         float hue1 = rand.nextFloat();
         float saturation1 = rand.nextFloat(0.4F, 0.8F);
         float brightness1 = rand.nextFloat(0.7F, 1.0F);
         float hue2 = rand.nextFloat();
         float saturation2 = rand.nextFloat(0.7F, 1.0F);
         float brightness2 = rand.nextFloat(0.2F, 0.7F);
         int col1 = Color.HSBtoRGB(hue1, saturation1, brightness1);
         int col2 = Color.HSBtoRGB(hue2, saturation2, brightness2);
         this.colors = new int[]{col1, col2};
      }

      @Override
      protected int getRawColor(float time, Vec3 position) {
         return ADPigment.morphBetweenColors(this.colors, new Vec3(0.1, 0.1, 0.1), time / 400.0F, position);
      }
   }
}
