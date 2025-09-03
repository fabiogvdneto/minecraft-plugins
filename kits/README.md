# Introduction

Kits is a paper plugin implementation of a kit system. The plugin allows
server administrators to create kits that can be redeemed by regular players.

### Features

* Create a kit with your inventory's items.
* No need to touch configuration files or GUIs.

### Future Work

* Kit cooldown.
* Kit price.

### Commands

| Name               | Description              |
|--------------------|--------------------------|
| /kit <name>        | Redeem a kit.            |
| /kits              | View all kits available. |
| /createkit <name>  | Create a new kit.        |
| /deletekit <name>  | Delete a kit.            |

### Permissions

| Name                   | Description                     |
|------------------------|---------------------------------|
| kits.admin             | Grant all advanced permissions. |
| kits.command.kit       | Execute `/kit`.                 |
| kits.command.kits      | Execute `/kits`.                |
| kits.command.createkit | Execute `/createkit`.           |
| kits.command.deletekit | Execute `/deletekit`.           |
| kits.kit.<name>        | Redeem kit <name>.              |