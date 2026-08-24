package dev.worldgen.lithostitched.worldgen.blockentitymodifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifier;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifierType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ApplyAll(List<RuleBlockEntityModifier> modifiers) implements RuleBlockEntityModifier {
   public static final MapCodec<ApplyAll> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(RuleBlockEntityModifier.CODEC.listOf().fieldOf("modifiers").forGetter(ApplyAll::modifiers)).apply(instance, ApplyAll::new)
   );
   public static final RuleBlockEntityModifierType<ApplyAll> TYPE = () -> CODEC;

   @Nullable
   public CompoundTag apply(@NotNull RandomSource randomSource, @Nullable CompoundTag compoundTag) {
      for (RuleBlockEntityModifier modifier : this.modifiers) {
         compoundTag = modifier.apply(randomSource, compoundTag);
      }

      return compoundTag;
   }

   @NotNull
   public RuleBlockEntityModifierType<?> getType() {
      return TYPE;
   }
}
