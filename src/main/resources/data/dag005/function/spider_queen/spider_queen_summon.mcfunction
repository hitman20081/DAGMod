
execute in minecraft:overworld run summon spider ~ ~ ~ {Tags:["id_custom_mob","id_spider_queen","init_me"],attributes:[{id:"minecraft:armor",base:10},{id:"minecraft:armor_toughness",base:2},{id:"minecraft:attack_damage",base:5},{id:"minecraft:attack_speed",base:2},{id:"minecraft:jump_strength",base:0.58},{id:"minecraft:knockback_resistance",base:0.9},{id:"minecraft:max_health",base:35},{id:"minecraft:movement_speed",base:0.45},{id:"minecraft:scale",base:3.0},{id:"minecraft:movement_efficiency",base:1}]}

execute store result score @e[tag=id_spider_queen, tag=init_me] spider_timer run function dag005:spider_queen/get_random_lay_time

tag @s remove init_me

return 1