package dev.latvian.mods.kubejs.entity;

import java.util.Collection;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.CommonHooks;
import org.jetbrains.annotations.Nullable;

public class EntityPotionEffectsJS {
   private final LivingEntity entity;

   public EntityPotionEffectsJS(LivingEntity e) {
      this.entity = e;
   }

   public void clear() {
      this.entity.removeAllEffects();
   }

   public Collection<MobEffectInstance> getActive() {
      return this.entity.getActiveEffects();
   }

   public Map<Holder<MobEffect>, MobEffectInstance> getMap() {
      return this.entity.getActiveEffectsMap();
   }

   public boolean isActive(Holder<MobEffect> mobEffect) {
      return mobEffect != null && this.entity.hasEffect(mobEffect);
   }

   public int getDuration(Holder<MobEffect> mobEffect) {
      MobEffectInstance i = this.entity.getEffect(mobEffect);
      return i == null ? 0 : i.getDuration();
   }

   @Nullable
   public MobEffectInstance getActive(Holder<MobEffect> mobEffect) {
      return mobEffect == null ? null : this.entity.getEffect(mobEffect);
   }

   public void add(Holder<MobEffect> mobEffect) {
      this.add(mobEffect, 200);
   }

   public void add(Holder<MobEffect> mobEffect, int duration) {
      this.add(mobEffect, duration, 0);
   }

   public void add(Holder<MobEffect> mobEffect, int duration, int amplifier) {
      this.add(mobEffect, duration, amplifier, false, true);
   }

   public void add(Holder<MobEffect> mobEffect, int duration, int amplifier, boolean ambient, boolean showParticles) {
      this.entity.addEffect(new MobEffectInstance(mobEffect, duration, amplifier, ambient, showParticles));
   }

   public boolean isApplicable(MobEffectInstance effect) {
      return CommonHooks.canMobEffectBeApplied(this.entity, effect, null);
   }
}
