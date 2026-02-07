


scoreboard players remove @s skeleton_king 1

#Count babies
#execute store result score #summoner skeleton_king if entity @e[type=wither_skeleton, tag=summoner]

#Summon egg when timer runs out
#execute if score #summoner skeleton_summoner matches ..101 if score @s skeleton_summoner matches ..0 run summon block_display ~-0.5 ~ ~-0.5 {Tags:["skeleton_spawn"], block_state:{Name:"minecraft:skeleton_skull"}} 

#Get new lay time
#execute if score @s skeleton_summoner matches ..0 store result score @s skeleton_summoner run function dag005:skeleton_lord/get_random_lay_time
