package io.wispforest.owo.command.debug;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.LootCommand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public class MakeLootContainerCommand {
   public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
      dispatcher.register(
         (LiteralArgumentBuilder)Commands.literal("make-loot-container")
            .then(
               Commands.argument("item", ItemArgument.item(registryAccess))
                  .then(
                     Commands.argument("loot_table", ResourceLocationArgument.id())
                        .suggests(LootCommand.SUGGEST_LOOT_TABLE)
                        .executes(MakeLootContainerCommand::execute)
                  )
            )
      );
   }

   private static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      ItemStack targetStack = ItemArgument.getItem(context, "item").createItemStack(1, false);
      ResourceLocation tableId = ResourceLocationArgument.getId(context, "loot_table");
      CustomData blockEntityTag = (CustomData)targetStack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY);
      blockEntityTag = blockEntityTag.update(x -> x.putString("LootTable", tableId.toString()));
      targetStack.set(DataComponents.BLOCK_ENTITY_DATA, blockEntityTag);
      ((CommandSourceStack)context.getSource()).getPlayer().getInventory().placeItemBackInInventory(targetStack);
      return 0;
   }
}
