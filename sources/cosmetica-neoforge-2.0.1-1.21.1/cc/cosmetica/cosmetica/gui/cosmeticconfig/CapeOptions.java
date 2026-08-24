package cc.cosmetica.cosmetica.gui.cosmeticconfig;

public final class CapeOptions extends CosmeticOptions {
   private final boolean cloak;
   private final boolean elytra;

   public CapeOptions(boolean cloak, boolean elytra) {
      this.cloak = cloak;
      this.elytra = elytra;
   }

   public CapeOptions(int flags) {
      this((flags & 1) != 0, (flags & 2) != 0);
   }

   public boolean isCloak() {
      return this.cloak;
   }

   public boolean isElytra() {
      return this.elytra;
   }
}
