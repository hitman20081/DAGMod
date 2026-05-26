#!/usr/bin/env python3
"""Pass 9: Fix remaining API issues found after pass 8 compile."""

import os
import re

SRC = os.path.join(os.path.dirname(__file__), "src", "main", "java")

CODE_REPLACEMENTS = [
    # NonNullList
    (r'NonNullList\.ofSize\(', 'NonNullList.withSize('),

    # ContainerHelper
    (r'ContainerHelper\.splitStack\(', 'ContainerHelper.removeItem('),
    (r'ContainerHelper\.removeStack\(', 'ContainerHelper.takeItem('),

    # ItemStack damage API (Yarn -> Mojang)
    (r'\.isDamageable\(\)', '.isDamageableItem()'),
    (r'\.getDamage\(\)', '.getDamageValue()'),
    (r'\.setDamage\(', '.setDamageValue('),

    # ItemStack grow (Yarn increment -> Mojang grow)
    (r'\.increment\(', '.grow('),

    # ItemStack max stack size
    (r'\.getMaxCount\(\)', '.getItem().getDefaultMaxStackSize()'),

    # ItemStack capCount -> limitSize
    (r'\.capCount\(', '.limitSize('),

    # getMaxCount in ImplementedInventory interface context -> getMaxStackSize
    (r'\bgetMaxCount\(', 'getMaxStackSize('),

    # BlockEntity getCachedState -> getBlockState
    (r'getCachedState\(\)', 'getBlockState()'),

    # Level.updateListeners -> sendBlockUpdated
    (r'\.updateListeners\(', '.sendBlockUpdated('),

    # EnumProperty.of -> create
    (r'EnumProperty\.of\(', 'EnumProperty.create('),

    # Holder.matchesKey -> Holder.is
    (r'\.matchesKey\(', '.is('),

    # LivingEntity.getAttributeInstance -> getAttribute
    (r'\.getAttributeInstance\(', '.getAttribute('),

    # Entity.getBlockPos -> blockPosition
    (r'\.getBlockPos\(\)', '.blockPosition()'),

    # Vec3.ofCenter -> atCenterOf
    (r'Vec3\.ofCenter\(', 'Vec3.atCenterOf('),

    # LookControl.lookAt -> setLookAt
    (r'getLookControl\(\)\.lookAt\(', 'getLookControl().setLookAt('),

    # Mob.tryAttack -> doHurtTarget
    (r'\.tryAttack\(', '.doHurtTarget('),

    # Mob.setAttacking -> setAggressive
    (r'\.setAttacking\(', '.setAggressive('),

    # Mob.getMaxLookPitchChange -> getMaxHeadXRot
    (r'\.getMaxLookPitchChange\(\)', '.getMaxHeadXRot()'),

    # Entity.getPitch -> getXRot
    (r'\.getPitch\(\)', '.getXRot()'),

    # Entity.getYaw -> getYRot
    (r'\.getYaw\(\)', '.getYRot()'),

    # Entity.isSneaking -> isShiftKeyDown
    (r'\.isSneaking\(\)', '.isShiftKeyDown()'),

    # validateTicker -> createTickerHelper (BaseEntityBlock)
    (r'\bvalidateTicker\b', 'createTickerHelper'),

    # PotionContents.matches -> PotionContents.is
    (r'\.matches\(', '.is('),

    # StateDefinition.defaultBlockState -> Block.defaultBlockState
    # (happens in registerDefaultState calls inside block constructors)
    (r'getStateDefinition\(\)\.defaultBlockState\(\)', 'this.stateDefinition.any()'),

    # BlockPos.up -> above (Yarn -> Mojang)
    # Match .up() only — not .update() or other things
    (r'(?<!\w)\.up\(\)', '.above()'),

    # BlockPos.add(int,int,int) -> offset(int,int,int)
    # Match center.add(x, y, z) patterns; safe since Vec3 uses add differently
    # Only match BlockPos-like patterns where all 3 args are plain ints/vars
    # This is risky to do globally, handle in specific files

    # markDirty(level, pos, state) static form -> BlockEntity.setChanged(level, pos, state)
    (r'\bmarkDirty\((\w+),\s*(\w+),\s*(\w+)\)', r'BlockEntity.setChanged(\1, \2, \3)'),

    # ImplementedInventory Yarn method names -> Mojang Container method names
    # These are method *calls* throughout the codebase
    (r'\.getStack\(', '.getItem('),
    (r'\.setStack\(', '.setItem('),
    # removeStack(slot, count) -> removeItem(slot, count) - 2-arg form
    # removeStack(slot) -> removeItemNoUpdate(slot) - 1-arg form
    # Handle 2-arg first since it's more specific
    (r'\.removeStack\(([^,)]+),\s*([^)]+)\)', r'.removeItem(\1, \2)'),
    (r'\.removeStack\(', '.removeItemNoUpdate('),

    # Container method names in ImplementedInventory interface
    # size() -> getContainerSize() (avoid matching List.size())
    # canPlayerUse -> stillValid
    (r'\bcanPlayerUse\b', 'stillValid'),

    # getAvailableSlots -> getSlotsForFace (WorldlyContainer)
    (r'\bgetAvailableSlots\b', 'getSlotsForFace'),

    # canInsert -> canPlaceItemThroughFace (WorldlyContainer)
    (r'\bcanInsert\b', 'canPlaceItemThroughFace'),

    # canExtract -> canTakeItemThroughFace (WorldlyContainer)
    (r'\bcanExtract\b', 'canTakeItemThroughFace'),

    # BlockEntity field renames: pos -> worldPosition, world -> level
    # These are risky to do globally, only needed in block entity files
    # Skip for now; handle manually

    # FlyingPathNavigation: remove setCanSwim call entirely
    (r'\s*\w+\.setCanSwim\([^)]*\);\n', '\n'),
    (r'\s*birdNavigation\.setCanSwim\([^)]*\);', ''),
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

    print(f"\nPass 9 complete: {updated}/{total} files updated.")


if __name__ == '__main__':
    main()
