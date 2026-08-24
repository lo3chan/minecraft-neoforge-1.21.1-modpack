package vectorwing.farmersdelight.common;

import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import vectorwing.farmersdelight.client.renderer.SkilletItemRenderer;
import vectorwing.farmersdelight.common.registry.ModItems;

public class EnumParameters {
   public static final EnumProxy<RecipeBookCategories> PROXY_COOKING_SEARCH = new EnumProxy(
      RecipeBookCategories.class, new Object[]{(Supplier<List<ItemStack>>)() -> List.of(new ItemStack(Items.COMPASS))}
   );
   public static final EnumProxy<RecipeBookCategories> PROXY_COOKING_MEALS = new EnumProxy(
      RecipeBookCategories.class, new Object[]{(Supplier<List<ItemStack>>)() -> List.of(new ItemStack((ItemLike)ModItems.VEGETABLE_NOODLES.get()))}
   );
   public static final EnumProxy<RecipeBookCategories> PROXY_COOKING_DRINKS = new EnumProxy(
      RecipeBookCategories.class, new Object[]{(Supplier<List<ItemStack>>)() -> List.of(new ItemStack((ItemLike)ModItems.APPLE_CIDER.get()))}
   );
   public static final EnumProxy<RecipeBookCategories> PROXY_COOKING_MISC = new EnumProxy(
      RecipeBookCategories.class,
      new Object[]{
         (Supplier<List<ItemStack>>)() -> List.of(new ItemStack((ItemLike)ModItems.DUMPLINGS.get()), new ItemStack((ItemLike)ModItems.TOMATO_SAUCE.get()))
      }
   );
   public static final EnumProxy<ArmPose> PROXY_SKILLET_FLIP = new EnumProxy(ArmPose.class, new Object[]{false, new SkilletItemRenderer.ArmPoseTransformer()});
}
