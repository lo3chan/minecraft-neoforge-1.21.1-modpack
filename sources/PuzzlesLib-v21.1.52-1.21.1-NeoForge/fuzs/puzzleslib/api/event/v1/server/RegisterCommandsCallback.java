package fuzs.puzzleslib.api.event.v1.server;

import com.mojang.brigadier.CommandDispatcher;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands.CommandSelection;

@FunctionalInterface
public interface RegisterCommandsCallback {
   EventInvoker<RegisterCommandsCallback> EVENT = EventInvoker.lookup(RegisterCommandsCallback.class);

   void onRegisterCommands(CommandDispatcher<CommandSourceStack> var1, CommandBuildContext var2, CommandSelection var3);
}
