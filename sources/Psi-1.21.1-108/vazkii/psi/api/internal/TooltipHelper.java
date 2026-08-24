package vazkii.psi.api.internal;

import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public final class TooltipHelper {
   @OnlyIn(Dist.CLIENT)
   public static void tooltipIfShift(List<Component> tooltip, Runnable r) {
      if (Screen.hasShiftDown()) {
         r.run();
      } else {
         tooltip.add(Component.translatable("psimisc.shift_for_info"));
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static void tooltipIfCtrl(List<Component> tooltip, Runnable r) {
      if (Screen.hasControlDown()) {
         r.run();
      } else {
         tooltip.add(Component.translatable("psimisc.ctrl_for_stats"));
      }
   }
}
