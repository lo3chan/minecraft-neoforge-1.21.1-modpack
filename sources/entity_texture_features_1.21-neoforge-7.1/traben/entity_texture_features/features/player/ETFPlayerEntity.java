package traben.entity_texture_features.features.player;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import traben.entity_texture_features.utils.ETFEntity;

public interface ETFPlayerEntity extends ETFEntity {
   Entity etf$getEntity();

   boolean etf$isTeammate(Player var1);

   Inventory etf$getInventory();

   @Deprecated
   boolean etf$isPartVisible(PlayerModelPart var1);

   Component etf$getName();

   String etf$getUuidAsString();
}
