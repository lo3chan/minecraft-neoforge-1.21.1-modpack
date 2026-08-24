package com.github.alexthe666.alexsmobs;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.citadel.server.message.AnimationMessage;
import com.github.alexthe666.alexsmobs.citadel.server.message.PropertiesMessage;
import com.github.alexthe666.alexsmobs.client.model.layered.AMModelLayers;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.config.BiomeConfig;
import com.github.alexthe666.alexsmobs.config.ConfigHolder;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.event.ServerEvents;
import com.github.alexthe666.alexsmobs.inventory.AMMenuRegistry;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.message.AMNeoNetwork;
import com.github.alexthe666.alexsmobs.message.AMNetContext;
import com.github.alexthe666.alexsmobs.message.MessageCrowDismount;
import com.github.alexthe666.alexsmobs.message.MessageCrowMountPlayer;
import com.github.alexthe666.alexsmobs.message.MessageHurtMultipart;
import com.github.alexthe666.alexsmobs.message.MessageInteractMultipart;
import com.github.alexthe666.alexsmobs.message.MessageKangarooEat;
import com.github.alexthe666.alexsmobs.message.MessageKangarooInventorySync;
import com.github.alexthe666.alexsmobs.message.MessageMosquitoDismount;
import com.github.alexthe666.alexsmobs.message.MessageMosquitoMountPlayer;
import com.github.alexthe666.alexsmobs.message.MessageMungusBiomeChange;
import com.github.alexthe666.alexsmobs.message.MessageSendVisualFlagFromServer;
import com.github.alexthe666.alexsmobs.message.MessageSetPupfishChunkOnClient;
import com.github.alexthe666.alexsmobs.message.MessageStartDancing;
import com.github.alexthe666.alexsmobs.message.MessageSwingArm;
import com.github.alexthe666.alexsmobs.message.MessageSyncEntityPos;
import com.github.alexthe666.alexsmobs.message.MessageTarantulaHawkSting;
import com.github.alexthe666.alexsmobs.message.MessageTransmuteFromMenu;
import com.github.alexthe666.alexsmobs.message.MessageUpdateCapsid;
import com.github.alexthe666.alexsmobs.message.MessageUpdateEagleControls;
import com.github.alexthe666.alexsmobs.message.MessageUpdateTransmutablesToDisplay;
import com.github.alexthe666.alexsmobs.misc.AMAdvancementTriggerRegistry;
import com.github.alexthe666.alexsmobs.misc.AMCreativeTabRegistry;
import com.github.alexthe666.alexsmobs.misc.AMItemHandlers;
import com.github.alexthe666.alexsmobs.misc.AMLootRegistry;
import com.github.alexthe666.alexsmobs.misc.AMPointOfInterestRegistry;
import com.github.alexthe666.alexsmobs.misc.AMRecipeRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.tileentity.AMTileEntityRegistry;
import com.github.alexthe666.alexsmobs.world.AMFeatureRegistry;
import com.github.alexthe666.alexsmobs.world.AMLeafcutterAntBiomeModifier;
import com.github.alexthe666.alexsmobs.world.AMMobSpawnBiomeModifier;
import com.github.alexthe666.alexsmobs.world.AMMobSpawnStructureModifier;
import com.mojang.serialization.MapCodec;
import java.util.Calendar;
import java.util.Date;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.StructureModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("alexsmobs")
public class AlexsMobs {
   public static final Logger LOGGER = LogManager.getLogger();
   public static final String MODID = "alexsmobs";
   private static final String PROTOCOL_VERSION = Integer.toString(1);
   public static final CommonProxy PROXY = FMLEnvironment.dist.isClient() ? (ClientProxy::new).get() : new CommonProxy();
   private static int packetsRegistered;
   private static boolean isAprilFools = false;
   private static boolean isHalloween = false;

   public AlexsMobs(IEventBus modBusEvent, ModContainer modLoadingContext) {
      modBusEvent.addListener(this::setup);
      modBusEvent.addListener(this::setupClient);
      modBusEvent.addListener(this::setupEntityModelLayers);
      modBusEvent.addListener(AMNeoNetwork::onRegisterPayloads);
      modBusEvent.addListener(AMItemHandlers::onRegisterCapabilities);
      AMBlockRegistry.DEF_REG.register(modBusEvent);
      AMEntityRegistry.DEF_REG.register(modBusEvent);
      AMEffectRegistry.EFFECT_DEF_REG.register(modBusEvent);
      AMEffectRegistry.POTION_DEF_REG.register(modBusEvent);
      AMSoundRegistry.DEF_REG.register(modBusEvent);
      AMItemRegistry.DEF_REG.register(modBusEvent);
      AMTileEntityRegistry.DEF_REG.register(modBusEvent);
      AMPointOfInterestRegistry.DEF_REG.register(modBusEvent);
      AMFeatureRegistry.DEF_REG.register(modBusEvent);
      AMParticleRegistry.DEF_REG.register(modBusEvent);
      AMMenuRegistry.DEF_REG.register(modBusEvent);
      AMRecipeRegistry.DEF_REG.register(modBusEvent);
      AMLootRegistry.DEF_REG.register(modBusEvent);
      AMCreativeTabRegistry.DEF_REG.register(modBusEvent);
      AMAdvancementTriggerRegistry.DEF_REG.register(modBusEvent);
      DeferredRegister<MapCodec<? extends BiomeModifier>> biomeModifiers = DeferredRegister.create(Keys.BIOME_MODIFIER_SERIALIZERS, "alexsmobs");
      biomeModifiers.register(modBusEvent);
      AMMobSpawnBiomeModifier.SERIALIZER = biomeModifiers.register("am_mob_spawns", AMMobSpawnBiomeModifier::makeCodec);
      AMLeafcutterAntBiomeModifier.SERIALIZER = biomeModifiers.register("am_leafcutter_ant_spawns", AMLeafcutterAntBiomeModifier::makeCodec);
      DeferredRegister<MapCodec<? extends StructureModifier>> structureModifiers = DeferredRegister.create(Keys.STRUCTURE_MODIFIER_SERIALIZERS, "alexsmobs");
      structureModifiers.register(modBusEvent);
      AMMobSpawnStructureModifier.SERIALIZER = structureModifiers.register("am_structure_spawns", AMMobSpawnStructureModifier::makeCodec);
      ConfigHolder.load();
      BiomeConfig.init();
      PROXY.init();
      NeoForge.EVENT_BUS.register(new ServerEvents());
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(new Date());
      isAprilFools = calendar.get(2) + 1 == 4 && calendar.get(5) == 1;
      isHalloween = calendar.get(2) + 1 == 10 && calendar.get(5) >= 29 && calendar.get(5) <= 31;
   }

   public static boolean isAprilFools() {
      return isAprilFools || AMConfig.superSecretSettings;
   }

   public static boolean isHalloween() {
      return isHalloween || AMConfig.superSecretSettings;
   }

   private void setupEntityModelLayers(RegisterLayerDefinitions event) {
      AMModelLayers.register(event);
   }

   public static <MSG> void sendMSGToServer(MSG message) {
      AMNeoNetwork.sendToServer(message);
   }

   public static <MSG> void sendMSGToAll(MSG message) {
      for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
         sendNonLocal(message, player);
      }
   }

   public static <MSG> void sendNonLocal(MSG msg, ServerPlayer player) {
      AMNeoNetwork.sendToPlayer(msg, player);
   }

   private static <MSG> void registerMessage(
      Class<MSG> clazz, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, AMNetContext> handler
   ) {
      AMNeoNetwork.register(clazz, encoder, decoder, handler);
   }

   private void setup(FMLCommonSetupEvent event) {
      registerMessage(
         MessageMosquitoMountPlayer.class, MessageMosquitoMountPlayer::write, MessageMosquitoMountPlayer::read, MessageMosquitoMountPlayer.Handler::handle
      );
      registerMessage(MessageMosquitoDismount.class, MessageMosquitoDismount::write, MessageMosquitoDismount::read, MessageMosquitoDismount.Handler::handle);
      registerMessage(MessageHurtMultipart.class, MessageHurtMultipart::write, MessageHurtMultipart::read, MessageHurtMultipart.Handler::handle);
      registerMessage(MessageCrowMountPlayer.class, MessageCrowMountPlayer::write, MessageCrowMountPlayer::read, MessageCrowMountPlayer.Handler::handle);
      registerMessage(MessageCrowDismount.class, MessageCrowDismount::write, MessageCrowDismount::read, MessageCrowDismount.Handler::handle);
      registerMessage(MessageMungusBiomeChange.class, MessageMungusBiomeChange::write, MessageMungusBiomeChange::read, MessageMungusBiomeChange.Handler::handle);
      registerMessage(
         MessageKangarooInventorySync.class,
         MessageKangarooInventorySync::write,
         MessageKangarooInventorySync::read,
         MessageKangarooInventorySync.Handler::handle
      );
      registerMessage(MessageKangarooEat.class, MessageKangarooEat::write, MessageKangarooEat::read, MessageKangarooEat.Handler::handle);
      registerMessage(MessageUpdateCapsid.class, MessageUpdateCapsid::write, MessageUpdateCapsid::read, MessageUpdateCapsid.Handler::handle);
      registerMessage(MessageSwingArm.class, MessageSwingArm::write, MessageSwingArm::read, MessageSwingArm.Handler::handle);
      registerMessage(
         MessageUpdateEagleControls.class, MessageUpdateEagleControls::write, MessageUpdateEagleControls::read, MessageUpdateEagleControls.Handler::handle
      );
      registerMessage(MessageSyncEntityPos.class, MessageSyncEntityPos::write, MessageSyncEntityPos::read, MessageSyncEntityPos.Handler::handle);
      registerMessage(
         MessageTarantulaHawkSting.class, MessageTarantulaHawkSting::write, MessageTarantulaHawkSting::read, MessageTarantulaHawkSting.Handler::handle
      );
      registerMessage(MessageStartDancing.class, MessageStartDancing::write, MessageStartDancing::read, MessageStartDancing.Handler::handle);
      registerMessage(MessageInteractMultipart.class, MessageInteractMultipart::write, MessageInteractMultipart::read, MessageInteractMultipart.Handler::handle);
      registerMessage(
         MessageSendVisualFlagFromServer.class,
         MessageSendVisualFlagFromServer::write,
         MessageSendVisualFlagFromServer::read,
         MessageSendVisualFlagFromServer.Handler::handle
      );
      registerMessage(
         MessageSetPupfishChunkOnClient.class,
         MessageSetPupfishChunkOnClient::write,
         MessageSetPupfishChunkOnClient::read,
         MessageSetPupfishChunkOnClient.Handler::handle
      );
      registerMessage(
         MessageUpdateTransmutablesToDisplay.class,
         MessageUpdateTransmutablesToDisplay::write,
         MessageUpdateTransmutablesToDisplay::read,
         MessageUpdateTransmutablesToDisplay.Handler::handle
      );
      registerMessage(MessageTransmuteFromMenu.class, MessageTransmuteFromMenu::write, MessageTransmuteFromMenu::read, MessageTransmuteFromMenu.Handler::handle);
      registerMessage(PropertiesMessage.class, PropertiesMessage::write, PropertiesMessage::read, PropertiesMessage.Handler::handle);
      registerMessage(AnimationMessage.class, AnimationMessage::write, AnimationMessage::read, AnimationMessage.Handler::handle);
      event.enqueueWork(AMItemRegistry::init);
      event.enqueueWork(AMItemRegistry::initDispenser);
      AMAdvancementTriggerRegistry.init();
      AMRecipeRegistry.init();
      PROXY.initPathfinding();
   }

   private void setupClient(FMLClientSetupEvent event) {
      event.enqueueWork(PROXY::clientInit);
   }
}
