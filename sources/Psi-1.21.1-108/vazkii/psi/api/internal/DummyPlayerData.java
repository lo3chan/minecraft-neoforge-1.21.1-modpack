package vazkii.psi.api.internal;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import vazkii.psi.api.spell.SpellPiece;

public class DummyPlayerData implements IPlayerData {
   @Override
   public int getTotalPsi() {
      return 0;
   }

   @Override
   public int getAvailablePsi() {
      return 0;
   }

   @Override
   public int getLastAvailablePsi() {
      return 0;
   }

   @Override
   public int getRegenCooldown() {
      return 0;
   }

   @Override
   public int getRegenPerTick() {
      return 0;
   }

   @Override
   public boolean isOverflowed() {
      return false;
   }

   @Override
   public void deductPsi(int psi, int cd, boolean sync, boolean shatter) {
   }

   @Override
   public boolean isPieceGroupUnlocked(ResourceLocation group, @Nullable ResourceLocation piece) {
      return false;
   }

   @Override
   public void unlockPieceGroup(ResourceLocation group) {
   }

   @Override
   public void markPieceExecuted(SpellPiece piece) {
   }

   @Override
   public CompoundTag getCustomData() {
      return null;
   }

   @Override
   public void save() {
   }
}
