package com.iafenvoy.origins.accessor;

import java.util.Optional;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public interface PowerCraftingObject {
   Optional<Player> origins$getPlayer();

   void origins$setPlayer(@NotNull Player var1);

   void origins$clearPlayer();
}
