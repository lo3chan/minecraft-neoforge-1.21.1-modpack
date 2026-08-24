package traben.entity_texture_features.features.property_reading.properties.etf_properties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFUtils2;

public class BiomeTagProperty extends RandomProperty {
   private final String input;
   private final List<ResourceLocation> tagsList;
   private final boolean print;

   protected BiomeTagProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
      this.input = RandomProperty.readPropertiesOrThrow(properties, propertyNum, "biomeTag", "biomeTags");
      this.print = this.input.startsWith("print:");
      this.tagsList = Arrays.stream(this.input.replaceFirst("^print:", "").split("\\s+")).map(ETFUtils2::res).collect(Collectors.toCollection(ArrayList::new));
   }

   public static BiomeTagProperty getPropertyOrNull(Properties properties, int propertyNum) {
      try {
         return new BiomeTagProperty(properties, propertyNum);
      } catch (RandomProperty.RandomPropertyException var3) {
         return null;
      }
   }

   @Override
   protected boolean testEntityInternal(ETFEntityRenderState entity) {
      if (entity == null) {
         return this.fail();
      } else {
         Level level = entity.world();
         if (level == null) {
            return this.fail();
         } else {
            Holder<Biome> biome = level.getBiome(entity.blockPos());
            if (biome == null) {
               return this.fail();
            } else {
               Stream<TagKey<Biome>> tagStream = biome.tags();
               return tagStream.map(tag -> {
                  ResourceLocation loc = tag.location();
                  if (this.print) {
                     ETFUtils2.logMessage("BiomeTagProperty: " + this.input + " found tag: " + loc);
                  }

                  return loc;
               }).anyMatch(this.tagsList::contains);
            }
         }
      }
   }

   private boolean fail() {
      if (this.print) {
         ETFUtils2.logMessage("BiomeTagProperty: " + this.input + " failed to read entity");
      }

      return false;
   }

   @NotNull
   @Override
   public String[] getPropertyIds() {
      return new String[]{"biomeTag", "biomeTags"};
   }

   @Override
   protected String getPrintableRuleInfo() {
      return "biomeTag=" + this.input;
   }
}
