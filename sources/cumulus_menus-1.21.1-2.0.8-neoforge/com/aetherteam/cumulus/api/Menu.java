package com.aetherteam.cumulus.api;

import com.aetherteam.cumulus.mixin.mixins.client.accessor.ScreenAccessor;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;

public record Menu(ResourceLocation icon, Component name, TitleScreen screen, Runnable apply, Music music, CubeMap panorama) {
   public Menu(ResourceLocation icon, Component name, TitleScreen screen) {
      this(icon, name, screen, new Menu.Properties());
   }

   public Menu(ResourceLocation icon, Component name, TitleScreen screen, Menu.Properties properties) {
      this(icon, name, screen, properties.apply, properties.music, properties.panorama);
   }

   public ResourceLocation getId() {
      return Menus.getKey(this);
   }

   @Override
   public String toString() {
      return this.getId().toString();
   }

   public static class Properties {
      private Runnable apply = () -> {};
      private Music music = Musics.MENU;
      private CubeMap panorama = ScreenAccessor.cumulus$getCubeMap();

      public Menu.Properties apply(Runnable apply) {
         this.apply = apply;
         return this;
      }

      public Menu.Properties music(Music music) {
         this.music = music;
         return this;
      }

      public Menu.Properties panorama(CubeMap panorama) {
         this.panorama = panorama;
         return this;
      }

      public static Menu.Properties propertiesFromType(Menu menu) {
         Menu.Properties props = new Menu.Properties();
         props.apply = menu.apply;
         props.music = menu.music;
         props.panorama = menu.panorama;
         return props;
      }
   }
}
