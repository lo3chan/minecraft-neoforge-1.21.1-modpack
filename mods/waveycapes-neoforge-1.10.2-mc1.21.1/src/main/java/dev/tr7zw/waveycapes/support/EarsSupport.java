/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.unascribed.ears.api.EarsFeatureType
 *  com.unascribed.ears.api.features.EarsFeatures
 *  com.unascribed.ears.api.iface.EarsInhibitor
 *  com.unascribed.ears.api.registry.EarsInhibitorRegistry
 *  com.unascribed.ears.common.render.EarsRenderDelegate$TexSource
 *  dev.tr7zw.transition.mc.GeneralUtil
 *  dev.tr7zw.transition.mc.entitywrapper.PlayerWrapper
 *  net.minecraft.client.model.geom.ModelPart
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.player.Player
 */
package dev.tr7zw.waveycapes.support;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.unascribed.ears.api.EarsFeatureType;
import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.api.iface.EarsInhibitor;
import com.unascribed.ears.api.registry.EarsInhibitorRegistry;
import com.unascribed.ears.common.render.EarsRenderDelegate;
import dev.tr7zw.transition.mc.GeneralUtil;
import dev.tr7zw.transition.mc.entitywrapper.PlayerWrapper;
import dev.tr7zw.waveycapes.NMSUtil;
import dev.tr7zw.waveycapes.render.CapeInfos;
import dev.tr7zw.waveycapes.render.CapeRenderer;
import dev.tr7zw.waveycapes.support.ModSupport;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class EarsSupport
implements ModSupport,
EarsInhibitor {
    private EarsRenderer render = new EarsRenderer();
    private WeakHashMap<Object, AtomicInteger> cache = new WeakHashMap();
    private ModelPart[] customCape = NMSUtil.buildCape(20, 16, x -> -1, y -> y - 1);

    public EarsSupport() {
        EarsInhibitorRegistry.register((String)"Waveycapes", (EarsInhibitor)this);
    }

    @Override
    public boolean shouldBeUsed(PlayerWrapper capeRenderInfo) {
        Player entity = capeRenderInfo.getEntity();
        EarsFeatures playerFeatures = EarsFeatures.getById((UUID)entity.getUUID());
        return playerFeatures != null && playerFeatures.capeEnabled && this.getPlayerCape(capeRenderInfo) != null;
    }

    @Override
    public CapeRenderer getRenderer() {
        return this.render;
    }

    private ResourceLocation getPlayerCape(PlayerWrapper capeRenderInfo) {
        ResourceLocation skin = capeRenderInfo.getCapeTexture();
        if (skin != null) {
            return GeneralUtil.getResourceLocation((String)skin.getNamespace(), (String)EarsRenderDelegate.TexSource.CAPE.addSuffix(skin.getPath()));
        }
        return null;
    }

    @Override
    public boolean blockFeatureRenderer(Object feature) {
        return false;
    }

    public boolean shouldInhibit(EarsFeatureType arg0, Object arg1) {
        if (arg0 == EarsFeatureType.CAPE) {
            if (this.cache.containsKey(arg1)) {
                return true;
            }
            this.cache.put(arg1, null);
        }
        return false;
    }

    private class EarsRenderer
    implements CapeRenderer {
        private EarsRenderer() {
        }

        @Override
        public void render(PlayerWrapper capeRenderInfo, int part, ModelPart model, PoseStack poseStack, VertexConsumer vertexConsumer, int light, int overlay) {
            EarsSupport.this.customCape[part].render(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
        }

        @Override
        public boolean vanillaUvValues() {
            return false;
        }

        @Override
        public CapeInfos getCapeInfo(PlayerWrapper capeRenderInfo) {
            ResourceLocation cape;
            EarsFeatures playerFeatures = EarsFeatures.getById((UUID)capeRenderInfo.getEntity().getUUID());
            if (playerFeatures != null && playerFeatures.capeEnabled && (cape = EarsSupport.this.getPlayerCape(capeRenderInfo)) != null) {
                return new CapeInfos(this, RenderType.armorCutoutNoCull((ResourceLocation)cape), false);
            }
            return null;
        }
    }
}

