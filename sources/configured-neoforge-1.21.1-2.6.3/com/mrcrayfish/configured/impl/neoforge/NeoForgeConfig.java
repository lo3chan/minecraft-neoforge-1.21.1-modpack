package com.mrcrayfish.configured.impl.neoforge;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.concurrent.SynchronizedConfig;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.google.common.base.Suppliers;
import com.mrcrayfish.configured.Constants;
import com.mrcrayfish.configured.api.ActionResult;
import com.mrcrayfish.configured.api.ConfigType;
import com.mrcrayfish.configured.api.ExecutionContext;
import com.mrcrayfish.configured.api.IConfigEntry;
import com.mrcrayfish.configured.api.IConfigValue;
import com.mrcrayfish.configured.api.IModConfig;
import com.mrcrayfish.configured.client.ClientSessionData;
import com.mrcrayfish.configured.network.payload.SyncNeoForgeConfigPayload;
import com.mrcrayfish.configured.util.ConfigHelper;
import com.mrcrayfish.configured.util.NeoForgeConfigHelper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.ValueSpec;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class NeoForgeConfig implements IModConfig {
   protected static final EnumMap<Type, ConfigType> TYPE_RESOLVER = (EnumMap<Type, ConfigType>)Util.make(new EnumMap(Type.class), map -> {
      map.put(Type.CLIENT, ConfigType.CLIENT);
      map.put(Type.COMMON, ConfigType.UNIVERSAL);
      map.put(Type.SERVER, ConfigType.WORLD_SYNC);
      map.put(Type.STARTUP, ConfigType.UNIVERSAL);
   });
   protected final ModConfig config;
   protected final Supplier<List<NeoForgeConfig.ForgeValueEntry>> allConfigValues;

   public NeoForgeConfig(ModConfig config) {
      this.config = config;
      this.allConfigValues = Suppliers.memoize(() -> this.getAllConfigValues(config));
   }

   @Override
   public ActionResult update(IConfigEntry entry) {
      CommentedConfig origConfig = NeoForgeConfigHelper.getConfigData(this.config);
      if (origConfig == null) {
         Constants.LOG.error("Unable to update config '{}' as it is not loaded", this.config.getFileName());
         return ActionResult.fail(Component.translatable("configured.gui.update_error.unloaded"));
      } else {
         Set<IConfigValue<?>> changedValues = ConfigHelper.getChangedValues(entry);
         if (!changedValues.isEmpty()) {
            SynchronizedConfig newConfig = new SynchronizedConfig(TomlFormat.instance(), LinkedHashMap::new);
            changedValues.forEach(value -> {
               if (value instanceof NeoForgeValue<?> forge) {
                  if (forge instanceof NeoForgeListValue<?> forgeList) {
                     List<?> converted = forgeList.getConverted();
                     if (converted != null) {
                        newConfig.set(forge.configValue.getPath(), converted);
                        return;
                     }
                  }

                  newConfig.set(forge.configValue.getPath(), value.get());
               }
            });
            origConfig.putAll(newConfig);
            NeoForgeConfigHelper.correctConfig(this.config, origConfig);
         }

         if (this.getType() == ConfigType.WORLD_SYNC && !ConfigHelper.isSingleplayer()) {
            if (!ConfigHelper.isPlayingGame()) {
               NeoForgeConfigHelper.saveConfig(this.config);
               NeoForgeConfigHelper.closeConfig(this.config);
            } else {
               this.syncToServer();
            }
         } else if (!changedValues.isEmpty()) {
            Constants.LOG.info("Saving config and sending reloading event for {}", this.config.getFileName());
            NeoForgeConfigHelper.resetConfigCache(this.config);
            NeoForgeConfigHelper.saveConfig(this.config);
         }

         return ActionResult.success();
      }
   }

   @Override
   public IConfigEntry createRootEntry() {
      return new NeoForgeFolderEntry(((ModConfigSpec)this.config.getSpec()).getValues(), (ModConfigSpec)this.config.getSpec());
   }

   @Override
   public ConfigType getType() {
      return TYPE_RESOLVER.get(this.config.getType());
   }

   @Override
   public String getFileName() {
      return this.config.getFileName();
   }

   @Override
   public String getModId() {
      return this.config.getModId();
   }

   @Override
   public ActionResult loadWorldConfig(Path path) {
      if (this.config.getLoadedConfig() == null) {
         try {
            NeoForgeConfigHelper.openConfig(this.config, path);
            return this.config.getLoadedConfig() != null ? ActionResult.success() : ActionResult.fail();
         } catch (Exception var3) {
            return ActionResult.fail(Component.literal(var3.getMessage()));
         }
      } else {
         return ActionResult.success();
      }
   }

   @Override
   public void stopEditing(boolean updated) {
      if (this.config != null && this.getType() == ConfigType.WORLD_SYNC && !ConfigHelper.isPlayingGame()) {
         NeoForgeConfigHelper.closeConfig(this.config);
      }
   }

   @Override
   public boolean isChanged() {
      CommentedConfig data = NeoForgeConfigHelper.getConfigData(this.config);
      return data == null ? false : this.allConfigValues.get().stream().anyMatch(entry -> !Objects.equals(entry.value.get(), entry.spec.getDefault()));
   }

   @Override
   public Optional<Runnable> restoreDefaultsTask() {
      return this.config.getType() == Type.SERVER && ConfigHelper.isPlayingOnRemoteServer()
         ? Optional.empty()
         : Optional.ofNullable(NeoForgeConfigHelper.getConfigData(this.config)).map(data -> () -> {
            CommentedConfig newConfig = CommentedConfig.copy(data);
            this.allConfigValues.get().forEach(entry -> newConfig.set(entry.value.getPath(), entry.spec.getDefault()));
            data.putAll(newConfig);
            this.allConfigValues.get().forEach(pair -> pair.value.clearCache());
         });
   }

   @Override
   public ActionResult canPlayerEdit(@Nullable Player player) {
      ExecutionContext context = new ExecutionContext(player);
      if (context.isClient()) {
         return switch (this.config.getType()) {
            case CLIENT, COMMON, STARTUP -> !context.isMainMenu() && !context.isLocalPlayer() ? ActionResult.fail() : ActionResult.success();
            case SERVER -> context.isMainMenu() || context.isSingleplayer()
               ? ActionResult.success()
               : (
                  context.isPlayingOnLan()
                     ? (
                        context.isIntegratedServerOwnedByPlayer()
                           ? ActionResult.fail(Component.translatable("configured.gui.no_editing_published_lan_server"))
                           : ActionResult.fail(Component.translatable("configured.gui.lan_server"))
                     )
                     : (
                        context.isPlayingOnRemoteServer()
                           ? (
                              context.isPlayerAnOperator() && context.isDeveloperPlayer()
                                 ? ActionResult.success()
                                 : ActionResult.fail(Component.translatable("configured.gui.no_developer_status"))
                           )
                           : ActionResult.fail()
                     )
               );
            default -> throw new MatchException(null, null);
         };
      } else if (!context.isDedicatedServer()) {
         return ActionResult.fail();
      } else {
         return switch (this.config.getType()) {
            case CLIENT, COMMON, STARTUP -> ActionResult.fail();
            case SERVER -> context.isPlayerAnOperator() && context.isDeveloperPlayer() ? ActionResult.success() : ActionResult.fail();
            default -> throw new MatchException(null, null);
         };
      }
   }

   @Override
   public ActionResult showSaveConfirmation(Player player) {
      ExecutionContext context = new ExecutionContext(player);
      return context.isClient() && context.isPlayingOnRemoteServer() && this.config.getType() == Type.SERVER
         ? ActionResult.success(Component.translatable("configured.gui.neoforge.players_kicked"))
         : ActionResult.fail();
   }

   private void syncToServer() {
      if (this.config != null) {
         CommentedConfig data = NeoForgeConfigHelper.getConfigData(this.config);
         if (data != null) {
            if (!ConfigHelper.isSingleplayer() && !ConfigHelper.isPlayingLan()) {
               if (ConfigHelper.isPlayingGame()) {
                  if (ConfigHelper.isConfiguredInstalledOnServer()) {
                     if (this.getType() == ConfigType.WORLD_SYNC) {
                        Player player = ConfigHelper.getClientPlayer();
                        if (ConfigHelper.isOperator(player) && ClientSessionData.isDeveloper()) {
                           try {
                              ByteArrayOutputStream stream = new ByteArrayOutputStream();
                              TomlFormat.instance().createWriter().write(data, stream);
                              PacketDistributor.sendToServer(
                                 new SyncNeoForgeConfigPayload(this.config.getFileName(), stream.toByteArray()), new CustomPacketPayload[0]
                              );
                              stream.close();
                           } catch (IOException var4) {
                              Constants.LOG.error("Failed to close byte stream when sending config to server");
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   protected List<NeoForgeConfig.ForgeValueEntry> getAllConfigValues(ModConfig config) {
      return NeoForgeConfigHelper.gatherAllConfigValues(config)
         .stream()
         .map(pair -> new NeoForgeConfig.ForgeValueEntry((ConfigValue<?>)pair.getLeft(), (ValueSpec)pair.getRight()))
         .toList();
   }

   protected record ForgeValueEntry(ConfigValue<?> value, ValueSpec spec) {
   }
}
