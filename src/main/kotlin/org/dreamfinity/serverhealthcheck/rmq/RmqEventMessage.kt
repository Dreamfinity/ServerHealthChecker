package org.dreamfinity.serverhealthcheck.rmq

import org.json.simple.JSONObject

class RmqEventMessage {
    private val json: JSONObject = JSONObject()

    fun setEvent(event: Event): RmqEventMessage {
        json["event"] = event.json
        return this
    }

    fun setMessage(message: String?): RmqEventMessage {
        json["message"] = message
        return this
    }

    fun setMetadata(metadata: JSONObject?): RmqEventMessage {
        json["metadata"] = metadata
        return this
    }

    fun toJSONString(): String {
        return json.toJSONString()
    }
}