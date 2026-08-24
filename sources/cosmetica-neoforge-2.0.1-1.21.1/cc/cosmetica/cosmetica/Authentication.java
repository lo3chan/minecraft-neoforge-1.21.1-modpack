package cc.cosmetica.cosmetica;

import cc.cosmetica.core.api.CosmeticaAPI;
import cc.cosmetica.core.api.LoginResult;
import cc.cosmetica.core.api.CosmeticaAPI.AuthChangeReason;
import cc.cosmetica.core.api.LoginResult.Code;
import cc.cosmetica.core.builtin.manager.SelfCosmeticManager;
import cc.cosmetica.core.impl.BlockModelManager;
import cc.cosmetica.core.impl.Logging;
import cc.cosmetica.core.impl.LoggingCategory;
import cc.cosmetica.cosmetica.settings.CosmeticaSettings;
import cc.cosmetica.cosmetica.util.CosmeticaLogCategory;
import cc.cosmetica.kupe.api.State;
import cc.cosmetica.kupe.api.Text;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import gg.cloaks.javaclient.ApiException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public final class Authentication {
   private static final ResourceLocation SESSIONS = ResourceLocation.fromNamespaceAndPath("cosmetica", ".sessions");
   private static final ScheduledExecutorService LOGIN_SCHEDULER = Executors.newScheduledThreadPool(1, new ThreadFactory() {
      private int counter = 1;

      @Override
      public Thread newThread(@NotNull Runnable r) {
         Thread t = new Thread(r);
         t.setName("Cosmetica Login Worker " + this.counter++);
         return t;
      }
   });
   private static volatile boolean authenticating = false;
   private static final AtomicInteger RETRIES = new AtomicInteger(0);
   private static final Object lock = new Object();
   public static final State<Optional<LoginResult>> LOGIN_RESULT = new State(Optional.empty());
   private static long lastInvalidation = System.currentTimeMillis() - 1000L;
   public static final AtomicBoolean everAuthenticated = new AtomicBoolean(false);
   public static final AtomicBoolean showedUnauthenticatedToast = new AtomicBoolean(false);

   static void authenticate() {
      CosmeticaAPI.addAuthenticationChangeCallback(
         reason -> {
            if (!CosmeticaAPI.isAuthenticated()) {
               Minecraft.getInstance().execute(CosmeticaSettings::clearSettings);
               if (reason == AuthChangeReason.ERROR_401) {
                  synchronized (lock) {
                     if (System.currentTimeMillis() - lastInvalidation > 1000L) {
                        invalidateToken();
                     } else {
                        Logging.getInstance()
                           .debug(CosmeticaLogCategory.LOGIN, "Skipping token invalidation as token already invalidated within last second.", new Object[0]);
                     }

                     lastInvalidation = System.currentTimeMillis();
                  }
               }
            }

            if (!System.getProperties().containsKey("cosmetica.token")) {
               boolean startAuth = false;
               synchronized (lock) {
                  if (!authenticating && !CosmeticaAPI.isAuthenticated()) {
                     authenticating = true;
                     startAuth = true;
                  } else if (CosmeticaAPI.isAuthenticated()) {
                     RETRIES.set(0);
                     authenticating = false;
                     everAuthenticated.set(true);
                     if (showedUnauthenticatedToast.compareAndSet(true, false)) {
                        Cosmetica.showToast(Text.translatable("toast.cosmetica.reconnected", new String[0]), null);
                     }
                  }
               }

               if (startAuth) {
                  startAuthentication();
               }
            }
         }
      );
      if (!System.getProperties().containsKey("cosmetica.token")) {
         authenticating = true;
         startAuthentication();
      }
   }

   private static void startAuthentication() {
      Path sessionsInfo = BlockModelManager.getCacheFile(SESSIONS, null);
      Properties properties = new Properties();
      boolean login = false;

      try {
         login = logInFromCache(sessionsInfo, properties);
      } catch (IOException var4) {
         Logging.getInstance().error("Failed to log into Cosmetica via cache", var4);
      }

      if (!login) {
         LOGIN_SCHEDULER.schedule(() -> repeatLogInFromApi(sessionsInfo, properties), 0L, TimeUnit.SECONDS);
      }
   }

   private static void invalidateToken() {
      Logging.getInstance().info("Cosmetica authentication has expired. Will reauthenticate!", new Object[0]);
      Path sessionsInfo = BlockModelManager.getCacheFile(SESSIONS, null);
      Properties properties = new Properties();
      if (!Files.isRegularFile(sessionsInfo)) {
         Logging.getInstance().warn("Tried to invalidate token but sessions path doesn't exist", new Object[0]);
      } else {
         try (BufferedInputStream b = new BufferedInputStream(Files.newInputStream(sessionsInfo))) {
            properties.load(b);
         } catch (IOException var12) {
            Logging.getInstance().error("Failed to load cosmetica sessions", var12);
            return;
         }

         User user = Minecraft.getInstance().getUser();
         String tokenKey = jwtKey(user.getProfileId());
         properties.remove(tokenKey);

         try (BufferedOutputStream boss = new BufferedOutputStream(Files.newOutputStream(sessionsInfo))) {
            properties.store(boss, "Cosmetica Session Info");
            Logging.getInstance().debug(CosmeticaLogCategory.LOGIN, "Invalidated token", new Object[0]);
         } catch (IOException var10) {
            Logging.getInstance().error("Failed to save cosmetica sessions", var10);
         }
      }
   }

   private static String jwtKey(String uuid) {
      return "jwt-" + uuid.replace("-", "");
   }

   private static String jwtKey(UUID uuid) {
      return "jwt-" + uuid.toString().replace("-", "");
   }

   private static void repeatLogInFromApi(Path sessionsInfo, Properties properties) {
      if (!logInFromApi(sessionsInfo, properties)) {
         int[] retryCounts = new int[]{1, 5, 10, 30, 60};
         int retries = RETRIES.getAndIncrement();
         if (retries >= retryCounts.length) {
            Logging.getInstance().info("Retrying cosmetica login in {} seconds", new Object[]{retryCounts[retryCounts.length - 1]});
            LOGIN_SCHEDULER.schedule(() -> repeatLogInFromApi(sessionsInfo, properties), (long)retryCounts[retryCounts.length - 1], TimeUnit.SECONDS);
         } else {
            if (retries == 2) {
               Logging.getInstance().debug(LoggingCategory.COSMETICS, "Clearing cosmetics due to 2 failed retries.", new Object[0]);
               SelfCosmeticManager.clear();
            }

            Logging.getInstance().info("Retrying cosmetica login in {} seconds", new Object[]{retryCounts[retries]});
            LOGIN_SCHEDULER.schedule(() -> repeatLogInFromApi(sessionsInfo, properties), (long)retryCounts[retries], TimeUnit.SECONDS);
         }
      }
   }

   private static boolean logInFromCache(Path sessionInfoPath, Properties sessionInfo) throws IOException {
      if (Files.isRegularFile(sessionInfoPath)) {
         BufferedInputStream b = new BufferedInputStream(Files.newInputStream(sessionInfoPath));

         try {
            sessionInfo.load(b);
         } catch (Throwable var9) {
            try {
               b.close();
            } catch (Throwable var7) {
               var9.addSuppressed(var7);
            }

            throw var9;
         }

         b.close();
         User var10 = Minecraft.getInstance().getUser();
         String token = sessionInfo.getProperty(jwtKey(var10.getProfileId()));
         if (token != null) {
            try {
               byte[] info = Base64.getDecoder().decode(token.split("\\.")[1]);
               JsonObject object = new JsonParser().parse(new InputStreamReader(new ByteArrayInputStream(info))).getAsJsonObject();
               String exp = object.get("exp").getAsString();
               if (Long.parseLong(exp) - Instant.now().getEpochSecond() > 0L) {
                  Logging.getInstance().debug(CosmeticaLogCategory.LOGIN, "Using cached JWT for auth", new Object[0]);
                  CosmeticaAPI.authenticate(
                     token, "Cosmetica Official Mod", !CosmeticaSettings.willApplyLocalSettings(), (String)CosmeticaSettings.MODPACK_ID.peek()
                  );
                  return true;
               }
            } catch (IndexOutOfBoundsException | JsonParseException var8) {
               throw new RuntimeException("Malformed JWT", var8);
            }
         }
      } else {
         Files.createDirectories(sessionInfoPath.getParent());
         Files.createFile(sessionInfoPath);
      }

      return false;
   }

   private static boolean logInFromApi(Path sessionInfoPath, Properties sessionInfo) {
      Logging.getInstance().debug(CosmeticaLogCategory.LOGIN, "Logging in to Cosmetica...", new Object[0]);

      try {
         LoginResult result = CosmeticaAPI.login(
            "Cosmetica Official Mod", !CosmeticaSettings.willApplyLocalSettings(), (String)CosmeticaSettings.MODPACK_ID.peek()
         );
         Logging.getInstance().debug(CosmeticaLogCategory.LOGIN, "LoginResult received", new Object[0]);
         Minecraft.getInstance()
            .execute(
               () -> {
                  LoginResult message = result;
                  if (result.getException().isPresent()
                     && result.getException().get() instanceof ApiException
                     && ((Exception)result.getException().get()).getCause() instanceof UnknownHostException) {
                     message = new LoginResult(
                        false, Code.SUCCESS, result.getMessage(), (UnknownHostException)((Exception)result.getException().get()).getCause()
                     );
                  }

                  if (!((Optional)LOGIN_RESULT.peek()).isPresent()
                     || ((LoginResult)((Optional)LOGIN_RESULT.peek()).get()).getCode() != message.getCode()
                     || ((LoginResult)((Optional)LOGIN_RESULT.peek()).get()).isSuccess() != message.isSuccess()) {
                     LOGIN_RESULT.set(Optional.of(message));
                  }
               }
            );
         if (result.isSuccess()) {
            String token = CosmeticaAPI.getSessionToken();
            User user = Minecraft.getInstance().getUser();
            if (!token.isEmpty()) {
               sessionInfo.setProperty(jwtKey(user.getProfileId()), token);

               try (BufferedOutputStream b = new BufferedOutputStream(Files.newOutputStream(sessionInfoPath))) {
                  sessionInfo.store(b, "Cosmetica Session Info");
               }
            }

            return true;
         }
      } catch (UnknownHostException var10) {
         Logging.getInstance().error("Failed to log in", var10);
         Minecraft.getInstance().execute(() -> LOGIN_RESULT.set(Optional.of(new LoginResult(false, Code.SUCCESS, var10.getMessage(), var10))));
      } catch (IOException var11) {
         Logging.getInstance().error("Failed to log in", var11);
      }

      return false;
   }
}
