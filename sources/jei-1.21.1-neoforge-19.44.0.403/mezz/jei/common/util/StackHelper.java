package mezz.jei.common.util;

import java.util.List;
import java.util.Objects;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IStackHelper;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.ingredients.subtypes.ISubtypeManager;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class StackHelper implements IStackHelper {
   private final ISubtypeManager subtypeManager;

   public StackHelper(ISubtypeManager subtypeManager) {
      this.subtypeManager = subtypeManager;
   }

   @Override
   public boolean isEquivalent(@Nullable ItemStack lhs, @Nullable ItemStack rhs, UidContext context) {
      ErrorUtil.checkNotNull(context, "context");
      if (lhs == rhs) {
         return true;
      } else if (lhs != null && rhs != null) {
         if (lhs.getItem() != rhs.getItem()) {
            return false;
         } else {
            Object keyLhs = this.subtypeManager.getSubtypeData(lhs, context);
            Object keyRhs = this.subtypeManager.getSubtypeData(rhs, context);
            return Objects.equals(keyLhs, keyRhs);
         }
      } else {
         return false;
      }
   }

   @Override
   public Object getUidForStack(ItemStack stack, UidContext context) {
      Item item = stack.getItem();
      Object subtypeData = this.subtypeManager.getSubtypeData(stack, context);
      return subtypeData != null ? List.of(item, (Item)subtypeData) : item;
   }

   @Override
   public Object getUidForStack(ITypedIngredient<ItemStack> typedIngredient, UidContext context) {
      Item item = typedIngredient.getBaseIngredient(VanillaTypes.ITEM_STACK);
      Object subtypeData = this.subtypeManager.getSubtypeData(VanillaTypes.ITEM_STACK, typedIngredient, context);
      return subtypeData != null ? List.of(item, (Item)subtypeData) : item;
   }

   @Override
   public String getUniqueIdentifierForStack(ItemStack stack, UidContext context) {
      String result = getRegistryNameForStack(stack);
      String subtypeInfo = this.subtypeManager.getSubtypeInfo(stack, context);
      if (!subtypeInfo.isEmpty()) {
         result = result + ":" + subtypeInfo;
      }

      return result;
   }

   public boolean hasSubtypes(ItemStack stack) {
      return this.subtypeManager.hasSubtypes(stack);
   }

   public static String getRegistryNameForStack(ItemStack stack) {
      ErrorUtil.checkNotNull(stack, "stack");
      Item item = stack.getItem();
      ResourceLocation key = RegistryUtil.getRegistry(Registries.ITEM).getKey(item);
      if (key == null) {
         String stackInfo = ErrorUtil.getItemStackInfo(stack);
         throw new IllegalStateException("Item has no registry key: " + stackInfo);
      } else {
         return key.toString();
      }
   }
}
