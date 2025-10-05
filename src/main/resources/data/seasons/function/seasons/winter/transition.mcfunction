# Winter Transition Effects
# Begin freezing process, first snow

# Initial freeze - convert some water to ice
execute as @a at @s run execute positioned ~-10 ~-5 ~-10 run fill ~0 ~0 ~0 ~20 ~10 ~20 minecraft:ice replace minecraft:water

# First snowfall
weather rain 800

# Add initial snow layer to exposed surfaces
execute as @a at @s run execute positioned ~-8 ~-2 ~-8 if block ~ ~1 ~ minecraft:air unless block ~ ~1 ~ #minecraft:leaves run fill ~0 ~1 ~0 ~16 ~1 ~16 minecraft:snow[layers=1] replace minecraft:air

# Remove flowers (they die in winter)
execute as @a at @s run execute positioned ~-15 ~-5 ~-15 run fill ~0 ~0 ~0 ~30 ~10 ~30 minecraft:air replace #minecraft:flowers

# Blizzard warning particles
execute as @a at @s run particle minecraft:snowflake ~ ~3 ~ 3 3 3 0.1 20
execute as @a at @s run particle minecraft:cloud ~ ~2 ~ 5 1 5 0.1 15

# Animals seek shelter behavior
execute as @e[type=#seasons:farm_animals] at @s unless block ~ ~1 ~ minecraft:air run tp @s ~ ~ ~