package dev.latvian.mods.kubejs.command;

import dev.latvian.mods.kubejs.ingredient.NamespaceIngredient;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;

public class InformationCommands {
   private static Component copy(String s, ChatFormatting col, String info) {
      return copy(Component.literal(s).withStyle(col), Component.literal(info));
   }

   private static Component copy(String s, ChatFormatting col, Component info) {
      return copy(Component.literal(s).withStyle(col), info);
   }

   private static Component copy(Component c, Component info) {
      return Component.literal("- ")
         .withStyle(ChatFormatting.GRAY)
         .withStyle(Style.EMPTY.withClickEvent(new ClickEvent(Action.COPY_TO_CLIPBOARD, c.getString())))
         .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT, info.copy().append(" (Click to copy)"))))
         .append(c);
   }

   public static int hand(ServerPlayer player, InteractionHand hand) {
      player.sendSystemMessage(Component.literal("Item in hand:"));
      ItemStack stack = player.getItemInHand(hand);
      Holder<Item> holder = stack.getItemHolder();
      Registry<Item> itemRegistry = (Registry<Item>)player.server.registryAccess().registry(Registries.ITEM).orElseThrow();
      Registry<Block> blockRegistry = (Registry<Block>)player.server.registryAccess().registry(Registries.BLOCK).orElseThrow();
      Registry<Fluid> fluidRegistry = (Registry<Fluid>)player.server.registryAccess().registry(Registries.FLUID).orElseThrow();
      Registry<CreativeModeTab> tabRegistry = (Registry<CreativeModeTab>)player.server.registryAccess().registry(Registries.CREATIVE_MODE_TAB).orElseThrow();
      player.sendSystemMessage(
         copy(stack.kjs$toItemString0(player.server.registryAccess().createSerializationContext(NbtOps.INSTANCE)), ChatFormatting.GREEN, "Item ID")
      );

      for (TagKey<Item> tag : holder.tags().toList()) {
         String id = "'#%s'".formatted(tag.location());
         Integer size = itemRegistry.getTag(tag).<Integer>map(HolderSet::size).orElse(0);
         player.sendSystemMessage(copy(id, ChatFormatting.YELLOW, "Item Tag [" + size + " items]"));
      }

      player.sendSystemMessage(
         copy(
            "'@" + stack.kjs$getMod() + "'",
            ChatFormatting.AQUA,
            "Mod [" + new NamespaceIngredient(stack.kjs$getMod()).toVanilla().kjs$getStacks().size() + " items]"
         )
      );

      for (CreativeModeTab tab : tabRegistry) {
         if (tab.contains(stack)) {
            ResourceLocation id = tabRegistry.getKey(tab);
            int count = tab.getDisplayItems().size();
            int searchCount = tab.getSearchTabDisplayItems().size();
            player.sendSystemMessage(
               copy(
                  "'%" + id + "'",
                  ChatFormatting.LIGHT_PURPLE,
                  tab.getDisplayName().copy().append(" [%d/%d items in tab / search tab]".formatted(count, searchCount))
               )
            );
         }
      }

      if (stack.getItem() instanceof BlockItem blockItem) {
         player.sendSystemMessage(Component.literal("Held block:"));
         Block block = blockItem.getBlock();
         Reference<Block> blockHolder = block.builtInRegistryHolder();
         player.sendSystemMessage(copy("'" + block.kjs$getId() + "'", ChatFormatting.GREEN, "Block ID"));

         for (TagKey<Block> tag : blockHolder.tags().toList()) {
            String id = "'#%s'".formatted(tag.location());
            Integer size = blockRegistry.getTag(tag).<Integer>map(HolderSet::size).orElse(0);
            player.sendSystemMessage(copy(id, ChatFormatting.YELLOW, "Block Tag [" + size + " items]"));
         }
      }

      Optional<FluidStack> containedFluid = FluidUtil.getFluidContained(stack);
      if (containedFluid.isPresent()) {
         player.sendSystemMessage(Component.literal("Held fluid:"));
         FluidStack fluid = containedFluid.orElseThrow();
         Reference<Fluid> fluidHolder = fluid.getFluid().builtInRegistryHolder();
         player.sendSystemMessage(copy(fluidHolder.key().location().toString(), ChatFormatting.GREEN, "Fluid ID"));

         for (TagKey<Fluid> tag : fluidHolder.tags().toList()) {
            String id = "'#%s'".formatted(tag.location());
            Integer size = fluidRegistry.getTag(tag).<Integer>map(HolderSet::size).orElse(0);
            player.sendSystemMessage(copy(id, ChatFormatting.YELLOW, "Fluid Tag [" + size + " items]"));
         }
      }

      return 1;
   }

   public static int inventory(ServerPlayer player) {
      return dump(player.getInventory().items, player, "Inventory");
   }

   public static int hotbar(ServerPlayer player) {
      return dump(player.getInventory().items.subList(0, 9), player, "Hotbar");
   }

   public static int dump(List<ItemStack> stacks, ServerPlayer player, String name) {
      RegistryOps<Tag> ops = player.server.registryAccess().createSerializationContext(NbtOps.INSTANCE);
      List<String> dump = stacks.stream().filter(is -> !is.isEmpty()).map(is -> is.kjs$toItemString0(ops)).toList();
      player.sendSystemMessage(copy(dump.toString(), ChatFormatting.WHITE, name + " Item List"));
      return 1;
   }
}
