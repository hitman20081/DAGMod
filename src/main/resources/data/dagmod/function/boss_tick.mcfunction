



#Process spiders
execute as @e[type=spider] run function dagmod:spider_queen/spider_process

#Process skeletons
execute as @e[type=skeleton] run function dagmod:field_captain/skeleton_process
execute as @e[type=wither_skeleton] run function dagmod:skeleton_lord/skeleton_process




#Process eggs
execute as @e[type=minecraft:block_display, tag=spider_egg] at @s run function dagmod:spider_queen/egg_process


#Process skulls
execute as @e[type=minecraft:block_display, tag=skeleton_spawn] at @s run function dagmod:skeleton_lord/skull_process
execute as @e[type=minecraft:block_display, tag=skeleton_spawn2] at @s run function dagmod:field_captain/skull_process

#Process healing stations
execute as @e[type=minecraft:armor_stand, tag=healing_origin] at @s if entity @a[distance=..3] run function dagmod:station/tick_station
execute as @e[type=minecraft:area_effect_cloud, tag=healing_beam] at @s run function dagmod:station/tick_beam

#Process GUI/UI system
execute as @a run function dagmod:player/tick
execute as @e[type=chest_minecart,tag=ui] run function dagmod:minecart/tick