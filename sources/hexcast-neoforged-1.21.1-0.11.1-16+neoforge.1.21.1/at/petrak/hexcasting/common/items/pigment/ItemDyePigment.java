package at.petrak.hexcasting.common.items.pigment;

import at.petrak.hexcasting.api.item.PigmentItem;
import at.petrak.hexcasting.api.pigment.ColorProvider;
import java.util.UUID;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.phys.Vec3;

public class ItemDyePigment extends Item implements PigmentItem {
   private final DyeColor dyeColor;
   protected ItemDyePigment.MyColorProvider colorProvider = new ItemDyePigment.MyColorProvider();

   public ItemDyePigment(DyeColor dyeColor, Properties pProperties) {
      super(pProperties);
      this.dyeColor = dyeColor;
   }

   public DyeColor getDyeColor() {
      return this.dyeColor;
   }

   @Override
   public ColorProvider provideColor(ItemStack stack, UUID owner) {
      return this.colorProvider;
   }

   protected class MyColorProvider extends ColorProvider {
      @Override
      protected int getRawColor(float time, Vec3 position) {
         return ItemDyePigment.this.dyeColor.getTextColor();
      }
   }
}
