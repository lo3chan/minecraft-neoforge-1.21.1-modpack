package com.aetherteam.aether.perk.data;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import com.aetherteam.aether.network.packet.serverbound.ServerMoaSkinPacket;
import com.aetherteam.aether.perk.CustomizationsOptions;
import com.aetherteam.aether.perk.types.MoaData;
import com.aetherteam.aether.perk.types.MoaSkins;
import com.aetherteam.nitrogen.api.users.User;
import com.aetherteam.nitrogen.api.users.UserData.Client;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

public class ClientMoaSkinPerkData extends ClientPerkData<MoaData> {
   public static final ClientMoaSkinPerkData INSTANCE = new ClientMoaSkinPerkData();
   private static final Map<UUID, MoaData> CLIENT_USER_SKIN_DATA = new HashMap<>();

   @Override
   public void syncFromClient(Player player) {
      if (this.canSync(player)) {
         User user = Client.getClientUser();
         UUID uuid = player.getUUID();
         UUID lastRiddenMoa = ((AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER)).getLastRiddenMoa();
         CustomizationsOptions.INSTANCE.load();
         String moaSkinName = CustomizationsOptions.INSTANCE.getMoaSkin();
         Map<String, MoaSkins.MoaSkin> moaSkins = MoaSkins.getMoaSkins();
         Map<UUID, MoaData> userSkinsData = this.getClientPerkData();
         if (moaSkinName != null && !moaSkinName.isEmpty() && moaSkins.containsKey(moaSkinName)) {
            MoaSkins.MoaSkin moaSkin = MoaSkins.getMoaSkins().get(moaSkinName);
            MoaData moaData = new MoaData(lastRiddenMoa, moaSkin);
            if ((
                  !userSkinsData.containsKey(uuid)
                     || userSkinsData.get(uuid) == null
                     || userSkinsData.get(uuid).moaUUID() == null && moaData.moaUUID() != null
                     || userSkinsData.get(uuid).moaSkin() == null && moaData.moaSkin() != null
                     || userSkinsData.get(uuid).moaUUID() != null
                        && moaData.moaUUID() != null
                        && !Objects.equals(userSkinsData.get(uuid).moaUUID(), moaData.moaUUID())
                     || userSkinsData.get(uuid).moaSkin() != null
                        && moaData.moaSkin() != null
                        && !Objects.equals(userSkinsData.get(uuid).moaSkin(), moaData.moaSkin())
               )
               && moaSkin.getUserPredicate().test(user)) {
               PacketDistributor.sendToServer(new ServerMoaSkinPacket.Apply(player.getUUID(), new MoaData(lastRiddenMoa, moaSkin)), new CustomPacketPayload[0]);
            }
         } else if ((moaSkinName == null || moaSkinName.isEmpty())
            && userSkinsData.containsKey(uuid)
            && userSkinsData.get(uuid) != null
            && (userSkinsData.get(uuid).moaUUID() != null || userSkinsData.get(uuid).moaSkin() != null)) {
            PacketDistributor.sendToServer(new ServerMoaSkinPacket.Remove(player.getUUID()), new CustomPacketPayload[0]);
         }
      }
   }

   @Override
   protected Map<UUID, MoaData> getMap() {
      return CLIENT_USER_SKIN_DATA;
   }
}
