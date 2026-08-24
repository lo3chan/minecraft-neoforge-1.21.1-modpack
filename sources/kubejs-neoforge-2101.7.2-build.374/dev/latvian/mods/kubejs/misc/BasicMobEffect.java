package dev.latvian.mods.kubejs.misc;

import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import org.jetbrains.annotations.NotNull;

public class BasicMobEffect extends MobEffect {
   public final transient MobEffectBuilder builder;
   private boolean modified = false;
   private final ResourceLocation id;
   private final boolean instant;

   public BasicMobEffect(MobEffectBuilder builder) {
      super(builder.category, builder.color);
      this.builder = builder;
      this.id = builder.id;
      this.instant = builder.instant;
   }

   public boolean applyEffectTick(@NotNull LivingEntity entity, int i) {
      if (this.builder.effectTick == null) {
         return false;
      } else {
         try {
            this.builder.effectTick.applyEffectTick(entity, i);
            return true;
         } catch (Throwable var4) {
            ScriptType.STARTUP.console.error("Error while ticking mob effect " + this.id + " for entity " + entity.getName().getString(), var4);
            return false;
         }
      }
   }

   public void onEffectStarted(LivingEntity livingEntity, int amplifier) {
   }

   public void onMobRemoved(LivingEntity livingEntity, int amplifier, RemovalReason reason) {
      super.onMobRemoved(livingEntity, amplifier, reason);
   }

   void applyAttributeModifications() {
      if (!this.modified) {
         this.builder.attributeModifiers.forEach((r, m) -> BuiltInRegistries.ATTRIBUTE.getHolder(r).ifPresent(h -> this.attributeModifiers.put(h, m)));
         this.modified = true;
      }
   }

   public void removeAttributeModifiers(AttributeMap attributeMap) {
      this.applyAttributeModifications();
      super.removeAttributeModifiers(attributeMap);
   }

   public MobEffect addAttributeModifier(Holder<Attribute> attribute, ResourceLocation id, double d, Operation operation) {
      this.applyAttributeModifications();
      return super.addAttributeModifier(attribute, id, d, operation);
   }

   public boolean isInstantenous() {
      return this.instant && this.builder.effectTick != null;
   }

   public boolean shouldApplyEffectTickThisTick(int i, int j) {
      return this.builder.effectTick != null;
   }
}
