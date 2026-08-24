package io.wispforest.owo.command.debug;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.wispforest.owo.Owo;
import io.wispforest.owo.ops.TextOps;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.commands.arguments.NbtPathArgument.NbtPath;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;

public class CcaDataCommand {
   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("cca-data").executes(CcaDataCommand::executeDumpAll))
            .then(Commands.argument("path", NbtPathArgument.nbtPath()).executes(CcaDataCommand::executeDumpPath))
      );
   }

   private static int executeDumpAll(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)context.getSource()).getPlayer();
      CompoundTag nbt = player.saveWithoutId(new CompoundTag()).getCompound("cardinal_components");
      ((CommandSourceStack)context.getSource()).sendSuccess(() -> TextOps.concat(Owo.PREFIX, TextOps.withFormatting("CCA Data:", ChatFormatting.GRAY)), false);
      ((CommandSourceStack)context.getSource()).sendSuccess(() -> NbtUtils.toPrettyComponent(nbt), false);
      return 0;
   }

   private static int executeDumpPath(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)context.getSource()).getPlayer();
      NbtPath path = NbtPathArgument.getPath(context, "path");
      Tag nbt = (Tag)path.get(player.saveWithoutId(new CompoundTag()).getCompound("cardinal_components")).iterator().next();
      ((CommandSourceStack)context.getSource()).sendSuccess(() -> TextOps.concat(Owo.PREFIX, TextOps.withFormatting("CCA Data:", ChatFormatting.GRAY)), false);
      ((CommandSourceStack)context.getSource()).sendSuccess(() -> NbtUtils.toPrettyComponent(nbt), false);
      return 0;
   }
}
