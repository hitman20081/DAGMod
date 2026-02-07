# Place structure at world spawn on the surface
execute unless score #spawn_placed dag011.config matches 1 in minecraft:overworld positioned 0 ~ 0 at @e[type=minecraft:marker,tag=world_spawn,limit=1] run place template dag011:spawn/hall_of_champions ~ ~ ~
execute unless score #spawn_placed dag011.config matches 1 in minecraft:overworld positioned 0 ~ 0 run spreadplayers 0 0 0 1 false @s
execute unless score #spawn_placed dag011.config matches 1 at @s run place template dag011:spawn/hall_of_champions ~ ~ ~
execute unless score #spawn_placed dag011.config matches 1 at @s run setworldspawn ~ ~1 ~
execute unless score #spawn_placed dag011.config matches 1 run tellraw @a {"text":"Hall of Champions established at spawn!","color":"gold","bold":true}
execute unless score #spawn_placed dag011.config matches 1 run scoreboard players set #spawn_placed dag011.config 1