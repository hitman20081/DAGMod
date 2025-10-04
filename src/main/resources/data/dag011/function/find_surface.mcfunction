# Scan downward to find the surface
execute if block ~ ~-1 ~ #minecraft:air positioned ~ ~-1 ~ run function dag011:find_surface
# Once we find solid ground, place structure
execute unless block ~ ~-1 ~ #minecraft:air run function dag011:place_at_surface