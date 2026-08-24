package dev.latvian.mods.kubejs.web;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;

public record RelativeURL(String path, Map<String, String> query) {
   public RelativeURL(String path) {
      this(path, Map.of());
   }

   @Override
   public String toString() {
      StringBuilder url = new StringBuilder(this.path);
      boolean first = true;

      for (Entry<String, String> entry : this.query.entrySet()) {
         url.append((char)(first ? '?' : '&'))
            .append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8))
            .append('=')
            .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
         first = false;
      }

      return url.toString();
   }

   public String fullString() {
      LocalWebServer instance = LocalWebServer.instance();
      return instance == null ? "" : instance.url() + this;
   }
}
