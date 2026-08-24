package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.data._common.ColorSettings;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public class OverlayPower extends Power {
   public static final MapCodec<OverlayPower> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(
            Power.BaseSettings.CODEC.forGetter(Power::getSettings),
            ResourceLocation.CODEC.fieldOf("texture").forGetter(OverlayPower::getTexture),
            Codec.FLOAT.optionalFieldOf("strength", 1.0F).forGetter(OverlayPower::getStrength),
            ColorSettings.NO_ALPHA_CODEC.forGetter(OverlayPower::getColor),
            OverlayPower.DrawMode.CODEC.fieldOf("draw_mode").forGetter(OverlayPower::getDrawMode),
            OverlayPower.DrawPhase.CODEC.fieldOf("draw_phase").forGetter(OverlayPower::getDrawPhase),
            Codec.BOOL.optionalFieldOf("hide_with_hud", true).forGetter(OverlayPower::shouldHideWithHud),
            Codec.BOOL.optionalFieldOf("visible_in_third_person", false).forGetter(OverlayPower::isVisibleInThirdPerson)
         )
         .apply(i, OverlayPower::new)
   );
   private final ResourceLocation texture;
   private final float strength;
   private final ColorSettings color;
   private final OverlayPower.DrawMode drawMode;
   private final OverlayPower.DrawPhase drawPhase;
   private final boolean hideWithHud;
   private final boolean visibleInThirdPerson;

   public OverlayPower(
      Power.BaseSettings settings,
      ResourceLocation texture,
      float strength,
      ColorSettings color,
      OverlayPower.DrawMode drawMode,
      OverlayPower.DrawPhase drawPhase,
      boolean hideWithHud,
      boolean visibleInThirdPerson
   ) {
      super(settings);
      this.texture = texture;
      this.strength = strength;
      this.color = color;
      this.drawMode = drawMode;
      this.drawPhase = drawPhase;
      this.hideWithHud = hideWithHud;
      this.visibleInThirdPerson = visibleInThirdPerson;
   }

   public ResourceLocation getTexture() {
      return this.texture;
   }

   public float getStrength() {
      return this.strength;
   }

   public ColorSettings getColor() {
      return this.color;
   }

   public OverlayPower.DrawMode getDrawMode() {
      return this.drawMode;
   }

   public OverlayPower.DrawPhase getDrawPhase() {
      return this.drawPhase;
   }

   public boolean shouldHideWithHud() {
      return this.hideWithHud;
   }

   public boolean isVisibleInThirdPerson() {
      return this.visibleInThirdPerson;
   }

   @NotNull
   @Override
   public MapCodec<? extends Power> codec() {
      return CODEC;
   }

   public static enum DrawMode implements StringRepresentable {
      NAUSEA,
      TEXTURE;

      public static final Codec<OverlayPower.DrawMode> CODEC = StringRepresentable.fromValues(OverlayPower.DrawMode::values);

      @NotNull
      public String getSerializedName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }

   public static enum DrawPhase implements StringRepresentable {
      BELOW_HUD,
      ABOVE_HUD;

      public static final Codec<OverlayPower.DrawPhase> CODEC = StringRepresentable.fromValues(OverlayPower.DrawPhase::values);

      @NotNull
      public String getSerializedName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
