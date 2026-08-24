package dev.latvian.mods.kubejs.server;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.ParsedCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.neoforge.event.CommandEvent;

public class CommandKubeEvent extends ServerKubeEvent {
   private final CommandEvent event;
   private final String commandName;

   public CommandKubeEvent(CommandEvent event) {
      super(((CommandSourceStack)event.getParseResults().getContext().getSource()).getServer());
      this.event = event;
      this.commandName = event.getParseResults().getContext().getNodes().isEmpty()
         ? ""
         : ((ParsedCommandNode)event.getParseResults().getContext().getNodes().getFirst()).getNode().getName();
   }

   public String getCommandName() {
      return this.commandName;
   }

   public String getInput() {
      return this.event.getParseResults().getReader().getString();
   }

   public ParseResults<CommandSourceStack> getParseResults() {
      return this.event.getParseResults();
   }

   public void setParseResults(ParseResults<CommandSourceStack> parse) {
      this.event.setParseResults(parse);
   }

   public Throwable getException() {
      return this.event.getException();
   }

   public void setException(Throwable exception) {
      this.event.setException(exception);
   }
}
