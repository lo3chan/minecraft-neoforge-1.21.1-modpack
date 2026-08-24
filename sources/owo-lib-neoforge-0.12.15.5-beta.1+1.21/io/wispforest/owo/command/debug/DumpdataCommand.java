package io.wispforest.owo.command.debug;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.wispforest.owo.Owo;
import io.wispforest.owo.ops.TextOps;
import java.util.regex.Pattern;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.commands.arguments.NbtPathArgument.NbtPath;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;

public class DumpdataCommand {
   private static final int GENERAL_PURPLE = 12157951;
   private static final int KEY_BLUE = 9745405;
   private static final int VALUE_BLUE = 9755391;

   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("dumpdata")
                  .then(
                     ((LiteralArgumentBuilder)Commands.literal("item").executes(withRootPath(DumpdataCommand::executeItem)))
                        .then(Commands.argument("nbt_path", NbtPathArgument.nbtPath()).executes(withPathArg(DumpdataCommand::executeItem)))
                  ))
               .then(
                  ((LiteralArgumentBuilder)Commands.literal("block").executes(withRootPath(DumpdataCommand::executeBlock)))
                     .then(Commands.argument("nbt_path", NbtPathArgument.nbtPath()).executes(withPathArg(DumpdataCommand::executeBlock)))
               ))
            .then(
               ((LiteralArgumentBuilder)Commands.literal("entity").executes(withRootPath(DumpdataCommand::executeEntity)))
                  .then(Commands.argument("nbt_path", NbtPathArgument.nbtPath()).executes(withPathArg(DumpdataCommand::executeEntity)))
            )
      );
   }

   private static Command<CommandSourceStack> withRootPath(DumpdataCommand.DataDumper dumper) {
      return context -> dumper.dump(context, NbtPathArgument.nbtPath().parse(new StringReader("")));
   }

   private static Command<CommandSourceStack> withPathArg(DumpdataCommand.DataDumper dumper) {
      return context -> {
         NbtPath path = NbtPathArgument.getPath(context, "nbt_path");
         return dumper.dump(context, path);
      };
   }

   private static int executeItem(CommandContext<CommandSourceStack> context, NbtPath path) throws CommandSyntaxException {
      CommandSourceStack source = (CommandSourceStack)context.getSource();
      ItemStack stack = source.getPlayer().getMainHandItem();
      informationHeader(source, "Item");
      sendIdentifier(source, stack.getItem(), BuiltInRegistries.ITEM);
      if (stack.get(DataComponents.MAX_DAMAGE) != null) {
         feedback(source, TextOps.withColor("Durability: §" + stack.get(DataComponents.MAX_DAMAGE), TextOps.color(ChatFormatting.GRAY), 9745405));
      } else {
         feedback(source, TextOps.withFormatting("Not damageable", ChatFormatting.GRAY));
      }

      if (!stack.getComponentsPatch().isEmpty()) {
         feedback(
            source,
            TextOps.withFormatting("Component changes" + formatPath(path) + ": ", ChatFormatting.GRAY)
               .append(
                  NbtUtils.toPrettyComponent(getPath((Tag)DataComponentPatch.CODEC.encodeStart(NbtOps.INSTANCE, stack.getComponentsPatch()).getOrThrow(), path))
               )
         );
      } else {
         feedback(source, TextOps.withFormatting("No component changes", ChatFormatting.GRAY));
      }

      feedback(source, TextOps.withFormatting("-----------------------", ChatFormatting.GRAY));
      return 0;
   }

   private static int executeEntity(CommandContext<CommandSourceStack> context, NbtPath path) throws CommandSyntaxException {
      CommandSourceStack source = (CommandSourceStack)context.getSource();
      ServerPlayer player = source.getPlayer();
      EntityHitResult target = ProjectileUtil.getEntityHitResult(
         player,
         player.getEyePosition(0.0F),
         player.getEyePosition(0.0F).add(player.getViewVector(0.0F).scale(5.0)),
         player.getBoundingBox().expandTowards(player.getViewVector(0.0F).scale(5.0)).inflate(1.0),
         entityx -> true,
         25.0
      );
      if (target != null && target.getType() == Type.ENTITY) {
         Entity entity = target.getEntity();
         informationHeader(source, "Entity");
         sendIdentifier(source, entity.getType(), BuiltInRegistries.ENTITY_TYPE);
         feedback(
            source,
            TextOps.withFormatting("NBT" + formatPath(path) + ": ", ChatFormatting.GRAY)
               .append(NbtUtils.toPrettyComponent(getPath(entity.saveWithoutId(new CompoundTag()), path)))
         );
         feedback(source, TextOps.withFormatting("-----------------------", ChatFormatting.GRAY));
         return 0;
      } else {
         source.sendFailure(TextOps.concat(Owo.PREFIX, Component.literal("You're not looking at an entity")));
         return 1;
      }
   }

   private static int executeBlock(CommandContext<CommandSourceStack> context, NbtPath path) throws CommandSyntaxException {
      CommandSourceStack source = (CommandSourceStack)context.getSource();
      ServerPlayer player = source.getPlayer();
      HitResult target = player.pick(5.0, 0.0F, false);
      if (target.getType() != Type.BLOCK) {
         source.sendFailure(TextOps.concat(Owo.PREFIX, Component.literal("You're not looking at a block")));
         return 1;
      } else {
         BlockPos pos = ((BlockHitResult)target).getBlockPos();
         BlockState blockState = player.level().getBlockState(pos);
         String blockStateString = blockState.toString();
         informationHeader(source, "Block");
         sendIdentifier(source, blockState.getBlock(), BuiltInRegistries.BLOCK);
         if (blockStateString.contains("[")) {
            feedback(source, TextOps.withFormatting("State properties: ", ChatFormatting.GRAY));
            String stateString = blockStateString.split(Pattern.quote("["))[1];
            stateString = stateString.substring(0, stateString.length() - 1);
            String[] stateInfo = stateString.replaceAll("=", ": §").split(",");

            for (String property : stateInfo) {
               feedback(source, TextOps.withColor("    " + property, 9745405, 9755391));
            }
         } else {
            feedback(source, TextOps.withFormatting("No state properties", ChatFormatting.GRAY));
         }

         BlockEntity blockEntity = player.level().getBlockEntity(pos);
         if (blockEntity != null) {
            feedback(
               source,
               TextOps.withFormatting("Block Entity NBT" + formatPath(path) + ": ", ChatFormatting.GRAY)
                  .append(NbtUtils.toPrettyComponent(getPath(blockEntity.saveWithoutMetadata(player.registryAccess()), path)))
            );
         } else {
            feedback(source, TextOps.withFormatting("No block entity", ChatFormatting.GRAY));
         }

         feedback(source, TextOps.withFormatting("-----------------------", ChatFormatting.GRAY));
         return 0;
      }
   }

   private static <T> void sendIdentifier(CommandSourceStack source, T object, Registry<T> registry) {
      String[] id = registry.getKey(object).toString().split(":");
      feedback(source, TextOps.withColor("Identifier: §" + id[0] + ":§" + id[1], TextOps.color(ChatFormatting.GRAY), 9745405, 9755391));
   }

   private static void informationHeader(CommandSourceStack source, String name) {
      feedback(
         source, TextOps.withColor("---[§ " + name + " Information §]---", TextOps.color(ChatFormatting.GRAY), 12157951, TextOps.color(ChatFormatting.GRAY))
      );
   }

   private static void feedback(CommandSourceStack source, Component message) {
      source.sendSuccess(() -> message, false);
   }

   private static String formatPath(NbtPath path) {
      return path.toString().isBlank() ? "" : "(" + path + ")";
   }

   private static Tag getPath(Tag nbt, NbtPath path) throws CommandSyntaxException {
      return (Tag)path.get(nbt).iterator().next();
   }

   @FunctionalInterface
   private interface DataDumper {
      int dump(CommandContext<CommandSourceStack> var1, NbtPath var2) throws CommandSyntaxException;
   }
}
