package at.petrak.hexcasting.common.recipe.ingredient;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.UnmodifiableIterator;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class StateIngredientBlocks implements StateIngredient {
   protected final ImmutableSet<Block> blocks;

   public StateIngredientBlocks(Collection<Block> blocks) {
      this.blocks = ImmutableSet.copyOf(blocks);
   }

   @Override
   public boolean test(BlockState state) {
      return this.blocks.contains(state.getBlock());
   }

   @Override
   public BlockState pick(Random random) {
      return ((Block)this.blocks.asList().get(random.nextInt(this.blocks.size()))).defaultBlockState();
   }

   @Override
   public JsonObject serialize() {
      JsonObject object = new JsonObject();
      object.addProperty("type", "blocks");
      JsonArray array = new JsonArray();
      UnmodifiableIterator var3 = this.blocks.iterator();

      while (var3.hasNext()) {
         Block block = (Block)var3.next();
         array.add(BuiltInRegistries.BLOCK.getKey(block).toString());
      }

      object.add("blocks", array);
      return object;
   }

   @Override
   public void write(FriendlyByteBuf buffer) {
      List<Block> blocks = this.getBlocks();
      buffer.writeVarInt(0);
      buffer.writeVarInt(blocks.size());

      for (Block block : blocks) {
         buffer.writeVarInt(BuiltInRegistries.BLOCK.getId(block));
      }
   }

   @Override
   public List<ItemStack> getDisplayedStacks() {
      return this.blocks.stream().filter(b -> b.asItem() != Items.AIR).<ItemStack>map(ItemStack::new).collect(Collectors.toList());
   }

   @Override
   public List<BlockState> getDisplayed() {
      return this.blocks.stream().<BlockState>map(Block::defaultBlockState).collect(Collectors.toList());
   }

   @Nonnull
   protected List<Block> getBlocks() {
      return this.blocks.asList();
   }

   @Override
   public String toString() {
      return "StateIngredientBlocks{" + this.blocks.toString() + "}";
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else {
         return o != null && this.getClass() == o.getClass() ? this.blocks.equals(((StateIngredientBlocks)o).blocks) : false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.blocks);
   }
}
