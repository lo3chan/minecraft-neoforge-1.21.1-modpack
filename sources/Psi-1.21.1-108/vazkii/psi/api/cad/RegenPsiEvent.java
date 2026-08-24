package vazkii.psi.api.cad;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import vazkii.psi.api.internal.IPlayerData;

public class RegenPsiEvent extends Event implements ICancellableEvent {
   private final int playerPsiCapacity;
   private final int playerPsi;
   private final int cadPsiCapacity;
   private final int cadPsi;
   private final boolean wasOverflowed;
   private final Player player;
   private final IPlayerData playerData;
   private final ItemStack cad;
   private final int previousRegenCooldown;
   private final int baseRegenRate;
   private int regenRate;
   private int cadRegenCost = 0;
   private boolean regenCadFirst = true;
   private int maxPlayerRegen = -1;
   private int maxCadRegen = -1;
   private int playerRegen = 0;
   private int cadRegen = 0;
   private boolean healOverflow = false;
   private int regenCooldown;

   public RegenPsiEvent(Player player, IPlayerData playerData, ItemStack cad) {
      this.playerPsiCapacity = playerData.getTotalPsi();
      this.playerPsi = playerData.getAvailablePsi();
      this.regenRate = this.baseRegenRate = playerData.getRegenPerTick();
      this.wasOverflowed = playerData.isOverflowed();
      this.previousRegenCooldown = playerData.getRegenCooldown();
      this.regenCooldown = Math.max(0, this.previousRegenCooldown - 1);
      if (!cad.isEmpty()) {
         ICAD cadItem = (ICAD)cad.getItem();
         this.cadPsiCapacity = cadItem.getStatValue(cad, EnumCADStat.OVERFLOW);
         this.cadPsi = cadItem.getStoredPsi(cad);
      } else {
         this.cadPsiCapacity = this.cadPsi = 0;
      }

      this.player = player;
      this.playerData = playerData;
      this.cad = cad;
      this.applyRegen();
   }

   public Player getPlayer() {
      return this.player;
   }

   public IPlayerData getPlayerData() {
      return this.playerData;
   }

   public ItemStack getCad() {
      return this.cad;
   }

   public int getPlayerPsiCapacity() {
      return this.playerPsiCapacity;
   }

   public int getPlayerPsi() {
      return this.playerPsi;
   }

   public int getCadPsiCapacity() {
      return this.cadPsiCapacity;
   }

   public int getCadPsi() {
      return this.cadPsi;
   }

   public int getRegenRate() {
      return this.regenRate;
   }

   public int getBaseRegenRate() {
      return this.baseRegenRate;
   }

   public int getPreviousRegenCooldown() {
      return this.previousRegenCooldown;
   }

   public boolean wasOverflowed() {
      return this.wasOverflowed;
   }

   public int getPlayerRegen() {
      return this.playerRegen;
   }

   public int getCadRegen() {
      return this.cadRegen;
   }

   public int getCadRegenCost() {
      return this.cadRegenCost;
   }

   public boolean willHealOverflow() {
      return this.healOverflow;
   }

   public int getRegenCooldown() {
      return this.regenCooldown;
   }

   public void setRegenCooldown(int regenCooldown) {
      this.regenCooldown = regenCooldown;
      this.applyRegen();
   }

   public void addRegen(int amount) {
      this.regenRate += amount;
      this.applyRegen();
   }

   public void removeRegen(int amount) {
      this.regenRate -= amount;
      this.applyRegen();
   }

   public int getMaxPlayerRegen() {
      return this.maxPlayerRegen;
   }

   public void setMaxPlayerRegen(int maxPlayerRegen) {
      this.maxPlayerRegen = maxPlayerRegen;
      this.applyRegen();
   }

   public int getMaxCadRegen() {
      return this.maxCadRegen;
   }

   public void setMaxCadRegen(int maxCadRegen) {
      this.maxCadRegen = maxCadRegen;
      this.applyRegen();
   }

   public boolean willRegenCadFirst() {
      return this.regenCadFirst;
   }

   public void regenCadFirst(boolean regenCadFirst) {
      this.regenCadFirst = regenCadFirst;
      this.applyRegen();
   }

   private void applyRegen() {
      if (this.regenCooldown == 0) {
         this.cadRegenCost = 0;
         this.cadRegen = 0;
         this.playerRegen = 0;
         int regenLeft = this.regenRate;
         if (this.regenCadFirst) {
            regenLeft = this.applyCadRegen(regenLeft);
         }

         regenLeft = this.applyPlayerRegen(regenLeft);
         if (!this.regenCadFirst) {
            this.applyCadRegen(regenLeft);
         }
      }
   }

   private int applyPlayerRegen(int regenLeft) {
      int playerRegenTotal = Math.min(this.playerPsiCapacity - this.playerPsi, regenLeft);
      if (this.maxPlayerRegen >= 0) {
         playerRegenTotal = Math.min(this.maxPlayerRegen, playerRegenTotal);
      }

      if (regenLeft > 0 && playerRegenTotal > 0) {
         this.playerRegen = playerRegenTotal;
         regenLeft -= playerRegenTotal;
      } else {
         this.playerRegen = 0;
      }

      this.healOverflow = regenLeft > 0;
      if (this.healOverflow && this.wasOverflowed) {
         regenLeft--;
      }

      return regenLeft;
   }

   private int applyCadRegen(int regenLeft) {
      int cadRegenTotal = this.cadPsiCapacity - this.cadPsi;
      if (this.maxCadRegen >= 0) {
         cadRegenTotal = Math.min(this.maxCadRegen, cadRegenTotal);
      }

      this.cadRegenCost = Math.min(regenLeft, cadRegenTotal * 2);
      if (this.cadRegenCost > 0) {
         this.cadRegen = Math.min(Math.max(1, this.cadRegenCost / 2), cadRegenTotal);
         regenLeft -= this.cadRegenCost;
      } else {
         this.cadRegen = 0;
      }

      return regenLeft;
   }
}
