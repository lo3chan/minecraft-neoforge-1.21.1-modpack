package de.cristelknight.cristellib.data.condition.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.cristelknight.cristellib.Constants;
import de.cristelknight.cristellib.ModLoadingUtil;
import de.cristelknight.cristellib.data.condition.ICondition;
import de.cristelknight.cristellib.util.ModVersionComparator;
import java.util.Optional;

public record ModLoadedCondition(String modId, Optional<String> optionalVersion) implements ICondition<ModLoadedCondition> {
   public static final Codec<ModLoadedCondition> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            Codec.STRING.fieldOf("modid").forGetter(ModLoadedCondition::modId),
            Codec.STRING.optionalFieldOf("version").forGetter(ModLoadedCondition::optionalVersion)
         )
         .apply(instance, ModLoadedCondition::new)
   );

   @Override
   public boolean test() {
      if (this.optionalVersion.isEmpty()) {
         return ModLoadingUtil.isModLoaded(this.modId);
      } else {
         String version = this.optionalVersion.get();

         for (ModVersionComparator comparator : ModVersionComparator.values()) {
            String sign = comparator.getSerialized();
            if (version.startsWith(sign)) {
               return comparator.test(this.modId, version.replaceFirst(sign, ""));
            }
         }

         Constants.LOG.warn("Couldn't compare \"version\": \"{}\" of \"mod\": \"{}\"", version, this.modId);
         return false;
      }
   }

   @Override
   public Codec<ModLoadedCondition> getCodec() {
      return CODEC;
   }
}
