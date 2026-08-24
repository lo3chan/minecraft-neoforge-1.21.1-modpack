package net.joefoxe.hexerei.container;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.custom.CrowFluteItem;
import net.joefoxe.hexerei.item.data_components.FluteData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

public class CrowFluteContainer extends AbstractContainerMenu {
   public final Player playerEntity;
   public final ItemStack stack;
   public InteractionHand hand;
   private final IItemHandler playerInventory;
   public List<Entity> crowList;

   public CrowFluteContainer(int windowId, Inventory playerInv) {
      this(windowId, playerInv, playerInv.player, InteractionHand.MAIN_HAND);
   }

   public CrowFluteContainer(int windowId, Inventory playerInv, RegistryFriendlyByteBuf byteBuf) {
      this(windowId, playerInv, playerInv.player, InteractionHand.MAIN_HAND);
   }

   public CrowFluteContainer(int windowId, Inventory playerInventory, Player player, InteractionHand hand) {
      super((MenuType)ModContainers.CROW_FLUTE_CONTAINER.get(), windowId);
      this.stack = playerInventory.player.getItemInHand(hand);
      this.playerEntity = player;
      this.playerInventory = new InvWrapper(playerInventory);
      this.hand = hand;
      this.crowList = Lists.newArrayList();
      FluteData fluteData = (FluteData)this.stack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY);
      if (player.level().isClientSide) {
         for (FluteData.CrowIds crowIds : fluteData.crowList()) {
            Entity entity = player.level().getEntity(crowIds.id());
            if (entity instanceof CrowEntity) {
               this.crowList.add(entity);
            }
         }
      }
   }

   public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
      return null;
   }

   public void clicked(int p_150400_, int p_150401_, ClickType p_150402_, Player p_150403_) {
      super.clicked(p_150400_, p_150401_, p_150402_, p_150403_);
   }

   public int getCommand() {
      return ((FluteData)this.playerEntity.getItemInHand(this.hand).getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY)).commandSelected();
   }

   public ItemStack setCommand(int value, ItemStack stack) {
      FluteData fluteData = (FluteData)stack.get(ModDataComponents.FLUTE);
      if (fluteData != null) {
         fluteData = new FluteData(value, fluteData.helpCommandSelected(), 0, fluteData.crowList(), fluteData.dyeColor1(), fluteData.dyeColor2());
         stack.set(ModDataComponents.FLUTE, fluteData);
         System.out.println(fluteData);
      }

      CrowFluteItem.setCommand(value, this.playerEntity.getItemInHand(this.hand), this.playerEntity, this.hand);
      return stack;
   }

   public int getHelpCommand() {
      return ((FluteData)this.playerEntity.getItemInHand(this.hand).getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY)).helpCommandSelected();
   }

   public ItemStack setHelpCommand(int value, ItemStack stack) {
      FluteData fluteData = (FluteData)stack.get(ModDataComponents.FLUTE);
      if (fluteData != null) {
         fluteData = new FluteData(fluteData.commandSelected(), value, 0, fluteData.crowList(), fluteData.dyeColor1(), fluteData.dyeColor2());
         stack.set(ModDataComponents.FLUTE, fluteData);
      }

      CrowFluteItem.setHelpCommand(value, this.playerEntity.getItemInHand(this.hand), this.playerEntity, this.hand);
      return stack;
   }

   public ItemStack setCommandMode(int value, ItemStack stack) {
      FluteData fluteData = (FluteData)stack.get(ModDataComponents.FLUTE);
      if (fluteData != null) {
         fluteData = new FluteData(
            fluteData.commandSelected(), fluteData.commandSelected(), value, fluteData.crowList(), fluteData.dyeColor1(), fluteData.dyeColor2()
         );
         stack.set(ModDataComponents.FLUTE, fluteData);
      }

      CrowFluteItem.setCommandMode(value, this.playerEntity.getItemInHand(this.hand), this.playerEntity, this.hand);
      return stack;
   }

   public int getCommandMode() {
      return ((FluteData)this.playerEntity.getItemInHand(this.hand).getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY)).commandMode();
   }

   public ItemStack clearCrowList(ItemStack stack) {
      FluteData fluteData = (FluteData)stack.get(ModDataComponents.FLUTE);
      if (fluteData != null) {
         fluteData = new FluteData(
            fluteData.commandSelected(), fluteData.commandSelected(), fluteData.commandMode(), new ArrayList<>(), fluteData.dyeColor1(), fluteData.dyeColor2()
         );
         stack.set(ModDataComponents.FLUTE, fluteData);
      }

      CrowFluteItem.clearCrowList(this.playerEntity.getItemInHand(this.hand), this.playerEntity, this.hand);
      this.crowList.clear();
      return stack;
   }

   public void clearCrowPerch() {
      CrowFluteItem.clearCrowPerch(this.playerEntity.getItemInHand(this.hand), this.playerEntity, this.hand);
   }

   public boolean stillValid(Player playerIn) {
      return playerIn.getItemInHand(this.hand) == this.stack;
   }
}
