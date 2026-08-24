package com.teamresourceful.resourcefulconfig.web.server.paths;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.teamresourceful.resourcefulconfig.web.info.UserJwtPayload;
import com.teamresourceful.resourcefulconfig.web.utils.WebServerUtils;
import com.teamresourceful.resourcefulconfig.web.utils.WebVerifier;
import java.io.IOException;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface BasePath extends HttpHandler {
   @Override
   default void handle(HttpExchange exchange) throws IOException {
      if (!WebServerUtils.handleCors(exchange, this.verifier().origin(), this.method())) {
         if (exchange.getRequestMethod().equalsIgnoreCase(this.method())) {
            UserJwtPayload payload = this.verifier().getInfo(exchange);
            if (payload != null) {
               this.handleCall(exchange, payload);
            } else {
               WebServerUtils.send(exchange, 401, null, new byte[0]);
            }
         } else {
            WebServerUtils.send(exchange, 400, null, new byte[0]);
         }
      }
   }

   void handleCall(HttpExchange var1, UserJwtPayload var2) throws IOException;

   WebVerifier verifier();

   String method();
}
