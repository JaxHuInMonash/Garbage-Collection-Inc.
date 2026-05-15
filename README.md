# FIT2099 Assignment (Semester 1, 2026)

```
 _______  _______  ______    _______  _______  _______  _______                          
|       ||   _   ||    _ |  |  _    ||   _   ||       ||       |                         
|    ___||  |_|  ||   | ||  | |_|   ||  |_|  ||    ___||    ___|                         
|   | __ |       ||   |_||_ |       ||       ||   | __ |   |___                          
|   ||  ||       ||    __  ||  _   | |       ||   ||  ||    ___|                         
|   |_| ||   _   ||   |  | || |_|   ||   _   ||   |_| ||   |___                          
|_______||__| |__||___|  |_||_______||__| |__||_______||_______|                         
 _______  _______  ___      ___      _______  _______  _______  ___   _______  __    _   
|       ||       ||   |    |   |    |       ||       ||       ||   | |       ||  |  | |  
|       ||   _   ||   |    |   |    |    ___||       ||_     _||   | |   _   ||   |_| |  
|       ||  | |  ||   |    |   |    |   |___ |       |  |   |  |   | |  | |  ||       |  
|      _||  |_|  ||   |___ |   |___ |    ___||      _|  |   |  |   | |  |_|  ||  _    |  
|     |_ |       ||       ||       ||   |___ |     |_   |   |  |   | |       || | |   |  
|_______||_______||_______||_______||_______||_______|  |___|  |___| |_______||_|  |__|  
 ___   __    _  _______                                                                  
|   | |  |  | ||       |                                                                 
|   | |   |_| ||       |                                                                 
|   | |       ||       |                                                                 
|   | |  _    ||      _| ___                                                             
|   | | | |   ||     |_ |   |                                                            
|___| |_|  |__||_______||___|                                                                                                                                                                                                                         
```

1. The Swarmling (The Map Purifier)

The Swarmling is a biological containment unit designed to hunt the invasive Parasites (x). It transitions based on the density of "prey" versus the presence of "threats".

The 4 States:

DormantState (Initial): The Swarmling curls into a stone-like ball.
Transition A (to ScoutState): If no Parasites are within 5 tiles for 3 turns.
Transition B (to CullingState): If >3 Parasites enter a 3-tile radius.
Immediate Effect: Hardened Shell. Reflects the next 20 points of damage back at any attacker.

ScoutState: Moves rapidly searching for infestation.
Transition A (to CullingState): If it detects any Parasite in its surroundings.
Transition B (to BerserkState): If it takes damage from a non-Parasite source (e.g., a Worker or Fire).
Immediate Effect: Pheromone Trail. Spawns a "trail" on its current tile that grants Workers a +2 movement boost for 5 turns.

CullingState (Unique Behavior 1): Focuses entirely on Parasites.
Transition A (to BerserkState): If a Worker is attacked by a Parasite within its line of sight.
Transition B (to DormantState): If the local Parasite count reaches zero.
Unique Behavior (Devour): Instead of a standard attack, it "consumes" a Parasite from the ground, removing it and healing itself for 10% max HP.
Immediate Effect: High-Frequency Screech. Pulls all Parasites within 8 tiles exactly 2 tiles closer to the Swarmling.

BerserkState (Unique Behavior 2): A whirlwind of aggression.
Transition A (to CullingState): If its health drops below 20%.
Transition B (to DormantState): If it has killed at least 3 entities in this state.
Unique Behavior (Chain Strike): Every attack triggers a shockwave hitting all adjacent hostile actors for 50% damage.
Immediate Effect: Tidal Strike. Knocks back all adjacent actors (Worker/Parasite) by 3 tiles and deals 15 damage.

2. The Meeple (The High-Tech Nomad)

A friendly-ish mechanical creature that acts as a support station but malfunctions when overwhelmed.

The 4 States:

NomadState (Initial): Wanders the map aimlessly.
Transition A (to EngineerState): If adjacent to "Scrap" or "Undead" remains.
Transition B (to GuardianState): If a Worker’s HP is below 50% within 4 tiles.
Immediate Effect: Supply Beacon. Reveals all hidden items/Parasites within 10 tiles for 3 turns.

EngineerState (Unique Behavior 1): Focuses on resource conversion.
Transition A (to NomadState): If no Undead/Scrap items remain in its surroundings.
Transition B (to OverloadedState): If more than 3 Undead actors surround it.
Unique Behavior (Transmute): Converts an Undead corpse into "Refined Material," which Workers can pick up to increase Max HP by 20.
Immediate Effect: Repelling Pulse. Makes all adjacent non-Worker actors "Dizzy" (unable to act) for 1 turn.

GuardianState (Unique Behavior 2): Protects and buffs Workers.
Transition A (to NomadState): If no Workers are within 4 tiles.
Transition B (to OverloadedState): If its "Shield Battery" counter reaches 0.
Unique Behavior (Kinetic Link): Tethers to the nearest Worker; 50% of damage taken by that Worker is transferred to the Meeple.
Immediate Effect: Nanobot Injection. Grants all Workers within 3 tiles a temporary +30 HP shield.

OverloadedState: A state of mechanical panic.
Transition A (to NomadState): If it remains in this state for 3 turns (energy discharge).
Transition B (to GuardianState): If a Worker "repairs" it via interaction.
Immediate Effect: Static Discharge. Forces every actor within 2 tiles to drop their entire inventory onto the ground.

Contribution Log for CL_AppliedSession21TeamD
https://docs.google.com/spreadsheets/d/1s2YXhHnrShSlOuxDv9mJ1T95J-Y2PVcPt5fqlGWt8TM/edit?gid=1582995291#gid=1582995291