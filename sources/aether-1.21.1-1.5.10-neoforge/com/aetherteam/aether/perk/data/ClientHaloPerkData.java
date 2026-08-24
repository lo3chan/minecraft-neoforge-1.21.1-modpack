package com.aetherteam.aether.perk.data;

import com.aetherteam.aether.network.packet.serverbound.ServerHaloPacket;
import com.aetherteam.aether.perk.CustomizationsOptions;
import com.aetherteam.aether.perk.PerkUtil;
import com.aetherteam.aether.perk.types.Halo;
import com.aetherteam.nitrogen.api.users.User;
import com.aetherteam.nitrogen.api.users.UserData.Client;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

public class ClientHaloPerkData extends ClientPerkData<Halo> {
   public static final ClientHaloPerkData INSTANCE = new ClientHaloPerkData();
   private static final Map<UUID, Halo> CLIENT_USER_HALO_DATA = new HashMap<>();

   @Override
   public void syncFromClient(Player player) {
      if (this.canSync(player)) {
         User user = Client.getClientUser();
         UUID uuid = player.getUUID();
         CustomizationsOptions.INSTANCE.load();
         boolean haloEnabled = CustomizationsOptions.INSTANCE.isHaloEnabled();
         String haloColor = CustomizationsOptions.INSTANCE.getHaloHex();
         Map<UUID, Halo> userHaloData = this.getClientPerkData();
         if (haloEnabled) {
            if ((!userHaloData.containsKey(uuid) || userHaloData.get(uuid) == null || haloColor != null && !userHaloData.get(uuid).hexColor().equals(haloColor))
               && PerkUtil.hasHalo().test(user)) {
               PacketDistributor.sendToServer(new ServerHaloPacket.Apply(player.getUUID(), new Halo(haloColor)), new CustomPacketPayload[0]);
            }
         } else {
            PacketDistributor.sendToServer(new ServerHaloPacket.Remove(player.getUUID()), new CustomPacketPayload[0]);
         }
      }
   }

   @Override
   protected Map<UUID, Halo> getMap() {
      return CLIENT_USER_HALO_DATA;
   }
}
