tellraw @a {"text":"Finding spawn location...","color":"yellow"}
# Summon marker high up at world spawn
execute in minecraft:overworld run summon marker 0 200 0 {Tags:["spawn_finder"]}
# Start finding the ground
execute as @e[type=marker,tag=spawn_finder] at @s run function dag011:find_surface