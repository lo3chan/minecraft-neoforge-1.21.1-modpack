package net.cibernet.alchemancy;

import com.mojang.logging.LogUtils;
import java.util.Arrays;
import net.cibernet.alchemancy.commands.AlchemancyCommands;
import net.cibernet.alchemancy.registries.AlchemancyBlockEntities;
import net.cibernet.alchemancy.registries.AlchemancyBlocks;
import net.cibernet.alchemancy.registries.AlchemancyCreativeTabs;
import net.cibernet.alchemancy.registries.AlchemancyCriteriaTriggers;
import net.cibernet.alchemancy.registries.AlchemancyDataAttachments;
import net.cibernet.alchemancy.registries.AlchemancyEntities;
import net.cibernet.alchemancy.registries.AlchemancyIngredients;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyMobEffects;
import net.cibernet.alchemancy.registries.AlchemancyParticles;
import net.cibernet.alchemancy.registries.AlchemancyPoiTypes;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyRecipeTypes;
import net.cibernet.alchemancy.registries.AlchemancySoundEvents;
import net.cibernet.alchemancy.registries.AlchemancyWorldGen;
import net.cibernet.alchemancy.util.MixinUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod("alchemancy")
public class Alchemancy {
   public static final String MODID = "alchemancy";
   public static final Logger LOGGER = LogUtils.getLogger();

   public static ResourceLocation resourceLocation(String key) {
      return ResourceLocation.fromNamespaceAndPath("alchemancy", key);
   }

   public Alchemancy(IEventBus modEventBus, ModContainer modContainer) {
      modEventBus.addListener(this::commonSetup);
      AlchemancyBlocks.REGISTRY.register(modEventBus);
      AlchemancyBlockEntities.REGISTRY.register(modEventBus);
      AlchemancyItems.REGISTRY.register(modEventBus);
      AlchemancyItems.Materials.ARMOR_MATERIAL_REGISTRY.register(modEventBus);
      AlchemancyItems.Components.REGISTRY.register(modEventBus);
      AlchemancyEntities.REGISTRY.register(modEventBus);
      AlchemancyProperties.REGISTRY.register(modEventBus);
      AlchemancyProperties.Modifiers.REGISTRY.register(modEventBus);
      AlchemancyCreativeTabs.REGISTRY.register(modEventBus);
      AlchemancyRecipeTypes.REGISTRY.register(modEventBus);
      AlchemancyRecipeTypes.Serializers.REGISTRY.register(modEventBus);
      AlchemancyParticles.REGISTRY.register(modEventBus);
      AlchemancyWorldGen.Features.REGISTRY.register(modEventBus);
      AlchemancyIngredients.REGISTRY.register(modEventBus);
      AlchemancyCriteriaTriggers.REGISTRY.register(modEventBus);
      AlchemancySoundEvents.REGISTRY.register(modEventBus);
      AlchemancyPoiTypes.REGISTRY.register(modEventBus);
      AlchemancyDataAttachments.REGISTRY.register(modEventBus);
      AlchemancyCommands.Arguments.REGISTRY.register(modEventBus);
      AlchemancyMobEffects.REGISTRY.register(modEventBus);
      modContainer.registerConfig(Type.SERVER, AlchemancyConfig.Server.SPEC);
      modContainer.registerConfig(Type.CLIENT, AlchemancyConfig.Client.SPEC);
   }

   private void commonSetup(FMLCommonSetupEvent event) {
      registerModifiableDataComponent(
         DataComponents.TOOL,
         DataComponents.MAX_DAMAGE,
         DataComponents.MAX_STACK_SIZE,
         DataComponents.FOOD,
         DataComponents.FIREWORKS,
         DataComponents.FIRE_RESISTANT,
         DataComponents.UNBREAKABLE,
         (DataComponentType)AlchemancyItems.Components.INFUSION_SLOTS.get(),
         (DataComponentType)AlchemancyItems.Components.FE_STORAGE.get()
      );
   }

   public static void registerModifiableDataComponent(Holder<DataComponentType<?>>... componentTypeHolder) {
      MixinUtils.registerModifiableDataComponent(Arrays.stream(componentTypeHolder).map(holder -> holder.getKey().location()).toArray(ResourceLocation[]::new));
   }

   public static void registerModifiableDataComponent(DataComponentType<?>... componentType) {
      MixinUtils.registerModifiableDataComponent(
         Arrays.stream(componentType).map(BuiltInRegistries.DATA_COMPONENT_TYPE::getKey).toArray(ResourceLocation[]::new)
      );
   }
}
