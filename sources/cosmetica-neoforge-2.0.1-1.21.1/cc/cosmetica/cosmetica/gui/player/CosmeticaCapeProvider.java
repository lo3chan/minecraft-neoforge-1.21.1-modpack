package cc.cosmetica.cosmetica.gui.player;

import cc.cosmetica.core.api.CachedImage;
import cc.cosmetica.core.api.Cosmetics;
import cc.cosmetica.core.api.ImageCosmetic;
import cc.cosmetica.core.builtin.manager.SelfCosmeticManager;
import cc.cosmetica.kupe.api.gui.GUIPlayer.CapeProperties;
import cc.cosmetica.kupe.api.gui.GUIPlayer.CapeProvider;
import cc.cosmetica.kupe.api.gui.GUIPlayer.ElytraProperties;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class CosmeticaCapeProvider implements CapeProvider {
   @Nullable
   public CapeProperties getCapeTexture(UUID uuid) {
      Optional<Cosmetics> cosmetics = getCosmetics(uuid);
      return cosmetics.isPresent()
         ? new CapeProperties(cosmetics.get().getCloak().<CachedImage>map(ImageCosmetic::getImage).map(c -> c.location).orElse(null))
         : null;
   }

   @Nullable
   public ElytraProperties getElytraTexture(UUID uuid) {
      Optional<Cosmetics> cosmetics = getCosmetics(uuid);
      return cosmetics.isPresent()
         ? cosmetics.get()
            .getElytra()
            .<CachedImage>map(ImageCosmetic::getImage)
            .map(c -> new ElytraProperties(c.location, false, true))
            .orElse(ElytraProperties.DEFAULT)
         : null;
   }

   static Optional<Cosmetics> getCosmetics(UUID uuid) {
      if (Minecraft.getInstance().getUser().getProfileId().equals(uuid)) {
         return SelfCosmeticManager.getCosmetics();
      } else {
         if (Minecraft.getInstance().level != null) {
            Player player = Minecraft.getInstance().level.getPlayerByUUID(uuid);
            if (player != null) {
               return Cosmetics.getCosmetics(player);
            }
         }

         return Optional.empty();
      }
   }
}
