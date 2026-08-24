package com.nyfaria.nyfsspiders.registration.neoforge;

import com.google.auto.service.AutoService;
import com.mojang.brigadier.arguments.ArgumentType;
import com.nyfaria.nyfsspiders.registration.ArgumentTypeHelper;
import com.nyfaria.nyfsspiders.registration.RegistrationProvider;
import com.nyfaria.nyfsspiders.registration.RegistryObject;
import java.util.function.Supplier;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.ArgumentTypeInfo.Template;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
@AutoService({ArgumentTypeHelper.class})
public class NeoForgeArgumentTypeHelper implements ArgumentTypeHelper {
   @Override
   public <A extends ArgumentType<?>, T extends Template<A>, I extends ArgumentTypeInfo<A, T>> RegistryObject<ArgumentTypeInfo<?, ?>, I> register(
      RegistrationProvider<ArgumentTypeInfo<?, ?>> provider, String name, Class<A> clazz, Supplier<I> serializer
   ) {
      return (RegistryObject<ArgumentTypeInfo<?, ?>, I>)provider.register(name, () -> ArgumentTypeInfos.registerByClass(clazz, serializer.get()));
   }
}
