package dev.architectury.hooks.item.tool.forge;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.NeoForge;

public class HoeItemHooksImpl {
   public static void addTillable(Block input, Predicate<UseOnContext> predicate, Consumer<UseOnContext> action, Function<UseOnContext, BlockState> function) {
      NeoForge.EVENT_BUS
         .addListener(
            event -> {
               UseOnContext context = event.getContext();
               if (ItemAbilities.HOE_TILL == event.getItemAbility()
                  && context.getItemInHand().canPerformAction(ItemAbilities.HOE_TILL)
                  && event.getState().is(input)
                  && predicate.test(context)) {
                  if (!event.isSimulated()) {
                     action.accept(context);
                  }

                  event.setFinalState(function.apply(context));
               }
            }
         );
   }
}
