package mezz.jei.common.config;

import mezz.jei.common.config.file.ConfigValue;
import mezz.jei.common.config.file.IConfigCategoryBuilder;
import mezz.jei.common.config.file.IConfigSchemaBuilder;
import mezz.jei.common.search.SearchMode;

public class IngredientFilterConfig implements IIngredientFilterConfig {
   private final ConfigValue<SearchMode> modNameSearchMode;
   private final ConfigValue<SearchMode> tooltipSearchMode;
   private final ConfigValue<SearchMode> tagSearchMode;
   private final ConfigValue<SearchMode> colorSearchMode;
   private final ConfigValue<SearchMode> resourceLocationSearchMode;
   private final ConfigValue<SearchMode> creativeTabSearchMode;
   private final ConfigValue<Boolean> searchAdvancedTooltips;
   private final ConfigValue<Boolean> searchModIds;
   private final ConfigValue<Boolean> searchModAliases;
   private final ConfigValue<Boolean> searchShortModNames;
   private final ConfigValue<Boolean> searchIngredientAliases;

   public IngredientFilterConfig(IConfigSchemaBuilder builder) {
      IConfigCategoryBuilder search = builder.addCategory("search");
      this.modNameSearchMode = search.addEnum("modNameSearchMode", SearchMode.REQUIRE_PREFIX);
      this.tagSearchMode = search.addEnum("tagSearchMode", SearchMode.REQUIRE_PREFIX);
      this.tooltipSearchMode = search.addEnum("tooltipSearchMode", SearchMode.ENABLED);
      this.colorSearchMode = search.addEnum("colorSearchMode", SearchMode.DISABLED);
      this.resourceLocationSearchMode = search.addEnum("resourceLocationSearchMode", SearchMode.DISABLED);
      this.creativeTabSearchMode = search.addEnum("creativeTabSearchMode", SearchMode.DISABLED);
      this.searchAdvancedTooltips = search.addBoolean("searchAdvancedTooltips", false);
      this.searchModIds = search.addBoolean("searchModIds", true);
      this.searchModAliases = search.addBoolean("searchModAliases", true);
      this.searchShortModNames = search.addBoolean("searchShortModNames", false);
      this.searchIngredientAliases = search.addBoolean("searchIngredientAliases", true);
   }

   public ConfigValue<SearchMode> modNameSearchMode() {
      return this.modNameSearchMode;
   }

   public ConfigValue<SearchMode> tooltipSearchMode() {
      return this.tooltipSearchMode;
   }

   public ConfigValue<SearchMode> tagSearchMode() {
      return this.tagSearchMode;
   }

   public ConfigValue<SearchMode> colorSearchMode() {
      return this.colorSearchMode;
   }

   public ConfigValue<SearchMode> resourceLocationSearchMode() {
      return this.resourceLocationSearchMode;
   }

   public ConfigValue<SearchMode> creativeTabSearchMode() {
      return this.creativeTabSearchMode;
   }

   public ConfigValue<Boolean> searchAdvancedTooltips() {
      return this.searchAdvancedTooltips;
   }

   public ConfigValue<Boolean> searchModIds() {
      return this.searchModIds;
   }

   public ConfigValue<Boolean> searchModAliases() {
      return this.searchModAliases;
   }

   public ConfigValue<Boolean> searchIngredientAliases() {
      return this.searchIngredientAliases;
   }

   public ConfigValue<Boolean> searchShortModNames() {
      return this.searchShortModNames;
   }
}
