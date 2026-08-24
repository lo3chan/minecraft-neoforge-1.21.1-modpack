package vazkii.patchouli.neoforge.common;

import java.util.Collections;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.EnumValue;
import vazkii.patchouli.api.PatchouliConfigAccess;
import vazkii.patchouli.common.base.PatchouliConfig;

public class NeoForgePatchouliConfig {
   public static final ConfigValue<Boolean> disableAdvancementLocking;
   public static final ConfigValue<List<? extends String>> noAdvancementBooks;
   public static final ConfigValue<Boolean> testingMode;
   public static final ConfigValue<String> inventoryButtonBook;
   public static final ConfigValue<Boolean> useShiftForQuickLookup;
   public static final EnumValue<PatchouliConfigAccess.TextOverflowMode> overflowMode;
   public static final ConfigValue<Integer> quickLookupTime;
   private static final ModConfigSpec SPEC;

   public static void setup(ModContainer container) {
      container.registerConfig(Type.CLIENT, SPEC);
      PatchouliConfig.set(new PatchouliConfigAccess() {
         @Override
         public boolean disableAdvancementLocking() {
            return (Boolean)NeoForgePatchouliConfig.disableAdvancementLocking.get();
         }

         @Override
         public List<String> noAdvancementBooks() {
            return (List<String>)NeoForgePatchouliConfig.noAdvancementBooks.get();
         }

         @Override
         public boolean testingMode() {
            return (Boolean)NeoForgePatchouliConfig.testingMode.get();
         }

         @Override
         public String inventoryButtonBook() {
            return (String)NeoForgePatchouliConfig.inventoryButtonBook.get();
         }

         @Override
         public boolean useShiftForQuickLookup() {
            return (Boolean)NeoForgePatchouliConfig.useShiftForQuickLookup.get();
         }

         @Override
         public PatchouliConfigAccess.TextOverflowMode overflowMode() {
            return (PatchouliConfigAccess.TextOverflowMode)NeoForgePatchouliConfig.overflowMode.get();
         }

         @Override
         public int quickLookupTime() {
            return (Integer)NeoForgePatchouliConfig.quickLookupTime.get();
         }
      });
   }

   static {
      Builder builder = new Builder();
      disableAdvancementLocking = builder.comment(
            "Set this to true to disable advancement locking for ALL books, making all entries visible at all times. Config Flag: advancements_disabled"
         )
         .define("disableAdvancementLocking", false);
      noAdvancementBooks = builder.comment(
            "Granular list of Book ID's to disable advancement locking for, e.g. [ \"botania:lexicon\" ]. Config Flags: advancements_disabled_<bookid>"
         )
         .defineListAllowEmpty(List.of("noAdvancementBooks"), Collections::emptyList, o -> o instanceof String s && ResourceLocation.tryParse(s) != null);
      testingMode = builder.comment(
            "Enable testing mode. By default this doesn't do anything, but you can use the config flag in your books if you want. Config Flag: testing_mode"
         )
         .define("testingMode", false);
      inventoryButtonBook = builder.comment("Set this to the ID of a book to have it show up in players' inventories, replacing the recipe book.")
         .define("inventoryButtonBook", "");
      useShiftForQuickLookup = builder.comment("Set this to true to use Shift instead of Ctrl for the inventory quick lookup feature.")
         .define("useShiftForQuickLookup", false);
      overflowMode = builder.comment(
            "Set how text overflow should be coped with: overflow the text off the page, truncate overflowed text, or resize everything to fit. Relogin after changing."
         )
         .defineEnum("textOverflowMode", PatchouliConfigAccess.TextOverflowMode.RESIZE);
      quickLookupTime = builder.comment("How long in ticks the quick lookup key needs to be pressed before the book opens").define("quickLookupTime", 10);
      SPEC = builder.build();
   }
}
