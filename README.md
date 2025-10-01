# ABCO
ABCO is a mod designed to add many features to Better Combat necessary to make it a truly engaging combat experience.

# Adding Support
If your mod uses default Better Combat weapon attributes, then it should support ABCO out of the box! 

If not, there's a very simple list of attributes you can add to your weapon attributes:
```JSON5
"versatile": true,
"versatile_damage": 2.0,
"versatile_attacks": [
// ...
],
"special_attacks": [
// ...
]
```
If versatile is true, your weapon attributes MUST define a valid versatile_attacks. Versatile is a weapon attribute that gives your weapon two different attack attributes based on whether or not you are two-handing your weapon. Versatile damage is _additional_ damage while using versatile and will be shown on the item tooltip.

Additionally, ABCO adds new Attack attributes:
```JSON5
"critical": false,
"knockback_multiplier": 1
```
These new attributes are mostly self-explanatory but should be powerful tools for creating unique and interesting attack combos, as well as an engaging gameplay loop. In ABCO's default datapack, most attack combos end in a critical hit.

### I wrote a datapack and my weapon_attributes are not overriding ABCO's. Why?
Unfortunately, due to the way that Fabric loads datapacks, ABCO completely overwrites Better Combat's parsing algorithm. What this means is that `data/bettercombat/weapon_attributes` is NOT used while ABCO is installed. _Instead_, all attributes are loaded from `data/abco/weapon_attributes`. Unlike most datapack files, `weapon_attributes` loaded this way consider `bettercombat` as their namespace rather than `abco` to preserve out of the box compatibility.