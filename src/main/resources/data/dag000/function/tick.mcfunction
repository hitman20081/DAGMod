execute run function dag000:player/classes/classes_tick
execute run function dag000:player/player_tick

#assign ID to player
execute as @a unless score @s ID = @s ID store result score @s ID run scoreboard players add #id_holder ID 1


#Process dead player
execute as @a[scores={has_died=1..}] at @s run function dag000:graves/player_died

#Process collecting inventory
execute as @e[type=minecraft:interaction, tag=grave] at @s run function dag000:graves/collect_items
