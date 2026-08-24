package net.joefoxe.hexerei.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.joefoxe.hexerei.data.coffer.CofferInventorySavedData;
import net.joefoxe.hexerei.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.network.chat.HoverEvent.ItemStackInfo;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public class CofferListCommand {
   private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
   private static final int ENTRIES_PER_PAGE = 10;

   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("hexerei").requires(source -> source.hasPermission(2)))
            .then(
               ((LiteralArgumentBuilder)Commands.literal("coffers").executes(ctx -> execute(ctx, 1)))
                  .then(Commands.argument("page", IntegerArgumentType.integer(1)).executes(ctx -> execute(ctx, IntegerArgumentType.getInteger(ctx, "page"))))
            )
      );
   }

   private static int execute(CommandContext<CommandSourceStack> context, int page) {
      CommandSourceStack source = (CommandSourceStack)context.getSource();
      if (!(source.getEntity() instanceof ServerPlayer player)) {
         source.sendFailure(Component.literal("This command can only be used by players"));
         return 0;
      } else {
         CofferInventorySavedData data = CofferInventorySavedData.get(source.getServer().overworld());
         Map<UUID, ZonedDateTime> lastModifiedTimes = data.getLastModified();
         if (lastModifiedTimes.isEmpty()) {
            source.sendSuccess(() -> Component.literal("No coffer inventories found"), false);
            return 1;
         } else {
            List<Entry<UUID, ZonedDateTime>> sortedEntries = lastModifiedTimes.entrySet()
               .stream()
               .sorted(Entry.<UUID, ChronoZonedDateTime<?>>comparingByValue().reversed())
               .collect(Collectors.toList());
            int totalPages = (int)Math.ceil(sortedEntries.size() / 10.0);
            if (page > totalPages) {
               page = totalPages;
            }

            int startIndex = (page - 1) * 10;
            int endIndex = Math.min(startIndex + 10, sortedEntries.size());
            int finalPage = page;
            source.sendSuccess(() -> Component.literal(String.format("Coffer Inventories (Page %d/%d):", finalPage, totalPages)), false);

            for (int i = startIndex; i < endIndex; i++) {
               Entry<UUID, ZonedDateTime> entry = sortedEntries.get(i);
               UUID cofferId = entry.getKey();
               String formattedTime = entry.getValue().format(FORMATTER);
               ItemStack replacement = ((Item)ModItems.COFFER.get()).getDefaultInstance();
               CompoundTag tag = ((CustomData)replacement.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
               tag.putUUID("CofferId", cofferId);
               replacement.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
               Component message = Component.literal(String.format("%d. ", i + 1))
                  .append(
                     Component.literal("[Coffer] ")
                        .withStyle(
                           style -> style.withColor(ChatFormatting.BLUE)
                              .withUnderlined(true)
                              .withClickEvent(
                                 new ClickEvent(
                                    Action.RUN_COMMAND, "/give @s hexerei:coffer[minecraft:custom_data={CofferId:" + uuidToNbtString(cofferId) + "}]"
                                 )
                              )
                              .withHoverEvent(
                                 new HoverEvent(
                                    net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT,
                                    Component.literal("Click to get coffer \nLast modified: " + formattedTime)
                                 )
                              )
                        )
                        .append(
                           Component.literal(cofferId.toString())
                              .withStyle(
                                 style -> style.withColor(ChatFormatting.BLUE)
                                    .withUnderlined(true)
                                    .withClickEvent(
                                       new ClickEvent(
                                          Action.RUN_COMMAND, "/give @s hexerei:coffer[minecraft:custom_data={CofferId:" + uuidToNbtString(cofferId) + "}]"
                                       )
                                    )
                                    .withHoverEvent(new HoverEvent(net.minecraft.network.chat.HoverEvent.Action.SHOW_ITEM, new ItemStackInfo(replacement)))
                              )
                        )
                  );
               player.sendSystemMessage(message);
            }

            if (totalPages > 1) {
               MutableComponent footer = Component.literal("\nNavigation: ");
               if (page > 1) {
                  footer = footer.append(
                     Component.literal("[← Prev] ")
                        .withStyle(
                           style -> style.withColor(ChatFormatting.GREEN)
                              .withClickEvent(new ClickEvent(Action.RUN_COMMAND, "/hexerei coffers " + (finalPage - 1)))
                              .withHoverEvent(
                                 new HoverEvent(net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT, Component.literal("Go to page " + (finalPage - 1)))
                              )
                        )
                  );
               }

               footer = footer.append(Component.literal(String.format(" %d/%d ", page, totalPages)).withStyle(ChatFormatting.WHITE));
               if (page < totalPages) {
                  footer = footer.append(
                     Component.literal("[Next →]")
                        .withStyle(
                           style -> style.withColor(ChatFormatting.GREEN)
                              .withClickEvent(new ClickEvent(Action.RUN_COMMAND, "/hexerei coffers " + (finalPage + 1)))
                              .withHoverEvent(
                                 new HoverEvent(net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT, Component.literal("Go to page " + (finalPage + 1)))
                              )
                        )
                  );
               }

               player.sendSystemMessage(footer);
            }

            return 1;
         }
      }
   }

   public static String uuidToNbtString(UUID uuid) {
      return String.format(
         "[I;%d,%d,%d,%d]",
         (int)(uuid.getMostSignificantBits() >> 32),
         (int)uuid.getMostSignificantBits(),
         (int)(uuid.getLeastSignificantBits() >> 32),
         (int)uuid.getLeastSignificantBits()
      );
   }
}
