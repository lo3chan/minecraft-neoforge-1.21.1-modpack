package io.wispforest.owo.ui.component;

import com.mojang.blaze3d.vertex.PoseStack;
import io.wispforest.owo.Owo;
import io.wispforest.owo.mixin.ui.access.EditBoxAccessor;
import io.wispforest.owo.mixin.ui.access.EditBoxWidgetAccessor;
import io.wispforest.owo.ui.core.CursorStyle;
import io.wispforest.owo.ui.core.Size;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.ui.parsing.UIParsing;
import io.wispforest.owo.util.EventSource;
import io.wispforest.owo.util.EventStream;
import io.wispforest.owo.util.Observable;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.MultilineTextField;
import net.minecraft.client.gui.components.Whence;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.w3c.dom.Element;

public class TextAreaComponent extends MultiLineEditBox {
   protected final Observable<String> textValue = Observable.of("");
   protected final EventStream<TextAreaComponent.OnChanged> changedEvents = TextAreaComponent.OnChanged.newStream();
   protected final MultilineTextField editBox;
   protected final Observable<Boolean> displayCharCount = Observable.of(false);
   protected final Observable<Integer> maxLines = Observable.of(-1);

   protected TextAreaComponent(Sizing horizontalSizing, Sizing verticalSizing) {
      super(Minecraft.getInstance().font, 0, 0, 0, 0, Component.empty(), Component.empty());
      this.editBox = ((EditBoxWidgetAccessor)this).owo$getEditBox();
      this.sizing(horizontalSizing, verticalSizing);
      this.textValue.observe(this.changedEvents.sink()::onChanged);
      Observable.observeAll(this.widgetWrapper()::notifyParentIfMounted, this.displayCharCount, this.maxLines);
      super.setValueListener(s -> {
         this.textValue.set(s);
         if (this.maxLines.get() >= 0) {
            this.widgetWrapper().notifyParentIfMounted();
         }
      });
   }

   @Deprecated(
      forRemoval = true
   )
   public void setValueListener(Consumer<String> changeListener) {
      Owo.debugWarn(Owo.LOGGER, "setChangeListener stub on TextAreaComponent invoked");
   }

   public void update(float delta, int mouseX, int mouseY) {
      super.update(delta, mouseX, mouseY);
      this.cursorStyle(this.scrollbarVisible() && mouseX >= this.getX() + this.width - 9 ? CursorStyle.NONE : CursorStyle.TEXT);
   }

   protected void renderDecorations(GuiGraphics context) {
      this.height--;
      PoseStack matrices = context.pose();
      matrices.pushPose();
      matrices.translate(-9.0F, 1.0F, 0.0F);
      int previousMaxLength = this.editBox.characterLimit();
      this.editBox.setCharacterLimit(2147483647);
      super.renderDecorations(context);
      this.editBox.setCharacterLimit(previousMaxLength);
      matrices.popPose();
      this.height++;
      if (this.displayCharCount.get()) {
         MutableComponent text = this.editBox.hasCharacterLimit()
            ? Component.translatable("gui.multiLineEditBox.character_limit", new Object[]{this.editBox.value().length(), this.editBox.characterLimit()})
            : Component.literal(String.valueOf(this.editBox.value().length()));
         Font textRenderer = Minecraft.getInstance().font;
         context.drawString(textRenderer, text, this.getX() + this.width - textRenderer.width(text), this.getY() + this.height + 3, 10526880);
      }
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      this.width -= 9;
      boolean result = super.mouseClicked(mouseX, mouseY, button);
      this.width += 9;
      return result;
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      boolean result = super.keyPressed(keyCode, scanCode, modifiers);
      if (keyCode == 258) {
         this.editBox.insertText("    ");
         return true;
      } else {
         return result;
      }
   }

   public void inflate(Size space) {
      super.inflate(space);
      int cursor = this.editBox.cursor();
      int selection = ((EditBoxAccessor)this.editBox).owo$getSelectionEnd();
      ((EditBoxAccessor)this.editBox).owo$setWidth(this.width() - this.totalInnerPadding() - 9);
      this.editBox.setValue(this.getValue());
      super.inflate(space);
      this.editBox.setValue(this.getValue());
      this.editBox.seekCursor(Whence.ABSOLUTE, cursor);
      ((EditBoxAccessor)this.editBox).owo$setSelectionEnd(selection);
   }

   public EventSource<TextAreaComponent.OnChanged> onChanged() {
      return this.changedEvents.source();
   }

   public TextAreaComponent maxLines(int maxLines) {
      this.maxLines.set(maxLines);
      return this;
   }

   public int maxLines() {
      return this.maxLines.get();
   }

   public TextAreaComponent displayCharCount(boolean displayCharCount) {
      this.displayCharCount.set(displayCharCount);
      return this;
   }

   public boolean displayCharCount() {
      return this.displayCharCount.get();
   }

   public TextAreaComponent text(String text) {
      this.setValue(text);
      return this;
   }

   public int heightOffset() {
      return this.displayCharCount.get() ? -12 : 0;
   }

   public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
      super.parseProperties(model, element, children);
      UIParsing.apply(children, "display-char-count", UIParsing::parseBool, this::displayCharCount);
      UIParsing.apply(children, "max-length", UIParsing::parseUnsignedInt, this::setCharacterLimit);
      UIParsing.apply(children, "max-lines", UIParsing::parseUnsignedInt, this::maxLines);
      UIParsing.apply(children, "text", $ -> $.getTextContent().strip(), this::text);
   }

   public interface OnChanged {
      void onChanged(String var1);

      static EventStream<TextAreaComponent.OnChanged> newStream() {
         return new EventStream<>(subscribers -> value -> {
            for (TextAreaComponent.OnChanged subscriber : subscribers) {
               subscriber.onChanged(value);
            }
         });
      }
   }
}
