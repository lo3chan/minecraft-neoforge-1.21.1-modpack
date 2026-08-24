package at.petrak.hexcasting.common.blocks.circles.impetuses;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.circles.BlockEntityAbstractImpetus;
import at.petrak.hexcasting.common.lib.HexBlockEntities;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BlockEntityRedstoneImpetus extends BlockEntityAbstractImpetus {
   public static final String TAG_STORED_PLAYER = "stored_player";
   public static final String TAG_STORED_PLAYER_PROFILE = "stored_player_profile";
   public static final String TAG_STORED_PLAYER_NAME = "stored_player_name";
   private GameProfile storedPlayerProfile = null;
   private UUID storedPlayer = null;
   private GameProfile cachedDisplayProfile = null;
   private ItemStack cachedDisplayStack = null;

   public BlockEntityRedstoneImpetus(BlockPos pWorldPosition, BlockState pBlockState) {
      super(HexBlockEntities.IMPETUS_REDSTONE_TILE, pWorldPosition, pBlockState);
   }

   @Nullable
   protected GameProfile getPlayerName() {
      Player player = this.getStoredPlayer();
      return player != null ? player.getGameProfile() : this.storedPlayerProfile;
   }

   public void setPlayer(GameProfile profile, UUID player) {
      this.storedPlayerProfile = profile;
      this.storedPlayer = player;
      this.setChanged();
   }

   public void clearPlayer() {
      this.storedPlayerProfile = null;
      this.storedPlayer = null;
   }

   public void updatePlayerProfile() {
      ServerPlayer player = this.getStoredPlayer();
      if (player != null) {
         GameProfile newProfile = player.getGameProfile();
         if (!newProfile.equals(this.storedPlayerProfile)) {
            this.storedPlayerProfile = newProfile;
            this.setChanged();
         }
      } else {
         this.storedPlayerProfile = null;
      }
   }

   @Nullable
   public ServerPlayer getStoredPlayer() {
      if (this.storedPlayer == null) {
         return null;
      } else if (this.level instanceof ServerLevel slevel) {
         Entity e = slevel.getEntity(this.storedPlayer);
         if (e instanceof ServerPlayer player) {
            return player;
         } else {
            HexAPI.LOGGER.error("Entity {} stored in a cleric impetus wasn't a player somehow", e);
            return null;
         }
      } else {
         HexAPI.LOGGER.error("Called getStoredPlayer on the client");
         return null;
      }
   }

   @Override
   public void applyScryingLensOverlay(List<Pair<ItemStack, Component>> lines, BlockState state, BlockPos pos, Player observer, Level world, Direction hitFace) {
      super.applyScryingLensOverlay(lines, state, pos, observer, world, hitFace);
      GameProfile name = this.getPlayerName();
      if (name != null) {
         if (!name.equals(this.cachedDisplayProfile) || this.cachedDisplayStack == null) {
            this.cachedDisplayProfile = name;
            ItemStack head = new ItemStack(Items.PLAYER_HEAD);
            this.cachedDisplayStack = head;
         }

         lines.add(new Pair(this.cachedDisplayStack, Component.translatable("hexcasting.tooltip.lens.impetus.redstone.bound", new Object[]{name.getName()})));
      } else {
         lines.add(new Pair(new ItemStack(Items.BARRIER), Component.translatable("hexcasting.tooltip.lens.impetus.redstone.bound.none")));
      }
   }

   @Override
   protected void saveModData(CompoundTag tag) {
      super.saveModData(tag);
      if (this.storedPlayer != null) {
         tag.putUUID("stored_player", this.storedPlayer);
      }

      if (this.storedPlayerProfile != null) {
         if (this.storedPlayerProfile.getId() != null) {
            tag.putUUID("stored_player_profile", this.storedPlayerProfile.getId());
         }

         if (this.storedPlayerProfile.getName() != null) {
            tag.putString("stored_player_name", this.storedPlayerProfile.getName());
         }
      }
   }

   @Override
   protected void loadModData(CompoundTag tag) {
      super.loadModData(tag);
      if (tag.contains("stored_player", 11)) {
         this.storedPlayer = tag.getUUID("stored_player");
      } else {
         this.storedPlayer = null;
      }

      if (!tag.hasUUID("stored_player_profile") && !tag.contains("stored_player_name", 8)) {
         this.storedPlayerProfile = null;
      } else {
         this.storedPlayerProfile = new GameProfile(
            tag.hasUUID("stored_player_profile") ? tag.getUUID("stored_player_profile") : null, tag.getString("stored_player_name")
         );
      }
   }
}
