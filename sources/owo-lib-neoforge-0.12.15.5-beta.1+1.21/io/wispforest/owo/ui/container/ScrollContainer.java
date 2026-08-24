package io.wispforest.owo.ui.container;

import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.Easing;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Size;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.ui.parsing.UIModelParsingException;
import io.wispforest.owo.ui.parsing.UIParsing;
import io.wispforest.owo.ui.util.Delta;
import io.wispforest.owo.ui.util.NinePatchTexture;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.w3c.dom.Element;

public class ScrollContainer<C extends Component> extends WrappingParentComponent<C> {
   public static final ResourceLocation VERTICAL_VANILLA_SCROLLBAR_TEXTURE = ResourceLocation.fromNamespaceAndPath("owo", "scrollbar/vanilla_vertical");
   public static final ResourceLocation DISABLED_VERTICAL_VANILLA_SCROLLBAR_TEXTURE = ResourceLocation.fromNamespaceAndPath(
      "owo", "scrollbar/vanilla_vertical_disabled"
   );
   public static final ResourceLocation HORIZONTAL_VANILLA_SCROLLBAR_TEXTURE = ResourceLocation.fromNamespaceAndPath(
      "owo", "scrollbar/vanilla_horizontal_disabled"
   );
   public static final ResourceLocation DISABLED_HORIZONTAL_VANILLA_SCROLLBAR_TEXTURE = ResourceLocation.fromNamespaceAndPath(
      "owo", "scrollbar/vanilla_horizontal_disabled"
   );
   public static final ResourceLocation VANILLA_SCROLLBAR_TRACK_TEXTURE = ResourceLocation.fromNamespaceAndPath("owo", "scrollbar/track");
   public static final ResourceLocation FLAT_VANILLA_SCROLLBAR_TEXTURE = ResourceLocation.fromNamespaceAndPath("owo", "scrollbar/vanilla_flat");
   protected double scrollOffset = 0.0;
   protected double currentScrollPosition = 0.0;
   protected int lastScrollPosition = -1;
   protected int scrollStep = 0;
   protected int fixedScrollbarLength = 0;
   protected double lastScrollbarLength = 0.0;
   protected ScrollContainer.Scrollbar scrollbar = ScrollContainer.Scrollbar.flat(Color.ofArgb(-1610612736));
   protected int scrollbarThiccness = 3;
   protected long lastScrollbarInteractTime = 0L;
   protected int scrollbarOffset = 0;
   protected boolean scrollbaring = false;
   protected int maxScroll = 0;
   protected int childSize = 0;
   protected final ScrollContainer.ScrollDirection direction;

   protected ScrollContainer(ScrollContainer.ScrollDirection direction, Sizing horizontalSizing, Sizing verticalSizing, C child) {
      super(horizontalSizing, verticalSizing, child);
      this.direction = direction;
   }

   @Override
   protected int determineHorizontalContentSize(Sizing sizing) {
      if (this.direction == ScrollContainer.ScrollDirection.VERTICAL) {
         return super.determineHorizontalContentSize(sizing);
      } else {
         throw new UnsupportedOperationException("Horizontal ScrollContainer cannot be horizontally content-sized");
      }
   }

   @Override
   protected int determineVerticalContentSize(Sizing sizing) {
      if (this.direction == ScrollContainer.ScrollDirection.HORIZONTAL) {
         return super.determineVerticalContentSize(sizing);
      } else {
         throw new UnsupportedOperationException("Vertical ScrollContainer cannot be vertically content-sized");
      }
   }

   @Override
   public void layout(Size space) {
      super.layout(space);
      this.maxScroll = Math.max(
         0, this.direction.sizeGetter.apply(this.child) - (this.direction.sizeGetter.apply(this) - this.direction.insetGetter.apply(this.padding.get()))
      );
      this.scrollOffset = Mth.clamp(this.scrollOffset, 0.0, this.maxScroll + 0.5);
      this.childSize = this.direction.sizeGetter.apply(this.child);
      this.lastScrollPosition = -1;
   }

   @Override
   protected int childMountX() {
      return (int)(super.childMountX() - this.direction.choose(this.currentScrollPosition, 0.0));
   }

   @Override
   protected int childMountY() {
      return (int)(super.childMountY() - this.direction.choose(0.0, this.currentScrollPosition));
   }

   @Override
   protected void parentUpdate(float delta, int mouseX, int mouseY) {
      super.parentUpdate(delta, mouseX, mouseY);
      this.currentScrollPosition = this.currentScrollPosition + Delta.compute(this.currentScrollPosition, this.scrollOffset, delta * 0.5);
   }

   @Override
   public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
      super.draw(context, mouseX, mouseY, partialTicks, delta);
      int effectiveScrollOffset = this.scrollStep > 0 ? (int)this.scrollOffset / this.scrollStep * this.scrollStep : (int)this.currentScrollPosition;
      if (this.scrollStep > 0 && this.maxScroll - this.scrollOffset == -1.0) {
         effectiveScrollOffset = (int)(effectiveScrollOffset + this.scrollOffset % this.scrollStep);
      }

      int newScrollPosition = this.direction.coordinateGetter.apply(this) - effectiveScrollOffset;
      if (newScrollPosition != this.lastScrollPosition) {
         this.direction
            .coordinateSetter
            .accept(
               this.child,
               newScrollPosition
                  + (
                     this.direction == ScrollContainer.ScrollDirection.VERTICAL
                        ? this.padding.get().top() + this.child.margins().get().top()
                        : this.padding.get().left() + this.child.margins().get().left()
                  )
            );
         this.lastScrollPosition = newScrollPosition;
      }

      context.pose().pushPose();
      double visualOffset = -(this.currentScrollPosition % 1.0);
      if (visualOffset > 0.9999999 || visualOffset < 1.0E-7) {
         visualOffset = 0.0;
      }

      context.pose().translate(this.direction.choose(visualOffset, 0.0), this.direction.choose(0.0, visualOffset), 0.0);
      this.drawChildren(context, mouseX, mouseY, partialTicks, delta, this.childView);
      context.pose().popPose();
      if (this.isInScrollbar(mouseX, mouseY) || this.scrollbaring) {
         this.lastScrollbarInteractTime = System.currentTimeMillis() + 1500L;
      }

      Insets padding = this.padding.get();
      int selfSize = this.direction.sizeGetter.apply(this);
      int contentSize = this.direction.sizeGetter.apply(this) - this.direction.insetGetter.apply(padding);
      this.scrollbarOffset = this.direction == ScrollContainer.ScrollDirection.VERTICAL
         ? this.x + this.width - padding.right() - this.scrollbarThiccness
         : this.y + this.height - padding.bottom() - this.scrollbarThiccness;
      this.lastScrollbarLength = this.fixedScrollbarLength == 0
         ? Math.min(Math.floor((float)selfSize / this.childSize * contentSize), (double)contentSize)
         : this.fixedScrollbarLength;
      double scrollbarPosition = this.maxScroll != 0 ? this.currentScrollPosition / this.maxScroll * (contentSize - this.lastScrollbarLength) : 0.0;
      if (this.direction == ScrollContainer.ScrollDirection.VERTICAL) {
         this.scrollbar
            .draw(
               context,
               this.scrollbarOffset,
               (int)(this.y + scrollbarPosition + padding.top()),
               this.scrollbarThiccness,
               (int)this.lastScrollbarLength,
               this.scrollbarOffset,
               this.y + padding.top(),
               this.scrollbarThiccness,
               this.height - padding.vertical(),
               this.lastScrollbarInteractTime,
               this.direction,
               this.maxScroll > 0
            );
      } else {
         this.scrollbar
            .draw(
               context,
               (int)(this.x + scrollbarPosition + padding.left()),
               this.scrollbarOffset,
               (int)this.lastScrollbarLength,
               this.scrollbarThiccness,
               this.x + padding.left(),
               this.scrollbarOffset,
               this.width - padding.horizontal(),
               this.scrollbarThiccness,
               this.lastScrollbarInteractTime,
               this.direction,
               this.maxScroll > 0
            );
      }
   }

   @Override
   public boolean canFocus(Component.FocusSource source) {
      return true;
   }

   @Override
   public boolean onMouseScroll(double mouseX, double mouseY, double amount) {
      if (this.child.onMouseScroll(this.x + mouseX - this.child.x(), this.y + mouseY - this.child.y(), amount)) {
         return true;
      } else {
         if (this.scrollStep < 1) {
            this.scrollBy(-amount * 15.0, false, true);
         } else {
            this.scrollBy(-amount * this.scrollStep, true, true);
         }

         return true;
      }
   }

   @Override
   public boolean onMouseDown(double mouseX, double mouseY, int button) {
      if (this.isInScrollbar(this.x + mouseX, this.y + mouseY)) {
         super.onMouseDown(mouseX, mouseY, button);
         return true;
      } else {
         return super.onMouseDown(mouseX, mouseY, button);
      }
   }

   @Override
   public boolean onMouseDrag(double mouseX, double mouseY, double deltaX, double deltaY, int button) {
      if (!this.scrollbaring && !this.isInScrollbar(this.x + mouseX, this.y + mouseY)) {
         return super.onMouseDrag(mouseX, mouseY, deltaX, deltaY, button);
      } else {
         double delta = this.direction.choose(deltaX, deltaY);
         double selfSize = this.direction.sizeGetter.apply(this) - this.direction.insetGetter.apply(this.padding.get());
         double scalar = this.maxScroll / (selfSize - this.lastScrollbarLength);
         if (!Double.isFinite(scalar)) {
            scalar = 0.0;
         }

         this.scrollBy(delta * scalar, true, false);
         this.scrollbaring = true;
         return true;
      }
   }

   @Override
   public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
      if (keyCode == this.direction.lessKeycode) {
         this.scrollBy(-10.0, false, true);
      } else if (keyCode == this.direction.moreKeycode) {
         this.scrollBy(10.0, false, true);
      } else if (keyCode == 267) {
         this.scrollBy(this.direction.choose(this.width, this.height) * 0.8, false, true);
         this.lastScrollbarInteractTime = System.currentTimeMillis() + 1250L;
      } else if (keyCode == 266) {
         this.scrollBy(this.direction.choose(this.width, this.height) * -0.8, false, true);
      }

      return false;
   }

   @Override
   public boolean onMouseUp(double mouseX, double mouseY, int button) {
      this.scrollbaring = false;
      return true;
   }

   @Nullable
   @Override
   public Component childAt(int x, int y) {
      return (Component)(this.isInScrollbar(x, y) ? this : super.childAt(x, y));
   }

   protected void scrollBy(double offset, boolean instant, boolean showScrollbar) {
      this.scrollOffset = Mth.clamp(this.scrollOffset + offset, 0.0, this.maxScroll + 0.5);
      if (instant) {
         this.currentScrollPosition = this.scrollOffset;
      }

      if (showScrollbar) {
         this.lastScrollbarInteractTime = System.currentTimeMillis() + 1250L;
      }
   }

   protected boolean isInScrollbar(double mouseX, double mouseY) {
      return this.isInBoundingBox(mouseX, mouseY) && this.direction.choose(mouseY, mouseX) >= this.scrollbarOffset;
   }

   public ScrollContainer<C> scrollTo(Component component) {
      if (this.direction == ScrollContainer.ScrollDirection.VERTICAL) {
         this.scrollOffset = Mth.clamp(this.scrollOffset - (this.y - component.y() + component.margins().get().top()), 0.0, this.maxScroll);
      } else {
         this.scrollOffset = Mth.clamp(this.scrollOffset - (this.x - component.x() + component.margins().get().right()), 0.0, this.maxScroll);
      }

      return this;
   }

   public ScrollContainer<C> scrollTo(@Range(from = 0L,to = 1L) double progress) {
      this.scrollOffset = this.maxScroll * progress;
      return this;
   }

   public ScrollContainer<C> scrollbarThiccness(int scrollbarThiccness) {
      this.scrollbarThiccness = scrollbarThiccness;
      return this;
   }

   public int scrollbarThiccness() {
      return this.scrollbarThiccness;
   }

   public ScrollContainer<C> scrollbar(ScrollContainer.Scrollbar scrollbar) {
      this.scrollbar = scrollbar;
      return this;
   }

   public ScrollContainer.Scrollbar scrollbar() {
      return this.scrollbar;
   }

   public ScrollContainer<C> scrollStep(int scrollStep) {
      this.scrollStep = scrollStep;
      return this;
   }

   public int scrollStep() {
      return this.scrollStep;
   }

   public ScrollContainer<C> fixedScrollbarLength(int fixedScrollbarLength) {
      this.fixedScrollbarLength = fixedScrollbarLength;
      return this;
   }

   public int fixedScrollbarLength() {
      return this.fixedScrollbarLength;
   }

   @Override
   public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
      super.parseProperties(model, element, children);
      UIParsing.apply(children, "fixed-scrollbar-length", UIParsing::parseUnsignedInt, this::fixedScrollbarLength);
      UIParsing.apply(children, "scrollbar-thiccness", UIParsing::parseUnsignedInt, this::scrollbarThiccness);
      UIParsing.apply(children, "scrollbar", ScrollContainer.Scrollbar::parse, this::scrollbar);
      UIParsing.apply(children, "scroll-step", UIParsing::parseUnsignedInt, this::scrollStep);
   }

   public static ScrollContainer<?> parse(Element element) {
      return element.getAttribute("direction").equals("vertical")
         ? Containers.verticalScroll(Sizing.content(), Sizing.content(), null)
         : Containers.horizontalScroll(Sizing.content(), Sizing.content(), null);
   }

   public static enum ScrollDirection {
      VERTICAL(Component::height, Component::updateY, Component::y, Insets::vertical, 265, 264),
      HORIZONTAL(Component::width, Component::updateX, Component::x, Insets::horizontal, 263, 262);

      public final Function<Component, Integer> sizeGetter;
      public final BiConsumer<Component, Integer> coordinateSetter;
      public final Function<ScrollContainer<?>, Integer> coordinateGetter;
      public final Function<Insets, Integer> insetGetter;
      public final int lessKeycode;
      public final int moreKeycode;

      private ScrollDirection(
         Function<Component, Integer> sizeGetter,
         BiConsumer<Component, Integer> coordinateSetter,
         Function<ScrollContainer<?>, Integer> coordinateGetter,
         Function<Insets, Integer> insetGetter,
         int lessKeycode,
         int moreKeycode
      ) {
         this.sizeGetter = sizeGetter;
         this.coordinateSetter = coordinateSetter;
         this.coordinateGetter = coordinateGetter;
         this.insetGetter = insetGetter;
         this.lessKeycode = lessKeycode;
         this.moreKeycode = moreKeycode;
      }

      public double choose(double horizontal, double vertical) {
         return switch (this) {
            case VERTICAL -> vertical;
            case HORIZONTAL -> horizontal;
         };
      }
   }

   @FunctionalInterface
   public interface Scrollbar {
      static ScrollContainer.Scrollbar flat(Color color) {
         int scrollbarColor = color.argb();
         return (context, x, y, width, height, trackX, trackY, trackWidth, trackHeight, lastInteractTime, direction, active) -> {
            if (active) {
               float progress = Easing.SINE.apply((float)Mth.clamp(lastInteractTime - System.currentTimeMillis(), 0L, 750L) / 750.0F);
               int alpha = (int)(progress * (scrollbarColor >>> 24));
               context.fill(x, y, x + width, y + height, alpha << 24 | scrollbarColor & 16777215);
            }
         };
      }

      static ScrollContainer.Scrollbar vanilla() {
         return (context, x, y, width, height, trackX, trackY, trackWidth, trackHeight, lastInteractTime, direction, active) -> {
            NinePatchTexture.draw(ScrollContainer.VANILLA_SCROLLBAR_TRACK_TEXTURE, context, trackX, trackY, trackWidth, trackHeight);
            ResourceLocation texture = direction == ScrollContainer.ScrollDirection.VERTICAL
               ? (active ? ScrollContainer.VERTICAL_VANILLA_SCROLLBAR_TEXTURE : ScrollContainer.DISABLED_VERTICAL_VANILLA_SCROLLBAR_TEXTURE)
               : (active ? ScrollContainer.HORIZONTAL_VANILLA_SCROLLBAR_TEXTURE : ScrollContainer.DISABLED_HORIZONTAL_VANILLA_SCROLLBAR_TEXTURE);
            NinePatchTexture.draw(texture, context, x + 1, y + 1, width - 2, height - 2);
         };
      }

      static ScrollContainer.Scrollbar vanillaFlat() {
         return (context, x, y, width, height, trackX, trackY, trackWidth, trackHeight, lastInteractTime, direction, active) -> {
            context.fill(trackX, trackY, trackX + trackWidth, trackY + trackHeight, Color.BLACK.argb());
            NinePatchTexture.draw(ScrollContainer.FLAT_VANILLA_SCROLLBAR_TEXTURE, context, x, y, width, height);
         };
      }

      void draw(
         OwoUIDrawContext var1,
         int var2,
         int var3,
         int var4,
         int var5,
         int var6,
         int var7,
         int var8,
         int var9,
         long var10,
         ScrollContainer.ScrollDirection var12,
         boolean var13
      );

      static ScrollContainer.Scrollbar parse(Element element) {
         List<Element> children = UIParsing.allChildrenOfType(element, (short)1);
         if (children.size() > 1) {
            throw new UIModelParsingException("'scrollbar' declaration may only contain a single child");
         } else {
            Element scrollbarElement = children.get(0);
            String var3 = scrollbarElement.getNodeName();

            return switch (var3) {
               case "vanilla" -> vanilla();
               case "vanilla-flat" -> vanillaFlat();
               case "flat" -> flat(Color.parse(scrollbarElement));
               default -> throw new UIModelParsingException("Unknown scrollbar type '" + scrollbarElement.getNodeName() + "'");
            };
         }
      }
   }
}
