package mezz.jei.gui.input.handlers;

import com.mojang.blaze3d.platform.InputConstants.Key;
import java.util.List;
import java.util.Optional;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IClickableIngredient;
import mezz.jei.api.runtime.IRecipesGui;
import mezz.jei.api.runtime.IScreenHelper;
import mezz.jei.common.input.IInternalKeyMappings;
import mezz.jei.gui.bookmarks.BookmarkList;
import mezz.jei.gui.input.UserInput;
import mezz.jei.gui.overlay.elements.IngredientElement;
import mezz.jei.gui.util.FocusUtil;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

public class ChatLinkInputHandler {
   private final IRecipesGui recipesGui;
   private final FocusUtil focusUtil;
   private final IScreenHelper screenHelper;
   private final BookmarkList bookmarkList;
   @Nullable
   private ChatLinkInputHandler.PendingInput pendingInput;

   public ChatLinkInputHandler(IRecipesGui recipesGui, FocusUtil focusUtil, IScreenHelper screenHelper, BookmarkList bookmarkList) {
      this.recipesGui = recipesGui;
      this.focusUtil = focusUtil;
      this.screenHelper = screenHelper;
      this.bookmarkList = bookmarkList;
   }

   public boolean handleUserInput(Screen screen, UserInput input, IInternalKeyMappings keyBindings) {
      if (screen instanceof ChatScreen chatScreen) {
         return switch (input.getInputType()) {
            case IMMEDIATE -> this.handleImmediateInput(chatScreen, input, keyBindings);
            case SIMULATE -> this.handleSimulateInput(chatScreen, input, keyBindings);
            case EXECUTE -> this.handleExecuteInput(chatScreen, input);
         };
      } else {
         this.pendingInput = null;
         return false;
      }
   }

   public void handleGuiChange() {
      this.pendingInput = null;
   }

   private boolean handleImmediateInput(ChatScreen chatScreen, UserInput input, IInternalKeyMappings keyBindings) {
      Optional<ChatLinkInputHandler.Action> optionalAction = getAction(input, keyBindings);
      if (optionalAction.isEmpty()) {
         return false;
      } else {
         Optional<ITypedIngredient<?>> optionalIngredient = this.getHoveredIngredient(chatScreen, input);
         if (optionalIngredient.isEmpty()) {
            return false;
         } else {
            ChatLinkInputHandler.Action action = optionalAction.get();
            ITypedIngredient<?> typedIngredient = optionalIngredient.get();
            this.executeAction(typedIngredient, action);
            return true;
         }
      }
   }

   private boolean handleSimulateInput(ChatScreen chatScreen, UserInput input, IInternalKeyMappings keyBindings) {
      this.pendingInput = null;
      Optional<ChatLinkInputHandler.Action> optionalAction = getAction(input, keyBindings);
      if (optionalAction.isEmpty()) {
         return false;
      } else {
         Optional<ITypedIngredient<?>> optionalIngredient = this.getHoveredIngredient(chatScreen, input);
         if (optionalIngredient.isEmpty()) {
            return false;
         } else {
            ChatLinkInputHandler.Action action = optionalAction.get();
            ITypedIngredient<?> typedIngredient = optionalIngredient.get();
            this.pendingInput = new ChatLinkInputHandler.PendingInput(input.getKey(), typedIngredient, action);
            return true;
         }
      }
   }

   private boolean handleExecuteInput(ChatScreen chatScreen, UserInput input) {
      ChatLinkInputHandler.PendingInput pendingInput = this.pendingInput;
      this.pendingInput = null;
      if (pendingInput == null) {
         return false;
      } else if (!pendingInput.key().equals(input.getKey())) {
         return false;
      } else {
         Optional<ITypedIngredient<?>> optionalIngredient = this.getHoveredIngredient(chatScreen, input);
         if (optionalIngredient.isEmpty()) {
            return false;
         } else {
            ITypedIngredient<?> typedIngredient = optionalIngredient.get();
            if (typedIngredient != pendingInput.typedIngredient()) {
               return false;
            } else {
               this.executeAction(typedIngredient, pendingInput.action());
               return true;
            }
         }
      }
   }

   private Optional<ITypedIngredient<?>> getHoveredIngredient(ChatScreen chatScreen, UserInput input) {
      return this.screenHelper
         .getClickableIngredientUnderMouse(chatScreen, input.getMouseX(), input.getMouseY())
         .map(ChatLinkInputHandler::getTypedIngredient)
         .findFirst();
   }

   private static ITypedIngredient<?> getTypedIngredient(IClickableIngredient<?> clickableIngredient) {
      return clickableIngredient.getTypedIngredient();
   }

   private static Optional<ChatLinkInputHandler.Action> getAction(UserInput input, IInternalKeyMappings keyBindings) {
      if (input.is(keyBindings.getShowRecipe())) {
         return Optional.of(ChatLinkInputHandler.Action.SHOW_RECIPE);
      } else if (input.is(keyBindings.getShowUses())) {
         return Optional.of(ChatLinkInputHandler.Action.SHOW_USES);
      } else {
         return input.is(keyBindings.getBookmark()) ? Optional.of(ChatLinkInputHandler.Action.BOOKMARK) : Optional.empty();
      }
   }

   private void executeAction(ITypedIngredient<?> typedIngredient, ChatLinkInputHandler.Action action) {
      switch (action) {
         case SHOW_RECIPE:
            this.show(typedIngredient, List.of(RecipeIngredientRole.OUTPUT));
            break;
         case SHOW_USES:
            this.show(typedIngredient, List.of(RecipeIngredientRole.INPUT, RecipeIngredientRole.CATALYST));
            break;
         case BOOKMARK:
            this.bookmarkList.addIngredientBookmark(typedIngredient);
      }
   }

   private void show(ITypedIngredient<?> typedIngredient, List<RecipeIngredientRole> roles) {
      IngredientElement<?> element = new IngredientElement<>(typedIngredient);
      element.show(this.recipesGui, this.focusUtil, roles);
   }

   private static enum Action {
      SHOW_RECIPE,
      SHOW_USES,
      BOOKMARK;
   }

   private record PendingInput(Key key, ITypedIngredient<?> typedIngredient, ChatLinkInputHandler.Action action) {
   }
}
