package cc.cosmetica.cosmetica.mixin.attach;

import cc.cosmetica.core.api.Cosmetics;
import cc.cosmetica.core.impl.Logging;
import cc.cosmetica.cosmetica.StateHolder;
import cc.cosmetica.kupe.api.State;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.RemotePlayer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({RemotePlayer.class})
public abstract class RemotePlayerMixin extends AbstractClientPlayer implements StateHolder {
   @Unique
   private final State<Cosmetics> cosmetica$noCosmetics = new State(null);

   public RemotePlayerMixin(ClientLevel clientLevel, GameProfile gameProfile) {
      super(clientLevel, gameProfile);
   }

   @Override
   public State<Cosmetics> cosmetica$getCosmeticState() {
      if (Minecraft.getInstance().player == null) {
         return this.cosmetica$noCosmetics;
      } else {
         ClientPacketListener connection = Minecraft.getInstance().player.connection;
         PlayerInfo info = connection.getPlayerInfo(this.getUUID());
         if (info != null) {
            return ((StateHolder)info).cosmetica$getCosmeticState();
         } else {
            Logging.getInstance()
               .warnOnce(
                  "remotePlayerInfoNullState", "Remote Player " + this.getUUID() + " had associated Player Info == null while retrieving state?", new Object[0]
               );
            return this.cosmetica$noCosmetics;
         }
      }
   }

   @Override
   public void cosmetica$setCosmeticState(@Nullable Cosmetics cosmetics) {
      if (Minecraft.getInstance().player != null) {
         ClientPacketListener connection = Minecraft.getInstance().player.connection;
         PlayerInfo info = connection.getPlayerInfo(this.getUUID());
         if (info != null) {
            ((StateHolder)info).cosmetica$setCosmeticState(cosmetics);
         } else {
            Logging.getInstance()
               .warnOnce(
                  "remotePlayerInfoNullState", "Remote Player " + this.getUUID() + " had associated Player Info == null while setting state?", new Object[0]
               );
         }
      }
   }
}
