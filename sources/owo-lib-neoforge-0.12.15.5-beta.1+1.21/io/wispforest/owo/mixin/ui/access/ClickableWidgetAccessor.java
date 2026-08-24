package io.wispforest.owo.mixin.ui.access;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.WidgetTooltipHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({AbstractWidget.class})
public interface ClickableWidgetAccessor {
   @Accessor("height")
   void owo$setHeight(int var1);

   @Accessor("width")
   void owo$setWidth(int var1);

   @Accessor("x")
   void owo$setX(int var1);

   @Accessor("y")
   void owo$setY(int var1);

   @Accessor("tooltip")
   WidgetTooltipHolder owo$getTooltip();
}
