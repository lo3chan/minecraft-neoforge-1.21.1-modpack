package codx.codxlib.api.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.ToIntFunction;

public final class CodxCommandBuilder {
   private final String name;
   private final ArgumentType<?> type;
   private ToIntFunction<CodxCommandContext> action;
   private final List<CodxCommandBuilder> children = new ArrayList<>();

   private CodxCommandBuilder(String name, ArgumentType<?> type) {
      this.name = name;
      this.type = type;
   }

   static CodxCommandBuilder literal(String name) {
      return new CodxCommandBuilder(name, null);
   }

   static CodxCommandBuilder argument(String name, ArgumentType<?> type) {
      return new CodxCommandBuilder(name, type);
   }

   public CodxCommandBuilder executes(ToIntFunction<CodxCommandContext> action) {
      this.action = action;
      return this;
   }

   public CodxCommandBuilder then(CodxCommandBuilder child) {
      this.children.add(child);
      return this;
   }

   <S> ArgumentBuilder<S, ?> materialize(CodxCommands.SourceAdapter<S> adapter) {
      ArgumentBuilder<S, ?> builder = (ArgumentBuilder<S, ?>)(this.type == null
         ? LiteralArgumentBuilder.literal(this.name)
         : RequiredArgumentBuilder.argument(this.name, this.type));
      if (this.action != null) {
         ToIntFunction<CodxCommandContext> a = this.action;
         builder.executes(ctx -> a.applyAsInt(new CodxCommandContext(ctx, adapter.wrap((S)ctx.getSource()))));
      }

      for (CodxCommandBuilder child : this.children) {
         builder.then(child.materialize(adapter));
      }

      return builder;
   }
}
