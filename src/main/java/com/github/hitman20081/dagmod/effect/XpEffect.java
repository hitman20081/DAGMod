package com.github.hitman20081.dagmod.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;

public class XpEffect extends MobEffect {
    protected XpEffect() {
        // category: MobEffectCategory - describes if the effect is helpful (BENEFICIAL), harmful (HARMFUL) or useless (NEUTRAL)
        // color: int - Color is the color assigned to the effect (in RGB)
        super(MobEffectCategory.BENEFICIAL, 0xe9b8b3);
    }

    // Called every tick to check if the effect can be applied or not
    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        // In our case, we just make it return true so that it applies the effect every tick
        return true;
    }

    // Called when the effect is applied.
    @Override
    public boolean applyEffectTick(ServerLevel world, LivingEntity entity, int amplifier) {
        if (entity instanceof Player) {
            ((Player) entity).giveExperiencePoints(1 << amplifier); // Higher amplifier gives you experience faster
        }
        return super.applyEffectTick(world, entity, amplifier);
    }
}
