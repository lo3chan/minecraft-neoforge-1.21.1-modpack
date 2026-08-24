package io.wispforest.owo.ui.component;

import io.wispforest.owo.mixin.ui.access.CheckboxWidgetAccessor;
import io.wispforest.owo.ui.core.CursorStyle;
import io.wispforest.owo.ui.core.Size;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.ui.parsing.UIParsing;
import io.wispforest.owo.util.Observable;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.network.chat.Component;
import org.w3c.dom.Element;

public class CheckboxComponent extends Checkbox {
   protected final Observable<Boolean> listeners = Observable.of(this.selected());

   protected CheckboxComponent(Component message) {
      super(0, 0, 0, message, Minecraft.getInstance().font, false, (checkbox, checked) -> {});
      this.sizing(Sizing.content(), Sizing.fixed(20));
   }

   public void onPress() {
      super.onPress();
      this.listeners.set(this.selected());
   }

   public CheckboxComponent checked(boolean checked) {
      ((CheckboxWidgetAccessor)this).owo$setChecked(checked);
      this.listeners.set(this.selected());
      return this;
   }

   public CheckboxComponent onChanged(Consumer<Boolean> listener) {
      this.listeners.observe(listener);
      return this;
   }

   public void inflate(Size space) {
      super.inflate(space);
      ((CheckboxWidgetAccessor)this).owo$getTextWidget().setMaxWidth(this.width);
   }

   public void setMessage(Component message) {
      super.setMessage(message);
      ((CheckboxWidgetAccessor)this).owo$getTextWidget().setMessage(message);
   }

   public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
      super.parseProperties(model, element, children);
      UIParsing.apply(children, "checked", UIParsing::parseBool, this::checked);
      UIParsing.apply(children, "text", UIParsing::parseText, this::setMessage);
   }

   public CursorStyle owo$preferredCursorStyle() {
      return CursorStyle.HAND;
   }
}
