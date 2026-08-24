package io.wispforest.owo.config.ui;

import io.wispforest.owo.Owo;
import io.wispforest.owo.config.ConfigWrapper;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.ExcludeFromScreen;
import io.wispforest.owo.config.annotation.Expanded;
import io.wispforest.owo.config.annotation.RestartRequired;
import io.wispforest.owo.config.annotation.SectionHeader;
import io.wispforest.owo.config.ui.component.ConfigEnumButton;
import io.wispforest.owo.config.ui.component.ConfigSlider;
import io.wispforest.owo.config.ui.component.ConfigTextBox;
import io.wispforest.owo.config.ui.component.ConfigToggleButton;
import io.wispforest.owo.config.ui.component.OptionValueProvider;
import io.wispforest.owo.config.ui.component.SearchAnchorComponent;
import io.wispforest.owo.ui.base.BaseComponent;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.CollapsibleContainer;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.CursorStyle;
import io.wispforest.owo.ui.core.Easing;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.ParentComponent;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import io.wispforest.owo.ui.parsing.UIParsing;
import io.wispforest.owo.ui.util.UISounds;
import io.wispforest.owo.util.NumberReflection;
import io.wispforest.owo.util.ReflectionUtils;
import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;

public class ConfigScreen extends BaseUIModelScreen<FlowLayout> {
   public static final ResourceLocation DEFAULT_MODEL_ID = ResourceLocation.fromNamespaceAndPath("owo", "config");
   private static final Map<Predicate<Option<?>>, OptionComponentFactory<?>> DEFAULT_FACTORIES = new HashMap<>();
   protected final Map<Predicate<Option<?>>, OptionComponentFactory<?>> extraFactories = new HashMap<>();
   protected final Screen parent;
   protected final ConfigWrapper<?> config;
   protected final Map<Option, OptionValueProvider> options = new HashMap<>();
   protected String lastSearchFieldText = "";
   @Nullable
   protected ConfigScreen.SearchMatches currentMatches = null;
   protected int currentMatchIndex = 0;

   protected ConfigScreen(ResourceLocation modelId, ConfigWrapper<?> config, @Nullable Screen parent) {
      super(FlowLayout.class, BaseUIModelScreen.DataSource.asset(modelId));
      this.parent = parent;
      this.config = config;
   }

   public static ConfigScreen create(ConfigWrapper<?> config, @Nullable Screen parent) {
      return new ConfigScreen(DEFAULT_MODEL_ID, config, parent);
   }

   public static ConfigScreen createWithCustomModel(ResourceLocation modelId, ConfigWrapper<?> config, @Nullable Screen parent) {
      return new ConfigScreen(modelId, config, parent);
   }

   @Deprecated(
      forRemoval = true
   )
   public static <S extends ConfigScreen> void registerProvider(String modId, Function<Screen, S> supplier) {
      ConfigScreenProviders.registerOwoConfigScreen(modId, supplier);
   }

   @Deprecated(
      forRemoval = true
   )
   @Nullable
   public static Function<Screen, ? extends ConfigScreen> getProvider(String modId) {
      return ConfigScreenProviders.getOwoProvider(modId);
   }

   @Deprecated(
      forRemoval = true
   )
   public static void forEachProvider(BiConsumer<String, Function<Screen, ? extends ConfigScreen>> action) {
      ConfigScreenProviders.forEachOwoProvider(action);
   }

   protected void build(FlowLayout rootComponent) {
      this.options.clear();
      rootComponent.childById(LabelComponent.class, "title")
         .text(net.minecraft.network.chat.Component.translatable("text.config." + this.config.name() + ".title"));
      if (this.minecraft.level == null) {
         rootComponent.surface(Surface.OPTIONS_BACKGROUND);
      }

      ((ButtonComponent)rootComponent.childById(ButtonComponent.class, "done-button")).onPress(button -> this.onClose());
      ((ButtonComponent)rootComponent.childById(ButtonComponent.class, "reload-button")).onPress(button -> {
         this.config.load();
         this.uiAdapter = null;
         this.rebuildWidgets();
      });
      FlowLayout optionPanel = rootComponent.childById(FlowLayout.class, "option-panel");
      LinkedHashMap<Component, net.minecraft.network.chat.Component> sections = new LinkedHashMap<>();
      HashMap<Option.Key, FlowLayout> containers = new HashMap<>();
      containers.put(Option.Key.ROOT, optionPanel);
      ((TextBoxComponent)rootComponent.childById(TextBoxComponent.class, "search-field"))
         .configure(
            searchField -> {
               LabelComponent matchIndicator = rootComponent.childById(LabelComponent.class, "search-match-indicator");
               ScrollContainer optionScroll = rootComponent.childById(ScrollContainer.class, "option-panel-scroll");
               String searchHint = I18n.get("text.owo.config.search", new Object[0]);
               searchField.setSuggestion(searchHint);
               searchField.onChanged().subscribe(s -> {
                  searchField.setSuggestion(s.isEmpty() ? searchHint : "");
                  if (!s.equals(this.lastSearchFieldText)) {
                     searchField.setTextColor(14737632);
                     matchIndicator.text(net.minecraft.network.chat.Component.empty());
                  }
               });
               searchField.keyPress()
                  .subscribe(
                     (keyCode, scanCode, modifiers) -> {
                        if (keyCode != 257 && keyCode != 335) {
                           return false;
                        } else {
                           String query = searchField.getValue().toLowerCase(Locale.ROOT);
                           if (query.isBlank()) {
                              return false;
                           } else {
                              if (this.currentMatches == null || !this.currentMatches.query.equals(query)) {
                                 String[] splitQuery = query.split(" ");
                                 this.currentMatchIndex = 0;
                                 this.currentMatches = new ConfigScreen.SearchMatches(
                                    query,
                                    this.collectSearchAnchors(optionScroll)
                                       .stream()
                                       .filter(anchor -> Arrays.stream(splitQuery).allMatch(anchor.currentSearchText()::contains))
                                       .toList()
                                 );
                              } else if (this.currentMatches.matches().isEmpty()) {
                                 this.currentMatchIndex = -1;
                              } else {
                                 this.currentMatchIndex = (this.currentMatchIndex + 1) % this.currentMatches.matches.size();
                              }

                              if (this.currentMatches.matches.isEmpty()) {
                                 matchIndicator.text(net.minecraft.network.chat.Component.translatable("text.owo.config.search.no_matches"));
                                 searchField.setTextColor(15408438);
                              } else {
                                 matchIndicator.text(
                                    net.minecraft.network.chat.Component.translatable(
                                       "text.owo.config.search.matches", new Object[]{this.currentMatchIndex + 1, this.currentMatches.matches.size()}
                                    )
                                 );
                                 searchField.setTextColor(2686911);
                                 SearchAnchorComponent selectedMatch = this.currentMatches.matches.get(this.currentMatchIndex);
                                 ParentComponent anchorFrame = selectedMatch.anchorFrame();
                                 ArrayDeque<Option.Key> pathToRoot = new ArrayDeque<>();

                                 for (Option.Key key = selectedMatch.key(); !key.isRoot(); key = key.parent()) {
                                    pathToRoot.push(key);
                                 }

                                 while (!pathToRoot.isEmpty()) {
                                    if (containers.get(pathToRoot.pop()) instanceof CollapsibleContainer collapsible && !collapsible.expanded()) {
                                       collapsible.toggleExpansion();
                                    }
                                 }

                                 if (anchorFrame instanceof FlowLayout flow) {
                                    flow.child(0, selectedMatch.configure(new ConfigScreen.SearchHighlighterComponent()));
                                 }

                                 if (anchorFrame.y() < optionScroll.y() || anchorFrame.y() + anchorFrame.height() > optionScroll.y() + optionScroll.height()) {
                                    optionScroll.scrollTo(selectedMatch.anchorFrame());
                                 }
                              }

                              return true;
                           }
                        }
                     }
                  );
            }
         );
      this.config
         .forEachOption(
            option -> {
               if (!option.backingField().hasAnnotation(ExcludeFromScreen.class)) {
                  Option.Key parentKey = option.key().parent();
                  if (parentKey.isRoot() || !this.config.fieldForKey(parentKey).isAnnotationPresent(ExcludeFromScreen.class)) {
                     OptionComponentFactory factory = this.factoryForOption(option);
                     if (factory == null) {
                        Owo.LOGGER.warn("Could not create UI component for config option {}", option);
                     } else {
                        OptionComponentFactory.Result result = factory.make(this.model, option);
                        this.options.put(option, result.optionProvider());
                        boolean expanded = !parentKey.isRoot() && this.config.fieldForKey(parentKey).isAnnotationPresent(Expanded.class);
                        FlowLayout container = containers.getOrDefault(
                           parentKey,
                           Containers.collapsible(
                                 Sizing.fill(100),
                                 Sizing.content(),
                                 net.minecraft.network.chat.Component.translatable("text.config." + this.config.name() + ".category." + parentKey.asString()),
                                 expanded
                              )
                              .configure(
                                 nestedContainer -> {
                                    String categoryKey = "text.config." + this.config.name() + ".category." + parentKey.asString();
                                    if (I18n.exists(categoryKey + ".tooltip")) {
                                       nestedContainer.titleLayout().tooltip(net.minecraft.network.chat.Component.translatable(categoryKey + ".tooltip"));
                                    }

                                    nestedContainer.titleLayout()
                                       .child(
                                          new SearchAnchorComponent(nestedContainer.titleLayout(), option.key(), () -> I18n.get(categoryKey, new Object[0]))
                                             .highlightConfigurator(
                                                highlight -> highlight.positioning(Positioning.absolute(-5, -5)).verticalSizing(Sizing.fixed(19))
                                             )
                                       );
                                 }
                              )
                        );
                        if (!containers.containsKey(parentKey) && containers.containsKey(parentKey.parent())) {
                           if (this.config.fieldForKey(parentKey).isAnnotationPresent(SectionHeader.class)) {
                              this.appendSection(sections, this.config.fieldForKey(parentKey), containers.get(parentKey.parent()));
                           }

                           containers.put(parentKey, container);
                           containers.get(parentKey.parent()).child(container);
                        }

                        if (option.detached()) {
                           result.baseComponent()
                              .tooltip(
                                 this.minecraft
                                    .font
                                    .split(net.minecraft.network.chat.Component.translatable("text.owo.config.managed_by_server"), 2147483647)
                                    .stream()
                                    .<ClientTooltipComponent>map(ClientTooltipComponent::create)
                                    .toList()
                              );
                        } else {
                           ArrayList<FormattedCharSequence> tooltipText = new ArrayList<>();
                           String tooltipTranslationKey = option.translationKey() + ".tooltip";
                           if (I18n.exists(tooltipTranslationKey)) {
                              tooltipText.addAll(
                                 this.minecraft.font.split(net.minecraft.network.chat.Component.translatable(tooltipTranslationKey), 2147483647)
                              );
                           }

                           if (option.backingField().hasAnnotation(RestartRequired.class)) {
                              tooltipText.add(net.minecraft.network.chat.Component.translatable("text.owo.config.applies_after_restart").getVisualOrderText());
                           }

                           if (!tooltipText.isEmpty()) {
                              result.baseComponent().tooltip(tooltipText.stream().<ClientTooltipComponent>map(ClientTooltipComponent::create).toList());
                           }
                        }

                        if (option.backingField().hasAnnotation(SectionHeader.class)) {
                           this.appendSection(sections, option.backingField().field(), container);
                        }

                        container.child(result.baseComponent());
                     }
                  }
               }
            }
         );
      if (!sections.isEmpty()) {
         FlowLayout panelContainer = rootComponent.childById(FlowLayout.class, "option-panel-container");
         ScrollContainer panelScroll = rootComponent.childById(ScrollContainer.class, "option-panel-scroll");
         panelScroll.margins(Insets.right(10));
         FlowLayout buttonPanel = this.model.expandTemplate(FlowLayout.class, "section-buttons", Map.of());
         sections.forEach((component, text) -> {
            MutableComponent hoveredText = text.copy().withStyle(ChatFormatting.YELLOW);
            LabelComponent label = Components.label(text);
            label.cursorStyle(CursorStyle.HAND).margins(Insets.of(2));
            label.mouseEnter().subscribe(() -> label.text(hoveredText));
            label.mouseLeave().subscribe(() -> label.text(text));
            label.mouseDown().subscribe((mouseX, mouseY, button) -> {
               panelScroll.scrollTo(component);
               UISounds.playInteractionSound();
               return true;
            });
            buttonPanel.child(label);
         });
         LabelComponent closeButton = Components.label(net.minecraft.network.chat.Component.literal("<").withStyle(ChatFormatting.BOLD));
         closeButton.tooltip(net.minecraft.network.chat.Component.translatable("text.owo.config.sections_tooltip"));
         closeButton.positioning(Positioning.relative(100, 50)).cursorStyle(CursorStyle.HAND).margins(Insets.right(2));
         panelContainer.child(closeButton);
         panelContainer.mouseDown()
            .subscribe(
               (mouseX, mouseY, button) -> {
                  if (mouseX < panelContainer.width() - 10) {
                     return false;
                  } else {
                     if (buttonPanel.horizontalSizing().animation() == null) {
                        buttonPanel.horizontalSizing().animate(350, Easing.CUBIC, Sizing.content());
                     }

                     buttonPanel.horizontalSizing().animation().reverse();
                     closeButton.text(
                        net.minecraft.network.chat.Component.literal(closeButton.text().getString().equals(">") ? "<" : ">").withStyle(ChatFormatting.BOLD)
                     );
                     UISounds.playInteractionSound();
                     return true;
                  }
               }
            );
         rootComponent.childById(FlowLayout.class, "main-panel").child(buttonPanel);
      }
   }

   protected void appendSection(Map<Component, net.minecraft.network.chat.Component> sections, Field field, FlowLayout container) {
      String translationKey = "text.config." + this.config.name() + ".section." + field.getAnnotation(SectionHeader.class).value();
      FlowLayout header = this.model.expandTemplate(FlowLayout.class, "section-header", Map.of());
      header.childById(LabelComponent.class, "header")
         .configure(
            label -> {
               label.text(
                  net.minecraft.network.chat.Component.translatable(translationKey).withStyle(new ChatFormatting[]{ChatFormatting.YELLOW, ChatFormatting.BOLD})
               );
               header.child(new SearchAnchorComponent(header, Option.Key.ROOT, () -> label.text().getString()));
            }
         );
      sections.put(header, net.minecraft.network.chat.Component.translatable(translationKey));
      container.child(header);
   }

   protected List<SearchAnchorComponent> collectSearchAnchors(ParentComponent root) {
      ArrayList<SearchAnchorComponent> discovered = new ArrayList<>();
      ArrayDeque<Component> candidates = new ArrayDeque<>(root.children());

      while (!candidates.isEmpty()) {
         Component candidate = candidates.poll();
         if (candidate instanceof CollapsibleContainer collapsible) {
            candidates.addAll(collapsible.children());
            if (!collapsible.expanded()) {
               candidates.addAll(collapsible.collapsibleChildren());
            }
         } else if (candidate instanceof ParentComponent parentComponent) {
            candidates.addAll(parentComponent.children());
         } else if (candidate instanceof SearchAnchorComponent anchor) {
            discovered.add(anchor);
         }
      }

      return discovered;
   }

   @Override
   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (keyCode == 70 && (modifiers & 2) != 0) {
         this.uiAdapter
            .rootComponent
            .focusHandler()
            .focus(this.uiAdapter.rootComponent.childById(Component.class, "search-field"), Component.FocusSource.MOUSE_CLICK);
         return true;
      } else {
         return super.keyPressed(keyCode, scanCode, modifiers);
      }
   }

   public void onClose() {
      MutableBoolean shouldRestart = new MutableBoolean();
      this.options.forEach((option, component) -> {
         if (option.backingField().hasAnnotation(RestartRequired.class)) {
            if (!Objects.equals(option.value(), component.parsedValue())) {
               shouldRestart.setTrue();
            }
         }
      });
      this.minecraft.setScreen((Screen)(shouldRestart.booleanValue() ? new RestartRequiredScreen(this.parent) : this.parent));
   }

   @Override
   public void removed() {
      this.options.forEach((option, component) -> {
         if (component.isValid()) {
            option.set(component.parsedValue());
         }
      });
      super.removed();
   }

   @Nullable
   protected OptionComponentFactory factoryForOption(Option<?> option) {
      for (Predicate<Option<?>> predicate : this.extraFactories.keySet()) {
         if (predicate.test(option)) {
            return this.extraFactories.get(predicate);
         }
      }

      for (Predicate<Option<?>> predicatex : DEFAULT_FACTORIES.keySet()) {
         if (predicatex.test(option)) {
            return DEFAULT_FACTORIES.get(predicatex);
         }
      }

      return null;
   }

   private static boolean isStringOrNumberList(Field field) {
      if (field.getType() != List.class) {
         return false;
      } else {
         Class<?> listType = ReflectionUtils.getTypeArgument(field.getGenericType(), 0);
         return listType == null ? false : String.class == listType || NumberReflection.isNumberType(listType);
      }
   }

   static {
      DEFAULT_FACTORIES.put(option -> NumberReflection.isNumberType(option.clazz()), OptionComponentFactory.NUMBER);
      DEFAULT_FACTORIES.put(option -> option.clazz() == String.class, OptionComponentFactory.STRING);
      DEFAULT_FACTORIES.put(option -> option.clazz() == Boolean.class || option.clazz() == boolean.class, OptionComponentFactory.BOOLEAN);
      DEFAULT_FACTORIES.put(option -> option.clazz() == ResourceLocation.class, OptionComponentFactory.IDENTIFIER);
      DEFAULT_FACTORIES.put(option -> option.clazz() == Color.class, OptionComponentFactory.COLOR);
      DEFAULT_FACTORIES.put(option -> isStringOrNumberList(option.backingField().field()), OptionComponentFactory.LIST);
      DEFAULT_FACTORIES.put(option -> option.clazz().isEnum(), OptionComponentFactory.ENUM);
      UIParsing.registerFactory("config-slider", element -> new ConfigSlider());
      UIParsing.registerFactory("config-toggle-button", element -> new ConfigToggleButton());
      UIParsing.registerFactory("config-enum-button", element -> new ConfigEnumButton());
      UIParsing.registerFactory("config-text-box", element -> new ConfigTextBox());
   }

   public static class SearchHighlighterComponent extends BaseComponent {
      private final Color startColor = Color.ofArgb(9280480);
      private final Color endColor = Color.ofArgb(1284348896);
      private float age = 0.0F;

      public SearchHighlighterComponent() {
         this.positioning(Positioning.absolute(0, 0));
         this.sizing(Sizing.fill(100), Sizing.fill(100));
      }

      @Override
      public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
         int mainColor = this.startColor.interpolate(this.endColor, (float)Math.sin(this.age / 25.0F * 3.141592653589793)).argb();
         int segmentWidth = (int)(this.width * 0.3F);
         int baseX = (int)(this.x - segmentWidth + Easing.CUBIC.apply(this.age / 25.0F) * (this.width + segmentWidth * 2));
         context.drawGradientRect(baseX - segmentWidth, this.y, segmentWidth, this.height, 0, mainColor, mainColor, 0);
         context.drawGradientRect(baseX, this.y, segmentWidth, this.height, mainColor, 0, 0, mainColor);
      }

      @Override
      public void update(float delta, int mouseX, int mouseY) {
         super.update(delta, mouseX, mouseY);
         if ((this.age += delta) > 25.0F) {
            this.parent.queue(() -> this.parent.removeChild(this));
         }
      }
   }

   protected record SearchMatches(String query, List<SearchAnchorComponent> matches) {
   }
}
