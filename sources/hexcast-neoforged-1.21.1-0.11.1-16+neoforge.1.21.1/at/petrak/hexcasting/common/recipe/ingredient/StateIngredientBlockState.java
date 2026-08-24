package at.petrak.hexcasting.common.recipe.ingredient;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class StateIngredientBlockState implements StateIngredient {
   private final BlockState state;

   public StateIngredientBlockState(BlockState state) {
      this.state = state;
   }

   @Override
   public boolean test(BlockState blockState) {
      return this.state == blockState;
   }

   @Override
   public BlockState pick(Random random) {
      return this.state;
   }

   @Override
   public JsonObject serialize() {
      JsonObject object = StateIngredientHelper.serializeBlockState(this.state);
      object.addProperty("type", "state");
      return object;
   }

   @Override
   public void write(FriendlyByteBuf buffer) {
      buffer.writeVarInt(2);
      buffer.writeVarInt(Block.getId(this.state));
   }

   @Override
   public List<ItemStack> getDisplayedStacks() {
      Block block = this.state.getBlock();
      return block.asItem() == Items.AIR ? Collections.emptyList() : Collections.singletonList(new ItemStack(block));
   }

   @Nullable
   @Override
   public List<Component> descriptionTooltip() {
      Map<Property<?>, Comparable<?>> map = this.state.getValues();
      if (map.isEmpty()) {
         return StateIngredient.super.descriptionTooltip();
      } else {
         List<Component> tooltip = new ArrayList<>(map.size());

         for (Entry<Property<?>, Comparable<?>> entry : map.entrySet()) {
            Property<?> key = entry.getKey();
            String name = key.getName(entry.getValue());
            tooltip.add(Component.literal(key.getName() + " = " + name).withStyle(ChatFormatting.GRAY));
         }

         return tooltip;
      }
   }

   @Override
   public List<BlockState> getDisplayed() {
      return Collections.singletonList(this.state);
   }

   public BlockState getState() {
      return this.state;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else {
         return o != null && this.getClass() == o.getClass() ? this.state == ((StateIngredientBlockState)o).state : false;
      }
   }

   @Override
   public int hashCode() {
      return this.state.hashCode();
   }

   @Override
   public String toString() {
      return "StateIngredientBlockState{" + this.state + "}";
   }
}
