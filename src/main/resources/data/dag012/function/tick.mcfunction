


execute as @e[type=minecraft:armor_stand, tag=healing_origin] at @s if entity @a[distance=..3] run function dag012:station/tick_station

execute as @e[type=minecraft:area_effect_cloud, tag=healing_beam] at @s run function dag012:station/tick_beam