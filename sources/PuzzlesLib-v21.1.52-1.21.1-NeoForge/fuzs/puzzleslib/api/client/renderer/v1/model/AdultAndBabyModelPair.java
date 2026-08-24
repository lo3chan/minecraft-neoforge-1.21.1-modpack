package fuzs.puzzleslib.api.client.renderer.v1.model;

import net.minecraft.client.model.Model;

public record AdultAndBabyModelPair<T extends Model>(T adultModel, T babyModel) {
   public T getModel(boolean isBaby) {
      return isBaby ? this.babyModel : this.adultModel;
   }
}
