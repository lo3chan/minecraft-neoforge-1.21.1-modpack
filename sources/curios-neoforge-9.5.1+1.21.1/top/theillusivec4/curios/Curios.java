package top.theillusivec4.curios;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.resources.PlayerSkin.Model;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.AddLayers;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.extensions.RegisterCuriosExtensionsEvent;
import top.theillusivec4.curios.api.type.ISlotType;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.client.ClientEventHandler;
import top.theillusivec4.curios.client.CuriosClientConfig;
import top.theillusivec4.curios.client.IconHelper;
import top.theillusivec4.curios.client.KeyRegistry;
import top.theillusivec4.curios.client.gui.CuriosScreen;
import top.theillusivec4.curios.client.gui.GuiEventHandler;
import top.theillusivec4.curios.client.render.CuriosLayer;
import top.theillusivec4.curios.common.CuriosConfig;
import top.theillusivec4.curios.common.CuriosHelper;
import top.theillusivec4.curios.common.CuriosRegistry;
import top.theillusivec4.curios.common.capability.CurioInventoryCapability;
import top.theillusivec4.curios.common.capability.CurioItemHandler;
import top.theillusivec4.curios.common.capability.ItemizedCurioCapability;
import top.theillusivec4.curios.common.data.CuriosEntityManager;
import top.theillusivec4.curios.common.data.CuriosSlotManager;
import top.theillusivec4.curios.common.event.CuriosEventHandler;
import top.theillusivec4.curios.common.integration.CuriosIntegrations;
import top.theillusivec4.curios.common.network.NetworkHandler;
import top.theillusivec4.curios.common.slottype.LegacySlotManager;
import top.theillusivec4.curios.mixin.CuriosImplMixinHooks;
import top.theillusivec4.curios.server.SlotHelper;
import top.theillusivec4.curios.server.command.CurioArgumentType;
import top.theillusivec4.curios.server.command.CuriosCommand;
import top.theillusivec4.curios.server.command.CuriosSelectorOptions;

@Mod("curios")
public class Curios {
   public Curios(IEventBus eventBus, ModContainer modContainer) {
      CuriosRegistry.init(eventBus);
      CuriosIntegrations.setup(eventBus);
      eventBus.addListener(this::setup);
      eventBus.addListener(this::process);
      eventBus.addListener(this::registerCaps);
      eventBus.addListener(this::registerPayloadHandler);
      NeoForge.EVENT_BUS.addListener(this::serverAboutToStart);
      NeoForge.EVENT_BUS.addListener(this::serverStopped);
      NeoForge.EVENT_BUS.addListener(this::registerCommands);
      NeoForge.EVENT_BUS.addListener(this::reload);
      modContainer.registerConfig(Type.CLIENT, CuriosClientConfig.CLIENT_SPEC);
      modContainer.registerConfig(Type.COMMON, CuriosConfig.COMMON_SPEC);
      modContainer.registerConfig(Type.SERVER, CuriosConfig.SERVER_SPEC);
   }

   private void registerPayloadHandler(RegisterPayloadHandlersEvent evt) {
      NetworkHandler.register(evt.registrar("1.0"));
   }

   private void setup(FMLCommonSetupEvent evt) {
      CuriosApi.setCuriosHelper(new CuriosHelper());
      NeoForge.EVENT_BUS.register(new CuriosEventHandler());
      ModLoader.postEventWrapContainerInModOrder(new RegisterCuriosExtensionsEvent());
      evt.enqueueWork(CuriosSelectorOptions::register);
   }

   private void registerCaps(RegisterCapabilitiesEvent evt) {
      for (EntityType<?> entityType : BuiltInRegistries.ENTITY_TYPE) {
         evt.registerEntity(
            CuriosCapability.ITEM_HANDLER,
            entityType,
            (entity, ctx) -> entity instanceof LivingEntity livingEntity && !CuriosApi.getEntitySlots(livingEntity).isEmpty()
               ? new CurioItemHandler(livingEntity)
               : null
         );
         evt.registerEntity(
            CuriosCapability.INVENTORY,
            entityType,
            (entity, ctx) -> entity instanceof LivingEntity livingEntity && !CuriosApi.getEntitySlots(livingEntity).isEmpty()
               ? new CurioInventoryCapability(livingEntity)
               : null
         );
      }

      for (Item item : BuiltInRegistries.ITEM) {
         evt.registerItem(CuriosCapability.ITEM, (stack, ctx) -> {
            Item it = stack.getItem();
            ICurioItem curioItem = CuriosImplMixinHooks.getCurioFromRegistry(item).orElse(null);
            if (curioItem == null && it instanceof ICurioItem itemCurio) {
               curioItem = itemCurio;
            }

            return curioItem != null && curioItem.hasCurioCapability(stack) ? new ItemizedCurioCapability(curioItem, stack) : null;
         }, new ItemLike[]{item});
      }
   }

   private void process(InterModProcessEvent evt) {
      LegacySlotManager.buildImcSlotTypes(evt.getIMCStream("register_type"::equals), evt.getIMCStream("modify_type"::equals));
   }

   private void serverAboutToStart(ServerAboutToStartEvent evt) {
      CuriosApi.setSlotHelper(new SlotHelper());
      Set<String> slotIds = new HashSet<>();

      for (ISlotType value : CuriosSlotManager.SERVER.getSlots().values()) {
         CuriosApi.getSlotHelper().addSlotType(value);
         slotIds.add(value.getIdentifier());
      }

      CurioArgumentType.slotIds = slotIds;
   }

   private void serverStopped(ServerStoppedEvent evt) {
      CuriosApi.setSlotHelper(null);
   }

   private void registerCommands(RegisterCommandsEvent evt) {
      CuriosCommand.register(evt.getDispatcher(), evt.getBuildContext());
   }

   private void reload(AddReloadListenerEvent evt) {
      CuriosSlotManager.SERVER = new CuriosSlotManager();
      evt.addListener(CuriosSlotManager.SERVER);
      CuriosEntityManager.SERVER = new CuriosEntityManager();
      evt.addListener(CuriosEntityManager.SERVER);
      evt.addListener(new SimplePreparableReloadListener<Void>() {
         @Nonnull
         protected Void prepare(@Nonnull ResourceManager resourceManagerIn, @Nonnull ProfilerFiller profilerIn) {
            return null;
         }

         protected void apply(@Nonnull Void objectIn, @Nonnull ResourceManager resourceManagerIn, @Nonnull ProfilerFiller profilerIn) {
            CuriosEventHandler.dirtyTags = true;
         }
      });
   }

   public static String itemCacheKey(ItemStack stack) {
      return BuiltInRegistries.ITEM.getKey(stack.getItem()).toString()
         + (!stack.getComponents().isEmpty() ? stack.getComponents().stream().map(TypedDataComponent::toString).reduce((s, s2) -> s + s2) : "");
   }

   @EventBusSubscriber(
      modid = "curios",
      value = {Dist.CLIENT},
      bus = Bus.MOD
   )
   public static class ClientProxy {
      @SubscribeEvent
      public static void registerKeys(RegisterKeyMappingsEvent evt) {
         evt.register(KeyRegistry.openCurios);
      }

      @SubscribeEvent
      public static void setupClient(FMLClientSetupEvent evt) {
         CuriosApi.setIconHelper(new IconHelper());
         NeoForge.EVENT_BUS.register(new ClientEventHandler());
         NeoForge.EVENT_BUS.register(new GuiEventHandler());
      }

      @SubscribeEvent
      public static void registerMenuScreens(RegisterMenuScreensEvent evt) {
         evt.register(CuriosRegistry.CURIO_MENU.get(), CuriosScreen::new);
      }

      @SubscribeEvent
      public static void addLayers(AddLayers evt) {
         for (Model skin : evt.getSkins()) {
            addPlayerLayer(evt, skin);
         }

         CuriosRendererRegistry.load();
      }

      private static void addPlayerLayer(AddLayers evt, Model model) {
         if (evt.getSkin(model) instanceof LivingEntityRenderer livingRenderer) {
            livingRenderer.addLayer(new CuriosLayer(livingRenderer));
         }
      }
   }
}
