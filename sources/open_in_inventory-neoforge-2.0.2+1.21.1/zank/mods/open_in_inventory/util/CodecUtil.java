package zank.mods.open_in_inventory.util;

import com.mojang.serialization.DataResult;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class CodecUtil {
   public static <R> R getOrThrow(DataResult<R> result, Function<? super String, ? extends RuntimeException> error) {
      CodecUtil.ErrorCapture errorCapture = new CodecUtil.ErrorCapture();
      Optional<R> optional = result.resultOrPartial(errorCapture);
      if (errorCapture.err != null) {
         throw (RuntimeException)error.apply(errorCapture.err);
      } else {
         return optional.orElseThrow();
      }
   }

   private static final class ErrorCapture implements Consumer<String> {
      private String err = null;

      public void accept(String s) {
         this.err = s;
      }
   }
}
