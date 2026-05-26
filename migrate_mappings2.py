#!/usr/bin/env python3
"""
Second-pass migration fixes:
- Inline FQN replacements (fully-qualified names in method bodies)
- Missing class renames: World→Level, Text→Component, Settings→Properties
- Fabric API v1 screen handler → menu API v1
- PacketCodec → StreamCodec, RegistryByteBuf → RegistryFriendlyByteBuf
- WrapperLookup → HolderLookup, StringIdentifiable → StringRepresentable
- storage.ReadView/WriteView → NBT-based alternatives
- Block wildcard import: add missing BlockState import
"""

import os
import re

# Import-level replacements (exact string match)
IMPORT_REPLACEMENTS = [
    # Fabric screen handler → menu API
    ("import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;",
     "import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;"),
    ("import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;",
     "import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;"),
    ("import net.fabricmc.fabric.api.screenhandler.v1.FabricScreenHandler;",
     "import net.minecraft.world.inventory.AbstractContainerMenu;"),

    # Networking
    ("import net.minecraft.network.PacketByteBuf;",    "import net.minecraft.network.FriendlyByteBuf;"),
    ("import net.minecraft.network.RegistryByteBuf;",  "import net.minecraft.network.RegistryFriendlyByteBuf;"),
    ("import net.minecraft.network.codec.PacketCodec;","import net.minecraft.network.codec.StreamCodec;"),
    ("import net.minecraft.network.codec.PacketCodecs;","import net.minecraft.network.codec.ByteBufCodecs;"),
    ("import net.minecraft.network.packet.CustomPayload;","import net.minecraft.network.protocol.common.custom.CustomPacketPayload;"),
    ("import net.minecraft.network.packet.s2c.play.PositionFlag;","import net.minecraft.world.entity.Relative;"),

    # Registry / Lookup
    ("import net.minecraft.registry.RegistryWrapper;",  "import net.minecraft.core.HolderLookup;"),
    ("import net.minecraft.registry.WrapperLookup;",    "import net.minecraft.core.HolderLookup;"),

    # Misc util
    ("import net.minecraft.util.StringIdentifiable;",   "import net.minecraft.util.StringRepresentable;"),

    # Storage (Yarn 1.21.4+ save API)
    ("import net.minecraft.storage.ReadView;",          "import net.minecraft.nbt.CompoundTag;"),
    ("import net.minecraft.storage.WriteView;",         "import net.minecraft.nbt.CompoundTag;"),

    # Recipe - IngredientPlacement removed, Ingredient stays
    ("import net.minecraft.recipe.input.SingleStackRecipeInput;","import net.minecraft.world.item.crafting.SingleRecipeInput;"),
    ("import net.minecraft.recipe.IngredientPlacement;","import net.minecraft.world.item.crafting.Ingredient;"),

    # Block missing imports
    ("import net.minecraft.world.level.block.state.BlockBehaviour;",
     "import net.minecraft.world.level.block.state.BlockBehaviour;"),

    # Component type fixes
    ("import net.minecraft.component.type.FoodComponent;",      "import net.minecraft.world.food.FoodProperties;"),
    ("import net.minecraft.component.type.ConsumableComponent;","import net.minecraft.world.item.consume.ConsumableComponent;"),

    # Particle
    ("import net.minecraft.particle.ParticleTypes;",    "import net.minecraft.core.particles.ParticleTypes;"),

    # Inventory
    ("import net.minecraft.screen.ScreenHandlerContext;", "import net.minecraft.world.inventory.ContainerLevelAccess;"),
]

# Inline FQN replacements (in code bodies, not just imports)
INLINE_FQN = [
    # Old inline package refs
    ("net.minecraft.server.world.ServerWorld",      "net.minecraft.server.level.ServerLevel"),
    ("net.minecraft.server.world.ServerLevel",      "net.minecraft.server.level.ServerLevel"),  # keep correct
    ("net.minecraft.block.BlockState",              "net.minecraft.world.level.block.state.BlockState"),
    ("net.minecraft.util.math.BlockPos",            "net.minecraft.core.BlockPos"),
    ("net.minecraft.util.math.Vec3d",               "net.minecraft.world.phys.Vec3"),
    ("net.minecraft.particle.ParticleEffect",       "net.minecraft.core.particles.ParticleEffect"),
    ("net.minecraft.particle.ParticleType",         "net.minecraft.core.particles.ParticleType"),
    ("net.minecraft.item.Item",                     "net.minecraft.world.item.Item"),
    ("net.minecraft.storage.WriteView",             "net.minecraft.nbt.CompoundTag"),
    ("net.minecraft.storage.ReadView",              "net.minecraft.nbt.CompoundTag"),
    ("net.minecraft.item.ItemStack",                "net.minecraft.world.item.ItemStack"),
    ("net.minecraft.entity.player.Player",          "net.minecraft.world.entity.player.Player"),
    ("net.minecraft.entity.LivingEntity",           "net.minecraft.world.entity.LivingEntity"),
    ("net.minecraft.world.World",                   "net.minecraft.world.level.Level"),
]

# Regex-based code body replacements
CODE_REPLACEMENTS = [
    # === Critical missing class renames ===
    # World → Level (standalone type references)
    (r'\bWorld\b(?!\s*Gen|\s*Acc|\s*View|\s*ly|\s*Prop|\s*Sav|\s*Spawn|\s*Provider)', 'Level'),

    # Text → Component (standalone type references)
    # But not within TextRenderer, TextComponent, etc.
    (r'(?<![A-Za-z])Text\b(?!Renderer|RendererMixin|Component|Codec|Color|Attr|ure)', 'Component'),

    # Settings → Properties (in block/item context)
    # AbstractBlock.Settings → BlockBehaviour.Properties (already done)
    # Standalone Settings as a type (from block builders)
    # Only replace when it's clearly used as AbstractBlock.Settings or FabricBlockSettings
    (r'\bFabricBlockSettings\b', 'BlockBehaviour.Properties'),
    (r'(?<![A-Za-z\.])Settings\b(?!\s*\(|\s*[a-z].*=\s*Settings)', 'Properties'),

    # Item.Settings → Item.Properties
    (r'\bItem\.Settings\b', 'Item.Properties'),

    # Fabric screen handler → menu
    (r'\bExtendedScreenHandlerFactory\b', 'ExtendedMenuProvider'),
    (r'\bExtendedScreenHandlerType\b',    'ExtendedMenuType'),
    (r'net\.fabricmc\.fabric\.api\.screenhandler\.v1', 'net.fabricmc.fabric.api.menu.v1'),

    # Networking
    (r'\bPacketByteBuf\b',      'FriendlyByteBuf'),
    (r'\bRegistryByteBuf\b',    'RegistryFriendlyByteBuf'),
    (r'\bPacketCodec\b',        'StreamCodec'),
    (r'\bPacketCodecs\b',       'ByteBufCodecs'),
    (r'\bCustomPayload\b',      'CustomPacketPayload'),
    (r'\bPositionFlag\b',       'Relative'),

    # Registry
    (r'\bWrapperLookup\b',      'HolderLookup.Provider'),
    (r'\bRegistryWrapper\.WrapperLookup\b', 'HolderLookup.Provider'),

    # Misc
    (r'\bStringIdentifiable\b', 'StringRepresentable'),

    # IngredientPlacement → removed (use Ingredient directly)
    (r'\bIngredientPlacement\b', 'Ingredient'),

    # Recipe input
    (r'\bSingleStackRecipeInput\b', 'SingleRecipeInput'),

    # DataTracker method renames
    (r'DataTracker\.Builder\b', 'SynchedEntityData.Builder'),
    (r'\.createDataTracker\b',  '.defineSynchedData'),
    (r'\.setCustomName\(Text\b', '.setCustomName(Component'),

    # StatusEffect legacy usages in code
    (r'\bStatusEffect\b(?!Category|Instance|s\b)', 'MobEffect'),

    # Write/ReadCustomData method signature fixes (storage API)
    (r'WriteView writeView\b',  'CompoundTag tag'),
    (r'ReadView readView\b',    'CompoundTag tag'),
    (r'writeView\b',            'tag'),
    (r'readView\b',             'tag'),

    # ScreenHandlerContext → ContainerLevelAccess
    (r'\bScreenHandlerContext\b', 'ContainerLevelAccess'),

    # Formatting already → ChatFormatting but Text.Formatting → ChatFormatting
    (r'\bText\.Formatting\b', 'ChatFormatting'),

    # Collision context full replacement
    (r'\bAbstractBlock\.AbstractBlockState\b', 'BlockBehaviour.BlockStateBase'),

    # Stat
    (r'\bStats\b(?=\.\w)', 'Stats'),  # stays same

    # PortionFlag → Relative
    (r'\bPositionFlag\.', 'Relative.'),

    # GraveData / NBT fix: WrapperLookup in save methods
    (r'\bRegistryWrapper\.WrapperLookup\b', 'HolderLookup.Provider'),
]

def add_missing_block_state_import(content):
    """
    If file imports net.minecraft.world.level.block.* (wildcard) but also
    uses BlockState (which is in state subpackage), add explicit import.
    """
    has_wildcard = "import net.minecraft.world.level.block.*;" in content
    has_blockstate_import = "import net.minecraft.world.level.block.state.BlockState;" in content
    uses_blockstate = bool(re.search(r'\bBlockState\b', content))

    if has_wildcard and not has_blockstate_import and uses_blockstate:
        # Insert after the wildcard import
        content = content.replace(
            "import net.minecraft.world.level.block.*;",
            "import net.minecraft.world.level.block.*;\nimport net.minecraft.world.level.block.state.BlockState;"
        )
    elif not has_blockstate_import and uses_blockstate:
        # Look for any block import to insert after
        match = re.search(r'(import net\.minecraft\.world\.level\.block[^;]*;)', content)
        if match:
            insert_after = match.group(1)
            if "import net.minecraft.world.level.block.state.BlockState;" not in content:
                content = content.replace(
                    insert_after,
                    insert_after + "\nimport net.minecraft.world.level.block.state.BlockState;"
                )
    return content


def add_missing_collision_context_import(content):
    """Add CollisionContext import if used but not imported."""
    has_import = "import net.minecraft.world.phys.shapes.CollisionContext;" in content
    uses_it = bool(re.search(r'\bCollisionContext\b', content))
    if not has_import and uses_it:
        # Find a good place to insert - after last phys import or any import
        match = re.search(r'(import net\.minecraft\.world\.phys[^;]*;)', content)
        if match:
            content = content.replace(
                match.group(1),
                match.group(1) + "\nimport net.minecraft.world.phys.shapes.CollisionContext;"
            )
        else:
            # Insert before class declaration
            match = re.search(r'^(public|protected|private|abstract|final)\s+class\s+', content, re.MULTILINE)
            if match:
                pos = match.start()
                content = content[:pos] + "import net.minecraft.world.phys.shapes.CollisionContext;\n" + content[pos:]
    return content


def add_missing_component_import(content):
    """Add Component import if Text was replaced by Component but import is missing."""
    has_component_import = "import net.minecraft.network.chat.Component;" in content
    has_text_import = "import net.minecraft.network.chat.Text;" in content
    uses_component = bool(re.search(r'\bComponent\b', content))

    if not has_component_import and not has_text_import and uses_component:
        match = re.search(r'(import net\.minecraft[^;]+;)', content)
        if match:
            insert_pos = content.rfind('\nimport ')
            # Find last import line
            last_import = list(re.finditer(r'^import [^;]+;', content, re.MULTILINE))
            if last_import:
                last = last_import[-1]
                content = content[:last.end()] + "\nimport net.minecraft.network.chat.Component;" + content[last.end():]
    return content


def process_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    original = content

    # 1. Import replacements
    for old, new in IMPORT_REPLACEMENTS:
        content = content.replace(old, new)

    # 2. Inline FQN replacements
    for old, new in INLINE_FQN:
        content = content.replace(old, new)

    # 3. Regex code body replacements
    for pattern, replacement in CODE_REPLACEMENTS:
        content = re.sub(pattern, replacement, content)

    # 4. Add missing imports
    content = add_missing_block_state_import(content)
    content = add_missing_collision_context_import(content)

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
