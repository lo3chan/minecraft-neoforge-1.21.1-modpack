package net.mehvahdjukaar.moonlight.core.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.mehvahdjukaar.moonlight.api.map.MapDataRegistry;
import net.mehvahdjukaar.moonlight.api.map.MapHelper;
import net.mehvahdjukaar.moonlight.api.map.decoration.MLMapDecorationType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder.Reference;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class MapMarkerCommand {
   public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext context) {
      return ((LiteralArgumentBuilder)Commands.literal("add_map_marker").requires(cs -> cs.hasPermission(2)))
         .then(
            Commands.argument("marker", ResourceArgument.resource(context, MapDataRegistry.MAP_DECORATION_REGISTRY_KEY))
               .executes(MapMarkerCommand::addMapMarker)
         );
   }

   public static int addMapMarker(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      CommandSourceStack source = (CommandSourceStack)context.getSource();
      ServerLevel level = source.getLevel();
      Reference<MLMapDecorationType<?, ?>> decoration = ResourceArgument.getResource(context, "marker", MapDataRegistry.MAP_DECORATION_REGISTRY_KEY);
      ServerPlayer p = source.getPlayer();
      if (p != null) {
         ItemStack stack = p.getMainHandItem();
         MapItemSavedData data = MapHelper.getMapData(stack, level, p);
         if (data != null) {
            MapHelper.addCustomTargetDecorationToItem(stack, p.getOnPos(), decoration, 0);
            ((CommandSourceStack)context.getSource())
               .sendSuccess(() -> Component.translatable("commands.moonlight.added_map_marker", new Object[]{decoration.value()}), false);
         }
      }

      return 0;
   }
}
