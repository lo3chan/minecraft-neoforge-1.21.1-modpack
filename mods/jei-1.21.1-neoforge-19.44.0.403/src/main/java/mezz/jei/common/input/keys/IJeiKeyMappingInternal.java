/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants
 *  com.mojang.blaze3d.platform.InputConstants$Key
 *  com.mojang.blaze3d.platform.InputConstants$Type
 *  java.lang.MatchException
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.Minecraft
 *  net.minecraft.network.chat.Component
 *  org.lwjgl.glfw.GLFW
 */
package mezz.jei.common.input.keys;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.function.Consumer;
import mezz.jei.api.runtime.IJeiKeyMapping;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public interface IJeiKeyMappingInternal
extends IJeiKeyMapping {
    @Override
    public boolean isActiveAndMatches(InputConstants.Key var1);

    @Override
    public boolean isUnbound();

    @Override
    public Component getTranslatedKeyMessage();

    public boolean isDown();

    public IJeiKeyMappingInternal register(Consumer<KeyMapping> var1);

    public static boolean isKeyDown(InputConstants.Key key) {
        if (InputConstants.UNKNOWN.equals((Object)key)) {
            return false;
        }
        Minecraft minecraft = Minecraft.getInstance();
        long windowHandle = minecraft.getWindow().getWindow();
        return switch (key.getType()) {
            default -> throw new MatchException(null, null);
            case InputConstants.Type.KEYSYM -> InputConstants.isKeyDown((long)windowHandle, (int)key.getValue());
            case InputConstants.Type.MOUSE -> {
                if (GLFW.glfwGetMouseButton((long)windowHandle, (int)key.getValue()) == 1) {
                    yield true;
                }
                yield false;
            }
            case InputConstants.Type.SCANCODE -> false;
        };
    }
}

