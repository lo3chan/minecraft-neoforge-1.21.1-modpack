package com.alonie.brbe.smithingtable;

import com.alonie.brbe.api.BRBBookCategories;
import com.alonie.brbe.generic.GenericGhostRecipe;
import com.alonie.brbe.mixins.accessors.HolderReferenceAccessor;
import com.alonie.brbe.recipe.BRBSmithingRecipe;
import com.alonie.brbe.recipe.smithing.BRBSmithingTransformRecipe;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.armortrim.TrimPatterns;
import org.jetbrains.annotations.Nullable;

public class SmithingGhostRecipe extends GenericGhostRecipe<BRBSmithingRecipe> {
   public SmithingGhostRecipe(@Nullable Consumer<ItemStack> onGhostUpdate, RegistryAccess registryAccess) {
      super(onGhostUpdate, registryAccess);
   }

   @Override
   public ItemStack getCurrentResult(BRBBookCategories.Category category) {
      if (this.recipe == null) {
         return ItemStack.EMPTY;
      } else if (this.recipe instanceof BRBSmithingTransformRecipe) {
         return this.recipe.getResult(this.registryAccess, category);
      } else {
         ItemStack itemStack = this.recipe.getBase().copy();
         Stream<Reference<TrimMaterial>> holders = this.registryAccess.registryOrThrow(Registries.TRIM_MATERIAL).holders();
         Optional<Reference<TrimMaterial>> currentMaterialReference = TrimMaterials.getFromIngredient(this.registryAccess, this.ingredients.get(0).getItem());
         if (currentMaterialReference.isEmpty()) {
            return itemStack;
         } else {
            Reference<TrimMaterial> material = holders.filter(
                  holder -> ((HolderReferenceAccessor)holder).getKey().equals(((HolderReferenceAccessor)currentMaterialReference.get()).getKey())
               )
               .findFirst()
               .get();
            Optional<Reference<TrimPattern>> trim = TrimPatterns.getFromTemplate(this.registryAccess, this.recipe.getTemplate().getItems()[0]);
            if (trim.isPresent()) {
               ArmorTrim armorTri = new ArmorTrim(material, (Holder)trim.get());
               itemStack.set(DataComponents.TRIM, armorTri);
            }

            return itemStack;
         }
      }
   }
}
