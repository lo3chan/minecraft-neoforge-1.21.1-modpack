/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.tr7zw.transition.mc.entitywrapper.PlayerWrapper
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.resources.ResourceLocation
 */
package dev.tr7zw.waveycapes.render;

import dev.tr7zw.transition.mc.entitywrapper.PlayerWrapper;
import dev.tr7zw.waveycapes.render.CapeInfos;
import dev.tr7zw.waveycapes.render.CapeRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class VanillaCapeRenderer
implements CapeRenderer {
    @Override
    public CapeInfos getCapeInfo(PlayerWrapper capeRenderInfo) {
        ResourceLocation cape = capeRenderInfo.getCapeTexture();
        if (cape != null) {
            return new CapeInfos(this, RenderType.entityTranslucent((ResourceLocation)cape), false);
        }
        return null;
    }

    @Override
    public boolean vanillaUvValues() {
        return true;
    }
}

