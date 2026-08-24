package com.finndog.moogs_structures.client;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ClothRequiredScreen extends Screen {
   private static final Component MESSAGE = Component.literal(
      "Moog's Structure Lib's config screen requires Cloth Config API. Install it to configure in game."
   );
   private final Screen parent;

   public ClothRequiredScreen(Screen parent) {
      super(Component.literal("Moog's Structure Lib"));
      this.parent = parent;
   }

   protected void init() {
      int cx = this.width / 2;
      MultiLineTextWidget text = new MultiLineTextWidget(cx - 155, this.height / 2 - 40, MESSAGE, this.font);
      text.setMaxWidth(310);
      text.setCentered(true);
      this.addRenderableWidget(text);
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> this.onClose()).bounds(cx - 100, this.height / 2 + 30, 200, 20).build());
   }

   public void onClose() {
      this.minecraft.setScreen(this.parent);
   }
}
