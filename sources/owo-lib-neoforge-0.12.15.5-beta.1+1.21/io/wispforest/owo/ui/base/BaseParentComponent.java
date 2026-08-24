package io.wispforest.owo.ui.base;

import io.wispforest.owo.ui.core.AnimatableProperty;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.ParentComponent;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Size;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import io.wispforest.owo.ui.core.VerticalAlignment;
import io.wispforest.owo.ui.util.FocusHandler;
import io.wispforest.owo.ui.util.ScissorStack;
import io.wispforest.owo.util.Observable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public abstract class BaseParentComponent extends BaseComponent implements ParentComponent {
   protected final Observable<VerticalAlignment> verticalAlignment = Observable.of(VerticalAlignment.TOP);
   protected final Observable<HorizontalAlignment> horizontalAlignment = Observable.of(HorizontalAlignment.LEFT);
   protected final AnimatableProperty<Insets> padding = AnimatableProperty.of(Insets.none());
   @Nullable
   protected FocusHandler focusHandler = null;
   @Nullable
   protected ArrayList<Runnable> taskQueue = null;
   protected Surface surface = Surface.BLANK;
   protected boolean allowOverflow = false;

   protected BaseParentComponent(Sizing horizontalSizing, Sizing verticalSizing) {
      this.horizontalSizing.set(horizontalSizing);
      this.verticalSizing.set(verticalSizing);
      Observable.observeAll(this::updateLayout, this.horizontalAlignment, this.verticalAlignment, this.padding);
   }

   @Override
   public final void update(float delta, int mouseX, int mouseY) {
      ParentComponent.super.update(delta, mouseX, mouseY);
      super.update(delta, mouseX, mouseY);
      this.parentUpdate(delta, mouseX, mouseY);
      if (this.taskQueue != null) {
         this.taskQueue.forEach(Runnable::run);
         this.taskQueue.clear();
      }
   }

   protected void parentUpdate(float delta, int mouseX, int mouseY) {
   }

   @Override
   public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
      this.surface.draw(context, this);
   }

   @Override
   public void queue(Runnable task) {
      if (this.taskQueue == null) {
         this.parent.queue(task);
      } else {
         this.taskQueue.add(task);
      }
   }

   @Nullable
   @Override
   public FocusHandler focusHandler() {
      return this.focusHandler == null ? super.focusHandler() : this.focusHandler;
   }

   @Override
   public ParentComponent verticalAlignment(VerticalAlignment alignment) {
      this.verticalAlignment.set(alignment);
      return this;
   }

   @Override
   public VerticalAlignment verticalAlignment() {
      return this.verticalAlignment.get();
   }

   @Override
   public ParentComponent horizontalAlignment(HorizontalAlignment alignment) {
      this.horizontalAlignment.set(alignment);
      return this;
   }

   @Override
   public HorizontalAlignment horizontalAlignment() {
      return this.horizontalAlignment.get();
   }

   @Override
   public ParentComponent padding(Insets padding) {
      this.padding.set(padding);
      this.updateLayout();
      return this;
   }

   @Override
   public AnimatableProperty<Insets> padding() {
      return this.padding;
   }

   @Override
   public ParentComponent allowOverflow(boolean allowOverflow) {
      this.allowOverflow = allowOverflow;
      return this;
   }

   @Override
   public boolean allowOverflow() {
      return this.allowOverflow;
   }

   @Override
   public ParentComponent surface(Surface surface) {
      this.surface = surface;
      return this;
   }

   @Override
   public Surface surface() {
      return this.surface;
   }

   @Override
   public void mount(ParentComponent parent, int x, int y) {
      super.mount(parent, x, y);
      if (parent == null && this.focusHandler == null) {
         this.focusHandler = new FocusHandler(this);
         this.taskQueue = new ArrayList<>();
      }
   }

   @Override
   public void inflate(Size space) {
      if (!this.space.equals(space) || this.dirty) {
         this.space = space;

         for (Component child : this.children()) {
            child.dismount(Component.DismountReason.LAYOUT_INFLATION);
         }

         super.inflate(space);
         this.layout(space);
         super.inflate(space);
      }
   }

   protected void updateLayout() {
      if (this.mounted) {
         if (this.batchedEvents > 0) {
            this.batchedEvents++;
         } else {
            Size previousSize = this.fullSize();
            this.dirty = true;
            this.inflate(this.space);
            if (!previousSize.equals(this.fullSize()) && this.parent != null) {
               this.parent.onChildMutated(this);
            }
         }
      }
   }

   @Override
   protected void runAndDeferEvents(Runnable action) {
      try {
         this.batchedEvents = 1;
         action.run();
      } finally {
         if (this.batchedEvents > 1) {
            this.batchedEvents = 0;
            this.updateLayout();
         } else {
            this.batchedEvents = 0;
         }
      }
   }

   @Override
   public void onChildMutated(Component child) {
      this.updateLayout();
   }

   @Override
   public boolean onMouseDown(double mouseX, double mouseY, int button) {
      if (this.focusHandler != null) {
         this.focusHandler.updateClickFocus(this.x + mouseX, this.y + mouseY);
      }

      return ParentComponent.super.onMouseDown(mouseX, mouseY, button) || super.onMouseDown(mouseX, mouseY, button);
   }

   @Override
   public boolean onMouseUp(double mouseX, double mouseY, int button) {
      if (this.focusHandler != null && this.focusHandler.focused() != null) {
         Component focused = this.focusHandler.focused();
         return focused.onMouseUp(this.x + mouseX - focused.x(), this.y + mouseY - focused.y(), button);
      } else {
         return super.onMouseUp(mouseX, mouseY, button);
      }
   }

   @Override
   public boolean onMouseScroll(double mouseX, double mouseY, double amount) {
      return ParentComponent.super.onMouseScroll(mouseX, mouseY, amount) || super.onMouseScroll(mouseX, mouseY, amount);
   }

   @Override
   public boolean onMouseDrag(double mouseX, double mouseY, double deltaX, double deltaY, int button) {
      if (this.focusHandler != null && this.focusHandler.focused() != null) {
         Component focused = this.focusHandler.focused();
         return focused.onMouseDrag(this.x + mouseX - focused.x(), this.y + mouseY - focused.y(), deltaX, deltaY, button);
      } else {
         return super.onMouseDrag(mouseX, mouseY, deltaX, deltaY, button);
      }
   }

   @Override
   public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
      if (this.focusHandler == null) {
         return false;
      } else {
         if (keyCode == 258) {
            this.focusHandler.cycle((modifiers & 1) == 0);
         } else if ((keyCode == 262 || keyCode == 263 || keyCode == 264 || keyCode == 265) && (modifiers & 4) != 0) {
            this.focusHandler.moveFocus(keyCode);
         } else if (this.focusHandler.focused() != null) {
            return this.focusHandler.focused().onKeyPress(keyCode, scanCode, modifiers);
         }

         return super.onKeyPress(keyCode, scanCode, modifiers);
      }
   }

   @Override
   public boolean onCharTyped(char chr, int modifiers) {
      if (this.focusHandler == null) {
         return false;
      } else {
         return this.focusHandler.focused() != null ? this.focusHandler.focused().onCharTyped(chr, modifiers) : super.onCharTyped(chr, modifiers);
      }
   }

   @Override
   public void updateX(int x) {
      int offset = x - this.x;
      super.updateX(x);

      for (Component child : this.children()) {
         child.updateX(child.baseX() + offset);
      }
   }

   @Override
   public void updateY(int y) {
      int offset = y - this.y;
      super.updateY(y);

      for (Component child : this.children()) {
         child.updateY(child.baseY() + offset);
      }
   }

   protected Size childMountingOffset() {
      Insets padding = this.padding.get();
      return Size.of(padding.left(), padding.top());
   }

   protected void mountChild(@Nullable Component child, Consumer<Component> layoutFunc) {
      if (child != null) {
         Positioning positioning = child.positioning().get();
         Insets componentMargins = child.margins().get();
         Insets padding = this.padding.get();
         switch (positioning.type) {
            case LAYOUT:
               layoutFunc.accept(child);
               break;
            case ABSOLUTE:
               child.mount(
                  this, this.x + positioning.x + componentMargins.left() + padding.left(), this.y + positioning.y + componentMargins.top() + padding.top()
               );
               break;
            case RELATIVE:
               child.mount(
                  this,
                  this.x
                     + padding.left()
                     + componentMargins.left()
                     + Math.round(positioning.x / 100.0F * (this.width() - child.fullSize().width() - padding.horizontal())),
                  this.y
                     + padding.top()
                     + componentMargins.top()
                     + Math.round(positioning.y / 100.0F * (this.height() - child.fullSize().height() - padding.vertical()))
               );
               break;
            case ACROSS:
               child.mount(
                  this,
                  this.x + padding.left() + componentMargins.left() + Math.round(positioning.x / 100.0F * (this.width() - padding.horizontal())),
                  this.y + padding.top() + componentMargins.top() + Math.round(positioning.y / 100.0F * (this.height() - padding.vertical()))
               );
         }
      }
   }

   protected void drawChildren(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta, List<? extends Component> children) {
      if (!this.allowOverflow) {
         Insets padding = this.padding.get();
         ScissorStack.push(this.x + padding.left(), this.y + padding.top(), this.width - padding.horizontal(), this.height - padding.vertical(), context.pose());
      }

      FocusHandler focusHandler = this.focusHandler();

      for (int i = 0; i < children.size(); i++) {
         Component child = children.get(i);
         if (ScissorStack.isVisible(child, context.pose())) {
            context.pose().translate(0.0F, 0.0F, child.zIndex() + 1);
            child.draw(context, mouseX, mouseY, partialTicks, delta);
            if (focusHandler.lastFocusSource() == Component.FocusSource.KEYBOARD_CYCLE && focusHandler.focused() == child) {
               child.drawFocusHighlight(context, mouseX, mouseY, partialTicks, delta);
            }

            context.pose().translate(0.0F, 0.0F, -child.zIndex() - 1);
         }
      }

      if (!this.allowOverflow) {
         ScissorStack.pop();
      }
   }

   protected Size calculateChildSpace(Size thisSpace) {
      Insets padding = this.padding.get();
      return Size.of(
         Mth.lerpInt(this.horizontalSizing.get().contentFactor(), this.width - padding.horizontal(), thisSpace.width() - padding.horizontal()),
         Mth.lerpInt(this.verticalSizing.get().contentFactor(), this.height - padding.vertical(), thisSpace.height() - padding.vertical())
      );
   }

   public BaseParentComponent positioning(Positioning positioning) {
      return (BaseParentComponent)super.positioning(positioning);
   }

   public BaseParentComponent margins(Insets margins) {
      return (BaseParentComponent)super.margins(margins);
   }
}
