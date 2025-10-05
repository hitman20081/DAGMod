
#Revoke
advancement revoke @s only dag010:magic_wand_charging
advancement revoke @s only dag010:flame_wand_charging



#increment charge score
scoreboard players add @s magic_wand.charge 1
scoreboard players add @s flame_wand.charge 1

#if score is certain level then run function
execute if score @s magic_wand.charge matches 50.. run function dag010:wand/charged
execute if score @s flame_wand.charge matches 50.. run function dag010:wand/charged


#save the game time to player's timestamp
execute store result score @s magic_wand.timestamp run time query gametime
execute store result score @s flame_wand.timestamp run time query gametime



#Add to the player's timestamp
scoreboard players add @s magic_wand.timestamp 2
scoreboard players add @s flame_wand.timestamp 2





#check if the player releases right button
schedule function dag010:wand/discharge_check 2t append

