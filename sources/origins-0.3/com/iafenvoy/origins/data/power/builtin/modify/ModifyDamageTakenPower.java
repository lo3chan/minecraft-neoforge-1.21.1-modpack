package com.iafenvoy.origins.data.power.builtin.modify;

import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data._common.helper.ModifierPowerHelper;
import com.iafenvoy.origins.data.action.BiEntityAction;
import com.iafenvoy.origins.data.action.EntityAction;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.data.condition.DamageCondition;
import com.iafenvoy.origins.data.condition.EntityCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.util.codec.CombinedCodecs;
import com.iafenvoy.origins.util.math.Modifier;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber
public class ModifyDamageTakenPower extends Power implements ModifierPowerHelper {
   public static final MapCodec<ModifyDamageTakenPower> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(
            Power.BaseSettings.CODEC.forGetter(Power::getSettings),
            CombinedCodecs.MODIFIER.fieldOf("modifier").forGetter(ModifyDamageTakenPower::getModifier),
            BiEntityAction.optionalCodec("bientity_action").forGetter(ModifyDamageTakenPower::getBiEntityAction),
            EntityAction.optionalCodec("self_action").forGetter(ModifyDamageTakenPower::getSelfAction),
            EntityAction.optionalCodec("attacker_action").forGetter(ModifyDamageTakenPower::getAttackerAction),
            BiEntityCondition.optionalCodec("bientity_condition").forGetter(ModifyDamageTakenPower::getBiEntityCondition),
            EntityCondition.optionalCodec("apply_armor_condition").forGetter(ModifyDamageTakenPower::getApplyArmorCondition),
            EntityCondition.optionalCodec("damage_armor_condition").forGetter(ModifyDamageTakenPower::getDamageArmorCondition),
            DamageCondition.optionalCodec("damage_condition").forGetter(ModifyDamageTakenPower::getDamageCondition)
         )
         .apply(i, ModifyDamageTakenPower::new)
   );
   private final List<Modifier> modifier;
   private final BiEntityAction biEntityAction;
   private final EntityAction selfAction;
   private final EntityAction attackerAction;
   private final BiEntityCondition biEntityCondition;
   private final EntityCondition applyArmorCondition;
   private final EntityCondition damageArmorCondition;
   private final DamageCondition damageCondition;

   public ModifyDamageTakenPower(
      Power.BaseSettings settings,
      List<Modifier> modifier,
      BiEntityAction biEntityAction,
      EntityAction selfAction,
      EntityAction attackerAction,
      BiEntityCondition biEntityCondition,
      EntityCondition applyArmorCondition,
      EntityCondition damageArmorCondition,
      DamageCondition damageCondition
   ) {
      super(settings);
      this.modifier = modifier;
      this.biEntityAction = biEntityAction;
      this.selfAction = selfAction;
      this.attackerAction = attackerAction;
      this.biEntityCondition = biEntityCondition;
      this.applyArmorCondition = applyArmorCondition;
      this.damageArmorCondition = damageArmorCondition;
      this.damageCondition = damageCondition;
   }

   @Override
   public List<Modifier> getModifier() {
      return this.modifier;
   }

   public BiEntityAction getBiEntityAction() {
      return this.biEntityAction;
   }

   public EntityAction getSelfAction() {
      return this.selfAction;
   }

   public EntityAction getAttackerAction() {
      return this.attackerAction;
   }

   public BiEntityCondition getBiEntityCondition() {
      return this.biEntityCondition;
   }

   public EntityCondition getApplyArmorCondition() {
      return this.applyArmorCondition;
   }

   public EntityCondition getDamageArmorCondition() {
      return this.damageArmorCondition;
   }

   public DamageCondition getDamageCondition() {
      return this.damageCondition;
   }

   @NotNull
   @Override
   public MapCodec<? extends Power> codec() {
      return CODEC;
   }

   private boolean check(Entity entity, DamageSource source, float amount) {
      if (!this.damageCondition.test(source, amount)) {
         return false;
      } else {
         Entity attacker = source.getEntity();
         return attacker == null ? true : this.biEntityCondition.test(attacker, entity);
      }
   }

   public void execute(Entity entity, DamageSource source) {
      this.selfAction.execute(entity);
      if (source.getEntity() != null) {
         this.attackerAction.execute(source.getEntity());
         this.biEntityAction.execute(source.getEntity(), entity);
      }
   }

   @SubscribeEvent
   public static void onDamage(Pre event) {
      Entity target = event.getEntity();
      DamageSource s = event.getSource();
      PowerHelper.get(target).execute(ModifyDamageTakenPower.class, (h, p) -> {
         float baseValue = event.getNewDamage();
         if (p.check(target, s, baseValue)) {
            event.setNewDamage(p.modify(h, baseValue));
            p.selfAction.execute(target);
            Entity attacker = s.getEntity();
            if (attacker != null) {
               p.attackerAction.execute(attacker);
               p.biEntityAction.execute(attacker, target);
            }
         }
      });
   }
}
