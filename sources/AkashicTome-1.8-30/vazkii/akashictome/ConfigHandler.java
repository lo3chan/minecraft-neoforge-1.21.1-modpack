package vazkii.akashictome;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Predicate;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import org.apache.commons.lang3.tuple.Pair;

public class ConfigHandler {
   public static BooleanValue allItems;
   public static ConfigValue<List<? extends String>> whitelistedItems;
   public static ConfigValue<List<? extends String>> whitelistedNames;
   public static ConfigValue<List<? extends String>> blacklistedMods;
   public static ConfigValue<List<? extends String>> blacklistedItems;
   public static ConfigValue<List<? extends String>> aliasesList;
   public static BooleanValue hideBookRender;
   static final ConfigHandler CONFIG;
   static final ModConfigSpec CONFIG_SPEC;

   public ConfigHandler(Builder builder) {
      allItems = builder.define("Allow all items to be added", false);
      Predicate<Object> validator = o -> o instanceof String;
      whitelistedItems = builder.defineList(
         "Whitelisted Items",
         Lists.newArrayList(
            new String[]{
               "roots:runedtablet",
               "opencomputers:tool:4",
               "immersiveengineering:tool:3",
               "integrateddynamics:on_the_dynamics_of_integration",
               "theoneprobe:probenote",
               "evilcraft:origins_of_darkness",
               "draconicevolution:info_tablet",
               "charset:tablet",
               "antiqueatlas:antique_atlas",
               "theurgy:grimiore",
               "tconstruct:materials_and_you",
               "tconstruct:puny_smelting",
               "tconstruct:mighty_smelting",
               "tconstruct:tinkers_gadgetry",
               "tconstruct:fantastic_foundry",
               "tetra:holo",
               "occultism:dictionary_of_spirits"
            }
         ),
         validator
      );
      whitelistedNames = builder.defineList(
         "Whitelisted Names",
         Lists.newArrayList(
            new String[]{
               "book",
               "tome",
               "lexicon",
               "nomicon",
               "manual",
               "knowledge",
               "pedia",
               "compendium",
               "guide",
               "codex",
               "dictionary",
               "journal",
               "tablet",
               "grimoire",
               "bestiary"
            }
         ),
         validator
      );
      blacklistedMods = builder.defineList("Blacklisted Mods", Lists.newArrayList(), validator);
      blacklistedItems = builder.defineList(
         "Blacklisted Items", Lists.newArrayList(new String[]{"primalmagick:lore_tablet_dirty", "primalmagick:lore_tablet_fragment"}), validator
      );
      aliasesList = builder.defineList(
         "Mod Aliases",
         Lists.newArrayList(
            new String[]{
               "nautralpledge=botania",
               "thermalexpansion=thermalfoundation",
               "thermaldynamics=thermalfoundation",
               "thermalcultivation=thermalfoundation",
               "redstonearsenal=thermalfoundation",
               "rftoolsdim=rftools",
               "rftoolspower=rftools",
               "rftoolscontrol=rftools",
               "ae2stuff=appliedenergistics2",
               "animus=bloodmagic",
               "integrateddynamics=integratedtunnels",
               "mekanismgenerators=mekanism",
               "mekanismtools=mekanism",
               "deepresonance=rftools",
               "xnet=rftools",
               "buildcrafttransport=buildcraft",
               "buildcraftfactory=buildcraft",
               "buildcraftsilicon=buildcraft"
            }
         ),
         validator
      );
      hideBookRender = builder.define("Hide Book Render", false);
   }

   static {
      Pair<ConfigHandler, ModConfigSpec> specPair = new Builder().configure(ConfigHandler::new);
      CONFIG = (ConfigHandler)specPair.getLeft();
      CONFIG_SPEC = (ModConfigSpec)specPair.getRight();
   }
}
