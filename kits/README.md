# Introduction

Kits is a paper plugin implementation of a kit system. The plugin allows
server administrators to create kits that can be redeemed by regular players.

### Features

* Create kits based on the contents of your inventory.
* Add cooldown to kits.
* Add price to kits (vault support).
* Add limit to kits (can only be redeemed x times per player).

### Commands

| Name               | Description              |
|--------------------|--------------------------|
| /kit <name>        | Redeem a kit.            |
| /kits              | View all kits available. |
| /createkit <name>  | Create a new kit.        |
| /deletekit <name>  | Delete a kit.            |

### Permissions

| Name                   | Description           |
|------------------------|-----------------------|
| kits.admin             | Advanced permissions. |
| kits.basics            | Basic permissions.    |
| kits.command.kit       | Execute `/kit`.       |
| kits.command.kits      | Execute `/kits`.      |
| kits.command.createkit | Execute `/createkit`. |
| kits.command.deletekit | Execute `/deletekit`. |
| kits.kit.*             | All kits.             |
| kits.kit.<name>        | Redeem kit <name>.    |

* kits.admin
  * Access to all commands.
  * Access to all kits.
  * Bypass kit cooldown.
  * Bypass kit limits.