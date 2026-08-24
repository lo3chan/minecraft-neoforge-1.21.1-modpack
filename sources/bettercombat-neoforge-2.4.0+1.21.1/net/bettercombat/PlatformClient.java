package net.bettercombat;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.injectables.annotations.ExpectPlatform.Transformed;
import net.bettercombat.neoforge.PlatformClientImpl;
import net.minecraft.world.entity.player.Player;

public class PlatformClient {
   @ExpectPlatform
   @Transformed
   public static void onEmptyLeftClick(Player player) {
      PlatformClientImpl.onEmptyLeftClick(player);
   }
}
