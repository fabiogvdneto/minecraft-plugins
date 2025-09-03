# Introduction

Warps is a paper plugin that gives players the ability to teleport
to predefined locations. These locations can be a warp or a home.

**Warps** are public locations designed to be accessed by everyone,
as long as the player has permission to do so. It's the server
administrator's responsibility to create, delete, and configure
each warp.

**Homes** are private locations owned by someone. The owner has
full control over the homes he creates, and can decide whether
other players can visit their homes or not.

### Features

* Create warps.
* Create homes.
* Per-player home limit (permission-based).
* Per-player teleportation delay (permission-based).
* Efficient detection of movement and damage during teleportation.
* Prevent command execution while teleporting.
* Cancel teleportation before it happens.
* Send teleportation requests to other players (/tpa).
* Data auto-saving (automatically save data to prevent data loss on crashes).
* Data auto-purging (automatically delete data that is no longer needed).
* All messages are customizable.
* All messages support json-style formatting (powered by Adventure API).

### Commands

| Name                   | Description                               |
|------------------------|-------------------------------------------|
| `spawn`                | Teleport to the spawn.                    |
| `warps`                | List all warps available to you.          |
| `warp <name>`          | Teleport to the specified warp.           |
| `setwarp <name>`       | Create a new warp.                        |
| `delwarp <name>`       | Delete a warp.                            |
| `homes`                | List all your homes.                      |
| `home <name> [player]` | Teleport to the specified home.           |
| `sethome <name>`       | Create a new (private) home.              |
| `delhome <name>`       | Delete a home.                            |
| `tpa <player>`         | Send a teleportation request to a player. |
| `tphere <player>`      | Teleport a player to your location.       |
| `tpaccept [player]`    | Accept a teleportation request.           |
| `tpdeny [player]`      | Deny a teleportation request.             |
| `tpcancel`             | Cancel the teleportation.                 |

### Permissions

| Name                             | Description                                             |
|----------------------------------|---------------------------------------------------------|
| warps.admin                      | All permissions.                                        |
| warps.command.setwarp            | Allow to execute /setwarp.                              |
| warps.command.delwarp            | Allow to execute /delwarp.                              |
| warps.command.delhome.others     | Allow to execute /delhome to other players.             |
| warps.command.sethome.others     | Allow to execute /sethome to other players.             |
| warps.command.tpa                | Allow to execute /tpa.                                  |
| warps.command.tphere             | Allow to execute /tphere.                               |
| warps.warp.<name>                | Allow to teleport to <name> even if the warp is closed. |
| warps.homes.limit.<number>       | Allow to create a maximum of <number> homes.            |
| warps.teleporter.delay.<seconds> | Wait <seconds> before actually teleporting.             |