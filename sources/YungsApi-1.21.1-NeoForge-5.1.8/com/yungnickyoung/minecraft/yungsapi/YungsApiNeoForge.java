package com.yungnickyoung.minecraft.yungsapi;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.RegisterEvent.RegisterHelper;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;

@Mod("yungsapi")
public class YungsApiNeoForge {
   public static IEventBus loadingContextEventBus;

   public YungsApiNeoForge(IEventBus eventBus) {
      loadingContextEventBus = eventBus;
      YungsApiCommon.init();
   }

   public static <T> Consumer<RegisterEvent> buildSimpleRegistrar(ResourceKey<Registry<T>> registryKey, List<AutoRegisterField> registerables) {
      return buildAutoRegistrar(registryKey, registerables, data -> (T)data.object());
   }

   @NotNull
   public static <T> Consumer<RegisterEvent> buildAutoRegistrar(
      ResourceKey<Registry<T>> registryKey, List<AutoRegisterField> registerables, Function<AutoRegisterField, T> unwrapper
   ) {
      return buildAutoRegistrar(registryKey, registerables, unwrapper, (data, value, helper) -> helper.register(data.name(), value));
   }

   @NotNull
   public static <T> Consumer<RegisterEvent> buildAutoRegistrar(
      ResourceKey<Registry<T>> registryKey,
      List<AutoRegisterField> registerables,
      Function<AutoRegisterField, T> unwrapper,
      TriConsumer<AutoRegisterField, T, RegisterHelper<T>> registrationHandler
   ) {
      return event -> event.register(registryKey, helper -> registerables.stream().filter(data -> !data.processed()).forEach(data -> {
         registrationHandler.accept(data, unwrapper.apply(data), helper);
         data.markProcessed();
      }));
   }
}
