package cc.cosmetica.cosmetica.mixin.keybinds;

import cc.cosmetica.cosmetica.Keybinds;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Options.class})
public class OptionsMixin {
   @Shadow
   @Final
   @Mutable
   public KeyMapping[] keyMappings;
   @Unique
   private boolean cosmetica$modified = false;

   @Inject(
      at = {@At("HEAD")},
      method = {"load()V"}
   )
   private void onLoad(CallbackInfo ci) {
      if (!this.cosmetica$modified) {
         this.cosmetica$modified = true;
         KeyMapping[] cosmeticaMappings = new KeyMapping[]{Keybinds.CUSTOMISE, Keybinds.SNIPE, Keybinds.SELECT_OUTFIT};
         KeyMapping[] newKeyMappings = new KeyMapping[this.keyMappings.length + cosmeticaMappings.length];
         System.arraycopy(this.keyMappings, 0, newKeyMappings, 0, this.keyMappings.length);
         System.arraycopy(cosmeticaMappings, 0, newKeyMappings, this.keyMappings.length, cosmeticaMappings.length);
         this.keyMappings = newKeyMappings;
      }
   }
}
