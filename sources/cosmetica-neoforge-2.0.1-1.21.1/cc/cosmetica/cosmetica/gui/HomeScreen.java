package cc.cosmetica.cosmetica.gui;

import cc.cosmetica.core.api.Cosmetics;
import cc.cosmetica.core.api.LoginResult;
import cc.cosmetica.core.api.LoginResult.Code;
import cc.cosmetica.cosmetica.Authentication;
import cc.cosmetica.cosmetica.Cosmetica;
import cc.cosmetica.cosmetica.gui.widget.CosmeticEntry;
import cc.cosmetica.cosmetica.gui.widget.CosmeticsList;
import cc.cosmetica.kupe.api.ResourceKey;
import cc.cosmetica.kupe.api.State;
import cc.cosmetica.kupe.api.Text;
import cc.cosmetica.kupe.api.gui.Align;
import cc.cosmetica.kupe.api.gui.Button;
import cc.cosmetica.kupe.api.gui.Component;
import cc.cosmetica.kupe.api.gui.Div;
import cc.cosmetica.kupe.api.gui.Justify;
import cc.cosmetica.kupe.api.gui.Label;
import cc.cosmetica.kupe.api.gui.style.CommonProperties;
import cc.cosmetica.kupe.api.gui.style.Style;
import cc.cosmetica.kupe.api.gui.style.Stylesheet;
import cc.cosmetica.kupe.api.maths.Margins;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HomeScreen extends AbstractHomeScreen {
   private static boolean shownDebugToast = false;
   private final State<Boolean> dismissedError = new State(false);
   @Nullable
   private static LoginResult dismissed = null;
   private static final LoginResult GENERIC = new LoginResult(false, Code.SUCCESS, "", null);
   public static final ResourceKey ID = new ResourceKey("cosmetica", "home");

   public HomeScreen() {
      super(ID);
      if (Boolean.getBoolean("cosmetica.debug") && !shownDebugToast) {
         shownDebugToast = true;
         Cosmetica.showToast(Text.literal("Debug Toast"), Text.literal("You have debug mode on!"));
      }
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (Boolean.getBoolean("cosmetica.debug") && keyCode == 345) {
         Cosmetica.showToast(Text.literal("Debug Toast"), Text.literal("You hit right control!"));
      }

      return super.keyPressed(keyCode, scanCode, modifiers);
   }

   @NotNull
   @Override
   protected Component createRightMenu(Cosmetics cosmetics, boolean authenticated) {
      return new Component() {
         public List<Component> build() {
            boolean dismissedError = (Boolean)HomeScreen.this.dismissedError.acquire(this);
            LoginResult result = ((Optional)Authentication.LOGIN_RESULT.acquire(this)).orElse(HomeScreen.GENERIC);
            if (authenticated || dismissedError && HomeScreen.dismissed == result) {
               List<CosmeticEntry> entries = new ArrayList<>();
               CosmeticEntry.populateEntryList(entries, cosmetics, CosmeticEntry.Type.removable(authenticated));
               return ImmutableList.of(
                  new CosmeticsList(
                     entries,
                     !authenticated
                        ? CosmeticsList.ListType.OFFLINE
                        : (cosmetics != null && cosmetics.getOutfitId().isPresent() ? CosmeticsList.ListType.EDITABLE : CosmeticsList.ListType.DISABLED)
                  )
               );
            } else {
               return ImmutableList.of(HomeScreen.this.NotLoggedIn(result));
            }
         }
      };
   }

   private Component NotLoggedIn(LoginResult error) {
      return new Div(
            new Component[]{
               new Label(Text.translatable("label.cosmetica.offline", new String[0])).tag(new String[]{"not-logged-in-title"}),
               new Label(
                  error == GENERIC
                     ? Text.translatable("label.cosmetica.offline.logging_in", new String[0])
                     : (
                        error.getCode() == Code.SUCCESS && !error.isSuccess()
                           ? Text.translatable("label.cosmetica.offline.no_internet", new String[0])
                           : Text.translatable("label.cosmetica.offline." + error.getCode().toString().toLowerCase(Locale.ROOT), new String[0])
                     )
               ),
               new Label(Text.literal(error.getMessage())).tag(new String[]{"not-logged-in-description"}),
               new Button(Text.translatable("button.cosmetica.dismiss", new String[0]), () -> {
                  dismissed = error;
                  this.dismissedError.set(true);
               })
            }
         )
         .tag(new String[]{"not-logged-in"});
   }

   @NotNull
   @Override
   public Stylesheet getStylesheet() {
      return super.getStylesheet()
         .tag("not-logged-in", Style.create().set(Div.JUSTIFY_CONTENT, Justify.CENTRE).set(CommonProperties.PADDING, CommonProperties.fixed(new Margins(4))))
         .tag(
            "not-logged-in-title",
            Style.create().set(Label.ALIGN_TEXT, Align.CENTRE).set(CommonProperties.MARGINS, CommonProperties.fixed(new Margins(20, 0, 4, 0)))
         )
         .tag("not-logged-in-description", Style.create().set(CommonProperties.HEIGHT, CommonProperties.fixedSize(60)).set(Label.TEXT_COLOUR, 10526880));
   }
}
