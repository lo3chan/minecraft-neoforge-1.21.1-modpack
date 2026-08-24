package net.mehvahdjukaar.moonlight.api.fluids;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Supplier;
import net.mehvahdjukaar.moonlight.api.util.codec.LenientListCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluidContainerList implements Iterable<FluidContainerList.Category> {
   public static final Codec<FluidContainerList> CODEC = LenientListCodec.of(FluidContainerList.Category.CODEC)
      .xmap(FluidContainerList::new, FluidContainerList::getCategories);
   private final Map<Item, FluidContainerList.Category> emptyToFilledMap = new IdentityHashMap<>();

   public FluidContainerList(List<FluidContainerList.Category> categoryList) {
      categoryList.forEach(this::addCategory);
   }

   public FluidContainerList() {
   }

   private void addCategory(FluidContainerList.Category newCategory) {
      if (!newCategory.isEmpty()) {
         if (this.emptyToFilledMap.containsKey(newCategory.emptyContainer)) {
            FluidContainerList.Category c = this.emptyToFilledMap.get(newCategory.emptyContainer);
            if (c.containerCapacity == newCategory.containerCapacity) {
               c.filled.addAll(newCategory.filled);
            }
         } else {
            this.emptyToFilledMap.put(newCategory.emptyContainer, newCategory);
         }
      }
   }

   public Optional<Item> getEmpty(Item filledContainer) {
      for (Entry<Item, FluidContainerList.Category> e : this.emptyToFilledMap.entrySet()) {
         if (e.getValue().getFilledItems().contains(filledContainer)) {
            return Optional.of(e.getKey());
         }
      }

      return Optional.empty();
   }

   public Optional<Item> getFilled(Item emptyContainer) {
      FluidContainerList.Category c = this.emptyToFilledMap.get(emptyContainer);
      return c != null ? c.getFirstFilled() : Optional.empty();
   }

   public Optional<FluidContainerList.Category> getCategoryFromEmpty(Item emptyContainer) {
      return Optional.ofNullable(this.emptyToFilledMap.get(emptyContainer));
   }

   public Optional<FluidContainerList.Category> getCategoryFromFilled(Item filledContainer) {
      return this.getEmpty(filledContainer).map(this.emptyToFilledMap::get);
   }

   public Collection<Item> getPossibleFilled() {
      List<Item> list = new ArrayList<>();
      this.emptyToFilledMap.values().forEach(c -> list.addAll(c.filled));
      return list;
   }

   public Collection<Item> getPossibleEmpty() {
      return this.emptyToFilledMap.keySet();
   }

   public List<FluidContainerList.Category> getCategories() {
      return List.copyOf(this.emptyToFilledMap.values());
   }

   @NotNull
   @Override
   public Iterator<FluidContainerList.Category> iterator() {
      return this.emptyToFilledMap.values().iterator();
   }

   protected void merge(FluidContainerList other) {
      other.emptyToFilledMap.values().forEach(this::addCategory);
   }

   protected void add(Item empty, Item filled, int amount) {
      FluidContainerList.Category c = this.emptyToFilledMap.computeIfAbsent(empty, i -> new FluidContainerList.Category(i, amount));
      c.addItem(filled);
   }

   protected void add(Item empty, Item filled, int amount, SoundEvent fillSound, SoundEvent emptySound) {
      FluidContainerList.Category c = this.emptyToFilledMap.computeIfAbsent(empty, i -> new FluidContainerList.Category(i, amount));
      c.addItem(filled);
      if (c.fillSound == null) {
         c.fillSound = fillSound;
      }

      if (c.emptySound == null) {
         c.emptySound = emptySound;
      }
   }

   public static class Category {
      private static final Supplier<FluidContainerList.Category> EMPTY = Suppliers.memoize(
         () -> new FluidContainerList.Category((Item)BuiltInRegistries.ITEM.get(BuiltInRegistries.ITEM.getDefaultKey()), 1)
      );
      public static final Codec<FluidContainerList.Category> CODEC = RecordCodecBuilder.create(
         instance -> instance.group(
               BuiltInRegistries.ITEM.byNameCodec().fieldOf("empty").forGetter(c -> c.emptyContainer),
               SoftFluid.Capacity.INT_CODEC.fieldOf("capacity").forGetter(FluidContainerList.Category::getCapacity),
               BuiltInRegistries.ITEM.byNameCodec().listOf().fieldOf("filled").forGetter(c -> c.filled),
               BuiltInRegistries.SOUND_EVENT.byNameCodec().optionalFieldOf("fill_sound").forGetter(c -> Optional.ofNullable(c.getFillSound())),
               BuiltInRegistries.SOUND_EVENT.byNameCodec().optionalFieldOf("empty_sound").forGetter(c -> Optional.ofNullable(c.getEmptySound()))
            )
            .apply(instance, FluidContainerList.Category::decode)
      );
      private final Item emptyContainer;
      private final int containerCapacity;
      private SoundEvent fillSound;
      private SoundEvent emptySound;
      private final List<Item> filled = new ArrayList<>();

      private Category(Item emptyContainer, int capacity, @Nullable SoundEvent fillSound, @Nullable SoundEvent emptySound) {
         this.emptyContainer = emptyContainer;
         this.containerCapacity = capacity;
         this.fillSound = fillSound;
         this.emptySound = emptySound;
      }

      private Category(Item emptyContainer, int capacity) {
         this(emptyContainer, capacity, null, null);
      }

      private static FluidContainerList.Category decode(Item empty, int capacity, List<Item> filled) {
         return decode(empty, capacity, filled, Optional.empty(), Optional.empty());
      }

      private static FluidContainerList.Category decode(
         Item empty, int capacity, List<Item> filled, Optional<SoundEvent> fillSound, Optional<SoundEvent> emptySound
      ) {
         FluidContainerList.Category category = new FluidContainerList.Category(empty, capacity, fillSound.orElse(null), emptySound.orElse(null));
         filled.forEach(category::addItem);
         return category.isEmpty() ? EMPTY.get() : category;
      }

      public Item getEmptyContainer() {
         return this.emptyContainer;
      }

      public int getCapacity() {
         return this.containerCapacity;
      }

      @Deprecated(
         forRemoval = true
      )
      public int getAmount() {
         return this.containerCapacity;
      }

      private void addItem(Item i) {
         if (!i.getDefaultInstance().isEmpty() && !this.filled.contains(i)) {
            this.filled.add(i);
         }
      }

      public SoundEvent getFillSound() {
         return this.fillSound == null ? SoundEvents.BOTTLE_FILL : this.fillSound;
      }

      public SoundEvent getEmptySound() {
         return this.emptySound == null ? SoundEvents.BOTTLE_EMPTY : this.emptySound;
      }

      public List<Item> getFilledItems() {
         return this.filled;
      }

      public boolean isEmpty() {
         return this.filled.isEmpty();
      }

      public Optional<Item> getFirstFilled() {
         return this.filled.stream().findFirst();
      }
   }
}
