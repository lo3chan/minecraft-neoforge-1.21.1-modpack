package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.Prioritized;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Gui.HeartType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class StatusBarTexturePower extends Power implements Prioritized {
   public static final MapCodec<StatusBarTexturePower> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(
            Power.BaseSettings.CODEC.forGetter(Power::getSettings),
            Codec.unboundedMap(ResourceLocation.CODEC, ResourceLocation.CODEC).fieldOf("texture_map").forGetter(StatusBarTexturePower::getTextureMap),
            Codec.INT.fieldOf("priority").forGetter(StatusBarTexturePower::getPriority)
         )
         .apply(i, StatusBarTexturePower::new)
   );
   private final Map<ResourceLocation, ResourceLocation> textureMap;
   private final int priority;

   protected StatusBarTexturePower(Power.BaseSettings settings, Map<ResourceLocation, ResourceLocation> textureMap, int priority) {
      super(settings);
      this.textureMap = textureMap;
      this.priority = priority;
   }

   public Map<ResourceLocation, ResourceLocation> getTextureMap() {
      return this.textureMap;
   }

   @Override
   public int getPriority() {
      return this.priority;
   }

   @NotNull
   @Override
   public MapCodec<? extends Power> codec() {
      return CODEC;
   }

   @OnlyIn(Dist.CLIENT)
   public void drawHeartTexture(GuiGraphics context, HeartType heartType, int x, int y, int width, int height, boolean hardcore, boolean blinking, boolean half) {
      ResourceLocation texture = heartType.getSprite(hardcore, half, blinking);
      ResourceLocation newTexture = this.textureMap.getOrDefault(texture, texture);
      context.blitSprite(newTexture, x, y, width, height);
   }

   @OnlyIn(Dist.CLIENT)
   public void drawTextureRegion(GuiGraphics context, ResourceLocation texture, int width, int height, int minU, int minV, int x, int y, int maxU, int maxV) {
      context.blitSprite(this.textureMap.getOrDefault(texture, texture), width, height, minU, minV, x, y, maxU, maxV);
   }

   @OnlyIn(Dist.CLIENT)
   public void drawTexture(GuiGraphics context, ResourceLocation texture, int x, int y, int width, int height) {
      context.blitSprite(this.textureMap.getOrDefault(texture, texture), x, y, width, height);
   }
}
