



#Process spiders
execute as @e[type=spider] run function dag005:spider_queen/spider_process

#Process skeletons
execute as @e[type=skeleton] run function dag005:field_captain/skeleton_process
execute as @e[type=wither_skeleton] run function dag005:skeleton_lord/skeleton_process




#Process eggs
execute as @e[type=minecraft:block_display, tag=spider_egg] at @s run function dag005:spider_queen/egg_process


#Process skulls
execute as @e[type=minecraft:block_display, tag=skeleton_spawn] at @s run function dag005:skeleton_lord/skull_process
execute as @e[type=minecraft:block_display, tag=skeleton_spawn2] at @s run function dag005:field_captain/skull_process