/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonSyntaxException
 *  com.mojang.blaze3d.pipeline.RenderTarget
 *  com.mojang.blaze3d.platform.Window
 *  com.mojang.blaze3d.shaders.Uniform
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.PostChain
 *  net.minecraft.client.renderer.PostPass
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.ResourceProvider
 */
package me.flashyreese.mods.sodiumextra.client.render;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import me.flashyreese.mods.sodiumextra.client.SodiumExtraClientMod;
import me.flashyreese.mods.sodiumextra.client.config.SodiumExtraGameOptions;
import me.flashyreese.mods.sodiumextra.compat.IrisCompat;
import me.flashyreese.mods.sodiumextra.mixin.panini_projection.AccessorLevelRenderer;
import me.flashyreese.mods.sodiumextra.mixin.panini_projection.AccessorPostChain;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;

public class PaniniProjection {
    private static final ResourceLocation POST_CHAIN_ID = ResourceLocation.fromNamespaceAndPath((String)"sodium-extra", (String)"shaders/post/panini.json");
    private static final String CONFIG_UNIFORM = "Params";
    private static final AtomicBoolean WARNED_MISSING_CHAIN = new AtomicBoolean(false);
    private static final AtomicBoolean WARNED_MISSING_UNIFORM = new AtomicBoolean(false);
    private static PostChain postChain;
    private static boolean postChainUnavailable;
    private static int postChainWidth;
    private static int postChainHeight;

    public static void process(Minecraft minecraft, RenderTarget mainTarget, float tickDelta) {
        Window window = minecraft.getWindow();
        if (!PaniniProjection.shouldApply(minecraft) || !PaniniProjection.hasValidWindow(window)) {
            PaniniProjection.close();
            return;
        }
        PostChain postChain = PaniniProjection.getOrCreatePostChain(minecraft, mainTarget, window);
        if (postChain == null) {
            return;
        }
        if (PaniniProjection.updateUniforms(postChain, window)) {
            RenderSystem.disableBlend();
            RenderSystem.disableDepthTest();
            RenderSystem.resetTextureMatrix();
            postChain.process(tickDelta);
            mainTarget.bindWrite(true);
        }
    }

    private static boolean shouldApply(Minecraft minecraft) {
        SodiumExtraGameOptions.ExtraSettings settings = SodiumExtraClientMod.options().extraSettings;
        return settings.paniniProjection && settings.paniniProjectionStrength > 0 && !settings.preventShaders && minecraft.player != null && !minecraft.player.isScoping() && !minecraft.gameRenderer.isPanoramicMode() && !PaniniProjection.isFrustumCaptured(minecraft) && !IrisCompat.isShaderPackInUse();
    }

    private static boolean isFrustumCaptured(Minecraft minecraft) {
        return minecraft.levelRenderer != null && ((AccessorLevelRenderer)minecraft.levelRenderer).sodiumExtra$getCapturedFrustum() != null;
    }

    private static boolean hasValidWindow(Window window) {
        return window != null && window.getWidth() > 0 && window.getHeight() > 0;
    }

    private static PostChain getOrCreatePostChain(Minecraft minecraft, RenderTarget mainTarget, Window window) {
        if (postChainUnavailable) {
            return null;
        }
        int width = window.getWidth();
        int height = window.getHeight();
        if (postChain == null) {
            try {
                postChain = new PostChain(minecraft.getTextureManager(), (ResourceProvider)minecraft.getResourceManager(), mainTarget, POST_CHAIN_ID);
            }
            catch (JsonSyntaxException | IOException exception) {
                postChainUnavailable = true;
                if (WARNED_MISSING_CHAIN.compareAndSet(false, true)) {
                    SodiumExtraClientMod.logger().warn("Unable to apply Panini Projection because the post effect '{}' is unavailable", (Object)POST_CHAIN_ID, (Object)exception);
                }
                return null;
            }
        }
        if (postChainWidth != width || postChainHeight != height) {
            postChain.resize(width, height);
            postChainWidth = width;
            postChainHeight = height;
        }
        return postChain;
    }

    private static boolean updateUniforms(PostChain postChain, Window window) {
        List<PostPass> passes = ((AccessorPostChain)postChain).sodiumExtra$getPasses();
        for (PostPass pass : passes) {
            Uniform configUniform = pass.getEffect().getUniform(CONFIG_UNIFORM);
            if (configUniform == null) continue;
            SodiumExtraGameOptions.ExtraSettings settings = SodiumExtraClientMod.options().extraSettings;
            float strength = (float)settings.paniniProjectionStrength / 100.0f;
            float aspect = (float)window.getWidth() / (float)window.getHeight();
            configUniform.set(strength, aspect, 0.0f, 0.0f);
            return true;
        }
        if (WARNED_MISSING_UNIFORM.compareAndSet(false, true)) {
            SodiumExtraClientMod.logger().warn("Unable to apply Panini Projection because the '{}' post uniform is unavailable", (Object)CONFIG_UNIFORM);
        }
        return false;
    }

    private static void close() {
        if (postChain != null) {
            postChain.close();
            postChain = null;
            postChainWidth = -1;
            postChainHeight = -1;
        }
    }

    static {
        postChainWidth = -1;
        postChainHeight = -1;
    }
}

