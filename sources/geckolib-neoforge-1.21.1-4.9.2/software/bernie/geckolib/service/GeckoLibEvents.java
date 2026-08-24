package software.bernie.geckolib.service;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.GeoObjectRenderer;
import software.bernie.geckolib.renderer.GeoReplacedEntityRenderer;

public interface GeckoLibEvents {
   void fireCompileBlockRenderLayers(GeoBlockRenderer<?> var1);

   boolean fireBlockPreRender(GeoBlockRenderer<?> var1, PoseStack var2, BakedGeoModel var3, MultiBufferSource var4, float var5, int var6);

   void fireBlockPostRender(GeoBlockRenderer<?> var1, PoseStack var2, BakedGeoModel var3, MultiBufferSource var4, float var5, int var6);

   void fireCompileArmorRenderLayers(GeoArmorRenderer<?> var1);

   boolean fireArmorPreRender(GeoArmorRenderer<?> var1, PoseStack var2, BakedGeoModel var3, MultiBufferSource var4, float var5, int var6);

   void fireArmorPostRender(GeoArmorRenderer<?> var1, PoseStack var2, BakedGeoModel var3, MultiBufferSource var4, float var5, int var6);

   void fireCompileEntityRenderLayers(GeoEntityRenderer<?> var1);

   boolean fireEntityPreRender(GeoEntityRenderer<?> var1, PoseStack var2, BakedGeoModel var3, MultiBufferSource var4, float var5, int var6);

   void fireEntityPostRender(GeoEntityRenderer<?> var1, PoseStack var2, BakedGeoModel var3, MultiBufferSource var4, float var5, int var6);

   void fireCompileReplacedEntityRenderLayers(GeoReplacedEntityRenderer<?, ?> var1);

   boolean fireReplacedEntityPreRender(GeoReplacedEntityRenderer<?, ?> var1, PoseStack var2, BakedGeoModel var3, MultiBufferSource var4, float var5, int var6);

   void fireReplacedEntityPostRender(GeoReplacedEntityRenderer<?, ?> var1, PoseStack var2, BakedGeoModel var3, MultiBufferSource var4, float var5, int var6);

   void fireCompileItemRenderLayers(GeoItemRenderer<?> var1);

   boolean fireItemPreRender(GeoItemRenderer<?> var1, PoseStack var2, BakedGeoModel var3, MultiBufferSource var4, float var5, int var6);

   void fireItemPostRender(GeoItemRenderer<?> var1, PoseStack var2, BakedGeoModel var3, MultiBufferSource var4, float var5, int var6);

   void fireCompileObjectRenderLayers(GeoObjectRenderer<?> var1);

   boolean fireObjectPreRender(GeoObjectRenderer<?> var1, PoseStack var2, BakedGeoModel var3, MultiBufferSource var4, float var5, int var6);

   void fireObjectPostRender(GeoObjectRenderer<?> var1, PoseStack var2, BakedGeoModel var3, MultiBufferSource var4, float var5, int var6);
}
