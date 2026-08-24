package com.aetherteam.aether.client;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.client.event.listeners.AudioListener;
import com.aetherteam.aether.client.event.listeners.DimensionClientListener;
import com.aetherteam.aether.client.event.listeners.GuiListener;
import com.aetherteam.aether.client.event.listeners.LevelClientListener;
import com.aetherteam.aether.client.event.listeners.MenuListener;
import com.aetherteam.aether.client.event.listeners.abilities.AccessoryAbilityClientListener;
import com.aetherteam.aether.client.event.listeners.capability.AetherPlayerClientListener;
import com.aetherteam.aether.client.gui.screen.inventory.SunAltarScreen;
import com.aetherteam.aether.client.gui.screen.menu.AetherReceivingLevelScreen;
import com.aetherteam.aether.client.particle.AetherParticleTypes;
import com.aetherteam.aether.client.renderer.AetherOverlays;
import com.aetherteam.aether.client.renderer.AetherRenderers;
import com.aetherteam.aether.client.renderer.level.AetherRenderEffects;
import com.aetherteam.aether.data.resources.registries.AetherDimensions;
import com.aetherteam.aether.entity.AetherEntityTypes;
import com.aetherteam.aether.event.hooks.AbilityHooks;
import com.aetherteam.aether.inventory.menu.AetherMenuTypes;
import com.aetherteam.aether.inventory.menu.LoreBookMenu;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.perk.CustomizationsOptions;
import com.aetherteam.nitrogen.event.listeners.TooltipListeners;
import com.google.common.reflect.Reflection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterDimensionTransitionScreenEvent;
import net.neoforged.neoforge.client.event.RegisterEntitySpectatorShadersEvent;
import net.neoforged.neoforge.common.NeoForge;

public class AetherClient {
   public static void clientInit(IEventBus bus) {
      bus.addListener(AetherClient::clientSetup);
      bus.addListener(AetherClient::registerSpectatorShaders);
      bus.addListener(AetherClient::registerDimensionTransitionScreens);
      eventSetup(bus);
   }

   public static void clientSetup(FMLClientSetupEvent event) {
      Reflection.initialize(new Class[]{CustomizationsOptions.class});
      AetherRenderers.registerAccessoryRenderers();
      event.enqueueWork(() -> {
         AetherAtlases.registerTreasureChestAtlases();
         AetherAtlases.registerWoodTypeAtlases();
         registerItemModelProperties();
         registerTooltipOverrides();
      });
      registerLoreOverrides();
   }

   public static void registerItemModelProperties() {
      ItemProperties.register(
         (Item)AetherItems.PHOENIX_BOW.get(),
         ResourceLocation.withDefaultNamespace("pulling"),
         (stack, world, living, i) -> living != null && living.isUsingItem() && living.getUseItem() == stack ? 1.0F : 0.0F
      );
      ItemProperties.register(
         (Item)AetherItems.PHOENIX_BOW.get(),
         ResourceLocation.withDefaultNamespace("pull"),
         (stack, world, living, i) -> living != null
            ? (living.getUseItem() != stack ? 0.0F : (stack.getUseDuration(living) - living.getUseItemRemainingTicks()) / 20.0F)
            : 0.0F
      );
      ItemProperties.register(
         (Item)AetherItems.CANDY_CANE_SWORD.get(),
         ResourceLocation.fromNamespaceAndPath("aether", "named"),
         (stack, world, living, i) -> stack.getHoverName().getString().equalsIgnoreCase("green candy cane sword") ? 1.0F : 0.0F
      );
      ItemProperties.register(
         (Item)AetherItems.HAMMER_OF_KINGBDOGZ.get(),
         ResourceLocation.fromNamespaceAndPath("aether", "named"),
         (stack, world, living, i) -> stack.getHoverName().getString().equalsIgnoreCase("hammer of jeb") ? 1.0F : 0.0F
      );
   }

   public static void registerTooltipOverrides() {
      TooltipListeners.PREDICATES
         .put(
            AetherItems.BLUE_GUMMY_SWET,
            (player, stack, components, context, component) -> (Component)(AetherConfig.SERVER.healing_gummy_swets.get()
                  && component.getContents() instanceof TranslatableContents contents
                  && contents.getKey().endsWith(".1")
               ? Component.translatable(contents.getKey() + ".health")
               : component)
         );
      TooltipListeners.PREDICATES
         .put(
            AetherItems.GOLDEN_GUMMY_SWET,
            (player, stack, components, context, component) -> (Component)(AetherConfig.SERVER.healing_gummy_swets.get()
                  && component.getContents() instanceof TranslatableContents contents
                  && contents.getKey().endsWith(".1")
               ? Component.translatable(contents.getKey() + ".health")
               : component)
         );
      TooltipListeners.PREDICATES
         .put(
            AetherItems.LIFE_SHARD,
            (player, stack, components, context, component) -> (Component)(component.getContents() instanceof TranslatableContents contents
                  && contents.getKey().endsWith(".1")
               ? Component.translatable(contents.getKey(), new Object[]{AetherConfig.SERVER.maximum_life_shards.get()})
               : component)
         );
   }

   public static void registerLoreOverrides() {
      LoreBookMenu.addLoreEntryOverride(
         registryAccess -> stack -> stack.is((Item)AetherItems.HAMMER_OF_KINGBDOGZ.get()) && stack.getHoverName().getString().equalsIgnoreCase("hammer of jeb"),
         "lore.item.aether.hammer_of_jeb"
      );
      LoreBookMenu.addLoreEntryOverride(
         registryAccess -> stack -> ItemStack.isSameItemSameComponents(
            stack, AetherItems.createSwetBannerItemStack(registryAccess.registryOrThrow(Registries.BANNER_PATTERN).asLookup())
         ),
         "lore.item.aether.swet_banner"
      );
   }

   public static void eventSetup(IEventBus neoBus) {
      IEventBus bus = NeoForge.EVENT_BUS;
      AccessoryAbilityClientListener.listen(bus);
      AetherPlayerClientListener.listen(bus);
      AudioListener.listen(bus);
      DimensionClientListener.listen(bus);
      GuiListener.listen(bus);
      LevelClientListener.listen(bus);
      MenuListener.listen(bus);
      bus.addListener(event -> AbilityHooks.ToolHooks.resetDebuffToolsState());
      neoBus.addListener(AetherMenuTypes::registerMenuScreens);
      neoBus.addListener(AetherColorResolvers::registerBlockColor);
      neoBus.addListener(AetherColorResolvers::registerItemColor);
      neoBus.addListener(AetherKeys::registerKeyMappings);
      neoBus.addListener(AetherRecipeCategories::registerRecipeCategories);
      neoBus.addListener(AetherParticleTypes::registerParticleFactories);
      neoBus.addListener(AetherOverlays::registerOverlays);
      neoBus.addListener(AetherRenderers::registerEntityRenderers);
      neoBus.addListener(AetherRenderers::registerLayerDefinitions);
      neoBus.addListener(AetherRenderers::addEntityLayers);
      neoBus.addListener(AetherRenderers::bakeModels);
      neoBus.addListener(AetherRenderEffects::registerRenderEffects);
   }

   public static void registerSpectatorShaders(RegisterEntitySpectatorShadersEvent event) {
      event.register((EntityType)AetherEntityTypes.SUN_SPIRIT.get(), ResourceLocation.fromNamespaceAndPath("aether", "shaders/post/sun_spirit.json"));
   }

   public static void registerDimensionTransitionScreens(RegisterDimensionTransitionScreenEvent event) {
      event.registerIncomingEffect(AetherDimensions.AETHER_LEVEL, AetherReceivingLevelScreen::new);
      event.registerOutgoingEffect(AetherDimensions.AETHER_LEVEL, AetherReceivingLevelScreen::new);
   }

   public static void setToSunAltarScreen(Component name, int timeScale) {
      Minecraft.getInstance().setScreen(new SunAltarScreen(name, timeScale));
   }
}
