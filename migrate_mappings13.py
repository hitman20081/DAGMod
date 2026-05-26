#!/usr/bin/env python3
"""Pass 13: Fix remaining global renames found in compile output."""

import os
import re

SRC = os.path.join(os.path.dirname(__file__), "src", "main", "java")

CODE_REPLACEMENTS = [
    # BlockBehaviour.Properties method renames (Yarn -> Mojang)
    (r'\.sounds\(', '.sound('),
    (r'\.nonOpaque\(\)', '.noOcclusion()'),
    (r'\.requiresTool\(\)', '.requiresCorrectToolForDrops()'),

    # CreativeModeTabs field renames
    (r'\bCreativeModeTabs\.NATURAL\b', 'CreativeModeTabs.NATURAL_BLOCKS'),
    (r'\bCreativeModeTabs\.FUNCTIONAL\b', 'CreativeModeTabs.FUNCTIONAL_BLOCKS'),
    (r'\bCreativeModeTabs\.OPERATOR\b', 'CreativeModeTabs.OP_BLOCKS'),
    (r'\bCreativeModeTabs\.TOOLS\b', 'CreativeModeTabs.TOOLS_AND_UTILITIES'),

    # SoundType.BONE -> SoundType.BONE_BLOCK
    (r'\bSoundType\.BONE\b', 'SoundType.BONE_BLOCK'),

    # server.getWorlds() -> server.getAllLevels()
    (r'\.getWorlds\(\)', '.getAllLevels()'),

    # RecipeManager.getFirstMatch -> getRecipeFor (or remains getFirstMatch)
    # Actually keep getFirstMatch but fix the server access
    # (The getRecipeManager() fix from pass 12 adds .getServer() before .getRecipeManager())

    # Remove .registryKey(ResourceKey.create(Registries.ITEM, ...)) from Item.Properties
    # These appear in BoneRealmRegistry and elsewhere
    (r'\.registryKey\(ResourceKey\.create\(Registries\.ITEM,\s*Identifier\.fromNamespaceAndPath\([^)]+\)\)\)', ''),

    # luminance -> lightLevel (Yarn -> Mojang)
    (r'\.luminance\(', '.lightLevel('),

    # MutableText.styled( lambda ) -> MutableComponent.styled( lambda ) stays same
    # But if specific issue with style methods...
    # ChatFormatting vs Style color
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

    print(f"\nPass 13 complete: {updated}/{total} files updated.")


if __name__ == '__main__':
    main()
