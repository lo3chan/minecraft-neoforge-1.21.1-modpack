package net.cibernet.alchemancy;

import net.cibernet.alchemancy.client.data.CodexEntryReloadListenener;
import net.cibernet.alchemancy.client.render.AlchemancyCatalystItemRenderer;
import net.cibernet.alchemancy.client.render.AlchemancyCatalystRenderer;
import net.cibernet.alchemancy.client.render.ItemStackHolderRenderer;
import net.cibernet.alchemancy.client.render.RootedItemRenderer;
import net.cibernet.alchemancy.item.InnatePropertyItem;
import net.cibernet.alchemancy.item.components.InfusedPropertiesComponent;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.properties.RotationDataProperty;
import net.cibernet.alchemancy.properties.ToggleableProperty;
import net.cibernet.alchemancy.properties.special.WaywardWarpProperty;
import net.cibernet.alchemancy.properties.voidborn.TelekineticProperty;
import net.cibernet.alchemancy.registries.AlchemancyBlockEntities;
import net.cibernet.alchemancy.registries.AlchemancyEntities;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.cibernet.alchemancy.util.ClientUtil;
import net.cibernet.alchemancy.util.ColorUtils;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions;
import net.neoforged.neoforge.client.event.ModelEvent.RegisterAdditional;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(
   modid = "alchemancy",
   bus = Bus.MOD,
   value = {Dist.CLIENT}
)
@Mod(
   value = "alchemancy",
   dist = {Dist.CLIENT}
)
public class AlchemancyClient {
   public AlchemancyClient(IEventBus modEventBus, ModContainer modContainer) {
      modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
   }

   @SubscribeEvent
   public static void defineModelLayers(RegisterLayerDefinitions event) {
      event.registerLayerDefinition(AlchemancyCatalystRenderer.LAYER_LOCATION, AlchemancyCatalystRenderer::createBodyLayer);
   }

   @SubscribeEvent
   public static void onClientSetup(FMLClientSetupEvent event) {
      BlockEntityRenderers.register((BlockEntityType)AlchemancyBlockEntities.ITEMSTACK_HOLDER.get(), ItemStackHolderRenderer::new);
      BlockEntityRenderers.register((BlockEntityType)AlchemancyBlockEntities.ROOTED_ITEM.get(), RootedItemRenderer::new);
      BlockEntityRenderers.register((BlockEntityType)AlchemancyBlockEntities.ALCHEMANCY_CATALYST.get(), AlchemancyCatalystRenderer::new);
      EntityRenderers.register((EntityType)AlchemancyEntities.INFUSION_FLASK.get(), ThrownItemRenderer::new);
      EntityRenderers.register((EntityType)AlchemancyEntities.ITEM_PROJECTILE.get(), ThrownItemRenderer::new);
      EntityRenderers.register((EntityType)AlchemancyEntities.FALLING_BLOCK.get(), FallingBlockRenderer::new);
      ResourceLocation toggled = ResourceLocation.fromNamespaceAndPath("alchemancy", "toggled");

      for (Item toggleableItem : InnatePropertyItem.TOGGLEABLE_ITEMS) {
         ItemProperties.register(
            toggleableItem, toggled, (stack, level, entity, seed) -> ((ToggleableProperty)AlchemancyProperties.TOGGLEABLE.get()).getData(stack) ? 1.0F : 0.0F
         );
      }

      ItemProperties.register(
         (Item)AlchemancyItems.WAYWARD_MEDALLION.get(),
         ResourceLocation.fromNamespaceAndPath("alchemancy", "bound"),
         (stack, level, entity, seed) -> !WaywardWarpProperty.hasDeathTracker(entity, stack)
               && !((WaywardWarpProperty)AlchemancyProperties.WAYWARD_WARP.value()).getData(stack).hasTarget()
            ? 0.0F
            : 1.0F
      );
      registerItemProperties(
         "active",
         (stack, level, entity, seed) -> entity != null && stack.equals(entity.getUseItem()) ? 1.0F : 0.0F,
         AlchemancyItems.ROCKET_POWERED_HAMMER,
         AlchemancyItems.BARRELS_WARHAMMER,
         AlchemancyItems.DREAMSTEEL_BOW
      );
      registerItemProperties("use_time", (stack, level, entity, seed) -> {
         if (entity == null) {
            return 0.0F;
         } else {
            return entity.getUseItem() != stack ? 0.0F : (stack.getUseDuration(entity) - entity.getUseItemRemainingTicks()) / 20.0F;
         }
      }, AlchemancyItems.DREAMSTEEL_BOW);
      registerItemProperties(
         "flask_contents",
         (stack, level, entity, seed) -> (float)InfusedPropertiesHelper.getInfusedProperties(stack)
            .stream()
            .filter(propertyHolder -> !propertyHolder.is(AlchemancyTags.Properties.IGNORED_BY_INFUSION_FLASK))
            .count(),
         AlchemancyItems.INFUSION_FLASK
      );
   }

   private static void registerItemProperties(String key, ItemPropertyFunction function, DeferredItem<?>... items) {
      ResourceLocation location = ResourceLocation.fromNamespaceAndPath("alchemancy", key);

      for (DeferredItem<?> item : items) {
         ItemProperties.register(item.asItem(), location, function);
      }
   }

   @SubscribeEvent
   public static void initItemColors(net.neoforged.neoforge.client.event.RegisterColorHandlersEvent.Item event) {
      event.register(
         (stack, tintIndex) -> {
            int[] colors = ((InfusedPropertiesComponent)stack.getOrDefault(AlchemancyItems.Components.STORED_PROPERTIES, InfusedPropertiesComponent.EMPTY))
               .properties()
               .stream()
               .map(propertyHolder -> ((Property)propertyHolder.value()).getColor(stack))
               .mapToInt(Integer::intValue)
               .toArray();
            return colors.length == 0 ? -1 : ARGB32.color(255, ColorUtils.interpolateColorsAndWait(1.0F, 2.0F, colors));
         },
         new ItemLike[]{AlchemancyItems.PROPERTY_CAPSULE}
      );
      event.register(
         (stack, tintIndex) -> {
            if (tintIndex != 1) {
               return -1;
            } else {
               int[] colors = ((InfusedPropertiesComponent)stack.getOrDefault(AlchemancyItems.Components.INFUSED_PROPERTIES, InfusedPropertiesComponent.EMPTY))
                  .properties()
                  .stream()
                  .filter(p -> !p.is(AlchemancyTags.Properties.SLOTLESS))
                  .map(propertyHolder -> ((Property)propertyHolder.value()).getColor(stack))
                  .mapToInt(Integer::intValue)
                  .toArray();
               return colors.length == 0 ? -1 : ARGB32.color(255, ColorUtils.interpolateColorsAndWait(1.0F, 2.0F, colors));
            }
         },
         new ItemLike[]{AlchemancyItems.IRON_RING, AlchemancyItems.SPARKLING_BAND}
      );
      event.register(
         (stack, tintIndex) -> {
            if (tintIndex != 1) {
               return -1;
            } else {
               int[] colors = ((InfusedPropertiesComponent)stack.getOrDefault(AlchemancyItems.Components.INFUSED_PROPERTIES, InfusedPropertiesComponent.EMPTY))
                  .properties()
                  .stream()
                  .filter(p -> !p.is(AlchemancyTags.Properties.IGNORED_BY_INFUSION_FLASK))
                  .map(propertyHolder -> ((Property)propertyHolder.value()).getColor(stack))
                  .mapToInt(Integer::intValue)
                  .toArray();
               return colors.length == 0 ? -1 : ARGB32.color(255, ColorUtils.interpolateColorsAndWait(1.0F, 2.0F, colors));
            }
         },
         new ItemLike[]{AlchemancyItems.INFUSION_FLASK}
      );
      event.register(
         (stack, tintIndex) -> tintIndex == 1 ? ((Property)AlchemancyProperties.TINTED_LENS.value()).getColor(stack) : -1,
         new ItemLike[]{AlchemancyItems.TINTED_GLASSES}
      );
      event.register(
         (stack, tintIndex) -> tintIndex == 1 ? ((Property)AlchemancyProperties.AWAKENED.value()).getColor(stack) : -1,
         new ItemLike[]{
            AlchemancyItems.PROPERTY_VISOR,
            AlchemancyItems.DREAMSTEEL_INGOT,
            AlchemancyItems.DREAMSTEEL_NUGGET,
            AlchemancyItems.DREAMSTEEL_PICKAXE,
            AlchemancyItems.DREAMSTEEL_AXE,
            AlchemancyItems.DREAMSTEEL_SHOVEL,
            AlchemancyItems.DREAMSTEEL_HOE,
            AlchemancyItems.DREAMSTEEL_SWORD,
            AlchemancyItems.DREAMSTEEL_BOW
         }
      );
      event.register(
         (stack, tintIndex) -> tintIndex == 0
            ? (
               WaywardWarpProperty.hasDeathTracker(ClientUtil.getLocalPlayer(), stack)
                  ? ((RotationDataProperty)AlchemancyProperties.DEATH_TRACKER.get()).getColor(stack)
                  : ((WaywardWarpProperty)AlchemancyProperties.WAYWARD_WARP.value()).getColor(stack)
            )
            : -1,
         new ItemLike[]{AlchemancyItems.WAYWARD_MEDALLION}
      );
      event.register(
         (stack, tintIndex) -> tintIndex == 0 ? ((Property)AlchemancyProperties.PARADOXICAL.value()).getColor(stack) : -1,
         new ItemLike[]{AlchemancyItems.PARADOX_PEARL}
      );
      event.register(
         (stack, tintIndex) -> tintIndex == 0 ? ARGB32.color(255, ((TelekineticProperty)AlchemancyProperties.KINETIC_GRAB.value()).getColor(stack)) : -1,
         new ItemLike[]{AlchemancyItems.TELEKINETIC_GLOVE}
      );
   }

   @SubscribeEvent
   public static void initClientExtensions(RegisterClientExtensionsEvent event) {
      event.registerItem(new IClientItemExtensions() {
         @NotNull
         public BlockEntityWithoutLevelRenderer getCustomRenderer() {
            return AlchemancyCatalystItemRenderer.instance;
         }
      }, new Item[]{(Item)AlchemancyItems.ALCHEMANCY_CATALYST.get()});
   }

   @SubscribeEvent
   public static void initAdditionalModels(RegisterAdditional event) {
      event.register(AlchemancyCatalystItemRenderer.FRAME_LOCATION);
   }

   @SubscribeEvent
   public static void registerReloadListeners(RegisterClientReloadListenersEvent event) {
      event.registerReloadListener(CodexEntryReloadListenener.INSTANCE);
   }
}
