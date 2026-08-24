package io.wispforest.owo.compat.rei;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.mixin.itemgroup.CreativeInventoryScreenAccessor;
import io.wispforest.owo.mixin.ui.access.BaseOwoHandledScreenAccessor;
import io.wispforest.owo.ui.base.BaseOwoHandledScreen;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.ParentComponent;
import io.wispforest.owo.ui.core.Surface;
import io.wispforest.owo.ui.util.ScissorStack;
import io.wispforest.owo.util.pond.OwoCreativeInventoryScreenExtensions;
import java.util.ArrayList;
import java.util.Collections;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.REIRuntime;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZones;
import me.shedaniel.rei.api.client.registry.screen.OverlayDecider;
import me.shedaniel.rei.api.client.registry.screen.OverlayRendererProvider;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.client.registry.screen.OverlayRendererProvider.Sink;
import me.shedaniel.rei.forge.REIPluginClient;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4fStack;

@REIPluginClient
public class OwoReiPlugin implements REIClientPlugin {
   @Nullable
   private static Sink renderSink = null;

   public void registerExclusionZones(ExclusionZones zones) {
      zones.register(CreativeModeInventoryScreen.class, screen -> {
         if (!(CreativeInventoryScreenAccessor.owo$getSelectedTab() instanceof OwoItemGroup owoGroup)) {
            return Collections.emptySet();
         } else if (owoGroup.getButtons().isEmpty()) {
            return Collections.emptySet();
         } else {
            int x = ((OwoCreativeInventoryScreenExtensions)screen).owo$getRootX();
            int y = ((OwoCreativeInventoryScreenExtensions)screen).owo$getRootY();
            int stackHeight = owoGroup.getButtonStackHeight();
            y -= 13 * (stackHeight - 4);
            ArrayList<Rectangle> rectangles = new ArrayList<>();

            for (int i = 0; i < owoGroup.getButtons().size(); i++) {
               int xOffset = x + 198 + i / stackHeight * 26;
               int yOffset = y + 10 + i % stackHeight * 30;
               rectangles.add(new Rectangle(xOffset, yOffset, 24, 24));
            }

            return rectangles;
         }
      });
      zones.register(
         BaseOwoHandledScreen.class,
         screen -> screen.componentsForExclusionAreas().map(rect -> new Rectangle(rect.x(), rect.y(), rect.width(), rect.height())).toList()
      );
   }

   public void registerScreens(ScreenRegistry registry) {
      registry.registerDecider(new OverlayDecider() {
         public <R extends Screen> boolean isHandingScreen(Class<R> screen) {
            return BaseOwoHandledScreen.class.isAssignableFrom(screen);
         }

         public OverlayRendererProvider getRendererProvider() {
            return new OverlayRendererProvider() {
               public void onApplied(Sink sink) {
                  OwoReiPlugin.renderSink = sink;
               }

               public void onRemoved() {
                  OwoReiPlugin.renderSink = null;
               }
            };
         }
      });
   }

   private static void renderOverlay(Screen screen, Runnable renderFunction) {
      if (REIRuntime.getInstance().getSearchTextField().getText().equals("froge")) {
         Matrix4fStack modelView = RenderSystem.getModelViewStack();
         long time = System.currentTimeMillis();
         float scale = 0.75F + (float)(Math.sin(time / 500.0) * 0.5);
         modelView.pushMatrix();
         modelView.translate(screen.width / 2.0F - scale / 2.0F * screen.width, screen.height / 2.0F - scale / 2.0F * screen.height, 0.0F);
         modelView.scale(scale, scale, 1.0F);
         modelView.translate((float)(Math.sin(time / 1000.0) * 0.75) * screen.width, (float)(Math.sin(time / 500.0) * 0.75) * screen.height, 0.0F);
         modelView.translate(screen.width / 2.0F, screen.height / 2.0F, 0.0F);
         modelView.rotate(Axis.ZP.rotationDegrees((float)(time / 25.0 % 360.0)));
         modelView.translate(screen.width / -2.0F, screen.height / -2.0F, 0.0F);

         for (int i = 0; i < 20; i++) {
            modelView.pushMatrix();
            modelView.translate(screen.width / 2.0F, screen.height / 2.0F, 0.0F);
            modelView.rotate(Axis.ZP.rotationDegrees(i * 18));
            modelView.translate(screen.width / -2.0F, screen.height / -2.0F, 0.0F);
            RenderSystem.applyModelViewMatrix();
            ScissorStack.pushDirect(0, 0, 2147483647, 2147483647);
            renderFunction.run();
            GlStateManager._enableScissorTest();
            ScissorStack.pop();
            modelView.popMatrix();
         }

         modelView.popMatrix();
         RenderSystem.applyModelViewMatrix();
      } else {
         ScissorStack.pushDirect(0, 0, 2147483647, 2147483647);
         renderFunction.run();
         GlStateManager._enableScissorTest();
         ScissorStack.pop();
      }
   }

   static {
      NeoForge.EVENT_BUS
         .addListener(
            event -> {
               if (event.getScreen() instanceof BaseOwoHandledScreenAccessor accessor) {
                  ParentComponent root = accessor.owo$getUIAdapter().rootComponent;
                  OwoReiPlugin.CallbackSurface surface;
                  if (root.surface() instanceof OwoReiPlugin.CallbackSurface wrapped) {
                     surface = wrapped;
                  } else {
                     surface = new OwoReiPlugin.CallbackSurface(root.surface());
                     root.surface(surface);
                  }

                  surface.callback = () -> {
                     if (renderSink != null) {
                        renderOverlay(
                           event.getScreen(), () -> renderSink.render(event.getGuiGraphics(), event.getMouseX(), event.getMouseY(), event.getPartialTick())
                        );
                     }
                  };
               }
            }
         );
      NeoForge.EVENT_BUS
         .addListener(
            event -> {
               if (event.getScreen() instanceof BaseOwoHandledScreenAccessor) {
                  if (renderSink != null) {
                     renderOverlay(
                        event.getScreen(), () -> renderSink.lateRender(event.getGuiGraphics(), event.getMouseX(), event.getMouseY(), event.getPartialTick())
                     );
                  }
               }
            }
         );
   }

   private static class CallbackSurface implements Surface {
      public final Surface inner;
      @NotNull
      public Runnable callback = () -> {};

      private CallbackSurface(Surface inner) {
         this.inner = inner;
      }

      @Override
      public void draw(OwoUIDrawContext context, ParentComponent component) {
         this.inner.draw(context, component);
         this.callback.run();
      }
   }
}
