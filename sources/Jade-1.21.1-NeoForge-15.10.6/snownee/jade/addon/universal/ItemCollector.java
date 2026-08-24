package snownee.jade.addon.universal;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import snownee.jade.api.Accessor;
import snownee.jade.api.view.ViewGroup;

public class ItemCollector<T> {
   public static final int MAX_SIZE = 54;
   public static final ItemCollector<?> EMPTY = new ItemCollector(null);
   private static final Predicate<ItemStack> NON_EMPTY = stack -> {
      if (stack.isEmpty()) {
         return false;
      } else {
         if (stack.has(DataComponents.CUSTOM_MODEL_DATA)) {
            CustomData customData = (CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
            CompoundTag tag = customData.getUnsafe();

            for (String key : tag.getAllKeys()) {
               if (key.toLowerCase(Locale.ENGLISH).endsWith("clear") && tag.getBoolean(key)) {
                  return false;
               }
            }
         }

         return true;
      }
   };
   private final Object2IntLinkedOpenHashMap<ItemCollector.ItemDefinition> items = new Object2IntLinkedOpenHashMap();
   private final ItemIterator<T> iterator;
   public long version;
   public long lastTimeFinished;
   public boolean lastTimeIsEmpty;
   public List<ViewGroup<ItemStack>> mergedResult;

   public ItemCollector(ItemIterator<T> iterator) {
      this.iterator = iterator;
   }

   public List<ViewGroup<ItemStack>> update(Accessor<?> accessor) {
      if (this.iterator == null) {
         return null;
      } else {
         T container = this.iterator.find(accessor);
         if (container == null) {
            return null;
         } else {
            long currentVersion = this.iterator.getVersion(container);
            long gameTime = accessor.getLevel().getGameTime();
            if (this.mergedResult != null && this.iterator.isFinished()) {
               if (this.version == currentVersion) {
                  return this.mergedResult;
               }

               if (this.lastTimeFinished + 5L > gameTime) {
                  return this.mergedResult;
               }

               this.iterator.reset();
            }

            AtomicInteger count = new AtomicInteger();
            this.iterator.populate(container, 108).forEach(stack -> {
               count.incrementAndGet();
               if (NON_EMPTY.test(stack)) {
                  ItemCollector.ItemDefinition def = new ItemCollector.ItemDefinition(stack);
                  this.items.addTo(def, stack.getCount());
               }
            });
            this.iterator.afterPopulate(count.get());
            if (this.mergedResult != null && !this.iterator.isFinished()) {
               this.updateCollectingProgress((ViewGroup<ItemStack>)this.mergedResult.getFirst());
               return this.mergedResult;
            } else {
               List<ItemStack> partialResult = this.items.object2IntEntrySet().stream().limit(54L).map(entry -> {
                  ItemCollector.ItemDefinition def = (ItemCollector.ItemDefinition)entry.getKey();
                  return def.toStack(entry.getIntValue());
               }).toList();
               List<ViewGroup<ItemStack>> groups = List.of(this.updateCollectingProgress(new ViewGroup<>(partialResult)));
               if (this.iterator.isFinished()) {
                  this.mergedResult = groups;
                  this.lastTimeIsEmpty = ((ViewGroup)this.mergedResult.getFirst()).views.isEmpty();
                  this.version = currentVersion;
                  this.lastTimeFinished = gameTime;
                  this.items.clear();
               }

               return groups;
            }
         }
      }
   }

   protected ViewGroup<ItemStack> updateCollectingProgress(ViewGroup<ItemStack> group) {
      if (this.lastTimeIsEmpty && group.views.isEmpty()) {
         return group;
      } else {
         float progress = this.iterator.getCollectingProgress();
         CompoundTag data = group.getExtraData();
         if (!Float.isNaN(progress) && !(progress >= 1.0F)) {
            data.putFloat("Collecting", progress);
         } else {
            data.remove("Collecting");
         }

         return group;
      }
   }

   public record ItemDefinition(Item item, DataComponentPatch components) {
      ItemDefinition(ItemStack stack) {
         this(stack.getItem(), stack.getComponentsPatch());
      }

      public ItemStack toStack(int count) {
         ItemStack itemStack = new ItemStack(this.item, count);
         itemStack.applyComponents(this.components);
         return itemStack;
      }
   }
}
