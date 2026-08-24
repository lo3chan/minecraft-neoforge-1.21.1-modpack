package mezz.jei.api.runtime;

import com.mojang.blaze3d.platform.InputConstants.Key;
import net.minecraft.network.chat.Component;

public interface IJeiKeyMapping {
   boolean isActiveAndMatches(Key var1);

   boolean isUnbound();

   Component getTranslatedKeyMessage();
}
