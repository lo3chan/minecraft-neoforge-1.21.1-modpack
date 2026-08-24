package de.cristelknight.cristellib.autoconfig;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.cristelknight.cristellib.config.simple.ConfigRegistry;
import de.cristelknight.cristellib.config.simple.ConfigSettings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.Util;

public record ACConfig(
   boolean disableAutoConfig,
   boolean disableAutoConfigScreens,
   String autoConfigSubPath,
   List<String> blacklistedMods,
   List<String> clientExcludedMods,
   List<String> modOverrideWhitelist
) {
   public static final Codec<ACConfig> CODEC = RecordCodecBuilder.create(
      builder -> builder.group(
            Codec.BOOL.fieldOf("disableAutoConfig").forGetter(ACConfig::disableAutoConfig),
            Codec.BOOL.fieldOf("disableAutoConfigScreens").forGetter(ACConfig::disableAutoConfigScreens),
            Codec.STRING.fieldOf("autoConfigSubPath").forGetter(ACConfig::autoConfigSubPath),
            Codec.list(Codec.STRING).fieldOf("blacklistedMods").forGetter(ACConfig::blacklistedMods),
            Codec.list(Codec.STRING).fieldOf("clientExcludedMods").forGetter(ACConfig::clientExcludedMods),
            Codec.list(Codec.STRING).fieldOf("modOverrideWhitelist").forGetter(ACConfig::modOverrideWhitelist)
         )
         .apply(builder, ACConfig::new)
   );
   public static final ConfigSettings<ACConfig> SETTINGS = new ConfigSettings<ACConfig>() {
      @Override
      public String getSubPath() {
         return "cristellib/auto_config_settings";
      }

      @Override
      public Codec<ACConfig> getCodec() {
         return ACConfig.CODEC;
      }

      public ACConfig getDefault() {
         return new ACConfig(false, false, "cristellib/", ACInfoData.getBlackListedMods(), ACInfoData.getClientBlackListedMods(), List.of());
      }

      @Override
      public String getHeader() {
         return "Auto-Config Settings\nThe Config for Cristel Lib's automated structure config generation.\n";
      }

      @Override
      public HashMap<String, String> getComments() {
         return (HashMap<String, String>)Util.make(
            new HashMap(),
            map -> {
               map.put("disableAutoConfig", "Disable automatic structure config generation.");
               map.put("disableAutoConfigScreens", "Disable automatic screen generation for structure configs.");
               map.put("autoConfigSubPath", "Set the default sub path of all automatically generated configs. Requires a RESTART to apply!");
               map.put("blacklistedMods", "Mods where automatic structure config generation is disabled.");
               map.put("clientExcludedMods", "Mods where automatic screen generation for structure configs is disabled.");
               map.put(
                  "modOverrideWhitelist",
                  "This list lets you override the default settings provided by mod authors.\nIf you add a mod that is blacklisted by default (in the two other lists)\nyou can now remove it without it getting added back automatically.\nProceed at your own risk."
               );
            }
         );
      }
   };

   public static void update() {
      ACConfig config = ConfigRegistry.get(ACConfig.class);
      List<String> defaultBlacklistedMods = new ArrayList<>(SETTINGS.getDefault().blacklistedMods());
      List<String> defaultClientExcludedMods = new ArrayList<>(SETTINGS.getDefault().clientExcludedMods());
      List<String> modOverrideWhitelist = new ArrayList<>(config.modOverrideWhitelist());
      List<String> blacklistedMods = new ArrayList<>(config.blacklistedMods());
      List<String> clientExcludedMods = new ArrayList<>(config.clientExcludedMods());
      if (!defaultBlacklistedMods.isEmpty() || !defaultClientExcludedMods.isEmpty() || !modOverrideWhitelist.isEmpty()) {
         blacklistedMods.removeAll(defaultBlacklistedMods);
         clientExcludedMods.removeAll(defaultClientExcludedMods);
         List<String> allDefaultBlacklists = new ArrayList<>(SETTINGS.getDefault().blacklistedMods());
         allDefaultBlacklists.addAll(SETTINGS.getDefault().clientExcludedMods());
         modOverrideWhitelist.retainAll(allDefaultBlacklists);
         defaultBlacklistedMods.removeAll(modOverrideWhitelist);
         defaultClientExcludedMods.removeAll(modOverrideWhitelist);
         blacklistedMods.addAll(defaultBlacklistedMods);
         clientExcludedMods.addAll(defaultClientExcludedMods);
         ConfigRegistry.updateAndSave(
            new ACConfig(
               config.disableAutoConfig(),
               config.disableAutoConfigScreens(),
               config.autoConfigSubPath(),
               blacklistedMods,
               clientExcludedMods,
               modOverrideWhitelist
            )
         );
      }
   }

   static {
      ConfigRegistry.registerWithScreen(ACConfig.class, SETTINGS, "cristellib", "Auto-config", ACConfig::update);
   }
}
