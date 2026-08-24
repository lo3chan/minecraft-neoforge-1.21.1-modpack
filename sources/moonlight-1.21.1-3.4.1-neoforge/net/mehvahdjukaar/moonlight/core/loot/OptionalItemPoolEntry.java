package net.mehvahdjukaar.moonlight.core.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import net.mehvahdjukaar.moonlight.api.MoonlightRegistry;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer.Builder;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import org.jetbrains.annotations.Nullable;

public class OptionalItemPoolEntry extends LootPoolSingletonContainer {
   public static final MapCodec<OptionalItemPoolEntry> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(Codec.STRING.fieldOf("name").forGetter(o -> o.tagOrItemId))
         .and(singletonFields(instance))
         .apply(instance, OptionalItemPoolEntry::new)
   );
   @Nullable
   private final Item item;
   private final String tagOrItemId;

   OptionalItemPoolEntry(String tagOrItemId, int quality, int weight, List<LootItemCondition> lootItemConditions, List<LootItemFunction> lootItemFunctions) {
      super(quality, weight, disableIfInvalid(tagOrItemId, lootItemConditions), lootItemFunctions);
      this.item = getOptional(tagOrItemId);
      this.tagOrItemId = tagOrItemId;
   }

   @Nullable
   private static Item getOptional(String res) {
      if (res.startsWith("#")) {
         TagKey<Item> key = TagKey.create(Registries.ITEM, ResourceLocation.parse(res.substring(1)));
         Iterator var2 = BuiltInRegistries.ITEM.getTagOrEmpty(key).iterator();
         if (var2.hasNext()) {
            Holder<Item> v = (Holder<Item>)var2.next();
            return (Item)v.value();
         } else {
            return null;
         }
      } else {
         return (Item)BuiltInRegistries.ITEM.getOptional(ResourceLocation.parse(res)).orElse(null);
      }
   }

   private static List<LootItemCondition> disableIfInvalid(String res, List<LootItemCondition> lootItemConditions) {
      if (getOptional(res) == null) {
         List<LootItemCondition> newCond = new ArrayList<>();
         newCond.add(LootItemRandomChanceCondition.randomChance(0.0F).build());
         newCond.addAll(lootItemConditions);
         return newCond;
      } else {
         return lootItemConditions;
      }
   }

   public LootPoolEntryType getType() {
      return MoonlightRegistry.LAZY_ITEM.get();
   }

   public void createItemStack(Consumer<ItemStack> stackConsumer, LootContext lootContext) {
      if (this.item != null) {
         stackConsumer.accept(new ItemStack(this.item));
      } else {
         Moonlight.LOGGER.warn("Tried to add an item from a disabled OptionalLootPoolEntry");
      }
   }

   public static Builder<?> lootTableOptionalItem(String itemRes) {
      return simpleBuilder((i, j, lootItemConditions, lootItemFunctions) -> new OptionalItemPoolEntry(itemRes, i, j, lootItemConditions, lootItemFunctions));
   }
}
