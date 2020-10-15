package xyz.nulldev.ts.ext

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.json.JSONObject

/**
 * Created by nulldev on 5/11/17.
 */

fun JSONObject.gson() = JsonParser.parseString(this.toString())!!
fun JsonObject.json() = JSONObject(this.toString())
