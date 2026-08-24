package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.gui.KubeJSGUI;
import dev.latvian.mods.kubejs.gui.KubeJSMenu;
import dev.latvian.mods.kubejs.gui.chest.ChestMenuData;
import dev.latvian.mods.kubejs.gui.chest.CustomChestMenu;
import dev.latvian.mods.kubejs.level.LevelBlock;
import dev.latvian.mods.kubejs.net.KubeJSNet;
import dev.latvian.mods.kubejs.net.NotificationPayload;
import dev.latvian.mods.kubejs.net.SendDataFromServerPayload;
import dev.latvian.mods.kubejs.net.SetActivePostShaderPayload;
import dev.latvian.mods.kubejs.player.PlayerStatsJS;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.kubejs.typings.ThisIs;
import dev.latvian.mods.kubejs.util.NotificationToastData;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.UserBanListEntry;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import org.jetbrains.annotations.Nullable;

@RemapPrefixForJS("kjs$")
public interface ServerPlayerKJS extends PlayerKJS {
   @HideFromJS
   default ServerPlayer kjs$self() {
      return (ServerPlayer)this;
   }

   @ThisIs({ServerPlayer.class})
   @Info("Checks if the entity is a server-side player.")
   @Override
   default boolean kjs$isServerPlayer() {
      return true;
   }

   @Override
   default void kjs$sendData(String channel, @Nullable CompoundTag data) {
      if (!channel.isEmpty()) {
         KubeJSNet.safeSendToPlayer(this.kjs$self(), new SendDataFromServerPayload(channel, data));
      }
   }

   @Override
   default PlayerStatsJS kjs$getStats() {
      return new PlayerStatsJS(this.kjs$self(), this.kjs$self().getStats());
   }

   @Info("Checks, whether the player is currently mining a block.")
   @Override
   default boolean kjs$isMiningBlock() {
      return this.kjs$self().gameMode.isDestroyingBlock;
   }

   @Override
   default void kjs$setPositionAndRotation(double x, double y, double z, float yaw, float pitch) {
      PlayerKJS.super.kjs$setPositionAndRotation(x, y, z, yaw, pitch);
      this.kjs$self().connection.teleport(x, y, z, yaw, pitch);
   }

   @Info(
      value = "Switches the player's gamemode between Creative and Survival.\nTo change the player's gamemode to a mode other than Creative or Survival, use `setGameMode`.\n",
      params = {@Param(
         name = "mode",
         value = "`true` to change the player's gamemode to Creative.\n`false` to change the player's gamemode to Survival.\n"
      )}
   )
   default void kjs$setCreativeMode(boolean mode) {
      this.kjs$self().setGameMode(mode ? GameType.CREATIVE : GameType.SURVIVAL);
   }

   @Info("Checks, whether the player is a server operator.")
   default boolean kjs$isOp() {
      return this.kjs$self().server.getPlayerList().isOp(this.kjs$self().getGameProfile());
   }

   @Info(
      value = "Kicks the player from the server with the provided reason.",
      params = {@Param(
         name = "reason",
         value = "A text component, containing the kick reason. It may be a string, which will be implicitly wrapped into a text component."
      )}
   )
   default void kjs$kick(Component reason) {
      this.kjs$self().connection.disconnect(reason);
   }

   @Info("Kicks the player from the server with a generic reason.")
   default void kjs$kick() {
      this.kjs$kick(Component.translatable("multiplayer.disconnect.kicked"));
   }

   @Info(
      value = "Bans the player from the server.",
      params = {@Param(
         name = "banner",
         value = "A string, that specifies who/what banned the player."
      ), @Param(
         name = "reason",
         value = "A string, that contains the ban reason."
      ), @Param(
         name = "banDuration",
         value = "Duration of a ban. Negative durations will result in a 10-year ban."
      )}
   )
   default void kjs$ban(String banner, String reason, Duration banDuration) {
      long TEN_YEARS_SECONDS = 315569520L;
      Instant start = Instant.now();
      Instant end = start.plus(banDuration);
      UserBanListEntry userlistbansentry = new UserBanListEntry(
         this.kjs$self().getGameProfile(), Date.from(start), banner, Date.from(start.isBefore(end) ? end : start.plus(Duration.ofSeconds(315569520L))), reason
      );
      this.kjs$self().server.getPlayerList().getBans().add(userlistbansentry);
      this.kjs$kick(Component.translatable("multiplayer.disconnect.banned"));
   }

   default boolean kjs$isAdvancementDone(ResourceLocation id) {
      AdvancementNode a = this.kjs$self().server.kjs$getAdvancement(id);
      return a != null && this.kjs$self().getAdvancements().getOrStartProgress(a.holder()).isDone();
   }

   default void kjs$unlockAdvancement(ResourceLocation id) {
      AdvancementNode a = this.kjs$self().server.kjs$getAdvancement(id);
      if (a != null) {
         AdvancementProgress advancementprogress = this.kjs$self().getAdvancements().getOrStartProgress(a.holder());

         for (String s : advancementprogress.getRemainingCriteria()) {
            this.kjs$self().getAdvancements().award(a.holder(), s);
         }
      }
   }

   default void kjs$revokeAdvancement(ResourceLocation id) {
      AdvancementNode a = this.kjs$self().server.kjs$getAdvancement(id);
      if (a != null) {
         AdvancementProgress advancementprogress = this.kjs$self().getAdvancements().getOrStartProgress(a.holder());
         if (advancementprogress.hasProgress()) {
            for (String s : advancementprogress.getCompletedCriteria()) {
               this.kjs$self().getAdvancements().revoke(a.holder(), s);
            }
         }
      }
   }

   @Override
   default void kjs$setSelectedSlot(int index) {
      int p = this.kjs$getSelectedSlot();
      PlayerKJS.super.kjs$setSelectedSlot(index);
      int n = this.kjs$getSelectedSlot();
      if (p != n && this.kjs$self().connection != null) {
         this.kjs$self().connection.send(new ClientboundSetCarriedItemPacket(n));
      }
   }

   @Override
   default void kjs$setMouseItem(ItemStack item) {
      PlayerKJS.super.kjs$setMouseItem(item);
      if (this.kjs$self().connection != null) {
         this.kjs$self().inventoryMenu.broadcastChanges();
      }
   }

   @Nullable
   default LevelBlock kjs$getSpawnLocation() {
      BlockPos pos = this.kjs$self().getRespawnPosition();
      return pos == null ? null : this.kjs$getLevel().kjs$getBlock(pos);
   }

   default void kjs$setSpawnLocation(LevelBlock c) {
      this.kjs$self().setRespawnPosition(c.getDimensionKey(), c.getPos(), 0.0F, true, false);
   }

   @Override
   default void kjs$notify(NotificationToastData builder) {
      KubeJSNet.safeSendToPlayer(this.kjs$self(), new NotificationPayload(builder));
   }

   default void kjs$openChestGUI(Consumer<KubeJSGUI> gui) {
      final KubeJSGUI data = new KubeJSGUI();
      gui.accept(data);
      this.kjs$self().openMenu(new MenuProvider() {
         public Component getDisplayName() {
            return data.title;
         }

         public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
            return new KubeJSMenu(i, inventory, data);
         }
      }, data::write);
   }

   default void kjs$openInventoryGUI(InventoryKJS inventory, Component title) {
      this.kjs$openInventoryGUI(inventory, title, inventory.kjs$getWidth());
   }

   default void kjs$openInventoryGUI(InventoryKJS inventory, Component title, int columns) {
      this.kjs$openInventoryGUI(inventory, title, columns, inventory.kjs$getSlots() / columns);
   }

   default void kjs$openInventoryGUI(InventoryKJS inventory, Component title, int columns, int rows) {
      if (inventory.kjs$getSlots() < columns * rows) {
         throw new RuntimeException("Given container size is unable to contain inventory of size %sx%s!".formatted(columns, rows));
      } else {
         this.kjs$openChestGUI(gui -> {
            gui.title = title;
            gui.setInventory(inventory);
            gui.inventoryWidth = columns;
            gui.inventoryHeight = rows;
            gui.height = 114 + gui.inventoryHeight * 18;
            gui.width = 14 + gui.inventoryWidth * 18;
            gui.inventoryLabelY = gui.height - 94;
         });
      }
   }

   default Container kjs$captureInventory(boolean autoRestore) {
      NonNullList<ItemStack> playerItems = this.kjs$self().getInventory().items;
      SimpleContainer captured = new SimpleContainer(playerItems.size());
      HashMap<Integer, ItemStack> map = new HashMap<>();

      for (int i = 0; i < playerItems.size(); i++) {
         ItemStack c = (ItemStack)playerItems.set(i, ItemStack.EMPTY);
         if (!c.isEmpty()) {
            if (autoRestore) {
               map.put(i, c);
            }

            captured.setItem(i, c.copy());
         }
      }

      if (autoRestore && !map.isEmpty()) {
         this.kjs$self().getServer().kjs$restoreInventories().put(this.kjs$self().getUUID(), map);
      }

      return captured;
   }

   default void kjs$openChestGUI(Component title, int rows, Consumer<ChestMenuData> gui) {
      final ChestMenuData data = new ChestMenuData(this.kjs$self(), title, Mth.clamp(rows, 1, 6));
      gui.accept(data);
      if (this.kjs$self().containerMenu instanceof CustomChestMenu open) {
         data.capturedInventory = open.data.capturedInventory;
      } else {
         data.capturedInventory = this.kjs$captureInventory(true);
      }

      if (this.kjs$self().containerMenu instanceof CustomChestMenu open && open.data.rows == data.rows && open.data.title.equals(title)) {
         open.data = data;
         data.sync();
      } else {
         data.sync();
         this.kjs$self().openMenu(new MenuProvider() {
            public Component getDisplayName() {
               return title;
            }

            public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
               return new CustomChestMenu(i, data);
            }
         });
      }
   }

   @Info("Heals the player to full, and fully restores hunger and saturation.")
   default void kjs$heal() {
      this.kjs$self().heal(this.kjs$self().getMaxHealth());
      this.kjs$self().getFoodData().eat(20, 1.0F);
   }

   @Override
   default void kjs$setActivePostShader(@Nullable ResourceLocation id) {
      KubeJSNet.safeSendToPlayer(this.kjs$self(), new SetActivePostShaderPayload(Optional.ofNullable(id)));
   }
}
