package com.anthonyhilyard.iceberg.neoforge.client;

import com.anthonyhilyard.iceberg.Iceberg;
import com.anthonyhilyard.iceberg.events.client.RegisterTooltipComponentFactoryEvent;
import com.anthonyhilyard.iceberg.events.client.RenderTooltipEvents;
import com.anthonyhilyard.iceberg.events.common.ConfigEvents;
import com.anthonyhilyard.iceberg.util.Tooltips;
import com.mojang.datafixers.util.Either;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.TooltipContext;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent.Loading;
import net.neoforged.fml.event.config.ModConfigEvent.Reloading;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent.Color;
import net.neoforged.neoforge.client.event.RenderTooltipEvent.GatherComponents;
import net.neoforged.neoforge.client.event.RenderTooltipEvent.Pre;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

public class IcebergNeoForgeClient {
   private static boolean vanillaGatherEvent = false;

   public static class ModEvents {
      public static RenderTooltipEvents.GatherResult tooltipGatherEvent(
         ItemStack itemStack, int screenWidth, int screenHeight, List<Either<FormattedText, TooltipComponent>> tooltipElements, int maxWidth, int index
      ) {
         if (!IcebergNeoForgeClient.vanillaGatherEvent) {
            GatherComponents event = new GatherComponents(itemStack, screenWidth, screenHeight, tooltipElements, maxWidth);
            NeoForge.EVENT_BUS.post(event);
            return new RenderTooltipEvents.GatherResult(
               event.isCanceled() ? InteractionResult.FAIL : InteractionResult.PASS, event.getMaxWidth(), event.getTooltipElements()
            );
         } else {
            return new RenderTooltipEvents.GatherResult(InteractionResult.PASS, maxWidth, tooltipElements);
         }
      }

      @SubscribeEvent(
         priority = EventPriority.LOWEST
      )
      public static void registerTooltipComponentsEvent(RegisterClientTooltipComponentFactoriesEvent event) {
         for (Class<? extends TooltipComponent> type : RegisterTooltipComponentFactoryEvent.EVENT.getListenerTypes().keySet()) {
            event.register(type, RegisterTooltipComponentFactoryEvent.EVENT.invoker()::getComponent);
         }

         try {
            Field factoriesField = RegisterClientTooltipComponentFactoriesEvent.class.getDeclaredField("factories");
            factoriesField.setAccessible(true);
            Map<Class<? extends TooltipComponent>, Function<TooltipComponent, ClientTooltipComponent>> factories = (Map<Class<? extends TooltipComponent>, Function<TooltipComponent, ClientTooltipComponent>>)factoriesField.get(
               event
            );

            for (Class<? extends TooltipComponent> type : factories.keySet()) {
               Iceberg.LOGGER.debug("Registering tooltip component: " + type.getName());
               RegisterTooltipComponentFactoryEvent.EVENT.register(type, data -> {
                  try {
                     return factories.get(type).apply(data);
                  } catch (Exception var4x) {
                     return null;
                  }
               });
            }
         } catch (Exception var5) {
            Iceberg.LOGGER.warn("Failed to register tooltip components: " + var5.getMessage());
         }
      }

      @SubscribeEvent(
         priority = EventPriority.HIGH
      )
      public static void configLoadEvent(Loading event) {
         ConfigEvents.LOAD.invoker().onLoad(event.getConfig().getModId());
      }

      @SubscribeEvent(
         priority = EventPriority.HIGH
      )
      public static void configReloadEvent(Reloading event) {
         ConfigEvents.RELOAD.invoker().onReload(event.getConfig().getModId());
      }
   }

   public static class NeoForgeEvents {
      @SubscribeEvent(
         priority = EventPriority.HIGH
      )
      public static void itemTooltipEvent(ItemTooltipEvent event) {
         Minecraft minecraft = Minecraft.getInstance();
         com.anthonyhilyard.iceberg.events.client.ItemTooltipEvent.EVENT
            .invoker()
            .onItemTooltip(event.getItemStack(), TooltipContext.of(minecraft.level), event.getFlags(), event.getToolTip());
      }

      @SubscribeEvent(
         priority = EventPriority.HIGH
      )
      public static void tooltipPreRenderEvent(Pre event) {
         Tooltips.TooltipRenderContext context = Tooltips.getCurrentRenderContext();
         RenderTooltipEvents.PreExtResult result = RenderTooltipEvents.PREEXT
            .invoker()
            .onPre(
               event.getItemStack(),
               event.getGraphics(),
               event.getX(),
               event.getY(),
               event.getScreenWidth(),
               event.getScreenHeight(),
               event.getFont(),
               event.getComponents(),
               event.getTooltipPositioner(),
               context.comparison(),
               context.index()
            );
         event.setFont(result.font());
         event.setX(result.x());
         event.setY(result.y());
         event.setCanceled(result.result() != InteractionResult.PASS);
      }

      @SubscribeEvent(
         priority = EventPriority.HIGHEST
      )
      public static void tooltipColorEvent(Color event) {
         Tooltips.TooltipRenderContext context = Tooltips.getCurrentRenderContext();
         RenderTooltipEvents.ColorExtResult result = RenderTooltipEvents.COLOREXT
            .invoker()
            .onColor(
               event.getItemStack(),
               event.getGraphics(),
               event.getX(),
               event.getY(),
               event.getFont(),
               event.getBackgroundStart(),
               event.getBackgroundEnd(),
               event.getBorderStart(),
               event.getBorderEnd(),
               event.getComponents(),
               context.comparison(),
               context.index()
            );
         event.setBackgroundStart(result.backgroundStart());
         event.setBackgroundEnd(result.backgroundEnd());
         event.setBorderStart(result.borderStart());
         event.setBorderEnd(result.borderEnd());
      }

      @SubscribeEvent(
         priority = EventPriority.HIGH
      )
      public static void tooltipGatherEvent(GatherComponents event) {
         IcebergNeoForgeClient.vanillaGatherEvent = true;
         Tooltips.TooltipRenderContext context = Tooltips.getCurrentRenderContext();
         RenderTooltipEvents.GatherResult result = RenderTooltipEvents.GATHER
            .invoker()
            .onGather(event.getItemStack(), event.getScreenWidth(), event.getScreenHeight(), event.getTooltipElements(), event.getMaxWidth(), context.index());
         event.setMaxWidth(result.maxWidth());
         event.setCanceled(result.result() != InteractionResult.PASS);
         IcebergNeoForgeClient.vanillaGatherEvent = false;
      }
   }
}
