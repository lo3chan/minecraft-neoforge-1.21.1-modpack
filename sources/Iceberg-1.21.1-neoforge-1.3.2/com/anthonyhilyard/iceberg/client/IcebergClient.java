package com.anthonyhilyard.iceberg.client;

import com.anthonyhilyard.iceberg.component.TitleBreakComponent;
import com.anthonyhilyard.iceberg.events.client.RenderTooltipEvents;
import com.anthonyhilyard.iceberg.renderer.CustomItemRenderer;
import com.anthonyhilyard.iceberg.services.Services;
import com.mojang.datafixers.util.Either;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public class IcebergClient {
   public static void init() {
      TitleBreakComponent.registerFactory();
      RenderTooltipEvents.GATHER.register(IcebergClient::onGatherComponentsEventEnd);
      Services.getReloadListenerRegistrar()
         .registerListener(
            (Supplier<PreparableReloadListener>)(() -> CustomItemRenderer.getInstance()),
            ResourceLocation.fromNamespaceAndPath("iceberg", "custom_item_renderer")
         );
   }

   public static RenderTooltipEvents.GatherResult onGatherComponentsEventEnd(
      ItemStack itemStack, int screenWidth, int screenHeight, List<Either<FormattedText, TooltipComponent>> tooltipElements, int maxWidth, int index
   ) {
      if (tooltipElements.size() > 1) {
         if (tooltipElements.stream().anyMatch(e -> e.right().filter(c -> c instanceof TitleBreakComponent).isPresent())) {
            return new RenderTooltipEvents.GatherResult(InteractionResult.PASS, maxWidth, tooltipElements);
         }

         for (int i = 0; i < tooltipElements.size(); i++) {
            if (tooltipElements.get(i).left().isPresent()) {
               tooltipElements.add(i + 1, Either.right(new TitleBreakComponent()));
               break;
            }
         }
      }

      return new RenderTooltipEvents.GatherResult(InteractionResult.PASS, maxWidth, tooltipElements);
   }
}
