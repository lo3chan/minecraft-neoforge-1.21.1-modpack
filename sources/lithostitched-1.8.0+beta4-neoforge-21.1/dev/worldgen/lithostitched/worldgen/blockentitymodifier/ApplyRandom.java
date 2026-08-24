package dev.worldgen.lithostitched.worldgen.blockentitymodifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.util.WeightedList;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifier;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifierType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ApplyRandom(WeightedList<RuleBlockEntityModifier> modifiers) implements RuleBlockEntityModifier {
   public static final MapCodec<ApplyRandom> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(WeightedList.codec(RuleBlockEntityModifier.CODEC).fieldOf("modifiers").forGetter(ApplyRandom::modifiers))
         .apply(instance, ApplyRandom::new)
   );
   public static final RuleBlockEntityModifierType<ApplyRandom> TYPE = () -> CODEC;

   @Nullable
   public CompoundTag apply(@NotNull RandomSource randomSource, @Nullable CompoundTag compoundTag) {
      Optional<RuleBlockEntityModifier> modifier = this.modifiers.getRandom(randomSource);
      return modifier.isPresent() ? modifier.get().apply(randomSource, compoundTag) : compoundTag;
   }

   @NotNull
   public RuleBlockEntityModifierType<?> getType() {
      return TYPE;
   }
}
