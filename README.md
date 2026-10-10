# 经验绿宝石 Experience Emerald

Experience Emerald 是一个 NeoForge 1.21.1 小型模组：把绿宝石转成可封存经验的
经验绿宝石，并允许玩家通过砂轮安全取回原始绿宝石和经验。

## 功能

- 手持绿宝石右击，会按可用经验一次性批量转化尽可能多的绿宝石。
- 默认优先吸收附近经验球，不足的部分再从玩家自身经验扣除。
- 每颗经验绿宝石默认消耗 5 点经验；经验不足一颗时不会消耗物品或经验。
- 经验只够部分转化时，会转换可负担的数量，剩余绿宝石留在手上。
- 将经验绿宝石放入砂轮，可输出普通绿宝石并返还配置的经验值。
- 粒子、音效、转化数值和砂轮返还方式都可以在 common 配置中调整。

## 环境

- Minecraft 1.21.1
- NeoForge 21.1.238
- Java 21

## 开发

```powershell
.\gradlew.bat compileJava
.\gradlew.bat runClient
.\gradlew.bat runClient2
.\gradlew.bat runServer
```

`runClient` 使用 Alice 和默认 `run/` 目录，`runClient2` 使用 Bob 和
`run-client-2/` 目录，`runServer` 使用 `run-server/` 目录。

构建完整产物：

```powershell
.\gradlew.bat clean build
```

输出位于 `build/libs/experience_emerald-1.0.0-r2.jar`。

## 配置

common 配置位于 `config/experience_emerald-common.toml`，包含吸收经验量、副手开关、
冷却时间、经验球优先、扫描半径、砂轮返还经验和粒子音效等选项。

## English Summary

Experience Emerald adds an emerald-based experience storage loop for NeoForge
1.21.1. Right-click an emerald to absorb experience, then use a grindstone to
safely convert the experience emerald back into an emerald and experience.
