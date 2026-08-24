package io.wispforest.owo.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class EnumArgumentType<T extends Enum<T>> implements ArgumentType<Enum<T>> {
   private final DynamicCommandExceptionType noValueException;
   private final String noElementMessage;
   private final Class<T> enumClass;

   private EnumArgumentType(Class<T> enumClass, String noElementMessage) {
      this.enumClass = enumClass;
      this.noElementMessage = noElementMessage;
      this.noValueException = new DynamicCommandExceptionType(o -> Component.literal(this.noElementMessage.replace("{}", o.toString())));
   }

   public static <T extends Enum<T>> EnumArgumentType<T> create(Class<T> enumClass) {
      return create(enumClass, "Invalid enum value '{}'");
   }

   public static <T extends Enum<T>> EnumArgumentType<T> create(Class<T> enumClass, String noElementMessage) {
      EnumArgumentType<T> type = new EnumArgumentType<>(enumClass, noElementMessage);
      ArgumentTypeInfos.registerByClass(
         type.getClass(),
         (SingletonArgumentInfo)Registry.register(
            BuiltInRegistries.COMMAND_ARGUMENT_TYPE,
            ResourceLocation.fromNamespaceAndPath("owo", "enum_" + enumClass.getName().toLowerCase(Locale.ROOT)),
            SingletonArgumentInfo.contextFree(() -> type)
         )
      );
      return type;
   }

   public T get(CommandContext<?> context, String name) {
      return (T)context.getArgument(name, this.enumClass);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      return SharedSuggestionProvider.suggest(Arrays.<Enum>stream(this.enumClass.getEnumConstants()).map(Enum::toString), builder);
   }

   public T parse(StringReader reader) throws CommandSyntaxException {
      String name = reader.readString();

      try {
         return Enum.valueOf(this.enumClass, name);
      } catch (IllegalArgumentException var4) {
         throw this.noValueException.create(name);
      }
   }
}
