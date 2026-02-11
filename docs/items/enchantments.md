# Custom Enchantments

DAGMod includes 26 custom enchantments from the dag009 datapack, ranging from combat enhancements to utility effects. Some are data-driven (JSON effects) while others have custom Java implementations for unique behavior.

---

## Combat Enchantments

### Berserker
- **Slot:** Chest armor
- **Max Level:** 10
- **Rarity:** Very Rare (weight 1)
- **Effect:** +100% attack speed and +100% attack damage (scales +5% per level). Applies Hunger IV-VII to the wearer after each hit.
- **Note:** High risk, high reward. The hunger cost is significant at higher levels.

### Blazing Strike
- **Slot:** Mainhand (swords)
- **Max Level:** 1
- **Rarity:** Very Rare (weight 1)
- **Effect:** Sets the target on fire for 6 seconds on each hit.

### Frostbite
- **Slot:** Mainhand (swords)
- **Max Level:** 1
- **Rarity:** Very Rare (weight 1)
- **Effect:** On hit: applies Slowness I-III for 1-3 seconds, deals 1-5 freeze damage, and spawns ice-blue particles on the target.

### Leach
- **Slot:** Mainhand (swords)
- **Max Level:** 1
- **Rarity:** Very Rare (weight 1)
- **Effect:** On hit: applies Wither IX to the target for 1 second, and grants the attacker Health Boost XI-XXI for 1 second.

### Lightning
- **Slot:** Mainhand (tridents)
- **Max Level:** 1
- **Rarity:** Uncommon (weight 3)
- **Effect:** On hit: deals 5-10 lightning bolt damage and spawns electric spark particles on the target.

### Savage
- **Slot:** Mainhand (sharp weapons)
- **Max Level:** 5
- **Rarity:** Common (weight 10)
- **Effect:** +1 base damage (+10 per level above first). On hit: applies Weakness II-IV to the target for 1-3 seconds.

### Xtra Damage
- **Slot:** Mainhand (netherite sword only)
- **Max Level:** 10
- **Rarity:** Very Common (weight 50)
- **Effect:** +30 base damage (+20 per level above first), but only against sneaking skeleton-type mobs. A niche enchantment for specific encounters.

---

## Damage Type Enchantments

### Bane of the White Walker
- **Slot:** Mainhand (weapons)
- **Max Level:** 5
- **Rarity:** Uncommon (weight 5)
- **Effect:** +2.5 damage per level against freeze-immune mobs. Applies Slowness IV on hit to those mobs.

### Rise of the Zombies
- **Slot:** Mainhand (weapons)
- **Max Level:** 5
- **Rarity:** Uncommon (weight 5)
- **Effect:** +2.5 damage per level against zombies. Applies Levitation IV on hit to zombies, launching them into the air. Exclusive with other damage enchantments.

---

## Defense & Survival Enchantments

### Heart of the Armor
- **Slot:** Any armor piece
- **Max Level:** 5
- **Rarity:** Uncommon (weight 5)
- **Effect:** +2 max health per level (1 heart per level, up to +5 hearts at level 5).

### Siphon
- **Slot:** Chest armor
- **Max Level:** 1
- **Rarity:** Uncommon (weight 3)
- **Effect:** When hit by undead mobs: deals 1-3 thorns damage back to the attacker AND heals the wearer with Instant Health II.

### Shatter Proof
- **Slot:** Any item with durability (weapons, tools, armor, shields, bows)
- **Max Level:** 1
- **Rarity:** Very Rare (weight 1)
- **Effect:** Prevents the item from ever breaking. When durability would reach 0, it is capped at 1 instead. However, an item at 1 durability enters a **broken state** with 99% reduced effectiveness:
  - **Weapons:** 99% less attack damage
  - **Tools:** 99% less mining speed
  - **Shields:** 99% chance to fail blocking
- The item must be repaired (anvil, Mending, etc.) to restore full effectiveness. *(Java-implemented via ShatterproofMixin + ShatterproofDamageMixin + ShatterproofMiningMixin)*

### Soul Bound
- **Slot:** Armor, mainhand, offhand
- **Max Level:** 1
- **Rarity:** Very Rare (weight 1)
- **Effect:** The item is preserved when you die. Instead of dropping, it is automatically returned to your inventory on respawn. *(Java-implemented via SoulBoundMixin + SoulBoundStorage)*

### Soul Boost
- **Slot:** Any equipment
- **Max Level:** 1
- **Rarity:** Very Rare (weight 1)
- **Effect:** While in the Nether: grants Health Boost V (10 extra hearts) for 10 seconds, continuously refreshed. Also has a 25% chance on hit to apply Saturation III-V to the target.

---

## Utility & Movement Enchantments

### Climb
- **Slot:** Boots
- **Max Level:** 3
- **Rarity:** Uncommon (weight 5)
- **Effect:** +0.5 step height per level. At level 3, you can step up 1.5 extra blocks without jumping.

### Reach
- **Slot:** Mainhand (mining tools)
- **Max Level:** 3
- **Rarity:** Uncommon (weight 5)
- **Effect:** +1 block interaction range per level. Mine blocks from further away.

### Shadow
- **Slot:** Armor (leggings and boots only)
- **Max Level:** 5
- **Rarity:** Uncommon (weight 3)
- **Effect:** +5% movement speed and +5% movement efficiency per level. Stacks multiplicatively across pieces.

### Magma Walker
- **Slot:** Boots
- **Max Level:** 2
- **Rarity:** Rare (weight 2)
- **Effect:** Converts lava beneath you into magma blocks within a radius of 3 + 1 per level. Also grants immunity to hot-block stepping damage. Exclusive with Frost Walker.

---

## Mining & Gathering Enchantments

### Midas Touch
- **Slot:** Mainhand (pickaxes)
- **Max Level:** 1
- **Rarity:** Very Rare (weight 1)
- **Effect:** When you mine Gilded Blackstone, it drops a Gold Block instead of its normal drops. Exclusive with Fortune. *(Java-implemented)*

### Mud Collector
- **Slot:** Mainhand (shovels)
- **Max Level:** 1
- **Rarity:** Very Rare (weight 1)
- **Effect:** When mining dirt, grass, coarse dirt, or rooted dirt while it is raining, drops a Mud block. *(Java-implemented)*

### Tunneling
- **Slot:** Mainhand (mining tools)
- **Max Level:** 1
- **Rarity:** Uncommon (weight 3)
- **Effect:** Mines a 3x3 area perpendicular to the face you're mining. Skips air, bedrock, fluids, and unbreakable blocks. *(Java-implemented)*

### Light's Blessing
- **Slot:** Mainhand (swords)
- **Max Level:** 3
- **Rarity:** Rare (weight 2)
- **Effect:** +1 mob XP per level when killing undead mobs.

---

## Head Enchantments

### Lucky Looter
- **Slot:** Head armor (helmets)
- **Max Level:** 1
- **Rarity:** Rare (weight 2)
- **Effect:** 25% chance per mob kill to drop bonus valuable items. Hostile mobs drop better loot (diamonds, emeralds, gold) than passive mobs (nuggets). *(Java-implemented)*

### Summon
- **Slot:** Head armor (helmets)
- **Max Level:** 10
- **Rarity:** Uncommon (weight 3)
- **Effect:** When hit by a zombie, triggers a summoning effect via datapack function.

---

## Ranged Enchantments

### Solar
- **Slot:** Mainhand (bows)
- **Max Level:** 3
- **Rarity:** Very Rare (weight 1)
- **Effect:** Fires +3 extra projectiles (base) with +1 per additional level. While sneaking, also adds +3 spread to the extra projectiles for a shotgun-like effect.

---

## Curses

### Curse of Brittleness
- **Slot:** Any durable item
- **Max Level:** 1
- **Rarity:** Rare (weight 2)
- **Effect:** 12.5% chance per durability tick to multiply durability damage by 16x. Items with this curse break extremely fast. Exclusive with Unbreaking.

---

## Rarity Guide

| Rarity | Weight | Examples |
|--------|--------|---------|
| Very Common | 50 | Xtra Damage |
| Common | 10 | Savage |
| Uncommon | 3-5 | Heart of the Armor, Reach, Climb, Rise of the Zombies, Tunneling |
| Rare | 2 | Light's Blessing, Lucky Looter, Curse of Brittleness |
| Very Rare | 1 | Berserker, Blazing Strike, Frostbite, Leach, Midas Touch, Shatter Proof, Soul Bound, Solar |

---

## Acquisition

Custom enchantments can be obtained through:
- **Enchanting Table** - Random chance based on weight/rarity
- **Anvil** - Combine with enchanted books (anvil cost varies per enchantment)
- **Commands** - `/enchant @s dag009:<enchantment_name>`
- **Enchantsmith NPC** - Rotating enchanted book stock
- **Quest Rewards** - Some quests reward enchanted gear
