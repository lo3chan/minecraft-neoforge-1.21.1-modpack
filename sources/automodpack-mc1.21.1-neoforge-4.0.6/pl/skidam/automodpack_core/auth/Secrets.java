package pl.skidam.automodpack_core.auth;

import java.net.SocketAddress;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map.Entry;
import pl.skidam.automodpack_core.GlobalVariables;
import pl.skidam.automodpack_core.utils.TimedSet;

public class Secrets {
   private static final TimedSet<String> cachedValidSecrets = new TimedSet<>(3500L);

   public static Secrets.Secret generateSecret() {
      SecureRandom random = new SecureRandom();
      byte[] bytes = new byte[32];
      random.nextBytes(bytes);
      String secret = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
      if (secret == null) {
         return null;
      } else {
         long timestamp = System.currentTimeMillis() / 1000L;
         return new Secrets.Secret(secret, timestamp);
      }
   }

   public static boolean isSecretValid(String secretStr, SocketAddress address) {
      if (!GlobalVariables.serverConfig.validateSecrets) {
         return true;
      } else if (cachedValidSecrets.contains(secretStr)) {
         return true;
      } else {
         Entry<String, Secrets.Secret> playerSecretPair = SecretsStore.getHostSecret(secretStr);
         if (playerSecretPair == null) {
            return false;
         } else {
            Secrets.Secret secret = playerSecretPair.getValue();
            if (secret == null) {
               return false;
            } else {
               String playerUuid = playerSecretPair.getKey();
               if (!GlobalVariables.GAME_CALL.isPlayerAuthorized(address, playerUuid)) {
                  return false;
               } else {
                  long secretLifetime = GlobalVariables.serverConfig.secretLifetime * 3600L;
                  long currentTime = System.currentTimeMillis() / 1000L;
                  boolean valid = secret.timestamp() + secretLifetime > currentTime;
                  if (!valid) {
                     return false;
                  } else {
                     cachedValidSecrets.add(secretStr);
                     return true;
                  }
               }
            }
         }
      }
   }

   public static class Secret {
      private String secret;
      private Long timestamp;

      public Secret(String secret, Long timestamp) {
         this.secret = secret;
         this.timestamp = timestamp;
      }

      public String secret() {
         return this.secret;
      }

      public byte[] secretBytes() {
         return Base64.getUrlDecoder().decode(this.secret);
      }

      public Long timestamp() {
         return this.timestamp;
      }

      @Override
      public String toString() {
         return "Secret{secret='" + this.secret + "', timestamp=" + this.timestamp + "}";
      }
   }
}
