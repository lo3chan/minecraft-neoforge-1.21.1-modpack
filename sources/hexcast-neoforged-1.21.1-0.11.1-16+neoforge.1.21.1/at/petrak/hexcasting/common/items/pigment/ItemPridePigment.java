package at.petrak.hexcasting.common.items.pigment;

import at.petrak.hexcasting.api.addldata.ADPigment;
import at.petrak.hexcasting.api.item.PigmentItem;
import at.petrak.hexcasting.api.pigment.ColorProvider;
import java.util.Locale;
import java.util.UUID;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.phys.Vec3;

public class ItemPridePigment extends Item implements PigmentItem {
   public final ItemPridePigment.Type type;
   protected ItemPridePigment.MyColorProvider colorProvider = new ItemPridePigment.MyColorProvider();

   public ItemPridePigment(ItemPridePigment.Type type, Properties pProperties) {
      super(pProperties);
      this.type = type;
   }

   @Override
   public ColorProvider provideColor(ItemStack stack, UUID owner) {
      return this.colorProvider;
   }

   protected class MyColorProvider extends ColorProvider {
      @Override
      protected int getRawColor(float time, Vec3 position) {
         return ADPigment.morphBetweenColors(ItemPridePigment.this.type.colors, new Vec3(0.1, 0.1, 0.1), time / 400.0F, position);
      }
   }

   public static enum Type {
      AGENDER(new int[]{1483020, 16777215, 8028289, 3157808}),
      AROACE(new int[]{7475388, 15463271, 16777215, 8576235, 3100120}),
      AROMANTIC(new int[]{1483020, 8579979, 16777215, 8028289, 3157808}),
      ASEXUAL(new int[]{3355187, 10133409, 16777215, 7475388}),
      BISEXUAL(new int[]{14370303, 10234832, 6853844}),
      DEMIBOY(new int[]{10133409, 11141119, 16777215}),
      DEMIGIRL(new int[]{10133409, 16560639, 16777215}),
      GAY(new int[]{14167866, 14714943, 15463271, 2995224, 3100120}),
      GENDERFLUID(new int[]{16493817, 16777215, 10234832, 3355187, 3100120}),
      GENDERQUEER(new int[]{13269231, 16777215, 2995224}),
      INTERSEX(new int[]{15463271, 7475388}),
      LESBIAN(new int[]{14167866, 15710333, 16777215, 16493817, 10682978}),
      NONBINARY(new int[]{15463271, 16777215, 7475388, 3355187}),
      PANSEXUAL(new int[]{14842095, 15463271, 6996708}),
      PLURAL(new int[]{3196575, 3440095, 7028670, 0}),
      TRANSGENDER(new int[]{15438570, 16777215, 6996708});

      private final int[] colors;

      private Type(int[] colors) {
         this.colors = colors;

         for (int i = 0; i < this.colors.length; i++) {
            this.colors[i] = this.colors[i] | 0xFF000000;
         }
      }

      public String getName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
