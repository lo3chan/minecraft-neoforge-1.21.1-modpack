package mezz.jei.library.plugins.vanilla.grindstone;

import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.List;
import mezz.jei.api.recipe.vanilla.IJeiGrindstoneRecipe;
import mezz.jei.common.util.MathUtil;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public final class GrindstoneRecipe implements IJeiGrindstoneRecipe {
   private final List<ItemStack> topInputs;
   private final List<ItemStack> bottomInputs;
   private final List<ItemStack> outputs;
   private int minXpReward;
   private final int maxXpReward;
   @Nullable
   private final ResourceLocation uid;

   public GrindstoneRecipe(
      List<ItemStack> topInputs, List<ItemStack> bottomInputs, List<ItemStack> outputs, int minXpReward, int maxXpReward, @Nullable ResourceLocation uid
   ) {
      this.topInputs = topInputs;
      this.bottomInputs = bottomInputs;
      this.outputs = outputs;
      this.minXpReward = minXpReward;
      this.maxXpReward = maxXpReward;
      this.uid = uid;
   }

   @Unmodifiable
   @Override
   public List<ItemStack> getTopInputs() {
      return this.topInputs;
   }

   @Unmodifiable
   @Override
   public List<ItemStack> getBottomInputs() {
      return this.bottomInputs;
   }

   @Unmodifiable
   @Override
   public List<ItemStack> getOutputs() {
      return this.outputs;
   }

   @Override
   public int getMinXpReward() {
      if (this.minXpReward < 0) {
         this.minXpReward = getMinXp((ItemStack)this.topInputs.getFirst(), (ItemStack)this.bottomInputs.getFirst());
      }

      return this.minXpReward;
   }

   @Override
   public int getMaxXpReward() {
      return this.maxXpReward < 0 ? this.getMinXpReward() * 2 : this.maxXpReward;
   }

   @Nullable
   @Override
   public ResourceLocation getUid() {
      return this.uid;
   }

   @Unmodifiable
   @Override
   public boolean isOutputRenderOnly() {
      return true;
   }

   private static int getMinXp(ItemStack topItem, ItemStack bottomItem) {
      int topXp = getExperienceFromItem(topItem);
      int bottomXp = getExperienceFromItem(bottomItem);
      return MathUtil.divideCeil(topXp + bottomXp, 2);
   }

   private static int getExperienceFromItem(ItemStack stack) {
      int i = 0;
      ItemEnchantments itemEnchantments = EnchantmentHelper.getEnchantmentsForCrafting(stack);

      for (Entry<Holder<Enchantment>> entry : itemEnchantments.entrySet()) {
         Holder<Enchantment> holder = (Holder<Enchantment>)entry.getKey();
         int j = entry.getIntValue();
         if (!holder.is(EnchantmentTags.CURSE)) {
            i += ((Enchantment)holder.value()).getMinCost(j);
         }
      }

      return i;
   }
}
