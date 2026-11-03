<div align="center">

<img src="assets/banner.svg" alt="WhitelistPerm" width="100%">

# WhitelistPerm

**资源服权限白名单** —— 按权限节点控制玩家进入指定世界：空岛玩家/无权限玩家禁止进入资源服，支持 PlaceholderAPI 动态授权。

![Platform](https://img.shields.io/badge/Platform-Spigot%20%2F%20Paper-62bda8?style=flat-square&logo=minecraft&logoColor=white)
![Minecraft](https://img.shields.io/badge/MC-1.20.x-8a9a5b?style=flat-square)
![Java](https://img.shields.io/badge/Java-17-f89820?style=flat-square&logo=openjdk&logoColor=white)
![Depends](https://img.shields.io/badge/softdepend-PlaceholderAPI-e2b90f?style=flat-square)

[功能](#-功能) · [配置](#️-配置)

</div>

## ✨ 功能

- **世界级白名单**：进入受控世界时检查玩家是否持有 `perm` 列表中的权限节点，没有则拒绝并提示
- **显式白名单**：`white-list` 里的玩家名直接放行
- **Placeholder 动态授权**：`enable-placeholder` 开启后按 `placeholder` 变量判定资格，并通过 `placeholder-commands` 自动授予对应权限（如 `lp user %player_name% permission set island.player true`）
- **总开关**：`enable` 一键启停，`debug` 输出判定过程

## ⚙️ 配置

```yaml
enable: false
perm:
  - island.player
white-list:
  - windy
message: "&c空岛玩家或无权限玩家禁止进入资源服"
enable-placeholder: false
placeholder:
  - '%iridiumSkyblock_current_island_owner%'
placeholder-commands:
  - 'lp user %player_name% permission set island.player true'
```

## 🔨 构建

```bash
./gradlew build
```
