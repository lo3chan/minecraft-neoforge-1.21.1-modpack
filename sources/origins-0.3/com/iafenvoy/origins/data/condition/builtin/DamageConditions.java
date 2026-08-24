package com.iafenvoy.origins.data.condition.builtin;

import com.iafenvoy.origins.data.condition.AlwaysTrueCondition;
import com.iafenvoy.origins.data.condition.ConditionRegistries;
import com.iafenvoy.origins.data.condition.DamageCondition;
import com.iafenvoy.origins.data.condition.builtin.damage.AmountCondition;
import com.iafenvoy.origins.data.condition.builtin.damage.AttackerCondition;
import com.iafenvoy.origins.data.condition.builtin.damage.FireDamageCondition;
import com.iafenvoy.origins.data.condition.builtin.damage.IdCondition;
import com.iafenvoy.origins.data.condition.builtin.damage.InTagCondition;
import com.iafenvoy.origins.data.condition.builtin.damage.MagicDamageCondition;
import com.iafenvoy.origins.data.condition.builtin.damage.ProjectileCondition;
import com.iafenvoy.origins.data.condition.builtin.damage.TypeCondition;
import com.iafenvoy.origins.data.condition.builtin.damage.meta.AndCondition;
import com.iafenvoy.origins.data.condition.builtin.damage.meta.ChanceCondition;
import com.iafenvoy.origins.data.condition.builtin.damage.meta.ConstantCondition;
import com.iafenvoy.origins.data.condition.builtin.damage.meta.NotCondition;
import com.iafenvoy.origins.data.condition.builtin.damage.meta.OrCondition;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class DamageConditions {
   public static final DeferredRegister<MapCodec<? extends DamageCondition>> REGISTRY = DeferredRegister.create(ConditionRegistries.DAMAGE_CONDITION, "origins");
   public static final DeferredHolder<MapCodec<? extends DamageCondition>, MapCodec<AlwaysTrueCondition>> ALWAYS_TRUE = REGISTRY.register(
      "always_true", () -> AlwaysTrueCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends DamageCondition>, MapCodec<AmountCondition>> AMOUNT = REGISTRY.register(
      "amount", () -> AmountCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends DamageCondition>, MapCodec<AttackerCondition>> ATTACKER = REGISTRY.register(
      "attacker", () -> AttackerCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends DamageCondition>, MapCodec<IdCondition>> ID = REGISTRY.register("id", () -> IdCondition.CODEC);
   public static final DeferredHolder<MapCodec<? extends DamageCondition>, MapCodec<InTagCondition>> IN_TAG = REGISTRY.register(
      "in_tag", () -> InTagCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends DamageCondition>, MapCodec<MagicDamageCondition>> MAGIC = REGISTRY.register(
      "magic", () -> MagicDamageCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends DamageCondition>, MapCodec<ProjectileCondition>> PROJECTILE = REGISTRY.register(
      "projectile", () -> ProjectileCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends DamageCondition>, MapCodec<TypeCondition>> TYPE = REGISTRY.register("type", () -> TypeCondition.CODEC);
   public static final DeferredHolder<MapCodec<? extends DamageCondition>, MapCodec<AndCondition>> AND = REGISTRY.register("and", () -> AndCondition.CODEC);
   public static final DeferredHolder<MapCodec<? extends DamageCondition>, MapCodec<ChanceCondition>> CHANCE = REGISTRY.register(
      "chance", () -> ChanceCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends DamageCondition>, MapCodec<ConstantCondition>> CONSTANT = REGISTRY.register(
      "constant", () -> ConstantCondition.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends DamageCondition>, MapCodec<NotCondition>> NOT = REGISTRY.register("not", () -> NotCondition.CODEC);
   public static final DeferredHolder<MapCodec<? extends DamageCondition>, MapCodec<OrCondition>> OR = REGISTRY.register("or", () -> OrCondition.CODEC);
   public static final DeferredHolder<MapCodec<? extends DamageCondition>, MapCodec<FireDamageCondition>> FIRE = REGISTRY.register(
      "fire", () -> FireDamageCondition.CODEC
   );
}
