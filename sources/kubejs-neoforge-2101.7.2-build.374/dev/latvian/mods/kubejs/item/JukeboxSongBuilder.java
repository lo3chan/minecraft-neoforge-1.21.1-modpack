package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.JukeboxSong;

@ReturnsSelf
public class JukeboxSongBuilder extends BuilderBase<JukeboxSong> {
   public transient Holder<SoundEvent> sound = SoundEvents.MUSIC_DISC_11;
   public transient float lengthInSeconds = 71.0F;
   public transient Component description;
   public transient int comparatorOutput;

   public JukeboxSongBuilder(ResourceLocation id) {
      super(id);
      this.description = Component.translatable(Util.makeDescriptionId("jukebox_song", id));
      this.comparatorOutput = 0;
   }

   public JukeboxSong createObject() {
      return new JukeboxSong(this.sound, this.description, this.lengthInSeconds, this.comparatorOutput);
   }

   public JukeboxSongBuilder song(Holder<SoundEvent> sound, float length) {
      this.sound = sound;
      this.lengthInSeconds = length;
      return this;
   }

   public JukeboxSongBuilder description(Component description) {
      this.description = description;
      return this;
   }

   public JukeboxSongBuilder comparatorOutput(int comparatorOutput) {
      this.comparatorOutput = comparatorOutput;
      return this;
   }
}
