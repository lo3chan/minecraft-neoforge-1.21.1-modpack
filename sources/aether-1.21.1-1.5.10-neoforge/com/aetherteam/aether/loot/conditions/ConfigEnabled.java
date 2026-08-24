package com.aetherteam.aether.loot.conditions;

import com.aetherteam.aether.data.ConfigSerializationUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

public class ConfigEnabled implements LootItemCondition {
   public static final MapCodec<ConfigEnabled> CODEC = RecordCodecBuilder.mapCodec(
      builder -> builder.group(Codec.STRING.fieldOf("config").forGetter(instance -> ConfigSerializationUtil.serialize(instance.config)))
         .apply(builder, e -> new ConfigEnabled(ConfigSerializationUtil.deserialize(e)))
   );
   private final ConfigValue<Boolean> config;

   public ConfigEnabled(ConfigValue<Boolean> config) {
      this.config = config;
   }

   public LootItemConditionType getType() {
      return (LootItemConditionType)AetherLootConditions.CONFIG_ENABLED.get();
   }

   public boolean test(LootContext lootContext) {
      return (Boolean)this.config.get();
   }

   public static Builder isEnabled(ConfigValue<Boolean> config) {
      return () -> new ConfigEnabled(config);
   }
}
