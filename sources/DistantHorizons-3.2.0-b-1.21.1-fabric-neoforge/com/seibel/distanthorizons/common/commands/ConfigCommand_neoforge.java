package com.seibel.distanthorizons.common.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.seibel.distanthorizons.core.config.ConfigHandler;
import com.seibel.distanthorizons.core.config.types.AbstractConfigBase;
import com.seibel.distanthorizons.core.config.types.ConfigEntry;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.function.ToIntBiFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class ConfigCommand_neoforge extends AbstractCommand_neoforge {
   private static final List<ConfigCommand$CommandArgumentData_neoforge<?>> commandArguments = Arrays.asList(
      new ConfigCommand$CommandArgumentData_neoforge<>(
         Integer.class, configEntry -> IntegerArgumentType.integer(configEntry.getMin(), configEntry.getMax()), IntegerArgumentType::getInteger
      ),
      new ConfigCommand$CommandArgumentData_neoforge<>(
         Double.class, configEntry -> DoubleArgumentType.doubleArg(configEntry.getMin(), configEntry.getMax()), DoubleArgumentType::getDouble
      ),
      new ConfigCommand$CommandArgumentData_neoforge<>(Boolean.class, BoolArgumentType::bool, BoolArgumentType::getBool),
      new ConfigCommand$CommandArgumentData_neoforge<>(String.class, StringArgumentType::string, StringArgumentType::getString)
   );

   @Override
   public LiteralArgumentBuilder<CommandSourceStack> buildCommand() {
      LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("config");
      HashSet<String> addedCommands = new HashSet<>();

      for (AbstractConfigBase<?> type : ConfigHandler.INSTANCE.configBaseList) {
         if (type instanceof ConfigEntry configEntry && configEntry.getChatCommandName() != null) {
            if (!addedCommands.add(configEntry.getChatCommandName())) {
               throw new IllegalStateException("Duplicate command name: " + configEntry.getChatCommandName());
            }

            LiteralArgumentBuilder<CommandSourceStack> subcommand = (LiteralArgumentBuilder<CommandSourceStack>)Commands.literal(
                  configEntry.getChatCommandName()
               )
               .executes(
                  commandContext -> this.sendSuccessResponse(
                     commandContext,
                     "\nDescription of §l"
                        + configEntry.getChatCommandName()
                        + "§r:\n§o"
                        + configEntry.getComment().trim()
                        + "§r\n§7Config file name: §f"
                        + configEntry.name
                        + "§7, category: §f"
                        + configEntry.category
                        + "\n\nCurrent value of "
                        + configEntry.getChatCommandName()
                        + " is §n"
                        + configEntry.get()
                        + "§r",
                     false
                  )
               );
            ToIntBiFunction<CommandContext<CommandSourceStack>, Object> updateConfigValue = (commandContext, value) -> {
               configEntry.set(value);
               return this.sendSuccessResponse(commandContext, "Changed the value of [" + configEntry.getChatCommandName() + "] to [" + value + "]", true);
            };
            if (Enum.class.isAssignableFrom(configEntry.getType())) {
               for (Object choice : configEntry.getType().getEnumConstants()) {
                  subcommand.then(Commands.literal(choice.toString()).executes(c -> updateConfigValue.applyAsInt(c, choice)));
               }
            } else {
               boolean setterAdded = false;

               for (ConfigCommand$CommandArgumentData_neoforge<?> commandArgumentData : commandArguments) {
                  if (commandArgumentData.argumentClass.isAssignableFrom(configEntry.getType())) {
                     subcommand.then(
                        Commands.argument("value", commandArgumentData.getArgumentType(configEntry))
                           .executes(c -> updateConfigValue.applyAsInt(c, commandArgumentData.getValue(c, "value")))
                     );
                     setterAdded = true;
                     break;
                  }
               }

               if (!setterAdded) {
                  throw new RuntimeException("Config type of " + type.getName() + " is not supported: " + configEntry.getType().getSimpleName());
               }
            }

            builder.then(subcommand);
         }
      }

      return builder;
   }
}
