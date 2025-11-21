



scoreboard players add @s skeleton_timer 1

#particles
execute at @s[scores={skeleton_timer=0..100}] run particle dust{color:[0.5,0.9,0.1],scale:1} ~0.4 ~ ~0.5 0.01 0.001 0.1 0 1
execute at @s[scores={skeleton_timer=101..200}] run particle dust{color:[0.9,0.7,0.1],scale:1} ~0.4 ~ ~0.5 0.01 0.001 0.1 0 1
execute at @s[scores={skeleton_timer=202..300}] run particle dust{color:[1.0,0.0,0.0],scale:1} ~0.4 ~ ~0.5 0.01 0.001 0.1 0 1


#sound
execute at @s[scores={skeleton_timer=301}] run playsound minecraft:entity.skeleton.ambient hostile @a ~ ~ ~ 1 1 0


#summon skeleton soldiers
execute at @s[scores={skeleton_timer=301}] run summon wither_skeleton ~ ~ ~ {DeathLootTable:"dag005:boss/minion_loot_table",Tags:["boneling"],equipment:{feet:{id:"minecraft:iron_boots",count:1,components:{"minecraft:trim":{material:"minecraft:netherite",pattern:"minecraft:dune"},"minecraft:custom_name":{"color":"dark_red","text":"Sabatons of the Bone Minion"}}},legs:{id:"minecraft:iron_leggings",count:1,components:{"minecraft:trim":{material:"minecraft:netherite",pattern:"minecraft:dune"},"minecraft:custom_name":{"color":"dark_red","text":"Leggings of the Bone Minion"}}},chest:{id:"minecraft:iron_chestplate",count:1,components:{"minecraft:trim":{material:"minecraft:netherite",pattern:"minecraft:dune"},"minecraft:custom_name":{"color":"dark_red","text":"Chest of the Bone Minion"}}},head:{id:"minecraft:iron_helmet",count:1,components:{"minecraft:trim":{material:"minecraft:netherite",pattern:"minecraft:dune"},"minecraft:custom_name":{"color":"dark_red","text":"Helm of the Bone Minion"}}},mainhand:{id:"minecraft:iron_sword",count:1,components:{"minecraft:attribute_modifiers":[{id:"attack_speed",type:"attack_speed",amount:0.05,operation:"add_multiplied_base",slot:"mainhand"}]}}},attributes:[{id:"minecraft:knockback_resistance",base:1},{id:"minecraft:max_health",base:15},{id:"minecraft:movement_efficiency",base:1},{id:"minecraft:scale",base:1.2}]}


#kill egg
kill @s[scores={skeleton_timer=301}]