package at.petrak.hexcasting.common.recipe.ingredient;

import com.google.gson.JsonObject;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public interface StateIngredient extends Predicate<BlockState> {
   boolean test(BlockState var1);

   BlockState pick(Random var1);

   JsonObject serialize();

   void write(FriendlyByteBuf var1);

   List<ItemStack> getDisplayedStacks();

   default List<Component> descriptionTooltip() {
      return Collections.emptyList();
   }

   List<BlockState> getDisplayed();
}
