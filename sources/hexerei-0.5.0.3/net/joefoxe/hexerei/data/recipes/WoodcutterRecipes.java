package net.joefoxe.hexerei.data.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.connected.ConnectedPillarBlock;
import net.joefoxe.hexerei.block.connected.LayeredBlock;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiTags;
import net.joefoxe.hexerei.util.message.WoodcutterRecipesPacket;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.common.brewing.BrewingRecipe;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

@EventBusSubscriber(
   bus = Bus.GAME
)
public class WoodcutterRecipes {
   public static List<WoodcutterRecipe> ALL = new ArrayList<>();
   public static Map<Item, List<WoodcutterRecipe>> BY_ITEM;
   public static List<BrewingRecipe> recipes = new ArrayList<>();

   public static void addSwappableRecipes(List<WoodcutterRecipe> recipes, List<Item> items) {
      for (Item part1 : items) {
         for (Item part2 : items) {
            if (part2 != part1) {
               recipes.add(recipeOf(new ItemStack(part1, 1), new ItemStack(part2, 1)));
            }
         }
      }
   }

   public static void createRecipes(MinecraftServer server) {
      List<WoodcutterRecipe> recipes = new ArrayList<>();
      addSwappableRecipes(
         recipes,
         List.of(
            ((Block)ModBlocks.WILLOW_PLANKS.get()).asItem(),
            ((Block)ModBlocks.POLISHED_WILLOW_PLANKS.get()).asItem(),
            ((Block)ModBlocks.WILLOW_CONNECTED.get()).asItem(),
            ((Block)ModBlocks.POLISHED_WILLOW_CONNECTED.get()).asItem(),
            ((ConnectedPillarBlock)ModBlocks.POLISHED_WILLOW_PILLAR.get()).asItem(),
            ((LayeredBlock)ModBlocks.POLISHED_WILLOW_LAYERED.get()).asItem()
         )
      );
      addSwappableRecipes(
         recipes,
         List.of(
            ((Block)ModBlocks.WITCH_HAZEL_PLANKS.get()).asItem(),
            ((Block)ModBlocks.POLISHED_WITCH_HAZEL_PLANKS.get()).asItem(),
            ((Block)ModBlocks.WITCH_HAZEL_CONNECTED.get()).asItem(),
            ((Block)ModBlocks.POLISHED_WITCH_HAZEL_CONNECTED.get()).asItem(),
            ((ConnectedPillarBlock)ModBlocks.POLISHED_WITCH_HAZEL_PILLAR.get()).asItem(),
            ((LayeredBlock)ModBlocks.POLISHED_WITCH_HAZEL_LAYERED.get()).asItem()
         )
      );
      addSwappableRecipes(
         recipes,
         List.of(
            ((Block)ModBlocks.MAHOGANY_PLANKS.get()).asItem(),
            ((Block)ModBlocks.POLISHED_MAHOGANY_PLANKS.get()).asItem(),
            ((Block)ModBlocks.MAHOGANY_CONNECTED.get()).asItem(),
            ((Block)ModBlocks.POLISHED_MAHOGANY_CONNECTED.get()).asItem(),
            ((ConnectedPillarBlock)ModBlocks.POLISHED_MAHOGANY_PILLAR.get()).asItem(),
            ((LayeredBlock)ModBlocks.POLISHED_MAHOGANY_LAYERED.get()).asItem()
         )
      );
      recipes.add(recipeOf(new ItemStack((ItemLike)ModBlocks.WILLOW_PLANKS.get(), 3), new ItemStack((ItemLike)ModBlocks.WILLOW_ALTAR.get(), 1)));
      recipes.add(new WoodcutterRecipe("", Ingredient.of(HexereiTags.Items.WILLOW_PLANKS), 5, new ItemStack((ItemLike)ModBlocks.WILLOW_CHEST.get(), 1)));
      recipes.add(recipeOf(new ItemStack((ItemLike)ModBlocks.WITCH_HAZEL_PLANKS.get(), 3), new ItemStack((ItemLike)ModBlocks.WITCH_HAZEL_ALTAR.get(), 1)));
      recipes.add(
         new WoodcutterRecipe("", Ingredient.of(HexereiTags.Items.WITCH_HAZEL_PLANKS), 5, new ItemStack((ItemLike)ModBlocks.WITCH_HAZEL_CHEST.get(), 1))
      );
      recipes.add(recipeOf(new ItemStack((ItemLike)ModBlocks.MAHOGANY_PLANKS.get(), 3), new ItemStack((ItemLike)ModBlocks.BOOK_OF_SHADOWS_ALTAR.get(), 1)));
      recipes.add(new WoodcutterRecipe("", Ingredient.of(HexereiTags.Items.MAHOGANY_PLANKS), 5, new ItemStack((ItemLike)ModBlocks.MAHOGANY_CHEST.get(), 1)));
      List<Item> planksList = BuiltInRegistries.ITEM.stream().filter(item -> item.getDefaultInstance().is(ItemTags.PLANKS)).toList();
      List<Item> logList = BuiltInRegistries.ITEM.stream().filter(item -> item.getDefaultInstance().is(ItemTags.LOGS_THAT_BURN)).toList();

      for (Item logs : logList) {
         List<ItemStack> stacks = new ArrayList<>(Stream.generate(ItemStack.EMPTY::copy).limit(9L).toList());
         stacks.set(0, logs.getDefaultInstance());
         ResourceLocation loc = BuiltInRegistries.ITEM.getKey(logs);
         String prefix = "stripped_";
         if (loc.getPath().startsWith(prefix)) {
            Item non_stripped = (Item)BuiltInRegistries.ITEM
               .get(ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), loc.getPath().substring(prefix.length())));
            recipes.add(recipeOf(new ItemStack(non_stripped, 1), new ItemStack(logs, 1)));
         }
      }

      for (Item logsx : logList) {
         List<ItemStack> stacks = new ArrayList<>(Stream.generate(ItemStack.EMPTY::copy).limit(9L).toList());
         stacks.set(0, logsx.getDefaultInstance());

         for (RecipeHolder<?> recipe : server.getRecipeManager().getRecipesFor(RecipeType.CRAFTING, CraftingInput.of(3, 3, stacks), server.overworld())) {
            if (recipe.value().getType() == RecipeType.CRAFTING) {
               ItemStack output = recipe.value().getResultItem(server.registryAccess());
               if (output.is(ItemTags.PLANKS)) {
                  for (ItemStack ingredient : recipe.value()
                     .getIngredients()
                     .stream()
                     .map(Ingredient::getItems)
                     .flatMap(Arrays::stream)
                     .toArray(ItemStack[]::new)) {
                     if (ingredient.getItem() == logsx) {
                        recipes.add(recipeOf(new ItemStack(logsx, 1), new ItemStack(recipe.value().getResultItem(server.registryAccess()).getItem(), 5)));
                     }
                  }
               }
            }
         }
      }

      for (Item planks : planksList) {
         for (RecipeHolder<?> recipex : server.getRecipeManager().getRecipes()) {
            if (recipex.value().getType() == RecipeType.CRAFTING) {
               ItemStack output = recipex.value().getResultItem(server.registryAccess());
               if (output.is(ItemTags.BOATS)) {
                  for (ItemStack ingredientx : recipex.value()
                     .getIngredients()
                     .stream()
                     .map(Ingredient::getItems)
                     .flatMap(Arrays::stream)
                     .toArray(ItemStack[]::new)) {
                     if (ingredientx.getItem() == planks) {
                        recipes.add(recipeOf(new ItemStack(planks, 4), new ItemStack(recipex.value().getResultItem(server.registryAccess()).getItem(), 1)));
                        List<ItemStack> stacks = new ArrayList<>(Stream.generate(ItemStack.EMPTY::copy).limit(9L).toList());
                        stacks.set(0, Items.CHEST.getDefaultInstance());
                        stacks.set(1, new ItemStack(recipex.value().getResultItem(server.registryAccess()).getItem(), 1));
                        Optional<RecipeHolder<CraftingRecipe>> recipe1 = server.getRecipeManager()
                           .getRecipeFor(RecipeType.CRAFTING, CraftingInput.of(3, 3, stacks), server.overworld());
                        recipe1.ifPresent(
                           rec -> recipes.add(
                              recipeOf(
                                 new ItemStack(planks, 8), new ItemStack(((CraftingRecipe)rec.value()).getResultItem(server.registryAccess()).getItem(), 1)
                              )
                           )
                        );
                        break;
                     }
                  }
               }

               if (output.is(ItemTags.SIGNS)) {
                  for (ItemStack ingredientxx : recipex.value()
                     .getIngredients()
                     .stream()
                     .map(Ingredient::getItems)
                     .flatMap(Arrays::stream)
                     .toArray(ItemStack[]::new)) {
                     if (ingredientxx.getItem() == planks) {
                        WoodcutterRecipe newRecipe = recipeOf(
                           new ItemStack(planks, 2), new ItemStack(recipex.value().getResultItem(server.registryAccess()).getItem(), 1)
                        );
                        recipes.add(newRecipe);
                        break;
                     }
                  }
               }

               if (output.is(ItemTags.WOODEN_SLABS)) {
                  for (ItemStack ingredientxxx : recipex.value()
                     .getIngredients()
                     .stream()
                     .map(Ingredient::getItems)
                     .flatMap(Arrays::stream)
                     .toArray(ItemStack[]::new)) {
                     if (ingredientxxx.getItem() == planks) {
                        WoodcutterRecipe newRecipe = recipeOf(
                           new ItemStack(planks, 1), new ItemStack(recipex.value().getResultItem(server.registryAccess()).getItem(), 2)
                        );
                        recipes.add(newRecipe);
                        break;
                     }
                  }
               }

               if (output.is(ItemTags.WOODEN_STAIRS)) {
                  for (ItemStack ingredientxxxx : recipex.value()
                     .getIngredients()
                     .stream()
                     .map(Ingredient::getItems)
                     .flatMap(Arrays::stream)
                     .toArray(ItemStack[]::new)) {
                     if (ingredientxxxx.getItem() == planks) {
                        WoodcutterRecipe newRecipe = recipeOf(
                           new ItemStack(planks, 1), new ItemStack(recipex.value().getResultItem(server.registryAccess()).getItem(), 2)
                        );
                        recipes.add(newRecipe);
                        break;
                     }
                  }
               }

               if (output.is(ItemTags.WOODEN_DOORS)) {
                  for (ItemStack ingredientxxxxx : recipex.value()
                     .getIngredients()
                     .stream()
                     .map(Ingredient::getItems)
                     .flatMap(Arrays::stream)
                     .toArray(ItemStack[]::new)) {
                     if (ingredientxxxxx.getItem() == planks) {
                        WoodcutterRecipe newRecipe = recipeOf(
                           new ItemStack(planks, 2), new ItemStack(recipex.value().getResultItem(server.registryAccess()).getItem(), 1)
                        );
                        recipes.add(newRecipe);
                        break;
                     }
                  }
               }

               if (output.is(ItemTags.WOODEN_TRAPDOORS)) {
                  for (ItemStack ingredientxxxxxx : recipex.value()
                     .getIngredients()
                     .stream()
                     .map(Ingredient::getItems)
                     .flatMap(Arrays::stream)
                     .toArray(ItemStack[]::new)) {
                     if (ingredientxxxxxx.getItem() == planks) {
                        WoodcutterRecipe newRecipe = recipeOf(
                           new ItemStack(planks, 2), new ItemStack(recipex.value().getResultItem(server.registryAccess()).getItem(), 1)
                        );
                        recipes.add(newRecipe);
                        break;
                     }
                  }
               }

               if (output.is(ItemTags.WOODEN_BUTTONS)) {
                  for (ItemStack ingredientxxxxxxx : recipex.value()
                     .getIngredients()
                     .stream()
                     .map(Ingredient::getItems)
                     .flatMap(Arrays::stream)
                     .toArray(ItemStack[]::new)) {
                     if (ingredientxxxxxxx.getItem() == planks) {
                        WoodcutterRecipe newRecipe = recipeOf(
                           new ItemStack(planks, 1), new ItemStack(recipex.value().getResultItem(server.registryAccess()).getItem(), 2)
                        );
                        recipes.add(newRecipe);
                        break;
                     }
                  }
               }

               if (output.is(ItemTags.WOODEN_FENCES)) {
                  for (ItemStack ingredientxxxxxxxx : recipex.value()
                     .getIngredients()
                     .stream()
                     .map(Ingredient::getItems)
                     .flatMap(Arrays::stream)
                     .toArray(ItemStack[]::new)) {
                     if (ingredientxxxxxxxx.getItem() == planks) {
                        WoodcutterRecipe newRecipe = recipeOf(
                           new ItemStack(planks, 2), new ItemStack(recipex.value().getResultItem(server.registryAccess()).getItem(), 1)
                        );
                        recipes.add(newRecipe);
                        break;
                     }
                  }
               }

               if (output.is(ItemTags.FENCE_GATES)) {
                  for (ItemStack ingredientxxxxxxxxx : recipex.value()
                     .getIngredients()
                     .stream()
                     .map(Ingredient::getItems)
                     .flatMap(Arrays::stream)
                     .toArray(ItemStack[]::new)) {
                     if (ingredientxxxxxxxxx.getItem() == planks) {
                        WoodcutterRecipe newRecipe = recipeOf(
                           new ItemStack(planks, 2), new ItemStack(recipex.value().getResultItem(server.registryAccess()).getItem(), 1)
                        );
                        recipes.add(newRecipe);
                        break;
                     }
                  }
               }

               if (output.is(ItemTags.WOODEN_PRESSURE_PLATES)) {
                  for (ItemStack ingredientxxxxxxxxxx : recipex.value()
                     .getIngredients()
                     .stream()
                     .map(Ingredient::getItems)
                     .flatMap(Arrays::stream)
                     .toArray(ItemStack[]::new)) {
                     if (ingredientxxxxxxxxxx.getItem() == planks) {
                        WoodcutterRecipe newRecipe = recipeOf(
                           new ItemStack(planks, 2), new ItemStack(recipex.value().getResultItem(server.registryAccess()).getItem(), 1)
                        );
                        recipes.add(newRecipe);
                        break;
                     }
                  }
               }
            }
         }
      }

      recipes.add(new WoodcutterRecipe("", Ingredient.of(ItemTags.LOGS_THAT_BURN), 1, new ItemStack(Items.STICK, 15)));
      recipes.add(new WoodcutterRecipe("", Ingredient.of(ItemTags.PLANKS), 1, new ItemStack(Items.STICK, 3)));
      recipes.add(new WoodcutterRecipe("", Ingredient.of(ItemTags.PLANKS), 2, new ItemStack(Items.CRAFTING_TABLE, 1)));
      recipes.add(new WoodcutterRecipe("", Ingredient.of(ItemTags.PLANKS), 4, new ItemStack(Items.BARREL, 1)));
      recipes.add(new WoodcutterRecipe("", Ingredient.of(ItemTags.PLANKS), 5, new ItemStack(Items.CHEST, 1)));
      ALL = recipes;
   }

   private static WoodcutterRecipe recipeOf(ItemStack input, ItemStack output) {
      return new WoodcutterRecipe("", Ingredient.of(new ItemStack[]{input}), input.getCount(), output);
   }

   public static void sendToClient(ServerPlayer player) {
      HexereiPacketHandler.sendToPlayerClient(new WoodcutterRecipesPacket(ALL), player);
   }

   @SubscribeEvent
   public static void serverStarted(ServerStartedEvent event) {
      if (ALL != null) {
         createRecipes(event.getServer());
      }
   }
}
