# Mixins

This document provides an overview of how Mixins are used in DAGMod for advanced users and modders.

## Introduction to Mixins

Mixins are a mechanism for modifying vanilla Minecraft classes at runtime without directly changing the source code. DAGMod uses Sponge Mixins (via Fabric) to hook into vanilla behavior for class abilities, progression, enchantments, and more.

All mixins are registered in `src/main/resources/dagmod.mixins.json`.

---

## Server Mixins

### LivingEntityMixin
**Target:** `LivingEntity`
**Purpose:** Awards DAGMod XP when players kill mobs. Injects at the `onDeath` method HEAD to detect player-caused kills and route them to `XPEventHandler.onMobKilled()`.

### RogueDamageMixin
**Target:** `LivingEntity`
**Purpose:** Modifies outgoing damage for Rogue backstab mechanics and weapon synergy bonuses. Also reduces fall damage for Rogues. Uses `@ModifyVariable` on the `damage` method to adjust the damage value before it is applied.

### MageStatusEffectMixin
**Target:** `LivingEntity`
**Purpose:** Handles Mage-specific status effect interactions, such as spell-enhanced effects and mana-related mechanics.

### RaceMiningMixin
**Target:** Mining-related vanilla class
**Purpose:** Implements Dwarf racial mining bonuses (+20% mining speed). Modifies block break speed based on the player's selected race.

### RaceHuntingMixin
**Target:** Hunting/gathering-related vanilla class
**Purpose:** Implements Orc hunting bonuses and Elf gathering bonuses for race-specific loot advantages.

### HumanExperienceMixin
**Target:** Experience-related vanilla class
**Purpose:** Implements the Human racial bonus of +25% XP gain from all sources. Multiplies incoming XP before it reaches the progression system.

### ShieldBlockingMixin
**Target:** `LivingEntity`
**Purpose:** Extends shield blocking to accept any `ShieldItem` (not just vanilla shield) for custom DAGMod shields. Also applies the Shatterproof broken-state debuff: shields at 1 durability with Shatterproof have a 99% chance to fail blocking.

### ShatterproofMixin
**Target:** `ItemStack`
**Purpose:** Implements the Shatter Proof enchantment. Intercepts `setDamage()` and caps durability damage at `maxDamage - 1` for items with the `dagmod:shatterproof` enchantment, preventing them from ever breaking.

### ShatterproofDamageMixin
**Target:** `LivingEntity`
**Purpose:** Applies the Shatterproof broken-state debuff to weapons. When a player attacks with a weapon at 1 durability (saved by Shatterproof), outgoing damage is reduced by 99%.

### ShatterproofMiningMixin
**Target:** `PlayerEntity`
**Purpose:** Applies the Shatterproof broken-state debuff to tools. When a player mines with a tool at 1 durability (saved by Shatterproof), mining speed is reduced by 99%.

### SoulBoundMixin
**Target:** `ServerPlayerEntity`
**Purpose:** Implements the Soul Bound enchantment. Injects at the `onDeath` method HEAD (default priority) to scan the player's inventory for items with `dagmod:soul_bound`, removes them before they drop, and stores them in `SoulBoundStorage`. Items are returned on respawn via the `AFTER_RESPAWN` event handler in `DagMod.java`.

### DeathGraveMixin
**Target:** `ServerPlayerEntity` (priority 1100)
**Purpose:** Implements the death recovery (grave) system. Injects at `onDeath` HEAD after SoulBoundMixin (which runs at default priority) has already removed soulbound items. Captures all remaining non-empty inventory items with their slot indices, clears the inventory so vanilla `dropAll()` drops nothing, then calls `GraveManager.createGrave()` to place a Lodestone grave block at the death location and persist the items to disk. Skips entirely if `keepInventory` gamerule is enabled.

### LifestealMixin
**Target:** `LivingEntity`
**Purpose:** Grants 10% lifesteal to Vampire Dust users on a successful hit, capped at 2.5 hearts (5.0 HP) per hit. Injects at the `RETURN` of `hurtServer`.

### DodgeMixin
**Target:** `LivingEntity`
**Purpose:** Implements the real dodge mechanic behind Phantom Dust (50% chance), Perfect Dodge (100% chance), and the Rogue's passive Shadow Step enchantment. Injects at `HEAD` of `hurtServer`, cancellable — runs before `LastStandMixin`. A successful dodge plays particles, an action-bar message, and *(as of the v2.0.0 working tree)* an Enderman-teleport sound.

### LastStandMixin
**Target:** `LivingEntity`
**Purpose:** Implements Last Stand Powder: prevents one otherwise-lethal hit and heals the player to 50% HP instead. Also covers void deaths, teleporting the player to the surface. Injects at `HEAD` of `hurtServer`.

### AnvilRaceClassGateMixin / EnchantmentTableRaceClassGateMixin
**Target:** `AnvilMenu` / `EnchantmentMenu`
**Purpose:** Enforce race/class-gated enchantments (see `RaceClassEnchantmentGate`) at the only two places an enchantment actually gets written onto an item — taking the result from an anvil, and taking the result from an enchanting table. Strips the enchantment before it reaches the player's inventory if they don't qualify.

### RaceEnchantmentCombatMixin
**Target:** `LivingEntity`
**Purpose:** Attacker-side damage multiplier hook powering the Deep Striker, Forest's Blessing, and Berserker's Fury race enchantments — mirrors `RogueDamageMixin`'s `hurtServer` hook, but checks the attacker instead of the defender.

### AnvilCostCapMixin
**Target:** `AnvilMenu`
**Purpose:** Removes vanilla's "Too Expensive!" 40-level cap on the anvil. `ModifyConstant`s the literal `40` in `createResult()` to `Integer.MAX_VALUE` so the result is never nulled out for exceeding it — `onTake()` already deducts the true XP cost with no cap of its own.

### EnchantmentTableBookshelfCapMixin
**Target:** `EnchantmentMenu`
**Purpose:** Removes the enchanting table's bookshelf requirement by forcing the "power" value fed into `EnchantmentHelper.getEnchantmentCost()` to 15 (vanilla's own maximum) regardless of how many bookshelves actually surround the table.

### MerchantCoinTopUpMixin
**Target:** `MerchantMenu`
**Purpose:** *(new in v2.0.0)* Vanilla's `tryMoveItems` only pulls a trade's cost from physical coin ItemStacks already sitting in the player's inventory slots. This mixin lets a trade also draw against the player's Coin Pouch balance, minting physical coins into the trade on the fly so pouch-only currency still works at merchant trade windows.

---

## Client Mixins

### DynamicLightMixin
**Purpose:** Injects into `BlockAndLightGetter.getLightLevel()` to make held light-emitting items illuminate the world dynamically, without placing an actual light-source block.

### ChestRenderStateMixin
**Purpose:** Modifies chest rendering state for locked Bone Realm chests to display a unique texture.

### LockedChestTextureMixin
**Purpose:** Two-part mixin (`ChestRendererMixin` + `TexturedRenderLayersMixin`) that registers and applies custom locked chest textures for the Bone Realm boss chests.

### EnchantmentDescriptionMixin
**Purpose:** Renders an in-tooltip flavor description for DAGMod's custom enchantments, matching vanilla enchantments' own tooltip text.

### AnvilTooExpensiveLabelMixin
**Purpose:** Client-side counterpart to `AnvilCostCapMixin` — `AnvilScreen` independently re-checks the same "cost >= 40" condition purely to decide the "Too Expensive!" label's text/color, so without this fix the label kept rendering red even after the server stopped actually blocking the take.

### client.CoinPouchMouseActionMixin
**Purpose:** *(new in v2.0.0)* Registers `CoinPouchMouseActions` into every inventory screen's mouse-action list, the same extension point vanilla uses for Bundle's scroll/click handling — gives the Coin Pouch its scroll-to-select-tier and slot-click deposit/withdraw behavior.

### ShieldRendererMixin
**Purpose:** Custom shield rendering for DAGMod's themed shields (Inferno, Shadow, Crystal, etc.) to display unique shield textures.
> **Note:** this class exists in source but is **not currently listed** in `dagmod.mixins.json`'s `client` array — as written today it never actually runs. Needs either re-registering or removing.

---

## Mixin Development Notes

- Mixins **cannot** have non-private static methods. Any public static storage or utility methods must be placed in a separate helper class.
- Server mixins run on both dedicated servers and integrated servers (singleplayer).
- Client mixins are only loaded on the client side.
- All mixins use `JAVA_21` compatibility level.
