package io.wispforest.owo.ui.base;

import io.wispforest.owo.Owo;
import io.wispforest.owo.mixin.ui.SlotAccessor;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.CursorStyle;
import io.wispforest.owo.ui.core.OwoUIAdapter;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.ParentComponent;
import io.wispforest.owo.ui.core.PositionedRectangle;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import io.wispforest.owo.ui.inject.GreedyInputComponent;
import io.wispforest.owo.ui.util.DisposableScreen;
import io.wispforest.owo.ui.util.UIErrorToast;
import io.wispforest.owo.util.pond.OwoSlotExtension;
import java.util.ArrayList;
import java.util.stream.Stream;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.OverrideOnly;
import org.lwjgl.opengl.GL11;

public abstract class BaseOwoHandledScreen<R extends ParentComponent, S extends AbstractContainerMenu>
   extends AbstractContainerScreen<S>
   implements DisposableScreen {
   protected OwoUIAdapter<R> uiAdapter = null;
   protected boolean invalid = false;

   protected BaseOwoHandledScreen(S handler, Inventory inventory, net.minecraft.network.chat.Component title) {
      super(handler, inventory, title);
   }

   @NotNull
   protected abstract OwoUIAdapter<R> createAdapter();

   protected abstract void build(R var1);

   protected void init() {
      super.init();
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

   protected void disableSlot(int index) {
      this.disableSlot((Slot)this.menu.slots.get(index));
   }

   protected void disableSlot(Slot slot) {
      ((OwoSlotExtension)slot).owo$setDisabledOverride(true);
   }

   protected void enableSlot(int index) {
      this.enableSlot((Slot)this.menu.slots.get(index));
   }

   protected void enableSlot(Slot slot) {
      ((OwoSlotExtension)slot).owo$setDisabledOverride(false);
   }

   protected boolean isSlotEnabled(int index) {
      return this.isSlotEnabled((Slot)this.menu.slots.get(index));
   }

   protected boolean isSlotEnabled(Slot slot) {
      return !((OwoSlotExtension)slot).owo$getDisabledOverride();
   }

   protected BaseOwoHandledScreen<R, S>.SlotComponent slotAsComponent(int index) {
      return new BaseOwoHandledScreen.SlotComponent(index);
   }

   protected <C extends Component> C component(Class<C> expectedClass, String id) {
      return this.uiAdapter.rootComponent.childById(expectedClass, id);
   }

   @OverrideOnly
   public Stream<Component> componentsForExclusionAreas() {
      if (this.children().isEmpty()) {
         return Stream.of();
      } else {
         R rootComponent = this.uiAdapter.rootComponent;
         ArrayList<Component> children = new ArrayList<>();
         rootComponent.collectDescendants(children);
         children.remove(rootComponent);
         return children.stream().filter(component -> !(component instanceof ParentComponent parent && parent.surface() == Surface.BLANK));
      }
   }

   public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float delta) {
   }

   public void render(GuiGraphics vanillaContext, int mouseX, int mouseY, float delta) {
      OwoUIDrawContext context = OwoUIDrawContext.of(vanillaContext);
      if (!this.invalid) {
         super.render(context, mouseX, mouseY, delta);
         if (this.uiAdapter.enableInspector) {
            context.pose().translate(0.0F, 0.0F, 500.0F);

            for (int i = 0; i < this.menu.slots.size(); i++) {
               Slot slot = (Slot)this.menu.slots.get(i);
               if (slot.isActive()) {
                  context.drawText(
                     net.minecraft.network.chat.Component.literal("H:" + i),
                     this.leftPos + slot.x + 15,
                     this.topPos + slot.y + 9,
                     0.5F,
                     38655,
                     OwoUIDrawContext.TextAnchor.BOTTOM_RIGHT
                  );
                  context.drawText(
                     net.minecraft.network.chat.Component.literal("I:" + slot.getContainerSlot()),
                     this.leftPos + slot.x + 15,
                     this.topPos + slot.y + 15,
                     0.5F,
                     5767423,
                     OwoUIDrawContext.TextAnchor.BOTTOM_RIGHT
                  );
               }
            }

            context.pose().translate(0.0F, 0.0F, -500.0F);
         }

         this.renderTooltip(context, mouseX, mouseY);
      } else {
         this.onClose();
      }
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      return (modifiers & 2) == 0
            && this.uiAdapter.rootComponent.focusHandler().focused() instanceof GreedyInputComponent inputComponent
            && inputComponent.onKeyPress(keyCode, scanCode, modifiers)
         ? true
         : super.keyPressed(keyCode, scanCode, modifiers);
   }

   public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
      return this.uiAdapter.mouseDragged(mouseX, mouseY, button, deltaX, deltaY) || super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
   }

   @Nullable
   public GuiEventListener getFocused() {
      return this.uiAdapter;
   }

   public void removed() {
      super.removed();
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

   protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {
   }

   public class SlotComponent extends BaseComponent {
      protected final Slot slot;
      protected boolean didDraw = false;

      protected SlotComponent(int index) {
         this.slot = BaseOwoHandledScreen.this.menu.getSlot(index);
      }

      @Override
      public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
         this.didDraw = true;
         int[] scissor = new int[4];
         GL11.glGetIntegerv(3088, scissor);
         ((OwoSlotExtension)this.slot).owo$setScissorArea(PositionedRectangle.of(scissor[0], scissor[1], scissor[2], scissor[3]));
      }

      @Override
      public void update(float delta, int mouseX, int mouseY) {
         super.update(delta, mouseX, mouseY);
         ((OwoSlotExtension)this.slot).owo$setDisabledOverride(!this.didDraw);
         this.didDraw = false;
      }

      @Override
      public void drawTooltip(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
         if (!this.slot.hasItem()) {
            super.drawTooltip(context, mouseX, mouseY, partialTicks, delta);
         }
      }

      @Override
      public boolean shouldDrawTooltip(double mouseX, double mouseY) {
         return super.shouldDrawTooltip(mouseX, mouseY);
      }

      @Override
      protected int determineHorizontalContentSize(Sizing sizing) {
         return 16;
      }

      @Override
      protected int determineVerticalContentSize(Sizing sizing) {
         return 16;
      }

      @Override
      public void updateX(int x) {
         super.updateX(x);
         ((SlotAccessor)this.slot).owo$setX(x - BaseOwoHandledScreen.this.leftPos);
      }

      @Override
      public void updateY(int y) {
         super.updateY(y);
         ((SlotAccessor)this.slot).owo$setY(y - BaseOwoHandledScreen.this.topPos);
      }
   }
}
