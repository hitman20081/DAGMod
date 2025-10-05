# Summer Transition Effects
# Clear weather, remove snow, dry up some water

# Clear the weather for summer
weather clear 2400

# Remove remaining snow from spring
execute as @a at @s run execute positioned ~-15 ~-5 ~-15 run fill ~0 ~0 ~0 ~30 ~10 ~30 minecraft:air replace minecraft:snow

# Dry up some small water sources (puddles)
execute as @a at @s run execute positioned ~-8 ~-3 ~-8 if predicate seasons:random/5_percent run fill ~0 ~0 ~0 ~16 ~3 ~16 minecraft:air replace minecraft:water

# Summer heat particles
execute as @a at @s run particle minecraft:flame ~ ~2 ~ 3 1 3 0.01 15

# Boost crop growth for the transition
execute as @a at @s run execute positioned ~-10 ~-3 ~-10 run fill ~0 ~0 ~0 ~20 ~3 ~20 minecraft:farmland[moisture=7] replace minecraft:farmland

# Make deserts more active
execute as @a[predicate=seasons:biomes/desert] at @s run particle minecraft:dust{color:[1.0,0.8,0.0],scale:1} ~ ~1 ~ 5 1 5 0.1 20