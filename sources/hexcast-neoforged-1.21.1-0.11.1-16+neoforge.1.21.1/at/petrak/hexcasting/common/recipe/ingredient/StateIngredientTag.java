package at.petrak.hexcasting.common.recipe.ingredient;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class StateIngredientTag extends StateIngredientBlocks {
   private final TagKey<Block> tag;

   public StateIngredientTag(ResourceLocation tag) {
      super(ImmutableSet.of());
      this.tag = TagKey.create(Registries.BLOCK, tag);
   }

   public Stream<Block> resolve() {
      return StreamSupport.stream(BuiltInRegistries.BLOCK.getTagOrEmpty(this.tag).spliterator(), false).map(Holder::value);
   }

   @Override
   public boolean test(BlockState state) {
      return state.is(this.tag);
   }

   @Override
   public BlockState pick(Random random) {
      List<Block> values = this.resolve().toList();
      return values.isEmpty() ? null : values.get(random.nextInt(values.size())).defaultBlockState();
   }

   @Override
   public JsonObject serialize() {
      JsonObject object = new JsonObject();
      object.addProperty("type", "tag");
      object.addProperty("tag", this.tag.location().toString());
      return object;
   }

   @Override
   public List<ItemStack> getDisplayedStacks() {
      return this.resolve().filter(b -> b.asItem() != Items.AIR).<ItemStack>map(ItemStack::new).collect(Collectors.toList());
   }

   @Nonnull
   @Override
   protected List<Block> getBlocks() {
      return this.resolve().toList();
   }

   @Override
   public List<BlockState> getDisplayed() {
      return this.resolve().<BlockState>map(Block::defaultBlockState).collect(Collectors.toList());
   }

   public ResourceLocation getTagId() {
      return this.tag.location();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else {
         return o != null && this.getClass() == o.getClass() ? this.tag.equals(((StateIngredientTag)o).tag) : false;
      }
   }

   @Override
   public int hashCode() {
      return this.tag.hashCode();
   }

   @Override
   public String toString() {
      return "StateIngredientTag{" + this.tag + "}";
   }
}
