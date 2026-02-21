package com.github.hitman20081.dagmod.mixin;

import com.github.hitman20081.dagmod.DagMod;
import com.github.hitman20081.dagmod.accessor.ChestRenderStateAccessor;
import com.github.hitman20081.dagmod.block.entity.IronChestBlockEntity;
import com.github.hitman20081.dagmod.block.entity.ModBlockEntities;
import com.github.hitman20081.dagmod.bone_realm.chest.BoneRealmChestRegistry;
import com.github.hitman20081.dagmod.bone_realm.chest.LockedBoneChestBlock;
import com.github.hitman20081.dagmod.bone_realm.chest.LockedBoneChestBlockEntity;
import com.github.hitman20081.dagmod.util.ChestTextureHolder;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LidOpenable;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.block.entity.state.ChestBlockEntityRenderState;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class LockedChestTextureMixin {

    @Mixin(ChestBlockEntityRenderer.class)
    public static abstract class ChestRendererMixin<T extends BlockEntity & LidOpenable> {

        @Inject(
                method = "updateRenderState",
                at = @At("HEAD")
        )
        private void captureCustomChest(
                T entity,
                ChestBlockEntityRenderState state,
                float tickDelta,
                Vec3d cameraPos,
                ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlayCommand,
                CallbackInfo ci) {

            ChestRenderStateAccessor accessor = (ChestRenderStateAccessor) state;

            // Check by instanceof first
            if (entity instanceof LockedBoneChestBlockEntity customChest) {
                LockedBoneChestBlock.LockedChestType chestType = customChest.getChestType();
                accessor.dagmod$setCustomChestType(chestType);
                accessor.dagmod$setCustomTextureName(null);
            } else if (entity instanceof IronChestBlockEntity) {
                // Iron Chest uses custom texture but isn't a locked chest
                accessor.dagmod$setCustomChestType(null);
                accessor.dagmod$setCustomTextureName("iron_chest");
            }
            // Also check by block entity TYPE (for item rendering where the entity might be a generic ChestBlockEntity)
            else if (entity.getType() == ModBlockEntities.IRON_CHEST) {
                accessor.dagmod$setCustomChestType(null);
                accessor.dagmod$setCustomTextureName("iron_chest");
            } else if (entity.getType() == BoneRealmChestRegistry.LOCKED_BONE_CHEST_ENTITY) {
                // For item rendering, we can't get the specific type, so default to bone realm
                accessor.dagmod$setCustomChestType(LockedBoneChestBlock.LockedChestType.BONE_REALM);
                accessor.dagmod$setCustomTextureName(null);
            } else {
                // IMPORTANT: Explicitly set to null for vanilla chests
                accessor.dagmod$setCustomChestType(null);
                accessor.dagmod$setCustomTextureName(null);
            }
        }

        // Set ThreadLocal right before rendering each chest
        @Inject(
                method = "render(Lnet/minecraft/client/render/block/entity/state/BlockEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V",
                at = @At("HEAD"),
                require = 0
        )
        private void setThreadLocalBeforeRender(BlockEntityRenderState state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraState, CallbackInfo ci) {

            // CRITICAL: Check for null state
            if (state == null) {
                ChestTextureHolder.clear();
            } else {
                ChestRenderStateAccessor accessor = (ChestRenderStateAccessor) state;

                // Get the locked chest type from THIS specific chest's render state
                LockedBoneChestBlock.LockedChestType customType = accessor.dagmod$getCustomChestType();
                ChestTextureHolder.setCustomChestType(customType);

                // Also get custom texture name (for non-locked custom chests like Iron Chest)
                String customTextureName = accessor.dagmod$getCustomTextureName();
                ChestTextureHolder.setCustomTextureName(customTextureName);
            }

        }
    }

    @Mixin(TexturedRenderLayers.class)
    public static class TexturedRenderLayersMixin {

        @Inject(
                method = "getChestTextureId(Lnet/minecraft/client/render/block/entity/state/ChestBlockEntityRenderState$Variant;Lnet/minecraft/block/enums/ChestType;)Lnet/minecraft/client/util/SpriteIdentifier;",
                at = @At("HEAD"),
                cancellable = true
        )
        private static void useCustomTexture(
                ChestBlockEntityRenderState.Variant variant,
                ChestType chestType,
                CallbackInfoReturnable<SpriteIdentifier> cir) {

            // First check for custom texture name (for non-locked custom chests like Iron Chest)
            String customTextureName = ChestTextureHolder.getCustomTextureName();
            if (customTextureName != null) {
                String suffix;
                if (chestType == ChestType.LEFT) {
                    suffix = "_left";
                } else if (chestType == ChestType.RIGHT) {
                    suffix = "_right";
                } else {
                    suffix = "";
                }

                SpriteIdentifier customTexture = new SpriteIdentifier(
                        TexturedRenderLayers.CHEST_ATLAS_TEXTURE,
                        Identifier.of(DagMod.MOD_ID, "entity/chest/" + customTextureName + suffix)
                );

                cir.setReturnValue(customTexture);
                return;
            }

            // Then check for locked chest type
            LockedBoneChestBlock.LockedChestType customType = ChestTextureHolder.getCustomChestType();

            // ONLY override if we have a custom type
            if (customType != null) {
                // Use if-else instead of switch to avoid inner class creation
                String textureName;
                if (customType == LockedBoneChestBlock.LockedChestType.SKELETON_KING) {
                    textureName = "skeleton_king_chest";
                } else if (customType == LockedBoneChestBlock.LockedChestType.BONE_REALM) {
                    textureName = "bone_realm_locked_chest";
                } else {
                    return; // Unknown type - let vanilla handle it
                }

                String suffix;
                if (chestType == ChestType.LEFT) {
                    suffix = "_left";
                } else if (chestType == ChestType.RIGHT) {
                    suffix = "_right";
                } else {
                    suffix = "";
                }

                SpriteIdentifier customTexture = new SpriteIdentifier(
                        TexturedRenderLayers.CHEST_ATLAS_TEXTURE,
                        Identifier.of(DagMod.MOD_ID, "entity/chest/" + textureName + suffix)
                );

                cir.setReturnValue(customTexture);
            }
            // If both are null, we do NOTHING and let vanilla handle it
        }
    }
}