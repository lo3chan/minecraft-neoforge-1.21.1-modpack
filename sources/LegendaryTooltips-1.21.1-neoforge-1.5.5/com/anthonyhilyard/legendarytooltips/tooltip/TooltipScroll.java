package com.anthonyhilyard.legendarytooltips.tooltip;

import com.anthonyhilyard.iceberg.util.Easing;
import com.anthonyhilyard.iceberg.util.Easing.EasingDirection;
import com.anthonyhilyard.iceberg.util.Easing.EasingType;
import com.anthonyhilyard.legendarytooltips.config.LegendaryTooltipsConfig;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.DeltaTracker;

public final class TooltipScroll {
   private static final float scrollDuration = 0.15F;
   private static final float overScroll = 3.0F;
   private static Map<Integer, TooltipScroll.ScrollData> dataMap = Maps.newHashMap();

   private TooltipScroll() {
   }

   public static void setScrollBounds(int index, float top, float bottom) {
      TooltipScroll.ScrollData data = dataMap.computeIfAbsent(index, k -> new TooltipScroll.ScrollData());
      data.scrollTop = top;
      data.scrollBottom = bottom;
   }

   public static void setContentHeight(int index, float height) {
      dataMap.computeIfAbsent(index, k -> new TooltipScroll.ScrollData()).contentHeight = height;
   }

   public static void setTooltipVisible(int index, boolean visible) {
      dataMap.computeIfAbsent(index, k -> new TooltipScroll.ScrollData()).tooltipVisible = visible;
   }

   public static boolean isTooltipVisible(int index) {
      return dataMap.computeIfAbsent(index, k -> new TooltipScroll.ScrollData()).tooltipVisible;
   }

   public static float getScrollTop(int index) {
      return dataMap.computeIfAbsent(index, k -> new TooltipScroll.ScrollData()).scrollTop;
   }

   public static float getScrollBottom(int index) {
      return dataMap.computeIfAbsent(index, k -> new TooltipScroll.ScrollData()).scrollBottom;
   }

   public static float currentScroll(int index) {
      return dataMap.computeIfAbsent(index, k -> new TooltipScroll.ScrollData()).scrollOffset;
   }

   public static void reset(int index) {
      dataMap.put(index, new TooltipScroll.ScrollData());
   }

   public static void resetAll() {
      dataMap.clear();
   }

   public static void scroll(float amount) {
      for (TooltipScroll.ScrollData data : dataMap.values()) {
         data.prevTargetOffset = data.scrollOffset;
         data.targetOffset = data.targetOffset + amount * LegendaryTooltipsConfig.getInstance().scrollSpeed.get().floatValue();
         data.targetOffset = Math.clamp(data.targetOffset, -3.0F, data.getScrollableHeight() + 3.0F);
         data.prevTargetOffset = Math.clamp(data.prevTargetOffset, 0.0F, data.contentHeight);
         data.scrollTimer = 0.0F;
      }
   }

   public static void onRenderTick(DeltaTracker tracker) {
      for (TooltipScroll.ScrollData data : dataMap.values()) {
         if (data.targetOffset != data.scrollOffset) {
            data.scrollTimer = data.scrollTimer + tracker.getRealtimeDeltaTicks() * 0.05F;
            if (data.scrollTimer < 0.15F) {
               float alpha = data.scrollTimer / 0.15F;
               data.scrollOffset = Easing.Ease(data.prevTargetOffset, data.targetOffset, alpha, EasingType.Quad, EasingDirection.Out);
            } else {
               data.scrollOffset = data.targetOffset;
               data.scrollTimer = 0.0F;
               data.prevTargetOffset = data.targetOffset;
               data.targetOffset = Math.clamp(data.targetOffset, 0.0F, data.getScrollableHeight());
            }
         }
      }
   }

   private static class ScrollData {
      public float scrollOffset = 0.0F;
      public float targetOffset = 0.0F;
      public float prevTargetOffset = 0.0F;
      public float scrollTimer = 0.0F;
      public float scrollTop = 0.0F;
      public float scrollBottom = 0.0F;
      public float contentHeight = 0.0F;
      public boolean tooltipVisible = false;

      public float getScrollableHeight() {
         return Math.max(this.contentHeight - Math.max(this.scrollBottom - this.scrollTop, 0.0F), 0.0F);
      }
   }
}
