package de.cristelknight.cristellib.builtinpacks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.cristelknight.cristellib.config.simple.ConfigRegistry;
import de.cristelknight.cristellib.config.simple.ConfigSettings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.Util;

public record BuiltInPackConfig(List<String> defaultPacks, List<String> disabledPacks, boolean hideAllPacksInScreen) {
   public static final Codec<BuiltInPackConfig> CODEC = RecordCodecBuilder.create(
      builder -> builder.group(
            Codec.list(Codec.STRING).fieldOf("defaultPacks").forGetter(BuiltInPackConfig::defaultPacks),
            Codec.list(Codec.STRING).fieldOf("disabledPacks").forGetter(BuiltInPackConfig::disabledPacks),
            Codec.BOOL.fieldOf("hideAllPacksInScreen").forGetter(BuiltInPackConfig::hideAllPacksInScreen)
         )
         .apply(builder, BuiltInPackConfig::new)
   );
   public static final ConfigSettings<BuiltInPackConfig> SETTINGS = new ConfigSettings<BuiltInPackConfig>() {
      @Override
      public String getSubPath() {
         return "cristellib/built_in_packs";
      }

      @Override
      public Codec<BuiltInPackConfig> getCodec() {
         return BuiltInPackConfig.CODEC;
      }

      public BuiltInPackConfig getDefault() {
         return new BuiltInPackConfig(BuiltInPackLoader.getCustomIDs(), List.of(), false);
      }

      @Override
      public String getHeader() {
         return "This config allows disabling built-in packs supplied by Cristel Lib.\nMove entries from 'defaultPacks' to 'disabledPacks' to disable them.\n";
      }

      @Override
      public HashMap<String, String> getComments() {
         return (HashMap<String, String>)Util.make(
            new HashMap(),
            map -> map.put("hideAllPacksInScreen", "This option hides all packs provided by Cristel Lib in the pack selection screen to reduce clutter.")
         );
      }
   };

   public static void update() {
      BuiltInPackConfig config = ConfigRegistry.get(BuiltInPackConfig.class);
      List<String> defaultPacks = new ArrayList<>(config.defaultPacks());
      List<String> disabledPacks = new ArrayList<>(config.disabledPacks());
      boolean changed = false;
      if (defaultPacks.retainAll(SETTINGS.getDefault().defaultPacks())) {
         changed = true;
      }

      if (disabledPacks.retainAll(SETTINGS.getDefault().defaultPacks())) {
         changed = true;
      }

      for (String item : SETTINGS.getDefault().defaultPacks()) {
         if (!defaultPacks.contains(item) && !disabledPacks.contains(item)) {
            defaultPacks.add(item);
            changed = true;
         }
      }

      if (changed) {
         ConfigRegistry.updateAndSave(new BuiltInPackConfig(defaultPacks, disabledPacks, config.hideAllPacksInScreen()));
      }
   }

   static {
      ConfigRegistry.registerWithScreen(BuiltInPackConfig.class, SETTINGS, "cristellib", "Built-in Packs", BuiltInPackConfig::update);
   }
}
