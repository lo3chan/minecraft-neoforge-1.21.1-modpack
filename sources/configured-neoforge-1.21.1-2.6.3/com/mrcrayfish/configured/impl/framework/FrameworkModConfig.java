package com.mrcrayfish.configured.impl.framework;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mrcrayfish.configured.Constants;
import com.mrcrayfish.configured.api.ActionResult;
import com.mrcrayfish.configured.api.ExecutionContext;
import com.mrcrayfish.configured.api.IConfigEntry;
import com.mrcrayfish.configured.api.IConfigValue;
import com.mrcrayfish.configured.api.IModConfig;
import com.mrcrayfish.configured.client.ClientSessionData;
import com.mrcrayfish.configured.impl.framework.message.MessageFramework;
import com.mrcrayfish.configured.util.ConfigHelper;
import com.mrcrayfish.framework.api.Environment;
import com.mrcrayfish.framework.api.config.AbstractProperty;
import com.mrcrayfish.framework.api.config.ConfigType;
import com.mrcrayfish.framework.api.config.event.FrameworkConfigEvents;
import com.mrcrayfish.framework.api.config.event.FrameworkConfigEvents.Reload;
import com.mrcrayfish.framework.config.FrameworkConfigManager.FrameworkConfigImpl;
import com.mrcrayfish.framework.config.FrameworkConfigManager.IMapEntry;
import com.mrcrayfish.framework.platform.Services;
import it.unimi.dsi.fastutil.Pair;
import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

public class FrameworkModConfig implements IModConfig {
   private static final Map<ConfigType, com.mrcrayfish.configured.api.ConfigType> TYPE_MAPPER = (Map<ConfigType, com.mrcrayfish.configured.api.ConfigType>)Util.make(
      () -> {
         Builder<ConfigType, com.mrcrayfish.configured.api.ConfigType> builder = ImmutableMap.builder();
         builder.put(ConfigType.CLIENT, com.mrcrayfish.configured.api.ConfigType.CLIENT);
         builder.put(ConfigType.UNIVERSAL, com.mrcrayfish.configured.api.ConfigType.UNIVERSAL);
         builder.put(ConfigType.SERVER, com.mrcrayfish.configured.api.ConfigType.SERVER);
         builder.put(ConfigType.SERVER_SYNC, com.mrcrayfish.configured.api.ConfigType.SERVER_SYNC);
         builder.put(ConfigType.DEDICATED_SERVER, com.mrcrayfish.configured.api.ConfigType.DEDICATED_SERVER);
         builder.put(ConfigType.WORLD, com.mrcrayfish.configured.api.ConfigType.WORLD);
         builder.put(ConfigType.WORLD_SYNC, com.mrcrayfish.configured.api.ConfigType.WORLD_SYNC);
         builder.put(ConfigType.MEMORY, com.mrcrayfish.configured.api.ConfigType.MEMORY);
         return builder.build();
      }
   );
   private final FrameworkConfigImpl config;
   private final Supplier<FrameworkModConfig.PropertyMap> map;

   public FrameworkModConfig(FrameworkConfigImpl config) {
      this.config = config;
      this.map = Suppliers.memoize(() -> new FrameworkModConfig.PropertyMap(config));
   }

   @Override
   public ActionResult update(IConfigEntry entry) {
      Preconditions.checkState(this.config.getConfig() != null, "Tried to update a config that is not loaded");
      if (!this.config.isReadOnly() && this.config.getConfig() instanceof Config configData) {
         Set<IConfigValue<?>> changedValues = ConfigHelper.getChangedValues(entry);
         if (changedValues.isEmpty()) {
            return ActionResult.success();
         } else {
            CommentedConfig newConfigData = CommentedConfig.inMemory();
            changedValues.forEach(value -> {
               if (value instanceof FrameworkValue<?> frameworkValue) {
                  newConfigData.set(frameworkValue.getPath(), frameworkValue.get());
               }
            });
            configData.putAll(newConfigData);
            this.config.correct(configData);
            this.config.getAllProperties().forEach(AbstractProperty::invalidateCache);
            if (this.getType().isServer()) {
               if (!ConfigHelper.isPlayingGame()) {
                  this.config.unload(false);
                  return ActionResult.success();
               }

               this.syncToServer();
               if (!ConfigHelper.isIntegratedServer() && !this.getType().isSync()) {
                  this.config.unload(false);
                  return ActionResult.success();
               }
            }

            Constants.LOG.info("Sending config reloading event for {}", this.getFileName());
            ((Reload)FrameworkConfigEvents.RELOAD.post()).handle(this.config.getSource());
            return ActionResult.success();
         }
      } else {
         return ActionResult.fail(Component.translatable("configured.gui.update_error.read_only"));
      }
   }

   @Override
   public IConfigEntry createRootEntry() {
      return new FrameworkFolderEntry((FrameworkModConfig.PropertyMap)this.map.get());
   }

   @Override
   public com.mrcrayfish.configured.api.ConfigType getType() {
      return TYPE_MAPPER.get(this.config.getType());
   }

   @Override
   public String getFileName() {
      return this.config.getFileName();
   }

   @Override
   public String getModId() {
      return this.config.getName().getNamespace();
   }

   @Override
   public ActionResult loadWorldConfig(Path path) {
      if (!this.config.isLoaded()) {
         this.config.load(path, false);
         return this.config.isLoaded() ? ActionResult.success() : ActionResult.fail(Component.translatable("configured.gui.load_world_config_failed"));
      } else {
         return ActionResult.success();
      }
   }

   @Override
   public boolean isReadOnly() {
      return this.config.isReadOnly();
   }

   @Override
   public boolean isChanged() {
      return this.config.isChanged();
   }

   @Override
   public Optional<Runnable> restoreDefaultsTask() {
      if (this.config.isReadOnly()) {
         return Optional.empty();
      } else if (FrameworkConfigHelper.isWorldType(this.config) && !this.config.isLoaded()) {
         return Optional.empty();
      } else {
         return this.config.getType().isServer() && ConfigHelper.isPlayingOnRemoteServer() ? Optional.empty() : Optional.of(this.config::restoreDefaults);
      }
   }

   @Override
   public void startEditing() {
      if (!ConfigHelper.isPlayingGame() && ConfigHelper.isServerConfig(this)) {
         this.config.load(Services.CONFIG.getConfigPath(), false);
      }
   }

   @Override
   public void stopEditing(boolean changed) {
      if (this.config.getConfig() != null) {
         if (this.getType().isServer()) {
            if (!ConfigHelper.isPlayingGame() || !ConfigHelper.isIntegratedServer() && !this.getType().isSync()) {
               this.config.unload(false);
            }
         }
      }
   }

   @Override
   public Optional<Runnable> requestFromServerTask() {
      return this.config.getType().isServer()
            && !this.config.getType().isSync()
            && this.config.getType().getEnv().stream().noneMatch(Environment::isDedicatedServer)
         ? Optional.of(() -> com.mrcrayfish.configured.platform.Services.PLATFORM.sendFrameworkConfigRequest(this.config.getName()))
         : Optional.empty();
   }

   @Override
   public ActionResult canPlayerEdit(@Nullable Player player) {
      ExecutionContext context = new ExecutionContext(player);
      if (context.isClient()) {
         return switch (this.getType()) {
            case DEDICATED_SERVER -> ActionResult.fail();
            case CLIENT, UNIVERSAL, MEMORY -> !context.isMainMenu() && !context.isLocalPlayer() ? ActionResult.fail() : ActionResult.success();
            case SERVER, WORLD, SERVER_SYNC, WORLD_SYNC -> context.isMainMenu() || context.isSingleplayer()
               ? ActionResult.success()
               : (
                  context.isPlayingOnLan()
                     ? (
                        context.isIntegratedServerOwnedByPlayer()
                           ? ActionResult.success()
                           : ActionResult.fail(Component.translatable("configured.gui.lan_server"))
                     )
                     : (
                        context.isPlayingOnRemoteServer()
                           ? (
                              context.isPlayerAnOperator() && context.isDeveloperPlayer()
                                 ? ActionResult.success()
                                 : ActionResult.fail(Component.translatable("configured.gui.no_developer_status"))
                           )
                           : ActionResult.fail(Component.translatable("configured.gui.no_permission"))
                     )
               );
         };
      } else if (!context.isDedicatedServer()) {
         return ActionResult.fail();
      } else {
         return switch (this.getType()) {
            case DEDICATED_SERVER, CLIENT, UNIVERSAL, MEMORY -> ActionResult.fail();
            case SERVER, WORLD, SERVER_SYNC, WORLD_SYNC -> context.isPlayerAnOperator() && context.isDeveloperPlayer()
               ? ActionResult.success()
               : ActionResult.fail();
         };
      }
   }

   private void syncToServer() {
      if (com.mrcrayfish.configured.platform.Services.PLATFORM.getEnvironment() == com.mrcrayfish.configured.api.Environment.CLIENT) {
         if (this.config.getConfig() != null) {
            if (ConfigHelper.isPlayingGame()) {
               if (ConfigHelper.isConfiguredInstalledOnServer()) {
                  if (this.getType().isServer() && this.getType() != com.mrcrayfish.configured.api.ConfigType.DEDICATED_SERVER) {
                     if (!this.isReadOnly()) {
                        Player player = ConfigHelper.getClientPlayer();
                        if (ConfigHelper.isOperator(player) && ClientSessionData.isDeveloper()) {
                           ByteArrayOutputStream stream = new ByteArrayOutputStream();
                           TomlFormat.instance().createWriter().write(this.config.getConfig(), stream);
                           com.mrcrayfish.configured.platform.Services.PLATFORM.sendFrameworkConfigToServer(this.config.getName(), stream.toByteArray());
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public boolean loadDataFromResponse(MessageFramework.Response message) {
      return this.config.loadFromData(message.data());
   }

   public static class PropertyMap implements IMapEntry {
      private final Map<String, IMapEntry> map = new HashMap<>();
      private final FrameworkConfigImpl config;
      private final List<String> path;

      private PropertyMap(FrameworkConfigImpl config, List<String> path) {
         this.config = config;
         this.path = path;
      }

      private PropertyMap(FrameworkConfigImpl config) {
         this.config = config;
         this.path = new ArrayList<>();
         config.getAllProperties()
            .forEach(
               p -> {
                  FrameworkModConfig.PropertyMap current = this;
                  List<String> path = p.getPath();

                  for (int i = 0; i < path.size() - 1; i++) {
                     int finalI = i + 1;
                     current = (FrameworkModConfig.PropertyMap)current.map
                        .computeIfAbsent(path.get(i), s -> new FrameworkModConfig.PropertyMap(config, path.subList(0, finalI)));
                  }

                  current.map.put(path.get(path.size() - 1), p);
               }
            );
      }

      public List<Pair<String, FrameworkModConfig.PropertyMap>> getConfigMaps() {
         return this.map
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue() instanceof FrameworkModConfig.PropertyMap)
            .map(entry -> Pair.of(entry.getKey(), (FrameworkModConfig.PropertyMap)entry.getValue()))
            .toList();
      }

      public List<AbstractProperty<?>> getConfigProperties() {
         List<AbstractProperty<?>> properties = new ArrayList<>();
         this.map.forEach((name, entry) -> {
            if (entry instanceof AbstractProperty<?> property) {
               properties.add(property);
            }
         });
         return properties;
      }

      @Nullable
      public String getComment() {
         return this.path != null && !this.path.isEmpty() ? this.config.getComments().getComment(this.path) : null;
      }

      public List<String> getPath() {
         return this.path;
      }

      public String getTranslationKey() {
         return this.path != null && !this.path.isEmpty()
            ? String.format("config.%s.%s.%s", this.config.getName().getNamespace(), this.config.getName().getPath(), StringUtils.join(this.path, '.'))
            : String.format("config.%s.%s", this.config.getName().getNamespace(), this.config.getName().getPath());
      }
   }
}
