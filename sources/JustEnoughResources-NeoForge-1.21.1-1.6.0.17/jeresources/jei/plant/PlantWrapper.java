package jeresources.jei.plant;

import jeresources.compatibility.CompatBase;
import jeresources.entry.PlantEntry;
import jeresources.util.PlantHelper;
import jeresources.util.RenderHelper;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

public class PlantWrapper implements IRecipeCategoryExtension<PlantEntry> {
   private BlockState state;
   private Property<?> ageProperty;
   private long timer = -1L;
   private static final int TICKS = 500;

   public void drawInfo(PlantEntry recipe, int recipeWidth, int recipeHeight, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
      RenderHelper.renderBlock(guiGraphics, this.getFarmland(recipe), 30.0F, 30.0F, -10.0F, 20.0F, 20.0F);
      RenderHelper.renderBlock(guiGraphics, this.getBlockState(recipe), 30.0F, 12.0F, 10.0F, 20.0F, 20.0F);
   }

   private BlockState getBlockState(PlantEntry recipe) {
      if (this.state == null) {
         if (recipe.getPlantState() != null) {
            this.state = recipe.getPlantState();
         } else if (recipe.getPlant() != null) {
            this.state = PlantHelper.getPlant(recipe.getPlant(), CompatBase.getLevel(), BlockPos.ZERO);
         } else {
            this.state = Block.byItem(recipe.getPlantItemStack().getItem()).defaultBlockState();
         }

         if (recipe.getAgeProperty() != null) {
            this.ageProperty = recipe.getAgeProperty();
         } else {
            this.state
               .getProperties()
               .stream()
               .filter(p -> p.getName().equals("age"))
               .findAny()
               .ifPresent(property -> this.ageProperty = (Property<?>)property);
         }
      }

      if (this.ageProperty != null) {
         if (this.timer == -1L) {
            this.timer = System.currentTimeMillis() + 500L;
         } else if (System.currentTimeMillis() > this.timer) {
            this.state = (BlockState)this.state.cycle(this.ageProperty);
            this.timer = System.currentTimeMillis() + 500L;
         }
      }

      return this.state;
   }

   private BlockState getFarmland(PlantEntry recipe) {
      return recipe.getSoil() != null ? recipe.getSoil() : Blocks.FARMLAND.defaultBlockState();
   }
}
