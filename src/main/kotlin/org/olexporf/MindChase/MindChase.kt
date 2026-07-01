package org.olexporf.mindchase

import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.UUID

class MindChase : JavaPlugin(), Listener, CommandExecutor {

    private val hunters = mutableSetOf<UUID>()
    private val speedrunners = mutableSetOf<UUID>()
    private var isGameRunning = false

    override fun onEnable() {
        server.pluginManager.registerEvents(this, this)

        getCommand("am")?.setExecutor(this)
        getCommand("im")?.setExecutor(this)
        getCommand("alert")?.setExecutor(this)
        getCommand("yo")?.setExecutor(this)

        logger.info("MindChase plugin has been successfully loaded")
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return true

        when (command.name.lowercase()) {
            "mc" -> {
                if (!sender.isOp) return true
                if (args.isNotEmpty() && args[0].lowercase() == "start") {
                    startGame(sender)
                } else if (args.isNotEmpty() && args[0].lowercase() == "stop") {
                    if (isGameRunning) {
                        stopGame("§cThe game is stopped by the admin.")
                    } else {
                        Bukkit.broadcastMessage("§cThe game hasn't started yet")
                    }
                } else {
                    sender.sendMessage("§eUsage: §6/mc start §eor §6/mc stop")
                }
            }

            "im" -> {
                if (!isGameRunning || !hunters.contains(sender.uniqueId)) {
                    sender.sendMessage("§cOnly hunters can talk here during the game")
                    return true
                }
                val message = args.joinToString(" ")
                hunters.mapNotNull { Bukkit.getPlayer(it) }.forEach { hunter ->
                    hunter.sendMessage("§4[Hunters chat] §c${sender.name}: §f$message")
                }
            }

            "alert" -> {
                val loc = sender.location
                val worldName = when (loc.world?.name) {
                    "world" -> "§aOverworld"
                    "world_nether" -> "§cNether"
                    "world_the_end" -> "§dEnd"
                    else -> "???"
                }
                Bukkit.broadcastMessage("§f<${sender.name}> alerted at §x§F§F§0§0§0§0${loc.blockX} ${loc.blockY} ${loc.blockZ} §fin the ${worldName}!")
            }

            "yo" -> {
                Bukkit.broadcastMessage("§f<${sender.name}> §aI am lost and need coordinates")
            }
        }
        return true
    }

    private fun startGame(starter: Player) {
        val players = Bukkit.getOnlinePlayers().toList()

        if (players.size < 2) {
            starter.sendMessage("§cYou need at least 2 people to start!")
            return
        }

        hunters.clear()
        speedrunners.clear()

        val hunterCount = if (players.size <= 4) 1 else 2

        val shuffled = players.shuffled()

        val hunterPlayers = shuffled.take(hunterCount)
        val runnerPlayers = shuffled.drop(hunterCount)

        hunterPlayers.forEach { player ->
            hunters.add(player.uniqueId)
            player.sendMessage("§4==============================")
            player.sendMessage("§x§F§F§0§0§0§0§lYour role: Hunter (Imposter)")
            player.sendMessage("§fHunters-exclusive chat: §a/im <message>")
            player.sendMessage("§x§F§F§0§0§0§0Your goal: kill every speedrunner (innocent)")
            player.sendMessage("§4==============================")
        }

        runnerPlayers.forEach { player ->
            speedrunners.add(player.uniqueId)
            player.sendMessage("§a==============================")
            player.sendMessage("§x§2§D§F§F§0§0§lYour role: Speedrunner (Innocent)")
            player.sendMessage("§aYour goal: beat the game or get rid of every imposter")
            player.sendMessage("§a==============================")
        }

        isGameRunning = true
        Bukkit.getOnlinePlayers().forEach {
            it.sendTitle("§cMindChase", "§fThe game has started", 10, 70, 20)
            it.sendMessage("§c[MindChase] §fImposters in this round: §c$hunterCount")
        }
    }

    private fun stopGame(reason: String) {
        isGameRunning = false
        hunters.clear()
        speedrunners.clear()
        Bukkit.broadcastMessage("§c§lMindChase: §f$reason")
    }

    @EventHandler
    fun onPlayerJoin(event: org.bukkit.event.player.PlayerJoinEvent) {
        val player = event.player

        // Отправляем сообщения лично зашедшему игроку, чтобы не спамить остальным
        player.sendMessage("§x§F§4§5§4§6§EThanks for using MindChase plugin!")
        player.sendMessage("§x§C§4§5§4§F§4This plugin is inspired by CaptainPuffy's & zorato's videos")
        player.sendMessage("§x§5§4§F§4§B§3§nIt's recommended to play with advancements and death messages disabled")
        player.sendMessage("§x§C§9§E§5§B§4The number of hunters (1 or 2) depends on the total number of players. Everyone else becomes a speedrunner.\n" +
                "\n" +
                "1-4 players -> 1 hunter\n" +
                "5+ players -> 2 hunters")
        player.sendMessage("§x§F§F§3§4§0§0§lCAUTION: Hardcore is highly recommended, otherwise speedrunners will be able to respawn and continue playing after dying.")
    }

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        if (!isGameRunning) return

        Bukkit.getScheduler().runTaskLater(this, Runnable {
            checkWinConditions()
        }, 1L)
    }

    private fun checkWinConditions() {
        if (!isGameRunning) return

        val aliveHunters = hunters.mapNotNull { Bukkit.getPlayer(it) }
            .count { !it.isDead && it.gameMode != GameMode.SPECTATOR }

        val aliveRunners = speedrunners.mapNotNull { Bukkit.getPlayer(it) }
            .count { !it.isDead && it.gameMode != GameMode.SPECTATOR }

        if (aliveHunters == 0) {
            Bukkit.getOnlinePlayers().forEach { it.sendTitle("§aSPEEDRUNNERS WON", "All hunters are dead", 10, 100, 20) }
            stopGame("Innocent team has won (all imposters are dead)")
        } else if (aliveRunners == 0) {
            Bukkit.getOnlinePlayers().forEach { it.sendTitle("§cHUNTERS WON", "All speedrunners are dead", 10, 100, 20) }
            stopGame("Imposter team has won (all innocents are dead)")
        }
    }

    @EventHandler
    fun onWorldChange(event: PlayerChangedWorldEvent) {
        if (!isGameRunning) return
        val player = event.player

        if (event.from.name.endsWith("the_end") && player.world.name.endsWith("world")) {
            if (speedrunners.contains(player.uniqueId) && player.gameMode != GameMode.SPECTATOR) {
                Bukkit.getOnlinePlayers().forEach {
                    it.sendTitle("§aSPEEDRUNNERS WON", "§e${player.name} §fhas beaten the game!", 10, 100, 20)
                }
                stopGame("Innocent team has won (the Ender-Dragon was defeated)")
            }
        }
    }
}