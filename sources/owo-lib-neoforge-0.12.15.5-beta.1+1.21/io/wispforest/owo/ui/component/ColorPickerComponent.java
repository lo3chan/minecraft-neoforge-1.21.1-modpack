package io.wispforest.owo.ui.component;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import io.wispforest.owo.client.OwoClient;
import io.wispforest.owo.ui.base.BaseComponent;
import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.CursorStyle;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.ui.parsing.UIParsing;
import io.wispforest.owo.util.EventSource;
import io.wispforest.owo.util.EventStream;
import io.wispforest.owo.util.Observable;
import java.util.Map;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.w3c.dom.Element;

public class ColorPickerComponent extends BaseComponent {
   protected EventStream<ColorPickerComponent.OnChanged> changedEvents = ColorPickerComponent.OnChanged.newStream();
   protected Observable<Color> selectedColor = Observable.of(Color.BLACK);
   @Nullable
   protected ColorPickerComponent.Section lastClicked = null;
   protected float hue = 0.5F;
   protected float saturation = 1.0F;
   protected float value = 1.0F;
   protected float alpha = 1.0F;
   protected int selectorWidth = 20;
   protected int selectorPadding = 10;
   protected boolean showAlpha = false;
   private int lastCursorX;

   public ColorPickerComponent() {
      this.selectedColor.observe(this.changedEvents.sink()::onChanged);
   }

   @Override
   public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
      this.lastCursorX = mouseX - this.x;
      BufferBuilder buffer = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
      Matrix4f matrix = context.pose().last().pose();
      buffer.addVertex(matrix, this.renderX(), this.renderY(), 0.0F).setColor(this.hue, 0.0F, 1.0F, 1.0F);
      buffer.addVertex(matrix, this.renderX(), this.renderY() + this.renderHeight(), 0.0F).setColor(this.hue, 0.0F, 0.0F, 1.0F);
      buffer.addVertex(matrix, this.renderX() + this.colorAreaWidth(), this.renderY() + this.renderHeight(), 0.0F).setColor(this.hue, 1.0F, 0.0F, 1.0F);
      buffer.addVertex(matrix, this.renderX() + this.colorAreaWidth(), this.renderY(), 0.0F).setColor(this.hue, 1.0F, 1.0F, 1.0F);
      OwoClient.HSV_PROGRAM.use();
      BufferUploader.drawWithShader(buffer.buildOrThrow());
      context.drawRectOutline(
         (int)(this.renderX() + this.saturation * this.colorAreaWidth() - 1.0F),
         (int)(this.renderY() + (1.0F - this.value) * (this.renderHeight() - 1) - 1.0F),
         3,
         3,
         Color.WHITE.argb()
      );
      context.drawSpectrum(this.renderX() + this.hueSelectorX(), this.renderY(), this.selectorWidth, this.renderHeight(), true);
      context.drawRectOutline(
         this.renderX() + this.hueSelectorX() - 1,
         this.renderY() + (int)((this.renderHeight() - 1) * (1.0F - this.hue) - 1.0F),
         this.selectorWidth + 2,
         3,
         Color.WHITE.argb()
      );
      if (this.showAlpha) {
         int color = 0xFF000000 | this.selectedColor.get().rgb();
         context.drawGradientRect(this.renderX() + this.alphaSelectorX(), this.renderY(), this.selectorWidth, this.renderHeight(), color, color, 0, 0);
         context.drawRectOutline(
            this.renderX() + this.alphaSelectorX() - 1,
            this.renderY() + (int)((this.renderHeight() - 1) * (1.0F - this.alpha) - 1.0F),
            this.selectorWidth + 2,
            3,
            Color.WHITE.argb()
         );
      }
   }

   @Override
   public boolean onMouseDown(double mouseX, double mouseY, int button) {
      this.lastClicked = this.showAlpha && mouseX >= this.alphaSelectorX()
         ? ColorPickerComponent.Section.ALPHA_SELECTOR
         : (mouseX > this.hueSelectorX() ? ColorPickerComponent.Section.HUE_SELECTOR : ColorPickerComponent.Section.COLOR_AREA);
      this.updateFromMouse(mouseX, mouseY);
      super.onMouseDown(mouseX, mouseY, button);
      return true;
   }

   @Override
   public boolean onMouseDrag(double mouseX, double mouseY, double deltaX, double deltaY, int button) {
      this.updateFromMouse(mouseX, mouseY);
      super.onMouseDrag(mouseX, mouseY, deltaX, deltaY, button);
      return true;
   }

   @Override
   public boolean canFocus(Component.FocusSource source) {
      return true;
   }

   @Override
   public CursorStyle cursorStyle() {
      boolean inColorArea = this.lastCursorX >= 0 && this.lastCursorX <= this.colorAreaWidth();
      boolean inHueSelector = this.lastCursorX >= this.hueSelectorX() && this.lastCursorX <= this.hueSelectorX() + this.selectorWidth;
      boolean inAlphaSelector = this.showAlpha && this.lastCursorX >= this.alphaSelectorX() && this.lastCursorX <= this.alphaSelectorX() + this.selectorWidth;
      return !inColorArea && !inHueSelector && !inAlphaSelector ? super.cursorStyle() : CursorStyle.MOVE;
   }

   protected void updateFromMouse(double mouseX, double mouseY) {
      mouseX = Mth.clamp(mouseX - 1.0, 0.0, this.renderWidth());
      mouseY = Mth.clamp(mouseY - 1.0, 0.0, this.renderHeight());
      if (this.lastClicked == ColorPickerComponent.Section.ALPHA_SELECTOR) {
         this.alpha = 1.0F - (float)(mouseY / this.renderHeight());
      } else if (this.lastClicked == ColorPickerComponent.Section.HUE_SELECTOR) {
         this.hue = 1.0F - (float)(mouseY / this.renderHeight());
      } else if (this.lastClicked == ColorPickerComponent.Section.COLOR_AREA) {
         this.saturation = Math.min(1.0F, (float)(mouseX / this.colorAreaWidth()));
         this.value = 1.0F - (float)(mouseY / this.renderHeight());
      }

      this.selectedColor.set(Color.ofHsv(this.hue, this.saturation, this.value, this.alpha));
   }

   protected int renderX() {
      return this.x + 1;
   }

   protected int renderY() {
      return this.y + 1;
   }

   protected int renderWidth() {
      return this.width - 2;
   }

   protected int renderHeight() {
      return this.height - 2;
   }

   protected int colorAreaWidth() {
      return this.showAlpha
         ? this.renderWidth() - this.selectorPadding - this.selectorWidth - this.selectorPadding - this.selectorWidth
         : this.renderWidth() - this.selectorPadding - this.selectorWidth;
   }

   protected int hueSelectorX() {
      return this.showAlpha ? this.renderWidth() - this.selectorWidth - this.selectorPadding - this.selectorWidth : this.renderWidth() - this.selectorWidth;
   }

   protected int alphaSelectorX() {
      return this.renderWidth() - this.selectorWidth;
   }

   public ColorPickerComponent selectedColor(Color color) {
      this.selectedColor.set(color);
      float[] hsv = color.hsv();
      this.hue = hsv[0];
      this.saturation = hsv[1];
      this.value = hsv[2];
      this.alpha = color.alpha();
      return this;
   }

   public ColorPickerComponent selectedColor(float hue, float saturation, float value) {
      this.selectedColor.set(Color.ofHsv(hue, saturation, value));
      this.hue = hue;
      this.saturation = saturation;
      this.value = value;
      this.alpha = 1.0F;
      return this;
   }

   public Color selectedColor() {
      return this.selectedColor.get();
   }

   public ColorPickerComponent selectorWidth(int selectorWidth) {
      this.selectorWidth = selectorWidth;
      return this;
   }

   public int selectorWidth() {
      return this.selectorWidth;
   }

   public ColorPickerComponent selectorPadding(int selectorPadding) {
      this.selectorPadding = selectorPadding;
      return this;
   }

   public int selectorPadding() {
      return this.selectorPadding;
   }

   public ColorPickerComponent showAlpha(boolean showAlpha) {
      this.showAlpha = showAlpha;
      return this;
   }

   public boolean showAlpha() {
      return this.showAlpha;
   }

   public EventSource<ColorPickerComponent.OnChanged> onChanged() {
      return this.changedEvents.source();
   }

   @Override
   public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
      super.parseProperties(model, element, children);
      UIParsing.apply(children, "show-alpha", UIParsing::parseBool, this::showAlpha);
      UIParsing.apply(children, "selector-width", UIParsing::parseUnsignedInt, this::selectorWidth);
      UIParsing.apply(children, "selector-padding", UIParsing::parseUnsignedInt, this::selectorPadding);
      UIParsing.apply(children, "selected-color", Color::parse, this::selectedColor);
   }

   public interface OnChanged {
      void onChanged(Color var1);

      static EventStream<ColorPickerComponent.OnChanged> newStream() {
         return new EventStream<>(subscribers -> value -> {
            for (ColorPickerComponent.OnChanged subscriber : subscribers) {
               subscriber.onChanged(value);
            }
         });
      }
   }

   protected static enum Section {
      COLOR_AREA,
      HUE_SELECTOR,
      ALPHA_SELECTOR;
   }
}
