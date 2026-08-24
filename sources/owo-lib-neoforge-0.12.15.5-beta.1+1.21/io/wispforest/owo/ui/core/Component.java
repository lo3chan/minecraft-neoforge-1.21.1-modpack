package io.wispforest.owo.ui.core;

import io.wispforest.owo.ui.event.CharTyped;
import io.wispforest.owo.ui.event.FocusGained;
import io.wispforest.owo.ui.event.FocusLost;
import io.wispforest.owo.ui.event.KeyPress;
import io.wispforest.owo.ui.event.MouseDown;
import io.wispforest.owo.ui.event.MouseDrag;
import io.wispforest.owo.ui.event.MouseEnter;
import io.wispforest.owo.ui.event.MouseLeave;
import io.wispforest.owo.ui.event.MouseScroll;
import io.wispforest.owo.ui.event.MouseUp;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.ui.parsing.UIParsing;
import io.wispforest.owo.ui.util.FocusHandler;
import io.wispforest.owo.util.EventSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Element;

public interface Component extends PositionedRectangle {
   void draw(OwoUIDrawContext var1, int var2, int var3, float var4, float var5);

   default void drawTooltip(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
      if (this.shouldDrawTooltip(mouseX, mouseY)) {
         context.drawTooltip(Minecraft.getInstance().font, mouseX, mouseY, this.tooltip());
         context.flush();
      }
   }

   default void drawFocusHighlight(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
      context.drawRectOutline(this.x(), this.y(), this.width(), this.height(), -1);
   }

   @Contract(
      pure = true
   )
   @Nullable
   ParentComponent parent();

   @Contract(
      pure = true
   )
   @Nullable
   FocusHandler focusHandler();

   Component positioning(Positioning var1);

   @Contract(
      pure = true
   )
   AnimatableProperty<Positioning> positioning();

   Component margins(Insets var1);

   @Contract(
      pure = true
   )
   AnimatableProperty<Insets> margins();

   default Component sizing(Sizing horizontalSizing, Sizing verticalSizing) {
      this.horizontalSizing(horizontalSizing);
      this.verticalSizing(verticalSizing);
      return this;
   }

   default Component sizing(Sizing sizing) {
      this.sizing(sizing, sizing);
      return this;
   }

   Component horizontalSizing(Sizing var1);

   @Contract(
      pure = true
   )
   AnimatableProperty<Sizing> horizontalSizing();

   Component verticalSizing(Sizing var1);

   @Contract(
      pure = true
   )
   AnimatableProperty<Sizing> verticalSizing();

   Component id(@Nullable String var1);

   @Nullable
   String id();

   Component tooltip(@Nullable List<ClientTooltipComponent> var1);

   default Component tooltip(@NotNull Collection<net.minecraft.network.chat.Component> tooltip) {
      ArrayList<ClientTooltipComponent> components = new ArrayList<>();

      for (net.minecraft.network.chat.Component line : tooltip) {
         components.add(ClientTooltipComponent.create(line.getVisualOrderText()));
      }

      this.tooltip(components);
      return this;
   }

   default Component tooltip(@NotNull net.minecraft.network.chat.Component tooltip) {
      ArrayList<ClientTooltipComponent> components = new ArrayList<>();

      for (FormattedCharSequence line : Minecraft.getInstance().font.split(tooltip, 2147483647)) {
         components.add(ClientTooltipComponent.create(line));
      }

      this.tooltip(components);
      return this;
   }

   @Contract(
      pure = true
   )
   @Nullable
   List<ClientTooltipComponent> tooltip();

   Component zIndex(int var1);

   int zIndex();

   default boolean shouldDrawTooltip(double mouseX, double mouseY) {
      return this.tooltip() != null && !this.tooltip().isEmpty() && this.isInBoundingBox(mouseX, mouseY);
   }

   void inflate(Size var1);

   void mount(ParentComponent var1, int var2, int var3);

   void dismount(Component.DismountReason var1);

   <C extends Component> C configure(Consumer<C> var1);

   @Contract(
      pure = true
   )
   default boolean hasParent() {
      return this.parent() != null;
   }

   default ParentComponent root() {
      ParentComponent root = this.parent();
      if (root == null) {
         return null;
      } else {
         while (root.hasParent()) {
            root = root.parent();
         }

         return root;
      }
   }

   default void remove() {
      if (this.hasParent()) {
         this.parent().queue(() -> this.parent().removeChild(this));
      }
   }

   boolean onMouseDown(double var1, double var3, int var5);

   EventSource<MouseDown> mouseDown();

   boolean onMouseUp(double var1, double var3, int var5);

   EventSource<MouseUp> mouseUp();

   boolean onMouseScroll(double var1, double var3, double var5);

   EventSource<MouseScroll> mouseScroll();

   boolean onMouseDrag(double var1, double var3, double var5, double var7, int var9);

   EventSource<MouseDrag> mouseDrag();

   boolean onKeyPress(int var1, int var2, int var3);

   EventSource<KeyPress> keyPress();

   boolean onCharTyped(char var1, int var2);

   EventSource<CharTyped> charTyped();

   default boolean canFocus(Component.FocusSource source) {
      return false;
   }

   void onFocusGained(Component.FocusSource var1);

   EventSource<FocusGained> focusGained();

   void onFocusLost();

   EventSource<FocusLost> focusLost();

   EventSource<MouseEnter> mouseEnter();

   EventSource<MouseLeave> mouseLeave();

   CursorStyle cursorStyle();

   Component cursorStyle(CursorStyle var1);

   default void update(float delta, int mouseX, int mouseY) {
      this.margins().update(delta);
      this.positioning().update(delta);
      this.horizontalSizing().update(delta);
      this.verticalSizing().update(delta);
   }

   @Override
   default boolean isInBoundingBox(double x, double y) {
      return PositionedRectangle.super.isInBoundingBox(x, y);
   }

   default Size fullSize() {
      Insets margins = this.margins().get();
      return Size.of(this.width() + margins.horizontal(), this.height() + margins.vertical());
   }

   default void parseProperties(UIModel model, Element element, Map<String, Element> children) {
      if (!element.getAttribute("id").isBlank()) {
         this.id(element.getAttribute("id").strip());
      }

      UIParsing.apply(children, "margins", Insets::parse, this::margins);
      UIParsing.apply(children, "positioning", Positioning::parse, this::positioning);
      UIParsing.apply(children, "z-index", UIParsing::parseSignedInt, this::zIndex);
      UIParsing.apply(children, "cursor-style", UIParsing.parseEnum(CursorStyle.class), this::cursorStyle);
      UIParsing.apply(children, "tooltip-text", UIParsing::parseText, this::tooltip);
      if (children.containsKey("sizing")) {
         Map<String, Element> sizingValues = UIParsing.childElements(children.get("sizing"));
         UIParsing.apply(sizingValues, "vertical", Sizing::parse, this::verticalSizing);
         UIParsing.apply(sizingValues, "horizontal", Sizing::parse, this::horizontalSizing);
      }
   }

   @Contract(
      pure = true
   )
   @Override
   int width();

   @Contract(
      pure = true
   )
   @Override
   int height();

   @Contract(
      pure = true
   )
   @Override
   int x();

   default int baseX() {
      return this.x();
   }

   void updateX(int var1);

   @Contract(
      pure = true
   )
   @Override
   int y();

   default int baseY() {
      return this.y();
   }

   void updateY(int var1);

   default void moveTo(int x, int y) {
      this.updateX(x);
      this.updateY(y);
   }

   public static enum DismountReason {
      LAYOUT_INFLATION,
      REMOVED;
   }

   public static enum FocusSource {
      MOUSE_CLICK,
      KEYBOARD_CYCLE;
   }
}
