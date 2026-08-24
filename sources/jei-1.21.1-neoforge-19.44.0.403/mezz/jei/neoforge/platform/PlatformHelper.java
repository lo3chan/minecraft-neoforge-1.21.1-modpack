package mezz.jei.neoforge.platform;

import java.util.function.Supplier;
import mezz.jei.common.platform.IPlatformFluidHelperInternal;
import mezz.jei.common.platform.IPlatformHelper;
import mezz.jei.common.util.function.LazySupplier;

public class PlatformHelper implements IPlatformHelper {
   private final Supplier<ItemStackHelper> itemStackHelper = new LazySupplier<>(ItemStackHelper::new);
   private final Supplier<FluidHelper> fluidHelper = new LazySupplier<>(FluidHelper::new);
   private final Supplier<RenderHelper> renderHelper = new LazySupplier<>(RenderHelper::new);
   private final Supplier<RecipeHelper> recipeHelper = new LazySupplier<>(RecipeHelper::new);
   private final Supplier<BrewingHelper> brewingHelper = new LazySupplier<>(BrewingHelper::new);
   private final Supplier<ConfigHelper> configHelper = new LazySupplier<>(ConfigHelper::new);
   private final Supplier<InputHelper> inputHelper = new LazySupplier<>(InputHelper::new);
   private final Supplier<ScreenHelper> screenHelper = new LazySupplier<>(ScreenHelper::new);
   private final Supplier<IngredientHelper> ingredientHelper = new LazySupplier<>(IngredientHelper::new);
   private final Supplier<ModHelper> modHelper = new LazySupplier<>(ModHelper::new);
   private final Supplier<WorldHelper> worldHelper = new LazySupplier<>(WorldHelper::new);

   public ItemStackHelper getItemStackHelper() {
      return this.itemStackHelper.get();
   }

   @Override
   public IPlatformFluidHelperInternal<?> getFluidHelper() {
      return this.fluidHelper.get();
   }

   public RenderHelper getRenderHelper() {
      return this.renderHelper.get();
   }

   public RecipeHelper getRecipeHelper() {
      return this.recipeHelper.get();
   }

   public BrewingHelper getBrewingHelper() {
      return this.brewingHelper.get();
   }

   public ConfigHelper getConfigHelper() {
      return this.configHelper.get();
   }

   public InputHelper getInputHelper() {
      return this.inputHelper.get();
   }

   public ScreenHelper getScreenHelper() {
      return this.screenHelper.get();
   }

   public IngredientHelper getIngredientHelper() {
      return this.ingredientHelper.get();
   }

   public ModHelper getModHelper() {
      return this.modHelper.get();
   }

   public WorldHelper getWorldHelper() {
      return this.worldHelper.get();
   }
}
