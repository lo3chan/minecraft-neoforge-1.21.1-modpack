package at.petrak.hexcasting.api.pigment;

import at.petrak.hexcasting.api.addldata.ADPigment;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.phys.Vec3;

public abstract class ColorProvider {
   private static final int[] MINIMUM_LUMINANCE_COLOR_WHEEL = new int[]{-14680064, -14671872, -16769024, -16768992, -16777184, -14680032};
   public static final ColorProvider MISSING = new ColorProvider() {
      @Override
      protected int getRawColor(float time, Vec3 position) {
         return -65316;
      }
   };

   protected abstract int getRawColor(float var1, Vec3 var2);

   public final int getColor(float time, Vec3 position) {
      int raw = this.getRawColor(time, position);
      int r = ARGB32.red(raw);
      int g = ARGB32.green(raw);
      int b = ARGB32.blue(raw);
      double luminance = (0.2126 * r + 0.7152 * g + 0.0722 * b) / 255.0;
      if (luminance < 0.05) {
         int rawMod = ADPigment.morphBetweenColors(MINIMUM_LUMINANCE_COLOR_WHEEL, new Vec3(0.1, 0.1, 0.1), time / 20.0F / 20.0F, position);
         r += ARGB32.red(rawMod);
         g += ARGB32.green(rawMod);
         b += ARGB32.blue(rawMod);
      }

      return 0xFF000000 | r << 16 | g << 8 | b;
   }
}
