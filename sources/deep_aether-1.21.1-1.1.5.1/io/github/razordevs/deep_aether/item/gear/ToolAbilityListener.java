package io.github.razordevs.deep_aether.item.gear;

import io.github.razordevs.deep_aether.DeepAetherConfig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.neoforged.neoforge.event.level.BlockEvent.BlockToolModificationEvent;

@EventBusSubscriber(
   modid = "deep_aether"
)
public class ToolAbilityListener {
   @SubscribeEvent
   public static void setupToolModifications(BlockToolModificationEvent event) {
      BlockState oldState = event.getState();
      ItemAbility toolAction = event.getItemAbility();
      BlockState newState = DAAbilityHooks.ToolHooks.setupToolActions(oldState, toolAction);
      if (newState != oldState && !event.isSimulated()) {
         event.setFinalState(newState);
      }
   }

   @SubscribeEvent
   public static void modifyBreakSpeed(BreakSpeed event) {
      if (!(Boolean)DeepAetherConfig.SERVER.enable_skyjade_rework.get()) {
         Player player = event.getEntity();
         ItemStack itemStack = player.getMainHandItem();
         if (!event.isCanceled()) {
            event.setNewSpeed(DAAbilityHooks.ToolHooks.handleSkyjadeToolAbility(itemStack, event.getNewSpeed()));
         }
      }
   }
}
