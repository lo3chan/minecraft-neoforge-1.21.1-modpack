package at.petrak.paucal.xplat.common.command;

import at.petrak.paucal.api.contrib.Contributor;
import at.petrak.paucal.xplat.common.ContributorsManifest;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Set;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

public class CommandGetContributorInfo {
   public static void add(LiteralArgumentBuilder<CommandSourceStack> builder) {
      builder.then(
         Commands.literal("getInfo")
            .then(
               ((RequiredArgumentBuilder)Commands.argument("target", EntityArgument.player())
                     .executes(ctx -> info(ctx, EntityArgument.getPlayer(ctx, "target"), false)))
                  .then(Commands.literal("getAll").executes(ctx -> info(ctx, EntityArgument.getPlayer(ctx, "target"), true)))
            )
      );
   }

   private static int info(CommandContext<CommandSourceStack> ctx, ServerPlayer target, boolean allKVs) {
      Contributor contrib = ContributorsManifest.getContributor(target.getUUID());
      if (contrib == null) {
         ((CommandSourceStack)ctx.getSource())
            .sendFailure(Component.translatable("command.paucal.contributor.not_contributor", new Object[]{target.getDisplayName()}));
         return 0;
      } else {
         Set<String> keySet = contrib.allKeys();
         MutableComponent out = Component.translatable(
            "command.paucal.contributor", new Object[]{target.getDisplayName(), contrib.getLevel(), contrib.isDev(), keySet.size()}
         );
         if (allKVs) {
            for (String key : keySet.stream().sorted().toList()) {
               out.append("\n- ");
               out.append(Component.literal(key).withStyle(ChatFormatting.GOLD));
               out.append(Component.literal(": "));
               out.append(Component.literal(String.valueOf(contrib.otherVals().get(key))).withStyle(ChatFormatting.LIGHT_PURPLE));
            }
         }

         ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> out, true);
         return keySet.size();
      }
   }
}
