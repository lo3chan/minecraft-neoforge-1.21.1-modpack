package io.github.razordevs.deep_aether.mixin.entity;

import io.github.razordevs.deep_aether.advancement.DAAdvancementTriggers;
import io.github.razordevs.deep_aether.advancement.PoisonTrigger;
import io.github.razordevs.deep_aether.entity.PoisonItem;
import io.github.razordevs.deep_aether.recipe.DARecipeTypes;
import io.github.razordevs.deep_aether.recipe.poison.PoisonRecipe;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ItemEntity.class})
public abstract class ItemEntityMixin extends Entity implements PoisonItem {
   @Unique
   private int poisonTime = 0;
   @Unique
   private boolean isConverting = false;
   @Unique
   private ItemStack resultItem = null;

   @Shadow
   public abstract ItemStack getItem();

   @Shadow
   @Nullable
   public abstract Entity getOwner();

   public ItemEntityMixin(EntityType<?> p_19870_, Level p_19871_) {
      super(p_19870_, p_19871_);
   }

   @Inject(
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/Entity;tick()V"
      )},
      method = {"tick"}
   )
   public void tick(CallbackInfo ci) {
      if (this.isConverting) {
         this.isConverting = false;
         if (this.poisonTime > 100) {
            if (this.getOwner() instanceof ServerPlayer player) {
               ((PoisonTrigger)DAAdvancementTriggers.POISON_TRIGGER.get()).trigger(player, this.getItem());
            }

            this.spawnAtLocation(this.resultItem, 0.0F);
            this.discard();
         }
      } else {
         this.poisonTime = 0;
      }
   }

   @Unique
   @Override
   public void deep_Aether$increaseTime() {
      if (this.resultItem == null) {
         for (RecipeHolder<PoisonRecipe> recipe : this.level().getRecipeManager().getAllRecipesFor((RecipeType)DARecipeTypes.POISON_RECIPE.get())) {
            if (((Ingredient)((PoisonRecipe)recipe.value()).getIngredients().getFirst()).getItems()[0].getItem() == this.getItem().getItem()) {
               this.resultItem = new ItemStack(((PoisonRecipe)recipe.value()).getResult().getItem(), this.getItem().getCount());
            }
         }

         if (this.resultItem == null) {
            this.resultItem = ItemStack.EMPTY;
         }
      } else if (!this.resultItem.isEmpty()) {
         this.poisonTime++;
         this.isConverting = true;
      }
   }

   @Unique
   @Override
   public boolean deep_Aether$canConvert() {
      return this.resultItem != null && !this.resultItem.isEmpty();
   }
}
