package cn.foggyhillside.ends_delight;

import cn.foggyhillside.ends_delight.client.renderer.EndStoveRenderer;
import cn.foggyhillside.ends_delight.registry.ModBiomeFeatures;
import cn.foggyhillside.ends_delight.registry.ModBlockEntityTypes;
import cn.foggyhillside.ends_delight.registry.ModBlocks;
import cn.foggyhillside.ends_delight.registry.ModCreativeTab;
import cn.foggyhillside.ends_delight.registry.ModItems;
import cn.foggyhillside.ends_delight.registry.ModLootModifiers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;

@Mod("ends_delight")
public class EndsDelight {
   public static final String MODID = "ends_delight";

   public EndsDelight(IEventBus modEventBus, ModContainer modContainer) {
      ModBlockEntityTypes.TILES.register(modEventBus);
      ModBlocks.BLOCKS.register(modEventBus);
      ModBiomeFeatures.FEATURES.register(modEventBus);
      ModItems.ITEMS.register(modEventBus);
      ModCreativeTab.CREATIVE_MODE_TABS.register(modEventBus);
      ModLootModifiers.LOOT_MODIFIERS.register(modEventBus);
      modContainer.registerConfig(Type.COMMON, EDCommonConfigs.SPEC);
   }

   @EventBusSubscriber(
      modid = "ends_delight",
      value = {Dist.CLIENT}
   )
   public static class ClientSetupEvents {
      @SubscribeEvent
      public static void onRegisterRenderers(RegisterRenderers event) {
         event.registerBlockEntityRenderer(ModBlockEntityTypes.END_STOVE.get(), EndStoveRenderer::new);
      }
   }
}
