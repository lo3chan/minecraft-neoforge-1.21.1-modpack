package dhcomgithubluben.zstd;

import java.io.IOException;

public class ZstdIOException extends IOException {
   private long code;

   public ZstdIOException(long l) {
      this(Zstd.getErrorCode(l), Zstd.getErrorName(l));
   }

   public ZstdIOException(long l, String string) {
      super(string);
      this.code = l;
   }

   public long getErrorCode() {
      return this.code;
   }
}
