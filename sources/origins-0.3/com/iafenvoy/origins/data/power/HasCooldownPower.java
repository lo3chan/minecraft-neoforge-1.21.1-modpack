package com.iafenvoy.origins.data.power;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data._common.HudRender;
import com.iafenvoy.origins.data.power.component.ComponentCollector;
import com.iafenvoy.origins.data.power.component.builtin.CooldownComponent;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;

public abstract class HasCooldownPower extends Power implements HudRenderable {
   private final HasCooldownPower.CooldownSettings cooldown;

   protected HasCooldownPower(Power.BaseSettings settings, HasCooldownPower.CooldownSettings cooldown) {
      super(settings);
      this.cooldown = cooldown;
   }

   public HasCooldownPower.CooldownSettings getCooldown() {
      return this.cooldown;
   }

   @Override
   public void createComponents(ComponentCollector collector) {
      super.createComponents(collector);
      collector.add(new CooldownComponent(this.cooldown.cooldown()));
   }

   @Override
   public Power getPowerForHudRender() {
      return this;
   }

   @Override
   public Optional<HudRender> getHudRenderData() {
      return this.cooldown.hudRender();
   }

   @Override
   public boolean shouldRender(OriginDataHolder holder) {
      return this.getCooldownComponent(holder).getValue() > 0;
   }

   @Override
   public int getMinValue() {
      return 0;
   }

   @Override
   public int getMaxValue() {
      return this.cooldown.cooldown();
   }

   @Override
   public int getValue(OriginDataHolder holder) {
      return this.getCooldownComponent(holder).getValue();
   }

   @Override
   public void setValue(OriginDataHolder holder, int value) {
      this.getCooldownComponent(holder).setValue(value);
   }

   protected CooldownComponent getCooldownComponent(OriginDataHolder holder) {
      return holder.getComponentFor(this, CooldownComponent.class).orElse(new CooldownComponent(1));
   }

   public record CooldownSettings(int cooldown, Optional<HudRender> hudRender) {
      public static final MapCodec<HasCooldownPower.CooldownSettings> CODEC = RecordCodecBuilder.mapCodec(
         i -> i.group(
               Codec.INT.optionalFieldOf("cooldown", 1).forGetter(HasCooldownPower.CooldownSettings::cooldown),
               HudRender.CODEC.optionalFieldOf("hud_render").forGetter(HasCooldownPower.CooldownSettings::hudRender)
            )
            .apply(i, HasCooldownPower.CooldownSettings::new)
      );
   }
}
