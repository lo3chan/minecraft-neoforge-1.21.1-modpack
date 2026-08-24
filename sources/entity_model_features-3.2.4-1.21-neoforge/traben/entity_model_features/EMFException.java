package traben.entity_model_features;

public class EMFException extends Exception {
   final String errorMsg;

   public EMFException(String s) {
      this.errorMsg = s;
   }

   public EMFException record() {
      return recordException(this);
   }

   public static <E extends Exception> E recordException(E s) {
      EMFManager.getInstance().receiveException(s);
      return s;
   }

   @Override
   public String toString() {
      return this.errorMsg;
   }
}
