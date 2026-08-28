/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 */
package mezz.jei.library.util;

import net.minecraft.resources.ResourceLocation;

public class ResourceLocationUtil {
    public static String sanitizePath(String path) {
        char[] charArray = path.toCharArray();
        boolean valid = true;
        for (int i = 0; i < charArray.length; ++i) {
            char c = charArray[i];
            if (ResourceLocation.validPathChar((char)c)) continue;
            charArray[i] = 46;
            valid = false;
        }
        if (valid) {
            return path;
        }
        return new String(charArray);
    }
}

