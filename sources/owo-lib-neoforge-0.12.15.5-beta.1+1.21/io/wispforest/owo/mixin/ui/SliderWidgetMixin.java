package io.wispforest.owo.mixin.ui;

import io.wispforest.owo.ui.component.DiscreteSliderComponent;
import io.wispforest.owo.ui.component.SliderComponent;
import io.wispforest.owo.ui.core.CursorStyle;
import java.math.BigDecimal;
import java.math.RoundingMode;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({AbstractSliderButton.class})
public abstract class SliderWidgetMixin extends AbstractWidget {
   @Shadow
   protected double value;

   @Shadow
   protected abstract void setValue(double var1);

   public SliderWidgetMixin(int x, int y, int width, int height, Component message) {
      super(x, y, width, height, message);
   }

   @ModifyArg(
      method = {"keyPressed(III)Z"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/components/AbstractSliderButton;setValue(D)V"
      )
   )
   private double injectCustomStep(double value) {
      return this instanceof SliderComponent slider ? this.value + Math.signum(value - this.value) * slider.scrollStep() : value;
   }

   @Inject(
      method = {"setValueFromMouse(D)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void makeItSnappyTeam(double mouseX, CallbackInfo ci) {
      if (this instanceof DiscreteSliderComponent discrete) {
         if (discrete.snap()) {
            ci.cancel();
            double value = (mouseX - (this.getX() + 4.0)) / (this.width - 8.0);
            double min = discrete.min();
            double max = discrete.max();
            int decimalPlaces = discrete.decimalPlaces();
            this.setValue((new BigDecimal(min + value * (max - min)).setScale(decimalPlaces, RoundingMode.HALF_UP).doubleValue() - min) / (max - min));
         }
      }
   }

   protected CursorStyle owo$preferredCursorStyle() {
      return CursorStyle.MOVE;
   }
}
