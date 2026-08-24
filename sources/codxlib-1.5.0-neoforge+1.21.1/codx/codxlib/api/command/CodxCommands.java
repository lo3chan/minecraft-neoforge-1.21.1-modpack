package codx.codxlib.api.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

public final class CodxCommands {
   private static final List<Supplier<CodxCommandBuilder>> CLIENT = new CopyOnWriteArrayList<>();

   private CodxCommands() {
   }

   public static CodxCommandBuilder literal(String name) {
      return CodxCommandBuilder.literal(name);
   }

   public static CodxCommandBuilder argument(String name, ArgumentType<?> type) {
      return CodxCommandBuilder.argument(name, type);
   }

   public static void registerClient(Supplier<CodxCommandBuilder> command) {
      CLIENT.add(command);
   }

   public static <S> void buildClientInto(CommandDispatcher<S> dispatcher, CodxCommands.SourceAdapter<S> adapter) {
      for (Supplier<CodxCommandBuilder> command : CLIENT) {
         if (command.get().materialize(adapter) instanceof LiteralArgumentBuilder<S> literal) {
            dispatcher.register(literal);
         }
      }
   }

   @FunctionalInterface
   public interface SourceAdapter<S> {
      CodxCommandSource wrap(S var1);
   }
}
