package dev.worldgen.lithostitched.worldgen.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Predicate;
import net.minecraft.core.HolderSet;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.jetbrains.annotations.NotNull;

public record CompositeConfig(HolderSet<PlacedFeature> features, CompositeConfig.Type placementType) implements FeatureConfiguration {
   public static final Codec<CompositeConfig> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            PlacedFeature.LIST_CODEC.fieldOf("features").forGetter(CompositeConfig::features),
            CompositeConfig.Type.CODEC.fieldOf("placement_type").orElse(CompositeConfig.Type.NEVER_CANCEL).forGetter(CompositeConfig::placementType)
         )
         .apply(instance, CompositeConfig::new)
   );

   public static enum Type implements StringRepresentable {
      NEVER_CANCEL("never_cancel", success -> true),
      CANCEL_ON_FAILURE("cancel_on_failure", success -> success),
      CANCEL_ON_SUCCESS("cancel_on_success", success -> !success);

      public static final Codec<CompositeConfig.Type> CODEC = StringRepresentable.fromEnum(CompositeConfig.Type::values);
      private final String name;
      private final Predicate<Boolean> continueCondition;

      private Type(String name, Predicate<Boolean> continueCondition) {
         this.name = name;
         this.continueCondition = continueCondition;
      }

      @NotNull
      public String getSerializedName() {
         return this.name;
      }

      public boolean shouldContinue(boolean success) {
         return this.continueCondition.test(success);
      }
   }
}
