package me.shedaniel.clothconfig2.gui.widget;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarratableEntry.NarrationPriority;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.navigation.FocusNavigationEvent.ArrowNavigation;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.Screen.NarratableSearchResult;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public abstract class DynamicElementListWidget<E extends DynamicElementListWidget.ElementEntry<E>> extends DynamicSmoothScrollingEntryListWidget<E> {
   private static final Component USAGE_NARRATION = Component.translatable("narration.selection.usage");

   public DynamicElementListWidget(Minecraft client, int width, int height, int top, int bottom, ResourceLocation backgroundLocation) {
      super(client, width, height, top, bottom, backgroundLocation);
   }

   @Nullable
   public ComponentPath nextFocusPath(FocusNavigationEvent focusNavigationEvent) {
      if (this.getItemCount() == 0) {
         return null;
      } else if (!(focusNavigationEvent instanceof ArrowNavigation arrowNavigation)) {
         return super.nextFocusPath(focusNavigationEvent);
      } else {
         DynamicElementListWidget.ElementEntry entry = this.getFocused();
         if (arrowNavigation.direction().getAxis() == ScreenAxis.HORIZONTAL && entry != null) {
            return ComponentPath.path(this, entry.nextFocusPath(focusNavigationEvent));
         } else {
            int i = -1;
            ScreenDirection screenDirection = arrowNavigation.direction();
            if (entry != null) {
               i = entry.children().indexOf(entry.getFocused());
            }

            if (i == -1) {
               switch (screenDirection) {
                  case LEFT:
                     i = 2147483647;
                     screenDirection = ScreenDirection.DOWN;
                     break;
                  case RIGHT:
                     i = 0;
                     screenDirection = ScreenDirection.DOWN;
                     break;
                  default:
                     i = 0;
               }
            }

            E entry2 = (E)entry;

            ComponentPath componentPath;
            do {
               entry2 = this.nextEntry(screenDirection, entryx -> !entryx.children().isEmpty(), entry2);
               if (entry2 == null) {
                  return null;
               }

               componentPath = entry2.focusPathAtIndex(arrowNavigation, i);
            } while (componentPath == null);

            return ComponentPath.path(this, componentPath);
         }
      }
   }

   @Override
   public void updateNarration(NarrationElementOutput narrationElementOutput) {
      E entry = this.hoveredItem;
      if (entry != null) {
         entry.updateNarration(narrationElementOutput.nest());
         this.narrateListElementPosition(narrationElementOutput, entry);
      } else {
         E entry2 = this.getFocused();
         if (entry2 != null) {
            entry2.updateNarration(narrationElementOutput.nest());
            this.narrateListElementPosition(narrationElementOutput, entry2);
         }
      }

      narrationElementOutput.add(NarratedElementType.USAGE, Component.translatable("narration.component_list.usage"));
   }

   @Override
   public void setFocused(@Nullable GuiEventListener guiEventListener) {
      super.setFocused(guiEventListener);
      if (guiEventListener == null) {
         this.selectItem(null);
      }
   }

   @Override
   public NarrationPriority narrationPriority() {
      return this.isFocused() ? NarrationPriority.FOCUSED : super.narrationPriority();
   }

   @Override
   protected boolean isSelected(int i) {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public abstract static class ElementEntry<E extends DynamicElementListWidget.ElementEntry<E>>
      extends DynamicEntryListWidget.Entry<E>
      implements ContainerEventHandler,
      NarratableEntry {
      @Nullable
      private GuiEventListener focused;
      @Nullable
      private NarratableEntry lastNarratable;
      private boolean dragging;

      public boolean isDragging() {
         return this.dragging;
      }

      public void setDragging(boolean bl) {
         this.dragging = bl;
      }

      @Nullable
      public GuiEventListener getFocused() {
         return this.focused;
      }

      public void setFocused(@Nullable GuiEventListener guiEventListener) {
         if (this.focused != null) {
            this.focused.setFocused(false);
         }

         if (guiEventListener != null) {
            guiEventListener.setFocused(true);
         }

         this.focused = guiEventListener;
      }

      @Nullable
      public ComponentPath focusPathAtIndex(FocusNavigationEvent focusNavigationEvent, int i) {
         if (this.children().isEmpty()) {
            return null;
         } else {
            ComponentPath componentPath = ((GuiEventListener)this.children().get(Math.min(i, this.children().size() - 1))).nextFocusPath(focusNavigationEvent);
            return ComponentPath.path(this, componentPath);
         }
      }

      @Nullable
      public ComponentPath nextFocusPath(FocusNavigationEvent focusNavigationEvent) {
         if (focusNavigationEvent instanceof ArrowNavigation arrowNavigation) {
            int var10000 = switch (arrowNavigation.direction()) {
               case LEFT -> -1;
               case RIGHT -> 1;
               case UP, DOWN -> 0;
               default -> throw new MatchException(null, null);
            };
            if (var10000 == 0) {
               return null;
            }

            int j = Mth.clamp(var10000 + this.children().indexOf(this.getFocused()), 0, this.children().size() - 1);

            for (int k = j; k >= 0 && k < this.children().size(); k += var10000) {
               GuiEventListener guiEventListener = (GuiEventListener)this.children().get(k);
               ComponentPath componentPath = guiEventListener.nextFocusPath(focusNavigationEvent);
               if (componentPath != null) {
                  return ComponentPath.path(this, componentPath);
               }
            }
         }

         return super.nextFocusPath(focusNavigationEvent);
      }

      @Override
      public abstract List<? extends NarratableEntry> narratables();

      @Override
      public void updateNarration(NarrationElementOutput narrationElementOutput) {
         List<? extends NarratableEntry> list = this.narratables();
         NarratableSearchResult narratableSearchResult = Screen.findNarratableWidget(list, this.lastNarratable);
         if (narratableSearchResult != null) {
            if (narratableSearchResult.priority.isTerminal()) {
               this.lastNarratable = narratableSearchResult.entry;
            }

            if (list.size() > 1) {
               narrationElementOutput.add(
                  NarratedElementType.POSITION,
                  Component.translatable("narrator.position.object_list", new Object[]{narratableSearchResult.index + 1, list.size()})
               );
               if (narratableSearchResult.priority == NarrationPriority.FOCUSED) {
                  narrationElementOutput.add(NarratedElementType.USAGE, Component.translatable("narration.component_list.usage"));
               }
            }

            narratableSearchResult.entry.updateNarration(narrationElementOutput.nest());
         }
      }

      public boolean isActive() {
         return false;
      }

      public NarrationPriority narrationPriority() {
         return this.isFocused() ? NarrationPriority.FOCUSED : NarrationPriority.NONE;
      }

      public boolean mouseClicked(double d, double e, int i) {
         return !this.isEnabled() ? false : super.mouseClicked(d, e, i);
      }

      public boolean mouseReleased(double d, double e, int i) {
         return !this.isEnabled() ? false : super.mouseReleased(d, e, i);
      }

      public boolean mouseDragged(double d, double e, int i, double f, double g) {
         return !this.isEnabled() ? false : super.mouseDragged(d, e, i, f, g);
      }

      public boolean mouseScrolled(double d, double e, double amountX, double amountY) {
         return !this.isEnabled() ? false : super.mouseScrolled(d, e, amountX, amountY);
      }

      public boolean keyPressed(int i, int j, int k) {
         return !this.isEnabled() ? false : super.keyPressed(i, j, k);
      }

      public boolean keyReleased(int i, int j, int k) {
         return !this.isEnabled() ? false : super.keyReleased(i, j, k);
      }

      public boolean charTyped(char c, int i) {
         return !this.isEnabled() ? false : super.charTyped(c, i);
      }
   }
}
