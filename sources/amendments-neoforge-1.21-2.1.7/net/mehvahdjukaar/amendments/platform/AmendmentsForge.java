package net.mehvahdjukaar.amendments.platform;

import net.mehvahdjukaar.amendments.Amendments;
import net.mehvahdjukaar.amendments.configs.ClientConfigs;
import net.mehvahdjukaar.amendments.events.ModEvents;
import net.mehvahdjukaar.amendments.integration.CompatHandler;
import net.mehvahdjukaar.amendments.integration.platform.BlueprintIntegration;
import net.mehvahdjukaar.amendments.integration.platform.configured.ModConfigSelectScreen;
import net.mehvahdjukaar.amendments.reg.ModRegistry;
import net.mehvahdjukaar.moonlight.api.fluids.platform.SoftFluidTankFluidHandlerWrapper;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.capabilities.Capabilities.FluidHandler;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem;

@Mod("amendments")
public class AmendmentsForge {
   public AmendmentsForge(IEventBus bus) {
      Amendments.init();
      bus.addListener(AmendmentsForge::registerCapabilities);
      NeoForge.EVENT_BUS.register(this);
      if (PlatHelper.getPhysicalSide().isClient()) {
         NeoForge.EVENT_BUS.register(ClientEvents.class);
         if (CompatHandler.CONFIGURED && ClientConfigs.CUSTOM_CONFIGURED_SCREEN.get()) {
            ModConfigSelectScreen.registerConfigScreen("amendments", ModConfigSelectScreen::new);
         }
      }

      if (CompatHandler.BLUEPRINT) {
         BlueprintIntegration.init();
      }
   }

   private static void registerCapabilities(RegisterCapabilitiesEvent event) {
      event.registerBlockEntity(
         FluidHandler.BLOCK, ModRegistry.LIQUID_CAULDRON_TILE.get(), (myBlockEntity, side) -> SoftFluidTankFluidHandlerWrapper.wrap(myBlockEntity)
      );
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public void onUseBlock(RightClickBlock event) {
      if (!event.isCanceled()) {
         InteractionResult ret = ModEvents.onRightClickBlock(event.getEntity(), event.getLevel(), event.getHand(), event.getHitVec());
         if (ret != InteractionResult.PASS) {
            event.setCanceled(true);
            event.setCancellationResult(ret);
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public void onEntityHurt(AttackEntityEvent event) {
      if (!event.isCanceled()) {
         ModEvents.onAttackEntity(event.getEntity(), event.getEntity().level(), event.getEntity().getUsedItemHand(), event.getTarget(), null);
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public void onUseBlockHP(RightClickBlock event) {
      if (!event.isCanceled()) {
         InteractionResult ret = ModEvents.onRightClickBlockHP(event.getEntity(), event.getLevel(), event.getHand(), event.getHitVec());
         if (ret != InteractionResult.PASS) {
            event.setCanceled(true);
            event.setCancellationResult(ret);
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public void onUseItem(RightClickItem event) {
      if (!event.isCanceled()) {
         InteractionResultHolder<ItemStack> ret = ModEvents.onUseItem(event.getEntity(), event.getLevel(), event.getHand());
         if (ret.getResult() != InteractionResult.PASS) {
            event.setCanceled(true);
            event.setCancellationResult(ret.getResult());
         }
      }
   }
}
