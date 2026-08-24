package net.mcreator.borninchaosv.item;

import java.util.function.Consumer;
import net.mcreator.borninchaosv.item.renderer.BonescallerStaffItemRenderer;
import net.mcreator.borninchaosv.procedures.BonescallerStaffKazhdyiTikVInvientarieProcedure;
import net.mcreator.borninchaosv.procedures.StaffoftheSummonerPriPoluchieniiPriedmietaPoRietsieptuProcedure;
import net.mcreator.borninchaosv.procedures.StaffoftheSummonerPriShchielchkiePravoiKnopkoiMyshiNaBlokieProcedure;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.AnimationController.State;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BonescallerStaffItem extends Item implements GeoItem {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   public String animationprocedure = "empty";
   String prevAnim = "empty";

   public BonescallerStaffItem() {
      super(new Properties().durability(150).fireResistant().rarity(Rarity.COMMON));
   }

   public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
      return false;
   }

   public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
      consumer.accept(new GeoRenderProvider() {
         private BonescallerStaffItemRenderer renderer;

         public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
            if (this.renderer == null) {
               this.renderer = new BonescallerStaffItemRenderer();
            }

            return this.renderer;
         }
      });
   }

   private PlayState idlePredicate(AnimationState event) {
      if (this.animationprocedure.equals("empty")) {
         event.getController().setAnimation(RawAnimation.begin().thenLoop("idle"));
         return PlayState.CONTINUE;
      } else {
         return PlayState.STOP;
      }
   }

   private PlayState procedurePredicate(AnimationState event) {
      if (!this.animationprocedure.equals("empty") && event.getController().getAnimationState() == State.STOPPED
         || !this.animationprocedure.equals(this.prevAnim) && !this.animationprocedure.equals("empty")) {
         if (!this.animationprocedure.equals(this.prevAnim)) {
            event.getController().forceAnimationReset();
         }

         event.getController().setAnimation(RawAnimation.begin().thenPlay(this.animationprocedure));
         if (event.getController().getAnimationState() == State.STOPPED) {
            this.animationprocedure = "empty";
            event.getController().forceAnimationReset();
         }
      } else if (this.animationprocedure.equals("empty")) {
         this.prevAnim = "empty";
         return PlayState.STOP;
      }

      this.prevAnim = this.animationprocedure;
      return PlayState.CONTINUE;
   }

   public void registerControllers(ControllerRegistrar data) {
      AnimationController procedureController = new AnimationController(this, "procedureController", 0, this::procedurePredicate);
      data.add(procedureController);
      AnimationController idleController = new AnimationController(this, "idleController", 0, this::idlePredicate);
      data.add(idleController);
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public int getEnchantmentValue() {
      return 15;
   }

   public InteractionResult useOn(UseOnContext context) {
      super.useOn(context);
      StaffoftheSummonerPriShchielchkiePravoiKnopkoiMyshiNaBlokieProcedure.execute(
         context.getLevel(),
         context.getClickedPos().getX(),
         context.getClickedPos().getY(),
         context.getClickedPos().getZ(),
         context.getPlayer(),
         context.getItemInHand()
      );
      return InteractionResult.SUCCESS;
   }

   public void onCraftedBy(ItemStack itemstack, Level world, Player entity) {
      super.onCraftedBy(itemstack, world, entity);
      StaffoftheSummonerPriPoluchieniiPriedmietaPoRietsieptuProcedure.execute(entity);
   }

   public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
      super.inventoryTick(itemstack, world, entity, slot, selected);
      BonescallerStaffKazhdyiTikVInvientarieProcedure.execute(world, entity.getX(), entity.getY(), entity.getZ(), entity);
   }
}
