package net.astralya.hexalia.client.renderer.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.astralya.hexalia.client.model.entity.SilkMothModel;
import net.astralya.hexalia.entity.custom.SilkMothEntity;
import net.astralya.hexalia.entity.custom.variant.SilkMothVariant;
import net.minecraft.Util;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SilkMothRenderer extends GeoEntityRenderer<SilkMothEntity> {
   private static final Map<SilkMothVariant, ResourceLocation> LOCATION_BY_VARIANT = (Map<SilkMothVariant, ResourceLocation>)Util.make(
      Maps.newEnumMap(SilkMothVariant.class), map -> {
         map.put(SilkMothVariant.DEFAULT, texture("silk_moth_default"));
         map.put(SilkMothVariant.BLUE, texture("silk_moth_blue"));
         map.put(SilkMothVariant.PINK, texture("silk_moth_pink"));
         map.put(SilkMothVariant.BLACK, texture("silk_moth_black"));
      }
   );

   public SilkMothRenderer(Context context) {
      super(context, new SilkMothModel());
   }

   public ResourceLocation getTextureLocation(SilkMothEntity animatable) {
      return LOCATION_BY_VARIANT.get(animatable.getVariant());
   }

   private static ResourceLocation texture(String name) {
      return ResourceLocation.fromNamespaceAndPath("hexalia", "textures/entity/" + name + ".png");
   }
}
