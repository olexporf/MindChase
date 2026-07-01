# MindChase

**MindChase** is a Minecraft Spigot/Paper mini-game plugin that includes mechanics of two games: Minecraft Manhunt and Among Us. This plugin was mostly inspired by [CaptainPuffy's](https://youtu.be/HC9tXLFwXSU) and [zorato's](https://youtu.be/JaN_pfpr1tE?si=ux4jxnvuHMQO9zEB) _«Minecraft Manhunt, But I Don't Know Who's The Hunter»_ series.

---

## Game Concept

A group of players attempts to beat Minecraft by defeating the Ender Dragon. However, some players among them are secretly **Hunters (Imposters)** whose goal is to eliminate all **Speedrunners (Innocents)**. 

* **The Balance:** The number of hunters (1 or 2) scales with the total player count. Everyone else becomes a speedrunner.
  * `1-4 players` ➡️ `1 hunter`
  * `5+ players` ➡️ `2 hunters`

⚠️ **CAUTION:** Hardcore mode is highly recommended! Otherwise, speedrunners will be able to respawn and break the intended game loop.
**Recommendation:** Disable vanilla advancements and death messages for better playing experience!

---

## 🛠️ Features & Commands

The plugin includes custom mechanics to enhance communication and gameplay:

* `/mc start` — Starts the game, shuffles roles, and gives specific goals (Op only).
* `/mc stop` — Force-stops the running match (Op only).
  
* `/im <message>` — A private, encrypted chat that is exclusive to Hunters.
* `/alert` — Broadcasts your current coordinates and dimension (`Overworld`, `Nether`, or `End`) to the global chat.
* `/yo` — Quickly sends a predefined message: *"I am lost and need coordinates"*.

---

## ⚙️ Requirements & Installation

1. Download the `MindChase.jar` from directory.
2. Drop the file into your server's `plugins/` folder.
3. Ensure you register commands in your `plugin.yml`.


## 🔃 Updates

 **Plugin is already finished but has a chance to be updated**.

 **Compatible Minecraft Versions: 26.1+**
