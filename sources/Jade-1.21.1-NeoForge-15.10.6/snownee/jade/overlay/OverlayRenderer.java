package snownee.jade.overlay;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import snownee.jade.Jade;
import snownee.jade.JadeClient;
import snownee.jade.api.JadeIds;
import snownee.jade.api.callback.JadeBeforeRenderCallback;
import snownee.jade.api.config.IWailaConfig;
import snownee.jade.api.theme.IThemeHelper;
import snownee.jade.api.ui.TooltipRect;
import snownee.jade.gui.BaseOptionsScreen;
import snownee.jade.gui.PreviewOptionsScreen;
import snownee.jade.impl.ObjectDataCenter;
import snownee.jade.impl.Tooltip;
import snownee.jade.impl.WailaClientRegistration;
import snownee.jade.impl.config.WailaConfig;
import snownee.jade.impl.ui.BoxElement;
import snownee.jade.impl.ui.ItemStackElement;
import snownee.jade.util.ClientProxy;
import snownee.jade.util.ModIdentification;

public class OverlayRenderer {
   public static final TooltipRect rect = new TooltipRect();
   public static float ticks;
   public static boolean shown;
   public static float alpha;
   private static BoxElement lingerTooltip;
   private static float disappearTicks;

   public static boolean shouldShow() {
      if (WailaTickHandler.instance().rootElement == null) {
         return false;
      } else {
         WailaConfig.ConfigGeneral general = Jade.CONFIG.get().getGeneral();
         if (!general.shouldDisplayTooltip()) {
            return false;
         } else if (general.getDisplayMode() == IWailaConfig.DisplayMode.HOLD_KEY && !JadeClient.showOverlay.isDown()) {
            return false;
         } else {
            IWailaConfig.BossBarOverlapMode mode = Jade.CONFIG.get().getGeneral().getBossBarOverlapMode();
            return mode != IWailaConfig.BossBarOverlapMode.HIDE_TOOLTIP
               || Minecraft.getInstance().screen instanceof BaseOptionsScreen
               || ClientProxy.getBossBarRect() == null;
         }
      }
   }

   public static boolean shouldShowImmediately(BoxElement box) {
      Minecraft mc = Minecraft.getInstance();
      if (!ClientProxy.shouldShowWithGui(mc, mc.screen)) {
         return false;
      } else {
         box.updateExpectedRect(rect);
         WailaConfig.ConfigGeneral general = Jade.CONFIG.get().getGeneral();
         if (mc.screen instanceof PreviewOptionsScreen optionsScreen) {
            if (optionsScreen.forcePreviewOverlay()) {
               return true;
            }

            if (!general.previewOverlay) {
               return false;
            }

            Window window = mc.getWindow();
            double x = mc.mouseHandler.xpos() * window.getGuiScaledWidth() / window.getScreenWidth();
            double y = mc.mouseHandler.ypos() * window.getGuiScaledHeight() / window.getScreenHeight();
            if (rect.expectedRect.contains((int)x, (int)y)) {
               return false;
            }
         }

         return mc.getOverlay() != null || mc.options.hideGui ? false : !mc.gui.getTabList().visible || !general.shouldHideFromTabList();
      }
   }

   public static void renderOverlay478757(GuiGraphics guiGraphics, float delta) {
      ticks += delta;
      shown = false;
      BoxElement root = WailaTickHandler.instance().rootElement;
      boolean show;
      if (root == null && PreviewOptionsScreen.isAdjustingPosition()) {
         Tooltip tooltip = new Tooltip();
         tooltip.add(IThemeHelper.get().title(Blocks.GRASS_BLOCK.getName()));
         tooltip.add(IThemeHelper.get().modName(ModIdentification.getModName(Blocks.GRASS_BLOCK)));
         root = new BoxElement(tooltip, IThemeHelper.get().theme().tooltipStyle);
         root.tag(JadeIds.ROOT);
         root.setThemeIcon(ItemStackElement.of(new ItemStack(Blocks.GRASS_BLOCK)), IThemeHelper.get().theme());
         root.updateExpectedRect(rect);
         show = true;
      } else {
         show = shouldShow();
      }

      WailaConfig.ConfigOverlay overlay = Jade.CONFIG.get().getOverlay();
      WailaConfig.ConfigGeneral general = Jade.CONFIG.get().getGeneral();
      if (root != null) {
         lingerTooltip = root;
      }

      if (root == null && lingerTooltip != null) {
         disappearTicks += delta;
         if (disappearTicks < overlay.getDisappearingDelay()) {
            root = lingerTooltip;
            show = true;
         }
      } else {
         disappearTicks = 0.0F;
      }

      if (overlay.getAnimation() && lingerTooltip != null) {
         root = lingerTooltip;
         float speed = general.isDebug() ? 0.1F : 0.6F;
         alpha += (show ? speed : -speed) * delta;
         alpha = Mth.clamp(alpha, 0.0F, 1.0F);
      } else {
         alpha = show ? 1.0F : 0.0F;
      }

      if (root != null) {
         if ((alpha < 0.1F || !shouldShowImmediately(root)) && !PreviewOptionsScreen.isAdjustingPosition()) {
            lingerTooltip = null;
            rect.rect.setWidth(0);
            WailaTickHandler.clearLastNarration();
         } else {
            Minecraft.getInstance().getProfiler().push("Jade Overlay");
            renderOverlay(root, guiGraphics);
            Minecraft.getInstance().getProfiler().pop();
         }
      }
   }

   public static void renderOverlay(BoxElement root, GuiGraphics guiGraphics) {
      root.updateRect(rect);

      for (JadeBeforeRenderCallback callback : WailaClientRegistration.instance().beforeRenderCallback.callbacks()) {
         if (callback.beforeRender(root, rect, guiGraphics, ObjectDataCenter.get())) {
            return;
         }
      }

      PoseStack matrixStack = guiGraphics.pose();
      matrixStack.pushPose();
      Minecraft mc = Minecraft.getInstance();
      Screen screen = mc.screen;
      float z;
      if (screen == null) {
         z = 1.0F;
      } else if (ClientProxy.shouldShowAfterGui(mc, screen)) {
         z = 100.0F;
      } else {
         z = -999.0F;
      }

      matrixStack.translate(rect.rect.getX(), rect.rect.getY(), z);
      float scale = rect.scale;
      if (scale != 1.0F) {
         matrixStack.scale(scale, scale, 1.0F);
      }

      RenderSystem.enableBlend();
      float maxWidth = rect.rect.getWidth();
      float maxHeight = rect.rect.getHeight();
      maxWidth /= scale;
      maxHeight /= scale;
      if (root.getStyle().hasRoundCorner()) {
         maxWidth -= 2.0F;
         maxHeight -= 2.0F;
      }

      root.render(guiGraphics, 0.0F, 0.0F, maxWidth, maxHeight);
      WailaClientRegistration.instance().afterRenderCallback.call(callbackx -> callbackx.afterRender(root, rect, guiGraphics, ObjectDataCenter.get()));
      RenderSystem.disableBlend();
      RenderSystem.disableDepthTest();
      matrixStack.popPose();
      if (Jade.CONFIG.get().getGeneral().shouldEnableTextToSpeech()) {
         WailaTickHandler.narrate(root.getTooltip(), true);
      }

      shown = true;
   }

   public static void clearState() {
      lingerTooltip = null;
      WailaTickHandler.clearLastNarration();
   }
}
