package fuzs.puzzleslib.api.item.v2;

import com.google.common.base.Suppliers;
import fuzs.puzzleslib.impl.item.TierImpl;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

public final class ToolMaterialFactory {
   private ToolMaterialFactory() {
   }

   public static Tier createToolMaterial(
      int miningLevel, int itemDurability, float miningSpeed, float attackDamageBonus, int enchantmentValue, TagKey<Item> repairItems
   ) {
      return createToolMaterial(getVanillaMiningLevelBlockTag(miningLevel), itemDurability, miningSpeed, attackDamageBonus, enchantmentValue, repairItems);
   }

   public static Tier createToolMaterial(
      TagKey<Block> incorrectBlocksForDrops, int itemDurability, float miningSpeed, float attackDamageBonus, int enchantmentValue, TagKey<Item> repairItems
   ) {
      return new TierImpl(
         incorrectBlocksForDrops, itemDurability, miningSpeed, attackDamageBonus, enchantmentValue, Suppliers.memoize(() -> Ingredient.of(repairItems))
      );
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
}
