package tannyjung.tanshugetrees;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
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
import tannyjung.tanshugetrees.init.TanshugetreesModBlockEntities;
import tannyjung.tanshugetrees.init.TanshugetreesModBlocks;
import tannyjung.tanshugetrees.init.TanshugetreesModItems;
import tannyjung.tanshugetrees.init.TanshugetreesModMenus;
import tannyjung.tanshugetrees.init.TanshugetreesModTabs;
import tannyjung.tanshugetrees.network.TanshugetreesModVariables;
import tannyjung.tanshugetrees_core.Core;

@Mod("tanshugetrees")
public class TanshugetreesMod {
   public static final Logger LOGGER = LogManager.getLogger(TanshugetreesMod.class);
   public static final String MODID = "tanshugetrees";
   private static boolean networkingRegistered = false;
   private static final Map<Type<?>, TanshugetreesMod.NetworkMessage<?>> MESSAGES = new HashMap<>();
   private static final Collection<Tuple<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();

   public TanshugetreesMod(IEventBus modEventBus) {
      NeoForge.EVENT_BUS.register(this);
      modEventBus.addListener(this::registerNetworking);
      TanshugetreesModBlocks.REGISTRY.register(modEventBus);
      TanshugetreesModBlockEntities.REGISTRY.register(modEventBus);
      TanshugetreesModItems.REGISTRY.register(modEventBus);
      TanshugetreesModTabs.REGISTRY.register(modEventBus);
      TanshugetreesModVariables.ATTACHMENT_TYPES.register(modEventBus);
      TanshugetreesModMenus.REGISTRY.register(modEventBus);
      Core.start(modEventBus);
   }

   public static <T extends CustomPacketPayload> void addNetworkMessage(
      Type<T> id, StreamCodec<? extends FriendlyByteBuf, T> reader, IPayloadHandler<T> handler
   ) {
      if (networkingRegistered) {
         throw new IllegalStateException("Cannot register new network messages after networking has been registered");
      } else {
         MESSAGES.put(id, new TanshugetreesMod.NetworkMessage(reader, handler));
      }
   }

   private void registerNetworking(RegisterPayloadHandlersEvent event) {
      PayloadRegistrar registrar = event.registrar("tanshugetrees");
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
