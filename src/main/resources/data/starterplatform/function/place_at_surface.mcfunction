# Place Village Inn at world spawn on the surface
execute unless score #spawn_placed dagmod.config matches 1 in minecraft:overworld positioned 0 ~ 0 at @e[type=minecraft:marker,tag=world_spawn,limit=1] run place template dagmod:village_npc/village_inn ~ ~ ~
execute unless score #spawn_placed dagmod.config matches 1 in minecraft:overworld positioned 0 ~ 0 run spreadplayers 0 0 0 1 false @s
execute unless score #spawn_placed dagmod.config matches 1 at @s run place template dagmod:village_npc/village_inn ~ ~ ~
execute unless score #spawn_placed dagmod.config matches 1 at @s run setworldspawn ~ ~1 ~
execute unless score #spawn_placed dagmod.config matches 1 run tellraw @a {"text":"Village Inn established at spawn!","color":"green","bold":true}
execute unless score #spawn_placed dagmod.config matches 1 run scoreboard players set #spawn_placed dagmod.config 1
