package net.mcreator.borninchaosv;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.mcreator.borninchaosv.init.BornInChaosV1ModBlockEntities;
import net.mcreator.borninchaosv.init.BornInChaosV1ModBlocks;
import net.mcreator.borninchaosv.init.BornInChaosV1ModEntities;
import net.mcreator.borninchaosv.init.BornInChaosV1ModFeatures;
import net.mcreator.borninchaosv.init.BornInChaosV1ModItems;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.mcreator.borninchaosv.init.BornInChaosV1ModPotions;
import net.mcreator.borninchaosv.init.BornInChaosV1ModSounds;
import net.mcreator.borninchaosv.init.BornInChaosV1ModTabs;
import net.mcreator.borninchaosv.network.BornInChaosV1ModVariables;
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

@Mod("born_in_chaos_v1")
public class BornInChaosV1Mod {
   public static final Logger LOGGER = LogManager.getLogger(BornInChaosV1Mod.class);
   public static final String MODID = "born_in_chaos_v1";
   private static boolean networkingRegistered = false;
   private static final Map<Type<?>, BornInChaosV1Mod.NetworkMessage<?>> MESSAGES = new HashMap<>();
   private static final Collection<Tuple<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();

   public BornInChaosV1Mod(IEventBus modEventBus) {
      NeoForge.EVENT_BUS.register(this);
      modEventBus.addListener(this::registerNetworking);
      BornInChaosV1ModSounds.REGISTRY.register(modEventBus);
      BornInChaosV1ModBlocks.REGISTRY.register(modEventBus);
      BornInChaosV1ModBlockEntities.REGISTRY.register(modEventBus);
      BornInChaosV1ModItems.REGISTRY.register(modEventBus);
      BornInChaosV1ModEntities.REGISTRY.register(modEventBus);
      BornInChaosV1ModTabs.REGISTRY.register(modEventBus);
      BornInChaosV1ModVariables.ATTACHMENT_TYPES.register(modEventBus);
      BornInChaosV1ModFeatures.REGISTRY.register(modEventBus);
      BornInChaosV1ModPotions.REGISTRY.register(modEventBus);
      BornInChaosV1ModMobEffects.REGISTRY.register(modEventBus);
      BornInChaosV1ModParticleTypes.REGISTRY.register(modEventBus);
   }

   public static <T extends CustomPacketPayload> void addNetworkMessage(
      Type<T> id, StreamCodec<? extends FriendlyByteBuf, T> reader, IPayloadHandler<T> handler
   ) {
      if (networkingRegistered) {
         throw new IllegalStateException("Cannot register new network messages after networking has been registered");
      } else {
         MESSAGES.put(id, new BornInChaosV1Mod.NetworkMessage(reader, handler));
      }
   }

   private void registerNetworking(RegisterPayloadHandlersEvent event) {
      PayloadRegistrar registrar = event.registrar("born_in_chaos_v1");
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
