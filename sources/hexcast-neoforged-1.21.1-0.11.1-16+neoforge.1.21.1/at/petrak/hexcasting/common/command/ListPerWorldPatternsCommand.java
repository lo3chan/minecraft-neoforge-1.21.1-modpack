package at.petrak.hexcasting.common.command;

import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.api.casting.iota.PatternIota;
import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.api.mod.HexTags;
import at.petrak.hexcasting.api.utils.HexUtils;
import at.petrak.hexcasting.common.casting.PatternRegistryManifest;
import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.server.ScrungledPatternsSave;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public class ListPerWorldPatternsCommand {
   public static void add(LiteralArgumentBuilder<CommandSourceStack> cmd) {
      cmd.then(
         ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("perWorldPatterns").requires(dp -> dp.hasPermission(2)))
                  .then(Commands.literal("list").executes(ctx -> list((CommandSourceStack)ctx.getSource()))))
               .then(
                  Commands.literal("give")
                     .then(
                        ((RequiredArgumentBuilder)Commands.argument("patternName", PatternResLocArgument.id())
                              .executes(
                                 ctx -> giveOne(
                                    (CommandSourceStack)ctx.getSource(),
                                    getDefaultTarget((CommandSourceStack)ctx.getSource()),
                                    ResourceLocationArgument.getId(ctx, "patternName"),
                                    PatternResLocArgument.getPattern(ctx, "patternName")
                                 )
                              ))
                           .then(
                              Commands.argument("targets", EntityArgument.players())
                                 .executes(
                                    ctx -> giveOne(
                                       (CommandSourceStack)ctx.getSource(),
                                       EntityArgument.getPlayers(ctx, "targets"),
                                       ResourceLocationArgument.getId(ctx, "patternName"),
                                       PatternResLocArgument.getPattern(ctx, "patternName")
                                    )
                                 )
                           )
                     )
               ))
            .then(
               ((LiteralArgumentBuilder)Commands.literal("giveAll")
                     .executes(ctx -> giveAll((CommandSourceStack)ctx.getSource(), getDefaultTarget((CommandSourceStack)ctx.getSource()))))
                  .then(
                     Commands.argument("targets", EntityArgument.players())
                        .executes(ctx -> giveAll((CommandSourceStack)ctx.getSource(), EntityArgument.getPlayers(ctx, "targets")))
                  )
            )
      );
   }

   private static Collection<ServerPlayer> getDefaultTarget(CommandSourceStack source) {
      return source.getEntity() instanceof ServerPlayer player ? List.of(player) : List.of();
   }

   private static int list(CommandSourceStack source) {
      Set<ResourceKey<ActionRegistryEntry>> keys = IXplatAbstractions.INSTANCE.getActionRegistry().registryKeySet();
      List<ResourceKey<ActionRegistryEntry>> listing = keys.stream().sorted((a, b) -> compareResLoc(a.location(), b.location())).toList();
      ServerLevel ow = source.getLevel().getServer().overworld();
      source.sendSuccess(() -> Component.translatable("command.hexcasting.pats.listing"), false);

      for (ResourceKey<ActionRegistryEntry> key : listing) {
         HexPattern pat = PatternRegistryManifest.getCanonicalStrokesPerWorld(key, ow);
         source.sendSuccess(() -> Component.literal(key.location().toString()).append(": ").append(new PatternIota(pat).display()), false);
      }

      return keys.size();
   }

   private static int giveAll(CommandSourceStack source, Collection<ServerPlayer> targets) {
      if (targets.isEmpty()) {
         return 0;
      } else {
         ServerLevel ow = source.getLevel().getServer().overworld();
         ScrungledPatternsSave save = ScrungledPatternsSave.open(ow);
         Registry<ActionRegistryEntry> regi = IXplatAbstractions.INSTANCE.getActionRegistry();
         int count = 0;

         for (Entry<ResourceKey<ActionRegistryEntry>, ActionRegistryEntry> entry : regi.entrySet()) {
            ResourceKey<ActionRegistryEntry> key = entry.getKey();
            if (HexUtils.isOfTag(regi, key, HexTags.Actions.PER_WORLD_PATTERN)) {
               Pair<String, ScrungledPatternsSave.PerWorldEntry> found = save.lookupReverse(key);
               String signature = (String)found.getFirst();
               HexDir startDir = ((ScrungledPatternsSave.PerWorldEntry)found.getSecond()).canonicalStartDir();
               HexPattern pat = HexPattern.fromAngles(signature, startDir);
               CompoundTag tag = new CompoundTag();
               tag.putString("op_id", key.location().toString());
               tag.put("pattern", pat.serializeToNBT());
               ItemStack stack = new ItemStack(HexItems.SCROLL_LARGE);
               stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));

               for (ServerPlayer player : targets) {
                  ItemEntity stackEntity = player.drop(stack, false);
                  if (stackEntity != null) {
                     stackEntity.setNoPickUpDelay();
                     stackEntity.setThrower(player);
                  }

                  count++;
               }
            }
         }

         int finalCount = count;
         source.sendSuccess(
            () -> Component.translatable(
               "command.hexcasting.pats.all", new Object[]{finalCount, targets.size() == 1 ? targets.iterator().next().getDisplayName() : targets.size()}
            ),
            true
         );
         return count;
      }
   }

   private static int giveOne(CommandSourceStack source, Collection<ServerPlayer> targets, ResourceLocation patternName, HexPattern pat) {
      if (!targets.isEmpty()) {
         CompoundTag tag = new CompoundTag();
         tag.putString("op_id", patternName.toString());
         tag.put("pattern", pat.serializeToNBT());
         ItemStack stack = new ItemStack(HexItems.SCROLL_LARGE);
         stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
         source.sendSuccess(
            () -> Component.translatable(
               "command.hexcasting.pats.specific.success",
               new Object[]{stack.getDisplayName(), patternName, targets.size() == 1 ? targets.iterator().next().getDisplayName() : targets.size()}
            ),
            true
         );

         for (ServerPlayer player : targets) {
            ItemEntity stackEntity = player.drop(stack, false);
            if (stackEntity != null) {
               stackEntity.setNoPickUpDelay();
               stackEntity.setThrower(player);
            }
         }

         return targets.size();
      } else {
         return 0;
      }
   }

   private static int compareResLoc(ResourceLocation a, ResourceLocation b) {
      int ns = a.getNamespace().compareTo(b.getNamespace());
      return ns != 0 ? ns : a.getPath().compareTo(b.getPath());
   }
}
