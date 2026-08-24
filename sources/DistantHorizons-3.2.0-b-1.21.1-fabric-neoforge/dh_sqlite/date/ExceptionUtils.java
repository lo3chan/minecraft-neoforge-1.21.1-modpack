package dh_sqlite.date;

public class ExceptionUtils {
   public static <R> R rethrow(Throwable throwable) {
      return typeErasure(throwable);
   }

   private static <R, T extends Throwable> R typeErasure(Throwable throwable) throws T {
      throw throwable;
   }
}
