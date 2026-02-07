# Encourage animal breeding in spring

# Add love mode to animals (simulates being fed)
data modify entity @s InLove set value 600

# Hearts particles around the animal
particle minecraft:heart ~ ~1 ~ 0.5 0.5 0.5 0.1 3

# If there's another same-type animal nearby, encourage breeding
execute if entity @e[type=minecraft:cow,distance=1..3] if entity @s[type=minecraft:cow] run data modify entity @e[type=minecraft:cow,distance=1..3,limit=1] InLove set value 600

execute if entity @e[type=minecraft:pig,distance=1..3] if entity @s[type=minecraft:pig] run data modify entity @e[type=minecraft:pig,distance=1..3,limit=1] InLove set value 600

execute if entity @e[type=minecraft:sheep,distance=1..3] if entity @s[type=minecraft:sheep] run data modify entity @e[type=minecraft:sheep,distance=1..3,limit=1] InLove set value 600