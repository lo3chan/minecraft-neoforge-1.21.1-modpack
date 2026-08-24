package cc.cosmetica.cosmetica.mixin;

import cc.cosmetica.core.api.AsyncApi;
import cc.cosmetica.core.api.CosmeticaAPI.AuthChangeReason;
import cc.cosmetica.core.impl.CosmeticaSession;
import cc.cosmetica.cosmetica.Authentication;
import cc.cosmetica.cosmetica.Cosmetica;
import cc.cosmetica.kupe.api.Text;
import gg.cloaks.javaclient.ApiException;
import java.util.concurrent.CompletionException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
   value = {AsyncApi.class},
   remap = false
)
public class AsyncApiMixin {
   @Inject(
      at = {@At("HEAD")},
      method = {"lambda$requestAsync$1(Ljava/lang/Throwable;)Ljava/lang/Object;"}
   )
   private static void a(Throwable t, CallbackInfoReturnable<Object> cir) {
      Throwable t1 = t;
      if (t instanceof CompletionException) {
         t1 = t.getCause();
      }

      if (t1 instanceof ApiException
         && (((ApiException)t1).getCode() == 502 || ((ApiException)t1).getCode() == 503 || ((ApiException)t1).getCode() == 504)
         && Authentication.everAuthenticated.get()) {
         CosmeticaSession.deauthenticate(AuthChangeReason.ERROR_401);
         Cosmetica.showToast(
            Text.translatable("toast.cosmetica.disconnected", new String[0]), Text.translatable("toast.cosmetica.disconnected.message", new String[0])
         );
         Authentication.showedUnauthenticatedToast.set(true);
      }
   }
}
