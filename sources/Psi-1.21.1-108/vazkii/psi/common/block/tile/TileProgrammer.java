package vazkii.psi.common.block.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.common.block.BlockProgrammer;
import vazkii.psi.common.block.base.ModBlocks;
import vazkii.psi.common.spell.SpellCompiler;

public class TileProgrammer extends BlockEntity {
   private static final String TAG_SPELL = "spell";
   private static final String TAG_PLAYER_LOCK = "playerLock";
   public Spell spell;
   public boolean enabled;
   public String playerLock = "";

   public TileProgrammer(BlockPos pos, BlockState state) {
      super((BlockEntityType)ModBlocks.programmerType.get(), pos, state);
   }

   public boolean isEnabled() {
      return this.spell != null && !this.spell.grid.isEmpty();
   }

   public boolean canCompile() {
      return this.isEnabled() && new SpellCompiler().compile(this.spell).left().isPresent();
   }

   public void onSpellChanged() {
      boolean wasEnabled = this.enabled;
      this.enabled = this.isEnabled();
      if (this.getLevel() != null) {
         if (wasEnabled != this.enabled) {
            this.getLevel().setBlockAndUpdate(this.worldPosition, (BlockState)this.getBlockState().setValue(BlockProgrammer.ENABLED, this.enabled));
         }

         this.setChanged();
      }
   }

   public void loadAdditional(@NotNull CompoundTag cmp, @NotNull Provider pRegistries) {
      super.loadAdditional(cmp, pRegistries);
      this.readPacketNBT(cmp);
   }

   public void saveAdditional(@NotNull CompoundTag cmp, @NotNull Provider pRegistries) {
      super.saveAdditional(cmp, pRegistries);
      CompoundTag spellCmp = new CompoundTag();
      if (this.spell != null) {
         this.spell.writeToNBT(spellCmp);
      }

      cmp.put("spell", spellCmp);
      cmp.putString("playerLock", this.playerLock);
   }

   public void readPacketNBT(CompoundTag cmp) {
      CompoundTag spellCmp = cmp.getCompound("spell");
      if (this.spell == null) {
         this.spell = Spell.createFromNBT(spellCmp);
      } else {
         this.spell.readFromNBT(spellCmp);
      }

      this.playerLock = cmp.getString("playerLock");
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   @NotNull
   public CompoundTag getUpdateTag(@NotNull Provider pRegistries) {
      CompoundTag cmp = new CompoundTag();
      this.saveAdditional(cmp, pRegistries);
      return cmp;
   }

   public boolean canPlayerInteract(Player player) {
      return player.isAlive()
         && player.distanceToSqr(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5) <= 64.0;
   }

   public void onDataPacket(@NotNull Connection net, ClientboundBlockEntityDataPacket pkt, @NotNull Provider pRegistries) {
      this.readPacketNBT(pkt.getTag());
   }
}
