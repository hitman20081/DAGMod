#Move Beam forward
execute if entity @e[type=minecraft:armor_stand, tag=healing_origin, distance=..8] run tp @s ^ ^ ^0.06


#Particles
particle dust{color:[0,1,0], scale:0.4} ~ ~ ~ 0.01 0.01 0.01 0 1


#Heal Near Players 
execute positioned ~ ~-0.95 ~ as @a[distance=..0.06] unless predicate dag012:has_regen run function dag012:station/heal_player


#Kill beam near players
execute positioned ~ ~-0.95 ~ if entity @a[distance=..0.06] run kill @s