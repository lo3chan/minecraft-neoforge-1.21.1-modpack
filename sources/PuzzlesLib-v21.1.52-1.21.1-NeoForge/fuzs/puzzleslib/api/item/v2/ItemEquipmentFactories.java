package fuzs.puzzleslib.api.item.v2;

import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import fuzs.puzzleslib.impl.item.TierImpl;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

@Deprecated
public final class ItemEquipmentFactories {
   private static final Type[] ARMOR_TYPES = new Type[]{Type.BOOTS, Type.LEGGINGS, Type.CHESTPLATE, Type.HELMET, Type.BODY};

   private ItemEquipmentFactories() {
   }

   public static Tier registerTier(
      int miningLevel, int itemDurability, float miningSpeed, float attackDamageBonus, int enchantability, Supplier<Ingredient> repairIngredient
   ) {
      return registerTier(getVanillaMiningLevelBlockTag(miningLevel), itemDurability, miningSpeed, attackDamageBonus, enchantability, repairIngredient);
   }

   public static TagKey<Block> getVanillaMiningLevelBlockTag(int miningLevel) {
      return switch (miningLevel) {
         case 0 -> BlockTags.INCORRECT_FOR_WOODEN_TOOL;
         case 1 -> BlockTags.INCORRECT_FOR_STONE_TOOL;
         case 2 -> BlockTags.INCORRECT_FOR_IRON_TOOL;
         case 3 -> BlockTags.INCORRECT_FOR_DIAMOND_TOOL;
         case 4 -> BlockTags.INCORRECT_FOR_NETHERITE_TOOL;
         default -> throw new IllegalArgumentException("Unsupported mining level: " + miningLevel);
      };
   }

   public static Tier registerTier(
      TagKey<Block> incorrectBlocksForDrops,
      int itemDurability,
      float miningSpeed,
      float attackDamageBonus,
      int enchantability,
      Supplier<Ingredient> repairIngredient
   ) {
      return new TierImpl(incorrectBlocksForDrops, itemDurability, miningSpeed, attackDamageBonus, enchantability, Suppliers.memoize(repairIngredient::get));
   }

   public static Map<Type, Integer> toArmorTypeMap(int... protectionAmounts) {
      return toArmorTypeMapWithFallback(0, protectionAmounts);
   }

   public static Map<Type, Integer> toArmorTypeMapWithFallback(int protectionAmountFallback, int... protectionAmounts) {
      Map<Type, Integer> map = new EnumMap<>(Type.class);

      for (int i = 0; i < ARMOR_TYPES.length; i++) {
         map.put(ARMOR_TYPES[i], i < protectionAmounts.length ? protectionAmounts[i] : protectionAmountFallback);
      }

      return Maps.immutableEnumMap(map);
   }
}
