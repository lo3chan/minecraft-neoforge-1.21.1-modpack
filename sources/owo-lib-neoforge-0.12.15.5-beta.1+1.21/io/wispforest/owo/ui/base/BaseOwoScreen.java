package io.wispforest.owo.ui.base;

import io.wispforest.owo.Owo;
import io.wispforest.owo.ui.core.CursorStyle;
import io.wispforest.owo.ui.core.OwoUIAdapter;
import io.wispforest.owo.ui.core.ParentComponent;
import io.wispforest.owo.ui.inject.GreedyInputComponent;
import io.wispforest.owo.ui.util.DisposableScreen;
import io.wispforest.owo.ui.util.UIErrorToast;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseOwoScreen<R extends ParentComponent> extends Screen implements DisposableScreen {
   protected OwoUIAdapter<R> uiAdapter = null;
   protected boolean invalid = false;

   protected BaseOwoScreen(Component title) {
      super(title);
   }

   protected BaseOwoScreen() {
      this(Component.empty());
   }

   @NotNull
   protected abstract OwoUIAdapter<R> createAdapter();

   protected abstract void build(R var1);

   protected void init() {
      if (!this.invalid) {
         if (this.uiAdapter != null) {
            this.uiAdapter.moveAndResize(0, 0, this.width, this.height);
            this.addRenderableWidget(this.uiAdapter);
         } else {
            try {
               this.uiAdapter = this.createAdapter();
               this.build(this.uiAdapter.rootComponent);
               this.uiAdapter.inflateAndMount();
            } catch (Exception var2) {
               Owo.LOGGER.warn("Could not initialize owo screen", var2);
               UIErrorToast.report(var2);
               this.invalid = true;
            }
         }
      }
   }

   protected <C extends io.wispforest.owo.ui.core.Component> C component(Class<C> expectedClass, String id) {
      return this.uiAdapter.rootComponent.childById(expectedClass, id);
   }

   public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float delta) {
   }

   public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
      if (!this.invalid) {
         super.render(context, mouseX, mouseY, delta);
      } else {
         this.onClose();
      }
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (this.uiAdapter == null) {
         return false;
      } else if ((modifiers & 2) == 0
         && this.uiAdapter.rootComponent.focusHandler().focused() instanceof GreedyInputComponent inputComponent
         && inputComponent.onKeyPress(keyCode, scanCode, modifiers)) {
         return true;
      } else if (super.keyPressed(keyCode, scanCode, modifiers)) {
         return true;
      } else if (keyCode == 256 && this.shouldCloseOnEsc()) {
         this.onClose();
         return true;
      } else {
         return false;
      }
   }

   public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
      return this.uiAdapter == null ? false : this.uiAdapter.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
   }

   @Nullable
   public GuiEventListener getFocused() {
      return this.uiAdapter;
   }

   public void removed() {
      if (this.uiAdapter != null) {
         this.uiAdapter.cursorAdapter.applyStyle(CursorStyle.NONE);
      }
   }

   @Override
   public void dispose() {
      if (this.uiAdapter != null) {
         this.uiAdapter.dispose();
      }
   }
}
