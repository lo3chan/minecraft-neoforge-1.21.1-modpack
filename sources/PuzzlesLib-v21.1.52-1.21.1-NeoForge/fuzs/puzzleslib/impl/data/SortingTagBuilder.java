package fuzs.puzzleslib.impl.data;

import java.util.Comparator;
import java.util.List;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagEntry;

public class SortingTagBuilder extends TagBuilder {
   public List<TagEntry> build() {
      return super.build().stream().sorted(Comparator.comparing(tagEntry -> tagEntry.id)).toList();
   }
}
