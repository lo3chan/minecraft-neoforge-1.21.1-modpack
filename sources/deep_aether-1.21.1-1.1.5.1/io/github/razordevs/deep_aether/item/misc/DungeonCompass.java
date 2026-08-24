package io.github.razordevs.deep_aether.item.misc;

import com.mojang.datafixers.util.Pair;
import io.github.razordevs.deep_aether.item.component.DADataComponentTypes;
import io.github.razordevs.deep_aether.item.component.DungeonTracker;
import io.github.razordevs.deep_aether.util.StructureUtil;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;

public class DungeonCompass extends Item {
   private final ResourceKey<Structure> dungeon;
   private final String dungeonName;

   public DungeonCompass(Properties pProperties, ResourceKey<Structure> dungeon, String dungeonName) {
      super(pProperties);
      this.dungeon = dungeon;
      this.dungeonName = dungeonName;
   }

   public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand hand) {
      ItemStack stack = playerIn.getItemInHand(hand);
      this.locateStructure(stack, playerIn);
      return super.use(worldIn, playerIn, hand);
   }

   private void locateStructure(ItemStack stack, Player player) {
      if (!player.level().isClientSide) {
         ServerLevel level = (ServerLevel)player.level();
         player.sendSystemMessage(Component.translatable("deep_aether.structure.locating", new Object[]{this.dungeon}).withStyle(ChatFormatting.YELLOW));
         Registry<Structure> registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
         HolderSet<Structure> featureHolderSet = (HolderSet<Structure>)registry.getHolder(this.dungeon)
            .map(xva$0 -> HolderSet.direct(new Holder[]{xva$0}))
            .orElse(null);
         if (featureHolderSet != null) {
            Pair<BlockPos, Holder<Structure>> pair = StructureUtil.findNearestMapStructure(level, featureHolderSet, player.blockPosition(), 100, true);
            this.bindPosition(stack, player, level, pair);
         }
      }
   }

   private void bindPosition(ItemStack stack, Player player, Level level, Pair<BlockPos, Holder<Structure>> pair) {
      BlockPos structurePos = pair != null ? (BlockPos)pair.getFirst() : null;
      if (structurePos == null) {
         stack.set(DADataComponentTypes.DUNGEON_TRACKER, new DungeonTracker(Optional.empty(), false));
         int range = 5000;
         player.sendSystemMessage(Component.translatable("deep_aether.structure.failed", new Object[]{this.dungeonName, range}).withStyle(ChatFormatting.RED));
      } else {
         stack.set(DADataComponentTypes.DUNGEON_TRACKER, new DungeonTracker(Optional.of(GlobalPos.of(level.dimension(), (BlockPos)pair.getFirst())), false));
         int distance = player.blockPosition().distManhattan(structurePos);
         player.sendSystemMessage(
            Component.translatable("deep_aether.structure.found", new Object[]{this.dungeonName, distance}).withStyle(ChatFormatting.GREEN)
         );
      }

      player.getCooldowns().addCooldown(this, 1000);
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
      Level level = context.level();
      if (stack.has(DADataComponentTypes.DUNGEON_TRACKER)) {
         DungeonTracker tracker = (DungeonTracker)stack.get(DADataComponentTypes.DUNGEON_TRACKER);
         if (tracker != null && tracker.found()) {
            if (level != null && tracker.target().isPresent() && tracker.target().get().dimension().equals(level.dimension())) {
               tooltip.add(Component.translatable("deep_aether.structure.found.tooltip", new Object[]{this.dungeonName}).withStyle(ChatFormatting.GREEN));
            } else {
               tooltip.add(
                  Component.translatable("deep_aether.structure.wrong_dimension.tooltip", new Object[]{this.dungeonName}).withStyle(ChatFormatting.RED)
               );
            }
         } else {
            tooltip.add(Component.translatable("deep_aether.structure.failed.tooltip", new Object[]{this.dungeonName}).withStyle(ChatFormatting.RED));
         }
      } else {
         tooltip.add(Component.translatable("deep_aether.structure.unset.tooltip").withStyle(ChatFormatting.GOLD));
      }
   }

   public Component getDescription() {
      return Component.translatable("deep_aether.item.disabled_item")
         .withStyle(Style.EMPTY.withItalic(true).withColor((TextColor)TextColor.parseColor("#d1362b").result().get()));
   }
}
