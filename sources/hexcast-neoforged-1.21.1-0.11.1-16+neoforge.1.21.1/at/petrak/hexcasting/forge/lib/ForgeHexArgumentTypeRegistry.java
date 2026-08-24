package at.petrak.hexcasting.forge.lib;

import at.petrak.hexcasting.common.command.PatternResLocArgument;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfo.Template;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ForgeHexArgumentTypeRegistry {
   public static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARGUMENT_TYPES = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, "hexcasting");
   public static final DeferredHolder<ArgumentTypeInfo<?, ?>, ArgumentTypeInfo<PatternResLocArgument, SingletonArgumentInfo<PatternResLocArgument>.Template>> PATTERN_RESLOC = register(
      PatternResLocArgument.class, "pattern", SingletonArgumentInfo.contextFree(PatternResLocArgument::id)
   );

   private static <A extends ArgumentType<?>, T extends Template<A>, I extends ArgumentTypeInfo<A, T>> DeferredHolder<ArgumentTypeInfo<?, ?>, ArgumentTypeInfo<A, T>> register(
      Class<A> clazz, String name, ArgumentTypeInfo<A, T> ati
   ) {
      DeferredHolder<ArgumentTypeInfo<?, ?>, ArgumentTypeInfo<A, T>> robj = ARGUMENT_TYPES.register(name, () -> ati);
      ArgumentTypeInfos.registerByClass(clazz, ati);
      return robj;
   }
}
