package io.wispforest.owo.ui.component;

import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.ui.parsing.UIParsing;
import io.wispforest.owo.util.EventSource;
import io.wispforest.owo.util.EventStream;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import org.w3c.dom.Element;

public class SliderComponent extends AbstractSliderButton {
   protected final EventStream<SliderComponent.OnChanged> changedEvents = SliderComponent.OnChanged.newStream();
   protected final EventStream<SliderComponent.OnSlideEnd> slideEndEvents = SliderComponent.OnSlideEnd.newStream();
   protected Function<String, Component> messageProvider = value -> Component.empty();
   protected double scrollStep = 0.05;

   protected SliderComponent(Sizing horizontalSizing) {
      super(0, 0, 0, 0, Component.empty(), 0.0);
      this.sizing(horizontalSizing, Sizing.fixed(20));
   }

   public SliderComponent value(double value) {
      value = Mth.clamp(value, 0.0, 1.0);
      if (this.value != value) {
         this.value = value;
         this.updateMessage();
         this.applyValue();
      }

      return this;
   }

   public double value() {
      return this.value;
   }

   public SliderComponent message(Function<String, Component> messageProvider) {
      this.messageProvider = messageProvider;
      this.updateMessage();
      return this;
   }

   public SliderComponent scrollStep(double scrollStep) {
      this.scrollStep = scrollStep;
      return this;
   }

   public double scrollStep() {
      return this.scrollStep;
   }

   public SliderComponent active(boolean active) {
      this.active = active;
      return this;
   }

   public boolean active() {
      return this.active;
   }

   public EventSource<SliderComponent.OnChanged> onChanged() {
      return this.changedEvents.source();
   }

   public EventSource<SliderComponent.OnSlideEnd> slideEnd() {
      return this.slideEndEvents.source();
   }

   protected void updateMessage() {
      this.setMessage(this.messageProvider.apply(String.valueOf(this.value)));
   }

   protected void applyValue() {
      this.changedEvents.sink().onChanged(this.value);
   }

   public boolean onMouseScroll(double mouseX, double mouseY, double amount) {
      if (!this.active) {
         return super.onMouseScroll(mouseX, mouseY, amount);
      } else {
         this.value(this.value + this.scrollStep * amount);
         super.onMouseScroll(mouseX, mouseY, amount);
         return true;
      }
   }

   public boolean onMouseUp(double mouseX, double mouseY, int button) {
      this.slideEndEvents.sink().onSlideEnd();
      return super.onMouseUp(mouseX, mouseY, button);
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      return !this.active ? false : super.keyPressed(keyCode, scanCode, modifiers);
   }

   protected boolean isValidClickButton(int button) {
      return this.active && super.isValidClickButton(button);
   }

   public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
      super.parseProperties(model, element, children);
      if (children.containsKey("text")) {
         Element node = children.get("text");
         String content = node.getTextContent().strip();
         if (node.getAttribute("translate").equalsIgnoreCase("true")) {
            this.message(value -> Component.translatable(content, new Object[]{value}));
         } else {
            MutableComponent text = Component.literal(content);
            this.message(value -> text);
         }
      }

      UIParsing.apply(children, "value", UIParsing::parseDouble, this::value);
   }

   @Deprecated
   public final void setMessage(Component message) {
      super.setMessage(message);
   }

   public interface OnChanged {
      void onChanged(double var1);

      static EventStream<SliderComponent.OnChanged> newStream() {
         return new EventStream<>(subscribers -> value -> {
            for (SliderComponent.OnChanged subscriber : subscribers) {
               subscriber.onChanged(value);
            }
         });
      }
   }

   public interface OnSlideEnd {
      void onSlideEnd();

      static EventStream<SliderComponent.OnSlideEnd> newStream() {
         return new EventStream<>(subscribers -> () -> {
            for (SliderComponent.OnSlideEnd subscriber : subscribers) {
               subscriber.onSlideEnd();
            }
         });
      }
   }
}
