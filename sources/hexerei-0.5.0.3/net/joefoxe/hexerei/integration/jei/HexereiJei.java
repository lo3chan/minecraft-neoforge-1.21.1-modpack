package net.joefoxe.hexerei.integration.jei;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.api.runtime.IRecipesGui;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.data.recipes.AddToCandleRecipe;
import net.joefoxe.hexerei.data.recipes.BookOfShadowsRecipe;
import net.joefoxe.hexerei.data.recipes.CauldronEmptyingRecipe;
import net.joefoxe.hexerei.data.recipes.CrowFluteRecipe;
import net.joefoxe.hexerei.data.recipes.DipperRecipe;
import net.joefoxe.hexerei.data.recipes.DryingRackRecipe;
import net.joefoxe.hexerei.data.recipes.FluidMixingRecipe;
import net.joefoxe.hexerei.data.recipes.KeychainRecipe;
import net.joefoxe.hexerei.data.recipes.MixingCauldronRecipe;
import net.joefoxe.hexerei.data.recipes.PestleAndMortarRecipe;
import net.joefoxe.hexerei.data.recipes.WoodcutterRecipe;
import net.joefoxe.hexerei.data.recipes.WoodcutterRecipes;
import net.joefoxe.hexerei.fluid.ModFluids;
import net.joefoxe.hexerei.fluid.PotionFluid;
import net.joefoxe.hexerei.fluid.PotionMixingRecipes;
import net.joefoxe.hexerei.screen.BroomScreen;
import net.joefoxe.hexerei.screen.CofferScreen;
import net.joefoxe.hexerei.screen.CrowScreen;
import net.joefoxe.hexerei.screen.MixingCauldronScreen;
import net.joefoxe.hexerei.screen.OwlScreen;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

@JeiPlugin
public class HexereiJei implements IModPlugin {
   public static IRecipesGui runtime;
   public static RecipeManager recipeManager;

   public ResourceLocation getPluginUid() {
      return HexereiUtil.getResource("jei_plugin");
   }

   public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
      runtime = jeiRuntime.getRecipesGui();
      super.onRuntimeAvailable(jeiRuntime);
   }

   public <T> void registerFluidSubtypes(ISubtypeRegistration registration, IPlatformFluidHelper<T> platformFluidHelper) {
      PotionFluidSubtypeInterpreter interpreter = new PotionFluidSubtypeInterpreter();
      PotionFluid potionFluid = (PotionFluid)ModFluids.POTION.get();
      registration.registerSubtypeInterpreter(NeoForgeTypes.FLUID_STACK, potionFluid, interpreter);
   }

   public static List<FluidStack> withImprovedVisibility(List<FluidStack> stacks) {
      return stacks.stream().map(HexereiJei::withImprovedVisibility).collect(Collectors.toList());
   }

   public static FluidStack withImprovedVisibility(FluidStack stack) {
      FluidStack display = stack.copy();
      int displayedAmount = (int)(stack.getAmount() * 0.75F) + 250;
      display.setAmount(displayedAmount);
      return display;
   }

   public void registerCategories(IRecipeCategoryRegistration registration) {
      if (PotionMixingRecipes.ALL == null || PotionMixingRecipes.ALL.isEmpty()) {
         PotionMixingRecipes.ALL = PotionMixingRecipes.createRecipes(Minecraft.getInstance().level.potionBrewing());
      }

      registration.addRecipeCategories(
         new IRecipeCategory[]{
            new AddToCandleRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
            new FluteRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
            new BookOfShadowsRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
            new MixingCauldronRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
            new FluidMixingRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
            new FluidMixingRecipeCategory(registration.getJeiHelpers().getGuiHelper(), "Potion"),
            new DipperRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
            new PestleAndMortarRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
            new WoodcutterRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
            new KeychainApplyRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
            new BottlingRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
            new BloodSigilRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
            new PlantPickingRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
            new DryingRackRecipeCategory(registration.getJeiHelpers().getGuiHelper())
         }
      );
   }

   public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
      registration.addRecipeCatalyst(
         new ItemStack((ItemLike)ModBlocks.MIXING_CAULDRON.get()),
         new RecipeType[]{new RecipeType(MixingCauldronRecipeCategory.UID, MixingCauldronRecipe.class)}
      );
      registration.addRecipeCatalyst(
         new ItemStack((ItemLike)ModBlocks.MIXING_CAULDRON.get()), new RecipeType[]{new RecipeType(FluidMixingRecipeCategory.UID, FluidMixingRecipe.class)}
      );
      registration.addRecipeCatalyst(
         new ItemStack((ItemLike)ModBlocks.MIXING_CAULDRON.get()),
         new RecipeType[]{new RecipeType(FluidMixingRecipeCategory.POTION_UID, FluidMixingRecipe.class)}
      );
      registration.addRecipeCatalyst(
         new ItemStack((ItemLike)ModBlocks.MIXING_CAULDRON.get()), new RecipeType[]{new RecipeType(BottlingRecipeCategory.UID, CauldronEmptyingRecipe.class)}
      );
      registration.addRecipeCatalyst(
         new ItemStack((ItemLike)ModBlocks.MIXING_CAULDRON.get()), new RecipeType[]{new RecipeType(BloodSigilRecipeCategory.UID, BloodSigilRecipeJEI.class)}
      );
      registration.addRecipeCatalyst(
         new ItemStack((ItemLike)ModBlocks.MIXING_CAULDRON.get()), new RecipeType[]{new RecipeType(DipperRecipeCategory.UID, DipperRecipe.class)}
      );
      registration.addRecipeCatalyst(
         new ItemStack((ItemLike)ModBlocks.CANDLE_DIPPER.get()), new RecipeType[]{new RecipeType(DipperRecipeCategory.UID, DipperRecipe.class)}
      );
      registration.addRecipeCatalyst(
         new ItemStack((ItemLike)ModBlocks.HERB_DRYING_RACK.get()), new RecipeType[]{new RecipeType(DryingRackRecipeCategory.UID, DryingRackRecipe.class)}
      );
      registration.addRecipeCatalyst(
         new ItemStack((ItemLike)ModBlocks.WILLOW_WOODCUTTER.get()), new RecipeType[]{new RecipeType(WoodcutterRecipeCategory.UID, WoodcutterRecipe.class)}
      );
      registration.addRecipeCatalyst(
         new ItemStack((ItemLike)ModBlocks.MAHOGANY_WOODCUTTER.get()), new RecipeType[]{new RecipeType(WoodcutterRecipeCategory.UID, WoodcutterRecipe.class)}
      );
      registration.addRecipeCatalyst(
         new ItemStack((ItemLike)ModBlocks.WITCH_HAZEL_WOODCUTTER.get()),
         new RecipeType[]{new RecipeType(WoodcutterRecipeCategory.UID, WoodcutterRecipe.class)}
      );
      registration.addRecipeCatalyst(
         new ItemStack((ItemLike)ModBlocks.PESTLE_AND_MORTAR.get()),
         new RecipeType[]{new RecipeType(PestleAndMortarRecipeCategory.UID, PestleAndMortarRecipe.class)}
      );
   }

   public void registerGuiHandlers(IGuiHandlerRegistration registration) {
      registration.addRecipeClickArea(
         MixingCauldronScreen.class,
         101,
         41,
         24,
         24,
         new RecipeType[]{
            new RecipeType(MixingCauldronRecipeCategory.UID, MixingCauldronRecipe.class),
            new RecipeType(FluidMixingRecipeCategory.UID, FluidMixingRecipe.class),
            new RecipeType(BottlingRecipeCategory.UID, CauldronEmptyingRecipe.class),
            new RecipeType(BloodSigilRecipeCategory.UID, BloodSigilRecipeJEI.class),
            new RecipeType(FluidMixingRecipeCategory.POTION_UID, FluidMixingRecipe.class)
         }
      );
      registration.addGuiContainerHandler(MixingCauldronScreen.class, new IGuiContainerHandler<MixingCauldronScreen>() {
         public List<Rect2i> getGuiExtraAreas(MixingCauldronScreen gui) {
            List<Rect2i> ret = new ArrayList<>();
            Rect2i rect2i = new Rect2i(gui.getGuiLeft() + 23, gui.getGuiTop(), 142, 97);
            ret.add(rect2i);
            rect2i = new Rect2i(gui.getGuiLeft() + 160, gui.getGuiTop() + 32, 49, 48);
            ret.add(rect2i);
            rect2i = new Rect2i(gui.getGuiLeft(), gui.getGuiTop() + 97, 188, 30);
            ret.add(rect2i);
            return ret;
         }
      });
      registration.addGuiContainerHandler(CrowScreen.class, new IGuiContainerHandler<CrowScreen>() {
         public List<Rect2i> getGuiExtraAreas(CrowScreen gui) {
            List<Rect2i> ret = new ArrayList<>();
            Rect2i rect2i = new Rect2i(gui.getGuiLeft(), gui.getGuiTop() - 28, 188, 153);
            ret.add(rect2i);
            rect2i = new Rect2i(gui.getGuiLeft() + 184 - 28 + (int)gui.whitelistOffset, gui.getGuiTop() + 17 - 28 + 3, 39, 101);
            ret.add(rect2i);
            rect2i = new Rect2i(gui.getGuiLeft() - 5 - (int)gui.leftPanelOffset, gui.getGuiTop() + 17 - 28 + 3, 39, 101);
            ret.add(rect2i);
            return ret;
         }
      });
      registration.addGuiContainerHandler(OwlScreen.class, new IGuiContainerHandler<OwlScreen>() {
         public List<Rect2i> getGuiExtraAreas(OwlScreen gui) {
            List<Rect2i> ret = new ArrayList<>();
            if (!gui.quirkSideBarHidden) {
               Rect2i rect2i = new Rect2i(gui.getGuiLeft() + 174, gui.getGuiTop() + 37, 26, 26);
               ret.add(rect2i);
            }

            return ret;
         }
      });
      registration.addGuiContainerHandler(CofferScreen.class, new IGuiContainerHandler<CofferScreen>() {
         public List<Rect2i> getGuiExtraAreas(CofferScreen gui) {
            List<Rect2i> ret = new ArrayList<>();
            Rect2i rect2i = new Rect2i(gui.getGuiLeft(), gui.getGuiTop() - 28, 214, 157);
            ret.add(rect2i);
            if (gui.getToggled()) {
               Rect2i rect2i2 = new Rect2i(gui.getGuiLeft() + 211, gui.getGuiTop() - 28 + 62, 62, 62);
               ret.add(rect2i2);
            }

            return ret;
         }
      });
      registration.addGuiContainerHandler(BroomScreen.class, new IGuiContainerHandler<BroomScreen>() {
         public List<Rect2i> getGuiExtraAreas(BroomScreen gui) {
            List<Rect2i> ret = new ArrayList<>();
            Rect2i rect2i = new Rect2i(gui.getGuiLeft(), gui.getGuiTop() - 34, 214, 82 + gui.offset);
            ret.add(rect2i);
            rect2i = new Rect2i(gui.getGuiLeft(), gui.getGuiTop() + 79 + gui.offset - 34, 214, 34);
            ret.add(rect2i);
            rect2i = new Rect2i(gui.getGuiLeft() + 184, gui.getGuiTop() + 55 + gui.offset + (int)gui.dropdownOffset - 34, 26, 58);
            ret.add(rect2i);
            return ret;
         }
      });
   }

   public void registerRecipes(IRecipeRegistration registration) {
      RecipeManager rm = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();
      recipeManager = rm;
      List<RecipeHolder<CraftingRecipe>> add_to_candle_recipes = rm.getAllRecipesFor(net.minecraft.world.item.crafting.RecipeType.CRAFTING);
      List<RecipeHolder<CraftingRecipe>> flute_dye_recipes = new ArrayList<>(add_to_candle_recipes);
      List<RecipeHolder<CraftingRecipe>> book_recipe = new ArrayList<>(add_to_candle_recipes);
      List<RecipeHolder<CraftingRecipe>> keychainRecipe = new ArrayList<>(add_to_candle_recipes);
      registration.addRecipes(
         new RecipeType(AddToCandleRecipeCategory.UID, AddToCandleRecipe.class),
         add_to_candle_recipes.stream().filter(craftingRecipe -> craftingRecipe.value() instanceof AddToCandleRecipe).map(RecipeHolder::value).toList()
      );
      registration.addRecipes(
         new RecipeType(FluteRecipeCategory.UID, CrowFluteRecipe.class),
         flute_dye_recipes.stream().filter(craftingRecipe -> craftingRecipe.value() instanceof CrowFluteRecipe).map(RecipeHolder::value).toList()
      );
      registration.addRecipes(
         RecipeTypes.CRAFTING, book_recipe.stream().filter(craftingRecipe -> craftingRecipe.value() instanceof BookOfShadowsRecipe).toList()
      );
      List<MixingCauldronRecipe> mixing_recipes = rm.getAllRecipesFor(MixingCauldronRecipe.Type.INSTANCE)
         .stream()
         .<MixingCauldronRecipe>map(RecipeHolder::value)
         .toList();
      registration.addRecipes(
         RecipeType.create(MixingCauldronRecipeCategory.UID.getNamespace(), MixingCauldronRecipeCategory.UID.getPath(), MixingCauldronRecipe.class),
         mixing_recipes
      );
      registration.addRecipes(
         new RecipeType(FluidMixingRecipeCategory.UID, FluidMixingRecipe.class),
         rm.getAllRecipesFor(FluidMixingRecipe.Type.INSTANCE).stream().map(RecipeHolder::value).toList()
      );
      if (PotionMixingRecipes.ALL == null || PotionMixingRecipes.ALL.isEmpty()) {
         PotionMixingRecipes.ALL = PotionMixingRecipes.createRecipes(Minecraft.getInstance().level.potionBrewing());
      }

      registration.addRecipes(new RecipeType(FluidMixingRecipeCategory.POTION_UID, FluidMixingRecipe.class), PotionMixingRecipes.ALL);
      registration.addRecipes(new RecipeType(BottlingRecipeCategory.UID, CauldronEmptyingRecipe.class), BottlingRecipeJEI.getRecipeList(rm));
      List<DipperRecipe> recipes = rm.getAllRecipesFor(DipperRecipe.Type.INSTANCE).stream().<DipperRecipe>map(RecipeHolder::value).toList();
      registration.addRecipes(new RecipeType(DipperRecipeCategory.UID, DipperRecipe.class), recipes);
      registration.addRecipes(
         new RecipeType(PestleAndMortarRecipeCategory.UID, PestleAndMortarRecipe.class),
         rm.getAllRecipesFor(PestleAndMortarRecipe.Type.INSTANCE).stream().map(RecipeHolder::value).toList()
      );
      registration.addRecipes(
         new RecipeType(DryingRackRecipeCategory.UID, DryingRackRecipe.class),
         rm.getAllRecipesFor(DryingRackRecipe.Type.INSTANCE).stream().map(RecipeHolder::value).toList()
      );
      List<WoodcutterRecipe> list = new ArrayList<>(rm.getAllRecipesFor(WoodcutterRecipe.Type.INSTANCE).stream().map(RecipeHolder::value).toList());
      list.addAll(WoodcutterRecipes.ALL);
      registration.addRecipes(new RecipeType(WoodcutterRecipeCategory.UID, WoodcutterRecipe.class), list);
      registration.addRecipes(
         new RecipeType(KeychainApplyRecipeCategory.UID, KeychainRecipe.class),
         keychainRecipe.stream().filter(craftingRecipe -> craftingRecipe.value() instanceof KeychainRecipe).map(RecipeHolder::value).toList()
      );
      registration.addRecipes(new RecipeType(BloodSigilRecipeCategory.UID, BloodSigilRecipeJEI.class), BloodSigilRecipeJEI.getRecipeList());
      registration.addRecipes(new RecipeType(PlantPickingRecipeCategory.UID, PlantPickingRecipeJEI.class), PlantPickingRecipeJEI.getRecipeList());
   }

   public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
      registration.addRecipeTransferHandler(
         new MixingCauldronTransferInfo(registration.getTransferHelper()), new RecipeType(MixingCauldronRecipeCategory.UID, MixingCauldronRecipe.class)
      );
   }

   public static void showUses(final FluidStack fluid) {
      if (runtime != null) {
         runtime.show(new IFocus<FluidStack>() {
            public ITypedIngredient<FluidStack> getTypedValue() {
               return new ITypedIngredient<FluidStack>() {
                  public IIngredientTypeWithSubtypes<Fluid, FluidStack> getType() {
                     return NeoForgeTypes.FLUID_STACK;
                  }

                  public FluidStack getIngredient() {
                     return fluid;
                  }

                  public <V> Optional<V> getIngredient(IIngredientType<V> ingredientType) {
                     return ingredientType == NeoForgeTypes.FLUID_STACK ? Optional.of((V)fluid) : Optional.empty();
                  }
               };
            }

            public RecipeIngredientRole getRole() {
               return RecipeIngredientRole.CATALYST;
            }

            public <T> Optional<IFocus<T>> checkedCast(IIngredientType<T> ingredientType) {
               return Optional.empty();
            }
         });
         if (!(Minecraft.getInstance().screen instanceof IRecipesGui)) {
            if (runtime == null) {
               return;
            }

            runtime.show(new IFocus<FluidStack>() {
               public ITypedIngredient<FluidStack> getTypedValue() {
                  return new ITypedIngredient<FluidStack>() {
                     public IIngredientTypeWithSubtypes<Fluid, FluidStack> getType() {
                        return NeoForgeTypes.FLUID_STACK;
                     }

                     public FluidStack getIngredient() {
                        return fluid;
                     }

                     public <V> Optional<V> getIngredient(IIngredientType<V> ingredientType) {
                        return ingredientType == NeoForgeTypes.FLUID_STACK ? Optional.of((V)fluid) : Optional.empty();
                     }
                  };
               }

               public RecipeIngredientRole getRole() {
                  return RecipeIngredientRole.INPUT;
               }

               public <T> Optional<IFocus<T>> checkedCast(IIngredientType<T> ingredientType) {
                  return Optional.empty();
               }
            });
         }
      }
   }

   public static void showUses(final ItemStack item) {
      if (runtime != null) {
         runtime.show(new IFocus<ItemStack>() {
            public ITypedIngredient<ItemStack> getTypedValue() {
               return new ITypedIngredient<ItemStack>() {
                  public IIngredientType<ItemStack> getType() {
                     return VanillaTypes.ITEM_STACK;
                  }

                  public ItemStack getIngredient() {
                     return item;
                  }

                  public <V> Optional<V> getIngredient(IIngredientType<V> ingredientType) {
                     return ingredientType == VanillaTypes.ITEM_STACK ? Optional.of((V)item) : Optional.empty();
                  }
               };
            }

            public RecipeIngredientRole getRole() {
               return RecipeIngredientRole.CATALYST;
            }

            public <T> Optional<IFocus<T>> checkedCast(IIngredientType<T> ingredientType) {
               return Optional.empty();
            }
         });
         if (!(Minecraft.getInstance().screen instanceof IRecipesGui)) {
            if (runtime == null) {
               return;
            }

            runtime.show(new IFocus<ItemStack>() {
               public ITypedIngredient<ItemStack> getTypedValue() {
                  return new ITypedIngredient<ItemStack>() {
                     public IIngredientType<ItemStack> getType() {
                        return VanillaTypes.ITEM_STACK;
                     }

                     public ItemStack getIngredient() {
                        return item;
                     }

                     public <V> Optional<V> getIngredient(IIngredientType<V> ingredientType) {
                        return ingredientType == VanillaTypes.ITEM_STACK ? Optional.of((V)item) : Optional.empty();
                     }
                  };
               }

               public RecipeIngredientRole getRole() {
                  return RecipeIngredientRole.INPUT;
               }

               public <T> Optional<IFocus<T>> checkedCast(IIngredientType<T> ingredientType) {
                  return Optional.empty();
               }
            });
         }
      }
   }

   public static void showRecipe(final ItemStack item) {
      if (runtime != null) {
         runtime.show(new IFocus<ItemStack>() {
            public ITypedIngredient<ItemStack> getTypedValue() {
               return new ITypedIngredient<ItemStack>() {
                  public IIngredientType<ItemStack> getType() {
                     return VanillaTypes.ITEM_STACK;
                  }

                  public ItemStack getIngredient() {
                     return item;
                  }

                  public <V> Optional<V> getIngredient(IIngredientType<V> ingredientType) {
                     return ingredientType == VanillaTypes.ITEM_STACK ? Optional.of((V)item) : Optional.empty();
                  }
               };
            }

            public RecipeIngredientRole getRole() {
               return RecipeIngredientRole.OUTPUT;
            }

            public <T> Optional<IFocus<T>> checkedCast(IIngredientType<T> ingredientType) {
               return Optional.empty();
            }
         });
      }
   }

   public static void showRecipe(final FluidStack fluid) {
      if (runtime != null) {
         runtime.show(new IFocus<FluidStack>() {
            public ITypedIngredient<FluidStack> getTypedValue() {
               return new ITypedIngredient<FluidStack>() {
                  public IIngredientType<FluidStack> getType() {
                     return NeoForgeTypes.FLUID_STACK;
                  }

                  public FluidStack getIngredient() {
                     return fluid;
                  }

                  public <V> Optional<V> getIngredient(IIngredientType<V> ingredientType) {
                     return ingredientType == NeoForgeTypes.FLUID_STACK ? Optional.of((V)fluid) : Optional.empty();
                  }
               };
            }

            public RecipeIngredientRole getRole() {
               return RecipeIngredientRole.OUTPUT;
            }

            public <T> Optional<IFocus<T>> checkedCast(IIngredientType<T> ingredientType) {
               return Optional.empty();
            }
         });
      }
   }
}
