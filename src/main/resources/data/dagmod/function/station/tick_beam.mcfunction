#Age the beam and expire it after 60 ticks (replaces the old area_effect_cloud's Duration:60,
#since armor stands have no built-in lifespan field)
scoreboard players add @s healing_beam_timer 1
execute if score @s healing_beam_timer matches 60.. run kill @s

#Move Beam forward
execute if entity @e[type=minecraft:armor_stand, tag=healing_origin, distance=..8] run tp @s ^ ^ ^0.06


#Particles
particle dust{color:[0,1,0], scale:0.4} ~ ~ ~ 0.01 0.01 0.01 0 1


#Heal Near Players 
execute positioned ~ ~-0.95 ~ as @a[distance=..0.06] unless predicate dagmod:has_regen run function dagmod:station/heal_player


#Kill beam near players
execute positioned ~ ~-0.95 ~ if entity @a[distance=..0.06] run kill @s