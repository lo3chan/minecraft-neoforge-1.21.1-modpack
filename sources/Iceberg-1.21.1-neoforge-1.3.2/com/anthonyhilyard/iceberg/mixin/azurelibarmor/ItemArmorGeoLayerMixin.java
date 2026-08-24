package com.anthonyhilyard.iceberg.mixin.azurelibarmor;

import com.anthonyhilyard.iceberg.Iceberg;
import java.lang.reflect.Field;
import mod.azure.azurelibarmor.common.api.client.renderer.layer.ItemArmorGeoLayer;
import net.minecraft.client.renderer.MultiBufferSource;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(
   value = {ItemArmorGeoLayer.class},
   remap = false
)
public class ItemArmorGeoLayerMixin {
   @Unique
   Field bufferSourceFieldV2 = null;
   @Unique
   Field bufferSourceFieldV3 = null;

   @ModifyVariable(
      method = {"renderforBone"},
      at = @At("LOAD"),
      argsOnly = true,
      index = 5,
      require = 0
   )
   private MultiBufferSource icebergStoreBufferSource(MultiBufferSource bufferSource) {
      try {
         if (this.bufferSourceFieldV2 == null) {
            this.bufferSourceFieldV2 = Class.forName("mod.azure.azurelibarmor.common.api.client.renderer.GeoArmorRenderer").getDeclaredField("bufferSource");
            this.bufferSourceFieldV2.setAccessible(true);
         }

         if (this.bufferSourceFieldV2 != null) {
            this.bufferSourceFieldV2.set(null, bufferSource);
         }
      } catch (Exception var4) {
         Iceberg.LOGGER.debug(ExceptionUtils.getStackTrace(var4));
      }

      try {
         if (this.bufferSourceFieldV3 == null) {
            this.bufferSourceFieldV3 = Class.forName("mod.azure.azurelibarmor.rewrite.render.armor.AzArmorModel").getDeclaredField("bufferSource");
            this.bufferSourceFieldV3.setAccessible(true);
         }

         if (this.bufferSourceFieldV3 != null) {
            this.bufferSourceFieldV3.set(null, bufferSource);
         }
      } catch (Exception var3) {
         Iceberg.LOGGER.debug(ExceptionUtils.getStackTrace(var3));
      }

      return bufferSource;
   }
}
