package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.item.ItemStack;

public final class AMStraddleboardTint {
   public static final AMStraddleboardTint BASE = new AMStraddleboardTint(false);
   public static final AMStraddleboardTint PANEL = new AMStraddleboardTint(true);
   public static final MapCodec<AMStraddleboardTint> BASE_CODEC = MapCodec.unit(BASE);
   public static final MapCodec<AMStraddleboardTint> PANEL_CODEC = MapCodec.unit(PANEL);
   private final boolean panel;

   private AMStraddleboardTint(boolean panel) {
      this.panel = panel;
   }

   public static int tintOf(ItemStack stack, boolean panel) {
      int rgb = panel ? AMCompat.getDyedColor(stack, AMConfig.straddleboardPanelColor) : AMConfig.straddleboardBaseColor;
      return 0xFF000000 | rgb;
   }
}
