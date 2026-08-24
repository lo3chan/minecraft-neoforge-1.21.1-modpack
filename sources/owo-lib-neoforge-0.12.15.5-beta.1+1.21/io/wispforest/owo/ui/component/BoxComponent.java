package io.wispforest.owo.ui.component;

import io.wispforest.owo.ui.base.BaseComponent;
import io.wispforest.owo.ui.core.AnimatableProperty;
import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.ui.parsing.UIParsing;
import java.util.Map;
import org.w3c.dom.Element;

public class BoxComponent extends BaseComponent {
   protected boolean fill = false;
   protected BoxComponent.GradientDirection direction = BoxComponent.GradientDirection.TOP_TO_BOTTOM;
   protected final AnimatableProperty<Color> startColor = AnimatableProperty.of(Color.BLACK);
   protected final AnimatableProperty<Color> endColor = AnimatableProperty.of(Color.BLACK);

   public BoxComponent(Sizing horizontalSizing, Sizing verticalSizing) {
      this.sizing(horizontalSizing, verticalSizing);
   }

   @Override
   public void update(float delta, int mouseX, int mouseY) {
      super.update(delta, mouseX, mouseY);
      this.startColor.update(delta);
      this.endColor.update(delta);
   }

   @Override
   public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
      int startColor = this.startColor.get().argb();
      int endColor = this.endColor.get().argb();
      if (this.fill) {
         switch (this.direction) {
            case TOP_TO_BOTTOM:
               context.drawGradientRect(this.x, this.y, this.width, this.height, startColor, startColor, endColor, endColor);
               break;
            case RIGHT_TO_LEFT:
               context.drawGradientRect(this.x, this.y, this.width, this.height, endColor, startColor, startColor, endColor);
               break;
            case BOTTOM_TO_TOP:
               context.drawGradientRect(this.x, this.y, this.width, this.height, endColor, endColor, startColor, startColor);
               break;
            case LEFT_TO_RIGHT:
               context.drawGradientRect(this.x, this.y, this.width, this.height, startColor, endColor, endColor, startColor);
         }
      } else {
         context.drawRectOutline(this.x, this.y, this.width, this.height, startColor);
      }
   }

   public BoxComponent fill(boolean fill) {
      this.fill = fill;
      return this;
   }

   public boolean fill() {
      return this.fill;
   }

   public BoxComponent direction(BoxComponent.GradientDirection direction) {
      this.direction = direction;
      return this;
   }

   public BoxComponent.GradientDirection direction() {
      return this.direction;
   }

   public BoxComponent color(Color color) {
      this.startColor.set(color);
      this.endColor.set(color);
      return this;
   }

   public BoxComponent startColor(Color startColor) {
      this.startColor.set(startColor);
      return this;
   }

   public AnimatableProperty<Color> startColor() {
      return this.startColor;
   }

   public BoxComponent endColor(Color endColor) {
      this.endColor.set(endColor);
      return this;
   }

   public AnimatableProperty<Color> endColor() {
      return this.endColor;
   }

   @Override
   public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
      super.parseProperties(model, element, children);
      UIParsing.expectChildren(element, children, "sizing");
      UIParsing.apply(children, "color", Color::parse, this::color);
      UIParsing.apply(children, "start-color", Color::parse, this::startColor);
      UIParsing.apply(children, "end-color", Color::parse, this::endColor);
      UIParsing.apply(children, "fill", UIParsing::parseBool, this::fill);
      UIParsing.apply(children, "direction", UIParsing.parseEnum(BoxComponent.GradientDirection.class), this::direction);
   }

   public static enum GradientDirection {
      TOP_TO_BOTTOM,
      RIGHT_TO_LEFT,
      BOTTOM_TO_TOP,
      LEFT_TO_RIGHT;
   }
}
