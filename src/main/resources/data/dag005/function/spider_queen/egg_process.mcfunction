



scoreboard players add @s spider_timer 1

#particles
execute at @s[scores={spider_timer=0..100}] run particle dust{color:[0.5,0.9,0.1],scale:1} ~0.4 ~ ~0.5 0.01 0.001 0.1 0 1
execute at @s[scores={spider_timer=101..200}] run particle dust{color:[0.9,0.7,0.1],scale:1} ~0.4 ~ ~0.5 0.01 0.001 0.1 0 1
execute at @s[scores={spider_timer=202..300}] run particle dust{color:[1.0,0.0,0.0],scale:1} ~0.4 ~ ~0.5 0.01 0.001 0.1 0 1


#sound
execute at @s[scores={spider_timer=301}] run playsound minecraft:entity.turtle.egg_hatch hostile @a ~ ~ ~ 1 1 0


#summon baby spider
execute at @s[scores={spider_timer=301}] run summon cave_spider ~ ~ ~ {Tags:["hatchling"], attributes: [{id:"minecraft:jump_strength",base:0.58},{id:"minecraft:knockback_resistance",base:0.9},{id:"minecraft:movement_speed",base:0.4},{id:"minecraft:scale",base:0.5},{id:"minecraft:movement_efficiency",base:0.1}]}


#kill egg
kill @s[scores={spider_timer=301}]