package cc.cosmetica.cosmetica.neoforge.mixin;

import cc.cosmetica.cosmetica.Keybinds;
import com.mojang.blaze3d.platform.InputConstants.Key;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.KeyMappingLookup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({KeyMapping.class})
public class KeyMappingMixin {
   @Redirect(
      at = @At(
         value = "INVOKE",
         target = "Lnet/neoforged/neoforge/client/settings/KeyMappingLookup;put(Lcom/mojang/blaze3d/platform/InputConstants$Key;Lnet/minecraft/client/KeyMapping;)V"
      ),
      method = {"resetMapping()V"}
   )
   private static void set(KeyMappingLookup instance, Key keyCode, KeyMapping keyBinding) {
      if (keyBinding == Keybinds.SNIPE) {
         Keybinds.SPECIAL_MAP.put(keyCode, keyBinding);
      } else {
         instance.put(keyCode, keyBinding);
      }
   }
}
