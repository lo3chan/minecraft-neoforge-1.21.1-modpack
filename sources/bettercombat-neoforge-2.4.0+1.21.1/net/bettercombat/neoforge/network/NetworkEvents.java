package net.bettercombat.neoforge.network;

import java.util.function.Consumer;
import net.bettercombat.BetterCombatMod;
import net.bettercombat.client.ClientNetwork;
import net.bettercombat.logic.WeaponRegistry;
import net.bettercombat.network.Packets;
import net.bettercombat.network.ServerNetwork;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.ConfigurationTask.Type;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(
   modid = "bettercombat",
   bus = Bus.MOD
)
public class NetworkEvents {
   @SubscribeEvent
   public static void register(RegisterConfigurationTasksEvent event) {
      event.register(new NetworkEvents.ConfigurationTask());
      event.register(new NetworkEvents.WeaponRegistrySyncTask());
   }

   @SubscribeEvent
   public static void register(RegisterPayloadHandlersEvent event) {
      PayloadRegistrar registrar = event.registrar("1");
      registrar.configurationToServer(Packets.Ack.PACKET_ID, Packets.Ack.CODEC, (payload, context) -> {
         if (payload.code().equals("bettercombat:config")) {
            context.finishCurrentTask(NetworkEvents.ConfigurationTask.KEY);
         }

         if (payload.code().equals("bettercombat:weapon_registry")) {
            context.finishCurrentTask(NetworkEvents.WeaponRegistrySyncTask.KEY);
         }
      });
      registrar.playToServer(Packets.C2S_AttackRequest.PACKET_ID, Packets.C2S_AttackRequest.CODEC, (packet, context) -> {
         ServerPlayer player = (ServerPlayer)context.player();
         MinecraftServer server = player.server;
         ServerGamePacketListenerImpl vanillaHandler = player.connection;
         ServerNetwork.handleAttackRequest(packet, server, player, vanillaHandler);
      });
      registrar.playToServer(Packets.C2S_BlockHit.PACKET_ID, Packets.C2S_BlockHit.CODEC, (packet, context) -> {
         ServerPlayer player = (ServerPlayer)context.player();
         MinecraftServer server = player.server;
         ServerNetwork.handleBlockHit(packet, server, player);
      });
      registrar.playBidirectional(Packets.AttackAnimation.PACKET_ID, Packets.AttackAnimation.CODEC, (packet, context) -> {
         if (context.flow().isClientbound()) {
            ClientNetwork.handleAttackAnimation(packet);
         } else {
            ServerPlayer player = (ServerPlayer)context.player();
            MinecraftServer server = player.server;
            ServerNetwork.handleAttackAnimation(packet, server, player);
         }
      });
      registrar.configurationToClient(Packets.ConfigSync.PACKET_ID, Packets.ConfigSync.CODEC, (packet, context) -> {
         ClientNetwork.handleConfigSync(packet);
         context.reply(new Packets.Ack("bettercombat:config"));
      });
      registrar.configurationToClient(Packets.WeaponRegistrySync.PACKET_ID, Packets.WeaponRegistrySync.CODEC, (packet, context) -> {
         ClientNetwork.handleWeaponRegistrySync(packet);
         context.reply(new Packets.Ack("bettercombat:weapon_registry"));
      });
      registrar.playToClient(Packets.AttackSound.PACKET_ID, Packets.AttackSound.CODEC, (packet, context) -> ClientNetwork.handleAttackSound(packet));
   }

   public record ConfigurationTask() implements ICustomConfigurationTask {
      public static final String name = "bettercombat:config";
      public static final Type KEY = new Type("bettercombat:config");

      public Type type() {
         return KEY;
      }

      public void run(Consumer<CustomPacketPayload> sender) {
         String configString = Packets.ConfigSync.serialize(BetterCombatMod.config);
         Packets.ConfigSync packet = new Packets.ConfigSync(configString);
         sender.accept(packet);
      }
   }

   public record WeaponRegistrySyncTask() implements ICustomConfigurationTask {
      public static final String name = "bettercombat:weapon_registry";
      public static final Type KEY = new Type("bettercombat:weapon_registry");

      public Type type() {
         return KEY;
      }

      public void run(Consumer<CustomPacketPayload> sender) {
         WeaponRegistry.Encoded encodded = WeaponRegistry.getEncodedRegistry();
         Packets.WeaponRegistrySync packet = new Packets.WeaponRegistrySync(encodded.compressed(), encodded.chunks());
         sender.accept(packet);
      }
   }
}
