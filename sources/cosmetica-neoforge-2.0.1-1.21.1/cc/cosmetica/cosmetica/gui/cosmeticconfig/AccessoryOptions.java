package cc.cosmetica.cosmetica.gui.cosmeticconfig;

import cc.cosmetica.kupe.api.State;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.Button;
import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.api.gui.Div;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public final class AccessoryOptions extends CosmeticOptions {
   private final AccessoryOptions.Range x;
   private final AccessoryOptions.Range y;
   private final AccessoryOptions.Range z;
   private final AccessoryOptions.VisibilityOption hideWithHelmet;
   private final AccessoryOptions.VisibilityOption hideWithChestplate;
   private final AccessoryOptions.VisibilityOption hideWithLeggings;
   private final AccessoryOptions.VisibilityOption hideWithBoots;
   private final AccessoryOptions.VisibilityOption hideWithCloak;
   private final AccessoryOptions.VisibilityOption hideWithElytra;
   private final AccessoryOptions.VisibilityOption hideWithParrot;

   public AccessoryOptions(
      double[] x,
      double[] y,
      double[] z,
      boolean hideWithHelmet,
      boolean hideWithChestplate,
      boolean hideWithLeggings,
      boolean hideWithBoots,
      boolean hideWithCloak,
      boolean hideWithElytra,
      boolean hideWithParrot
   ) {
      this.x = new AccessoryOptions.Range(x);
      this.y = new AccessoryOptions.Range(y);
      this.z = new AccessoryOptions.Range(z);
      this.hideWithHelmet = new AccessoryOptions.VisibilityOption("button.configureCosmetic.hide_with_helmet", 1, hideWithHelmet);
      this.hideWithChestplate = new AccessoryOptions.VisibilityOption("button.configureCosmetic.hide_with_chestplate", 2, hideWithChestplate);
      this.hideWithLeggings = new AccessoryOptions.VisibilityOption("button.configureCosmetic.hide_with_leggings", 4, hideWithLeggings);
      this.hideWithBoots = new AccessoryOptions.VisibilityOption("button.configureCosmetic.hide_with_boots", 8, hideWithBoots);
      this.hideWithCloak = new AccessoryOptions.VisibilityOption("button.configureCosmetic.hide_with_cloak", 16, hideWithCloak);
      this.hideWithElytra = new AccessoryOptions.VisibilityOption("button.configureCosmetic.hide_with_elytra", 32, hideWithElytra);
      this.hideWithParrot = new AccessoryOptions.VisibilityOption("button.configureCosmetic.hide_with_parrot", 64, hideWithParrot);
   }

   public AccessoryOptions.Range getXRange() {
      return this.x;
   }

   public AccessoryOptions.Range getYRange() {
      return this.y;
   }

   public AccessoryOptions.Range getZRange() {
      return this.z;
   }

   public void forAllVisibilityOptions(Consumer<AccessoryOptions.VisibilityOption> consumer) {
      consumer.accept(this.hideWithHelmet);
      consumer.accept(this.hideWithChestplate);
      consumer.accept(this.hideWithLeggings);
      consumer.accept(this.hideWithBoots);
      consumer.accept(this.hideWithCloak);
      consumer.accept(this.hideWithElytra);
      consumer.accept(this.hideWithParrot);
   }

   public static final class Range {
      private final double[] span;

      Range(double[] d) {
         this.span = d;
      }

      public double getRange() {
         return this.span[1] - this.span[0];
      }

      public double map(double d) {
         return this.span[0] + d * (this.span[1] - this.span[0]);
      }

      public double clamp(double d) {
         return d < this.span[0] ? this.span[0] : (d > this.span[1] ? this.span[1] : d);
      }

      public double clampMap(double d) {
         return this.clamp(this.map(d));
      }
   }

   public static final class VisibilityOption {
      private final String key;
      private final int mask;
      private final boolean defaultValue;
      private final State<Boolean> userValue;

      public VisibilityOption(String key, int mask, boolean defaultValue) {
         this.key = key;
         this.mask = mask;
         this.defaultValue = defaultValue;
         this.userValue = new State(defaultValue);
      }

      public String getTranslationKey() {
         return this.key;
      }

      public void configureUserValue(AtomicInteger flags) {
         if ((Boolean)this.userValue.peek()) {
            flags.set(flags.get() | this.mask);
         } else {
            flags.set(flags.get() & ~this.mask);
         }
      }

      public boolean getDefaultValue() {
         return this.defaultValue;
      }

      public boolean getUserValue() {
         return (Boolean)this.userValue.peek();
      }

      public Div createController() {
         return new Div() {
            public List<Component> build() {
               boolean value = (Boolean)VisibilityOption.this.userValue.acquire(this);
               return Arrays.asList(
                  new Button(
                     Text.translatable(
                        VisibilityOption.this.getTranslationKey(), new String[]{value ? Text.GUI_YES.getDisplayString() : Text.GUI_NO.getDisplayString()}
                     ),
                     () -> VisibilityOption.this.userValue.set(!(Boolean)VisibilityOption.this.userValue.peek())
                  )
               );
            }
         };
      }
   }
}
