package net.astralya.hexalia.compat.patchouli;

import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import net.astralya.hexalia.recipe.CelestialInfusionRecipe;
import net.astralya.hexalia.recipe.MortarAndPestleRecipe;
import net.astralya.hexalia.recipe.MutationRecipe;
import net.astralya.hexalia.recipe.NaturesRitualRecipe;
import net.astralya.hexalia.recipe.SmallCauldronRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.IVariable;

public class RecipePageComponent implements ICustomComponent {
   private static final int PAGE_WIDTH = 118;
   private static final int ITEM_OFFSET_X = 1;
   private static final int ITEM_OFFSET_Y = 1;
   private static final ResourceLocation MUTATION_TEXTURE = ResourceLocation.fromNamespaceAndPath("hexalia", "textures/gui/mutation_gui.png");
   private static final ResourceLocation MORTAR_TEXTURE = ResourceLocation.fromNamespaceAndPath("hexalia", "textures/gui/mortar_gui.png");
   private static final ResourceLocation RITUAL_BRAZIER_TEXTURE = ResourceLocation.fromNamespaceAndPath("hexalia", "textures/gui/ritual_brazier_gui.png");
   private static final ResourceLocation RITUAL_TABLE_TEXTURE = ResourceLocation.fromNamespaceAndPath("hexalia", "textures/gui/ritual_table_gui.png");
   private static final ResourceLocation SMALL_CAULDRON_TEXTURE = ResourceLocation.fromNamespaceAndPath(
      "hexalia", "textures/gui/category/small_cauldron_gui.png"
   );
   public String recipe_id = "";
   public String layout = "";
   private transient int x;
   private transient int y;
   private transient RecipePageComponent.RecipeView recipe;

   public void build(int componentX, int componentY, int pageNum) {
      this.x = componentX;
      this.y = componentY;
   }

   public void render(GuiGraphics graphics, IComponentRenderContext context, float pticks, int mouseX, int mouseY) {
      if (this.recipe == null) {
         graphics.drawString(Minecraft.getInstance().font, "Missing recipe: " + this.recipe_id, this.x, this.y + 28, -12566464, false);
      } else {
         graphics.drawString(
            Minecraft.getInstance().font,
            this.recipe.output.getHoverName(),
            this.x + 59 - Minecraft.getInstance().font.width(this.recipe.output.getHoverName()) / 2,
            this.y,
            -12566464,
            false
         );
         this.renderRecipe(graphics, context, this.recipe, mouseX, mouseY);
      }
   }

   public void onVariablesAvailable(UnaryOperator<IVariable> lookup, Provider registries) {
      this.recipe_id = lookup.apply(IVariable.wrap(this.recipe_id, registries)).asString();
      this.layout = lookup.apply(IVariable.wrap(this.layout, registries)).asString();
      this.recipe = loadRecipe(this.recipe_id, this.layout).orElse(null);
   }

   private void renderRecipe(GuiGraphics graphics, IComponentRenderContext context, RecipePageComponent.RecipeView currentRecipe, int mouseX, int mouseY) {
      String var6 = this.layout;
      switch (var6) {
         case "small_cauldron":
            this.renderSmallCauldron(graphics, context, currentRecipe, mouseX, mouseY);
            break;
         case "ritual_table":
            this.renderRitualTable(graphics, context, currentRecipe, mouseX, mouseY);
            break;
         case "mortar_and_pestle":
            this.renderMortar(graphics, context, currentRecipe, mouseX, mouseY);
            break;
         case "mutation":
            this.renderMutation(graphics, context, currentRecipe, mouseX, mouseY);
            break;
         case "ritual_brazier":
            this.renderRitualBrazier(graphics, context, currentRecipe, mouseX, mouseY);
            break;
         default:
            graphics.drawString(Minecraft.getInstance().font, "Unsupported layout: " + this.layout, this.x, this.y + 28, -12566464, false);
      }
   }

   private void renderMutation(GuiGraphics graphics, IComponentRenderContext context, RecipePageComponent.RecipeView currentRecipe, int mouseX, int mouseY) {
      int left = this.centeredX(118);
      int top = this.y + 18;
      graphics.blit(MUTATION_TEXTURE, left, top, 0.0F, 0.0F, 118, 80, 256, 256);
      this.renderIngredient(graphics, context, currentRecipe, 0, left + 47, top + 31, mouseX, mouseY);
      context.renderItemStack(graphics, left + 88 + 1, top + 30 + 1, mouseX, mouseY, currentRecipe.output);
   }

   private void renderMortar(GuiGraphics graphics, IComponentRenderContext context, RecipePageComponent.RecipeView currentRecipe, int mouseX, int mouseY) {
      int left = this.centeredX(118);
      int top = this.y + 18;
      graphics.blit(MORTAR_TEXTURE, left, top, 0.0F, 0.0F, 118, 80, 256, 256);
      int[][] slots = new int[][]{{left + 3, top + 30}, {left + 27, top + 30}, {left + 51, top + 30}};

      for (int i = 0; i < slots.length; i++) {
         this.renderIngredient(graphics, context, currentRecipe, i, slots[i][0], slots[i][1], mouseX, mouseY);
      }

      context.renderItemStack(graphics, left + 88 + 1, top + 30 + 1, mouseX, mouseY, currentRecipe.output);
   }

   private void renderSmallCauldron(GuiGraphics graphics, IComponentRenderContext context, RecipePageComponent.RecipeView currentRecipe, int mouseX, int mouseY) {
      int left = this.centeredX(89);
      int top = this.y + 30;
      graphics.blit(SMALL_CAULDRON_TEXTURE, left, top, 14.0F, 19.0F, 89, 42, 256, 256);
      int[][] slots = new int[][]{{left, top}, {left + 24, top}, {left, top + 24}, {left + 24, top + 24}};

      for (int i = 0; i < slots.length; i++) {
         this.renderIngredient(graphics, context, currentRecipe, i, slots[i][0], slots[i][1], mouseX, mouseY);
      }

      context.renderItemStack(graphics, left + 69 + 1, top + 11 + 1, mouseX, mouseY, currentRecipe.output);
   }

   private void renderRitualBrazier(GuiGraphics graphics, IComponentRenderContext context, RecipePageComponent.RecipeView currentRecipe, int mouseX, int mouseY) {
      int left = this.centeredX(118);
      int top = this.y + 18;
      graphics.blit(RITUAL_BRAZIER_TEXTURE, left, top, 0.0F, 0.0F, 118, 80, 256, 256);
      this.renderIngredient(graphics, context, currentRecipe, 0, left + 27, top + 30, mouseX, mouseY);
      context.renderItemStack(graphics, left + 88 + 1, top + 30 + 1, mouseX, mouseY, currentRecipe.output);
   }

   private void renderRitualTable(GuiGraphics graphics, IComponentRenderContext context, RecipePageComponent.RecipeView currentRecipe, int mouseX, int mouseY) {
      int left = this.centeredX(118);
      int top = this.y + 18;
      graphics.blit(RITUAL_TABLE_TEXTURE, left, top, 0.0F, 0.0F, 118, 80, 256, 256);
      int[][] slots = new int[][]{{left + 27, top + 30}, {left + 3, top + 30}, {left + 51, top + 30}, {left + 27, top + 6}, {left + 27, top + 54}};

      for (int i = 0; i < slots.length; i++) {
         this.renderIngredient(graphics, context, currentRecipe, i, slots[i][0], slots[i][1], mouseX, mouseY);
      }

      context.renderItemStack(graphics, left + 88 + 1, top + 30 + 1, mouseX, mouseY, currentRecipe.output);
   }

   private void renderIngredient(
      GuiGraphics graphics,
      IComponentRenderContext context,
      RecipePageComponent.RecipeView currentRecipe,
      int index,
      int slotX,
      int slotY,
      int mouseX,
      int mouseY
   ) {
      if (index < currentRecipe.ingredients.size()) {
         context.renderIngredient(graphics, slotX + 1, slotY + 1, mouseX, mouseY, currentRecipe.ingredients.get(index));
      }
   }

   private int centeredX(int width) {
      return this.x + (118 - width) / 2;
   }

   private static Optional<RecipePageComponent.RecipeView> loadRecipe(String recipeId, String recipeLayout) {
      Minecraft client = Minecraft.getInstance();
      if (client.level == null) {
         return Optional.empty();
      } else {
         ResourceLocation id = ResourceLocation.tryParse(recipeId);
         if (id == null) {
            return Optional.empty();
         } else {
            RecipeManager manager = client.level.getRecipeManager();

            return switch (recipeLayout) {
               case "small_cauldron" -> manager.byKey(id).filter(holder -> holder.value() instanceof SmallCauldronRecipe).map(holder -> {
                  SmallCauldronRecipe recipe = (SmallCauldronRecipe)holder.value();
                  return new RecipePageComponent.RecipeView(List.copyOf(recipe.getIngredients()), recipe.getResultItem(client.level.registryAccess()).copy());
               });
               case "ritual_brazier" -> manager.byKey(id).filter(holder -> holder.value() instanceof CelestialInfusionRecipe).map(holder -> {
                  CelestialInfusionRecipe recipe = (CelestialInfusionRecipe)holder.value();
                  return new RecipePageComponent.RecipeView(List.copyOf(recipe.getIngredients()), recipe.getResultItem(client.level.registryAccess()).copy());
               });
               case "mutation" -> manager.byKey(id).filter(holder -> holder.value() instanceof MutationRecipe).map(holder -> {
                  MutationRecipe recipe = (MutationRecipe)holder.value();
                  return new RecipePageComponent.RecipeView(List.copyOf(recipe.getIngredients()), recipe.getResultItem(client.level.registryAccess()).copy());
               });
               case "mortar_and_pestle" -> manager.byKey(id).filter(holder -> holder.value() instanceof MortarAndPestleRecipe).map(holder -> {
                  MortarAndPestleRecipe recipe = (MortarAndPestleRecipe)holder.value();
                  return new RecipePageComponent.RecipeView(List.copyOf(recipe.getIngredients()), recipe.getResultItem(client.level.registryAccess()).copy());
               });
               case "ritual_table" -> manager.byKey(id).filter(holder -> holder.value() instanceof NaturesRitualRecipe).map(holder -> {
                  NaturesRitualRecipe recipe = (NaturesRitualRecipe)holder.value();
                  return new RecipePageComponent.RecipeView(List.copyOf(recipe.getIngredients()), recipe.getResultItem(client.level.registryAccess()).copy());
               });
               default -> Optional.empty();
            };
         }
      }
   }

   private record RecipeView(List<Ingredient> ingredients, ItemStack output) {
   }
}
