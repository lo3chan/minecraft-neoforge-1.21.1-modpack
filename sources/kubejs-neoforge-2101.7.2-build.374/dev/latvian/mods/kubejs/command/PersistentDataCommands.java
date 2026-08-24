package dev.latvian.mods.kubejs.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.latvian.mods.kubejs.core.WithPersistentData;
import dev.latvian.mods.kubejs.util.UtilsJS;
import java.util.Collection;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.ObjectiveArgument;
import net.minecraft.commands.arguments.ScoreHolderArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ReadOnlyScoreInfo;
import net.minecraft.world.scores.ScoreHolder;

public class PersistentDataCommands {
   public static ArgumentBuilder<CommandSourceStack, ?> addPersistentDataCommands(
      ArgumentBuilder<CommandSourceStack, ?> cmd, PersistentDataCommands.PersistentDataFactory factory
   ) {
      cmd.then(
         ((LiteralArgumentBuilder)Commands.literal("get")
               .then(
                  Commands.literal("*")
                     .executes(
                        ctx -> {
                           Collection<? extends WithPersistentData> objects = factory.getAll(ctx);

                           for (WithPersistentData o : objects) {
                              Component dataStr = NbtUtils.toPrettyComponent(o.kjs$getPersistentData());
                              ((CommandSourceStack)ctx.getSource())
                                 .sendSuccess(
                                    () -> Component.literal("")
                                       .append(Component.literal("").withStyle(ChatFormatting.YELLOW).append(o.kjs$getDisplayName()))
                                       .append(": ")
                                       .append(dataStr),
                                    false
                                 );
                           }

                           return objects.size();
                        }
                     )
               ))
            .then(
               Commands.argument("key", StringArgumentType.string())
                  .executes(
                     ctx -> {
                        Collection<? extends WithPersistentData> objects = factory.getAll(ctx);
                        String key = StringArgumentType.getString(ctx, "key");

                        for (WithPersistentData o : objects) {
                           Tag data = (Tag)(key.equals("*") ? o.kjs$getPersistentData() : o.kjs$getPersistentData().get(key));
                           Component dataStr = (Component)(data == null
                              ? Component.literal("null").withStyle(ChatFormatting.RED)
                              : NbtUtils.toPrettyComponent(data));
                           ((CommandSourceStack)ctx.getSource())
                              .sendSuccess(
                                 () -> Component.literal("")
                                    .append(Component.literal("").withStyle(ChatFormatting.YELLOW).append(o.kjs$getDisplayName()))
                                    .append(": ")
                                    .append(dataStr),
                                 false
                              );
                        }

                        return objects.size();
                     }
                  )
            )
      );
      cmd.then(
         Commands.literal("merge")
            .then(
               Commands.argument("nbt", CompoundTagArgument.compoundTag())
                  .executes(
                     ctx -> {
                        Collection<? extends WithPersistentData> objects = factory.getAll(ctx);
                        CompoundTag tag = CompoundTagArgument.getCompoundTag(ctx, "nbt");

                        for (WithPersistentData o : objects) {
                           o.kjs$getPersistentData().merge(tag);
                           ((CommandSourceStack)ctx.getSource())
                              .sendSuccess(
                                 () -> Component.literal("")
                                    .append(Component.literal("").withStyle(ChatFormatting.YELLOW).append(o.kjs$getDisplayName()))
                                    .append(" updated"),
                                 false
                              );
                        }

                        return objects.size();
                     }
                  )
            )
      );
      cmd.then(((LiteralArgumentBuilder)Commands.literal("remove").then(Commands.literal("*").executes(ctx -> {
         Collection<? extends WithPersistentData> objects = factory.getAll(ctx);

         for (WithPersistentData o : objects) {
            o.kjs$getPersistentData().getAllKeys().removeIf(UtilsJS.ALWAYS_TRUE);
         }

         return objects.size();
      }))).then(Commands.argument("key", StringArgumentType.string()).executes(ctx -> {
         Collection<? extends WithPersistentData> objects = factory.getAll(ctx);
         String key = StringArgumentType.getString(ctx, "key");

         for (WithPersistentData o : objects) {
            o.kjs$getPersistentData().remove(key);
         }

         return objects.size();
      })));
      cmd.then(
         ((LiteralArgumentBuilder)Commands.literal("scoreboard")
               .then(
                  Commands.literal("import")
                     .then(
                        Commands.argument("key", StringArgumentType.string())
                           .then(
                              Commands.argument("target", ScoreHolderArgument.scoreHolder())
                                 .suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS)
                                 .then(Commands.argument("objective", ObjectiveArgument.objective()).executes(ctx -> {
                                    ServerScoreboard scoreboard = ((CommandSourceStack)ctx.getSource()).getServer().getScoreboard();
                                    Collection<? extends WithPersistentData> objects = factory.getAll(ctx);
                                    String key = StringArgumentType.getString(ctx, "key");
                                    ScoreHolder target = ScoreHolderArgument.getName(ctx, "target");
                                    Objective objective = ObjectiveArgument.getObjective(ctx, "objective");
                                    ReadOnlyScoreInfo info = scoreboard.getPlayerScoreInfo(target, objective);
                                    int score = info != null ? info.value() : 0;

                                    for (WithPersistentData o : objects) {
                                       o.kjs$getPersistentData().putInt(key, score);
                                    }

                                    return objects.size();
                                 }))
                           )
                     )
               ))
            .then(
               Commands.literal("export")
                  .then(
                     Commands.argument("key", StringArgumentType.string())
                        .then(
                           Commands.argument("targets", ScoreHolderArgument.scoreHolders())
                              .suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS)
                              .then(Commands.argument("objective", ObjectiveArgument.objective()).executes(ctx -> {
                                 ServerScoreboard scoreboard = ((CommandSourceStack)ctx.getSource()).getServer().getScoreboard();
                                 WithPersistentData object = factory.getOne(ctx);
                                 String key = StringArgumentType.getString(ctx, "key");
                                 Collection<ScoreHolder> targets = ScoreHolderArgument.getNames(ctx, "targets");
                                 Objective objective = ObjectiveArgument.getObjective(ctx, "objective");
                                 int score = object.kjs$getPersistentData().getInt(key);

                                 for (ScoreHolder target : targets) {
                                    scoreboard.getOrCreatePlayerScore(target, objective).set(score);
                                 }

                                 return 1;
                              }))
                        )
                  )
            )
      );
      return cmd;
   }

   @FunctionalInterface
   public interface PersistentDataFactory {
      SimpleCommandExceptionType EMPTY_LIST = new SimpleCommandExceptionType(Component.literal("Expected at least one target"));

      Collection<? extends WithPersistentData> apply(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException;

      default Collection<? extends WithPersistentData> getAll(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
         Collection<? extends WithPersistentData> list = this.apply(ctx);
         if (list.isEmpty()) {
            throw EMPTY_LIST.create();
         } else {
            return list;
         }
      }

      default WithPersistentData getOne(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
         Collection<? extends WithPersistentData> list = this.apply(ctx);
         if (list.isEmpty()) {
            throw EMPTY_LIST.create();
         } else {
            return list.iterator().next();
         }
      }
   }
}
