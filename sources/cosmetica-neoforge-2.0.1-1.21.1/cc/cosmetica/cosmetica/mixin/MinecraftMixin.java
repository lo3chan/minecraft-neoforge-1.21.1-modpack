package cc.cosmetica.cosmetica.mixin;

import cc.cosmetica.cosmetica.VersionChecker;
import cc.cosmetica.kupe.api.Text;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.ReceivingLevelScreen.Reason;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Minecraft.class})
public class MinecraftMixin {
   @Shadow
   @Final
   public Gui gui;

   @Inject(
      at = {@At("HEAD")},
      method = {"setLevel(Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/client/gui/screens/ReceivingLevelScreen$Reason;)V"}
   )
   private void versionChecker(ClientLevel clientLevel, Reason reason, CallbackInfo info) {
      Text text = VersionChecker.INSTANCE.getMessage();
      if (text != null) {
         this.gui.getChat().addMessage(text.toMinecraftComponent());
      }
   }
}
