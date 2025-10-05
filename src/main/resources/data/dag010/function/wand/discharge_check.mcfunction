#Store game time to fake player timestamp
execute store result score #this magic_wand.timestamp run time query gametime
execute store result score #this flame_wand.timestamp run time query gametime



#Check if players timestamp matches fake players timestamp
execute as @a if score @s magic_wand.timestamp = #this magic_wand.timestamp run scoreboard players reset @s magic_wand.charge
execute as @a if score @s flame_wand.timestamp = #this magic_wand.timestamp run scoreboard players reset @s flame_wand.charge

