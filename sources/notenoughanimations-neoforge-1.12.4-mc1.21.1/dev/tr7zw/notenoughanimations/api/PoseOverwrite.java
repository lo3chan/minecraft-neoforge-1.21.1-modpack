package dev.tr7zw.notenoughanimations.api;

import dev.tr7zw.notenoughanimations.access.PlayerData;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;

public interface PoseOverwrite {
   void updateState(AbstractClientPlayer var1, PlayerData var2, PlayerModel var3);
}
