package zank.mods.open_in_inventory.impl.handler;

import com.google.gson.JsonElement;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.JsonOps;
import dev.architectury.event.events.client.ClientCommandRegistrationEvent.ClientCommandSourceStack;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import zank.mods.open_in_inventory.OpenInInventory;
import zank.mods.open_in_inventory.OpenInInventoryConfig;
import zank.mods.open_in_inventory.api.OpenAction;
import zank.mods.open_in_inventory.impl.DefaultOpenAction;
import zank.mods.open_in_inventory.impl.WildCardOpenAction;
import zank.mods.open_in_inventory.util.CommandOptions;
import zank.mods.open_in_inventory.util.CommandUtil;

abstract class CommandAdd {
   public static final CommandOptions.CommandOption HOTBAR = new CommandOptions.CommandOption("hotbar");
   public static final CommandOptions.CommandOption WILDCARD = new CommandOptions.CommandOption("wildcard", "w");
   public static final CommandOptions.CommandOption SNEAK = new CommandOptions.CommandOption("sneak", "s");
   public static final CommandOptions.CommandOption SHOW = new CommandOptions.CommandOption("show");
   public static final CommandOptions OPTIONS = new CommandOptions(HOTBAR, WILDCARD, SNEAK, SHOW);

   static CompletableFuture<Suggestions> suggest(CommandContext<ClientCommandSourceStack> cx, SuggestionsBuilder builder) {
      String remaining = builder.getRemaining();
      int lastSpaceAt = remaining.lastIndexOf(32);
      if (lastSpaceAt >= 0) {
         builder = builder.createOffset(builder.getStart() + lastSpaceAt + 1);
      }

      for (CommandOptions.CommandOption option : OPTIONS.suggestNext(remaining)) {
         builder.suggest("--" + option.name(), Component.translatable("open_in_inventory.command.add.option." + option.name()));
         if (option.hasShorthand()) {
            builder.suggest("-" + option.shorthand(), Component.literal("Equivalent of: ").append("--" + option.name()));
         }
      }

      return builder.buildFuture();
   }

   public static int execute(CommandContext<ClientCommandSourceStack> cx) {
      Set<CommandOptions.CommandOption> options = OPTIONS.parse((String)cx.getArgument("args", String.class));
      return execute(cx, options);
   }

   public static int execute(CommandContext<ClientCommandSourceStack> cx, Set<CommandOptions.CommandOption> options) {
      LocalPlayer player = ((ClientCommandSourceStack)cx.getSource()).arch$getPlayer();
      List<ItemStack> stacks = options.contains(HOTBAR) ? player.getInventory().items.subList(0, 9) : List.of(player.getMainHandItem());
      Function<ItemStack, OpenAction> actionCtor;
      if (options.contains(WILDCARD)) {
         if (options.contains(SNEAK)) {
            actionCtor = stack -> new DefaultOpenAction(new ItemStack(stack.getItem()), true);
         } else {
            actionCtor = stack -> new WildCardOpenAction(stack.getItem());
         }
      } else {
         boolean shift = options.contains(SNEAK);
         actionCtor = stack -> new DefaultOpenAction(stack, shift);
      }

      List<JsonElement> actionJsons = stacks.stream().filter(stack -> !stack.isEmpty()).map(actionCtor).map(action -> {
         if (action instanceof DefaultOpenAction def) {
            return DefaultOpenAction.CODEC.encodeStart(JsonOps.INSTANCE, def);
         } else if (action instanceof WildCardOpenAction wild) {
            return WildCardOpenAction.CODEC.encodeStart(JsonOps.INSTANCE, wild);
         } else {
            throw new IllegalArgumentException("Unknown OpenAction instance: " + action);
         }
      }).flatMap(result -> result.resultOrPartial(OpenInInventory.LOGGER::error).stream()).toList();
      if (actionJsons.isEmpty()) {
         CommandUtil.sendSuccess(cx, () -> Component.literal("No items to add, skipping"));
         return 0;
      } else if (options.contains(SHOW)) {
         CommandUtil.sendSuccess(
            cx,
            () -> Component.translatable(
               "open_in_inventory.command.add.show",
               new Object[]{
                  Component.literal(String.valueOf(actionJsons.size())).withStyle(ChatFormatting.GRAY),
                  Component.literal(OpenInInventory.GSON.toJson(actionJsons))
                     .withStyle(CommandUtil.clickToCopy(OpenInInventory.GSON.toJson(actionJsons)))
                     .withStyle(ChatFormatting.GREEN)
               }
            )
         );
         return 1;
      } else {
         try {
            addToCfg(cx, actionJsons);
            CommandUtil.sendSuccess(
               cx,
               () -> Component.translatable(
                     "open_in_inventory.command.add", new Object[]{Component.literal(String.valueOf(actionJsons.size())).withStyle(ChatFormatting.GRAY)}
                  )
                  .withStyle(CommandUtil.hover(Component.literal(OpenInInventory.GSON.toJson(actionJsons))))
            );
            return 1;
         } catch (IOException var7) {
            ((ClientCommandSourceStack)cx.getSource()).arch$sendFailure(Component.literal("Failed to save config: " + var7));
            return 0;
         }
      }
   }

   private static void addToCfg(CommandContext<ClientCommandSourceStack> cx, List<JsonElement> actionJsons) throws IOException {
      OpenInInventoryConfig config = OpenInInventory.CONFIG;

      for (JsonElement actionJson : actionJsons) {
         config.enabledItems().add(actionJson);
      }

      config.write(OpenInInventory.CONFIG_PATH);
      OpenInInventory.refreshConfig();
   }
}
