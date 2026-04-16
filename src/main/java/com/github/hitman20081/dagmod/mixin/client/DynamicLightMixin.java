package com.github.hitman20081.dagmod.mixin.client;

import com.github.hitman20081.dagmod.client.DynamicLightManager;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.LightType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Injects into BlockRenderView.getLightLevel(LightType, BlockPos) — the
 * default interface method that World, ClientWorld, and all block-view
 * implementations delegate to for light queries.
 *
 * Targeting the interface directly means the boost propagates to:
 *   - Entity rendering  (EntityRenderer.getBlockLight → World.getLightLevel)
 *   - Chunk mesh builds (BrightnessGetter.method_68890 → ChunkRendererRegion.getLightLevel)
 *   - Any other lighting query that goes through this method
 *
 * No instanceof guard needed: this mixin is client-only (via dagmod.mixins.json
 * "client" array), DynamicLightManager.heldLightLevel is only written by the
 * client tick (always 0 on the server thread), and a small server-side effect
 * (suppressed mob spawning near a torch holder) is intentional.
 */
@Mixin(BlockRenderView.class)
@Environment(EnvType.CLIENT)
public interface DynamicLightMixin {

    @Inject(
        method = "getLightLevel(Lnet/minecraft/world/LightType;Lnet/minecraft/util/math/BlockPos;)I",
        at = @At("RETURN"),
        cancellable = true
    )
    private void dagmod$injectDynamicLight(LightType type, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        if (type != LightType.BLOCK) return;
        int boost = DynamicLightManager.getLightBoost(pos);
        if (boost > cir.getReturnValueI()) {
            cir.setReturnValue(Math.min(15, boost));
        }
    }
}
