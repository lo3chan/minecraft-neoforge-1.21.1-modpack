package com.iafenvoy.origins.loot.condition;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.registry.OriginsLootItemConditions;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContext.EntityTarget;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;

public record PowerLootCondition(EntityTarget target, Holder<Power> power, Optional<ResourceLocation> source) implements LootItemCondition {
   public static final MapCodec<PowerLootCondition> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            EntityTarget.CODEC.optionalFieldOf("entity", EntityTarget.THIS).forGetter(PowerLootCondition::target),
            Power.CODEC.fieldOf("power").forGetter(PowerLootCondition::power),
            ResourceLocation.CODEC.optionalFieldOf("source").forGetter(PowerLootCondition::source)
         )
         .apply(instance, PowerLootCondition::new)
   );

   @NotNull
   public LootItemConditionType getType() {
      return (LootItemConditionType)OriginsLootItemConditions.POWER.get();
   }

   public boolean test(LootContext lootContext) {
      Entity entity = (Entity)lootContext.getParamOrNull(this.target.getParam());
      return entity == null ? false : OriginDataHolder.optionalStream(entity).anyMatch(h -> h.hasPower(this.power));
   }
}
