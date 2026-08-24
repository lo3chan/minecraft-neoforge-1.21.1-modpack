package vazkii.psi.client.core.handler;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.regex.Pattern;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw.Layer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.common.NeoForge;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.ICAD;
import vazkii.psi.api.cad.IPsiBarDisplay;
import vazkii.psi.api.cad.ISocketable;
import vazkii.psi.api.gui.PsiHudElementType;
import vazkii.psi.api.gui.RenderPsiHudEvent;
import vazkii.psi.api.internal.PsiRenderHelper;
import vazkii.psi.common.core.handler.ConfigHandler;
import vazkii.psi.common.core.handler.PlayerDataHandler;
import vazkii.psi.common.item.base.IHUDItem;

@EventBusSubscriber(
   modid = "psi",
   value = {Dist.CLIENT}
)
public final class HUDHandler {
   public static final Layer SOCKETABLE_EQUIPPED_NAME = (graphics, deltatracker) -> {
      if (!((RenderPsiHudEvent)NeoForge.EVENT_BUS.post(new RenderPsiHudEvent(PsiHudElementType.SOCKETABLE_EQUIPPED_NAME))).isCanceled()) {
         renderSocketableEquippedName(graphics, deltatracker);
      }
   };
   public static final Layer HUD_ITEM = (graphics, deltatracker) -> {
      if (!((RenderPsiHudEvent)NeoForge.EVENT_BUS.post(new RenderPsiHudEvent(PsiHudElementType.HUD_ITEM))).isCanceled()) {
         renderHUDItem(graphics, deltatracker);
      }
   };
   private static final ResourceLocation psiBar = ResourceLocation.parse("psi:textures/gui/psi_bar.png");
   private static final ResourceLocation psiBarMask = ResourceLocation.parse("psi:textures/gui/psi_bar_mask.png");
   private static final ResourceLocation psiBarShatter = ResourceLocation.parse("psi:textures/gui/psi_bar_shatter.png");
   private static final int maxRemainingTicks = 30;
   private static boolean registeredMask = false;
   public static final Layer PSI_BAR = (graphics, deltatracker) -> {
      if (!((RenderPsiHudEvent)NeoForge.EVENT_BUS.post(new RenderPsiHudEvent(PsiHudElementType.PSI_BAR))).isCanceled()) {
         drawPsiBar(graphics, deltatracker);
      }
   };
   private static ItemStack remainingDisplayStack;
   private static int remainingTime;
   private static int remainingCount;
   public static final Layer REMAINING_ITEMS = (graphics, deltatracker) -> {
      if (!((RenderPsiHudEvent)NeoForge.EVENT_BUS.post(new RenderPsiHudEvent(PsiHudElementType.REMAINING_ITEMS))).isCanceled()) {
         renderRemainingItems(graphics, deltatracker);
      }
   };

   @SubscribeEvent
   @OnlyIn(Dist.CLIENT)
   public static void register(RegisterGuiLayersEvent event) {
      event.registerAboveAll(PsiAPI.location("psi_bar"), PSI_BAR);
      event.registerAboveAll(PsiAPI.location("socketable_equipped_name"), SOCKETABLE_EQUIPPED_NAME);
      event.registerAboveAll(PsiAPI.location("remaining_items"), REMAINING_ITEMS);
      event.registerAboveAll(PsiAPI.location("hud_item"), HUD_ITEM);
   }

   public static void tick() {
      if (remainingTime >= 0) {
         remainingTime--;
      }
   }

   private static boolean showsBar(PlayerDataHandler.PlayerData data, ItemStack stack) {
      if (stack.isEmpty()) {
         return false;
      } else {
         IPsiBarDisplay display = (IPsiBarDisplay)stack.getCapability(PsiAPI.PSI_BAR_DISPLAY_CAPABILITY);
         return display != null ? display.shouldShow(data) : false;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static void drawPsiBar(GuiGraphics graphics, DeltaTracker deltatracker) {
      Minecraft mc = Minecraft.getInstance();
      ItemStack cadStack = PsiAPI.getPlayerCAD(mc.player);
      if (!cadStack.isEmpty()) {
         ICAD cad = (ICAD)cadStack.getItem();
         PlayerDataHandler.PlayerData data = PlayerDataHandler.get(mc.player);
         int totalPsi = data.getTotalPsi();
         int currPsi = data.getAvailablePsi();
         if (!(Boolean)ConfigHandler.CLIENT.contextSensitiveBar.get()
            || currPsi != totalPsi
            || showsBar(data, mc.player.getMainHandItem())
            || showsBar(data, mc.player.getOffhandItem())) {
            graphics.pose().pushPose();
            boolean right = (Boolean)ConfigHandler.CLIENT.psiBarOnRight.get();
            int pad = 3;
            int width = 32;
            int height = 140;
            int x = -pad;
            if (right) {
               x = graphics.guiWidth() + pad - width;
            }

            int y = graphics.guiHeight() / 2 - height / 2;
            if (!registeredMask) {
               RenderSystem.setShaderTexture(0, psiBarMask);
               RenderSystem.setShaderTexture(1, psiBarShatter);
               registeredMask = true;
            }

            RenderSystem.enableBlend();
            graphics.blit(psiBar, x, y, 0.0F, 0.0F, width, height, 64, 256);
            x += 8;
            y += 26;
            int var28 = 16;
            int var30 = 106;
            float r = 0.6F;
            float g = 0.65F;
            float b = 1.0F;
            if (data.isOverflowed()) {
               r = 1.0F;
               g = 0.6F;
               b = 0.6F;
            }

            int origHeight = var30;
            int origY = y;
            int v = 0;
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(770, 771);

            for (PlayerDataHandler.PlayerData.Deduction d : data.deductions) {
               float a = d.getPercentile(deltatracker.getGameTimeDeltaPartialTick(false));
               RenderSystem.setShaderColor(r, g, b, a);
               var30 = (int)Math.ceil((double)origHeight * d.deduct / totalPsi);
               int effHeight = (int)((double)origHeight * d.current / totalPsi);
               v = origHeight - effHeight;
               y = origY + v;
               usePsiBarShader(a, d.shatter, data.overflowed);
               graphics.blit(psiBar, x, y, 32.0F, v, var28, var30, 64, 256);
            }

            float textY = origY;
            if (totalPsi > 0) {
               var30 = (int)((double)origHeight * data.availablePsi / totalPsi);
               v = origHeight - var30;
               y = origY + v;
               if (data.availablePsi != data.lastAvailablePsi) {
                  float textHeight = (float)(
                     origHeight
                        * (
                           data.availablePsi * deltatracker.getGameTimeDeltaPartialTick(false)
                              + data.lastAvailablePsi * (1.0 - deltatracker.getGameTimeDeltaPartialTick(false))
                        )
                        / totalPsi
                  );
                  textY = origY + (origHeight - textHeight);
               } else {
                  textY = y;
               }
            } else {
               var30 = 0;
            }

            RenderSystem.setShaderColor(r, g, b, 1.0F);
            usePsiBarShader(1.0F, false, data.overflowed);
            graphics.blit(psiBar, x, y, 32.0F, v, var28, var30, 64, 256);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            graphics.pose().pushPose();
            graphics.pose().translate(0.0F, textY, 0.0F);
            var28 = 44;
            int var33 = 3;
            int storedPsi = cad.getStoredPsi(cadStack);
            String s1 = storedPsi == -1 ? "∞" : data.availablePsi + "";
            String s2 = storedPsi + "";
            int offBar = 22;
            int offStr1 = 7 + mc.font.width(s1);
            int offStr2 = 7 + mc.font.width(s2);
            if (!right) {
               offBar = 6;
               offStr1 = -23;
               offStr2 = -23;
            }

            int color = cad.getSpellColor(cadStack);
            RenderSystem.setShaderColor(PsiRenderHelper.r(color) / 255.0F, PsiRenderHelper.g(color) / 255.0F, PsiRenderHelper.b(color) / 255.0F, 1.0F);
            graphics.blit(psiBar, x - offBar, -2, 0.0F, 140.0F, var28, var33, 64, 256);
            graphics.drawString(mc.font, s1, x - offStr1, -11, 16777215, true);
            graphics.pose().popPose();
            if (storedPsi != -1) {
               graphics.pose().pushPose();
               graphics.pose().translate(0.0F, Math.max(textY + 3.0F, (float)(origY + 100)), 0.0F);
               graphics.drawString(mc.font, s2, x - offStr2, 0, 16777215, true);
               graphics.pose().popPose();
            }

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.disableBlend();
            graphics.pose().popPose();
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   private static void renderSocketableEquippedName(GuiGraphics graphics, DeltaTracker deltatracker) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.player != null && mc.gui.toolHighlightTimer - 10 > 0) {
         ItemStack stack = mc.player.getItemInHand(InteractionHand.MAIN_HAND);
         if (ISocketable.isSocketable(stack)) {
            String name = ISocketable.getSocketedItemName(stack, "").getString();
            if (!stack.isEmpty() && !name.trim().isEmpty()) {
               ISocketable socketable = ISocketable.socketable(stack);
               ItemStack bullet = socketable.getSelectedBullet();
               int ticks = mc.gui.toolHighlightTimer - 10;
               int alpha = Math.min(255, (int)((ticks - deltatracker.getGameTimeDeltaPartialTick(false)) * 256.0F / 10.0F));
               int color = -15481345 + (alpha << 24);
               int x = graphics.guiWidth() / 2 - mc.font.width(name) / 2;
               int y = graphics.guiHeight() - 71;
               int w = mc.font.width(name);
               if (mc.player.isCreative()) {
                  y += 14;
               }

               graphics.drawString(mc.font, name, x, y, color, true);
               graphics.pose().pushPose();
               graphics.pose().translate(x + w, y - 6, 0.0F);
               graphics.pose().scale(alpha / 255.0F, 1.0F, 1.0F);
               graphics.renderFakeItem(bullet, 0, 0);
               graphics.pose().popPose();
            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   private static void renderRemainingItems(GuiGraphics graphics, DeltaTracker deltatracker) {
      if (remainingTime > 0 && !remainingDisplayStack.isEmpty()) {
         int pos = 30 - remainingTime;
         Minecraft mc = Minecraft.getInstance();
         int remainingLeaveTicks = 20;
         int x = graphics.guiWidth() / 2 + 10 + Math.max(0, pos - remainingLeaveTicks);
         int y = graphics.guiHeight() / 2;
         int start = 30 - remainingLeaveTicks;
         float alpha = remainingTime + deltatracker.getGameTimeDeltaPartialTick(false) > start
            ? 1.0F
            : (remainingTime + deltatracker.getGameTimeDeltaPartialTick(false)) / start;
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
         int xp = x + (int)(16.0F * (1.0F - alpha));
         graphics.pose().pushPose();
         graphics.pose().translate(xp, y, 0.0F);
         graphics.pose().scale(alpha, 1.0F, 1.0F);
         graphics.renderFakeItem(remainingDisplayStack, 0, 0);
         graphics.pose().scale(1.0F / alpha, 1.0F, 1.0F);
         graphics.pose().translate(-xp, -y, 0.0F);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         String text = remainingDisplayStack.getHoverName().plainCopy().withStyle(ChatFormatting.GREEN).getString();
         if (remainingCount >= 0) {
            int max = remainingDisplayStack.getMaxStackSize();
            int stacks = remainingCount / max;
            int rem = remainingCount % max;
            if (stacks == 0) {
               text = remainingCount + "";
            } else {
               text = remainingCount
                  + " ("
                  + ChatFormatting.AQUA
                  + stacks
                  + ChatFormatting.RESET
                  + "*"
                  + ChatFormatting.GRAY
                  + max
                  + ChatFormatting.RESET
                  + "+"
                  + ChatFormatting.YELLOW
                  + rem
                  + ChatFormatting.RESET
                  + ")";
            }
         } else if (remainingCount == -1) {
            text = "∞";
         }

         int color = 16777215 | (int)(alpha * 255.0F) << 24;
         graphics.drawString(mc.font, text, x + 20, y + 6, color, true);
         graphics.pose().popPose();
      }
   }

   @OnlyIn(Dist.CLIENT)
   private static void renderHUDItem(GuiGraphics graphics, DeltaTracker deltatracker) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.player != null) {
         ItemStack stack = mc.player.getMainHandItem();
         if (!stack.isEmpty() && stack.getItem() instanceof IHUDItem hudItem) {
            hudItem.drawHUD(graphics, deltatracker.getGameTimeDeltaPartialTick(false), graphics.guiWidth(), graphics.guiHeight(), stack);
         }

         stack = mc.player.getOffhandItem();
         if (!stack.isEmpty() && stack.getItem() instanceof IHUDItem hudItem) {
            hudItem.drawHUD(graphics, deltatracker.getGameTimeDeltaPartialTick(false), graphics.guiWidth(), graphics.guiHeight(), stack);
         }
      }
   }

   public static void setRemaining(ItemStack stack, int count) {
      remainingDisplayStack = stack;
      remainingCount = count;
      remainingTime = stack.isEmpty() ? 0 : 30;
   }

   public static void setRemaining(Player player, ItemStack displayStack, Pattern pattern) {
      int count = 0;

      for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
         ItemStack stack = player.getInventory().getItem(i);
         if (!stack.isEmpty() && (pattern == null ? ItemStack.isSameItem(displayStack, stack) : pattern.matcher(stack.getDescriptionId()).find())) {
            count += stack.getCount();
         }
      }

      setRemaining(displayStack, count);
   }

   @OnlyIn(Dist.CLIENT)
   public static void usePsiBarShader(float percentile, boolean shatter, boolean overflowed) {
      ShaderInstance psiBarShader = ShaderHandler.getPsiBarShader();
      RenderSystem.setShader(ShaderHandler::getPsiBarShader);
      RenderSystem.setShaderTexture(0, psiBar);
      RenderSystem.setShaderTexture(1, shatter ? psiBarShatter : psiBarMask);
      psiBarShader.safeGetUniform("GameTime").set(RenderSystem.getShaderGameTime());
      psiBarShader.safeGetUniform("PsiBarPercentile").set(percentile);
      psiBarShader.safeGetUniform("PsiBarOverflowed").set(overflowed ? 1 : 0);
   }
}
