package com.nyfaria.nyfsspiders.registration;

import com.mojang.brigadier.arguments.ArgumentType;
import com.nyfaria.nyfsspiders.registration.util.$InternalRegUtils;
import java.util.function.Supplier;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfo.Template;

public interface ArgumentTypeHelper {
   ArgumentTypeHelper INSTANCE = $InternalRegUtils.getOneAndOnlyService(ArgumentTypeHelper.class);

   <A extends ArgumentType<?>, T extends Template<A>, I extends ArgumentTypeInfo<A, T>> RegistryObject<ArgumentTypeInfo<?, ?>, I> register(
      RegistrationProvider<ArgumentTypeInfo<?, ?>> var1, String var2, Class<A> var3, Supplier<I> var4
   );
}
