package io.github.razordevs.deep_aether.client.renderer.entity;

import com.google.common.collect.Maps;
import io.github.razordevs.deep_aether.client.model.QuailModel;
import io.github.razordevs.deep_aether.client.renderer.DAModelLayers;
import io.github.razordevs.deep_aether.entity.living.quail.Quail;
import io.github.razordevs.deep_aether.entity.living.quail.QuailVariants;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class QuailRenderer extends MobRenderer<Quail, QuailModel> {
   public static final Map<QuailVariants, ResourceLocation> LOCATION_BY_VARIANT = (Map<QuailVariants, ResourceLocation>)Util.make(
      Maps.newEnumMap(QuailVariants.class), map -> {
         map.put(QuailVariants.OLD_GREEN, ResourceLocation.fromNamespaceAndPath("deep_aether", "textures/entity/quail/quail_old_green.png"));
         map.put(QuailVariants.PINK, ResourceLocation.fromNamespaceAndPath("deep_aether", "textures/entity/quail/quail_pink.png"));
         map.put(QuailVariants.PURPLE, ResourceLocation.fromNamespaceAndPath("deep_aether", "textures/entity/quail/quail_purple.png"));
         map.put(QuailVariants.TROPICAL_BLUE, ResourceLocation.fromNamespaceAndPath("deep_aether", "textures/entity/quail/quail_tropical_blue.png"));
         map.put(QuailVariants.FADED_YELLOW, ResourceLocation.fromNamespaceAndPath("deep_aether", "textures/entity/quail/quail_faded_yellow.png"));
         map.put(QuailVariants.LIGHT_BLUE, ResourceLocation.fromNamespaceAndPath("deep_aether", "textures/entity/quail/quail_light_blue.png"));
         map.put(QuailVariants.COPPER, ResourceLocation.fromNamespaceAndPath("deep_aether", "textures/entity/quail/quail_copper.png"));
      }
   );

   public QuailRenderer(Context renderManager) {
      super(renderManager, new QuailModel(renderManager.bakeLayer(DAModelLayers.QUAIL)), 0.3F);
   }

   protected float getBob(Quail pLivingBase, float pPartialTicks) {
      float f = Mth.lerp(pPartialTicks, pLivingBase.oFlap, pLivingBase.flap);
      float f1 = Mth.lerp(pPartialTicks, pLivingBase.oFlapSpeed, pLivingBase.flapSpeed);
      return (Mth.sin(f) + 1.0F) * f1;
   }

   @NotNull
   public ResourceLocation getTextureLocation(Quail instance) {
      return LOCATION_BY_VARIANT.get(instance.getVariant());
   }
}
