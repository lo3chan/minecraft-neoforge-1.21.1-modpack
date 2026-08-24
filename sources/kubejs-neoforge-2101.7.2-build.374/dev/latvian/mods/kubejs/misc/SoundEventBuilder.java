package dev.latvian.mods.kubejs.misc;

import dev.latvian.mods.kubejs.client.SoundsGenerator;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

@ReturnsSelf
public class SoundEventBuilder extends BuilderBase<SoundEvent> {
   public transient Consumer<SoundsGenerator.SoundGen> assetGen = gen -> gen.sound(this.id.toString()).subtitle(this.id.toLanguageKey("sound"));

   public SoundEventBuilder(ResourceLocation i) {
      super(i);
   }

   public SoundEventBuilder sounds(Consumer<SoundsGenerator.SoundGen> gen) {
      this.assetGen = gen;
      return this;
   }

   public SoundEvent createObject() {
      return SoundEvent.createVariableRangeEvent(this.id);
   }

   @Override
   public void generateAssets(KubeAssetGenerator generator) {
      generator.sounds(this.id.getNamespace(), g -> g.addSound(this.id.getPath(), this.assetGen));
   }
}
