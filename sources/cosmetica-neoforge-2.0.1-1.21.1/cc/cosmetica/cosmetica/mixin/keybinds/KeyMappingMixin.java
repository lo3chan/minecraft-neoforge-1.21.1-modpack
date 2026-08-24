package cc.cosmetica.cosmetica.mixin.keybinds;

import cc.cosmetica.cosmetica.Behaviour;
import cc.cosmetica.cosmetica.Keybinds;
import com.mojang.blaze3d.platform.InputConstants.Key;
import java.util.Map;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({KeyMapping.class})
public class KeyMappingMixin implements Behaviour {
   @Shadow
   @Final
   private static Map<String, Integer> CATEGORY_SORT_ORDER;
   @Shadow
   private int clickCount;

   @Inject(
      at = {@At("RETURN")},
      method = {"click(Lcom/mojang/blaze3d/platform/InputConstants$Key;)V"}
   )
   private static void onClick(Key key, CallbackInfo ci) {
      KeyMapping k = Keybinds.SPECIAL_MAP.get(key);
      if (k != null) {
         ((Behaviour)k).cosmetica$invoke();
      }
   }

   @Override
   public void cosmetica$invoke() {
      this.clickCount++;
   }

   @Inject(
      at = {@At("RETURN")},
      method = {"set(Lcom/mojang/blaze3d/platform/InputConstants$Key;Z)V"}
   )
   private static void onSet(Key key, boolean bl, CallbackInfo ci) {
      KeyMapping k = Keybinds.SPECIAL_MAP.get(key);
      if (k != null) {
         k.setDown(bl);
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"resetMapping()V"}
   )
   private static void beforeReset(CallbackInfo ci) {
      Keybinds.SPECIAL_MAP.clear();
   }

   @Inject(
      at = {@At("RETURN")},
      method = {"<clinit>()V"}
   )
   private static void onClInit(CallbackInfo ci) {
      CATEGORY_SORT_ORDER.put("key.categories.cosmetica", CATEGORY_SORT_ORDER.size() + 1);
   }
}
