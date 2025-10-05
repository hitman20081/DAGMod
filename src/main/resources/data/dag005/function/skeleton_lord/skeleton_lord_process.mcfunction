


scoreboard players remove @s skeleton_timer 1

#Count babies
execute store result score #bonelings skeleton_timer if entity @e[type=skeleton, tag=boneling]

#Summon egg when timer runs out
execute if score #bonelings skeleton_timer matches ..51 if score @s skeleton_timer matches ..0 run summon block_display ~-0.5 ~ ~-0.5 {Tags:["skeleton_spawn"], block_state:{Name:"minecraft:skeleton_skull"}} 

#Get new lay time
execute if score @s skeleton_timer matches ..0 store result score @s skeleton_timer run function dag005:skeleton_lord/get_random_lay_time
