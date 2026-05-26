#!/usr/bin/env python3
"""Pass 15: Fix remaining renames found after pass 14."""

import os
import re

SRC = os.path.join(os.path.dirname(__file__), "src", "main", "java")

CODE_REPLACEMENTS = [
    # MutableComponent.styled( -> .withStyle(
    (r'\.styled\(', '.withStyle('),

    # MapColor.BLACK -> MapColor.COLOR_BLACK (and similar Yarn->Mojang renames)
    (r'\bMapColor\.BLACK\b', 'MapColor.COLOR_BLACK'),
    (r'\bMapColor\.WHITE\b', 'MapColor.SNOW'),
    (r'\bMapColor\.ORANGE\b', 'MapColor.COLOR_ORANGE'),
    (r'\bMapColor\.MAGENTA\b', 'MapColor.COLOR_MAGENTA'),
    (r'\bMapColor\.LIGHT_BLUE\b', 'MapColor.COLOR_LIGHT_BLUE'),
    (r'\bMapColor\.YELLOW\b', 'MapColor.COLOR_YELLOW'),
    (r'\bMapColor\.LIME\b', 'MapColor.COLOR_LIGHT_GREEN'),
    (r'\bMapColor\.PINK\b', 'MapColor.COLOR_PINK'),
    (r'\bMapColor\.GRAY\b', 'MapColor.COLOR_GRAY'),
    (r'\bMapColor\.SILVER\b', 'MapColor.COLOR_LIGHT_GRAY'),
    (r'\bMapColor\.CYAN\b', 'MapColor.COLOR_CYAN'),
    (r'\bMapColor\.PURPLE\b', 'MapColor.COLOR_PURPLE'),
    (r'\bMapColor\.BLUE\b', 'MapColor.COLOR_BLUE'),
    (r'\bMapColor\.BROWN\b', 'MapColor.COLOR_BROWN'),
    (r'\bMapColor\.GREEN\b', 'MapColor.COLOR_GREEN'),
    (r'\bMapColor\.RED\b', 'MapColor.COLOR_RED'),

    # SwimGoal -> FloatGoal
    (r'\bSwimGoal\b', 'FloatGoal'),

    # getSoundPitch() -> getVoicePitch()
    (r'\bgetSoundPitch\(\)', 'getVoicePitch()'),

    # initEquipment( -> populateDefaultEquipmentSlots(
    (r'\binitEquipment\(', 'populateDefaultEquipmentSlots('),

    # equipStack( -> setItemSlot(
    (r'\bequipStack\(', 'setItemSlot('),

    # setEquipmentDropChance( -> setDropChance(
    (r'\bsetEquipmentDropChance\(', 'setDropChance('),

    # cannotDespawn() -> isPersistenceRequired()
    (r'\bcannotDespawn\(\)', 'isPersistenceRequired()'),

    # world.getPlayers( -> world.players(   (no-arg form, already fixed .getPlayers( with ) above)
    # Fix ServerLevel.getPlayers() -> players()
    (r'\.getPlayers\(\)', '.players()'),

    # BlockPos.isWithinDistance( -> BlockPos.closerThan(
    (r'\.isWithinDistance\(', '.closerThan('),

    # Inventory.insertStack( -> Inventory.add(
    (r'\.insertStack\(', '.add('),

    # ServerBossEvent.setPercent( -> setProgress(
    (r'\.setPercent\(', '.setProgress('),

    # BlockState.getItem() == -> .getBlock() == (for block checking)
    # Can't do this globally safely, do specifically
    # (.getItem() on ItemStack is different from BlockState.getItem())

    # HurtByTargetGoal and NearestAttackableTargetGoal need different import package
    # Can't fix imports via this script easily, but fix the wildcard

    # Player.dropItem( -> Player.drop(
    (r'\bdropItem\(', 'drop('),

    # BlockPos.add( -> BlockPos.offset( (only when 3 int args or variable args)
    # The pattern .add(x, y, z) or .add(-r, -r, -r) etc
    (r'\.add\((-?\w+),\s*(-?\w+),\s*(-?\w+)\)', r'.offset(\1, \2, \3)'),

    # LightBlock.LEVEL_15 -> LightBlock.LEVEL  (wrong property name)
    (r'\bLightBlock\.LEVEL_15\b', 'LightBlock.LEVEL'),

    # state.get( -> state.getValue( (for BlockState property reading)
    # Only when followed by a Property type (e.g., LightBlock.LEVEL, AXIS, etc.)
    # This is risky globally - only do for the specific pattern with LightBlock
    (r'state\.get\(LightBlock\.LEVEL\)', 'state.getValue(LightBlock.LEVEL)'),
]


def process_file(path):
    with open(path, 'r', encoding='utf-8') as f:
        original = f.read()

    content = original
    for pattern, replacement in CODE_REPLACEMENTS:
        content = re.sub(pattern, replacement, content)

    if content != original:
        with open(path, 'w', encoding='utf-8') as f:
            f.write(content)
        return True
    return False


def main():
    updated = 0
    total = 0
    for root, dirs, files in os.walk(SRC):
        dirs[:] = [d for d in dirs if d != '__pycache__']
        for fname in files:
            if not fname.endswith('.java'):
                continue
            path = os.path.join(root, fname)
            total += 1
            if process_file(path):
                updated += 1
                print(f"  Updated: {os.path.relpath(path, SRC)}")

    print(f"\nPass 15 complete: {updated}/{total} files updated.")


if __name__ == '__main__':
    main()
