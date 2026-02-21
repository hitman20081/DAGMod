package com.github.hitman20081.dagmod.trade;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Random;

/**
 * Handles merchant dialogue/flavor text.
 * Displays greeting messages when players open trade screens.
 */
public class MerchantDialogue {
    private static final Random RANDOM = new Random();

    // ==================== ARMORER DIALOGUE ====================
    private static final List<String> ARMORER_GREETINGS = List.of(
            "Welcome! I've got the finest armor in all the land.",
            "Need protection? You've come to the right place.",
            "My armor has saved many an adventurer's life.",
            "Step right up! Quality armor for quality warriors."
    );

    private static final List<String> ARMORER_ROTATION_HINTS = List.of(
            "Check out my %s collection - finest craftsmanship!",
            "Today I'm featuring %s. Limited stock!",
            "Just got a new shipment of %s in!"
    );

    // ==================== MYSTERY MERCHANT DIALOGUE ====================
    private static final List<String> MYSTERY_MERCHANT_GREETINGS = List.of(
            "Ah, a discerning customer... I have what you seek.",
            "Rare weapons for those with coin...",
            "Not everything I sell can be found elsewhere...",
            "Step closer... I have treasures untold."
    );

    private static final List<String> MYSTERY_MERCHANT_ROTATION_HINTS = List.of(
            "My %s are particularly... potent today.",
            "I've acquired some exceptional %s recently.",
            "For those seeking power, I offer %s."
    );

    // ==================== ENCHANTSMITH DIALOGUE ====================
    private static final List<String> ENCHANTSMITH_GREETINGS = List.of(
            "The arcane arts await! What enchantment do you seek?",
            "I can imbue your gear with magical power.",
            "Knowledge is power, and I deal in both.",
            "Welcome, seeker of magical enhancement."
    );

    private static final List<String> ENCHANTSMITH_ROTATION_HINTS = List.of(
            "My specialty today: %s. Very powerful!",
            "I've prepared some excellent %s for sale.",
            "Looking for %s? You're in luck!"
    );

    // ==================== VOODOO ILLUSIONER DIALOGUE ====================
    private static final List<String> VOODOO_ILLUSIONER_GREETINGS = List.of(
            "The spirits whisper of your arrival...",
            "Dark secrets can be yours... for a price.",
            "I deal in things others fear to touch.",
            "Welcome to the shadows, traveler."
    );

    private static final List<String> VOODOO_ILLUSIONER_ROTATION_HINTS = List.of(
            "The spirits have guided me to prepare %s...",
            "For those seeking change, I offer %s.",
            "My %s are particularly potent during this moon phase."
    );

    // ==================== TROPHY DEALER DIALOGUE ====================
    private static final List<String> TROPHY_DEALER_GREETINGS = List.of(
            "Trophies of the mightiest beasts! Care to browse?",
            "Only the rarest treasures find their way here.",
            "Heroes deserve legendary rewards.",
            "The spoils of victory await!"
    );

    private static final List<String> TROPHY_DEALER_ROTATION_HINTS = List.of(
            "I've acquired some magnificent %s!",
            "Fresh %s from brave adventurers!",
            "My current collection features %s."
    );

    // ==================== MINER DIALOGUE ====================
    private static final List<String> MINER_GREETINGS = List.of(
            "Ores and gems, fresh from the depths!",
            "Need mining supplies? I'm your dwarf.",
            "The earth yields its treasures to those who dig deep.",
            "Welcome, fellow miner!"
    );

    private static final List<String> MINER_ROTATION_HINTS = List.of(
            "Just processed a batch of rare %s!",
            "Today's special: %s at excellent prices!",
            "I've got %s that you won't find elsewhere."
    );

    // ==================== HUNTER DIALOGUE ====================
    private static final List<String> HUNTER_GREETINGS = List.of(
            "Bows, arrows, and the finest leather goods!",
            "The hunt never ends for a true ranger.",
            "Track your prey, and claim your prize.",
            "Welcome, fellow hunter!"
    );

    private static final List<String> HUNTER_ROTATION_HINTS = List.of(
            "I've stocked up on %s today!",
            "For the discerning hunter: %s!",
            "Just acquired some excellent %s."
    );

    // ==================== LUMBERJACK DIALOGUE ====================
    private static final List<String> LUMBERJACK_GREETINGS = List.of(
            "Wood, tools, and nature's bounty!",
            "The forest provides for those who respect it.",
            "Timber! And everything else you might need.",
            "Welcome to my shop, friend!"
    );

    private static final List<String> LUMBERJACK_ROTATION_HINTS = List.of(
            "I've got some special %s in stock!",
            "Today's featured items: %s!",
            "Just got a shipment of %s."
    );

    /**
     * Send a greeting message to the player when opening the trade screen.
     *
     * @param player The player opening the trade screen
     * @param type The merchant type
     */
    public static void sendGreeting(ServerPlayerEntity player, MerchantType type) {
        String greeting = getRandomGreeting(type);
        String merchantName = getMerchantDisplayName(type);

        // Format as chat message: <Merchant Name> message
        Text message = Text.literal("<" + merchantName + "> ")
                .formatted(Formatting.GOLD)
                .append(Text.literal(greeting).formatted(Formatting.YELLOW));

        player.sendMessage(message, false);
    }

    /**
     * Send a rotation hint message to the player.
     * This tells them about the current rotating stock.
     *
     * @param player The player
     * @param type The merchant type
     */
    public static void sendRotationHint(ServerPlayerEntity player, MerchantType type) {
        int rotationIndex = RotatingTradeManager.getInstance().getRotationIndex(type);
        String rotationDesc = RotatingTradeRegistry.getRotationDescription(type, rotationIndex);
        String hint = getRandomRotationHint(type, rotationDesc);
        String merchantName = getMerchantDisplayName(type);

        // Only send hint sometimes (30% chance)
        if (RANDOM.nextFloat() < 0.3f) {
            Text message = Text.literal("<" + merchantName + "> ")
                    .formatted(Formatting.GOLD)
                    .append(Text.literal(hint).formatted(Formatting.AQUA));

            player.sendMessage(message, false);
        }
    }

    private static String getRandomGreeting(MerchantType type) {
        List<String> greetings = switch (type) {
            case ARMORER -> ARMORER_GREETINGS;
            case MYSTERY_MERCHANT -> MYSTERY_MERCHANT_GREETINGS;
            case ENCHANTSMITH -> ENCHANTSMITH_GREETINGS;
            case VOODOO_ILLUSIONER -> VOODOO_ILLUSIONER_GREETINGS;
            case TROPHY_DEALER -> TROPHY_DEALER_GREETINGS;
            case MINER -> MINER_GREETINGS;
            case HUNTER -> HUNTER_GREETINGS;
            case LUMBERJACK -> LUMBERJACK_GREETINGS;
        };
        return greetings.get(RANDOM.nextInt(greetings.size()));
    }

    private static String getRandomRotationHint(MerchantType type, String rotationDesc) {
        List<String> hints = switch (type) {
            case ARMORER -> ARMORER_ROTATION_HINTS;
            case MYSTERY_MERCHANT -> MYSTERY_MERCHANT_ROTATION_HINTS;
            case ENCHANTSMITH -> ENCHANTSMITH_ROTATION_HINTS;
            case VOODOO_ILLUSIONER -> VOODOO_ILLUSIONER_ROTATION_HINTS;
            case TROPHY_DEALER -> TROPHY_DEALER_ROTATION_HINTS;
            case MINER -> MINER_ROTATION_HINTS;
            case HUNTER -> HUNTER_ROTATION_HINTS;
            case LUMBERJACK -> LUMBERJACK_ROTATION_HINTS;
        };
        String template = hints.get(RANDOM.nextInt(hints.size()));
        return String.format(template, rotationDesc);
    }

    private static String getMerchantDisplayName(MerchantType type) {
        return switch (type) {
            case ARMORER -> "Armorer";
            case MYSTERY_MERCHANT -> "Mystery Merchant";
            case ENCHANTSMITH -> "Enchantsmith";
            case VOODOO_ILLUSIONER -> "Voodoo Illusioner";
            case TROPHY_DEALER -> "Trophy Dealer";
            case MINER -> "Miner";
            case HUNTER -> "Hunter";
            case LUMBERJACK -> "Lumberjack";
        };
    }
}
