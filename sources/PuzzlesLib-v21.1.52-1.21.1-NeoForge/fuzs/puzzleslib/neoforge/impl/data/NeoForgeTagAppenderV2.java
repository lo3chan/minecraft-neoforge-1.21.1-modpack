package fuzs.puzzleslib.neoforge.impl.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import fuzs.puzzleslib.api.data.v2.tags.AbstractTagAppender;
import java.util.List;
import java.util.function.Function;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagEntry;
import net.minecraft.util.ExtraCodecs.TagOrElementLocation;
import org.jetbrains.annotations.Nullable;

public final class NeoForgeTagAppenderV2<T> extends AbstractTagAppender<T> {
   public NeoForgeTagAppenderV2(TagBuilder tagBuilder, @Nullable Function<T, ResourceKey<T>> keyExtractor) {
      super(tagBuilder, keyExtractor);
   }

   @Override
   public AbstractTagAppender<T> remove(ResourceLocation resourceLocation) {
      this.tagBuilder.removeElement(resourceLocation);
      return this;
   }

   @Override
   public AbstractTagAppender<T> removeTag(ResourceLocation resourceLocation) {
      this.tagBuilder.removeTag(resourceLocation);
      return this;
   }

   @Override
   public List<String> asStringList() {
      Builder<String> builder = ImmutableList.builder();

      for (TagEntry tagEntry : this.tagBuilder.build()) {
         builder.add(new TagOrElementLocation(tagEntry.getId(), tagEntry.isTag()).toString());
      }

      for (TagEntry tagEntry : this.tagBuilder.getRemoveEntries().toList()) {
         builder.add("!" + new TagOrElementLocation(tagEntry.getId(), tagEntry.isTag()));
      }

      return builder.build();
   }
}
