#!/usr/bin/env python3
"""Pass 7: Fix DragonGuardianEntity, DragonEggBlock, ClassSelectionAltarBlock patterns."""

import os
import re

SRC = os.path.join(os.path.dirname(__file__), "src", "main", "java")

IMPORT_REPLACEMENTS = [
    # Fix: add Registries import where getRegistryManager/registryAccess is used
]

CODE_REPLACEMENTS = [
    # Block shape
    (r'Block\.createCuboidShape\(', 'Block.box('),

    # SynchedEntityData
    (r'SynchedEntityData\.registerData\(', 'SynchedEntityData.defineId('),
    (r'EntityDataSerializers\.INTEGER\b', 'EntityDataSerializers.INT'),
    (r'\bdataTracker\b', 'entityData'),
    (r'\binitDataTracker\b', 'defineSynchedData'),
    (r'\bonTrackedDataSet\b', 'onSyncedDataUpdated'),

    # GoalSelector
    (r'\bgoalSelector\.add\(', 'goalSelector.addGoal('),
    (r'\btargetSelector\.add\(', 'targetSelector.addGoal('),

    # Goal inner class method renames (Yarn -> Mojang)
    (r'\bcanStart\(\)', 'canUse()'),
    (r'\bshouldContinue\(\)', 'canContinueToUse()'),
    (r'\bsetControls\(', 'setFlags('),
    (r'\bControl\.MOVE\b', 'Goal.Flag.MOVE'),
    (r'\bControl\.JUMP\b', 'Goal.Flag.JUMP'),
    (r'\bControl\.TARGET\b', 'Goal.Flag.TARGET'),
    (r'\bControl\.LOOK\b', 'Goal.Flag.LOOK'),

    # Entity world access (Yarn -> Mojang)
    (r'\.getEntityWorld\(\)', '.level()'),
    (r'\bgetEntitiesByClass\(', 'getEntitiesOfClass('),
    (r'\.getBoundingBox\(\)\.expand\(', '.getBoundingBox().inflate('),
    (r'\.squaredDistanceTo\(', '.distanceToSqr('),
    (r'\.setVelocity\(', '.setDeltaMovement('),
    (r'\.getVelocity\(\)', '.getDeltaMovement()'),
    (r'\.getRotationVec\(', '.getViewVector('),
    (r'\.spawnEntity\(', '.addFreshEntity('),
    (r'\.getPlayerByUuid\(', '.getPlayerByUUID('),
    (r'getStateManager\(\)', 'getStateDefinition()'),
    (r'\.getInventory\(\)\.size\(\)', '.getInventory().getContainerSize()'),
    (r'\.markDirty\(\)', '.setChanged()'),

    # BossEvent enum renames (Yarn -> Mojang)
    (r'BossEvent\.Color\.', 'BossEvent.BossBarColor.'),
    (r'BossEvent\.Style\.', 'BossEvent.BossBarOverlay.'),
    (r'\bBossEvent\.Color\b', 'BossEvent.BossBarColor'),
    (r'\.setDarkenSky\(', '.setDarkenScreen('),

    # Experience points field (Yarn -> Mojang)
    (r'\bthis\.experiencePoints\b', 'this.xpReward'),

    # Wrong attribute package from earlier passes
    (r'net\.minecraft\.world\.entity\.attribute\.', 'net.minecraft.world.entity.ai.attributes.'),

    # Block lifecycle method renames (Yarn -> Mojang)
    (r'\bonBreak\b', 'playerWillDestroy'),
    (r'\bonPlaced\b', 'setPlacedBy'),
    (r'\bdropStack\(', 'popResource('),
    (r'Stats\.MINED\.getOrCreateStat\(', 'Stats.MINE_BLOCK.get('),
    (r'player\.incrementStat\(', 'player.awardStat('),
    (r'\.getEnchantments\(\)\.getEnchantments\(\)', '.getEnchantments().keySet()'),

    # StringRepresentable (Yarn -> Mojang)
    (r'\basString\(\)', 'getSerializedName()'),

    # Entity lifecycle renames (Yarn -> Mojang)
    (r'\bwriteCustomData\b', 'addAdditionalSaveData'),
    (r'\breadCustomData\b', 'readAdditionalSaveData'),
    (r'\bonStartedTrackingBy\b', 'startSeenByPlayer'),
    (r'\bonStoppedTrackingBy\b', 'stopSeenByPlayer'),
    (r'\bcanImmediatelyDespawn\b', 'removeWhenFarAway'),
    (r'\brefreshPositionAndAngles\b', 'moveTo'),
    (r'getNavigation\(\)\.startMovingTo\(', 'getNavigation().moveTo('),
    (r'void onDeath\(', 'void die('),
    (r'super\.onDeath\(', 'super.die('),

    # ItemEnchantments builder API
    (r'\bItemEnchantments\.Builder\b', 'ItemEnchantments.Mutable'),
    (r'\bItemEnchantments\.DEFAULT\b', 'ItemEnchantments.EMPTY'),
    (r'\.getRegistryManager\(\)\.getOrThrow\(', '.registryAccess().lookupOrThrow('),

    # Age tick counter (Yarn field -> Mojang field)
    (r'\bthis\.age\b', 'this.tickCount'),
    (r'\bdragon\.age\b', 'dragon.tickCount'),
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

    print(f"\nPass 7 complete: {updated}/{total} files updated.")


if __name__ == '__main__':
    main()
