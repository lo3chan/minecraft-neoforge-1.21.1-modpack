/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants$Key
 *  com.mojang.blaze3d.platform.InputConstants$Type
 *  net.minecraft.network.chat.Component
 */
package mezz.jei.common.input;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.chat.Component;

public class KeyNameUtil {
    public static Component getKeyDisplayName(InputConstants.Key key) {
        if (key.getType() == InputConstants.Type.MOUSE) {
            int value = key.getValue();
            if (value == 0) {
                return Component.translatable((String)"jei.key.mouse.left");
            }
            if (value == 1) {
                return Component.translatable((String)"jei.key.mouse.right");
            }
        }
        return key.getDisplayName();
    }
}

