# Maid Expel

[![Available on GitHub](https://wsrv.nl/?url=https%3A%2F%2Fcdn.jsdelivr.net%2Fnpm%2F%40intergrav%2Fdevins-badges%403%2Fassets%2Fcozy%2Favailable%2Fgithub_vector.svg&n=-1)](https://github.com/Steven23334/Maid_Expel)
[![Available for Touhou Little Maid](https://cdn.modrinth.com/data/cached_images/ea5dc160571134bd0ea89ac52075b542fb331e46_0.webp)](https://modrinth.com/project/R0bDWFAW)

This is an addon for the **Touhou Little Maid** mod. It adds a "Release" button to the maid GUI, letting you release your maid back into the wild and drop everything she carries.

## 📦 New Content

- **Release Maid**  
  A new button in the maid GUI releases the maid: clears her owner binding so she becomes wild again, and drops every item she carries — backpack contents, the backpack item itself, armor, held items, TLM baubles, and Curios trinkets — onto the ground at her feet.

- **Full Inventory Drop**  
  Nothing is kept. The maid's backpack is emptied and the backpack item is unloaded, then dropped. All dropped items fall at the maid's position so you can pick them up.

- **Curios Support (Optional)**  
  If the Curios mod is installed, all Curios trinkets are also dropped. If Curios is not installed, the mod skips this step safely without crashing.

- **Depends on Steven Mod API**  
  This addon relies on the [Steven Mod API](https://github.com/Steven23334/Steven_Mod_API) for the maid GUI and event hooks. That API mod must be installed.

## 🎯 Purpose

- Lets players release a maid they no longer want, without manually stripping every item off her first.
- Keeps TLM's own state intact — using `setOwnerUUID(null)` and `setTame(false, false)` rather than discarding the entity.
- Minimal and targeted: one button, one network packet, one inventory helper.

## 🔧 Installation

1. Make sure both **Touhou Little Maid** and **Steven Mod API** are installed.
2. Place the `.jar` file of this mod into the `.minecraft/mods` folder.
3. Launch the game.

## ⚙️ Configuration

No configuration. This mod is designed to be plug-and-play.

## ⚠️ Requirements

- **Minecraft Versions**: 1.21.1
- **Mod Loader**: NeoForge 21.1.188+
- **Required Mods**:
    - [Touhou Little Maid](https://www.curseforge.com/minecraft/mc-mods/touhou-little-maid)
    - [Steven Mod API](https://github.com/Steven23334/Steven_Mod_API)
- **Optional Mod**: [Curios API](https://www.curseforge.com/minecraft/mc-mods/curios) — enables dropping of Curios trinkets

## ⚠️ Compatibility Notice

The "Release" button only appears in the maid GUI provided by **Steven Mod API**. If another addon replaces that GUI, the button may not show.

Releasing a maid is **irreversible** — there is no undo. Once released, the maid loses her owner and all carried items are dropped. Make sure you really want to release her before clicking.

If you also use addons that touch the maid's inventory or owner state, there is a small chance of behavior overlap. Because this mod has no toggle, the only way to isolate a conflict is to **disable one of the two mods entirely**.

## 📜 License

- Code: [MIT License](https://mit-license.org/)

## 🙏 Authors

- Programmer: Steven23334
- Inspiration: Love & Loathe mod by JumDa5he

---

# 女仆放生

[![可在 GitHub 上获取](https://wsrv.nl/?url=https%3A%2F%2Fcdn.jsdelivr.net%2Fnpm%2F%40intergrav%2Fdevins-badges%403%2Fassets%2Fcozy%2Favailable%2Fgithub_vector.svg&n=-1)](https://github.com/Steven23334/Maid_Expel)
[![适用于车万女仆](https://cdn.modrinth.com/data/cached_images/ea5dc160571134bd0ea89ac52075b542fb331e46_0.webp)](https://modrinth.com/project/R0bDWFAW)

这是一个为 **车万女仆 (Touhou Little Maid)** 模组开发的拓展，在女仆界面里添加了一个“放生”按钮，让你可以把女仆放归野外，并把她身上的东西全部掉落出来。

## 📦 新增内容

- **放生女仆**  
  女仆界面里新增一个“放生”按钮：清除女仆的主人绑定，让她恢复野生状态，并把她身上所有物品——背包内容物、背包物品本身、盔甲、手持、TLM 饰品、Curios 饰品——全部掉落在她脚下。

- **完整掉落背包**  
  什么都不会留下。背包内部会被清空，背包物品本身也会被卸下并掉落。所有掉落物都落在女仆所在位置，方便你捡起。

- **Curios 支持（可选）**  
  如果安装了 Curios 模组，女仆身上的 Curios 饰品也会一并掉落。如果未安装 Curios，本模组会安全跳过这一步，不会崩溃。

- **依赖 Steven Mod API**  
  本拓展依赖 [Steven Mod API](https://github.com/Steven23334/Steven_Mod_API) 提供的女仆界面与事件钩子。必须同时安装该 API 模组。

## 🎯 模组用途

- 让玩家可以放生不再想要的女仆，而不必先手动把东西一件件扒下来。
- 保持 TLM 自身状态完整——使用 `setOwnerUUID(null)` 和 `setTame(false, false)`，而不是直接删除实体。
- 改动最小且精准：一个按钮、一个网络包、一个物品栏工具类。

## 🔧 安装方法

1. 确保已安装 **车万女仆 (Touhou Little Maid)** 和 **Steven Mod API**。
2. 将本模组的 `.jar` 文件放入 `.minecraft/mods` 文件夹。
3. 启动游戏即可。

## ⚙️ 配置

无需配置。本模组设计为即插即用。

## ⚠️ 前置要求

- **Minecraft 版本**：1.21.1
- **模组加载器**：NeoForge 21.1.188+
- **必需模组**：
    - [车万女仆 (Touhou Little Maid)](https://www.curseforge.com/minecraft/mc-mods/touhou-little-maid)
    - [Steven Mod API](https://github.com/Steven23334/Steven_Mod_API)
- **可选模组**：[Curios API](https://www.curseforge.com/minecraft/mc-mods/curios) —— 用于掉落 Curios 饰品

## ⚠️ 兼容性提示

“放生”按钮只会出现在 **Steven Mod API** 提供的女仆界面里。如果其他拓展替换了该界面，按钮可能不会显示。

放生女仆是**不可逆**的操作——没有撤销。一旦放生，女仆就会失去主人，身上所有物品都会掉落。点击前请确认你真的想放生她。

如果你同时使用其他会改动女仆物品栏或主人状态的拓展，可能存在行为重叠的小概率。由于本模组没有开关，排查冲突的唯一方式是**完整禁用两个模组中的一个**。请自行承担混用带来的风险。

## 📜 许可证

- 代码：[MIT License](https://mit-license.org/)

## 🙏 作者

- 程序：Steven23334
- 灵感来源：JumDa5he 的车万女仆：爱憎分明模组