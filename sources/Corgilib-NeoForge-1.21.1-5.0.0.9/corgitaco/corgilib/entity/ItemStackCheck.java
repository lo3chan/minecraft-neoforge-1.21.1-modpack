package corgitaco.corgilib.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corgitaco.corgilib.comparator.DoubleComparator;
import corgitaco.corgilib.serialization.codec.CodecUtil;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class ItemStackCheck {
   public static final Codec<ItemStackCheck> CODEC = RecordCodecBuilder.create(
      builder -> builder.group(
            CodecUtil.ITEM_CODEC.fieldOf("item").forGetter(itemStackCheck -> itemStackCheck.item),
            DoubleComparator.CODEC.optionalFieldOf("durability_is").forGetter(itemStackCheck -> itemStackCheck.durabilityComparator),
            DoubleComparator.CODEC.optionalFieldOf("stack_size_is").forGetter(itemStackCheck -> itemStackCheck.stackSizeComparator),
            Codec.unboundedMap(Enchantment.CODEC, DoubleComparator.CODEC)
               .optionalFieldOf("enchantment_check")
               .forGetter(itemStackCheck -> itemStackCheck.enchantmentLevelComparator)
         )
         .apply(builder, ItemStackCheck::new)
   );
   private final Item item;
   private final Optional<DoubleComparator> durabilityComparator;
   private final Optional<DoubleComparator> stackSizeComparator;
   private final Optional<Map<Holder<Enchantment>, DoubleComparator>> enchantmentLevelComparator;

   public ItemStackCheck(
      Item item,
      Optional<DoubleComparator> durabilityComparator,
      Optional<DoubleComparator> stackSizeComparator,
      Optional<Map<Holder<Enchantment>, DoubleComparator>> enchantmentLevelComparator
   ) {
      this.item = item;
      if (durabilityComparator.isEmpty() && stackSizeComparator.isEmpty() && enchantmentLevelComparator.isEmpty()) {
         throw new IllegalArgumentException("We need at least one check in an Item Stack Check!");
      } else {
         this.durabilityComparator = durabilityComparator;
         this.stackSizeComparator = stackSizeComparator;
         this.enchantmentLevelComparator = enchantmentLevelComparator;
      }
   }

   public boolean test(ItemStack itemStack) {
      if (this.item != itemStack.getItem()) {
         return false;
      } else if (this.durabilityComparator.isPresent() && !this.durabilityComparator.get().check(itemStack.getDamageValue())) {
         return false;
      } else if (this.stackSizeComparator.isPresent() && !this.stackSizeComparator.get().check(itemStack.getCount())) {
         return false;
      } else {
         if (this.enchantmentLevelComparator.isPresent()) {
            Map<Holder<Enchantment>, DoubleComparator> enchantmentComparator = this.enchantmentLevelComparator.get();
            ItemEnchantments enchantments = itemStack.getEnchantments();

            for (Holder<Enchantment> enchantmentHolder : enchantments.keySet()) {
               if (enchantmentComparator.containsKey(enchantmentHolder)) {
                  DoubleComparator doubleComparator = enchantmentComparator.get(enchantmentHolder);
                  if (!doubleComparator.check(enchantments.getLevel(enchantmentHolder))) {
                     return false;
                  }
               }
            }
         }

         return true;
      }
   }

   public Item getItem() {
      return this.item;
   }
}
