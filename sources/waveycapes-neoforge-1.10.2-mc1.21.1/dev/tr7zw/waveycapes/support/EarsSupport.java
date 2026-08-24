package dev.tr7zw.waveycapes.support;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.unascribed.ears.api.EarsFeatureType;
import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.api.iface.EarsInhibitor;
import com.unascribed.ears.api.registry.EarsInhibitorRegistry;
import com.unascribed.ears.common.render.EarsRenderDelegate.TexSource;
import dev.tr7zw.transition.mc.GeneralUtil;
import dev.tr7zw.transition.mc.entitywrapper.PlayerWrapper;
import dev.tr7zw.waveycapes.NMSUtil;
import dev.tr7zw.waveycapes.render.CapeInfos;
import dev.tr7zw.waveycapes.render.CapeRenderer;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class EarsSupport implements ModSupport, EarsInhibitor {
   private EarsSupport.EarsRenderer render = new EarsSupport.EarsRenderer();
   private WeakHashMap<Object, AtomicInteger> cache = new WeakHashMap<>();
   private ModelPart[] customCape = NMSUtil.buildCape(20, 16, x -> -1, y -> y - 1);

   public EarsSupport() {
      EarsInhibitorRegistry.register("Waveycapes", this);
   }

   @Override
   public boolean shouldBeUsed(PlayerWrapper capeRenderInfo) {
      Player entity = capeRenderInfo.getEntity();
      EarsFeatures playerFeatures = EarsFeatures.getById(entity.getUUID());
      return playerFeatures != null && playerFeatures.capeEnabled && this.getPlayerCape(capeRenderInfo) != null;
   }

   @Override
   public CapeRenderer getRenderer() {
      return this.render;
   }

   private ResourceLocation getPlayerCape(PlayerWrapper capeRenderInfo) {
      ResourceLocation skin = capeRenderInfo.getCapeTexture();
      return skin != null ? GeneralUtil.getResourceLocation(skin.getNamespace(), TexSource.CAPE.addSuffix(skin.getPath())) : null;
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

   private class EarsRenderer implements CapeRenderer {
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
         EarsFeatures playerFeatures = EarsFeatures.getById(capeRenderInfo.getEntity().getUUID());
         if (playerFeatures != null && playerFeatures.capeEnabled) {
            ResourceLocation cape = EarsSupport.this.getPlayerCape(capeRenderInfo);
            if (cape != null) {
               return new CapeInfos(this, RenderType.armorCutoutNoCull(cape), false);
            }
         }

         return null;
      }
   }
}
