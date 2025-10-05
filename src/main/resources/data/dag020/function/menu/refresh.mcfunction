data modify storage ui mask set from storage dag020 current
execute on passengers run function dag020:menu/get_mask with entity @s data.page
data modify storage ui current set from storage ui mask

execute if score .type ui matches 1 run function dag020:minecart/load_page