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
**Purpose:** Implements the Soul Bound enchantment. Injects at the `onDeath` method HEAD to scan the player's inventory for items with `dagmod:soul_bound`, removes them before they drop, and stores them in `SoulBoundStorage`. Items are returned on respawn via the `AFTER_RESPAWN` event handler in `DagMod.java`.

---

## Client Mixins

### ChestRenderStateMixin
**Purpose:** Modifies chest rendering state for locked Bone Realm chests to display a unique texture.

### LockedChestTextureMixin
**Purpose:** Two-part mixin (`ChestRendererMixin` + `TexturedRenderLayersMixin`) that registers and applies custom locked chest textures for the Bone Realm boss chests.

### ShieldRendererMixin
**Purpose:** Custom shield rendering for DAGMod's themed shields (Inferno, Shadow, Crystal, etc.) to display unique shield textures.

---

## Mixin Development Notes

- Mixins **cannot** have non-private static methods. Any public static storage or utility methods must be placed in a separate helper class.
- Server mixins run on both dedicated servers and integrated servers (singleplayer).
- Client mixins are only loaded on the client side.
- All mixins use `JAVA_21` compatibility level.
