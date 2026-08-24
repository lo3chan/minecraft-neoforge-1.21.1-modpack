package malte0811.ferritecore.mixin.platform;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import malte0811.ferritecore.mixin.config.FerriteConfig;
import malte0811.ferritecore.mixin.config.IPlatformConfigHooks;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.loading.moddiscovery.ModInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigFileHandler implements IPlatformConfigHooks {
   private static final Logger LOGGER = LoggerFactory.getLogger("ferritecore-overrides");

   @Override
   public void readAndUpdateConfig(List<FerriteConfig.Option> options) throws IOException {
      ConfigSpec spec = new ConfigSpec();

      for (FerriteConfig.Option o : options) {
         spec.define(o.getName(), o.getDefaultValue());
      }

      CommentedFileConfig configData = read(FMLPaths.CONFIGDIR.get().resolve("ferritecore-mixin.toml"));

      for (FerriteConfig.Option o : options) {
         configData.setComment(o.getName(), o.getComment());
      }

      spec.correct(configData);
      configData.save();

      for (FerriteConfig.Option o : options) {
         o.set(configData::get);
      }
   }

   @Override
   public void collectDisabledOverrides(IPlatformConfigHooks.OverrideCallback disableOption) {
      for (ModInfo mod : FMLLoader.getLoadingModList().getMods()) {
         Optional<Object> maybeOverrides = mod.getConfigElement(new String[]{"ferritecore:disabled_options"});
         if (!maybeOverrides.isEmpty()) {
            Object var6 = maybeOverrides.get();
            if (var6 instanceof List) {
               for (Object override : (List)var6) {
                  if (override instanceof String overrideName) {
                     disableOption.addOverride(overrideName, mod.getModId());
                  } else {
                     LOGGER.warn("Override list for {} contains non-string {}", mod.getModId(), override);
                  }
               }
            } else {
               LOGGER.warn("Overrides for {} are not a list: {}", mod.getModId(), maybeOverrides.get());
            }
         }
      }
   }

   private static CommentedFileConfig read(Path configPath) {
      CommentedFileConfig configData = (CommentedFileConfig)CommentedFileConfig.builder(configPath).sync().preserveInsertionOrder().build();
      configData.load();
      return configData;
   }
}
