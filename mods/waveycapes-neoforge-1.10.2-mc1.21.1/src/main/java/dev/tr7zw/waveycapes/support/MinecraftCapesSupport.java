/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.tr7zw.transition.mc.entitywrapper.PlayerWrapper
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftcapes.config.MinecraftCapesConfig
 *  net.minecraftcapes.player.PlayerHandler
 */
package dev.tr7zw.waveycapes.support;

import dev.tr7zw.transition.mc.entitywrapper.PlayerWrapper;
import dev.tr7zw.waveycapes.render.CapeInfos;
import dev.tr7zw.waveycapes.render.CapeRenderer;
import dev.tr7zw.waveycapes.support.ModSupport;
import dev.tr7zw.waveycapes.versionless.ModBase;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftcapes.config.MinecraftCapesConfig;
import net.minecraftcapes.player.PlayerHandler;

public class MinecraftCapesSupport
implements ModSupport {
    private MinecraftCapesRenderer render = new MinecraftCapesRenderer();
    private Function<PlayerWrapper, PlayerHandler> getCape = null;

    private void init(PlayerWrapper test) {
        try {
            this.getCape = player -> {
                Player entity = player.getEntity();
                PlayerHandler.get((UUID)entity.getUUID()).getCapeLocation();
                return PlayerHandler.get((UUID)entity.getUUID());
            };
            this.getCape.apply(test);
            ModBase.LOGGER.info("Using 'get(UUID)' method for MinecraftCapes.");
            return;
        }
        catch (Throwable throwable) {
            for (Method m : PlayerHandler.class.getMethods()) {
                try {
                    if (m.getReturnType() != PlayerHandler.class && m.getParameterCount() == 1 && m.getParameterTypes()[0] != UUID.class) continue;
                    m.invoke(null, test);
                    this.getCape = player -> {
                        try {
                            return (PlayerHandler)m.invoke(null, player);
                        }
                        catch (IllegalAccessException | InvocationTargetException e) {
                            return null;
                        }
                    };
                    ModBase.LOGGER.info("Using '" + m.getName() + "' method for MinecraftCapes.");
                    return;
                }
                catch (Throwable throwable2) {
                    // empty catch block
                }
            }
            this.getCape = player -> null;
            ModBase.LOGGER.info("Unable to find a method for MinecraftCapes.");
            return;
        }
    }

    @Override
    public boolean shouldBeUsed(PlayerWrapper capeRenderInfo) {
        PlayerHandler handler;
        if (!MinecraftCapesConfig.isCapeVisible()) {
            return false;
        }
        if (this.getCape == null) {
            this.init(capeRenderInfo);
        }
        return (handler = this.getCape.apply(capeRenderInfo)) != null && handler.getCapeLocation() != null;
    }

    @Override
    public CapeRenderer getRenderer() {
        return this.render;
    }

    @Override
    public boolean blockFeatureRenderer(Object feature) {
        return false;
    }

    private class MinecraftCapesRenderer
    implements CapeRenderer {
        private MinecraftCapesRenderer() {
        }

        @Override
        public CapeInfos getCapeInfo(PlayerWrapper capeRenderInfo) {
            PlayerHandler playerHandler = MinecraftCapesSupport.this.getCape.apply(capeRenderInfo);
            if (MinecraftCapesConfig.isCapeVisible() && playerHandler.getCapeLocation() != null) {
                return new CapeInfos(this, RenderType.entityTranslucent((ResourceLocation)playerHandler.getCapeLocation()), playerHandler.getHasCapeGlint());
            }
            return new CapeInfos(this, RenderType.entityTranslucent((ResourceLocation)capeRenderInfo.getCapeTexture()), playerHandler.getHasCapeGlint());
        }
    }
}

