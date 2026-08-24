package net.blay09.mods.balm.neoforge.client.internal.commands;

import com.mojang.brigadier.CommandDispatcher;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import net.blay09.mods.balm.api.client.commands.BalmClientCommands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.neoforged.neoforge.common.NeoForge;

public class NeoForgeBalmClientCommands implements BalmClientCommands {
   private final List<Consumer<CommandDispatcher<SharedSuggestionProvider>>> commands = Collections.synchronizedList(new ArrayList<>());

   public NeoForgeBalmClientCommands() {
      NeoForge.EVENT_BUS.addListener(event -> {
         CommandDispatcher<SharedSuggestionProvider> sharedDispatcher = asSharedDispatcher(event.getDispatcher());
         this.commands.forEach(it -> it.accept(sharedDispatcher));
      });
   }

   private static CommandDispatcher<SharedSuggestionProvider> asSharedDispatcher(CommandDispatcher<?> dispatcher) {
      return (CommandDispatcher<SharedSuggestionProvider>)dispatcher;
   }

   @Override
   public void register(Consumer<CommandDispatcher<SharedSuggestionProvider>> initializer) {
      this.commands.add(initializer);
   }
}
