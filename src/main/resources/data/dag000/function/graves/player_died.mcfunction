#Reset has_died
scoreboard players reset @s has_died

#Count the items in Inventory
execute store result score #inventory_count ID run clear @s * 0

#Exit Function if count was zero
execute if score #inventory_count ID matches 0 run return run tellraw @s {"text":"You have no items to drop, no grave was placed","color":"gold"}

#Message the player
tellraw @s {"text":"A grave was placed","color":"green"}

#Place a block
setblock ~ ~ ~ minecraft:red_wool

#Summon interaction
execute align xz run summon minecraft:interaction ~0.5 ~ ~0.5 {Tags:["grave"], width:1.01, height:1.01, Passengers:[{id:"minecraft:marker", Tags:["grave","grave_marker","init_me"]}]}

#Copy the players ID to Marker
scoreboard players operation @e[type=minecraft:marker, tag=grave_marker, tag=init_me] ID = @s ID

#Copy the players inventory to the marker
data modify entity @e[type=minecraft:marker, tag=grave_marker, tag=init_me, limit=1] data.inventory set from entity @s Inventory

#Remove Init tag
tag @e[type=minecraft:marker, tag=grave_marker, tag=init_me] remove init_me

#Clear Inventory
clear @s