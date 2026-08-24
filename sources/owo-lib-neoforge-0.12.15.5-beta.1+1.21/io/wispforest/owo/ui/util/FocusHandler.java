package io.wispforest.owo.ui.util;

import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.ParentComponent;
import java.util.ArrayList;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public class FocusHandler {
   protected final ParentComponent root;
   @Nullable
   protected Component focused = null;
   @Nullable
   protected Component.FocusSource lastFocusSource = null;

   public FocusHandler(ParentComponent root) {
      this.root = root;
   }

   public void updateClickFocus(double mouseX, double mouseY) {
      Component clicked = this.root.childAt((int)mouseX, (int)mouseY);
      this.focus(clicked != null && clicked.canFocus(Component.FocusSource.MOUSE_CLICK) ? clicked : null, Component.FocusSource.MOUSE_CLICK);
   }

   @Contract(
      pure = true
   )
   @Nullable
   public Component focused() {
      return this.focused;
   }

   public Component.FocusSource lastFocusSource() {
      return this.lastFocusSource;
   }

   public void cycle(boolean forwards) {
      ArrayList<Component> allChildren = new ArrayList<>();
      this.root.collectDescendants(allChildren);
      allChildren.removeIf(component -> !component.canFocus(Component.FocusSource.KEYBOARD_CYCLE));
      if (!allChildren.isEmpty()) {
         int newIndex = this.focused == null ? (forwards ? 0 : allChildren.size() - 1) : allChildren.indexOf(this.focused) + (forwards ? 1 : -1);
         if (newIndex >= allChildren.size()) {
            newIndex -= allChildren.size();
         }

         if (newIndex < 0) {
            newIndex += allChildren.size();
         }

         this.focus(allChildren.get(newIndex), Component.FocusSource.KEYBOARD_CYCLE);
      }
   }

   public void moveFocus(int keyCode) {
      if (this.focused != null) {
         ArrayList<Component> allChildren = new ArrayList<>();
         this.root.collectDescendants(allChildren);
         allChildren.removeIf(component -> !component.canFocus(Component.FocusSource.KEYBOARD_CYCLE));
         if (!allChildren.isEmpty()) {
            Component closest = this.focused;
            switch (keyCode) {
               case 262:
                  int closestX = 2147483647;
                  int closestY = 2147483647;

                  for (Component childxxx : allChildren) {
                     if (childxxx != this.focused
                        && childxxx.x() >= this.focused.x() + this.focused.width()
                        && childxxx.x() <= closestX
                        && Math.abs(childxxx.y() - this.focused.y()) <= closestY) {
                        closest = childxxx;
                        closestX = childxxx.x();
                        closestY = Math.abs(childxxx.y() - this.focused.y());
                     }
                  }
                  break;
               case 263:
                  int closestX = 0;
                  int closestY = 2147483647;

                  for (Component childxx : allChildren) {
                     if (childxx != this.focused
                        && childxx.x() + childxx.width() <= this.focused.x()
                        && childxx.x() + childxx.width() >= closestX
                        && Math.abs(childxx.y() - this.focused.y()) <= closestY) {
                        closest = childxx;
                        closestX = childxx.x() + childxx.width();
                        closestY = Math.abs(childxx.y() - this.focused.y());
                     }
                  }
                  break;
               case 264:
                  int closestX = 2147483647;
                  int closestY = 2147483647;

                  for (Component child : allChildren) {
                     if (child != this.focused
                        && child.y() >= this.focused.y() + this.focused.height()
                        && child.y() + child.height() <= closestY
                        && Math.abs(child.x() - this.focused.x()) <= closestX) {
                        closest = child;
                        closestX = Math.abs(child.x() - this.focused.x());
                        closestY = child.y() + child.height();
                     }
                  }
                  break;
               case 265:
                  int closestX = 2147483647;
                  int closestY = 0;

                  for (Component childx : allChildren) {
                     if (childx != this.focused
                        && childx.y() + childx.height() <= this.focused.y()
                        && childx.y() + childx.height() >= closestY
                        && Math.abs(childx.x() - this.focused.x()) <= closestX) {
                        closest = childx;
                        closestX = Math.abs(childx.x() - this.focused.x());
                        closestY = childx.y() + childx.height();
                     }
                  }
            }

            this.focus(closest, Component.FocusSource.KEYBOARD_CYCLE);
         }
      }
   }

   public void focus(@Nullable Component component, Component.FocusSource source) {
      if (this.focused != component) {
         if (this.focused != null) {
            this.focused.onFocusLost();
         }

         if ((this.focused = component) != null) {
            this.focused.onFocusGained(source);
            this.lastFocusSource = source;
         } else {
            this.lastFocusSource = null;
         }
      }
   }
}
