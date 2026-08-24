package fuzs.eternalnether.neoforge.data.client;

import fuzs.eternalnether.init.ModSoundEvents;
import fuzs.puzzleslib.neoforge.api.data.v2.client.AbstractSoundDefinitionProvider;
import fuzs.puzzleslib.neoforge.api.data.v2.core.NeoForgeDataProviderContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.common.data.SoundDefinition;

public class ModSoundProvider extends AbstractSoundDefinitionProvider {
   public ModSoundProvider(NeoForgeDataProviderContext context) {
      super(context);
   }

   public void addSoundDefinitions() {
      SoundDefinition soundDefinition = definition().with(sound(this.id("wither_waltz")).stream());
      this.add((SoundEvent)ModSoundEvents.WITHER_WALTZ.value(), soundDefinition);
      soundDefinition.subtitle(null);
      this.add(
         (SoundEvent)ModSoundEvents.WARPED_ENDERMAN_AMBIENT.value(),
         new ResourceLocation[]{
            this.id("entity/warped_enderman/idle1"),
            this.id("entity/warped_enderman/idle2"),
            this.id("entity/warped_enderman/idle3"),
            this.id("entity/warped_enderman/idle4"),
            this.id("entity/warped_enderman/idle5")
         }
      );
      this.add((SoundEvent)ModSoundEvents.WARPED_ENDERMAN_DEATH.value(), new ResourceLocation[]{this.id("entity/warped_enderman/death")});
      this.add(
         (SoundEvent)ModSoundEvents.WARPED_ENDERMAN_HURT.value(),
         new ResourceLocation[]{
            this.id("entity/warped_enderman/hit1"),
            this.id("entity/warped_enderman/hit2"),
            this.id("entity/warped_enderman/hit3"),
            this.id("entity/warped_enderman/hit4")
         }
      );
      this.add(
         (SoundEvent)ModSoundEvents.WARPED_ENDERMAN_SCREAM.value(),
         new ResourceLocation[]{
            this.id("entity/warped_enderman/scream1"),
            this.id("entity/warped_enderman/scream2"),
            this.id("entity/warped_enderman/scream3"),
            this.id("entity/warped_enderman/scream4")
         }
      );
      this.add((SoundEvent)ModSoundEvents.WARPED_ENDERMAN_STARE.value(), new ResourceLocation[]{this.id("entity/warped_enderman/stare")});
      this.add(
         (SoundEvent)ModSoundEvents.WARPED_ENDERMAN_TELEPORT.value(),
         new ResourceLocation[]{this.id("entity/warped_enderman/portal1"), this.id("entity/warped_enderman/portal2")}
      );
      this.add(
         (SoundEvent)ModSoundEvents.WEX_AMBIENT.value(),
         new ResourceLocation[]{this.id("entity/wex/idle1"), this.id("entity/wex/idle2"), this.id("entity/wex/idle3"), this.id("entity/wex/idle4")}
      );
      this.add(
         (SoundEvent)ModSoundEvents.WEX_CHARGE.value(),
         new ResourceLocation[]{this.id("entity/wex/charge1"), this.id("entity/wex/charge2"), this.id("entity/wex/charge3")}
      );
      this.add((SoundEvent)ModSoundEvents.WEX_DEATH.value(), new ResourceLocation[]{this.id("entity/wex/death1"), this.id("entity/wex/death2")});
      this.add((SoundEvent)ModSoundEvents.WEX_HURT.value(), new ResourceLocation[]{this.id("entity/wex/hurt1"), this.id("entity/wex/hurt2")});
   }
}
