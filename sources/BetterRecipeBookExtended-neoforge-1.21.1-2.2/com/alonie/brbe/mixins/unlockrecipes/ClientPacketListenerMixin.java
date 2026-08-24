package com.alonie.brbe.mixins.unlockrecipes;

import com.alonie.brbe.BetterRecipeBook;
import com.alonie.brbe.interfaces.unlockrecipes.IMixinRecipeManager;
import com.alonie.brbe.mixins.accessors.RecipeBookComponentAccessor;
import com.alonie.brbe.util.BookStateCache;
import com.alonie.brbe.util.IncompatibleCraftingUtil;
import com.alonie.brbe.util.PartialCraftingUtil;
import com.alonie.brbe.util.RecipeIndex;
import com.alonie.brbe.util.RecipeMenuUtil;
import com.alonie.brbe.util.RecipeUnlockUtil;
import com.alonie.recipebookispain_extended.RecipeBookIsPain;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundPlaceGhostRecipePacket;
import net.minecraft.network.protocol.game.ClientboundRecipePacket;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientPacketListener.class})
public abstract class ClientPacketListenerMixin {
   @Shadow
   @Final
   private RecipeManager recipeManager;
   @Unique
   private Minecraft _$minecraft = Minecraft.getInstance();

   @Inject(
      method = {"handleAddOrRemoveRecipes"},
      at = {@At("RETURN")}
   )
   public void onAddOrRemoveRecipes(ClientboundRecipePacket packet, CallbackInfo ci) {
      Set<ResourceLocation> serverUnlockedRecipes = ((IMixinRecipeManager)this.recipeManager).brbe$getServerUnlockedRecipes();
      boolean invalidateCaches = false;
      switch (packet.getState()) {
         case INIT:
            serverUnlockedRecipes.clear();
            PartialCraftingUtil.clearCaches();
            IncompatibleCraftingUtil.clearCaches();
            BookStateCache.clear();
            RecipeIndex.clear();
            invalidateCaches = true;
         case ADD:
            if (!packet.getRecipes().isEmpty()) {
               serverUnlockedRecipes.addAll(packet.getRecipes());
               if (!invalidateCaches) {
                  RecipeBookIsPain.recipeGeneration++;
               }
            }
            break;
         case REMOVE:
            if (!packet.getRecipes().isEmpty()) {
               packet.getRecipes().forEach(serverUnlockedRecipes::remove);
               RecipeBookIsPain.recipeGeneration++;
            }
      }

      RecipeUnlockUtil.unlockRecipesIfRequired();
   }

   @Inject(
      method = {"handleUpdateRecipes"},
      at = {@At("RETURN")}
   )
   public void onUpdateRecipes(ClientboundUpdateRecipesPacket packet, CallbackInfo ci) {
      PartialCraftingUtil.clearCaches();
      IncompatibleCraftingUtil.clearCaches();
      BookStateCache.clear();
      RecipeIndex.clear();
      RecipeBookIsPain.recipeGeneration++;
      RecipeUnlockUtil.unlockRecipesIfRequired();
   }

   @Inject(
      method = {"handleContainerSetSlot"},
      at = {@At("HEAD")}
   )
   public void onContainerSetSlot(ClientboundContainerSetSlotPacket packet, CallbackInfo ci) {
      if (BetterRecipeBook.ctx().config().newRecipes.unlockAll
         && this._$minecraft.player != null
         && this._$minecraft.player.containerMenu instanceof RecipeBookMenu<?, ?> menu
         && this._$minecraft.player.containerMenu.containerId == packet.getContainerId()
         && this._$minecraft.screen instanceof RecipeUpdateListener rul
         && !packet.getItem().isEmpty()
         && RecipeMenuUtil.isRecipeSlot(menu, packet.getSlot())) {
         ((RecipeBookComponentAccessor)rul.getRecipeBookComponent()).getGhostRecipe().clear();
      }
   }

   @Inject(
      method = {"handlePlaceRecipe"},
      at = {@At(
         value = "HEAD",
         target = "Lnet/minecraft/client/gui/screens/recipebook/RecipeBookComponent;setupGhostRecipe(Lnet/minecraft/world/item/crafting/Recipe;Ljava/util/List;)V"
      )}
   )
   public void onHandlePlaceRecipe_setupGhostRecipe(ClientboundPlaceGhostRecipePacket packet, CallbackInfo ci) {
      if (this._$minecraft.screen instanceof RecipeUpdateListener rul) {
         ((RecipeBookComponentAccessor)rul.getRecipeBookComponent()).getGhostRecipe().clear();
      }
   }
}
