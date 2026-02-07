


scoreboard players remove @s spider_timer 1

#Count babies
execute store result score #hatchlings spider_timer if entity @e[type=cave_spider, tag=hatchling]

#Summon egg when timer runs out
execute if score #hatchlings spider_timer matches ..51 if score @s spider_timer matches ..0 run summon block_display ~-0.5 ~ ~-0.5 {Tags:["spider_egg"], block_state:{Name:"minecraft:turtle_egg"}} 

#Get new lay time
execute if score @s spider_timer matches ..0 store result score @s spider_timer run function dag005:spider_queen/get_random_lay_time
