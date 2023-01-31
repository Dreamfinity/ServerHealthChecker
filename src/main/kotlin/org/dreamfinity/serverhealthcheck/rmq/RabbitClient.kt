package org.dreamfinity.serverhealthcheck.rmq

import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.MessageProperties
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.dreamfinity.serverhealthcheck.ServerHealthCheck
import org.dreamfinity.serverhealthcheck.Config
import java.io.IOException
import java.util.concurrent.TimeoutException

object RabbitClient {
    private val factory = ConnectionFactory()
    private var logger: Logger = LogManager.getLogger(ServerHealthCheck.MODID)

    fun publish(message: String, exchangeName: String? = Config.exchange, routingKey: String? = Config.routingKey) {
        val connection = connection ?: return
        try {
            val channel = connection.createChannel()
            val messageBytes = message.toByteArray()
            channel.basicPublish(
                exchangeName,
                routingKey,
                MessageProperties.PERSISTENT_TEXT_PLAIN,
                messageBytes
            )
            logger.info("Sent message to '$exchangeName/$routingKey'")
        } catch (e: IOException) {
            logger.warn("Unable to create new channel")
            e.printStackTrace()
        }
    }

    fun setupConnectionFactory() {
        factory.username = Config.username
        factory.password = Config.password
        factory.virtualHost = Config.virtualHost
        factory.host = Config.host
        factory.port = Config.port
    }

    private val connection: Connection?
        get() = try {
            factory.newConnection()
        } catch (e: IOException) {
            logger.warn("Unable to create new connection:")
            e.printStackTrace()
            null
        } catch (e: TimeoutException) {
            logger.warn("Unable to create new connection:")
            e.printStackTrace()
            null
        }
}