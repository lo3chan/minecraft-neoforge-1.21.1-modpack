package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data._common.helper.ModifierPowerHelper;
import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.data.condition.DamageCondition;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.iafenvoy.origins.util.math.Modifier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber
public class ModifyEnchantmentDamageDealtPower extends Power implements ModifierPowerHelper {
   public static final MapCodec<ModifyEnchantmentDamageDealtPower> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            Power.BaseSettings.CODEC.forGetter(Power::getSettings),
            Enchantment.CODEC.fieldOf("enchantment").forGetter(ModifyEnchantmentDamageDealtPower::getEnchantment),
            Codec.FLOAT.fieldOf("base_value").forGetter(ModifyEnchantmentDamageDealtPower::getBaseValue),
            DamageCondition.optionalCodec("damage_condition").forGetter(ModifyEnchantmentDamageDealtPower::getDamageCondition),
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(ModifyEnchantmentDamageDealtPower::getBiEntityCondition),
            EntityCondition.optionalCodec("target_condition").forGetter(ModifyEnchantmentDamageDealtPower::getTargetCondition),
            CombinedCodecs.MODIFIER.optionalFieldOf("modifier", List.of()).forGetter(ModifyEnchantmentDamageDealtPower::getModifier),
            BiEntityAction.optionalCodec("bientity_action").forGetter(ModifyEnchantmentDamageDealtPower::getBiEntityAction)
         )
         .apply(instance, ModifyEnchantmentDamageDealtPower::new)
   );
   private final Holder<Enchantment> enchantment;
   private final float baseValue;
   private final DamageCondition damageCondition;
   private final BiEntityCondition biEntityCondition;
   private final EntityCondition targetCondition;
   private final List<Modifier> modifier;
   private final BiEntityAction biEntityAction;

   public ModifyEnchantmentDamageDealtPower(
      Power.BaseSettings settings,
      Holder<Enchantment> enchantment,
      float baseValue,
      DamageCondition damageCondition,
      BiEntityCondition biEntityCondition,
      EntityCondition targetCondition,
      List<Modifier> modifier,
      BiEntityAction biEntityAction
   ) {
      super(settings);
      this.enchantment = enchantment;
      this.baseValue = baseValue;
      this.damageCondition = damageCondition;
      this.biEntityCondition = biEntityCondition;
      this.targetCondition = targetCondition;
      this.modifier = modifier;
      this.biEntityAction = biEntityAction;
   }

   public Holder<Enchantment> getEnchantment() {
      return this.enchantment;
   }

   public float getBaseValue() {
      return this.baseValue;
   }

   public DamageCondition getDamageCondition() {
      return this.damageCondition;
   }

   public BiEntityCondition getBiEntityCondition() {
      return this.biEntityCondition;
   }

   public EntityCondition getTargetCondition() {
      return this.targetCondition;
   }

   @Override
   public List<Modifier> getModifier() {
      return this.modifier;
   }

   public BiEntityAction getBiEntityAction() {
      return this.biEntityAction;
   }

   @NotNull
   @Override
   public MapCodec<? extends Power> codec() {
      return CODEC;
   }

   private float getAdditionalDamage(OriginDataHolder holder, int enchantmentLevel) {
      float value = this.baseValue;

      for (int level = 1; level < enchantmentLevel; level++) {
         value = this.modify(holder, value);
      }

      return value;
   }

   @SubscribeEvent
   public static void onDamage(Pre event) {
      if (!event.getSource().is(DamageTypeTags.IS_PROJECTILE) && event.getSource().getEntity() instanceof LivingEntity attacker) {
         LivingEntity var3 = event.getEntity();
         PowerHelper.get(attacker)
            .execute(
               ModifyEnchantmentDamageDealtPower.class,
               (holder, power) -> {
                  int enchantmentLevel = attacker.getMainHandItem().getEnchantmentLevel(power.enchantment);
                  if (enchantmentLevel > 0
                     && power.damageCondition.test(event.getSource(), event.getNewDamage())
                     && power.targetCondition.test(var3)
                     && power.biEntityCondition.test(attacker, var3)) {
                     event.setNewDamage(event.getNewDamage() + power.getAdditionalDamage(holder, enchantmentLevel));
                     power.biEntityAction.execute(attacker, var3);
                  }
               }
            );
      }
   }
}
