package dev.latvian.mods.kubejs.web;

import dev.latvian.apps.tinyserver.ServerRegistry;
import dev.latvian.apps.tinyserver.http.HTTPHandler;
import dev.latvian.apps.tinyserver.http.HTTPMethod;
import dev.latvian.apps.tinyserver.http.response.HTTPResponse;
import dev.latvian.apps.tinyserver.http.response.error.client.ForbiddenError;
import dev.latvian.apps.tinyserver.http.response.error.client.UnauthorizedError;
import dev.latvian.apps.tinyserver.ws.WSEndpointHandler;
import dev.latvian.apps.tinyserver.ws.WSHandler;
import dev.latvian.apps.tinyserver.ws.WSSession;
import dev.latvian.apps.tinyserver.ws.WSSessionFactory;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class LocalWebServerRegistry implements ServerRegistry<KJSHTTPRequest> {
   private final KJSHTTPServer server;
   private final Set<LocalWebServer.Endpoint> endpoints;
   private final boolean requireAuth;

   LocalWebServerRegistry(KJSHTTPServer server, Set<LocalWebServer.Endpoint> endpoints, boolean requireAuth) {
      this.server = server;
      this.endpoints = endpoints;
      this.requireAuth = requireAuth;
   }

   private HTTPHandler<KJSHTTPRequest> wrap(HTTPHandler<KJSHTTPRequest> handler) {
      return (HTTPHandler<KJSHTTPRequest>)(this.requireAuth ? new LocalWebServerRegistry.AuthHandler(handler, "Bearer " + this.server.auth) : handler);
   }

   public void http(HTTPMethod method, String path, HTTPHandler<KJSHTTPRequest> handler) {
      this.endpoints.add(new LocalWebServer.Endpoint(method.name(), path, this.requireAuth));
      this.server.http(method, path, this.wrap(handler));
   }

   public <WSS extends WSSession<KJSHTTPRequest>> WSHandler<KJSHTTPRequest, WSS> ws(String path, WSSessionFactory<KJSHTTPRequest, WSS> factory) {
      this.endpoints.add(new LocalWebServer.Endpoint("WS", path, this.requireAuth));
      WSEndpointHandler<KJSHTTPRequest, WSS> handler = new WSEndpointHandler(factory, new ConcurrentHashMap());
      this.server.http(HTTPMethod.GET, path, this.wrap(handler));
      return handler;
   }

   private record AuthHandler(HTTPHandler<KJSHTTPRequest> handler, String match) implements HTTPHandler<KJSHTTPRequest> {
      public HTTPResponse handle(KJSHTTPRequest req) throws Exception {
         String a = req.header("Authorization").asString();
         if (a.isEmpty()) {
            throw new UnauthorizedError("Missing Authorization header");
         } else if (!a.equals(this.match)) {
            throw new ForbiddenError("Authorization header does not match configured auth token");
         } else {
            return this.handler.handle(req);
         }
      }
   }
}
