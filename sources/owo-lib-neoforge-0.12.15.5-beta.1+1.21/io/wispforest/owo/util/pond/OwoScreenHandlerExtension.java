package io.wispforest.owo.util.pond;

import io.wispforest.owo.client.screens.ScreenInternals;
import net.minecraft.world.entity.player.Player;

public interface OwoScreenHandlerExtension {
   void owo$attachToPlayer(Player var1);

   void owo$readPropertySync(ScreenInternals.SyncPropertiesPacket var1);

   void owo$handlePacket(ScreenInternals.LocalPacket var1, boolean var2);
}
