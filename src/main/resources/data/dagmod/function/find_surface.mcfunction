# Scan downward to find the surface
execute if block ~ ~-1 ~ #minecraft:air positioned ~ ~-1 ~ run function dagmod:find_surface
# Once we find solid ground, place structure
execute unless block ~ ~-1 ~ #minecraft:air run function dagmod:place_at_surface