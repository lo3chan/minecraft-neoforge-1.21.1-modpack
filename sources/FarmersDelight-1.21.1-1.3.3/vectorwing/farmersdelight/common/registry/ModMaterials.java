package vectorwing.farmersdelight.common.registry;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class ModMaterials {
   public static final Tier FLINT = new Tier() {
      public int getUses() {
         return 131;
      }

      public float getSpeed() {
         return 4.0F;
      }

      public float getAttackDamageBonus() {
         return 1.0F;
      }

      @NotNull
      public TagKey<Block> getIncorrectBlocksForDrops() {
         return BlockTags.INCORRECT_FOR_WOODEN_TOOL;
      }

      public int getEnchantmentValue() {
         return 5;
      }

      @NotNull
      public Ingredient getRepairIngredient() {
         return Ingredient.of(new ItemLike[]{Items.FLINT});
      }
   };
}
