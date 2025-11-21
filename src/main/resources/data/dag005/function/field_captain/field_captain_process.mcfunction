


scoreboard players remove @s skeleton_timer 1

#Count babies
execute store result score #soldiers skeleton_timer if entity @e[type=skeleton, tag=soldier]

#Summon egg when timer runs out
execute if score #soldiers skeleton_timer matches ..51 if score @s skeleton_timer matches ..0 run summon block_display ~-0.5 ~ ~-0.5 {Tags:["skeleton_spawn2"], block_state:{Name:"minecraft:skeleton_skull"}}

#Get new lay time
execute if score @s skeleton_timer matches ..0 store result score @s skeleton_timer run function dag005:field_captain/get_random_lay_time
