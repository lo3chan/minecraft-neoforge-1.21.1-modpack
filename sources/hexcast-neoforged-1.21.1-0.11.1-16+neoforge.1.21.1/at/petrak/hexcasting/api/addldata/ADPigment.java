package at.petrak.hexcasting.api.addldata;

import at.petrak.hexcasting.api.pigment.ColorProvider;
import java.util.UUID;
import net.minecraft.util.Mth;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.phys.Vec3;

public interface ADPigment {
   ColorProvider provideColor(UUID var1);

   static int morphBetweenColors(int[] colors, Vec3 gradientDir, float time, Vec3 position) {
      float fIdx = Mth.positiveModulo(time + (float)gradientDir.dot(position), 1.0F) * colors.length;
      int baseIdx = Mth.floor(fIdx);
      float tRaw = fIdx - baseIdx;
      float t = tRaw < 0.5 ? 4.0F * tRaw * tRaw * tRaw : (float)(1.0 - Math.pow(-2.0F * tRaw + 2.0F, 3.0) / 2.0);
      int start = colors[baseIdx % colors.length];
      int end = colors[(baseIdx + 1) % colors.length];
      int r1 = ARGB32.red(start);
      int g1 = ARGB32.green(start);
      int b1 = ARGB32.blue(start);
      int a1 = ARGB32.alpha(start);
      int r2 = ARGB32.red(end);
      int g2 = ARGB32.green(end);
      int b2 = ARGB32.blue(end);
      int a2 = ARGB32.alpha(end);
      float r = Mth.lerp(t, r1, r2);
      float g = Mth.lerp(t, g1, g2);
      float b = Mth.lerp(t, b1, b2);
      float a = Mth.lerp(t, a1, a2);
      return ARGB32.color((int)a, (int)r, (int)g, (int)b);
   }
}
