/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.Resource
 *  net.minecraft.server.packs.resources.ResourceManager
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package traben.entity_texture_features.features.property_reading;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.ETFApi;
import traben.entity_texture_features.ETFException;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.property_reading.RandomPropertyRule;
import traben.entity_texture_features.features.property_reading.properties.RandomProperties;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.SimpleIntegerArrayProperty;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.features.texture_handlers.ETFDirectory;
import traben.entity_texture_features.utils.ETFLruCache;
import traben.entity_texture_features.utils.ETFUtils2;

public class PropertiesRandomProvider
implements ETFApi.ETFVariantSuffixProvider {
    protected final List<RandomPropertyRule> propertyRules;
    protected final ETFLruCache.UUIDBoolean entityCanUpdate = new ETFLruCache.UUIDBoolean();
    @NotNull
    protected final String packname;
    protected BiConsumer<ETFEntityRenderState, @Nullable RandomPropertyRule> onMeetsRule = (entity, rule) -> {};

    private PropertiesRandomProvider(ResourceLocation propertiesFileIdentifier, List<RandomPropertyRule> propertyRules) {
        this.propertyRules = propertyRules;
        this.packname = Minecraft.getInstance().getResourceManager().getResource(propertiesFileIdentifier).map(Resource::sourcePackId).orElse("vanilla");
    }

    @Nullable
    public static PropertiesRandomProvider of(ResourceLocation initialPropertiesFileIdentifier, ResourceLocation vanillaIdentifier, String ... suffixKeyName) {
        ResourceLocation propertiesFileIdentifier = ETFDirectory.getDirectoryVersionOf(initialPropertiesFileIdentifier);
        if (propertiesFileIdentifier == null) {
            return null;
        }
        try {
            List<RandomPropertyRule> propertyRules;
            Properties props = ETFUtils2.readAndReturnPropertiesElseNull(propertiesFileIdentifier);
            if (props == null) {
                ETFUtils2.logMessage("Ignoring properties file that was null @ " + String.valueOf(propertiesFileIdentifier), false);
                return null;
            }
            if (vanillaIdentifier.getPath().endsWith(".png")) {
                ETFManager.getInstance().grabSpecialProperties(props, ETFRenderContext.getCurrentEntityState());
            }
            if ((propertyRules = PropertiesRandomProvider.getAllValidPropertyObjects(props, propertiesFileIdentifier, suffixKeyName)).isEmpty()) {
                ETFUtils2.logMessage("Ignoring properties file that failed to load any cases @ " + String.valueOf(propertiesFileIdentifier), false);
                return null;
            }
            if (!propertyRules.get(propertyRules.size() - 1).isAlwaysMet()) {
                propertyRules.add(RandomPropertyRule.DEFAULT_RETURN);
            }
            ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
            String properties = resourceManager.getResource(propertiesFileIdentifier).map(Resource::sourcePackId).orElse(null);
            String vanillaPack = resourceManager.getResource(vanillaIdentifier).map(Resource::sourcePackId).orElse(null);
            if (properties != null && properties.equals(ETFUtils2.returnNameOfHighestPackFromTheseTwo(properties, vanillaPack))) {
                return new PropertiesRandomProvider(propertiesFileIdentifier, propertyRules);
            }
        }
        catch (ETFException etf) {
            if (!propertiesFileIdentifier.toString().contains("optifine/cit/")) {
                ETFUtils2.logWarn("Ignoring properties file with problem: " + String.valueOf(propertiesFileIdentifier) + "\n" + String.valueOf(etf), false);
            }
        }
        catch (Exception e) {
            ETFUtils2.logWarn("Ignoring properties file that caused unexpected Exception: " + String.valueOf(propertiesFileIdentifier) + "\n" + String.valueOf(e), false);
            e.printStackTrace();
        }
        return null;
    }

    public static List<RandomPropertyRule> getAllValidPropertyObjects(Properties properties, ResourceLocation propertiesFilePath, String ... suffixToTest) throws ETFException {
        List<Integer> numbersList = PropertiesRandomProvider.getCaseNumbers(properties.stringPropertyNames());
        PropertiesRandomProvider.validateRuleNumbers(propertiesFilePath, numbersList);
        ArrayList<RandomPropertyRule> allRulesOfProperty = new ArrayList<RandomPropertyRule>();
        for (Integer ruleNumber : numbersList) {
            Integer[] suffixesOfRule = PropertiesRandomProvider.getSuffixes(properties, ruleNumber, suffixToTest);
            if (suffixesOfRule != null && suffixesOfRule.length != 0) {
                allRulesOfProperty.add(new RandomPropertyRule(propertiesFilePath.toString(), ruleNumber, suffixesOfRule, PropertiesRandomProvider.getWeights(properties, ruleNumber), PropertiesRandomProvider.getSeedOffset(properties, ruleNumber), properties.getProperty("seedSource." + ruleNumber), RandomProperties.getAllRegisteredRandomPropertiesOfIndex(properties, ruleNumber)));
                continue;
            }
            ETFUtils2.logWarn("property number \"" + ruleNumber + ". in file \"" + String.valueOf(propertiesFilePath) + ". failed to read.");
        }
        return allRulesOfProperty;
    }

    private static int getSeedOffset(Properties props, int num) {
        String seedOffsetStr = props.getProperty("seedOffset." + num);
        if (seedOffsetStr != null) {
            try {
                return Integer.parseInt(seedOffsetStr);
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        return 0;
    }

    private static void validateRuleNumbers(ResourceLocation propertiesFilePath, List<Integer> numbersList) {
        if (numbersList.isEmpty()) {
            ETFUtils2.logWarn("Properties file [" + String.valueOf(propertiesFilePath) + "] contains no rules, this is invalid.", false);
            throw new ETFException("Properties file [" + String.valueOf(propertiesFilePath) + "] contains no rules, this is invalid.");
        }
        if (numbersList.get(0) < 1) {
            ETFUtils2.logWarn("Properties file [" + String.valueOf(propertiesFilePath) + "] contains rule numbers less than 1, this is invalid.", false);
            throw new ETFException("Properties file [" + String.valueOf(propertiesFilePath) + "] contains rule numbers less than 1, this is invalid.");
        }
        int last = 0;
        for (Integer i : numbersList) {
            if (i >= last + 10) {
                last = -1;
                break;
            }
            last = i;
        }
        if (last == -1) {
            ETFUtils2.logWarn("Properties file [" + String.valueOf(propertiesFilePath) + "] has skipped rule numbers by values greater than 10, this is invalid in older OptiFine. This limitation has been disabled in ETF's settings, your pack is incompatible with older OptiFine.", false);
        }
    }

    @NotNull
    private static List<Integer> getCaseNumbers(Set<String> propIds) {
        HashSet<Integer> foundRuleNumbers = new HashSet<Integer>();
        for (String str : propIds) {
            String possibleRuleNumber;
            String[] split = str.split("\\.");
            if (split.length < 2 || split[1].isBlank() || (possibleRuleNumber = split[1].replaceAll("\\D", "")).isBlank()) continue;
            try {
                foundRuleNumbers.add(Integer.parseInt(possibleRuleNumber));
            }
            catch (NumberFormatException numberFormatException) {}
        }
        return foundRuleNumbers.stream().sorted().toList();
    }

    @Nullable
    private static Integer[] getSuffixes(Properties props, int num, String ... suffixToTest) throws ETFException {
        Object[] suffixes = SimpleIntegerArrayProperty.getGenericIntegerSplitWithRanges(props, num, suffixToTest);
        if (suffixes != null) {
            for (Integer n : suffixes) {
                if (n >= 1) continue;
                throw new ETFException("Invalid suffix: [" + n + "] in " + Arrays.toString(suffixes));
            }
        }
        return suffixes;
    }

    @Nullable
    private static Integer[] getWeights(Properties props, int num) {
        return SimpleIntegerArrayProperty.getGenericIntegerSplitWithRanges(props, num, "weights");
    }

    public void setOnMeetsRuleHook(BiConsumer<ETFEntityRenderState, RandomPropertyRule> onMeetsRule) {
        if (onMeetsRule != null) {
            this.onMeetsRule = onMeetsRule;
        }
    }

    @NotNull
    public String getPackName() {
        return this.packname;
    }

    public boolean isHigherPackThan(@Nullable String packNameOther) {
        return this.packname.equals(ETFUtils2.returnNameOfHighestPackFromTheseTwo(this.packname, packNameOther));
    }

    @Override
    public boolean entityCanUpdate(UUID uuid) {
        return (Boolean)this.entityCanUpdate.get(uuid);
    }

    public Set getAllSuffixes() {
        HashSet<Integer> allSuffixes = new HashSet<Integer>();
        for (RandomPropertyRule rule : this.propertyRules) {
            allSuffixes.addAll(rule.getSuffixSet());
        }
        return allSuffixes;
    }

    @Override
    public int size() {
        return this.propertyRules.size();
    }

    @Override
    public int getSuffixForETFEntity(ETFEntityRenderState entityToBeTested) {
        if (entityToBeTested == null) {
            return 0;
        }
        UUID id = entityToBeTested.uuid();
        boolean entityHasBeenTestedBefore = this.entityCanUpdate.containsKey(id);
        int result = this.testEntityAgainstRules(entityToBeTested, entityHasBeenTestedBefore);
        if (!entityHasBeenTestedBefore && ((Boolean)this.entityCanUpdate.get(id)).booleanValue()) {
            for (RandomPropertyRule rule : this.propertyRules) {
                rule.cacheEntityInitialResultsOfNonUpdatingProperties(entityToBeTested);
            }
        }
        if (result > 0) {
            return result;
        }
        this.onMeetsRule.accept(entityToBeTested, null);
        return 0;
    }

    private int testEntityAgainstRules(ETFEntityRenderState entityToBeTested, boolean isUpdate) {
        for (RandomPropertyRule rule : this.propertyRules) {
            if (!rule.doesEntityMeetConditionsOfThisCase(entityToBeTested, isUpdate, this.entityCanUpdate)) continue;
            this.onMeetsRule.accept(entityToBeTested, rule);
            return rule.getVariantSuffixFromThisCase(entityToBeTested);
        }
        return 0;
    }
}

