package dev.worldgen.lithostitched.worldgen.modifier.template;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;

public record TemplateList(List<ResourceLocation> templates) {
   public static final Codec<TemplateList> CODEC = ExtraCodecs.nonEmptyList(ResourceLocation.CODEC.listOf()).xmap(TemplateList::new, TemplateList::templates);

   public TemplateList(List<ResourceLocation> templates) {
      this.templates = new ArrayList<>(templates);
   }

   public ResourceLocation getRandom(RandomSource randomSource) {
      return this.templates.get(randomSource.nextInt(this.templates.size()));
   }

   public void addAll(List<ResourceLocation> templates) {
      this.templates.addAll(templates);
   }
}
