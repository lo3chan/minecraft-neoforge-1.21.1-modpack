package at.petrak.hexcasting.common.command;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

public class BrainsweepCommand {
   public static void add(LiteralArgumentBuilder<CommandSourceStack> cmd) {
      cmd.then(
         ((LiteralArgumentBuilder)Commands.literal("brainsweep").requires(dp -> dp.hasPermission(3)))
            .then(
               Commands.argument("target", EntityArgument.entity())
                  .executes(
                     ctx -> {
                        Entity target = EntityArgument.getEntity(ctx, "target");
                        if (target instanceof Mob mob) {
                           if (IXplatAbstractions.INSTANCE.isBrainswept(mob)) {
                              ((CommandSourceStack)ctx.getSource())
                                 .sendFailure(Component.translatable("command.hexcasting.brainsweep.fail.already", new Object[]{mob.getDisplayName()}));
                              return 0;
                           } else {
                              HexAPI.instance().brainsweep(mob);
                              ((CommandSourceStack)ctx.getSource())
                                 .sendSuccess(() -> Component.translatable("command.hexcasting.brainsweep", new Object[]{mob.getDisplayName()}), true);
                              return 1;
                           }
                        } else {
                           ((CommandSourceStack)ctx.getSource())
                              .sendFailure(Component.translatable("command.hexcasting.brainsweep.fail.badtype", new Object[]{target.getDisplayName()}));
                           return 0;
                        }
                     }
                  )
            )
      );
   }
}
