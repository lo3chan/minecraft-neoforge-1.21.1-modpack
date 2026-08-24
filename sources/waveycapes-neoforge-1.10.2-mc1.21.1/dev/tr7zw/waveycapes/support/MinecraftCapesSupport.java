package dev.tr7zw.waveycapes.support;

import dev.tr7zw.transition.mc.entitywrapper.PlayerWrapper;
import dev.tr7zw.waveycapes.render.CapeInfos;
import dev.tr7zw.waveycapes.render.CapeRenderer;
import dev.tr7zw.waveycapes.versionless.ModBase;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.player.Player;
import net.minecraftcapes.config.MinecraftCapesConfig;
import net.minecraftcapes.player.PlayerHandler;

public class MinecraftCapesSupport implements ModSupport {
   private MinecraftCapesSupport.MinecraftCapesRenderer render = new MinecraftCapesSupport.MinecraftCapesRenderer();
   private Function<PlayerWrapper, PlayerHandler> getCape = null;

   private void init(PlayerWrapper test) {
      try {
         this.getCape = player -> {
            Player entity = player.getEntity();
            PlayerHandler.get(entity.getUUID()).getCapeLocation();
            return PlayerHandler.get(entity.getUUID());
         };
         this.getCape.apply(test);
         ModBase.LOGGER.info("Using 'get(UUID)' method for MinecraftCapes.");
      } catch (Throwable var8) {
         for (Method m : PlayerHandler.class.getMethods()) {
            try {
               if (m.getReturnType() == PlayerHandler.class || m.getParameterCount() != 1 || m.getParameterTypes()[0] == UUID.class) {
                  m.invoke(null, test);
                  this.getCape = player -> {
                     try {
                        return (PlayerHandler)m.invoke(null, player);
                     } catch (InvocationTargetException | IllegalAccessException var3) {
                        return null;
                     }
                  };
                  ModBase.LOGGER.info("Using '" + m.getName() + "' method for MinecraftCapes.");
                  return;
               }
            } catch (Throwable var7) {
            }
         }

         this.getCape = player -> null;
         ModBase.LOGGER.info("Unable to find a method for MinecraftCapes.");
      }
   }

   @Override
   public boolean shouldBeUsed(PlayerWrapper capeRenderInfo) {
      if (!MinecraftCapesConfig.isCapeVisible()) {
         return false;
      } else {
         if (this.getCape == null) {
            this.init(capeRenderInfo);
         }

         PlayerHandler handler = this.getCape.apply(capeRenderInfo);
         return handler != null && handler.getCapeLocation() != null;
      }
   }

   @Override
   public CapeRenderer getRenderer() {
      return this.render;
   }

   @Override
   public boolean blockFeatureRenderer(Object feature) {
      return false;
   }

   private class MinecraftCapesRenderer implements CapeRenderer {
      @Override
      public CapeInfos getCapeInfo(PlayerWrapper capeRenderInfo) {
         PlayerHandler playerHandler = MinecraftCapesSupport.this.getCape.apply(capeRenderInfo);
         return MinecraftCapesConfig.isCapeVisible() && playerHandler.getCapeLocation() != null
            ? new CapeInfos(this, RenderType.entityTranslucent(playerHandler.getCapeLocation()), playerHandler.getHasCapeGlint())
            : new CapeInfos(this, RenderType.entityTranslucent(capeRenderInfo.getCapeTexture()), playerHandler.getHasCapeGlint());
      }
   }
}
