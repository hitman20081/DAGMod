package com.github.hitman20081.dagmod.item;

import com.github.hitman20081.dagmod.util.ModTags;
import net.minecraft.item.ToolMaterial;

public class ModToolMaterials {
    public static final ToolMaterial MYTHRIL = new ToolMaterial(
            ModTags.Blocks.INCORRECT_FOR_MYTHRIL_TOOL,  // Incorrect blocks tag
            2500,                                         // Durability (Diamond: 1561, Netherite: 2031)
            9.0f,                                         // Mining speed (Diamond: 8.0f, Netherite: 9.0f)
            4.0f,                                         // Attack damage bonus (Diamond: 3.0f, Netherite: 4.0f)
            15,                                           // Enchantability (Diamond: 10, Netherite: 15)
            ModTags.Items.MYTHRIL_REPAIR                  // Repair ingredient tag
    );
}
