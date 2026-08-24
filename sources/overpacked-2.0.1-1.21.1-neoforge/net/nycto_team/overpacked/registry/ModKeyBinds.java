package net.nycto_team.overpacked.registry;

import com.mojang.blaze3d.platform.InputConstants.Type;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.settings.KeyConflictContext;

@OnlyIn(Dist.CLIENT)
public class ModKeyBinds {
   private static final String category = "key.category.overpacked";
   public static final KeyMapping take_off = new KeyMapping(
      "key.overpacked.take_off_backpack", KeyConflictContext.IN_GAME, Type.KEYSYM, 66, "key.category.overpacked"
   );
}
