package io.wispforest.owo.ui.hud;

import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import java.util.function.Consumer;
import org.jetbrains.annotations.Nullable;

public class HudContainer extends FlowLayout {
   protected HudContainer(Sizing horizontalSizing, Sizing verticalSizing) {
      super(horizontalSizing, verticalSizing, FlowLayout.Algorithm.VERTICAL);
   }

   @Override
   protected void mountChild(@Nullable Component child, Consumer<Component> layoutFunc) {
      if (child != null) {
         if (child.positioning().get().type == Positioning.Type.LAYOUT) {
            throw new IllegalStateException("owo-ui HUD components must be explicitly positioned");
         } else {
            super.mountChild(child, layoutFunc);
         }
      }
   }
}
