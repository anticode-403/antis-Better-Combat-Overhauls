# ABCO
ABCO is a mod designed to add many features to Better Combat necessary to make it a truly engaging combat experience.

# Core Features
ABCO is stock full of great features to spice up your Minecraft combat!
- Critical hits are now dependent on your combo, usually granted by the last hit in the combo.
- Critical damage is now weapon dependent, with higher attack speed weapons giving the biggest benefit.
- Some weapons are now _Versatile_, which gives them increased damage, a unique combo, and the ability to special attack.
- Some weapons are now _Paired_, which gives them special attacks while dual-wielded.
- Some weapons are now _Finesse_, which replaces their special attacks with a parry mechanic.
- Two-Handed, Paired, and Versatile weapons now have the ability to special attack.
- Weapons only attempt to mine if the block being targeted requires that weapon as a tool (axes and wood, swords and cobwebs)
- For mod and datapack developers, an expanded list of attributes for you to use when creating your unique weapon!

# Compatibility
As of current, ABCO is **NOT** compatible with BCE (Better Combat Extension) or Spell Engine.

# Adding Support
If your mod uses default Better Combat weapon attributes, then it should support ABCO out of the box!

If your weapon has a unique right click action AND uses default Better Combat attributes, ABCO may overwrite the right click action. To prevent this, set the `special_attacks` attribute of the weapon to an empty array. (i.e. `"special_attacks": []`)

If not, there's a very simple list of attributes you can add to your weapon attributes:
```JSON5
{
  "attributes": {
    // ...
    "critical_multiplier": 1.5,
    "paired": false,
    "finesse": false,
    "versatile": false,
    "alternate_pose": "",
    "parry_pose": "",
    "parry_duration": 15,
    "parry_punishment": 20,
    "parry_resistance": 1.0,
    "versatile_damage": 0.0,
    "versatile_attacks": [
      // ...
    ],
    "special_attacks": [
      // ...
    ]
  },
}
```
If `versatile` is true, your weapon attributes MUST define a valid `versatile_attacks`. Versatile is a weapon attribute that gives your weapon two different attack attributes based on whether you are two-handing your weapon. Versatile damage is _additional_ damage while using versatile and will be shown on the item tooltip.

If `paired` is true, your weapon will get special attacks while it is being dual-wielded with a weapon of the same type.

If `finesse` is true, your weapon attributes MUST define a valid `parry_pose`. Finesse is a weapon attribute that replaces your special attack with a parry on interact. `parry_punishment` is the cooldown your weapon suffers if you fail to block any damage during the parry duration. `parry_resistance` (0-1) is the percentage damage resistance you are granted while blocking.

Versatile, Paired, and Two-Handed are all _incompatible_ with each other. Finesse is compatible with all of them, however Versatile Finesse is unnecessary.

Additionally, ABCO adds new Attack attributes:
```JSON5
{
  // ...
  "critical": false,
  "knockback_multiplier": 1.0,
  "attack_speed_multiplier": 1.0,
}
```
These new attributes are mostly self-explanatory but should be powerful tools for creating unique and interesting attack combos, as well as an engaging gameplay loop. In ABCO's default datapack, most attack combos end in a critical hit.

There's no need to check for ABCO, either. If ABCO is NOT loaded, Better Combat will simply ignore these additional attributes!

# TODO
These are a list of planned future features. These features aren't necessarily guaranteed to come to ABCO and there will be no specific release date, but they are intended.
- Finesse attribute
  - Deflect/parry-oriented weapons. Finesse weapons do not have special attacks.
- Support for Malfu Combat Animation
- Versatile attack speed
  - Just another lever that can make versatile weapons unique
- Provide weapon attributes for common mods
- Expanded weapon attributes (attack-specific range, weapon and attack-specific movement speed, etc)
- MC 1.21.x and beyond
- Bug fixes