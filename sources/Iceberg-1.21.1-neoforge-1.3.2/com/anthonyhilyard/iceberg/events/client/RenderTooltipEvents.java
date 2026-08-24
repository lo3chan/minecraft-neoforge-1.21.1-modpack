package com.anthonyhilyard.iceberg.events.client;

import com.anthonyhilyard.iceberg.events.Event;
import com.anthonyhilyard.iceberg.events.EventFactory;
import com.mojang.datafixers.util.Either;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public final class RenderTooltipEvents {
   public static final Event<RenderTooltipEvents.Gather> GATHER = EventFactory.create(
      RenderTooltipEvents.Gather.class, callbacks -> (itemStack, screenWidth, screenHeight, tooltipElements, maxWidth, index) -> {
         RenderTooltipEvents.GatherResult result = new RenderTooltipEvents.GatherResult(InteractionResult.PASS, maxWidth, tooltipElements);

         for (RenderTooltipEvents.Gather callback : callbacks) {
            result = callback.onGather(itemStack, screenWidth, screenHeight, result.tooltipElements, result.maxWidth, index);
            if (result.result != InteractionResult.PASS) {
               return result;
            }
         }

         return result;
      }
   );
   public static final Event<RenderTooltipEvents.PreExt> PREEXT = EventFactory.create(
      RenderTooltipEvents.PreExt.class,
      callbacks -> (stack, graphics, x, y, screenWidth, screenHeight, font, components, positioner, comparison, index) -> {
         RenderTooltipEvents.PreExtResult result = new RenderTooltipEvents.PreExtResult(InteractionResult.PASS, x, y, screenWidth, screenHeight, font);

         for (RenderTooltipEvents.PreExt callback : callbacks) {
            result = callback.onPre(
               stack, graphics, result.x, result.y, result.screenWidth, result.screenHeight, result.font, components, positioner, comparison, index
            );
            if (result.result != InteractionResult.PASS) {
               return result;
            }
         }

         return result;
      }
   );
   public static final Event<RenderTooltipEvents.ColorExt> COLOREXT = EventFactory.create(
      RenderTooltipEvents.ColorExt.class,
      callbacks -> (stack, graphics, x, y, font, backgroundStart, backgroundEnd, borderStart, borderEnd, components, comparison, index) -> {
         RenderTooltipEvents.ColorExtResult result = new RenderTooltipEvents.ColorExtResult(backgroundStart, backgroundEnd, borderStart, borderEnd);

         for (RenderTooltipEvents.ColorExt callback : callbacks) {
            result = callback.onColor(
               stack, graphics, x, y, font, result.backgroundStart, result.backgroundEnd, result.borderStart, result.borderEnd, components, comparison, index
            );
         }

         return result;
      }
   );
   public static final Event<RenderTooltipEvents.PostExt> POSTEXT = EventFactory.create(
      RenderTooltipEvents.PostExt.class, callbacks -> (stack, graphics, x, y, font, width, height, components, comparison, index) -> {
         for (RenderTooltipEvents.PostExt callback : callbacks) {
            callback.onPost(stack, graphics, x, y, font, width, height, components, comparison, index);
         }
      }
   );

   @FunctionalInterface
   public interface ColorExt {
      RenderTooltipEvents.ColorExtResult onColor(
         ItemStack var1,
         GuiGraphics var2,
         int var3,
         int var4,
         Font var5,
         int var6,
         int var7,
         int var8,
         int var9,
         List<ClientTooltipComponent> var10,
         boolean var11,
         int var12
      );
   }

   public record ColorExtResult(int backgroundStart, int backgroundEnd, int borderStart, int borderEnd) {
   }

   @FunctionalInterface
   public interface Gather {
      RenderTooltipEvents.GatherResult onGather(ItemStack var1, int var2, int var3, List<Either<FormattedText, TooltipComponent>> var4, int var5, int var6);
   }

   public record GatherResult(InteractionResult result, int maxWidth, List<Either<FormattedText, TooltipComponent>> tooltipElements) {
   }

   @FunctionalInterface
   public interface PostExt {
      void onPost(
         ItemStack var1, GuiGraphics var2, int var3, int var4, Font var5, int var6, int var7, List<ClientTooltipComponent> var8, boolean var9, int var10
      );
   }

   @FunctionalInterface
   public interface PreExt {
      RenderTooltipEvents.PreExtResult onPre(
         ItemStack var1,
         GuiGraphics var2,
         int var3,
         int var4,
         int var5,
         int var6,
         Font var7,
         List<ClientTooltipComponent> var8,
         ClientTooltipPositioner var9,
         boolean var10,
         int var11
      );
   }

   public record PreExtResult(InteractionResult result, int x, int y, int screenWidth, int screenHeight, Font font) {
   }
}
