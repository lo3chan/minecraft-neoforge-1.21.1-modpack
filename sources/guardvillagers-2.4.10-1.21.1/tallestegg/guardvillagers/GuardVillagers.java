package tallestegg.guardvillagers;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import tallestegg.guardvillagers.client.GuardSounds;
import tallestegg.guardvillagers.common.entities.Guard;
import tallestegg.guardvillagers.configuration.GuardConfig;
import tallestegg.guardvillagers.loot_tables.GuardLootTables;
import tallestegg.guardvillagers.networking.GuardFollowPacket;
import tallestegg.guardvillagers.networking.GuardOpenInventoryPacket;
import tallestegg.guardvillagers.networking.GuardSetPatrolPosPacket;

@Mod("guardvillagers")
public class GuardVillagers {
   public static final String MODID = "guardvillagers";

   public GuardVillagers(ModContainer container, IEventBus modEventBus) {
      container.registerConfig(Type.COMMON, GuardConfig.COMMON_SPEC);
      container.registerConfig(Type.CLIENT, GuardConfig.CLIENT_SPEC);
      container.registerConfig(Type.STARTUP, GuardConfig.STARTUP_SPEC);
      modEventBus.addListener(this::setup);
      NeoForge.EVENT_BUS.register(HandlerEvents.class);
      GuardEntityType.ENTITIES.register(modEventBus);
      GuardItems.ITEMS.register(modEventBus);
      GuardSounds.SOUNDS.register(modEventBus);
      GuardLootTables.LOOT_ITEM_CONDITION_TYPES.register(modEventBus);
      GuardLootTables.LOOT_ITEM_FUNCTION_TYPES.register(modEventBus);
      GuardStats.STATS.register(modEventBus);
      GuardDataAttachments.ATTACHMENT_TYPES.register(modEventBus);
      NeoForge.EVENT_BUS.addListener(this::serverStart);
      modEventBus.addListener(this::addAttributes);
      modEventBus.addListener(this::addCreativeTabs);
      modEventBus.addListener(this::register);
   }

   private void register(RegisterPayloadHandlersEvent event) {
      PayloadRegistrar reg = event.registrar("guardvillagers").versioned("2.0.2");
      reg.playToServer(GuardSetPatrolPosPacket.TYPE, GuardSetPatrolPosPacket.STREAM_CODEC, GuardSetPatrolPosPacket::setPatrolPosition);
      reg.playToClient(GuardOpenInventoryPacket.TYPE, GuardOpenInventoryPacket.STREAM_CODEC, GuardOpenInventoryPacket::handle);
      reg.playToServer(GuardFollowPacket.TYPE, GuardFollowPacket.STREAM_CODEC, GuardFollowPacket::handle);
   }

   public static boolean hotvChecker(Player player, Guard guard) {
      return player.hasEffect(MobEffects.HERO_OF_THE_VILLAGE) && (Boolean)GuardConfig.COMMON.giveGuardStuffHOTV.get()
         || !(Boolean)GuardConfig.COMMON.giveGuardStuffHOTV.get()
         || guard.getPlayerReputation(player) > (Integer)GuardConfig.COMMON.reputationRequirement.get() && !player.level().isClientSide();
   }

   public static boolean canFollow(Player player) {
      return (Boolean)GuardConfig.COMMON.followHero.get() && player.hasEffect(MobEffects.HERO_OF_THE_VILLAGE) || !(Boolean)GuardConfig.COMMON.followHero.get();
   }

   @SubscribeEvent
   private void addCreativeTabs(BuildCreativeModeTabContentsEvent event) {
      if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
         event.accept((ItemLike)GuardItems.GUARD_SPAWN_EGG.get());
         event.accept((ItemLike)GuardItems.ILLUSIONER_SPAWN_EGG.get());
      }
   }

   @SubscribeEvent
   private void setup(FMLCommonSetupEvent event) {
   }

   @SubscribeEvent
   private void addAttributes(EntityAttributeCreationEvent event) {
      event.put((EntityType)GuardEntityType.GUARD.get(), Guard.createAttributes().build());
   }

   public static String removeModIdFromVillagerType(String stringWithModId) {
      String[] parts = stringWithModId.split(":");
      return parts.length <= 1 ? parts[0] : parts[1];
   }

   private void serverStart(ServerAboutToStartEvent event) {
      Registry<StructureTemplatePool> templatePoolRegistry = (Registry<StructureTemplatePool>)event.getServer()
         .registryAccess()
         .registry(Registries.TEMPLATE_POOL)
         .orElseThrow();
      Registry<StructureProcessorList> processorListRegistry = (Registry<StructureProcessorList>)event.getServer()
         .registryAccess()
         .registry(Registries.PROCESSOR_LIST)
         .orElseThrow();
   }

   @Mod(
      value = "guardvillagers",
      dist = {Dist.CLIENT}
   )
   public static class GuardVillagersClient {
      public GuardVillagersClient(ModContainer container, IEventBus modEventBus) {
         container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
      }
   }
}
