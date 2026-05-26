#!/usr/bin/env python3
"""
Third-pass migration fixes:
- GuiGraphics → GuiGraphicsExtractor
- RenderTickCounter → DeltaTracker
- HudLayerRegistrationCallback → HudElementRegistry / HudElement
- Commands.RegistrationEnvironment → Commands.CommandSelection
- Random (Yarn) → RandomSource
- Merchant/MerchantOffer package fixes (world.item.trading)
- ItemGroup API → creative tab API
- FabricBrewingRecipeRegistryBuilder → FabricPotionBrewingBuilder
- Entity renderer Fabric API renames
- Inline FQN remaining fixes
- Remaining package errors
"""

import os
import re

IMPORT_REPLACEMENTS = [
    # GUI / HUD rendering
    ("import net.minecraft.client.gui.GuiGraphics;",            "import net.minecraft.client.gui.GuiGraphicsExtractor;"),
    ("import net.minecraft.client.render.RenderTickCounter;",   "import net.minecraft.client.DeltaTracker;"),
    ("import net.minecraft.client.renderer.RenderTickCounter;", "import net.minecraft.client.DeltaTracker;"),
    ("import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;",          "import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;\nimport net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;"),
    ("import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;","import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;\nimport net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;"),

    # Item group / creative tab API
    ("import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;",   "import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;"),
    ("import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;",   "import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;"),

    # Entity/block entity renderer Fabric API
    ("import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;","import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;"),

    # Brewing recipe
    ("import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;","import net.fabricmc.fabric.api.registry.FabricPotionBrewingBuilder;"),

    # Merchant (wrong package in prev pass)
    ("import net.minecraft.world.entity.npc.Merchant;",         "import net.minecraft.world.item.trading.Merchant;"),
    ("import net.minecraft.world.entity.npc.MerchantOffer;",    "import net.minecraft.world.item.trading.MerchantOffer;"),
    ("import net.minecraft.world.entity.npc.MerchantOffers;",   "import net.minecraft.world.item.trading.MerchantOffers;"),

    # Random (remaining inline/import issues)
    ("import net.minecraft.util.math.random.Random;",           "import net.minecraft.util.RandomSource;"),

    # ScheduledTickView → ScheduledTickAccess (already in world.level)
    ("import net.minecraft.world.level.redstone.ScheduledTickAccess;","import net.minecraft.world.level.ScheduledTickAccess;"),
    ("import net.minecraft.world.tick.ScheduledTickView;",      "import net.minecraft.world.level.ScheduledTickAccess;"),

    # HolderLookup (fix: previous replacement may have added wrong import)
    ("import net.minecraft.core.HolderLookup;\n",               "import net.minecraft.core.HolderLookup;\n"),  # keep

    # NetworkingPayload
    ("import net.minecraft.network.protocol.common.custom.CustomPacketPayload;","import net.minecraft.network.protocol.common.custom.CustomPacketPayload;"),

    # Particle effect interface
    ("import net.minecraft.core.particles.ParticleEffect;",     "import net.minecraft.core.particles.ParticleOptions;"),

    # DeltaTracker (correct Mojang name)
    ("import net.minecraft.client.renderer.DeltaTracker;",      "import net.minecraft.client.DeltaTracker;"),

    # Stats - Mojang keeps Stats in net.minecraft.stats
    ("import net.minecraft.stats.Stats;",                       "import net.minecraft.stats.Stats;"),

    # EntityData deduplication fix
    ("import net.minecraft.world.entity.SpawnGroupData;\nimport net.minecraft.world.entity.SpawnGroupData;",
     "import net.minecraft.world.entity.SpawnGroupData;"),
]

CODE_REPLACEMENTS = [
    # GUI / HUD rendering
    (r'\bGuiGraphics\b',            'GuiGraphicsExtractor'),
    (r'\bDrawContext\b',            'GuiGraphicsExtractor'),
    (r'\bRenderTickCounter\b',      'DeltaTracker'),

    # HUD registration
    (r'\bHudRenderCallback\b',      'HudElementRegistry'),
    (r'\bHudLayerRegistrationCallback\b', 'HudElementRegistry'),

    # Command selection
    (r'\bCommands\.RegistrationEnvironment\b', 'Commands.CommandSelection'),
    (r'\bCommandManager\.RegistrationEnvironment\b', 'Commands.CommandSelection'),

    # Random (Yarn util.math.random.Random)
    (r'\bnet\.minecraft\.util\.math\.random\.Random\b', 'net.minecraft.util.RandomSource'),
    # After import was replaced, still might have bare Random references
    # But only when used as a type name (not in method names or variable names that aren't types)
    # This is tricky - handle carefully:
    (r'(?<!\w)Random(?!\w+\.)', 'RandomSource'),

    # Merchant - fix package references in code bodies
    (r'net\.minecraft\.world\.entity\.npc\.Merchant\b(?!O)', 'net.minecraft.world.item.trading.Merchant'),
    (r'net\.minecraft\.world\.entity\.npc\.MerchantOffer\b', 'net.minecraft.world.item.trading.MerchantOffer'),
    (r'net\.minecraft\.world\.entity\.npc\.MerchantOffers\b', 'net.minecraft.world.item.trading.MerchantOffers'),

    # ItemGroup API → creative tab API
    (r'\bFabricItemGroup\b',        'FabricCreativeModeTab'),
    (r'\bItemGroupEvents\b',        'CreativeModeTabEvents'),
    (r'net\.fabricmc\.fabric\.api\.itemgroup\.v1', 'net.fabricmc.fabric.api.creativetab.v1'),

    # Entity renderer Fabric API
    (r'\bEntityModelLayerRegistry\b', 'ModelLayerRegistry'),

    # Brewing
    (r'\bFabricBrewingRecipeRegistryBuilder\b', 'FabricPotionBrewingBuilder'),

    # ScheduledTickAccess (was ScheduledTickView)
    (r'\bScheduledTickView\b',      'ScheduledTickAccess'),
    (r'net\.minecraft\.world\.level\.redstone\.ScheduledTickAccess', 'net.minecraft.world.level.ScheduledTickAccess'),

    # Particle API fix
    (r'\bParticleEffect\b',         'ParticleOptions'),
    (r'net\.minecraft\.particle\.', 'net.minecraft.core.particles.'),

    # DeltaTracker
    (r'net\.minecraft\.client\.renderer\.DeltaTracker', 'net.minecraft.client.DeltaTracker'),

    # Item entity package inline fix
    (r'net\.minecraft\.entity\.item\.', 'net.minecraft.world.entity.item.'),

    # Stats
    (r'\bnet\.minecraft\.stat\.\b', 'net.minecraft.stats.'),

    # Still remaining inline package refs
    (r'net\.minecraft\.util\.math\.random\.Random', 'net.minecraft.util.RandomSource'),
    (r'net\.minecraft\.entity\.mob\.', 'net.minecraft.world.entity.monster.'),
    (r'net\.minecraft\.entity\.passive\.', 'net.minecraft.world.entity.animal.'),
    (r'net\.minecraft\.entity\.ai\.goal\.', 'net.minecraft.world.entity.ai.goal.'),
    (r'net\.minecraft\.entity\.', 'net.minecraft.world.entity.'),
    (r'net\.minecraft\.block\.', 'net.minecraft.world.level.block.'),

    # SkeletonEntityRenderState → SkeletonRenderState (import was fixed, code body not)
    (r'\bSkeletonEntityRenderState\b', 'SkeletonRenderState'),
    (r'\bAbstractSkeletonEntityRenderer\b', 'AbstractSkeletonRenderer'),
    (r'\bVillagerEntityRenderState\b', 'VillagerEntityRenderState'),  # keep same for now

    # ItemStack.EMPTY usage
    (r'\bItemStack\.EMPTY\b', 'ItemStack.EMPTY'),  # stays same

    # EntityRendererRegistry (Fabric API v1)
    (r'\bEntityRendererFactories\.register\b', 'EntityRendererRegistry.register'),

    # HolderLookup.Provider (WrapperLookup replacement)
    (r'\bWrapperLookup\b', 'HolderLookup.Provider'),
    (r'\bRegistryWrapper\.WrapperLookup\b', 'HolderLookup.Provider'),

    # Screens
    (r'\bHandledScreen\b', 'AbstractContainerScreen'),

    # Fix duplicate BlockState import
    # (handled below)
]


def fix_duplicate_imports(content):
    """Remove duplicate import lines."""
    lines = content.split('\n')
    seen_imports = set()
    new_lines = []
    for line in lines:
        if line.startswith('import ') and line.endswith(';'):
            if line in seen_imports:
                continue
            seen_imports.add(line)
        new_lines.append(line)
    return '\n'.join(new_lines)


def process_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    original = content

    for old, new in IMPORT_REPLACEMENTS:
        content = content.replace(old, new)

    for pattern, replacement in CODE_REPLACEMENTS:
        content = re.sub(pattern, replacement, content)

    content = fix_duplicate_imports(content)

    if content != original:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)
        return True
    return False


def main():
    src_dir = os.path.join(os.path.dirname(__file__), 'src', 'main', 'java')
    changed = 0
    total = 0
    for root, dirs, files in os.walk(src_dir):
        for fname in files:
            if not fname.endswith('.java'):
                continue
            path = os.path.join(root, fname)
            total += 1
            if process_file(path):
                changed += 1
                print(f"  Updated: {os.path.relpath(path, src_dir)}")
    print(f"\nDone: {changed}/{total} files updated.")


if __name__ == '__main__':
    main()
