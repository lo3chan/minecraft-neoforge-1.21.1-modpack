package snownee.jade.api;

import org.jetbrains.annotations.Nullable;

public class TraceableException extends RuntimeException {
   private final String namespace;

   public TraceableException(Throwable cause, String namespace) {
      super("Exception occurred in " + namespace, cause);
      this.namespace = namespace;
   }

   public static RuntimeException create(Throwable cause, @Nullable String namespace) {
      if (namespace != null && !"minecraft".equals(namespace)) {
         return new TraceableException(cause, namespace);
      } else {
         return cause instanceof RuntimeException runtimeException ? runtimeException : new RuntimeException(cause);
      }
   }

   public String getNamespace() {
      return this.namespace;
   }
}
