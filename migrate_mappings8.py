#!/usr/bin/env python3
"""Pass 8: Fix remaining API issues found after pass 7 compile."""

import os
import re

SRC = os.path.join(os.path.dirname(__file__), "src", "main", "java")

CODE_REPLACEMENTS = [
    # MobEffects/MobEffectInstance package was placed in wrong path by earlier passes
    (r'net\.minecraft\.world\.entity\.effect\.', 'net.minecraft.world.effect.'),

    # addStatusEffect -> addEffect (Yarn -> Mojang)
    (r'\.addStatusEffect\(', '.addEffect('),
    (r'\.removeStatusEffect\(', '.removeEffect('),

    # Stats field name fix (we used MINE_BLOCK, correct is BLOCK_MINED)
    (r'\bStats\.MINE_BLOCK\b', 'Stats.BLOCK_MINED'),

    # Mob method renames (Yarn -> Mojang)
    (r'\binitGoals\b', 'registerGoals'),
    (r'\bcanTarget\b', 'canAttack'),
    (r'\binteractMob\b', 'mobInteract'),

    # Shapes union (Yarn -> Mojang)
    (r'\bShapes\.union\(', 'Shapes.or('),

    # SynchedEntityData getValue -> get (from entityData.getValue(x) -> entityData.get(x))
    (r'\bentityData\.getValue\(', 'entityData.get('),

    # Goal class renames (Yarn -> Mojang)
    (r'\bRevengeGoal\b', 'HurtByTargetGoal'),
    (r'\bActiveTargetGoal\b', 'NearestAttackableTargetGoal'),
    (r'\bFlyGoal\b', 'WaterAvoidingRandomFlyingGoal'),

    # Attack tracking renames (Yarn -> Mojang)
    (r'\bgetLastAttackedTime\(\)', 'getLastHurtByMobTimestamp()'),
    (r'\bgetLastAttackTime\(\)', 'getLastHurtMobTimestamp()'),
    (r'\bgetAttacker\(\)', 'getLastAttacker()'),
    (r'\bgetAttacking\(\)', 'getLastHurtMob()'),

    # Vec3.multiply(scalar) doesn't exist in Mojang; use scale(scalar)
    # Only match single-argument calls (no comma in the argument)
    (r'\.multiply\(([^,)]+)\)', r'.scale(\1)'),

    # Player.sendSystemMessage with boolean second arg -> sendOverlayMessage or sendSystemMessage
    # (fix_send_message was missed for sendSystemMessage(x, true) calls)
]

# Multiline: sendSystemMessage(expr, true) -> sendOverlayMessage(expr)
# sendSystemMessage(expr, false) -> sendSystemMessage(expr)
def fix_send_system_message(content):
    """Fix sendSystemMessage(Component, boolean) calls split across lines."""
    # sendSystemMessage(..., true) -> sendOverlayMessage(...)
    content = re.sub(
        r'\.sendSystemMessage\(([\s\S]*?),\s*true\s*\)',
        lambda m: '.sendOverlayMessage(' + m.group(1) + ')',
        content
    )
    # sendSystemMessage(..., false) -> sendSystemMessage(...)
    content = re.sub(
        r'\.sendSystemMessage\(([\s\S]*?),\s*false\s*\)',
        lambda m: '.sendSystemMessage(' + m.group(1) + ')',
        content
    )
    return content


def process_file(path):
    with open(path, 'r', encoding='utf-8') as f:
        original = f.read()

    content = original
    for pattern, replacement in CODE_REPLACEMENTS:
        content = re.sub(pattern, replacement, content)

    content = fix_send_system_message(content)

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

    print(f"\nPass 8 complete: {updated}/{total} files updated.")


if __name__ == '__main__':
    main()
