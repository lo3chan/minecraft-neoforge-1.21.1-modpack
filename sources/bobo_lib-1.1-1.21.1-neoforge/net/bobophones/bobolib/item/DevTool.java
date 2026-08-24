package net.bobophones.bobolib.item;

import java.util.ArrayList;
import java.util.List;
import net.bobophones.bobolib.util.BU;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class DevTool extends Item {
   public static final DevTool.Action explode = new DevTool.Action(Component.translatable("item.bobo_lib.dev_tool.explode"), (stack, level, player) -> {
      if (!level.isClientSide()) {
         Vec3 pos = BU.get_hit_pos(player, 128.0);
         level.explode(player, pos.x, pos.y, pos.z, 3.0F, false, ExplosionInteraction.NONE);
      }
   });
   private static final ArrayList<DevTool.Action> list = new ArrayList<>(List.of(explode));

   public DevTool() {
      super(new Properties().stacksTo(1));
   }

   public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
      return false;
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      int action = this.get_action(stack);
      if (player.isSecondaryUseActive()) {
         this.NextAction(stack, level);
         return InteractionResultHolder.consume(player.getItemInHand(hand));
      } else {
         list.get(action).action.apply(stack, level, player);
         return InteractionResultHolder.success(player.getItemInHand(hand));
      }
   }

   private void NextAction(ItemStack stack, Level level) {
      int action = this.get_action(stack) + 1;
      if (action >= list.size()) {
         action = 0;
      }

      CompoundTag tag = new CompoundTag();
      tag.putInt("Action", action);
      stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
      if (level.isClientSide()) {
         BU.ActionBarText(Component.translatable("item.bobo_lib.dev_tool.selected").append(": ").append(list.get(action).name));
      }
   }

   private int get_action(ItemStack stack) {
      CustomData data = (CustomData)stack.get(DataComponents.CUSTOM_DATA);
      if (data != null) {
         CompoundTag tag = data.copyTag();
         if (tag.contains("Action")) {
            return tag.getInt("Action");
         }
      }

      return 0;
   }

   public static void AddAction(DevTool.Action value) {
      list.add(value);
   }

   public record Action(Component name, DevTool.IAction<ItemStack, Level, Player> action) {
   }

   @FunctionalInterface
   public interface IAction<T, U, R> {
      void apply(T var1, U var2, R var3);
   }
}
