package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data._common.EffectEntry;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.mixin.accessor.MobEffectInstanceAccessor;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Applicable;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Remove;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Applicable.Result;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber
public class PermanentEffectPower extends Power {
   public static final MapCodec<PermanentEffectPower> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(
            Power.BaseSettings.CODEC.forGetter(Power::getSettings),
            EffectEntry.LIST_CODEC.fieldOf("effect").forGetter(PermanentEffectPower::getEffect),
            Codec.BOOL.optionalFieldOf("allow_higher_level", false).forGetter(PermanentEffectPower::allowHigher)
         )
         .apply(i, PermanentEffectPower::new)
   );
   private final List<EffectEntry> effect;
   private final boolean allowHigherLevel;

   public PermanentEffectPower(Power.BaseSettings settings, List<EffectEntry> effect, boolean allowHigherLevel) {
      super(settings);
      this.effect = effect;
      this.allowHigherLevel = allowHigherLevel;
   }

   public List<EffectEntry> getEffect() {
      return this.effect;
   }

   public boolean allowHigher() {
      return this.allowHigherLevel;
   }

   @NotNull
   @Override
   public MapCodec<? extends Power> codec() {
      return CODEC;
   }

   @Override
   public void active(@NotNull OriginDataHolder holder) {
      super.active(holder);
      if (holder.getEntity() instanceof LivingEntity living) {
         this.effect.stream().map(e -> e.create(-1)).forEach(living::addEffect);
      }
   }

   @Override
   public void inactive(@NotNull OriginDataHolder holder) {
      super.inactive(holder);
      if (holder.getEntity() instanceof LivingEntity living) {
         this.effect.stream().map(EffectEntry::effect).forEach(living::removeEffect);
      }
   }

   @Override
   public void respawn(OriginDataHolder holder, boolean backFromEnd) {
      if (!backFromEnd) {
         this.active(holder);
      }
   }

   @Override
   public void activeTick(OriginDataHolder holder) {
      super.activeTick(holder);
      if (holder.getEntity() instanceof LivingEntity living) {
         for (EffectEntry entry : this.effect) {
            MobEffectInstance instance = living.getEffect(entry.effect());
            if (instance == null) {
               living.addEffect(entry.create(-1));
            } else if (!this.allowHigherLevel) {
               if (!instance.isInfiniteDuration() || instance.getAmplifier() != entry.amplifier()) {
                  living.removeEffect(entry.effect());
                  living.addEffect(entry.create(-1));
               }
            } else if (instance.isInfiniteDuration()) {
               if (instance.getAmplifier() < entry.amplifier()) {
                  living.removeEffect(entry.effect());
                  living.addEffect(entry.create(-1));
               }
            } else if (!checkHiddenEffectChain(instance, entry)) {
               living.removeEffect(entry.effect());
               living.addEffect(entry.create(-1));
            }
         }
      }
   }

   private static boolean checkHiddenEffectChain(@NotNull MobEffectInstance instance, EffectEntry entry) {
      for (MobEffectInstance next = ((MobEffectInstanceAccessor)instance).getHiddenEffect();
         next != null;
         next = ((MobEffectInstanceAccessor)next).getHiddenEffect()
      ) {
         if (next.isInfiniteDuration() && next.getAmplifier() == entry.amplifier() && Objects.equals(next.getEffect(), entry.effect())) {
            return true;
         }
      }

      return false;
   }

   @Override
   public int tickInterval() {
      return 20;
   }

   @SubscribeEvent
   public static void handleReplace(Applicable event) {
      MobEffectInstance instance = event.getEffectInstance();
      LivingEntity entity = event.getEntity();
      List<PermanentEffectPower> powers = PowerHelper.get(entity)
         .listActive(PermanentEffectPower.class, p -> p.effect.stream().anyMatch(e -> Objects.equals(e.effect(), instance.getEffect())));
      if (!powers.isEmpty()) {
         if (!instance.isInfiniteDuration() || !powers.stream().anyMatch(p -> p.effect.stream().anyMatch(e -> e.amplifier() == instance.getAmplifier()))) {
            MobEffectInstance existing = entity.getEffect(instance.getEffect());
            if (existing == null || instance.getAmplifier() <= existing.getAmplifier() || !powers.stream().anyMatch(PermanentEffectPower::allowHigher)) {
               event.setResult(Result.DO_NOT_APPLY);
            }
         }
      }
   }

   @SubscribeEvent
   public static void handleRemove(Remove event) {
      MobEffectInstance instance = event.getEffectInstance();
      if (instance != null
         && instance.isInfiniteDuration()
         && PowerHelper.get(event.getEntity())
            .anyActive(
               PermanentEffectPower.class,
               p -> p.effect.stream().anyMatch(e -> Objects.equals(e.effect(), event.getEffect()) && e.amplifier() == instance.getAmplifier())
            )) {
         event.setCanceled(true);
      }
   }
}
