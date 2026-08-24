package net.mehvahdjukaar.moonlight.api.resources.recipe.platform;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Predicate;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ICondition.IContext;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public record OptionalRecipeCondition(ResourceLocation id, Predicate<String> predicate, String conditionValue) implements ICondition {
   public static MapCodec<OptionalRecipeCondition> createCodec(ResourceLocation id, Predicate<String> predicate) {
      String name = id.getPath();
      return RecordCodecBuilder.mapCodec(
         builder -> builder.group(Codec.STRING.fieldOf(name).forGetter(o -> o.id().getPath()))
            .apply(builder, s -> new OptionalRecipeCondition(id, predicate, s))
      );
   }

   public boolean test(IContext context) {
      return this.predicate.test(this.conditionValue);
   }

   public MapCodec<? extends ICondition> codec() {
      return (MapCodec<? extends ICondition>)NeoForgeRegistries.CONDITION_SERIALIZERS.get(this.id);
   }
}
