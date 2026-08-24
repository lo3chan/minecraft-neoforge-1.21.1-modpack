package fuzs.puzzleslib.api.item.v2;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fuzs.puzzleslib.impl.PuzzlesLib;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.GiveCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public final class GiveItemHelper {
   private GiveItemHelper() {
   }

   public static void giveItem(ItemStack itemStack, ServerPlayer serverPlayer) {
      giveItem(createEmptyCommandSource(serverPlayer.serverLevel()), itemStack, Collections.singleton(serverPlayer));
   }

   public static void giveItem(ItemStack itemStack, Collection<ServerPlayer> serverPlayers) {
      ServerPlayer serverPlayer = serverPlayers.iterator().next();
      giveItem(createEmptyCommandSource(serverPlayer.serverLevel()), itemStack, serverPlayers);
   }

   static void giveItem(CommandSourceStack commandSourceStack, ItemStack itemStack, Collection<ServerPlayer> serverPlayers) {
      try {
         ItemInput itemInput = new ItemInput(itemStack.getItemHolder(), itemStack.getComponentsPatch());
         GiveCommand.giveItem(commandSourceStack, itemInput, serverPlayers, itemStack.getCount());
      } catch (CommandSyntaxException var4) {
         PuzzlesLib.LOGGER.warn("Failed to give {} to players {}", new Object[]{itemStack, serverPlayers, var4});
      }
   }

   static CommandSourceStack createEmptyCommandSource(ServerLevel serverLevel) {
      return createEmptyCommandSource(serverLevel, 2);
   }

   static CommandSourceStack createEmptyCommandSource(ServerLevel serverLevel, int permissionLevel) {
      return createEmptyCommandSource(serverLevel, null, permissionLevel);
   }

   static CommandSourceStack createEmptyCommandSource(ServerLevel serverLevel, @Nullable Entity entity, int permissionLevel) {
      return new CommandSourceStack(
            CommandSource.NULL, Vec3.ZERO, Vec2.ZERO, serverLevel, permissionLevel, "Empty", Component.literal("Empty"), serverLevel.getServer(), entity
         )
         .withSuppressedOutput();
   }
}
