package dev.latvian.mods.kubejs.web;

import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.util.BaseProperties;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Random;

public class WebServerProperties extends BaseProperties {
   private static WebServerProperties instance;
   public boolean enabled;
   public int port;
   public String publicAddress;
   public String auth;

   public static WebServerProperties get() {
      if (instance == null) {
         instance = new WebServerProperties();
      }

      return instance;
   }

   public static void reload() {
      instance = new WebServerProperties();
   }

   private WebServerProperties() {
      super(KubeJSPaths.WEB_SERVER_PROPERTIES, "KubeJS Web Server Properties");
   }

   @Override
   protected void load() {
      this.enabled = this.get("enabled", true);
      this.port = this.get("port", 61423);
      this.publicAddress = this.get("public_address", "");
      byte[] randomAuth = new byte[33];
      new Random().nextBytes(randomAuth);
      this.auth = this.get("auth", new String(Base64.getUrlEncoder().encode(randomAuth), StandardCharsets.UTF_8));
   }
}
