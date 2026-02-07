# Remove ui element items from players
clear @a *[minecraft:custom_data~{ui_item:{}}]

# Future proofing,specifies it is a chest minecart
scoreboard players set .type ui 1

# Detect player putting items into the UI
data modify storage ui in set from storage ui current
data remove storage ui in[{components:{"minecraft:custom_data":{ui_item:{}}}}]
execute if data storage ui in[0] run return run function dag020:menu/on_input