# ABCO
ABCO is a mod designed to add many features to Better Combat necessary to make it a truly engaging combat experience.

# Adding Support
If your mod uses default Better Combat weapon attributes, then it should support ABCO out of the box! If not, there's a very simple list of attributes you can add to your weapon attributes:
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
