package dev.latvian.mods.kubejs.recipe;

import com.mojang.datafixers.util.Either;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.item.ItemPredicate;
import dev.latvian.mods.kubejs.script.data.VirtualDataMapFile;
import java.util.stream.Stream;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.datamaps.builtin.Compostable;

public record CompostableRecipesKubeEvent(VirtualDataMapFile<Item, Compostable> compostables) implements KubeEvent {
   public void add(ItemPredicate match, float f) {
      this.add(match, f, false);
   }

   public void add(ItemPredicate match, float f, boolean villager) {
      Compostable data = new Compostable(f, villager);
      dissolve(match).ifLeft(tag -> this.compostables.addTag(tag, data)).ifRight(items -> items.forEach(item -> this.compostables.add(item, data)));
   }

   public void addReplace(ItemPredicate match, float f) {
      this.addReplace(match, f, false);
   }

   public void addReplace(ItemPredicate match, float f, boolean villager) {
      Compostable data = new Compostable(f, villager);
      dissolve(match).ifLeft(tag -> this.compostables.addTag(tag, data, true)).ifRight(items -> items.forEach(item -> this.compostables.add(item, data, true)));
   }

   public void replaceAll() {
      this.compostables.replaceAll();
   }

   public void remove(ItemPredicate match) {
      dissolve(match).ifLeft(this.compostables::removeTag).ifRight(items -> items.forEach(this.compostables::remove));
   }

   public void removeAll() {
      this.compostables.clear();
      this.replaceAll();
   }

   private static Either<TagKey<Item>, Stream<Item>> dissolve(ItemPredicate filter) {
      TagKey<Item> tag = filter instanceof Ingredient ingredient ? ingredient.kjs$getTagKey() : null;
      return tag != null ? Either.left(tag) : Either.right(filter.kjs$getItemStream());
   }
}
