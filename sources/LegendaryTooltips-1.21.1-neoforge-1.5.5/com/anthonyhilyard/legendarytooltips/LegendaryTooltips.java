package com.anthonyhilyard.legendarytooltips;

import com.anthonyhilyard.iceberg.events.client.RenderTooltipEvents.ColorExtResult;
import com.anthonyhilyard.iceberg.events.client.RenderTooltipEvents.GatherResult;
import com.anthonyhilyard.iceberg.services.Services;
import com.anthonyhilyard.iceberg.services.IKeyMappingRegistrar.KeyMappingContext;
import com.anthonyhilyard.iceberg.util.Tooltips;
import com.anthonyhilyard.legendarytooltips.config.LegendaryTooltipsConfig;
import com.anthonyhilyard.legendarytooltips.tooltip.ItemModelComponent;
import com.anthonyhilyard.legendarytooltips.tooltip.TooltipDecor;
import com.anthonyhilyard.legendarytooltips.tooltip.TooltipScroll;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.InputConstants.Type;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import java.util.List;
import java.util.Map;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LegendaryTooltips {
   public static final String MODID = "legendarytooltips";
   public static final Logger LOGGER = LogManager.getLogger("legendarytooltips");
   public static final int NUM_FRAMES = 16;
   private static Map<Integer, ItemStack> lastTooltipItems = Maps.newHashMap();
   public static final KeyMapping scrollTooltips = Services.getKeyMappingRegistrar()
      .registerMapping(new KeyMapping("legendarytooltips.key.scrollTooltips", Type.KEYSYM, 340, "key.categories.inventory"), KeyMappingContext.NO_CONFLICT);
   public static boolean scrollTooltipsKeyDown = false;

   public static void init() {
      LegendaryTooltipsConfig.register(LegendaryTooltipsConfig.class, "legendarytooltips");
   }

   public static GatherResult onGatherComponentsEvent(
      ItemStack itemStack, int screenWidth, int screenHeight, List<Either<FormattedText, TooltipComponent>> tooltipElements, int maxWidth, int index
   ) {
      if (LegendaryTooltipsConfig.getInstance().compactTooltips.get()) {
         for (int i = 0; i < tooltipElements.size(); i++) {
            if (tooltipElements.get(i).left().isPresent()) {
               FormattedText text = (FormattedText)tooltipElements.get(i).left().get();
               if (text instanceof MutableComponent component
                  && component.getContents() instanceof TranslatableContents contents
                  && contents.getKey().startsWith("item.modifiers.")) {
                  tooltipElements.remove(i);
                  if (tooltipElements.size() > i - 1
                        && i > 0
                        && tooltipElements.get(i - 1).right().isPresent()
                        && tooltipElements.get(i - 1).right().get() == CommonComponents.EMPTY
                     || tooltipElements.get(i - 1).left().isPresent() && ((FormattedText)tooltipElements.get(i - 1).left().get()).getString().isEmpty()) {
                     tooltipElements.remove(i - 1);
                  }
               }
            }
         }
      }

      if (LegendaryTooltipsConfig.showModelForItem(itemStack) && !tooltipElements.isEmpty() && tooltipElements.get(0).left().isPresent()) {
         tooltipElements.add(0, Either.right(new ItemModelComponent(itemStack)));
      }

      if (LegendaryTooltipsConfig.getMaxTooltipWidth() < maxWidth || maxWidth == -1) {
         maxWidth = LegendaryTooltipsConfig.getMaxTooltipWidth();
      }

      return new GatherResult(InteractionResult.PASS, maxWidth, tooltipElements);
   }

   public static void onRenderTick(DeltaTracker tracker) {
      if (LegendaryTooltipsConfig.getInstance() != null && LegendaryTooltipsConfig.getInstance().isLoaded()) {
         float deltaTime = tracker.getRealtimeDeltaTicks() * 0.05F;
         TooltipDecor.updateTimer(deltaTime);
         ItemModelComponent.updateTimer(deltaTime);
         if (!Tooltips.anyTooltipsVisible()) {
            TooltipDecor.resetTimer();
            TooltipScroll.resetAll();
            lastTooltipItems.clear();
         }
      }
   }

   private static boolean areStacksEqual(ItemStack first, ItemStack second) {
      return first == second ? true : ItemStack.hashItemAndComponents(first) + first.getCount() == ItemStack.hashItemAndComponents(second) + second.getCount();
   }

   public static ColorExtResult onTooltipColorEvent(
      ItemStack stack,
      GuiGraphics graphics,
      int x,
      int y,
      Font font,
      int backgroundStart,
      int backgroundEnd,
      int borderStart,
      int borderEnd,
      List<ClientTooltipComponent> components,
      boolean comparison,
      int index
   ) {
      ColorExtResult result = new ColorExtResult(backgroundStart, backgroundEnd, borderStart, borderEnd);
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.level != null && minecraft.level.registryAccess() != null) {
         if (!areStacksEqual(lastTooltipItems.computeIfAbsent(index, k -> ItemStack.EMPTY), stack)) {
            TooltipDecor.resetTimer();
            TooltipScroll.reset(index);
            lastTooltipItems.put(index, stack);
         }

         LegendaryTooltipsConfig.FrameDefinition frameDefinition = LegendaryTooltipsConfig.getDefinitionColors(
            stack, borderStart, borderEnd, backgroundStart, backgroundEnd, minecraft.level.registryAccess()
         );
         TooltipDecor.setCurrentTooltipBorderStart(frameDefinition.startBorder().get());
         TooltipDecor.setCurrentTooltipBorderEnd(frameDefinition.endBorder().get());
         TooltipDecor.setCurrentTooltipBackgroundStart(frameDefinition.startBackground().get());
         TooltipDecor.setCurrentTooltipBackgroundEnd(frameDefinition.endBackground().get());
         return new ColorExtResult(
            frameDefinition.startBackground().get(),
            frameDefinition.endBackground().get(),
            frameDefinition.startBorder().get(),
            frameDefinition.endBorder().get()
         );
      } else {
         return result;
      }
   }

   public static void onPostTooltipEvent(
      ItemStack stack,
      GuiGraphics graphics,
      int x,
      int y,
      Font font,
      int width,
      int height,
      List<ClientTooltipComponent> components,
      boolean comparison,
      int index
   ) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.level != null && minecraft.level.registryAccess() != null) {
         LegendaryTooltipsConfig.FrameDefinition frameDefinition = LegendaryTooltipsConfig.getInstance()
            .getFrameDefinition(stack, minecraft.level.registryAccess());
         PoseStack poseStack = graphics.pose();
         if (LegendaryTooltipsConfig.getInstance().tooltipShadow.get()) {
            TooltipDecor.drawShadow(poseStack, x, y, width, height);
         }

         TooltipDecor.drawBorder(poseStack, x, y, width, height, stack, components, font, frameDefinition, comparison, index);
      }
   }
}
