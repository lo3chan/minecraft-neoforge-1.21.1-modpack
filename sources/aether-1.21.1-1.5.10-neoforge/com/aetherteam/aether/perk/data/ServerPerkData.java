package com.aetherteam.aether.perk.data;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.network.packet.clientbound.ClientDeveloperGlowPacket;
import com.aetherteam.aether.network.packet.clientbound.ClientHaloPacket;
import com.aetherteam.aether.network.packet.clientbound.ClientMoaSkinPacket;
import com.aetherteam.aether.perk.PerkUtil;
import com.aetherteam.aether.perk.types.DeveloperGlow;
import com.aetherteam.aether.perk.types.Halo;
import com.aetherteam.aether.perk.types.MoaData;
import com.aetherteam.nitrogen.api.users.User;
import com.aetherteam.nitrogen.api.users.UserData.Server;
import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.logging.log4j.util.TriConsumer;

public class ServerPerkData<T> {
   public static final ServerPerkData<MoaData> MOA_SKIN_INSTANCE = new ServerPerkData<>(
      data -> server -> data.getSavedData(server).getStoredSkinData(),
      data -> (server, uuid, perk) -> data.getSavedData(server).modifyStoredSkinData(uuid, perk),
      data -> (server, uuid) -> data.getSavedData(server).removeStoredSkinData(uuid),
      data -> ClientMoaSkinPacket.Apply::new,
      data -> ClientMoaSkinPacket.Remove::new,
      data -> ClientMoaSkinPacket.Sync::new,
      data -> perk -> perk.moaSkin().getUserPredicate()
   );
   public static final ServerPerkData<Halo> HALO_INSTANCE = new ServerPerkData<>(
      data -> server -> data.getSavedData(server).getStoredHaloData(),
      data -> (server, uuid, perk) -> data.getSavedData(server).modifyStoredHaloData(uuid, perk),
      data -> (server, uuid) -> data.getSavedData(server).removeStoredHaloData(uuid),
      data -> ClientHaloPacket.Apply::new,
      data -> ClientHaloPacket.Remove::new,
      data -> ClientHaloPacket.Sync::new,
      data -> perk -> PerkUtil.hasHalo()
   );
   public static final ServerPerkData<DeveloperGlow> DEVELOPER_GLOW_INSTANCE = new ServerPerkData<>(
      data -> server -> data.getSavedData(server).getStoredDeveloperGlowData(),
      data -> (server, uuid, perk) -> data.getSavedData(server).modifyStoredDeveloperGlowData(uuid, perk),
      data -> (server, uuid) -> data.getSavedData(server).removeStoredDeveloperGlowData(uuid),
      data -> ClientDeveloperGlowPacket.Apply::new,
      data -> ClientDeveloperGlowPacket.Remove::new,
      data -> ClientDeveloperGlowPacket.Sync::new,
      data -> perk -> PerkUtil.hasDeveloperGlow()
   );
   private final Function<MinecraftServer, Map<UUID, T>> savedMap;
   private final TriConsumer<MinecraftServer, UUID, T> modify;
   private final BiConsumer<MinecraftServer, UUID> remove;
   private final BiFunction<UUID, T, CustomPacketPayload> applyPacket;
   private final Function<UUID, CustomPacketPayload> removePacket;
   private final Function<Map<UUID, T>, CustomPacketPayload> syncPacket;
   private final Function<T, Predicate<User>> verificationPredicate;

   public ServerPerkData(
      Function<ServerPerkData<T>, Function<MinecraftServer, Map<UUID, T>>> savedMap,
      Function<ServerPerkData<T>, TriConsumer<MinecraftServer, UUID, T>> modify,
      Function<ServerPerkData<T>, BiConsumer<MinecraftServer, UUID>> remove,
      Function<ServerPerkData<T>, BiFunction<UUID, T, CustomPacketPayload>> applyPacket,
      Function<ServerPerkData<T>, Function<UUID, CustomPacketPayload>> removePacket,
      Function<ServerPerkData<T>, Function<Map<UUID, T>, CustomPacketPayload>> syncPacket,
      Function<ServerPerkData<T>, Function<T, Predicate<User>>> verificationPredicate
   ) {
      this.savedMap = savedMap.apply(this);
      this.modify = modify.apply(this);
      this.remove = remove.apply(this);
      this.applyPacket = applyPacket.apply(this);
      this.removePacket = removePacket.apply(this);
      this.syncPacket = syncPacket.apply(this);
      this.verificationPredicate = verificationPredicate.apply(this);
   }

   public void syncFromServer(Player player) {
      if (player instanceof ServerPlayer serverPlayer) {
         PacketDistributor.sendToPlayer(serverPlayer, this.getSyncPacket(this.getServerPerkData(serverPlayer.getServer())), new CustomPacketPayload[0]);
      }
   }

   public void applyPerkWithVerification(MinecraftServer server, UUID uuid, T perk) {
      Map<UUID, User> storedUsers = Server.getStoredUsers();
      if (storedUsers.containsKey(uuid)) {
         User user = storedUsers.get(uuid);

         try {
            if (user != null && this.getVerificationPredicate(perk).test(user)) {
               PacketDistributor.sendToAllPlayers(this.getApplyPacket(uuid, perk), new CustomPacketPayload[0]);
               this.modifySavedData(server, uuid, perk);
            }
         } catch (RuntimeException var7) {
            Aether.LOGGER.info(var7.getMessage());
         }
      }
   }

   public void removePerk(MinecraftServer server, UUID uuid) {
      PacketDistributor.sendToAllPlayers(this.getRemovePacket(uuid), new CustomPacketPayload[0]);
      this.removeSavedData(server, uuid);
   }

   public Map<UUID, T> getServerPerkData(MinecraftServer server) {
      Map<UUID, User> storedUsers = Server.getStoredUsers();
      Map<UUID, T> verifiedPerkData = new HashMap<>();

      for (Entry<UUID, T> serverPerkDataEntry : this.getSavedMap(server).entrySet()) {
         if (storedUsers.containsKey(serverPerkDataEntry.getKey())) {
            User user = storedUsers.get(serverPerkDataEntry.getKey());

            try {
               if (user != null && this.getVerificationPredicate(serverPerkDataEntry.getValue()).test(user)) {
                  verifiedPerkData.put(serverPerkDataEntry.getKey(), serverPerkDataEntry.getValue());
               }
            } catch (RuntimeException var8) {
               Aether.LOGGER.info(var8.getMessage());
            }
         }
      }

      return ImmutableMap.copyOf(verifiedPerkData);
   }

   protected PerkSavedData getSavedData(MinecraftServer server) {
      return PerkSavedData.compute(server.overworld().getDataStorage());
   }

   protected Map<UUID, T> getSavedMap(MinecraftServer server) {
      return this.savedMap.apply(server);
   }

   protected void modifySavedData(MinecraftServer server, UUID uuid, T perk) {
      this.modify.accept(server, uuid, perk);
   }

   protected void removeSavedData(MinecraftServer server, UUID uuid) {
      this.remove.accept(server, uuid);
   }

   protected CustomPacketPayload getApplyPacket(UUID uuid, T perk) {
      return this.applyPacket.apply(uuid, perk);
   }

   protected CustomPacketPayload getRemovePacket(UUID uuid) {
      return this.removePacket.apply(uuid);
   }

   protected CustomPacketPayload getSyncPacket(Map<UUID, T> serverPerkData) {
      return this.syncPacket.apply(serverPerkData);
   }

   protected Predicate<User> getVerificationPredicate(T perk) {
      return this.verificationPredicate.apply(perk);
   }
}
