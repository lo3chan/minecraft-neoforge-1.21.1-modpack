package fuzs.puzzleslib.neoforge.impl.config;

import com.electronwill.nightconfig.core.file.FileWatcher;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.config.ConfigDataHolderImpl;
import fuzs.puzzleslib.impl.config.ConfigHolderImpl;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import java.nio.file.Path;
import java.util.function.Supplier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.config.ModConfig.Type;

public class NeoForgeConfigHolderImpl extends ConfigHolderImpl {
   public NeoForgeConfigHolderImpl(String modId) {
      super(modId);
   }

   @Override
   protected <T extends ConfigCore> ConfigDataHolderImpl<T> client(Supplier<T> supplier) {
      return new NeoForgeConfigHolderImpl.NeoForgeConfigDataHolderImpl<>(Type.STARTUP, Type.CLIENT, supplier);
   }

   @Override
   protected <T extends ConfigCore> ConfigDataHolderImpl<T> common(Supplier<T> supplier) {
      return new NeoForgeConfigHolderImpl.NeoForgeConfigDataHolderImpl<>(Type.STARTUP, Type.COMMON, supplier);
   }

   @Override
   protected <T extends ConfigCore> ConfigDataHolderImpl<T> server(Supplier<T> supplier) {
      return new NeoForgeConfigHolderImpl.NeoForgeConfigDataHolderImpl<>(Type.SERVER, supplier);
   }

   @Override
   protected void bake(ConfigDataHolderImpl<?> holder, String modId) {
      NeoForgeModContainerHelper.getOptionalModEventBus(modId).ifPresent(eventBus -> this.registerLoadingHandlers(eventBus, holder));
      ((NeoForgeConfigHolderImpl.NeoForgeConfigDataHolderImpl)holder).register(modId);
   }

   private void registerLoadingHandlers(IEventBus eventBus, ConfigDataHolderImpl<?> holder) {
      eventBus.addListener(
         event -> ((NeoForgeConfigHolderImpl.NeoForgeConfigDataHolderImpl)holder)
            .onModConfig(event.getConfig(), ConfigDataHolderImpl.ModConfigEventType.LOADING)
      );
      eventBus.addListener(
         event -> ((NeoForgeConfigHolderImpl.NeoForgeConfigDataHolderImpl)holder)
            .onModConfig(event.getConfig(), ConfigDataHolderImpl.ModConfigEventType.RELOADING)
      );
      eventBus.addListener(
         event -> ((NeoForgeConfigHolderImpl.NeoForgeConfigDataHolderImpl)holder)
            .onModConfig(event.getConfig(), ConfigDataHolderImpl.ModConfigEventType.UNLOADING)
      );
   }

   private static class NeoForgeConfigDataHolderImpl<T extends ConfigCore> extends ConfigDataHolderImpl<T> {
      private final Type configType;

      NeoForgeConfigDataHolderImpl(Type configType, Supplier<T> supplier) {
         this(configType, configType, supplier);
      }

      NeoForgeConfigDataHolderImpl(Type configType, Type configNameType, Supplier<T> supplier) {
         super(supplier);
         this.setFileNameFactory(ConfigHolder.getDefaultNameFactory(configNameType.extension()));
         this.configType = configType;
      }

      void onModConfig(ModConfig modConfig, ConfigDataHolderImpl.ModConfigEventType eventType) {
         if (modConfig.getType() == this.configType) {
            super.onModConfig(eventType, modConfig.getFileName(), () -> {
               if (modConfig.getLoadedConfig() != null && !modConfig.getLoadedConfig().config().configFormat().isInMemory()) {
                  try {
                     Path path = modConfig.getFullPath();
                     FileWatcher.defaultInstance().removeWatch(path);
                  } catch (RuntimeException var2) {
                     PuzzlesLib.LOGGER.error("Failed to remove config {} from tracker!", modConfig.getFileName(), var2);
                  }
               }
            });
         }
      }

      void register(String modId) {
         ModContainer modContainer = NeoForgeModContainerHelper.getModContainer(modId);
         modContainer.registerConfig(this.configType, this.initialize(modId), this.getFileName());
      }
   }
}
