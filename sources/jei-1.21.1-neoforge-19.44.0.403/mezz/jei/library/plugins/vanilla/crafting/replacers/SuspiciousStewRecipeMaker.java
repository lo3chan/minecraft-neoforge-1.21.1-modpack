package mezz.jei.library.plugins.vanilla.crafting.replacers;

import java.util.List;
import mezz.jei.common.util.RegistryUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderSet.ListBacked;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBlock;

public final class SuspiciousStewRecipeMaker {
   public static List<RecipeHolder<CraftingRecipe>> createRecipes() {
      String group = "jei.suspicious.stew";
      Ingredient brownMushroom = Ingredient.of(new ItemLike[]{Blocks.BROWN_MUSHROOM.asItem()});
      Ingredient redMushroom = Ingredient.of(new ItemLike[]{Blocks.RED_MUSHROOM.asItem()});
      Ingredient bowl = Ingredient.of(new ItemLike[]{Items.BOWL});
      return RegistryUtil.getRegistry(Registries.ITEM)
         .getTag(ItemTags.SMALL_FLOWERS)
         .stream()
         .flatMap(ListBacked::stream)
         .<Item>map(Holder::value)
         .filter(BlockItem.class::isInstance)
         .map(item -> ((BlockItem)item).getBlock())
         .filter(FlowerBlock.class::isInstance)
         .map(FlowerBlock.class::cast)
         .map(flowerBlock -> {
            Ingredient flower = Ingredient.of(new ItemLike[]{flowerBlock.asItem()});
            NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY, new Ingredient[]{brownMushroom, redMushroom, bowl, flower});
            ItemStack output = new ItemStack(Items.SUSPICIOUS_STEW, 1);
            SuspiciousStewEffects effects = flowerBlock.getSuspiciousEffects();
            output.set(DataComponents.SUSPICIOUS_STEW_EFFECTS, effects);
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath("minecraft", "jei.suspicious.stew." + flowerBlock.getDescriptionId());
            CraftingRecipe recipe = new ShapelessRecipe(group, CraftingBookCategory.MISC, output, inputs);
            return new RecipeHolder(id, recipe);
         })
         .toList();
   }

   private SuspiciousStewRecipeMaker() {
   }
}
