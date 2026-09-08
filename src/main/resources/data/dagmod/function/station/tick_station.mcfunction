#Face random player
execute facing entity @r[distance=..3] feet run tp @s ~ ~ ~ ~ ~

#Summon beam marker (armor stand, not an area_effect_cloud -- AECs render their own ambient swirl
#particle proportional to Radius with no way to fully disable it; an armor stand has none at all)
summon minecraft:armor_stand ~ ~ ~ {Tags:["healing","healing_beam"],Invisible:true,Invulnerable:true,Marker:true,NoGravity:true}

#Rotate nearest beam marker to match station rotation and start its expiry timer
#(armor stands have no built-in Duration field like AEC did, so this replaces that manually --
#tick_beam.mcfunction increments it each tick and kills the beam once it passes 60. Both selectors
#below rely on the just-summoned marker being nearest to the station, same as the AEC version did)
tp @n[type=minecraft:armor_stand, tag=healing_beam] ~ ~0.95 ~ ~ ~
scoreboard players set @n[type=minecraft:armor_stand, tag=healing_beam] healing_beam_timer 0