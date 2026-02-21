package com.github.hitman20081.dagmod.mixin.client;

import com.github.hitman20081.dagmod.DagMod;
import com.github.hitman20081.dagmod.item.DagModShieldItem;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.model.ShieldEntityModel;
import net.minecraft.client.render.item.model.special.ShieldModelRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.ComponentMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin to render custom shield textures for DagMod shields.
 * Tracks the current shield item via getData() and renders custom textures in render().
 */
@Mixin(ShieldModelRenderer.class)
public class ShieldRendererMixin {

    @Shadow
    @Final
    private ShieldEntityModel model;

    @Unique
    private static final ThreadLocal<Item> CURRENT_SHIELD_ITEM = new ThreadLocal<>();

    /**
     * Track which shield item is being rendered by intercepting getData().
     * This is called right before render() for each shield.
     */
    @Inject(method = "getData(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/component/ComponentMap;", at = @At("HEAD"))
    private void dagmod$trackShieldItem(ItemStack stack, CallbackInfoReturnable<ComponentMap> cir) {
        if (stack != null && !stack.isEmpty() && stack.getItem() instanceof DagModShieldItem) {
            CURRENT_SHIELD_ITEM.set(stack.getItem());
        } else {
            CURRENT_SHIELD_ITEM.remove();
        }
    }

    @Inject(method = "render(Lnet/minecraft/component/ComponentMap;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;IIZI)V",
            at = @At("HEAD"), cancellable = true)
    private void dagmod$renderCustomShield(ComponentMap components, ItemDisplayContext displayContext,
            MatrixStack matrices, OrderedRenderCommandQueue queue, int light, int overlay,
            boolean hasGlint, int sortPriority, CallbackInfo ci) {

        Item currentItem = CURRENT_SHIELD_ITEM.get();
        if (currentItem instanceof DagModShieldItem shieldItem) {
            Identifier textureId = Identifier.of(DagMod.MOD_ID, "textures/entity/shield/" + shieldItem.shieldType + "_shield.png");

            matrices.push();
            matrices.scale(1.0F, -1.0F, -1.0F);

            RenderLayer layer = this.model.getLayer(textureId);

            final int capturedLight = light;
            final int capturedOverlay = overlay;

            // Render handle using submitCustom for direct vertex consumer access
            queue.submitCustom(matrices, layer, (entry, vertexConsumer) -> {
                MatrixStack tempStack = new MatrixStack();
                tempStack.peek().copy(entry);
                this.model.getHandle().render(tempStack, vertexConsumer, capturedLight, capturedOverlay);
            });

            // Render plate
            queue.submitCustom(matrices, layer, (entry, vertexConsumer) -> {
                MatrixStack tempStack = new MatrixStack();
                tempStack.peek().copy(entry);
                this.model.getPlate().render(tempStack, vertexConsumer, capturedLight, capturedOverlay);
            });

            if (hasGlint) {
                RenderLayer glintLayer = RenderLayers.entityGlint();
                queue.submitCustom(matrices, glintLayer, (entry, vertexConsumer) -> {
                    MatrixStack tempStack = new MatrixStack();
                    tempStack.peek().copy(entry);
                    this.model.getHandle().render(tempStack, vertexConsumer, capturedLight, capturedOverlay);
                    this.model.getPlate().render(tempStack, vertexConsumer, capturedLight, capturedOverlay);
                });
            }

            matrices.pop();
            ci.cancel();
        }
    }
}
