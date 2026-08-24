package at.petrak.paucal.xplat.common.command;

import at.petrak.paucal.xplat.PaucalGamerules;
import at.petrak.paucal.xplat.common.misc.PatPat;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Collection;
import java.util.UUID;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class CommandPatSound {
   public static void add(LiteralArgumentBuilder<CommandSourceStack> builder) {
      builder.then(((LiteralArgumentBuilder)Commands.literal("patsound").then(Commands.argument("pattee", GameProfileArgument.gameProfile()).executes(ctx -> {
         Collection<GameProfile> pattees = GameProfileArgument.getGameProfiles(ctx, "pattee");
         if (pattees.size() != 1) {
            ((CommandSourceStack)ctx.getSource()).sendFailure(Component.translatable("command.paucal.patSelf.bad_count", new Object[]{pattees.size()}));
            return 0;
         } else {
            return execute(pattees.iterator().next().getId(), ctx);
         }
      }))).executes(ctx -> execute(((CommandSourceStack)ctx.getSource()).getPlayerOrException().getUUID(), ctx)));
   }

   private static int execute(UUID target, CommandContext<CommandSourceStack> ctx) {
      boolean enabled = ((CommandSourceStack)ctx.getSource()).getLevel().getGameRules().getBoolean(PaucalGamerules.ALLOW_HEADPATS);
      if (!enabled) {
         ((CommandSourceStack)ctx.getSource()).sendFailure(Component.translatable("command.paucal.patSelf.disabled"));
         return 0;
      } else {
         ServerPlayer player = ((CommandSourceStack)ctx.getSource()).getPlayer();
         Vec3 pos = player != null ? player.getEyePosition() : ((CommandSourceStack)ctx.getSource()).getPosition();
         boolean ok = PatPat.tryPlayPatSound(target, pos, null, ((CommandSourceStack)ctx.getSource()).getLevel());
         if (ok) {
            ((CommandSourceStack)ctx.getSource())
               .sendSuccess(() -> Component.translatable("command.paucal.patSelf.ok", new Object[]{target.toString()}), false);
            return 1;
         } else {
            ((CommandSourceStack)ctx.getSource()).sendFailure(Component.translatable("command.paucal.patSelf.err", new Object[]{target.toString()}));
            return 0;
         }
      }
   }
}
