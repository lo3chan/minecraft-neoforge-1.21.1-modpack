package mezz.jei.common.config;

import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.common.config.file.ConfigValue;
import mezz.jei.common.config.file.IConfigCategoryBuilder;
import mezz.jei.common.config.file.IConfigSchemaBuilder;
import mezz.jei.common.util.NavigationVisibility;

public class IngredientGridConfig implements IIngredientGridConfig {
   private static final int minNumRows = 1;
   private static final int defaultNumRows = 16;
   private static final int largestNumRows = 100;
   private static final int minNumColumns = 2;
   private static final int defaultNumColumns = 9;
   private static final int largestNumColumns = 100;
   private static final VerticalAlignment defaultVerticalAlignment = VerticalAlignment.TOP;
   private static final NavigationVisibility defaultNavigationVisibility = NavigationVisibility.ENABLED;
   private static final boolean defaultDrawBackground = false;
   private static final IngredientGridLayoutMode defaultLayoutMode = IngredientGridLayoutMode.RECTANGULAR;
   private static final IngredientGridNavigationMode defaultNavigationMode = IngredientGridNavigationMode.PAGED;
   private final ConfigValue<Integer> maxRows;
   private final ConfigValue<Integer> maxColumns;
   private final ConfigValue<HorizontalAlignment> horizontalAlignment;
   private final ConfigValue<VerticalAlignment> verticalAlignment;
   private final ConfigValue<NavigationVisibility> navigationVisibility;
   private final ConfigValue<Boolean> drawBackground;
   private final ConfigValue<IngredientGridLayoutMode> layoutMode;
   private final ConfigValue<IngredientGridNavigationMode> navigationMode;

   public IngredientGridConfig(String categoryName, IConfigSchemaBuilder builder, HorizontalAlignment defaultHorizontalAlignment) {
      IConfigCategoryBuilder category = builder.addCategory(categoryName);
      this.maxRows = category.addInteger("maxRows", 16, 1, 100);
      this.maxColumns = category.addInteger("maxColumns", 9, 2, 100);
      this.horizontalAlignment = category.addEnum("horizontalAlignment", defaultHorizontalAlignment);
      this.verticalAlignment = category.addEnum("verticalAlignment", defaultVerticalAlignment);
      this.navigationVisibility = category.addEnum("navigationVisibility", defaultNavigationVisibility);
      this.drawBackground = category.addBoolean("drawBackground", false);
      this.layoutMode = category.addEnum("layoutMode", defaultLayoutMode);
      this.navigationMode = category.addEnum("navigationMode", defaultNavigationMode);
   }

   @Override
   public int getMinColumns() {
      return 2;
   }

   @Override
   public int getMinRows() {
      return 1;
   }

   public ConfigValue<Integer> maxColumns() {
      return this.maxColumns;
   }

   public ConfigValue<Integer> maxRows() {
      return this.maxRows;
   }

   public ConfigValue<Boolean> drawBackground() {
      return this.drawBackground;
   }

   public ConfigValue<IngredientGridLayoutMode> layoutMode() {
      return this.layoutMode;
   }

   public ConfigValue<IngredientGridNavigationMode> navigationMode() {
      return this.navigationMode;
   }

   public ConfigValue<HorizontalAlignment> horizontalAlignment() {
      return this.horizontalAlignment;
   }

   public ConfigValue<VerticalAlignment> verticalAlignment() {
      return this.verticalAlignment;
   }

   public ConfigValue<NavigationVisibility> navigationVisibility() {
      return this.navigationVisibility;
   }
}
