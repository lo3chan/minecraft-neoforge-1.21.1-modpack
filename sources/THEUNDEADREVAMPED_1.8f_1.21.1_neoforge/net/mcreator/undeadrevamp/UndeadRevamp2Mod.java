package net.mcreator.undeadrevamp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModAttributes;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModBlockEntities;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModBlocks;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModEntities;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModFeatures;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModItems;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModMenus;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModMobEffects;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModParticleTypes;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModPotions;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModSounds;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModTabs;
import net.mcreator.undeadrevamp.network.UndeadRevamp2ModVariables;
import net.mcreator.undeadrevamp.world.features.StructureFeature;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.util.Tuple;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.util.thread.SidedThreadGroups;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent.Post;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("undead_revamp2")
public class UndeadRevamp2Mod {
   public static final Logger LOGGER = LogManager.getLogger(UndeadRevamp2Mod.class);
   public static final String MODID = "undead_revamp2";
   private static boolean networkingRegistered = false;
   private static final Map<Type<?>, UndeadRevamp2Mod.NetworkMessage<?>> MESSAGES = new HashMap<>();
   private static final Collection<Tuple<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();

   public UndeadRevamp2Mod(IEventBus modEventBus) {
      NeoForge.EVENT_BUS.register(this);
      modEventBus.addListener(this::registerNetworking);
      UndeadRevamp2ModSounds.REGISTRY.register(modEventBus);
      UndeadRevamp2ModBlocks.REGISTRY.register(modEventBus);
      UndeadRevamp2ModBlockEntities.REGISTRY.register(modEventBus);
      UndeadRevamp2ModItems.REGISTRY.register(modEventBus);
      UndeadRevamp2ModEntities.REGISTRY.register(modEventBus);
      UndeadRevamp2ModTabs.REGISTRY.register(modEventBus);
      UndeadRevamp2ModVariables.ATTACHMENT_TYPES.register(modEventBus);
      UndeadRevamp2ModFeatures.REGISTRY.register(modEventBus);
      StructureFeature.REGISTRY.register(modEventBus);
      UndeadRevamp2ModPotions.REGISTRY.register(modEventBus);
      UndeadRevamp2ModMobEffects.REGISTRY.register(modEventBus);
      UndeadRevamp2ModMenus.REGISTRY.register(modEventBus);
      UndeadRevamp2ModParticleTypes.REGISTRY.register(modEventBus);
      UndeadRevamp2ModAttributes.REGISTRY.register(modEventBus);
   }

   public static <T extends CustomPacketPayload> void addNetworkMessage(
      Type<T> id, StreamCodec<? extends FriendlyByteBuf, T> reader, IPayloadHandler<T> handler
   ) {
      if (networkingRegistered) {
         throw new IllegalStateException("Cannot register new network messages after networking has been registered");
      } else {
         MESSAGES.put(id, new UndeadRevamp2Mod.NetworkMessage(reader, handler));
      }
   }

   private void registerNetworking(RegisterPayloadHandlersEvent event) {
      PayloadRegistrar registrar = event.registrar("undead_revamp2");
      MESSAGES.forEach((id, networkMessage) -> registrar.playBidirectional(id, networkMessage.reader(), networkMessage.handler()));
      networkingRegistered = true;
   }

   public static void queueServerWork(int tick, Runnable action) {
      if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER) {
         workQueue.add(new Tuple(action, tick));
      }
   }

   @SubscribeEvent
   public void tick(Post event) {
      List<Tuple<Runnable, Integer>> actions = new ArrayList<>();
      workQueue.forEach(work -> {
         work.setB((Integer)work.getB() - 1);
         if ((Integer)work.getB() == 0) {
            actions.add((Tuple<Runnable, Integer>)work);
         }
      });
      actions.forEach(e -> ((Runnable)e.getA()).run());
      workQueue.removeAll(actions);
   }

   private record NetworkMessage<T extends CustomPacketPayload>(StreamCodec<? extends FriendlyByteBuf, T> reader, IPayloadHandler<T> handler) {
   }
}
