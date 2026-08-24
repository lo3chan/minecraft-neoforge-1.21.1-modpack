package cc.cosmetica.cosmetica;

import cc.cosmetica.kupe.api.Text;

public class VersionChecker {
   private Text message;
   public static final VersionChecker INSTANCE = new VersionChecker();

   private VersionChecker() {
   }

   public synchronized void setMessage(Text message) {
      this.message = message;
   }

   public synchronized Text getMessage() {
      Text var1;
      try {
         var1 = this.message;
      } finally {
         this.message = null;
      }

      return var1;
   }
}
