#Tag the interacted if it has been right clicked
execute on target run tag @e[type=minecraft:interaction, tag=grave, distance=..0.1, limit=1] add process_me

#Exit if it hasn't
execute if entity @s[tag=!process_me] run return run data remove entity @s interaction

#Remove the tag
tag @s remove process_me

#Copt the players id and copy the id on the marker
execute on target run scoreboard players operation player ID = @s ID
execute on passengers run scoreboard players operation marker ID = @s ID

#Exit if the ID don't match
execute unless score player ID = marker ID store success entity @s interaction.player[] int 0 on target run return run tellraw @a {"text":"That's not your grave!","color":"red"}

#Message
execute on target run tellraw @s {"text":"Grave collected!","color":"green"}

#Remove the block
setblock ~ ~ ~ air

#Copy the items to storage
data remove storage id:data temporary
execute on passengers run data modify storage id:data temporary.inventory set from entity @s data.inventory

#Spawn the items
function dag000:graves/summon_items

#kill the grave marker
execute on passengers run kill @s

#Kill the grave interaction
kill @s
