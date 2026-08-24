package io.wispforest.owo.ui.container;

import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.ParentComponent;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import io.wispforest.owo.util.EventSource;
import org.jetbrains.annotations.Nullable;

public class OverlayContainer<C extends Component> extends WrappingParentComponent<C> {
   protected boolean closeOnClick = true;
   @Nullable
   protected EventSource<?>.Subscription exitSubscription = null;

   protected OverlayContainer(C child) {
      super(Sizing.fill(100), Sizing.fill(100), child);
      this.positioning(Positioning.absolute(0, 0));
      this.surface(Surface.VANILLA_TRANSLUCENT);
   }

   @Override
   public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
      super.draw(context, mouseX, mouseY, partialTicks, delta);
      this.drawChildren(context, mouseX, mouseY, partialTicks, delta, this.childView);
   }

   @Override
   public void drawFocusHighlight(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
   }

   @Override
   public void mount(ParentComponent parent, int x, int y) {
      super.mount(parent, x, y);
      this.exitSubscription = this.root().keyPress().subscribe((keyCode, scanCode, modifiers) -> {
         if (keyCode == 256) {
            this.remove();
            return true;
         } else {
            return false;
         }
      });
   }

   @Override
   public void dismount(Component.DismountReason reason) {
      super.dismount(reason);
      if (this.exitSubscription != null) {
         this.exitSubscription.cancel();
      }
   }

   @Override
   public boolean onMouseDown(double mouseX, double mouseY, int button) {
      boolean handled = super.onMouseDown(mouseX, mouseY, button) || this.child.isInBoundingBox(mouseX, mouseY);
      if (!handled && this.closeOnClick) {
         this.remove();
         return true;
      } else {
         return handled;
      }
   }

   @Override
   public boolean onMouseScroll(double mouseX, double mouseY, double amount) {
      super.onMouseScroll(mouseX, mouseY, amount);
      return true;
   }

   @Override
   public boolean canFocus(Component.FocusSource source) {
      return source == Component.FocusSource.KEYBOARD_CYCLE;
   }

   @Override
   protected int childMountX() {
      return this.x + this.padding.get().left() + (this.width - this.child.fullSize().width()) / 2;
   }

   @Override
   protected int childMountY() {
      return this.y + this.padding.get().top() + (this.height() - this.child.fullSize().height()) / 2;
   }

   public OverlayContainer<C> closeOnClick(boolean closeOnClick) {
      this.closeOnClick = closeOnClick;
      return this;
   }

   public boolean closeOnClick() {
      return this.closeOnClick;
   }
}
