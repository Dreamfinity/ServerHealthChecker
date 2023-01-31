package org.dreamfinity.serverhealthcheck.rmq

import org.json.simple.JSONObject

class Event {
    var json: JSONObject
        private set

    constructor() {
        json = JSONObject()
    }

    constructor(type: String?, server: String?) {
        json = JSONObject()
        json["type"] = type
        json["server"] = server
    }

    fun setType(type: String?): Event {
        json["type"] = type
        return this
    }

    fun setServer(server: String?): Event {
        json["server"] = server
        return this
    }
}