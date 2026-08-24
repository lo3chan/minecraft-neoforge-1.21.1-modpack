package net.conczin.immersive_gateways.neoforge;

import java.util.function.Consumer;
import net.conczin.immersive_gateways.BlockEntityTypes;
import net.conczin.immersive_gateways.Blocks;
import net.conczin.immersive_gateways.Common;
import net.conczin.immersive_gateways.Items;
import net.conczin.immersive_gateways.Sounds;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(
   modid = "immersive_gateways",
   bus = Bus.MOD
)
public class CommonNeoForgeModBus {
   private static <T> void registerHelper(RegisterEvent event, Registry<T> register, Consumer<Common.RegisterHelper<T>> consumer) {
      event.register(register.key(), registry -> consumer.accept(registry::register));
   }

   @SubscribeEvent
   public static void register(RegisterEvent event) {
      registerHelper(event, BuiltInRegistries.ITEM, Items::registerItems);
      registerHelper(event, BuiltInRegistries.BLOCK, Blocks::registerBlocks);
      registerHelper(event, BuiltInRegistries.SOUND_EVENT, Sounds::registerSounds);
      if (event.getRegistryKey() == Registries.BLOCK_ENTITY_TYPE) {
         event.register(Registries.BLOCK_ENTITY_TYPE, helper -> BlockEntityTypes.register((name, factory, block) -> {
            BlockEntityType<?> build = Builder.of(factory::create, new Block[]{block}).build(null);
            helper.register(name, build);
            return build;
         }));
      }
   }

   @SubscribeEvent
   static void onCommonSetup(FMLCommonSetupEvent event) {
      Common.init();
   }
}
