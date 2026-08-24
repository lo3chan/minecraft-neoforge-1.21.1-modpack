package de.cristelknight.cristellib.data.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record StructureSetData(String modId, List<ResourceLocation> sets) {
   public static final Codec<StructureSetData> CODEC = RecordCodecBuilder.create(
      builder -> builder.group(
            Codec.STRING.fieldOf("modid").forGetter(config -> String.valueOf(config.modId())),
            Codec.list(ResourceLocation.CODEC).fieldOf("structure_set").orElse(List.of()).forGetter(config -> config.sets)
         )
         .apply(builder, StructureSetData::new)
   );

   @NotNull
   @Override
   public String toString() {
      return "StructureSetData: " + this.modId() + ": " + this.sets.toString();
   }
}
