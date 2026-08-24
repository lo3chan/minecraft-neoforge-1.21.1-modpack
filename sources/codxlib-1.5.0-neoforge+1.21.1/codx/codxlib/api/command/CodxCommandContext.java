package codx.codxlib.api.command;

import com.mojang.brigadier.context.CommandContext;

public final class CodxCommandContext {
   private final CommandContext<?> context;
   private final CodxCommandSource source;

   CodxCommandContext(CommandContext<?> context, CodxCommandSource source) {
      this.context = context;
      this.source = source;
   }

   public CodxCommandSource source() {
      return this.source;
   }

   public <T> T get(String name, Class<T> type) {
      return (T)this.context.getArgument(name, type);
   }

   public String getString(String name) {
      return (String)this.context.getArgument(name, String.class);
   }

   public int getInt(String name) {
      return (Integer)this.context.getArgument(name, Integer.class);
   }

   public boolean getBool(String name) {
      return (Boolean)this.context.getArgument(name, Boolean.class);
   }
}
