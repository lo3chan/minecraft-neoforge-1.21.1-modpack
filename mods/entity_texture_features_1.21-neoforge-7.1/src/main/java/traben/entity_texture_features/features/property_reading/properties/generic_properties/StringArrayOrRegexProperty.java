/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package traben.entity_texture_features.features.property_reading.properties.generic_properties;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFUtils2;

public abstract class StringArrayOrRegexProperty
extends RandomProperty {
    protected static final Pattern GROUP_BY_QUOTATION_PATTERN = Pattern.compile("([^\"]\\S*|\".+?\")\\s*");
    protected final String ORIGINAL_INPUT;
    protected final Set<String> ARRAY;
    protected final RegexAndPatternPropertyMatcher MATCHER;
    protected final boolean usesRegex;
    protected final boolean doPrint;

    protected StringArrayOrRegexProperty(String stringInput) throws RandomProperty.RandomPropertyException {
        String testString;
        if (stringInput == null || stringInput.isBlank()) {
            throw new RandomProperty.RandomPropertyException(this.getPropertyId() + " property was broken");
        }
        this.ORIGINAL_INPUT = stringInput;
        this.doPrint = stringInput.startsWith("print:");
        String string = testString = this.doPrint ? stringInput.substring(6) : stringInput;
        if (testString.startsWith("regex:") || testString.startsWith("pattern:") || testString.startsWith("iregex:") || testString.startsWith("ipattern:")) {
            this.MATCHER = StringArrayOrRegexProperty.getStringMatcher_Regex_Pattern_List_Single(testString);
            this.ARRAY = Set.of(testString);
            this.usesRegex = true;
        } else {
            String[] array = testString.trim().split("\\s+");
            if (array.length == 0) {
                throw new RandomProperty.RandomPropertyException(this.getPropertyId() + " property was broken");
            }
            this.ARRAY = new HashSet<String>();
            for (String str : array) {
                this.ARRAY.add(this.shouldForceLowerCaseCheck() ? str.toLowerCase() : str);
            }
            if (array.length != 1) {
                this.ARRAY.add(testString.trim());
            }
            this.MATCHER = this::testArray;
            this.usesRegex = false;
        }
    }

    @Nullable
    public static RegexAndPatternPropertyMatcher getStringMatcher_Regex_Pattern_List_Single(@Nullable String propertyLine) {
        if (propertyLine == null || propertyLine.isBlank()) {
            return null;
        }
        String stringToMatch = propertyLine.trim();
        boolean invert = stringToMatch.startsWith("!");
        if (invert) {
            stringToMatch = stringToMatch.substring(1);
        }
        if (stringToMatch.startsWith("regex:") || stringToMatch.startsWith("iregex:")) {
            boolean ignoreCase = stringToMatch.startsWith("i");
            String finalStringToMatch = stringToMatch.replaceFirst("iregex:|regex:", "");
            Pattern regex = Pattern.compile((String)(ignoreCase ? "(?i)" + finalStringToMatch : finalStringToMatch));
            return string -> invert != regex.matcher(string).matches();
        }
        if (stringToMatch.startsWith("pattern:") || stringToMatch.startsWith("ipattern:")) {
            boolean ignoreCase = stringToMatch.startsWith("i");
            stringToMatch = stringToMatch.replaceFirst("ipattern:|pattern:", "").replace("*", "\\E.*\\Q").replace("?", "\\E.\\Q");
            String finalStringToMatch = "\\Q" + stringToMatch + "\\E";
            Pattern regex = Pattern.compile(ignoreCase ? "(?i)" + finalStringToMatch : finalStringToMatch);
            return string -> invert != regex.matcher(string).matches();
        }
        String[] splitMatches = stringToMatch.split("\\s+");
        boolean hasQuotes = stringToMatch.contains("\"");
        String finalString = stringToMatch;
        return string -> {
            boolean matchFound;
            boolean bl = matchFound = string.equals(finalString) || Arrays.asList(splitMatches).contains(string);
            if (!matchFound && hasQuotes) {
                Matcher m = GROUP_BY_QUOTATION_PATTERN.matcher(finalString);
                while (m.find()) {
                    if (!string.equals(m.group(1).replace("\"", "").trim())) continue;
                    matchFound = true;
                    break;
                }
            }
            return invert != matchFound;
        };
    }

    private boolean testArray(String fromEntity) {
        boolean matches = false;
        for (String string : this.ARRAY) {
            if (string.startsWith("!")) {
                matches = true;
                if (!string.substring(1).equals(fromEntity)) continue;
                return false;
            }
            if (!string.equals(fromEntity)) continue;
            return true;
        }
        return matches;
    }

    @Override
    public boolean testEntityInternal(ETFEntityRenderState entity) {
        String entityString = this.getValueFromEntity(entity);
        if (entityString != null) {
            boolean test = this.MATCHER.testString(this.shouldForceLowerCaseCheck() ? entityString.toLowerCase() : entityString);
            if (this.doPrint) {
                ETFUtils2.logMessage(this.getPropertyId() + " property value print: [" + entityString + "], returned: " + test + ", can update: " + this.canPropertyUpdate());
            }
            return test;
        }
        if (this.doPrint) {
            ETFUtils2.logMessage(this.getPropertyId() + " property value print: [<null>], returned: false, can update: " + this.canPropertyUpdate());
        }
        return false;
    }

    protected abstract boolean shouldForceLowerCaseCheck();

    @Nullable
    protected abstract String getValueFromEntity(ETFEntityRenderState var1);

    @Override
    protected String getPrintableRuleInfo() {
        return this.ORIGINAL_INPUT;
    }

    public static interface RegexAndPatternPropertyMatcher {
        public boolean testString(String var1);
    }
}

