/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 */
package net.diebuddies.opengl;

import com.mojang.blaze3d.systems.RenderSystem;

public class TextureHelper {
    public static int getLoadedTextures() {
        return RenderSystem.getShaderTexture((int)0);
    }
}

