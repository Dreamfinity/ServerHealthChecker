package org.dreamfinity.serverhealthcheck

import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.event.*
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.command.ServerCommandManager
import net.minecraft.server.MinecraftServer
import net.minecraft.server.dedicated.DedicatedServer
import org.apache.logging.log4j.LogManager
import org.dreamfinity.serverhealthcheck.rmq.Event
import org.dreamfinity.serverhealthcheck.rmq.RabbitClient
import org.dreamfinity.serverhealthcheck.rmq.RmqEventMessage

@Mod(
    modid = ServerHealthCheck.MODID,
    name = ServerHealthCheck.NAME,
    version = ServerHealthCheck.VERSION,
    acceptableRemoteVersions = "*"
)
class ServerHealthCheck {
    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        Config.load(event)
        RabbitClient.setupConnectionFactory()
        logger.info("Server Health Check mod has been loaded")
    }

    @Mod.EventHandler
    fun serverStopped(event: FMLServerStoppedEvent) {
        val serverName = (DedicatedServer.getServer() as DedicatedServer).getStringProperty("server-name", "Unknown")

        val message = RmqEventMessage().apply {
            setEvent(Event("server_health_event", serverName))
            setMessage("{\"status\":\"server_stopped\"}")
        }

        RabbitClient.publish(
            message.toJSONString()
        )
        logger.info("$serverName is stopped. Message was sent to RMQ")
    }

    @Mod.EventHandler
    fun onServerStart(event: FMLServerStartingEvent?) {
        val server = MinecraftServer.getServer()
        val serverCommandManager = server.commandManager as ServerCommandManager
        serverCommandManager.registerCommand(
            object : CommandBase() {
                override fun getCommandName(): String {
                    return "serverhealthcheck"
                }

                override fun getCommandUsage(sender: ICommandSender?): String {
                    return "/serverhealthcheck reload"
                }

                override fun processCommand(sender: ICommandSender, args: Array<out String>) {
                    when (args.size) {
                        0 -> logger.info("/serverhealthcheck reload")
                        else -> {
                            when (args.first()) {
                                "reload" -> {
                                    Config.reload()
                                    RabbitClient.setupConnectionFactory()
                                    logger.info("config reloaded")
                                }

                                else -> logger.info("/serverhealthcheck reload")
                            }
                        }
                    }
                }

            }
        )
    }

    companion object {
        const val MODID = "serverhealthcheck"
        const val NAME = "Mod ServerHealthCheck"
        const val VERSION = "@version@"
        var logger = LogManager.getLogger(MODID)

        @Mod.Instance(MODID)
        var instance: ServerHealthCheck? = null
    }
}