# Reset a player's health to 10 hearts
attribute @s minecraft:max_health base set 20

#Clear effects
effect clear @s

# Reset a player's race
#scoreboard players set @s has_race 0
team leave @s

# Reset a player's class
scoreboard players set @s select_class 0
tag @p remove warrior
tag @p remove mage
tag @p remove ranger
tag @p remove cleric


# Give selection books again
#function dag000:player/give_selection_books