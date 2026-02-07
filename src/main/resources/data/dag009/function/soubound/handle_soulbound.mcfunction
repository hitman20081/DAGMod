# Iterate through all players
#execute as @a store result score @s death_flag run data get entity @s DeathTime

# If player has just died, search for Soul Bound items in their inventory
#execute as @a[scores={death_flag=1..}] run loot replace inventory @s entity @s[nbt={Inventory:[{tag:{SoulBound:1b}}]}]

# Reset the death flag
#scoreboard players set @a[scores={death_flag=1..}] death_flag 0
