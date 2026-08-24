package jeresources.neoforge.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import java.nio.file.Path;
import jeresources.util.LogHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent.Loading;
import net.neoforged.fml.event.config.ModConfigEvent.Reloading;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
   public static Config instance = new Config();
   public static final ModConfigSpec COMMON = ConfigValues.build();

   private Config() {
   }

   public void loadConfig(ModConfigSpec spec, Path path) {
      LogHelper.debug("Loading config file {}", path);
      CommentedFileConfig configData = (CommentedFileConfig)CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();
      LogHelper.debug("Built TOML config for {}", path.toString());
      configData.load();
      LogHelper.debug("Loaded TOML config file {}", path.toString());
      spec.correct(configData);
      ConfigValues.pushChanges();
   }

   @SubscribeEvent
   public void onLoad(Loading configEvent) {
      LogHelper.debug("Loaded {} config file {}", "jeresources", configEvent.getConfig().getFileName());
      ConfigValues.pushChanges();
   }

   @SubscribeEvent
   public void onFileChange(Reloading configEvent) {
      LogHelper.debug("Reloaded {} config file {}", "jeresources", configEvent.getConfig().getFileName());
      ConfigValues.pushChanges();
   }
}
