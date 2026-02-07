# Summer Growth Bonus Effects
# Crops yield more items when harvested

# Double drop chance for mature crops near players
execute as @a at @s run execute as @e[type=minecraft:item,distance=..10,nbt={Item:{id:"minecraft:wheat"}}] run function seasons:growth/double_drop
execute as @a at @s run execute as @e[type=minecraft:item,distance=..10,nbt={Item:{id:"minecraft:carrot"}}] run function seasons:growth/double_drop
execute as @a at @s run execute as @e[type=minecraft:item,distance=..10,nbt={Item:{id:"minecraft:potato"}}] run function seasons:growth/double_drop
execute as @a at @s run execute as @e[type=minecraft:item,distance=..10,nbt={Item:{id:"minecraft:beetroot"}}] run function seasons:growth/double_drop

# Faster bone meal effects in summer
execute as @a[nbt={SelectedItem:{id:"minecraft:bone_meal"}}] at @s run particle minecraft:happy_villager ~ ~1 ~ 1 1 1 0.1 5

# Melon and pumpkin stems grow faster (using correct stem blocks)
execute as @a at @s if predicate seasons:random/10_percent run execute positioned ~-10 ~-5 ~-10 run fill ~0 ~0 ~0 ~20 ~10 ~20 minecraft:melon_stem[age=7] replace minecraft:melon_stem[age=6]
execute as @a at @s if predicate seasons:random/10_percent run execute positioned ~-10 ~-5 ~-10 run fill ~0 ~0 ~0 ~20 ~10 ~20 minecraft:pumpkin_stem[age=7] replace minecraft:pumpkin_stem[age=6]