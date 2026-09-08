# Consumable Powders ⭐ 15 Total

**Powerful one-time use consumables with class-specific effects!** All 15 are fully implemented — none are placeholder effects as of v1.7.4.

### Universal Consumables

#### Vampire Dust
**Effect:** Real lifesteal for 20 seconds — 10% of melee damage dealt is returned as healing, capped at 2.5 hearts per hit (`VampireDustHandler`, mixin-driven).

#### Phantom Dust
**Effect:** 50% chance to dodge an incoming hit entirely for 15 seconds (`DodgeHandler`, per-hit roll). A successful dodge plays particles, an action-bar message, and *(new)* an Enderman-teleport sound.

#### Fortune Dust
**Effect:** Fortune III on the next 10 blocks mined.

#### Featherfall Powder
**Effect:** Slow Falling for 60 seconds (no fall damage).

#### Last Stand Powder
**Effect:** Real death prevention — intercepts your next lethal hit (and void deaths, teleporting you to the surface instead) rather than just granting a temporary buff.

#### Time Distortion
**Effect:** Speed II on yourself for 10 seconds, plus Slowness IV on all enemies within 10 blocks for the same duration.

### Class-Specific Consumables

#### Mana Crystal (Mage)
**Effect:** Restores 50 Mana.

#### Cooldown Elixir (Warrior)
**Effect:** Reduces all Warrior ability cooldowns by 30 seconds.

#### Spell Echo (Mage)
**Effect:** Your next spell cast twice (`SpellModifierHandler`).

#### Overcharge Dust (Mage)
**Effect:** Your next spell has 2x power (`SpellModifierHandler`).

#### Battle Frenzy (Warrior)
**Effect:** Strength II, Speed II, and Haste III for 15 seconds.

#### Titan's Strength (Warrior)
**Effect:** Strength V for 20 seconds (+100% melee damage).

#### Energy Tonic (Rogue)
**Effect:** Restores 50 Energy.

#### Shadow Blend (Rogue)
**Effect:** Invisibility for 5 minutes, breaks on attack (`ShadowBlendHandler`).

#### Perfect Dodge (Rogue)
**Effect:** 100% dodge chance for 10 seconds (`DodgeHandler`, per-hit roll). A successful dodge plays particles, an action-bar message, and *(new)* an Enderman-teleport sound — same feedback as Phantom Dust and the Rogue's passive Shadow Step enchantment.

---

**Acquisition:** Quest rewards, crafting, and select merchants.
