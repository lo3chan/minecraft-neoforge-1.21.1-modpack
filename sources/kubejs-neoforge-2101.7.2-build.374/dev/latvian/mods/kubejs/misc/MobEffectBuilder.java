package dev.latvian.mods.kubejs.misc;

import dev.latvian.mods.kubejs.color.KubeColor;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect.AttributeTemplate;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

@ReturnsSelf
public class MobEffectBuilder extends BuilderBase<MobEffect> {
   public transient MobEffectCategory category = MobEffectCategory.NEUTRAL;
   public transient MobEffectBuilder.EffectEntityCallback effectTick;
   public transient Map<ResourceLocation, AttributeTemplate> attributeModifiers;
   public transient int color = 16777215;
   public transient boolean instant;

   public MobEffectBuilder(ResourceLocation i) {
      super(i);
      this.effectTick = null;
      this.attributeModifiers = new HashMap<>(0);
   }

   public MobEffect createObject() {
      BasicMobEffect effect = new BasicMobEffect(this);
      effect.applyAttributeModifications();
      return effect;
   }

   @Override
   public String getTranslationKeyGroup() {
      return "effect";
   }

   public MobEffectBuilder modifyAttribute(ResourceLocation attribute, ResourceLocation id, double amount, Operation operation) {
      this.attributeModifiers.put(attribute, new AttributeTemplate(id, amount, operation));
      return this;
   }

   public MobEffectBuilder category(MobEffectCategory c) {
      this.category = c;
      return this;
   }

   public MobEffectBuilder harmful() {
      return this.category(MobEffectCategory.HARMFUL);
   }

   public MobEffectBuilder beneficial() {
      return this.category(MobEffectCategory.BENEFICIAL);
   }

   public MobEffectBuilder effectTick(MobEffectBuilder.EffectEntityCallback effectTick) {
      this.effectTick = effectTick;
      return this;
   }

   public MobEffectBuilder color(KubeColor col) {
      this.color = col.kjs$getRGB();
      return this;
   }

   public MobEffectBuilder instant() {
      return this.instant(true);
   }

   public MobEffectBuilder instant(boolean instant) {
      this.instant = instant;
      return this;
   }

   @FunctionalInterface
   public interface EffectEntityCallback {
      void applyEffectTick(LivingEntity entity, int level);
   }
}
