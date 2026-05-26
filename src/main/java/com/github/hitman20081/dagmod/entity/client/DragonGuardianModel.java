// Made with Blockbench 5.0.3
// Adapted for DAGMod Fabric 1.21

package com.github.hitman20081.dagmod.entity.client;

import com.github.hitman20081.dagmod.DagMod;
import com.github.hitman20081.dagmod.entity.DragonGuardianEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.Identifier;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.PartPose;

/**
 * Custom dragon model with procedural state-based animations
 * Features: body, 5-segment neck, head with jaw, wings, 4 legs, 12-segment tail
 * Texture: 512x512 dragon_guardian.png
 * Animations vary based on dragon's state (idle, flying, attacking, swooping, roaring, fire breathing, landing, perched)
 */
public class DragonGuardianModel extends EntityModel<DragonGuardianRenderState> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Identifier.fromNamespaceAndPath(DagMod.MOD_ID, "dragon_guardian"), "main");

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

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();

        PartDefinition body = modelPartData.addOrReplaceChild("body",
            CubeListBuilder.create()
                .texOffs(0, 224).addBox(-12.0F, 0.0F, -16.0F, 24.0F, 24.0F, 64.0F)
                .texOffs(184, 317).addBox(-1.0F, -6.0F, -10.0F, 2.0F, 6.0F, 12.0F)
                .texOffs(320, 148).addBox(-1.0F, -6.0F, 10.0F, 2.0F, 6.0F, 12.0F)
                .texOffs(320, 166).addBox(-1.0F, -6.0F, 30.0F, 2.0F, 6.0F, 12.0F),
            PartPose.offsetAndRotation(0.0F, 4.0F, 8.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition neck5 = body.addOrReplaceChild("neck5",
            CubeListBuilder.create()
                .texOffs(232, 292).addBox(-5.0F, -5.0F, -10.0F, 10.0F, 10.0F, 10.0F)
                .texOffs(320, 322).addBox(-1.0F, -9.0F, -8.0F, 2.0F, 4.0F, 6.0F),
            PartPose.offsetAndRotation(0.0F, 12.0F, -16.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition neck4 = neck5.addOrReplaceChild("neck4",
            CubeListBuilder.create()
                .texOffs(288, 272).addBox(-5.0F, -5.0F, -10.0F, 10.0F, 10.0F, 10.0F)
                .texOffs(320, 312).addBox(-1.0F, -9.0F, -8.0F, 2.0F, 4.0F, 6.0F),
            PartPose.offsetAndRotation(0.0F, 0.0F, -10.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition neck3 = neck4.addOrReplaceChild("neck3",
            CubeListBuilder.create()
                .texOffs(288, 252).addBox(-5.0F, -5.0F, -10.0F, 10.0F, 10.0F, 10.0F)
                .texOffs(320, 194).addBox(-1.0F, -9.0F, -8.0F, 2.0F, 4.0F, 6.0F),
            PartPose.offsetAndRotation(0.0F, 0.0F, -10.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition neck2 = neck3.addOrReplaceChild("neck2",
            CubeListBuilder.create()
                .texOffs(288, 232).addBox(-5.0F, -5.0F, -10.0F, 10.0F, 10.0F, 10.0F)
                .texOffs(320, 184).addBox(-1.0F, -9.0F, -8.0F, 2.0F, 4.0F, 6.0F),
            PartPose.offsetAndRotation(0.0F, 0.0F, -10.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition neck = neck2.addOrReplaceChild("neck",
            CubeListBuilder.create()
                .texOffs(288, 212).addBox(-5.0F, -5.0F, -10.0F, 10.0F, 10.0F, 10.0F)
                .texOffs(216, 297).addBox(-1.0F, -9.0F, -8.0F, 2.0F, 4.0F, 6.0F),
            PartPose.offsetAndRotation(0.0F, 0.0F, -10.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition head = neck.addOrReplaceChild("head",
            CubeListBuilder.create()
                .texOffs(176, 256).addBox(-6.0F, -1.0F, -30.0F, 12.0F, 5.0F, 16.0F)
                .texOffs(176, 224).addBox(-8.0F, -8.0F, -16.0F, 16.0F, 16.0F, 16.0F)
                .texOffs(224, 204).addBox(-5.0F, -12.0F, -10.0F, 2.0F, 4.0F, 6.0F)
                .texOffs(328, 284).addBox(-5.0F, -3.0F, -28.0F, 2.0F, 2.0F, 4.0F)
                .texOffs(224, 214).addBox(3.0F, -12.0F, -10.0F, 2.0F, 4.0F, 6.0F)
                .texOffs(64, 332).addBox(3.0F, -3.0F, -28.0F, 2.0F, 2.0F, 4.0F),
            PartPose.offsetAndRotation(0.0F, -1.0F, -10.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition jaw = head.addOrReplaceChild("jaw",
            CubeListBuilder.create()
                .texOffs(176, 277).addBox(-6.0F, 0.0F, -15.0F, 12.0F, 4.0F, 16.0F),
            PartPose.offsetAndRotation(0.0F, 4.0F, -15.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition wing = body.addOrReplaceChild("wing",
            CubeListBuilder.create()
                .texOffs(224, 0).addBox(0.0F, -4.0F, -4.0F, 56.0F, 8.0F, 8.0F)
                .texOffs(0, 0).addBox(0.0F, 0.0F, 2.0F, 56.0F, 0.0F, 56.0F),
            PartPose.offsetAndRotation(12.0F, 1.0F, -6.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition wingtip = wing.addOrReplaceChild("wingtip",
            CubeListBuilder.create()
                .texOffs(224, 32).addBox(0.0F, -2.0F, -2.0F, 56.0F, 4.0F, 4.0F)
                .texOffs(0, 56).addBox(0.0F, 0.0F, 2.0F, 56.0F, 0.0F, 56.0F),
            PartPose.offsetAndRotation(56.0F, 0.0F, -2.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition wing1 = body.addOrReplaceChild("wing1",
            CubeListBuilder.create()
                .texOffs(224, 16).addBox(-56.0F, -4.0F, -4.0F, 56.0F, 8.0F, 8.0F)
                .texOffs(0, 112).addBox(-56.0F, 0.0F, 2.0F, 56.0F, 0.0F, 56.0F),
            PartPose.offsetAndRotation(-12.0F, 1.0F, -6.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition wingtip1 = wing1.addOrReplaceChild("wingtip1",
            CubeListBuilder.create()
                .texOffs(224, 40).addBox(-56.0F, -2.0F, -2.0F, 56.0F, 4.0F, 4.0F)
                .texOffs(0, 168).addBox(-56.0F, 0.0F, 2.0F, 56.0F, 0.0F, 56.0F),
            PartPose.offsetAndRotation(-56.0F, 0.0F, -2.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition rearleg = body.addOrReplaceChild("rearleg",
            CubeListBuilder.create()
                .texOffs(224, 108).addBox(-8.0F, -4.0F, -8.0F, 16.0F, 32.0F, 16.0F),
            PartPose.offsetAndRotation(-16.0F, 12.0F, 34.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition rearlegtip = rearleg.addOrReplaceChild("rearlegtip",
            CubeListBuilder.create()
                .texOffs(240, 204).addBox(-6.0F, -2.0F, 0.0F, 12.0F, 32.0F, 12.0F),
            PartPose.offsetAndRotation(0.0F, 30.0F, -6.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition rearfoot = rearlegtip.addOrReplaceChild("rearfoot",
            CubeListBuilder.create()
                .texOffs(224, 48).addBox(-9.0F, 0.0F, -20.0F, 18.0F, 6.0F, 24.0F),
            PartPose.offsetAndRotation(0.0F, 26.0F, 8.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition rearleg1 = body.addOrReplaceChild("rearleg1",
            CubeListBuilder.create()
                .texOffs(224, 156).addBox(-8.0F, -4.0F, -8.0F, 16.0F, 32.0F, 16.0F),
            PartPose.offsetAndRotation(16.0F, 12.0F, 34.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition rearlegtip1 = rearleg1.addOrReplaceChild("rearlegtip1",
            CubeListBuilder.create()
                .texOffs(240, 248).addBox(-6.0F, -2.0F, 0.0F, 12.0F, 32.0F, 12.0F),
            PartPose.offsetAndRotation(0.0F, 30.0F, -6.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition rearfoot1 = rearlegtip1.addOrReplaceChild("rearfoot1",
            CubeListBuilder.create()
                .texOffs(224, 78).addBox(-9.0F, 0.0F, -20.0F, 18.0F, 6.0F, 24.0F),
            PartPose.offsetAndRotation(0.0F, 26.0F, 8.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition frontleg = body.addOrReplaceChild("frontleg",
            CubeListBuilder.create()
                .texOffs(288, 148).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 24.0F, 8.0F),
            PartPose.offsetAndRotation(-12.0F, 16.0F, -6.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition frontlegtip = frontleg.addOrReplaceChild("frontlegtip",
            CubeListBuilder.create()
                .texOffs(296, 312).addBox(-3.0F, -1.0F, -3.0F, 6.0F, 24.0F, 6.0F),
            PartPose.offsetAndRotation(0.0F, 21.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition frontfoot = frontlegtip.addOrReplaceChild("frontfoot",
            CubeListBuilder.create()
                .texOffs(288, 108).addBox(-4.0F, 0.0F, -12.0F, 8.0F, 4.0F, 16.0F),
            PartPose.offsetAndRotation(0.0F, 19.0F, -1.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition frontleg1 = body.addOrReplaceChild("frontleg1",
            CubeListBuilder.create()
                .texOffs(288, 180).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 24.0F, 8.0F),
            PartPose.offsetAndRotation(12.0F, 16.0F, -6.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition frontlegtip1 = frontleg1.addOrReplaceChild("frontlegtip1",
            CubeListBuilder.create()
                .texOffs(160, 317).addBox(-3.0F, -1.0F, -3.0F, 6.0F, 24.0F, 6.0F),
            PartPose.offsetAndRotation(0.0F, 21.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition frontfoot1 = frontlegtip1.addOrReplaceChild("frontfoot1",
            CubeListBuilder.create()
                .texOffs(288, 128).addBox(-4.0F, 0.0F, -12.0F, 8.0F, 4.0F, 16.0F),
            PartPose.offsetAndRotation(0.0F, 19.0F, -1.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition tail = body.addOrReplaceChild("tail",
            CubeListBuilder.create()
                .texOffs(272, 292).addBox(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 10.0F)
                .texOffs(328, 204).addBox(-1.0F, -9.0F, 2.0F, 2.0F, 4.0F, 6.0F),
            PartPose.offsetAndRotation(0.0F, 6.0F, 48.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition tail2 = tail.addOrReplaceChild("tail2",
            CubeListBuilder.create()
                .texOffs(176, 297).addBox(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 10.0F)
                .texOffs(328, 214).addBox(-1.0F, -9.0F, 2.0F, 2.0F, 4.0F, 6.0F),
            PartPose.offsetAndRotation(0.0F, 0.0F, 10.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition tail3 = tail2.addOrReplaceChild("tail3",
            CubeListBuilder.create()
                .texOffs(308, 48).addBox(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 10.0F)
                .texOffs(328, 224).addBox(-1.0F, -9.0F, 2.0F, 2.0F, 4.0F, 6.0F),
            PartPose.offsetAndRotation(0.0F, 0.0F, 10.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition tail4 = tail3.addOrReplaceChild("tail4",
            CubeListBuilder.create()
                .texOffs(308, 68).addBox(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 10.0F)
                .texOffs(328, 234).addBox(-1.0F, -9.0F, 2.0F, 2.0F, 4.0F, 6.0F),
            PartPose.offsetAndRotation(0.0F, 0.0F, 10.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition tail5 = tail4.addOrReplaceChild("tail5",
            CubeListBuilder.create()
                .texOffs(308, 88).addBox(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 10.0F)
                .texOffs(328, 244).addBox(-1.0F, -9.0F, 2.0F, 2.0F, 4.0F, 6.0F),
            PartPose.offsetAndRotation(0.0F, 0.0F, 10.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition tail6 = tail5.addOrReplaceChild("tail6",
            CubeListBuilder.create()
                .texOffs(0, 312).addBox(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 10.0F)
                .texOffs(328, 254).addBox(-1.0F, -9.0F, 2.0F, 2.0F, 4.0F, 6.0F),
            PartPose.offsetAndRotation(0.0F, 0.0F, 10.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition tail7 = tail6.addOrReplaceChild("tail7",
            CubeListBuilder.create()
                .texOffs(40, 312).addBox(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 10.0F)
                .texOffs(328, 264).addBox(-1.0F, -9.0F, 2.0F, 2.0F, 4.0F, 6.0F),
            PartPose.offsetAndRotation(0.0F, 0.0F, 10.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition tail8 = tail7.addOrReplaceChild("tail8",
            CubeListBuilder.create()
                .texOffs(80, 312).addBox(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 10.0F)
                .texOffs(328, 274).addBox(-1.0F, -9.0F, 2.0F, 2.0F, 4.0F, 6.0F),
            PartPose.offsetAndRotation(0.0F, 0.0F, 10.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition tail9 = tail8.addOrReplaceChild("tail9",
            CubeListBuilder.create()
                .texOffs(120, 312).addBox(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 10.0F)
                .texOffs(0, 332).addBox(-1.0F, -9.0F, 2.0F, 2.0F, 4.0F, 6.0F),
            PartPose.offsetAndRotation(0.0F, 0.0F, 10.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition tail10 = tail9.addOrReplaceChild("tail10",
            CubeListBuilder.create()
                .texOffs(216, 312).addBox(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 10.0F)
                .texOffs(16, 332).addBox(-1.0F, -9.0F, 2.0F, 2.0F, 4.0F, 6.0F),
            PartPose.offsetAndRotation(0.0F, 0.0F, 10.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition tail11 = tail10.addOrReplaceChild("tail11",
            CubeListBuilder.create()
                .texOffs(256, 312).addBox(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 10.0F)
                .texOffs(32, 332).addBox(-1.0F, -9.0F, 2.0F, 2.0F, 4.0F, 6.0F),
            PartPose.offsetAndRotation(0.0F, 0.0F, 10.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition tail12 = tail11.addOrReplaceChild("tail12",
            CubeListBuilder.create()
                .texOffs(312, 292).addBox(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 10.0F)
                .texOffs(48, 332).addBox(-1.0F, -9.0F, 2.0F, 2.0F, 4.0F, 6.0F),
            PartPose.offsetAndRotation(0.0F, 0.0F, 10.0F, 0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(modelData, 512, 512);
    }

    @Override
    public void setupAnim(DragonGuardianRenderState state) {
        super.setupAnim(state);

        float age = state.ageInTicks;
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

        this.wing.zRot = wingFlap - 0.3F;
        this.wing1.zRot = -wingFlap + 0.3F;

        float wingPitch = (float) Math.sin(flapCycle) * 0.2F;
        this.wing.xRot = wingPitch;
        this.wing1.xRot = wingPitch;

        float tipFlap = (float) Math.sin(flapCycle + 0.5F) * 1.5F;
        this.wingtip.zRot = tipFlap - 0.5F;
        this.wingtip1.zRot = -tipFlap + 0.5F;

        // === TAIL SWAYING ===
        float tailBase = (float) Math.sin(age * 0.1F * animSpeed) * 0.15F;
        this.tail.yRot = tailBase;
        this.tail2.yRot = tailBase * 1.2F;
        this.tail3.yRot = tailBase * 1.4F;
        this.tail4.yRot = tailBase * 1.6F;
        this.tail5.yRot = tailBase * 1.8F;
        this.tail6.yRot = tailBase * 2.0F;
        this.tail7.yRot = tailBase * 2.2F;
        this.tail8.yRot = tailBase * 2.4F;
        this.tail9.yRot = tailBase * 2.6F;
        this.tail10.yRot = tailBase * 2.8F;
        this.tail11.yRot = tailBase * 3.0F;
        this.tail12.yRot = tailBase * 3.2F;

        // === NECK BOBBING ===
        float neckBob = (float) Math.sin(age * 0.15F * animSpeed) * 0.08F;
        this.neck5.xRot = neckBob;
        this.neck4.xRot = neckBob * 0.9F;
        this.neck3.xRot = neckBob * 0.8F;
        this.neck2.xRot = neckBob * 0.7F;
        this.neck.xRot = neckBob * 0.6F;

        // === HEAD & JAW ===
        float headBreathing = (float) Math.cos(age * 0.2F) * 0.05F;
        this.head.xRot = headBreathing;
        this.jaw.xRot = Math.abs((float) Math.sin(age * 0.15F)) * 0.1F;

        // === LEGS ===
        float legSwing = (float) Math.sin(age * 0.12F * animSpeed) * 0.15F;
        this.rearleg.xRot = legSwing * 0.5F;
        this.rearleg1.xRot = -legSwing * 0.5F;
        this.rearlegtip.xRot = -legSwing * 0.3F;
        this.rearlegtip1.xRot = legSwing * 0.3F;
        this.frontleg.xRot = -legSwing * 0.6F;
        this.frontleg1.xRot = legSwing * 0.6F;
        this.frontlegtip.xRot = legSwing * 0.4F;
        this.frontlegtip1.xRot = -legSwing * 0.4F;
    }

    private void animatePerched(DragonGuardianRenderState state) {
        // Wings folded against body
        this.wing.zRot = -1.5F;
        this.wing1.zRot = 1.5F;
        this.wingtip.zRot = -2.0F;
        this.wingtip1.zRot = 2.0F;

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
            tailSegment.yRot = tailCurl * i * 0.1F;
        }

        // Neck relaxed
        this.neck5.xRot = -0.1F;
        this.neck4.xRot = -0.05F;
        this.neck3.xRot = 0.0F;
        this.neck2.xRot = 0.0F;
        this.neck.xRot = 0.0F;

        // Head alert with breathing
        float breathing = (float) Math.sin(state.ageInTicks * 0.1F) * 0.03F;
        this.head.xRot = breathing;
        this.jaw.xRot = Math.abs((float) Math.sin(state.ageInTicks * 0.1F)) * 0.05F;

        // Legs standing
        this.rearleg.xRot = 0.0F;
        this.rearleg1.xRot = 0.0F;
        this.frontleg.xRot = 0.0F;
        this.frontleg1.xRot = 0.0F;
    }

    private void animateRoaring(DragonGuardianRenderState state, float age, float animSpeed) {
        // Wings spread wide
        float wingSpread = (float) Math.sin(age * 0.3F) * 0.3F;
        this.wing.zRot = 1.5F + wingSpread;
        this.wing1.zRot = -1.5F - wingSpread;
        this.wingtip.zRot = 2.0F;
        this.wingtip1.zRot = -2.0F;

        // Neck extended upward
        this.neck5.xRot = -0.3F;
        this.neck4.xRot = -0.25F;
        this.neck3.xRot = -0.2F;
        this.neck2.xRot = -0.15F;
        this.neck.xRot = -0.1F;

        // Head tilted back, jaw wide open
        this.head.xRot = -0.4F;
        this.jaw.xRot = 0.8F; // Wide open

        // Tail thrashing
        float tailThrash = (float) Math.sin(age * 0.5F) * 0.4F;
        this.tail.yRot = tailThrash;
        this.tail2.yRot = tailThrash * 1.2F;
        this.tail3.yRot = tailThrash * 1.4F;
        this.tail4.yRot = tailThrash * 1.6F;
        this.tail5.yRot = tailThrash * 1.8F;
        this.tail6.yRot = tailThrash * 2.0F;
        this.tail7.yRot = tailThrash * 1.8F;
        this.tail8.yRot = tailThrash * 1.6F;
        this.tail9.yRot = tailThrash * 1.4F;
        this.tail10.yRot = tailThrash * 1.2F;
        this.tail11.yRot = tailThrash;
        this.tail12.yRot = tailThrash * 0.8F;
    }

    private void animateFireBreathing(DragonGuardianRenderState state, float age, float animSpeed) {
        // Wings steady for stability
        this.wing.zRot = 0.5F;
        this.wing1.zRot = -0.5F;
        this.wingtip.zRot = 0.8F;
        this.wingtip1.zRot = -0.8F;

        // Neck extended forward
        this.neck5.xRot = 0.1F;
        this.neck4.xRot = 0.0F;
        this.neck3.xRot = -0.05F;
        this.neck2.xRot = -0.1F;
        this.neck.xRot = -0.15F;

        // Head aimed forward, jaw opening/closing
        this.head.xRot = -0.2F;
        float jawPulse = (float) Math.sin(age * 0.5F) * 0.3F + 0.3F;
        this.jaw.xRot = jawPulse; // Pulsing open/close

        // Tail straight for balance
        float tailStraight = (float) Math.sin(age * 0.2F) * 0.05F;
        this.tail.yRot = tailStraight;
        this.tail2.yRot = tailStraight * 0.9F;
        this.tail3.yRot = tailStraight * 0.8F;
        this.tail4.yRot = tailStraight * 0.7F;
        this.tail5.yRot = tailStraight * 0.6F;
        this.tail6.yRot = tailStraight * 0.5F;
        this.tail7.yRot = tailStraight * 0.4F;
        this.tail8.yRot = tailStraight * 0.3F;
        this.tail9.yRot = tailStraight * 0.2F;
        this.tail10.yRot = tailStraight * 0.1F;
        this.tail11.yRot = 0.0F;
        this.tail12.yRot = 0.0F;
    }

    private void animateSwooping(DragonGuardianRenderState state, float age, float animSpeed) {
        // Wings swept back for speed
        float wingFast = (float) Math.cos(age * 0.8F) * 0.6F; // Fast flaps
        this.wing.zRot = -0.8F + wingFast;
        this.wing1.zRot = 0.8F - wingFast;
        this.wing.xRot = -0.3F; // Angled back
        this.wing1.xRot = -0.3F;

        // Wingtips tight
        this.wingtip.zRot = -1.0F;
        this.wingtip1.zRot = 1.0F;

        // Neck streamlined
        this.neck5.xRot = 0.2F;
        this.neck4.xRot = 0.15F;
        this.neck3.xRot = 0.1F;
        this.neck2.xRot = 0.05F;
        this.neck.xRot = 0.0F;

        // Head forward, jaw closed
        this.head.xRot = 0.1F;
        this.jaw.xRot = 0.0F;

        // Tail straight behind
        this.tail.yRot = 0.0F;
        this.tail2.yRot = 0.0F;
        this.tail3.yRot = 0.0F;
        this.tail4.yRot = 0.0F;
        this.tail5.yRot = 0.0F;
        this.tail6.yRot = 0.0F;
        this.tail7.yRot = 0.0F;
        this.tail8.yRot = 0.0F;
        this.tail9.yRot = 0.0F;
        this.tail10.yRot = 0.0F;
        this.tail11.yRot = 0.0F;
        this.tail12.yRot = 0.0F;

        // Legs tucked
        this.rearleg.xRot = 0.8F;
        this.rearleg1.xRot = 0.8F;
        this.frontleg.xRot = 0.6F;
        this.frontleg1.xRot = 0.6F;
    }

    private void animateLanding(DragonGuardianRenderState state, float age, float animSpeed) {
        // Wings spread for braking
        this.wing.zRot = 1.8F;
        this.wing1.zRot = -1.8F;
        this.wingtip.zRot = 2.2F;
        this.wingtip1.zRot = -2.2F;

        // Neck curved down
        this.neck5.xRot = 0.3F;
        this.neck4.xRot = 0.25F;
        this.neck3.xRot = 0.2F;
        this.neck2.xRot = 0.15F;
        this.neck.xRot = 0.1F;

        // Head looking down
        this.head.xRot = 0.2F;
        this.jaw.xRot = 0.05F;

        // Tail up for balance
        this.tail.xRot = -0.2F;
        this.tail2.xRot = -0.15F;
        this.tail3.xRot = -0.1F;

        // Legs extended for landing
        this.rearleg.xRot = -0.3F;
        this.rearleg1.xRot = -0.3F;
        this.frontleg.xRot = -0.2F;
        this.frontleg1.xRot = -0.2F;
    }
}
