package jeresources.neoforge.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import jeresources.config.Settings;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

public class ConfigValues {
   public static IntValue itemsPerColumn;
   public static IntValue itemsPerRow;
   public static BooleanValue diyData;
   public static BooleanValue showDevData;
   public static ConfigValue<List<? extends String>> enchantsBlacklist;
   public static ConfigValue<List<? extends String>> hiddenTabs;
   public static ConfigValue<List<? extends Integer>> dimensionsBlacklist;
   public static BooleanValue disableLootManagerReloading;

   public static ModConfigSpec build() {
      Builder builder = new Builder();
      itemsPerColumn = builder.defineInRange("itemsPerColumn", 4, 1, 4);
      itemsPerRow = builder.defineInRange("itemsPerRow", 4, 1, 4);
      diyData = builder.worldRestart().define("diyData", true);
      showDevData = builder.worldRestart().define("showDevData", false);
      enchantsBlacklist = builder.worldRestart()
         .defineListAllowEmpty("enchantsBlacklist", List.of("flimflam", "soulBound"), () -> "", new ConfigValues.TypePredicate(String.class));
      hiddenTabs = builder.worldRestart().defineListAllowEmpty("hiddenTabs", new ArrayList(), () -> "", new ConfigValues.TypePredicate(String.class));
      dimensionsBlacklist = builder.worldRestart()
         .defineListAllowEmpty("dimensionsBlacklist", List.of(-11), () -> 100, new ConfigValues.TypePredicate(Integer.class));
      disableLootManagerReloading = builder.worldRestart().define("disableLootManagerReloading", false);
      return builder.build();
   }

   public static void pushChanges() {
      Settings.ITEMS_PER_COLUMN = (Integer)itemsPerColumn.get();
      Settings.ITEMS_PER_ROW = (Integer)itemsPerRow.get();
      Settings.useDIYdata = (Boolean)diyData.get();
      Settings.showDevData = (Boolean)showDevData.get();
      Settings.disableLootManagerReloading = (Boolean)disableLootManagerReloading.get();
      Settings.excludedEnchants = ((List)enchantsBlacklist.get()).toArray(new String[0]);
      Settings.hiddenCategories = ((List)hiddenTabs.get()).toArray(new String[0]);
      Settings.excludedDimensions = new ArrayList<>((Collection<? extends Integer>)dimensionsBlacklist.get());
      Settings.reload();
   }

   private static class TypePredicate implements Predicate<Object> {
      private Class type;

      public TypePredicate(Class type) {
         this.type = type;
      }

      @Override
      public boolean test(Object o) {
         return this.type.isInstance(o);
      }
   }
}
