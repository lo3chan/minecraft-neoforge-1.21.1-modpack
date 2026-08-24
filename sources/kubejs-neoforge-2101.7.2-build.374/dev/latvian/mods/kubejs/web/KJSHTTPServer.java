package dev.latvian.mods.kubejs.web;

import dev.latvian.apps.tinyserver.HTTPServer;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;
import net.minecraft.util.thread.BlockableEventLoop;

public class KJSHTTPServer extends HTTPServer<KJSHTTPRequest> {
   public final transient String auth;
   public final transient String encodedAuth;

   KJSHTTPServer(KJSHTTPServer.RequestFactory requestFactory, String auth) {
      super(requestFactory);
      this.auth = auth;
      this.encodedAuth = auth.isEmpty() ? "" : URLEncoder.encode(auth, StandardCharsets.UTF_8);
   }

   record RequestFactory(BlockableEventLoop<?> eventLoop) implements Supplier<KJSHTTPRequest> {
      public KJSHTTPRequest get() {
         return new KJSHTTPRequest(this.eventLoop);
      }
   }
}
