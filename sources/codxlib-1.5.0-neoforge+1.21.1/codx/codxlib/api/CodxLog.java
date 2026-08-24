package codx.codxlib.api;

public final class CodxLog {
   private final String prefix;
   private volatile boolean enabled;

   private CodxLog(String prefix) {
      this.prefix = prefix == null ? "" : prefix;
   }

   public static CodxLog create(String prefix) {
      return new CodxLog(prefix);
   }

   public void setEnabled(boolean value) {
      this.enabled = value;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void debug(String message) {
      if (this.enabled) {
         System.out.println(this.prefix + message);
      }
   }

   public void debug(String format, Object... args) {
      if (this.enabled) {
         System.out.println(this.prefix + (args.length == 0 ? format : String.format(format, args)));
      }
   }
}
