package net.bettercombat.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Type;
import java.util.List;
import net.minecraft.client.KeyMapping;

public class Keybindings {
   public static KeyMapping feintKeyBinding = new KeyMapping("keybinds.bettercombat.feint", Type.KEYSYM, InputConstants.UNKNOWN.getValue(), "Better Combat");
   public static KeyMapping toggleMineKeyBinding = new KeyMapping(
      "keybinds.bettercombat.toggle_mine_with_weapons", Type.KEYSYM, InputConstants.UNKNOWN.getValue(), "Better Combat"
   );
   public static List<KeyMapping> all = List.of(feintKeyBinding, toggleMineKeyBinding);
}
