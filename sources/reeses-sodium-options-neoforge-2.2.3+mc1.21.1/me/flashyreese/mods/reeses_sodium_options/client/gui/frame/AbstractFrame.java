package me.flashyreese.mods.reeses_sodium_options.client.gui.frame;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option.OptionRow;
import me.flashyreese.mods.reeses_sodium_options.client.gui.layout.LayoutBounds;
import me.flashyreese.mods.reeses_sodium_options.client.gui.widget.BaseWidget;
import net.caffeinemc.mods.sodium.client.config.structure.ModOptions;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractFrame extends BaseWidget implements ContainerEventHandler {
   protected final Screen screen;
   protected final List<GuiEventListener> children = new ArrayList<>();
   protected final List<OptionRow> optionRows = new ArrayList<>();
   protected final ModOptions modOptions;
   protected boolean renderOutline;
   private GuiEventListener focused;
   private boolean dragging;
   private Consumer<GuiEventListener> focusListener;

   public AbstractFrame(LayoutBounds dim, Screen screen, boolean renderOutline, ModOptions modOptions) {
      super(dim);
      this.screen = screen;
      this.renderOutline = renderOutline;
      this.modOptions = modOptions;
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      for (GuiEventListener element : this.children) {
         if (element instanceof OptionRow optionRow) {
            if (optionRow.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
               return true;
            }
         } else if (element instanceof AbstractFrame frame && frame.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
            return true;
         }
      }

      return false;
   }

   public void buildFrame() {
      this.clearFocusIfFocusedChildRemoved();
      this.collectOptionRows();
   }

   public void rebuildFrameContent() {
      this.buildFrame();
   }

   public void updateFrameDim(LayoutBounds dim) {
      this.setDim(dim);
   }

   protected void collectOptionRows() {
      this.optionRows.clear();

      for (GuiEventListener element : this.children) {
         if (element instanceof AbstractFrame abstractFrame) {
            this.optionRows.addAll(abstractFrame.optionRows);
         }

         if (element instanceof OptionRow optionRow) {
            this.optionRows.add(optionRow);
         }
      }
   }

   protected void clearFocusIfFocusedChildRemoved() {
      GuiEventListener focused = this.getFocused();
      if (focused != null && !this.children.contains(focused)) {
         this.setFocused(null);
      }
   }

   @Nullable
   public OptionRow findFirstOptionRow(Predicate<OptionRow> predicate) {
      return this.optionRows.stream().filter(predicate).findFirst().orElse(null);
   }

   @Nullable
   public OptionRow findLastOptionRow(Predicate<OptionRow> predicate) {
      for (int i = this.optionRows.size() - 1; i >= 0; i--) {
         OptionRow optionRow = this.optionRows.get(i);
         if (predicate.test(optionRow)) {
            return optionRow;
         }
      }

      return null;
   }

   public boolean focusOptionRow(OptionRow optionRow) {
      for (GuiEventListener child : this.children) {
         if (child == optionRow) {
            this.setFocused(optionRow);
            return true;
         }

         if (child instanceof AbstractFrame frame && frame.focusOptionRow(optionRow)) {
            this.setFocused(frame);
            return true;
         }
      }

      return false;
   }

   public void releaseActionButtonLayoutHolds() {
      for (OptionRow optionRow : this.optionRows) {
         optionRow.releaseActionButtonLayoutHold();
      }
   }

   @Override
   public void render(@NotNull GuiGraphics drawContext, int mouseX, int mouseY, float delta) {
      if (this.renderOutline) {
         this.drawBorder(drawContext, this.getX(), this.getY(), this.getLimitX(), this.getLimitY(), -5592406);
      }

      for (GuiEventListener child : this.children) {
         if (child instanceof Renderable renderable) {
            renderable.render(drawContext, mouseX, mouseY, delta);
         }
      }
   }

   public void applyScissor(GuiGraphics guiGraphics, int x, int y, int width, int height, Runnable action) {
      guiGraphics.enableScissor(x, y, x + width, y + height);
      action.run();
      guiGraphics.disableScissor();
   }

   protected LayoutBounds getFrameDim() {
      return this.getDimensions();
   }

   public void registerFocusListener(Consumer<GuiEventListener> focusListener) {
      this.focusListener = focusListener;
   }

   public boolean isDragging() {
      return this.dragging;
   }

   public void setDragging(boolean dragging) {
      this.dragging = dragging;
   }

   @Nullable
   public GuiEventListener getFocused() {
      return this.focused;
   }

   public void setFocused(@Nullable GuiEventListener focused) {
      if (this.focused != focused) {
         if (this.focused != null) {
            this.focused.setFocused(false);
         }

         this.focused = focused;
         if (focused != null) {
            focused.setFocused(true);
         }

         if (this.focusListener != null) {
            this.focusListener.accept(focused);
         }
      }
   }

   @NotNull
   public List<? extends GuiEventListener> children() {
      return this.children;
   }

   public List<NarratableEntry> collectNarratables() {
      List<NarratableEntry> narratables = new ArrayList<>();

      for (GuiEventListener child : this.children) {
         if (child instanceof AbstractFrame frame) {
            narratables.addAll(frame.collectNarratables());
         } else if (child instanceof OptionRow optionRow) {
            narratables.addAll(optionRow.collectNarratables());
         } else if (child instanceof NarratableEntry narratable) {
            narratables.add(narratable);
         }
      }

      return narratables;
   }

   public boolean keyPressed(int i, int j, int k) {
      GuiEventListener focused = this.getFocused();
      return focused != null && focused.keyPressed(i, j, k);
   }

   @Nullable
   @Override
   public ComponentPath nextFocusPath(@NotNull FocusNavigationEvent navigation) {
      return super.nextFocusPath(navigation);
   }
}
