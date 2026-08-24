package at.petrak.hexcasting.interop.pehkui;

import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.minecraft.world.entity.Entity;

public class PehkuiInterop {
   public static void init() {
   }

   public static boolean isActive() {
      return IXplatAbstractions.INSTANCE.isModPresent("pehkui");
   }

   public interface ApiAbstraction {
      float getScale(Entity var1);

      void setScale(Entity var1, float var2);
   }
}
