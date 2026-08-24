package snownee.jade.api.config;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.Jade;

public class IgnoreList<T> {
   public List<String> values = List.of();
   public int version = 1;

   public void reload(Registry<T> registry, Consumer<T> consumer) {
      List<Pattern> patterns = Lists.newArrayList();

      for (String value : this.values) {
         try {
            if (value.startsWith("/") && value.endsWith("/") && value.length() > 1) {
               patterns.add(Pattern.compile(value.substring(1, value.length() - 1)));
            } else {
               ResourceLocation id = ResourceLocation.parse(value);
               Optional<T> optional = registry.getOptional(id);
               if (!optional.isPresent()) {
                  throw new IllegalArgumentException("Unknown id: " + id);
               }

               consumer.accept((T)registry.get(id));
            }
         } catch (Exception var9) {
            Jade.LOGGER.error("Failed to parse ignore list entry: %s".formatted(value), var9);
         }
      }

      if (!patterns.isEmpty()) {
         for (Holder<T> holder : registry.asHolderIdMap()) {
            String s = ((ResourceKey)holder.unwrapKey().orElseThrow()).location().toString();

            for (Pattern pattern : patterns) {
               if (pattern.matcher(s).find()) {
                  consumer.accept((T)holder.value());
                  break;
               }
            }
         }
      }
   }
}
