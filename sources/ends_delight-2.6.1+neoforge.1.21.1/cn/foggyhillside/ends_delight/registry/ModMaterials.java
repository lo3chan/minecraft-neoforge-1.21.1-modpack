package cn.foggyhillside.ends_delight.registry;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class ModMaterials {
   public static final Tier DRAGON_EGG_SHELL = new Tier() {
      public int getUses() {
         return 1250;
      }

      public float getSpeed() {
         return 6.0F;
      }

      public float getAttackDamageBonus() {
         return 2.5F;
      }

      @NotNull
      public TagKey<Block> getIncorrectBlocksForDrops() {
         return BlockTags.INCORRECT_FOR_IRON_TOOL;
      }

      public int getEnchantmentValue() {
         return 14;
      }

      @NotNull
      public Ingredient getRepairIngredient() {
         return Ingredient.of(new ItemLike[]{(ItemLike)ModItems.HALF_DRAGON_EGG_SHELL.get()});
      }
   };
   public static final Tier END_STONE = new Tier() {
      public int getUses() {
         return 200;
      }

      public float getSpeed() {
         return 4.0F;
      }

      public float getAttackDamageBonus() {
         return 1.0F;
      }

      @NotNull
      public TagKey<Block> getIncorrectBlocksForDrops() {
         return BlockTags.INCORRECT_FOR_STONE_TOOL;
      }

      public int getEnchantmentValue() {
         return 5;
      }

      @NotNull
      public Ingredient getRepairIngredient() {
         return Ingredient.of(new ItemLike[]{Items.END_STONE});
      }
   };
   public static final Tier PURPUR = new Tier() {
      public int getUses() {
         return 200;
      }

      public float getSpeed() {
         return 4.0F;
      }

      public float getAttackDamageBonus() {
         return 1.0F;
      }

      @NotNull
      public TagKey<Block> getIncorrectBlocksForDrops() {
         return BlockTags.INCORRECT_FOR_STONE_TOOL;
      }

      public int getEnchantmentValue() {
         return 5;
      }

      @NotNull
      public Ingredient getRepairIngredient() {
         return Ingredient.of(new ItemLike[]{Items.POPPED_CHORUS_FRUIT});
      }
   };
   public static final Tier DRAGON_TOOTH = new Tier() {
      public int getUses() {
         return 1561;
      }

      public float getSpeed() {
         return 8.0F;
      }

      public float getAttackDamageBonus() {
         return 3.5F;
      }

      @NotNull
      public TagKey<Block> getIncorrectBlocksForDrops() {
         return BlockTags.INCORRECT_FOR_DIAMOND_TOOL;
      }

      public int getEnchantmentValue() {
         return 10;
      }

      @NotNull
      public Ingredient getRepairIngredient() {
         return Ingredient.of(new ItemLike[]{(ItemLike)ModItems.DRAGON_TOOTH.get()});
      }
   };
}
