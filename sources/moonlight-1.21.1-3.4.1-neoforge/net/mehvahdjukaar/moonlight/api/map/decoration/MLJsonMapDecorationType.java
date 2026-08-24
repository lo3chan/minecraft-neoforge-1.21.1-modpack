package net.mehvahdjukaar.moonlight.api.map.decoration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.mehvahdjukaar.moonlight.api.util.math.ColorUtils;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import org.jetbrains.annotations.Nullable;

public final class MLJsonMapDecorationType extends MLMapDecorationType<MLMapDecoration, SimpleMapMarker> {
   private static final ResourceLocation FACTORY_ID = Moonlight.res("json_decoration_type");
   static final Codec<MLJsonMapDecorationType> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            RuleTest.CODEC.lenientOptionalFieldOf("target_block").forGetter(MLJsonMapDecorationType::getTarget),
            ComponentSerialization.FLAT_CODEC.optionalFieldOf("name").forGetter(MLJsonMapDecorationType::getDisplayName),
            Codec.FLOAT.optionalFieldOf("rotation", 0.0F).forGetter(MLJsonMapDecorationType::getRotation),
            ColorUtils.CODEC.optionalFieldOf("map_color", 0).forGetter(MLJsonMapDecorationType::getDefaultMapColor),
            RegistryCodecs.homogeneousList(Registries.STRUCTURE)
               .lenientOptionalFieldOf("target_structures")
               .forGetter(MLJsonMapDecorationType::getAssociatedStructure)
         )
         .apply(instance, MLJsonMapDecorationType::new)
   );
   private final Optional<RuleTest> target;
   private final Optional<Component> name;
   private final Optional<HolderSet<Structure>> structures;
   private final int defaultMapColor;
   private final float defaultRotation;

   public MLJsonMapDecorationType(Optional<RuleTest> target) {
      this(target, Optional.empty(), 0.0F, 0);
   }

   public MLJsonMapDecorationType(Optional<RuleTest> target, Optional<Component> name, float rotation, int mapColor) {
      this(target, name, rotation, mapColor, Optional.empty());
   }

   public MLJsonMapDecorationType(Optional<RuleTest> target, Optional<Component> name, float rotation, int mapColor, Optional<HolderSet<Structure>> structure) {
      super(SimpleMapMarker.DIRECT_CODEC, MLMapDecoration.DIRECT_CODEC);
      this.target = target;
      this.name = name;
      this.defaultRotation = rotation;
      this.structures = structure;
      this.defaultMapColor = mapColor;
   }

   @Override
   public ResourceLocation getCustomFactoryID() {
      return FACTORY_ID;
   }

   public Optional<RuleTest> getTarget() {
      return this.target;
   }

   public Optional<Component> getDisplayName() {
      return this.name;
   }

   public float getRotation() {
      return this.defaultRotation;
   }

   @Override
   public Optional<HolderSet<Structure>> getAssociatedStructure() {
      return this.structures;
   }

   @Override
   public int getDefaultMapColor() {
      return this.defaultMapColor;
   }

   @Override
   public boolean isFromWorld() {
      return this.target.isPresent();
   }

   @Nullable
   public SimpleMapMarker createMarkerFromWorld(LevelAccessor reader, BlockPos pos) {
      if (this.target.isPresent() && this.target.get().test(reader.getBlockState(pos), RandomSource.create())) {
         Optional<Component> name = this.getDisplayName();
         if (name.isEmpty() && reader.getBlockEntity(pos) instanceof Nameable n) {
            name = Optional.ofNullable(n.getCustomName());
         }

         return new SimpleMapMarker(this.wrapAsHolder(reader.registryAccess()), pos, this.defaultRotation, name);
      } else {
         return null;
      }
   }
}
