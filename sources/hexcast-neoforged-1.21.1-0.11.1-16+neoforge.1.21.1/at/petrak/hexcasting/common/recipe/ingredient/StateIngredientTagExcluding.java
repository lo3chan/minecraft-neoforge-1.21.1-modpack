package at.petrak.hexcasting.common.recipe.ingredient;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class StateIngredientTagExcluding extends StateIngredientTag {
   private final List<StateIngredient> excludes;

   public StateIngredientTagExcluding(ResourceLocation id, Collection<StateIngredient> excludes) {
      super(id);
      this.excludes = List.copyOf(excludes);
   }

   @Override
   public boolean test(BlockState state) {
      return !super.test(state) ? false : this.isNotExcluded(state);
   }

   @Override
   public BlockState pick(Random random) {
      List<Block> blocks = this.getBlocks();
      return blocks.isEmpty() ? null : blocks.get(random.nextInt(blocks.size())).defaultBlockState();
   }

   private boolean isNotExcluded(BlockState state) {
      for (StateIngredient exclude : this.excludes) {
         if (exclude.test(state)) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean equals(Object o) {
      return super.equals(o) && this.excludes.equals(((StateIngredientTagExcluding)o).excludes);
   }

   @Override
   public int hashCode() {
      return super.hashCode();
   }

   @Override
   public JsonObject serialize() {
      JsonObject object = new JsonObject();
      object.addProperty("type", "tag_excluding");
      object.addProperty("tag", this.getTagId().toString());
      JsonArray array = new JsonArray();

      for (StateIngredient exclude : this.excludes) {
         array.add(exclude.serialize());
      }

      object.add("exclude", array);
      return object;
   }

   @Override
   public List<ItemStack> getDisplayedStacks() {
      return this.getBlocks().stream().filter(b -> b.asItem() != Items.AIR).<ItemStack>map(ItemStack::new).toList();
   }

   @NotNull
   @Override
   protected List<Block> getBlocks() {
      return super.getBlocks().stream().filter(b -> this.isNotExcluded(b.defaultBlockState())).toList();
   }

   @Override
   public List<BlockState> getDisplayed() {
      return super.getDisplayed().stream().filter(this::isNotExcluded).toList();
   }
}
