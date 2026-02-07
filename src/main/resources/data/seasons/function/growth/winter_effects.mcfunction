# Winter Effects - Freezing, slow growth, harsh conditions
# Crops barely grow, water freezes, players get cold

# Drastically slow crop growth (25% normal speed)
execute as @a at @s run execute positioned ~-8 ~-3 ~-8 run fill ~0 ~0 ~0 ~16 ~3 ~16 minecraft:farmland[moisture=0] replace minecraft:farmland[moisture=7]
execute as @a at @s run execute positioned ~-8 ~-3 ~-8 run fill ~0 ~0 ~0 ~16 ~3 ~16 minecraft:farmland[moisture=1] replace minecraft:farmland[moisture=6]

# Freeze water sources (convert water to ice)
execute as @a at @s run execute positioned ~-12 ~-8 ~-12 if predicate seasons:random/8_percent run fill ~0 ~0 ~0 ~24 ~16 ~24 minecraft:ice replace minecraft:water

# Add snow layers gradually
execute as @a at @s run execute positioned ~-10 ~-2 ~-10 if predicate seasons:random/3_percent unless block ~ ~1 ~ #minecraft:leaves unless block ~ ~1 ~ #minecraft:logs run fill ~0 ~1 ~0 ~20 ~1 ~20 minecraft:snow[layers=1] replace minecraft:air

# Hypothermia effect - players lose hunger faster in cold biomes
execute as @a[predicate=seasons:biomes/cold_biomes] run effect give @s minecraft:hunger 5 0 true

# Ice spikes form randomly
execute as @a at @s if predicate seasons:random/1_percent run execute positioned ~-15 ~-3 ~-15 if block ~ ~1 ~ minecraft:air if block ~ ~ ~ #minecraft:dirt run setblock ~ ~1 ~ minecraft:packed_ice

# Blizzard particle effects
execute as @a at @s if predicate seasons:random/10_percent run particle minecraft:snowflake ~0 ~10 ~0 15 8 15 0.1 50

# Slower movement in deep snow (if standing on snow blocks)
execute as @a at @s if block ~ ~-1 ~ minecraft:snow_block run effect give @s minecraft:slowness 1 0 true

# Animals huddle together (attract nearby animals)
execute as @e[type=#seasons:farm_animals] at @s run tp @e[type=#seasons:farm_animals,distance=1..3,limit=1] ~ ~ ~