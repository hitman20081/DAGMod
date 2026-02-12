#Face random player
execute facing entity @r[distance=..3] feet run tp @s ~ ~ ~ ~ ~

#Summon AEC
summon minecraft:area_effect_cloud ~ ~ ~ {Duration:60, Tags:["healing","healing_beam"]}

#Rotate nearest AEC to match station rotation
tp @n[type=minecraft:area_effect_cloud, tag=healing_beam] ~ ~0.95 ~ ~ ~