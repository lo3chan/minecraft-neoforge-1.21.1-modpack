package net.mehvahdjukaar.moonlight.core.loot;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.Set;
import net.mehvahdjukaar.moonlight.api.MoonlightRegistry;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.Nullable;

public class OptionalPropertyCondition implements LootItemCondition {
   public static final MapCodec<OptionalPropertyCondition> CODEC = RecordCodecBuilder.mapCodec(
         i -> i.group(
               ResourceLocation.CODEC.fieldOf("block").forGetter(o -> o.blockId),
               StatePropertiesPredicate.CODEC.optionalFieldOf("properties").forGetter(o -> o.properties)
            )
            .apply(i, OptionalPropertyCondition::new)
      )
      .validate(OptionalPropertyCondition::validate);
   @Nullable
   protected final Block block;
   protected final Optional<StatePropertiesPredicate> properties;
   protected final ResourceLocation blockId;

   OptionalPropertyCondition(ResourceLocation blockId, Optional<StatePropertiesPredicate> predicate) {
      this.properties = predicate;
      this.block = (Block)BuiltInRegistries.BLOCK.getOptional(blockId).orElse(null);
      this.blockId = blockId;
   }

   public LootItemConditionType getType() {
      return MoonlightRegistry.LAZY_PROPERTY.get();
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of(LootContextParams.BLOCK_STATE);
   }

   public boolean test(LootContext lootContext) {
      if (this.block == null) {
         return false;
      } else {
         BlockState blockState = (BlockState)lootContext.getParamOrNull(LootContextParams.BLOCK_STATE);
         return blockState != null && blockState.is(this.block) && (this.properties.isEmpty() || this.properties.get().matches(blockState));
      }
   }

   private static DataResult<OptionalPropertyCondition> validate(OptionalPropertyCondition condition) {
      return condition.block != null
         ? condition.properties
            .<String>flatMap(prop -> prop.checkState(condition.block.getStateDefinition()))
            .map(string -> DataResult.error(() -> "Block " + condition + " has no property" + string))
            .orElse(DataResult.success(condition))
         : DataResult.success(condition);
   }
}
