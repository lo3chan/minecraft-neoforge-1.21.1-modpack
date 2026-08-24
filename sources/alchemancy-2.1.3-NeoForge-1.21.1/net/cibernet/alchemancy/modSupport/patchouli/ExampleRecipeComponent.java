package net.cibernet.alchemancy.modSupport.patchouli;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.function.UnaryOperator;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.IVariable;

public class ExampleRecipeComponent extends IngredientRingComponentBase {
   transient List<ItemStack> ingredients;
   transient ItemStack input = ItemStack.EMPTY;
   protected transient int x;
   protected transient int y;
   @SerializedName("example_type")
   public String exampleType;
   protected static final int RADIUS = 24;

   @Override
   public void build(int componentX, int componentY, int pageNum) {
      this.x = componentX;
      this.y = componentY;
      String var4 = this.exampleType;
      switch (var4) {
         case "tinted":
            this.ingredients = List.of(Items.RED_DYE.getDefaultInstance(), Items.BLUE_DYE.getDefaultInstance());
            break;
         case "name_tag":
            ItemStack stack = Items.NAME_TAG.getDefaultInstance();
            stack.set(DataComponents.CUSTOM_NAME, Component.literal("Cool Sword"));
            this.input = Items.IRON_SWORD.getDefaultInstance();
            this.ingredients = List.of(stack);
            break;
         case "name_tag_multiple":
            ItemStack tag1 = InfusedPropertiesHelper.addProperty(Items.NAME_TAG.getDefaultInstance(), AlchemancyProperties.HEAVY);
            CommonUtils.applyChromaTint(tag1, 11546150);
            tag1.set(DataComponents.CUSTOM_NAME, Component.literal("The "));
            ItemStack tag2 = InfusedPropertiesHelper.addProperty(Items.NAME_TAG.getDefaultInstance(), AlchemancyProperties.SLIPPERY);
            CommonUtils.applyChromaTint(tag2, 3847130);
            tag2.set(DataComponents.CUSTOM_NAME, Component.literal("Coolest "));
            ItemStack tag3 = InfusedPropertiesHelper.addProperty(Items.NAME_TAG.getDefaultInstance(), AlchemancyProperties.SLASHING);
            CommonUtils.applyChromaTint(tag3, 16701501);
            tag3.set(DataComponents.CUSTOM_NAME, Component.literal("Sword "));
            ItemStack tag4 = InfusedPropertiesHelper.addProperties(
               Items.NAME_TAG.getDefaultInstance(), List.of(AlchemancyProperties.MINING, AlchemancyProperties.HEAVY)
            );
            CommonUtils.applyChromaTint(tag4, 8439583);
            tag4.set(DataComponents.CUSTOM_NAME, Component.literal("Ever"));
            this.input = Items.IRON_SWORD.getDefaultInstance();
            this.ingredients = List.of(tag1, tag2, tag3, tag4);
            break;
         case "lore":
            this.input = Items.IRON_SWORD.getDefaultInstance();
            this.ingredients = List.of(Items.WRITTEN_BOOK.getDefaultInstance());
            break;
         default:
            this.ingredients = List.of();
      }
   }

   @Override
   public void render(GuiGraphics graphics, IComponentRenderContext context, float pticks, int mouseX, int mouseY) {
      int i = 0;
      float totalSize = this.ingredients.size();

      for (ItemStack infusable : this.ingredients) {
         context.renderItemStack(
            graphics,
            this.x + 24 + (int)(24.0F * Mth.sin(3.1415927F - 6.2831855F * (i / totalSize))),
            this.y + 24 + (int)(24.0F * Mth.cos(3.1415927F + 6.2831855F * (i / totalSize))),
            mouseX,
            mouseY,
            infusable
         );
         i++;
      }

      context.renderItemStack(graphics, this.x + 24, this.y + 24, mouseX, mouseY, this.input);
   }

   public void onVariablesAvailable(UnaryOperator<IVariable> lookup, Provider registries) {
      this.exampleType = lookup.apply(IVariable.wrap(this.exampleType, registries)).asString();
   }
}
