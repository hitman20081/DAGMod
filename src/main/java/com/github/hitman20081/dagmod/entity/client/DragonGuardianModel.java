// Made with Blockbench 5.0.3
// Adapted for DAGMod Fabric 1.21

package com.github.hitman20081.dagmod.entity.client;

import com.github.hitman20081.dagmod.DagMod;
import com.github.hitman20081.dagmod.entity.DragonGuardianEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

/**
 * Custom dragon model with procedural state-based animations
 * Features: body, 5-segment neck, head with jaw, wings, 4 legs, 12-segment tail
 * Texture: 512x512 dragon_guardian.png
 * Animations vary based on dragon's state (idle, flying, attacking, swooping, roaring, fire breathing, landing, perched)
 */
public class DragonGuardianModel extends EntityModel<DragonGuardianRenderState> {

    public static final EntityModelLayer LAYER_LOCATION = new EntityModelLayer(Identifier.of(DagMod.MOD_ID, "dragon_guardian"), "main");

    private final ModelPart body;
    private final ModelPart neck5;
    private final ModelPart neck4;
    private final ModelPart neck3;
    private final ModelPart neck2;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart jaw;
    private final ModelPart wing;
    private final ModelPart wingtip;
    private final ModelPart wing1;
    private final ModelPart wingtip1;
    private final ModelPart rearleg;
    private final ModelPart rearlegtip;
    private final ModelPart rearfoot;
    private final ModelPart rearleg1;
    private final ModelPart rearlegtip1;
    private final ModelPart rearfoot1;
    private final ModelPart frontleg;
    private final ModelPart frontlegtip;
    private final ModelPart frontfoot;
    private final ModelPart frontleg1;
    private final ModelPart frontlegtip1;
    private final ModelPart frontfoot1;
    private final ModelPart tail;
    private final ModelPart tail2;
    private final ModelPart tail3;
    private final ModelPart tail4;
    private final ModelPart tail5;
    private final ModelPart tail6;
    private final ModelPart tail7;
    private final ModelPart tail8;
    private final ModelPart tail9;
    private final ModelPart tail10;
    private final ModelPart tail11;
    private final ModelPart tail12;

    public DragonGuardianModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
        this.neck5 = this.body.getChild("neck5");
        this.neck4 = this.neck5.getChild("neck4");
        this.neck3 = this.neck4.getChild("neck3");
        this.neck2 = this.neck3.getChild("neck2");
        this.neck = this.neck2.getChild("neck");
        this.head = this.neck.getChild("head");
        this.jaw = this.head.getChild("jaw");
        this.wing = this.body.getChild("wing");
        this.wingtip = this.wing.getChild("wingtip");
        this.wing1 = this.body.getChild("wing1");
        this.wingtip1 = this.wing1.getChild("wingtip1");
        this.rearleg = this.body.getChild("rearleg");
        this.rearlegtip = this.rearleg.getChild("rearlegtip");
        this.rearfoot = this.rearlegtip.getChild("rearfoot");
        this.rearleg1 = this.body.getChild("rearleg1");
        this.rearlegtip1 = this.rearleg1.getChild("rearlegtip1");
        this.rearfoot1 = this.rearlegtip1.getChild("rearfoot1");
        this.frontleg = this.body.getChild("frontleg");
        this.frontlegtip = this.frontleg.getChild("frontlegtip");
        this.frontfoot = this.frontlegtip.getChild("frontfoot");
        this.frontleg1 = this.body.getChild("frontleg1");
        this.frontlegtip1 = this.frontleg1.getChild("frontlegtip1");
        this.frontfoot1 = this.frontlegtip1.getChild("frontfoot1");
        this.tail = this.body.getChild("tail");
        this.tail2 = this.tail.getChild("tail2");
        this.tail3 = this.tail2.getChild("tail3");
        this.tail4 = this.tail3.getChild("tail4");
        this.tail5 = this.tail4.getChild("tail5");
        this.tail6 = this.tail5.getChild("tail6");
        this.tail7 = this.tail6.getChild("tail7");
        this.tail8 = this.tail7.getChild("tail8");
        this.tail9 = this.tail8.getChild("tail9");
        this.tail10 = this.tail9.getChild("tail10");
        this.tail11 = this.tail10.getChild("tail11");
        this.tail12 = this.tail11.getChild("tail12");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        ModelPartData body = modelPartData.addChild("body",
            ModelPartBuilder.create()
                .uv(0, 224).cuboid(-12.0F, 0.0F, -16.0F, 24.0F, 24.0F, 64.0F)
                .uv(184, 317).cuboid(-1.0F, -6.0F, -10.0F, 2.0F, 6.0F, 12.0F)
                .uv(320, 148).cuboid(-1.0F, -6.0F, 10.0F, 2.0F, 6.0F, 12.0F)
                .uv(320, 166).cuboid(-1.0F, -6.0F, 30.0F, 2.0F, 6.0F, 12.0F),
            ModelTransform.of(0.0F, 4.0F, 8.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData neck5 = body.addChild("neck5",
            ModelPartBuilder.create()
                .uv(232, 292).cuboid(-5.0F, -5.0F, -10.0F, 10.0F, 10.0F, 10.0F)
                .uv(320, 322).cuboid(-1.0F, -9.0F, -8.0F, 2.0F, 4.0F, 6.0F),
            ModelTransform.of(0.0F, 12.0F, -16.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData neck4 = neck5.addChild("neck4",
            ModelPartBuilder.create()
                .uv(288, 272).cuboid(-5.0F, -5.0F, -10.0F, 10.0F, 10.0F, 10.0F)
                .uv(320, 312).cuboid(-1.0F, -9.0F, -8.0F, 2.0F, 4.0F, 6.0F),
            ModelTransform.of(0.0F, 0.0F, -10.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData neck3 = neck4.addChild("neck3",
            ModelPartBuilder.create()
                .uv(288, 252).cuboid(-5.0F, -5.0F, -10.0F, 10.0F, 10.0F, 10.0F)
                .uv(320, 194).cuboid(-1.0F, -9.0F, -8.0F, 2.0F, 4.0F, 6.0F),
            ModelTransform.of(0.0F, 0.0F, -10.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData neck2 = neck3.addChild("neck2",
            ModelPartBuilder.create()
                .uv(288, 232).cuboid(-5.0F, -5.0F, -10.0F, 10.0F, 10.0F, 10.0F)
                .uv(320, 184).cuboid(-1.0F, -9.0F, -8.0F, 2.0F, 4.0F, 6.0F),
            ModelTransform.of(0.0F, 0.0F, -10.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData neck = neck2.addChild("neck",
            ModelPartBuilder.create()
                .uv(288, 212).cuboid(-5.0F, -5.0F, -10.0F, 10.0F, 10.0F, 10.0F)
                .uv(216, 297).cuboid(-1.0F, -9.0F, -8.0F, 2.0F, 4.0F, 6.0F),
            ModelTransform.of(0.0F, 0.0F, -10.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData head = neck.addChild("head",
            ModelPartBuilder.create()
                .uv(176, 256).cuboid(-6.0F, -1.0F, -30.0F, 12.0F, 5.0F, 16.0F)
                .uv(176, 224).cuboid(-8.0F, -8.0F, -16.0F, 16.0F, 16.0F, 16.0F)
                .uv(224, 204).cuboid(-5.0F, -12.0F, -10.0F, 2.0F, 4.0F, 6.0F)
                .uv(328, 284).cuboid(-5.0F, -3.0F, -28.0F, 2.0F, 2.0F, 4.0F)
                .uv(224, 214).cuboid(3.0F, -12.0F, -10.0F, 2.0F, 4.0F, 6.0F)
                .uv(64, 332).cuboid(3.0F, -3.0F, -28.0F, 2.0F, 2.0F, 4.0F),
            ModelTransform.of(0.0F, -1.0F, -10.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData jaw = head.addChild("jaw",
            ModelPartBuilder.create()
                .uv(176, 277).cuboid(-6.0F, 0.0F, -15.0F, 12.0F, 4.0F, 16.0F),
            ModelTransform.of(0.0F, 4.0F, -15.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData wing = body.addChild("wing",
            ModelPartBuilder.create()
                .uv(224, 0).cuboid(0.0F, -4.0F, -4.0F, 56.0F, 8.0F, 8.0F)
                .uv(0, 0).cuboid(0.0F, 0.0F, 2.0F, 56.0F, 0.0F, 56.0F),
            ModelTransform.of(12.0F, 1.0F, -6.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData wingtip = wing.addChild("wingtip",
            ModelPartBuilder.create()
                .uv(224, 32).cuboid(0.0F, -2.0F, -2.0F, 56.0F, 4.0F, 4.0F)
                .uv(0, 56).cuboid(0.0F, 0.0F, 2.0F, 56.0F, 0.0F, 56.0F),
            ModelTransform.of(56.0F, 0.0F, -2.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData wing1 = body.addChild("wing1",
            ModelPartBuilder.create()
                .uv(224, 16).cuboid(-56.0F, -4.0F, -4.0F, 56.0F, 8.0F, 8.0F)
                .uv(0, 112).cuboid(-56.0F, 0.0F, 2.0F, 56.0F, 0.0F, 56.0F),
            ModelTransform.of(-12.0F, 1.0F, -6.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData wingtip1 = wing1.addChild("wingtip1",
            ModelPartBuilder.create()
                .uv(224, 40).cuboid(-56.0F, -2.0F, -2.0F, 56.0F, 4.0F, 4.0F)
                .uv(0, 168).cuboid(-56.0F, 0.0F, 2.0F, 56.0F, 0.0F, 56.0F),
            ModelTransform.of(-56.0F, 0.0F, -2.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData rearleg = body.addChild("rearleg",
            ModelPartBuilder.create()
                .uv(224, 108).cuboid(-8.0F, -4.0F, -8.0F, 16.0F, 32.0F, 16.0F),
            ModelTransform.of(-16.0F, 12.0F, 34.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData rearlegtip = rearleg.addChild("rearlegtip",
            ModelPartBuilder.create()
                .uv(240, 204).cuboid(-6.0F, -2.0F, 0.0F, 12.0F, 32.0F, 12.0F),
            ModelTransform.of(0.0F, 30.0F, -6.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData rearfoot = rearlegtip.addChild("rearfoot",
            ModelPartBuilder.create()
                .uv(224, 48).cuboid(-9.0F, 0.0F, -20.0F, 18.0F, 6.0F, 24.0F),
            ModelTransform.of(0.0F, 26.0F, 8.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData rearleg1 = body.addChild("rearleg1",
            ModelPartBuilder.create()
                .uv(224, 156).cuboid(-8.0F, -4.0F, -8.0F, 16.0F, 32.0F, 16.0F),
            ModelTransform.of(16.0F, 12.0F, 34.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData rearlegtip1 = rearleg1.addChild("rearlegtip1",
            ModelPartBuilder.create()
                .uv(240, 248).cuboid(-6.0F, -2.0F, 0.0F, 12.0F, 32.0F, 12.0F),
            ModelTransform.of(0.0F, 30.0F, -6.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData rearfoot1 = rearlegtip1.addChild("rearfoot1",
            ModelPartBuilder.create()
                .uv(224, 78).cuboid(-9.0F, 0.0F, -20.0F, 18.0F, 6.0F, 24.0F),
            ModelTransform.of(0.0F, 26.0F, 8.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData frontleg = body.addChild("frontleg",
            ModelPartBuilder.create()
                .uv(288, 148).cuboid(-4.0F, -4.0F, -4.0F, 8.0F, 24.0F, 8.0F),
            ModelTransform.of(-12.0F, 16.0F, -6.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData frontlegtip = frontleg.addChild("frontlegtip",
            ModelPartBuilder.create()
                .uv(296, 312).cuboid(-3.0F, -1.0F, -3.0F, 6.0F, 24.0F, 6.0F),
            ModelTransform.of(0.0F, 21.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData frontfoot = frontlegtip.addChild("frontfoot",
            ModelPartBuilder.create()
                .uv(288, 108).cuboid(-4.0F, 0.0F, -12.0F, 8.0F, 4.0F, 16.0F),
            ModelTransform.of(0.0F, 19.0F, -1.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData frontleg1 = body.addChild("frontleg1",
            ModelPartBuilder.create()
                .uv(288, 180).cuboid(-4.0F, -4.0F, -4.0F, 8.0F, 24.0F, 8.0F),
            ModelTransform.of(12.0F, 16.0F, -6.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData frontlegtip1 = frontleg1.addChild("frontlegtip1",
            ModelPartBuilder.create()
                .uv(160, 317).cuboid(-3.0F, -1.0F, -3.0F, 6.0F, 24.0F, 6.0F),
            ModelTransform.of(0.0F, 21.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData frontfoot1 = frontlegtip1.addChild("frontfoot1",
            ModelPartBuilder.create()
                .uv(288, 128).cuboid(-4.0F, 0.0F, -12.0F, 8.0F, 4.0F, 16.0F),
            ModelTransform.of(0.0F, 19.0F, -1.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData tail = body.addChild("tail",
            ModelPartBuilder.create()
                .uv(272, 292).cuboid(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 10.0F)
                .uv(328, 204).cuboid(-1.0F, -9.0F, 2.0F, 2.0F, 4.0F, 6.0F),
            ModelTransform.of(0.0F, 6.0F, 48.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData tail2 = tail.addChild("tail2",
            ModelPartBuilder.create()
                .uv(176, 297).cuboid(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 10.0F)
                .uv(328, 214).cuboid(-1.0F, -9.0F, 2.0F, 2.0F, 4.0F, 6.0F),
            ModelTransform.of(0.0F, 0.0F, 10.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData tail3 = tail2.addChild("tail3",
            ModelPartBuilder.create()
                .uv(308, 48).cuboid(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 10.0F)
                .uv(328, 224).cuboid(-1.0F, -9.0F, 2.0F, 2.0F, 4.0F, 6.0F),
            ModelTransform.of(0.0F, 0.0F, 10.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData tail4 = tail3.addChild("tail4",
            ModelPartBuilder.create()
                .uv(308, 68).cuboid(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 10.0F)
                .uv(328, 234).cuboid(-1.0F, -9.0F, 2.0F, 2.0F, 4.0F, 6.0F),
            ModelTransform.of(0.0F, 0.0F, 10.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData tail5 = tail4.addChild("tail5",
            ModelPartBuilder.create()
                .uv(308, 88).cuboid(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 10.0F)
                .uv(328, 244).cuboid(-1.0F, -9.0F, 2.0F, 2.0F, 4.0F, 6.0F),
            ModelTransform.of(0.0F, 0.0F, 10.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData tail6 = tail5.addChild("tail6",
            ModelPartBuilder.create()
                .uv(0, 312).cuboid(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 10.0F)
                .uv(328, 254).cuboid(-1.0F, -9.0F, 2.0F, 2.0F, 4.0F, 6.0F),
            ModelTransform.of(0.0F, 0.0F, 10.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData tail7 = tail6.addChild("tail7",
            ModelPartBuilder.create()
                .uv(40, 312).cuboid(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 10.0F)
                .uv(328, 264).cuboid(-1.0F, -9.0F, 2.0F, 2.0F, 4.0F, 6.0F),
            ModelTransform.of(0.0F, 0.0F, 10.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData tail8 = tail7.addChild("tail8",
            ModelPartBuilder.create()
                .uv(80, 312).cuboid(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 10.0F)
                .uv(328, 274).cuboid(-1.0F, -9.0F, 2.0F, 2.0F, 4.0F, 6.0F),
            ModelTransform.of(0.0F, 0.0F, 10.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData tail9 = tail8.addChild("tail9",
            ModelPartBuilder.create()
                .uv(120, 312).cuboid(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 10.0F)
                .uv(0, 332).cuboid(-1.0F, -9.0F, 2.0F, 2.0F, 4.0F, 6.0F),
            ModelTransform.of(0.0F, 0.0F, 10.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData tail10 = tail9.addChild("tail10",
            ModelPartBuilder.create()
                .uv(216, 312).cuboid(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 10.0F)
                .uv(16, 332).cuboid(-1.0F, -9.0F, 2.0F, 2.0F, 4.0F, 6.0F),
            ModelTransform.of(0.0F, 0.0F, 10.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData tail11 = tail10.addChild("tail11",
            ModelPartBuilder.create()
                .uv(256, 312).cuboid(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 10.0F)
                .uv(32, 332).cuboid(-1.0F, -9.0F, 2.0F, 2.0F, 4.0F, 6.0F),
            ModelTransform.of(0.0F, 0.0F, 10.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData tail12 = tail11.addChild("tail12",
            ModelPartBuilder.create()
                .uv(312, 292).cuboid(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 10.0F)
                .uv(48, 332).cuboid(-1.0F, -9.0F, 2.0F, 2.0F, 4.0F, 6.0F),
            ModelTransform.of(0.0F, 0.0F, 10.0F, 0.0F, 0.0F, 0.0F));

        return TexturedModelData.of(modelData, 512, 512);
    }

    @Override
    public void setAngles(DragonGuardianRenderState state) {
        super.setAngles(state);

        float age = state.age;
        float animSpeed = state.animationSpeed; // Variable speed based on state
        DragonGuardianEntity.AnimationState animState = state.animationState;

        // === STATE-SPECIFIC ANIMATIONS ===
        switch (animState) {
            case PERCHED -> animatePerched(state);
            case ROARING -> animateRoaring(state, age, animSpeed);
            case FIRE_BREATHING -> animateFireBreathing(state, age, animSpeed);
            case SWOOPING -> animateSwooping(state, age, animSpeed);
            case LANDING -> animateLanding(state, age, animSpeed);
            default -> animateFlying(state, age, animSpeed); // IDLE, FLYING, ATTACKING
        }
    }

    private void animateFlying(DragonGuardianRenderState state, float age, float animSpeed) {
        // === WING FLAPPING ===
        float flapCycle = age * 0.4F * animSpeed; // Variable speed based on state
        float wingFlap = (float) Math.cos(flapCycle) * 1.2F;

        this.wing.roll = wingFlap - 0.3F;
        this.wing1.roll = -wingFlap + 0.3F;

        float wingPitch = (float) Math.sin(flapCycle) * 0.2F;
        this.wing.pitch = wingPitch;
        this.wing1.pitch = wingPitch;

        float tipFlap = (float) Math.sin(flapCycle + 0.5F) * 1.5F;
        this.wingtip.roll = tipFlap - 0.5F;
        this.wingtip1.roll = -tipFlap + 0.5F;

        // === TAIL SWAYING ===
        float tailBase = (float) Math.sin(age * 0.1F * animSpeed) * 0.15F;
        this.tail.yaw = tailBase;
        this.tail2.yaw = tailBase * 1.2F;
        this.tail3.yaw = tailBase * 1.4F;
        this.tail4.yaw = tailBase * 1.6F;
        this.tail5.yaw = tailBase * 1.8F;
        this.tail6.yaw = tailBase * 2.0F;
        this.tail7.yaw = tailBase * 2.2F;
        this.tail8.yaw = tailBase * 2.4F;
        this.tail9.yaw = tailBase * 2.6F;
        this.tail10.yaw = tailBase * 2.8F;
        this.tail11.yaw = tailBase * 3.0F;
        this.tail12.yaw = tailBase * 3.2F;

        // === NECK BOBBING ===
        float neckBob = (float) Math.sin(age * 0.15F * animSpeed) * 0.08F;
        this.neck5.pitch = neckBob;
        this.neck4.pitch = neckBob * 0.9F;
        this.neck3.pitch = neckBob * 0.8F;
        this.neck2.pitch = neckBob * 0.7F;
        this.neck.pitch = neckBob * 0.6F;

        // === HEAD & JAW ===
        float headBreathing = (float) Math.cos(age * 0.2F) * 0.05F;
        this.head.pitch = headBreathing;
        this.jaw.pitch = Math.abs((float) Math.sin(age * 0.15F)) * 0.1F;

        // === LEGS ===
        float legSwing = (float) Math.sin(age * 0.12F * animSpeed) * 0.15F;
        this.rearleg.pitch = legSwing * 0.5F;
        this.rearleg1.pitch = -legSwing * 0.5F;
        this.rearlegtip.pitch = -legSwing * 0.3F;
        this.rearlegtip1.pitch = legSwing * 0.3F;
        this.frontleg.pitch = -legSwing * 0.6F;
        this.frontleg1.pitch = legSwing * 0.6F;
        this.frontlegtip.pitch = legSwing * 0.4F;
        this.frontlegtip1.pitch = -legSwing * 0.4F;
    }

    private void animatePerched(DragonGuardianRenderState state) {
        // Wings folded against body
        this.wing.roll = -1.5F;
        this.wing1.roll = 1.5F;
        this.wingtip.roll = -2.0F;
        this.wingtip1.roll = 2.0F;

        // Tail curled
        float tailCurl = 0.2F;
        for (int i = 1; i <= 12; i++) {
            ModelPart tailSegment = switch (i) {
                case 1 -> this.tail;
                case 2 -> this.tail2;
                case 3 -> this.tail3;
                case 4 -> this.tail4;
                case 5 -> this.tail5;
                case 6 -> this.tail6;
                case 7 -> this.tail7;
                case 8 -> this.tail8;
                case 9 -> this.tail9;
                case 10 -> this.tail10;
                case 11 -> this.tail11;
                default -> this.tail12;
            };
            tailSegment.yaw = tailCurl * i * 0.1F;
        }

        // Neck relaxed
        this.neck5.pitch = -0.1F;
        this.neck4.pitch = -0.05F;
        this.neck3.pitch = 0.0F;
        this.neck2.pitch = 0.0F;
        this.neck.pitch = 0.0F;

        // Head alert with breathing
        float breathing = (float) Math.sin(state.age * 0.1F) * 0.03F;
        this.head.pitch = breathing;
        this.jaw.pitch = Math.abs((float) Math.sin(state.age * 0.1F)) * 0.05F;

        // Legs standing
        this.rearleg.pitch = 0.0F;
        this.rearleg1.pitch = 0.0F;
        this.frontleg.pitch = 0.0F;
        this.frontleg1.pitch = 0.0F;
    }

    private void animateRoaring(DragonGuardianRenderState state, float age, float animSpeed) {
        // Wings spread wide
        float wingSpread = (float) Math.sin(age * 0.3F) * 0.3F;
        this.wing.roll = 1.5F + wingSpread;
        this.wing1.roll = -1.5F - wingSpread;
        this.wingtip.roll = 2.0F;
        this.wingtip1.roll = -2.0F;

        // Neck extended upward
        this.neck5.pitch = -0.3F;
        this.neck4.pitch = -0.25F;
        this.neck3.pitch = -0.2F;
        this.neck2.pitch = -0.15F;
        this.neck.pitch = -0.1F;

        // Head tilted back, jaw wide open
        this.head.pitch = -0.4F;
        this.jaw.pitch = 0.8F; // Wide open

        // Tail thrashing
        float tailThrash = (float) Math.sin(age * 0.5F) * 0.4F;
        this.tail.yaw = tailThrash;
        this.tail2.yaw = tailThrash * 1.2F;
        this.tail3.yaw = tailThrash * 1.4F;
        this.tail4.yaw = tailThrash * 1.6F;
        this.tail5.yaw = tailThrash * 1.8F;
        this.tail6.yaw = tailThrash * 2.0F;
        this.tail7.yaw = tailThrash * 1.8F;
        this.tail8.yaw = tailThrash * 1.6F;
        this.tail9.yaw = tailThrash * 1.4F;
        this.tail10.yaw = tailThrash * 1.2F;
        this.tail11.yaw = tailThrash;
        this.tail12.yaw = tailThrash * 0.8F;
    }

    private void animateFireBreathing(DragonGuardianRenderState state, float age, float animSpeed) {
        // Wings steady for stability
        this.wing.roll = 0.5F;
        this.wing1.roll = -0.5F;
        this.wingtip.roll = 0.8F;
        this.wingtip1.roll = -0.8F;

        // Neck extended forward
        this.neck5.pitch = 0.1F;
        this.neck4.pitch = 0.0F;
        this.neck3.pitch = -0.05F;
        this.neck2.pitch = -0.1F;
        this.neck.pitch = -0.15F;

        // Head aimed forward, jaw opening/closing
        this.head.pitch = -0.2F;
        float jawPulse = (float) Math.sin(age * 0.5F) * 0.3F + 0.3F;
        this.jaw.pitch = jawPulse; // Pulsing open/close

        // Tail straight for balance
        float tailStraight = (float) Math.sin(age * 0.2F) * 0.05F;
        this.tail.yaw = tailStraight;
        this.tail2.yaw = tailStraight * 0.9F;
        this.tail3.yaw = tailStraight * 0.8F;
        this.tail4.yaw = tailStraight * 0.7F;
        this.tail5.yaw = tailStraight * 0.6F;
        this.tail6.yaw = tailStraight * 0.5F;
        this.tail7.yaw = tailStraight * 0.4F;
        this.tail8.yaw = tailStraight * 0.3F;
        this.tail9.yaw = tailStraight * 0.2F;
        this.tail10.yaw = tailStraight * 0.1F;
        this.tail11.yaw = 0.0F;
        this.tail12.yaw = 0.0F;
    }

    private void animateSwooping(DragonGuardianRenderState state, float age, float animSpeed) {
        // Wings swept back for speed
        float wingFast = (float) Math.cos(age * 0.8F) * 0.6F; // Fast flaps
        this.wing.roll = -0.8F + wingFast;
        this.wing1.roll = 0.8F - wingFast;
        this.wing.pitch = -0.3F; // Angled back
        this.wing1.pitch = -0.3F;

        // Wingtips tight
        this.wingtip.roll = -1.0F;
        this.wingtip1.roll = 1.0F;

        // Neck streamlined
        this.neck5.pitch = 0.2F;
        this.neck4.pitch = 0.15F;
        this.neck3.pitch = 0.1F;
        this.neck2.pitch = 0.05F;
        this.neck.pitch = 0.0F;

        // Head forward, jaw closed
        this.head.pitch = 0.1F;
        this.jaw.pitch = 0.0F;

        // Tail straight behind
        this.tail.yaw = 0.0F;
        this.tail2.yaw = 0.0F;
        this.tail3.yaw = 0.0F;
        this.tail4.yaw = 0.0F;
        this.tail5.yaw = 0.0F;
        this.tail6.yaw = 0.0F;
        this.tail7.yaw = 0.0F;
        this.tail8.yaw = 0.0F;
        this.tail9.yaw = 0.0F;
        this.tail10.yaw = 0.0F;
        this.tail11.yaw = 0.0F;
        this.tail12.yaw = 0.0F;

        // Legs tucked
        this.rearleg.pitch = 0.8F;
        this.rearleg1.pitch = 0.8F;
        this.frontleg.pitch = 0.6F;
        this.frontleg1.pitch = 0.6F;
    }

    private void animateLanding(DragonGuardianRenderState state, float age, float animSpeed) {
        // Wings spread for braking
        this.wing.roll = 1.8F;
        this.wing1.roll = -1.8F;
        this.wingtip.roll = 2.2F;
        this.wingtip1.roll = -2.2F;

        // Neck curved down
        this.neck5.pitch = 0.3F;
        this.neck4.pitch = 0.25F;
        this.neck3.pitch = 0.2F;
        this.neck2.pitch = 0.15F;
        this.neck.pitch = 0.1F;

        // Head looking down
        this.head.pitch = 0.2F;
        this.jaw.pitch = 0.05F;

        // Tail up for balance
        this.tail.pitch = -0.2F;
        this.tail2.pitch = -0.15F;
        this.tail3.pitch = -0.1F;

        // Legs extended for landing
        this.rearleg.pitch = -0.3F;
        this.rearleg1.pitch = -0.3F;
        this.frontleg.pitch = -0.2F;
        this.frontleg1.pitch = -0.2F;
    }
}
