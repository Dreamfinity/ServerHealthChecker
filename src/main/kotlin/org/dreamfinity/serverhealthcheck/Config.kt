package org.dreamfinity.serverhealthcheck

import cpw.mods.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.common.config.Configuration
import java.io.File

object Config {
    private var config: Configuration? = null
    private const val COMMON = "RMQ settings"
    var configDir = ""

    @JvmField
    var username: String = "guest"

    @JvmField
    var password: String = "guest"

    @JvmField
    var virtualHost: String = "/"

    @JvmField
    var host: String = "localhost"

    @JvmField
    var port: Int = 5672

    @JvmField
    var exchange: String = ""

    @JvmField
    var routingKey: String = ""


    @JvmStatic
    fun load(event: FMLPreInitializationEvent) {
        config = Configuration(File(event.modConfigurationDirectory, "ServerHealthCheck.cfg"), true)
        configDir = event.modConfigurationDirectory.absolutePath;
        config!!.load()
        syncData()
        config!!.save()
    }

    @JvmStatic
    fun reload() {
        config = Configuration(File(configDir, "ServerHealthCheck.cfg"), true)
        config!!.load()
        syncData()
        config!!.save()
    }

    @JvmStatic
    fun syncData() {
        username = config!!.getString("username", COMMON, "guest", "RMQ username")
        password = config!!.getString("password", COMMON, "guest", "RMQ passowrd")
        host = config!!.getString("host", COMMON, "localhost", "RMQ host")
        port = config!!.getInt("port", COMMON, 5672, 0, 65555, "RMQ port")
        exchange = config!!.getString("exchange", COMMON, "", "Exchange where to send messages to")
        routingKey = config!!.getString("routingKey", COMMON, "", "Routing key of queue to route message in given exchange")
        virtualHost = config!!.getString("virtualHost", COMMON, "/", "RMQ virtual host (you should not touch this is you installed rmq and didn't change this)")
    }
}