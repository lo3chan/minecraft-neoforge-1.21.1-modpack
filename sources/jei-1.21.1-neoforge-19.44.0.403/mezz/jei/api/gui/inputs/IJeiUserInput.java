package mezz.jei.api.gui.inputs;

import com.mojang.blaze3d.platform.InputConstants.Key;
import mezz.jei.api.runtime.IJeiKeyMapping;
import net.minecraft.client.KeyMapping;

public interface IJeiUserInput {
   Key getKey();

   int getModifiers();

   boolean isSimulate();

   boolean is(KeyMapping var1);

   boolean is(IJeiKeyMapping var1);
}
