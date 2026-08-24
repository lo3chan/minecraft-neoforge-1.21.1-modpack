package com.iafenvoy.origins.registry;

import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.data._common.helper.RecipeHelper;
import com.iafenvoy.origins.data.badge.BuiltinBadges;
import com.iafenvoy.origins.data.badge.builtin.CraftingRecipeBadge;
import com.iafenvoy.origins.data.badge.builtin.KeybindBadge;
import com.iafenvoy.origins.data.badge.builtin.TooltipBadge;
import com.iafenvoy.origins.data.power.Toggleable;
import com.iafenvoy.origins.data.power.builtin.modify.ModifyCraftingPower;
import com.iafenvoy.origins.data.power.reference.PowerHolder;
import com.iafenvoy.origins.data.power.reference.PowerReference;
import com.iafenvoy.origins.screen.badge.BadgeTooltipManager;
import com.iafenvoy.origins.screen.tooltip.CraftingRecipeTooltipComponent;
import com.iafenvoy.origins.util.wrapper.Mutable;
import com.mojang.serialization.MapCodec;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;

@EventBusSubscriber({Dist.CLIENT})
public final class OriginsRenderers {
   @SubscribeEvent
   public static void registerEntityRenderers(RegisterRenderers event) {
      event.registerEntityRenderer((EntityType)OriginsEntities.ENDERIAN_PEARL.get(), ThrownItemRenderer::new);
   }

   @SubscribeEvent
   public static void registerRenderTypes(FMLClientSetupEvent event) {
      ItemBlockRenderTypes.setRenderLayer((Block)OriginsBlocks.TEMPORARY_COBWEB.get(), RenderType.cutout());
   }

   @SubscribeEvent
   public static void registerBadgeTooltips(FMLClientSetupEvent event) {
      BadgeTooltipManager.register(
         (MapCodec<CraftingRecipeBadge>)BuiltinBadges.CRAFTING_RECIPE.get(),
         (badge, power, font, widthLimit, delta) -> {
            Minecraft client = Minecraft.getInstance();
            Player player = client.player;
            List<ClientTooltipComponent> tooltips = new LinkedList<>();
            if (client.level != null && player != null) {
               RegistryAccess access = client.level.registryAccess();
               CraftingRecipe recipe = badge.fromPower()
                  ? PowerReference.listAllPowers(access)
                     .filter(x -> Objects.equals(x.id(), badge.recipe()))
                     .map(PowerHolder::power)
                     .filter(RecipeHelper.class::isInstance)
                     .findAny()
                     .map(RecipeHelper.class::cast)
                     .map(RecipeHelper::getRecipe)
                     .orElse(null)
                  : client.level
                     .getRecipeManager()
                     .byKey(badge.recipe())
                     .<Recipe>map(RecipeHolder::value)
                     .filter(CraftingRecipe.class::isInstance)
                     .map(CraftingRecipe.class::cast)
                     .orElse(null);
               if (recipe == null) {
                  return tooltips;
               } else {
                  int recipeWidth = recipe instanceof ShapedRecipe shapedRecipe ? shapedRecipe.getWidth() : 3;
                  SlotAccess outputStackReference = Mutable.stack(recipe.getResultItem(access)).toSlotAccess();
                  PowerHelper.get(player)
                     .getFirst(ModifyCraftingPower.class, p -> p.doesApply(player, badge.recipe(), outputStackReference.get()))
                     .ifPresent(p -> p.getNewResult(player, outputStackReference));
                  CraftingRecipeTooltipComponent recipeTooltip = new CraftingRecipeTooltipComponent(recipeWidth, peekInputs(recipe), outputStackReference.get());
                  Consumer<Component> addLines = component -> font.split(component, widthLimit).stream().map(ClientTextTooltip::new).forEach(tooltips::add);
                  badge.prefix().ifPresent(addLines);
                  tooltips.add(recipeTooltip);
                  badge.suffix().ifPresent(addLines);
                  if (client.options.advancedItemTooltips) {
                     addLines.accept(Component.literal(badge.recipe().toString()).withStyle(ChatFormatting.DARK_GRAY));
                  }

                  return tooltips;
               }
            } else {
               return tooltips;
            }
         }
      );
      BadgeTooltipManager.register(
         (MapCodec<KeybindBadge>)BuiltinBadges.KEYBIND.get(),
         (badge, power, font, widthLimit, delta) -> {
            KeyMapping key = (KeyMapping)KeyMapping.ALL.get(power instanceof Toggleable toggleable ? toggleable.getKey().key() : badge.key());
            return List.of(
               ClientTooltipComponent.create(
                  Component.translatable(badge.text(), new Object[]{Component.literal("[").append(key.getKey().getDisplayName()).append("]")})
                     .getVisualOrderText()
               )
            );
         }
      );
      BadgeTooltipManager.register(
         (MapCodec<TooltipBadge>)BuiltinBadges.TOOLTIP.get(),
         (badge, power, font, widthLimit, delta) -> List.of(ClientTooltipComponent.create(badge.text().getVisualOrderText()))
      );
   }

   private static NonNullList<ItemStack> peekInputs(CraftingRecipe recipe) {
      NonNullList<ItemStack> inputs = NonNullList.withSize(9, ItemStack.EMPTY);
      List<Ingredient> ingredients = recipe.getIngredients();
      int seed = Mth.floor(System.currentTimeMillis() / 1.5 * 1000.0);

      for (int index = 0; index < ingredients.size(); index++) {
         ItemStack[] stacks = ingredients.get(index).getItems();
         if (stacks.length > 0) {
            inputs.set(index, stacks[seed % stacks.length]);
         }
      }

      return inputs;
   }
}
