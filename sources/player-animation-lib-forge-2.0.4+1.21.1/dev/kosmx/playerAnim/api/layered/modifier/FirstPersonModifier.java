package dev.kosmx.playerAnim.api.layered.modifier;

import dev.kosmx.playerAnim.api.firstPerson.FirstPersonConfiguration;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import org.jetbrains.annotations.NotNull;

public class FirstPersonModifier extends AbstractModifier {
   private FirstPersonModifier.FirstPersonConfigEnum currentFirstPersonConfig = FirstPersonModifier.FirstPersonConfigEnum.ENABLE_BOTH_ARMS;
   private FirstPersonMode currentFirstPersonMode = FirstPersonMode.DISABLED;

   @NotNull
   @Override
   public FirstPersonConfiguration getFirstPersonConfiguration(float tickDelta) {
      return this.currentFirstPersonConfig.getFirstPersonConfiguration();
   }

   @NotNull
   @Override
   public FirstPersonMode getFirstPersonMode(float tickDelta) {
      return this.currentFirstPersonMode;
   }

   public void setCurrentFirstPersonConfig(FirstPersonModifier.FirstPersonConfigEnum currentFirstPersonConfig) {
      this.currentFirstPersonConfig = currentFirstPersonConfig;
   }

   public void setCurrentFirstPersonMode(FirstPersonMode currentFirstPersonMode) {
      this.currentFirstPersonMode = currentFirstPersonMode;
   }

   public static enum FirstPersonConfigEnum {
      ENABLE_BOTH_ARMS(new FirstPersonConfiguration(true, true, true, true)),
      DISABLE_BOTH_ARMS(new FirstPersonConfiguration(false, false, false, false)),
      ONLY_RIGHT_ARM_AND_ITEM(new FirstPersonConfiguration(true, false, true, false)),
      ONLY_LEFT_ARM_AND_ITEM(new FirstPersonConfiguration(false, true, false, true)),
      ONLY_RIGHT_ARM(new FirstPersonConfiguration(true, false, false, false)),
      ONLY_LEFT_ARM(new FirstPersonConfiguration(false, true, false, false)),
      ONLY_RIGHT_ITEM(new FirstPersonConfiguration(false, false, true, false)),
      ONLY_LEFT_ITEM(new FirstPersonConfiguration(false, false, false, true));

      private final FirstPersonConfiguration firstPersonConfiguration;

      private FirstPersonConfigEnum(@NotNull FirstPersonConfiguration firstPersonConfiguration) {
         this.firstPersonConfiguration = firstPersonConfiguration;
      }

      public FirstPersonConfiguration getFirstPersonConfiguration() {
         return this.firstPersonConfiguration;
      }
   }
}
