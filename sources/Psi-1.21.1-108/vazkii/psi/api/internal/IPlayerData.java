package vazkii.psi.api.internal;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import vazkii.psi.api.spell.SpellPiece;

public interface IPlayerData {
   int getTotalPsi();

   int getAvailablePsi();

   int getLastAvailablePsi();

   int getRegenCooldown();

   int getRegenPerTick();

   boolean isOverflowed();

   void deductPsi(int var1, int var2, boolean var3, boolean var4);

   default boolean isPieceGroupUnlocked(ResourceLocation group) {
      return this.isPieceGroupUnlocked(group, null);
   }

   boolean isPieceGroupUnlocked(ResourceLocation var1, @Nullable ResourceLocation var2);

   void unlockPieceGroup(ResourceLocation var1);

   void markPieceExecuted(SpellPiece var1);

   CompoundTag getCustomData();

   void save();
}
