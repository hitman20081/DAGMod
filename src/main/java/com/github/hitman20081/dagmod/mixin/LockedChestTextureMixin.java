package com.github.hitman20081.dagmod.mixin;

import com.github.hitman20081.dagmod.accessor.ChestRenderStateAccessor;
import com.github.hitman20081.dagmod.bone_realm.chest.LockedBoneChestBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChestRenderer.class)
public class LockedChestTextureMixin {

    @Unique
    private static ChestRenderState dagmod$pendingState;

    @Inject(
        method = "extractRenderState(Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/client/renderer/blockentity/state/ChestRenderState;FLnet/minecraft/world/phys/Vec3;Lnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V",
        at = @At("RETURN")
    )
    private void dagmod$onExtractRenderState(
        BlockEntity blockEntity,
        ChestRenderState state,
        float tickDelta,
        Vec3 cameraPos,
        ModelFeatureRenderer.CrumblingOverlay crumbling,
        CallbackInfo ci
    ) {
        if (blockEntity instanceof LockedBoneChestBlockEntity) {
            ((ChestRenderStateAccessor) state).dagmod$setCustomTextureName("entity/chest/bone_realm_locked_chest");
        }
    }

    @Inject(
        method = "submit(Lnet/minecraft/client/renderer/blockentity/state/ChestRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
        at = @At("HEAD")
    )
    private void dagmod$captureStateForRedirect(
        ChestRenderState state,
        PoseStack poseStack,
        SubmitNodeCollector collector,
        CameraRenderState cameraState,
        CallbackInfo ci
    ) {
        dagmod$pendingState = state;
    }

    @Redirect(
        method = "submit(Lnet/minecraft/client/renderer/blockentity/state/ChestRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/Sheets;chooseSprite(Lnet/minecraft/client/renderer/blockentity/state/ChestRenderState$ChestMaterialType;Lnet/minecraft/world/level/block/state/properties/ChestType;)Lnet/minecraft/client/resources/model/sprite/SpriteId;"
        )
    )
    private SpriteId dagmod$redirectChooseSprite(
        ChestRenderState.ChestMaterialType material,
        ChestType type
    ) {
        if (dagmod$pendingState != null) {
            String customTexture = ((ChestRenderStateAccessor) dagmod$pendingState).dagmod$getCustomTextureName();
            if (customTexture != null) {
                return new SpriteId(
                    Sheets.CHEST_SHEET,
                    Identifier.fromNamespaceAndPath("dagmod", customTexture)
                );
            }
        }
        return Sheets.chooseSprite(material, type);
    }
}
