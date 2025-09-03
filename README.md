# Minecraft Plugins

A collection of simple, useful, and well-designed minecraft paper plugins,
developed to work in the latest versions of minecraft.

## Plugins

Each plugin lives in its own subfolder.

* **CursedWarps** [[more]](https://github.com/fabiogvdneto/minecraft-plugins/tree/main/warps) \
  This plugin is more than a simple warp plugin. It also gives your players the ability to
  create their own "homes" and send teleportation requests to other players (tpa).
* **CursedKits** [[more]](https://github.com/fabiogvdneto/minecraft-plugins/tree/main/kits) \
  A kit plugin, as simple as it should be. Create kits for your players with ease without 
  having to touch configuration files or GUIs. It supports every item you can imagine,
  without loosing specificity. From enchantments to NBT tags, everything is supported.

## Getting Started

Before building any plugin, make sure you have JDK version 17 or superior installed on your machine.
Gradle is not required since it comes bundled with the repository.

### Intallation

You can either:

1. Build all plugins at once:
   ```bash
   ./gradlew shadowJar
   ```
2. Build a single plugin:
   ```bash
   ./gradlew warps:shadowJar
   ./gradlew kits:shadowJar
   ```

The jar files will be created in `<module>/build/libs/<plugin-name>.jar`.