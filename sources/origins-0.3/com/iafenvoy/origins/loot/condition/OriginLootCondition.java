package com.iafenvoy.origins.loot.condition;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data.layer.Layer;
import com.iafenvoy.origins.data.origin.Origin;
import com.iafenvoy.origins.registry.OriginsLootItemConditions;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContext.EntityTarget;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;

public record OriginLootCondition(EntityTarget target, Holder<Origin> origin, Optional<Holder<Layer>> layer) implements LootItemCondition {
   public static final MapCodec<OriginLootCondition> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            EntityTarget.CODEC.optionalFieldOf("entity", EntityTarget.THIS).forGetter(OriginLootCondition::target),
            Origin.CODEC.fieldOf("origin").forGetter(OriginLootCondition::origin),
            Layer.CODEC.optionalFieldOf("layer").forGetter(OriginLootCondition::layer)
         )
         .apply(instance, OriginLootCondition::new)
   );

   @NotNull
   public LootItemConditionType getType() {
      return (LootItemConditionType)OriginsLootItemConditions.ORIGIN.get();
   }

   public boolean test(LootContext lootContext) {
      Entity entity = (Entity)lootContext.getParamOrNull(this.target.getParam());
      return entity == null
         ? false
         : OriginDataHolder.optional(entity)
            .map(h -> this.layer.<Boolean>map(l -> h.hasOrigin((Holder<Layer>)l, this.origin)).orElseGet(() -> h.hasOrigin(this.origin)))
            .orElse(false);
   }
}
