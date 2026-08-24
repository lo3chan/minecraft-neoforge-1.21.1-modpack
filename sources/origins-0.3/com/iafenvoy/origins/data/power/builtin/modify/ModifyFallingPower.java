package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data._common.helper.ModifierPowerHelper;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.iafenvoy.origins.util.math.Modifier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber
public class ModifyFallingPower extends Power implements ModifierPowerHelper {
   public static final MapCodec<ModifyFallingPower> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(
            Power.BaseSettings.CODEC.forGetter(Power::getSettings),
            Codec.BOOL.optionalFieldOf("take_fall_damage", true).forGetter(ModifyFallingPower::shouldTakeFallDamage),
            CombinedCodecs.MODIFIER.optionalFieldOf("modifier", List.of()).forGetter(ModifyFallingPower::getModifier)
         )
         .apply(i, ModifyFallingPower::new)
   );
   private final boolean takeFallDamage;
   private final List<Modifier> modifier;

   public ModifyFallingPower(Power.BaseSettings settings, boolean takeFallDamage, List<Modifier> modifier) {
      super(settings);
      this.takeFallDamage = takeFallDamage;
      this.modifier = modifier;
   }

   public boolean shouldTakeFallDamage() {
      return this.takeFallDamage;
   }

   @Override
   public List<Modifier> getModifier() {
      return this.modifier;
   }

   @NotNull
   @Override
   public MapCodec<? extends Power> codec() {
      return CODEC;
   }

   @SubscribeEvent
   public static void onFall(LivingFallEvent event) {
      LivingEntity living = event.getEntity();
      if (PowerHelper.get(living).anyActive(ModifyFallingPower.class, x -> !x.shouldTakeFallDamage())) {
         event.setDamageMultiplier(0.0F);
      }
   }

   public static double apply(LivingEntity living, double originalValue) {
      double modifier = PowerHelper.get(living).modify(ModifyFallingPower.class, originalValue);
      return modifier != originalValue && modifier >= 0.0 ? modifier : originalValue;
   }
}
