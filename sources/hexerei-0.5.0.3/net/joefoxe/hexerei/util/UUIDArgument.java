package net.joefoxe.hexerei.util;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.UUID;
import net.minecraft.network.chat.Component;

public class UUIDArgument implements ArgumentType<UUID> {
   public static final DynamicCommandExceptionType UUID_SECTION_INVALID = new DynamicCommandExceptionType(
      arg -> Component.translatable("argument.hexerei.uuid.section.invalid", new Object[]{arg})
   );
   public static final DynamicCommandExceptionType UUID_FORMAT_INVALID = new DynamicCommandExceptionType(
      arg -> Component.translatable("argument.hexerei.uuid.format.invalid", new Object[]{arg})
   );

   public static UUIDArgument uuid() {
      return new UUIDArgument();
   }

   public UUID parse(StringReader reader) throws CommandSyntaxException {
      String str = reader.readUnquotedString();

      try {
         return UUID.fromString(str);
      } catch (NumberFormatException var4) {
         throw UUID_SECTION_INVALID.create(str);
      } catch (IllegalArgumentException var5) {
         throw UUID_FORMAT_INVALID.create(str);
      }
   }
}
