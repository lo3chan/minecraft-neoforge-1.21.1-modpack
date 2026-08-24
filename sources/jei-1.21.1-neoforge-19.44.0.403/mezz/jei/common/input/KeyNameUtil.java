package mezz.jei.common.input;

import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.platform.InputConstants.Type;
import net.minecraft.network.chat.Component;

public class KeyNameUtil {
   public static Component getKeyDisplayName(Key key) {
      if (key.getType() == Type.MOUSE) {
         int value = key.getValue();
         if (value == 0) {
            return Component.translatable("jei.key.mouse.left");
         }

         if (value == 1) {
            return Component.translatable("jei.key.mouse.right");
         }
      }

      return key.getDisplayName();
   }
}
