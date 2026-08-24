package io.wispforest.owo.ui.core;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.Owo;
import io.wispforest.owo.mixin.ScreenAccessor;
import io.wispforest.owo.renderdoc.RenderDoc;
import io.wispforest.owo.ui.util.CursorAdapter;
import java.util.function.BiFunction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarratableEntry.NarrationPriority;
import net.minecraft.client.gui.screens.Screen;

public class OwoUIAdapter<R extends ParentComponent> implements GuiEventListener, Renderable, NarratableEntry {
   private static boolean isRendering = false;
   public final R rootComponent;
   public final CursorAdapter cursorAdapter;
   protected boolean disposed = false;
   protected boolean captureFrame = false;
   protected int x;
   protected int y;
   protected int width;
   protected int height;
   public boolean enableInspector = false;
   public boolean globalInspector = false;
   public int inspectorZOffset = 1000;

   protected OwoUIAdapter(int x, int y, int width, int height, R rootComponent) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
      this.cursorAdapter = CursorAdapter.ofClientWindow();
      this.rootComponent = rootComponent;
   }

   public static <R extends ParentComponent> OwoUIAdapter<R> create(Screen screen, BiFunction<Sizing, Sizing, R> rootComponentMaker) {
      R rootComponent = (R)rootComponentMaker.apply(Sizing.fill(100), Sizing.fill(100));
      OwoUIAdapter<R> adapter = new OwoUIAdapter<>(0, 0, screen.width, screen.height, rootComponent);
      ((ScreenAccessor)screen).owo$addDrawableChild(adapter);
      screen.setFocused(adapter);
      return adapter;
   }

   public static <R extends ParentComponent> OwoUIAdapter<R> createWithoutScreen(
      int x, int y, int width, int height, BiFunction<Sizing, Sizing, R> rootComponentMaker
   ) {
      R rootComponent = (R)rootComponentMaker.apply(Sizing.fill(100), Sizing.fill(100));
      return new OwoUIAdapter<>(x, y, width, height, rootComponent);
   }

   public void inflateAndMount() {
      this.rootComponent.inflate(Size.of(this.width, this.height));
      this.rootComponent.mount(null, this.x, this.y);
   }

   public void moveAndResize(int x, int y, int width, int height) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
      this.inflateAndMount();
   }

   public void dispose() {
      this.cursorAdapter.dispose();
      this.disposed = true;
   }

   public boolean toggleInspector() {
      return this.enableInspector = !this.enableInspector;
   }

   public boolean toggleGlobalInspector() {
      return this.globalInspector = !this.globalInspector;
   }

   public int x() {
      return this.x;
   }

   public int y() {
      return this.y;
   }

   public int width() {
      return this.width;
   }

   public int height() {
      return this.height;
   }

   public void render(GuiGraphics context, int mouseX, int mouseY, float partialTicks) {
      if (!(context instanceof OwoUIDrawContext)) {
         context = OwoUIDrawContext.of(context);
      }

      OwoUIDrawContext owoContext = (OwoUIDrawContext)context;

      try {
         isRendering = true;
         if (this.captureFrame) {
            RenderDoc.startFrameCapture();
         }

         float delta = Minecraft.getInstance().getTimer().getGameTimeDeltaTicks();
         Window window = Minecraft.getInstance().getWindow();
         this.rootComponent.update(delta, mouseX, mouseY);
         RenderSystem.enableDepthTest();
         GlStateManager._enableScissorTest();
         GlStateManager._scissorBox(0, 0, window.getWidth(), window.getHeight());
         this.rootComponent.draw(owoContext, mouseX, mouseY, partialTicks, delta);
         GlStateManager._disableScissorTest();
         RenderSystem.disableDepthTest();
         this.rootComponent.drawTooltip(owoContext, mouseX, mouseY, partialTicks, delta);
         Component hovered = this.rootComponent.childAt(mouseX, mouseY);
         if (!this.disposed && hovered != null) {
            this.cursorAdapter.applyStyle(hovered.cursorStyle());
         }

         if (this.enableInspector) {
            context.pose().translate(0.0F, 0.0F, this.inspectorZOffset);
            owoContext.drawInspector(this.rootComponent, mouseX, mouseY, !this.globalInspector);
            context.pose().translate(0.0F, 0.0F, -this.inspectorZOffset);
         }

         if (this.captureFrame) {
            RenderDoc.endFrameCapture();
         }
      } finally {
         isRendering = false;
         this.captureFrame = false;
      }
   }

   public boolean isMouseOver(double mouseX, double mouseY) {
      return this.rootComponent.isInBoundingBox(mouseX, mouseY);
   }

   public void setFocused(boolean focused) {
   }

   public boolean isFocused() {
      return true;
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      return this.rootComponent.onMouseDown(mouseX, mouseY, button);
   }

   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      return this.rootComponent.onMouseUp(mouseX, mouseY, button);
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      return this.rootComponent.onMouseScroll(mouseX, mouseY, verticalAmount);
   }

   public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
      return this.rootComponent.onMouseDrag(mouseX, mouseY, deltaX, deltaY, button);
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (Owo.DEBUG && keyCode == 340) {
         if ((modifiers & 2) != 0) {
            this.toggleInspector();
         } else if ((modifiers & 4) != 0) {
            this.toggleGlobalInspector();
         }
      }

      if (Owo.DEBUG && keyCode == 82 && RenderDoc.isAvailable() && (modifiers & 4) != 0 && (modifiers & 2) != 0) {
         this.captureFrame = true;
      }

      return this.rootComponent.onKeyPress(keyCode, scanCode, modifiers);
   }

   public boolean charTyped(char chr, int modifiers) {
      return this.rootComponent.onCharTyped(chr, modifiers);
   }

   public NarrationPriority narrationPriority() {
      return NarrationPriority.NONE;
   }

   public void updateNarration(NarrationElementOutput builder) {
   }

   public static boolean isRendering() {
      return isRendering;
   }
}
