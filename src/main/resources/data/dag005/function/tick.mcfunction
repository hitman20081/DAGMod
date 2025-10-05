



#Process spiders
execute as @e[type=spider] run function dag005:spider_queen/spider_process

#Process skeletons
execute as @e[type=skeleton] run function dag005:skeleton_lord/skeleton_process





#Process eggs
execute as @e[type=minecraft:block_display, tag=spider_egg] at @s run function dag005:spider_queen/egg_process


#Process skulls
execute as @e[type=minecraft:block_display, tag=skeleton_spawn] at @s run function dag005:skeleton_lord/skull_process