package com.aetherteam.aether.command;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.mixin.mixins.common.accessor.StoredUserListAccessor;
import com.mojang.authlib.GameProfile;
import java.io.File;
import net.minecraft.server.players.UserWhiteList;
import net.minecraft.server.players.UserWhiteListEntry;

public class SunAltarWhitelist {
   public static final File SUN_ALTAR_WHITELIST_FILE = new File(Aether.DIRECTORY.toString(), "sun_altar_whitelist.json");
   private final UserWhiteList sunAltarWhitelist = new UserWhiteList(SUN_ALTAR_WHITELIST_FILE);
   public static final SunAltarWhitelist INSTANCE = new SunAltarWhitelist();

   public SunAltarWhitelist() {
      this.load();
      this.save();
   }

   public UserWhiteList getSunAltarWhiteList() {
      return this.sunAltarWhitelist;
   }

   public String[] getSunAltarWhiteListNames() {
      return this.sunAltarWhitelist.getUserList();
   }

   public boolean isWhiteListed(GameProfile profile) {
      StoredUserListAccessor storedUserListAccessor = (StoredUserListAccessor)this.sunAltarWhitelist;
      return storedUserListAccessor.callContains(profile);
   }

   public void add(UserWhiteListEntry element) {
      this.getSunAltarWhiteList().add(element);
      this.save();
   }

   public void remove(UserWhiteListEntry element) {
      this.getSunAltarWhiteList().remove(element);
      this.save();
   }

   public void reload() {
      this.load();
   }

   private void load() {
      try {
         this.getSunAltarWhiteList().load();
      } catch (Exception var2) {
         Aether.LOGGER.warn("Failed to load Sun Altar whitelist: ", var2);
      }
   }

   private void save() {
      try {
         this.getSunAltarWhiteList().save();
      } catch (Exception var2) {
         Aether.LOGGER.warn("Failed to save Sun Altar whitelist: ", var2);
      }
   }
}
