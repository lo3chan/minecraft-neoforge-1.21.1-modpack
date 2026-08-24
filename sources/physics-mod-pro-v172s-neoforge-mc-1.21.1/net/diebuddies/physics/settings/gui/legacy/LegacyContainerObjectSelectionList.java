package net.diebuddies.physics.settings.gui.legacy;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarratableEntry.NarrationPriority;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.Screen.NarratableSearchResult;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public abstract class LegacyContainerObjectSelectionList<E extends LegacyAbstractSelectionList.LegacyEntry<E>> extends LegacyAbstractSelectionList<E> {
   private boolean hasFocus;

   public LegacyContainerObjectSelectionList(Minecraft minecraft, int i, int j, int k, int l, int m) {
      super(minecraft, i, j, k, l, m);
   }

   public void setFocused(boolean bl) {
      this.hasFocus = bl;
      if (this.hasFocus) {
         this.ensureVisible(this.getFocused());
      }
   }

   @Override
   public NarrationPriority narrationPriority() {
      return this.hasFocus ? NarrationPriority.FOCUSED : super.narrationPriority();
   }

   @Override
   protected boolean isSelectedItem(int i) {
      return false;
   }

   public void updateNarration(NarrationElementOutput narrationElementOutput) {
      E entry = this.getHovered();
      if (entry != null) {
         ((LegacyContainerObjectSelectionList.LegacyEntry)entry).updateNarration(narrationElementOutput.nest());
         this.narrateListElementPosition(narrationElementOutput, entry);
      } else {
         E entry2 = this.getFocused();
         if (entry2 != null) {
            ((LegacyContainerObjectSelectionList.LegacyEntry)entry2).updateNarration(narrationElementOutput.nest());
            this.narrateListElementPosition(narrationElementOutput, entry2);
         }
      }

      narrationElementOutput.add(NarratedElementType.USAGE, Component.translatable("narration.component_list.usage"));
   }

   public abstract static class LegacyEntry<E extends LegacyContainerObjectSelectionList.LegacyEntry<E>>
      extends LegacyAbstractSelectionList.LegacyEntry<E>
      implements ContainerEventHandler {
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

      public void setFocused(@Nullable GuiEventListener guiEventListener) {
         this.focused = guiEventListener;
      }

      @Nullable
      public GuiEventListener getFocused() {
         return this.focused;
      }

      public abstract List<? extends NarratableEntry> narratables();

      void updateNarration(NarrationElementOutput narrationElementOutput) {
         List<NarratableEntry> list = this.narratables();
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
   }
}
