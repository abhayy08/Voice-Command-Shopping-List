package com.abhay.voicecommandshoppinglist.data

import android.util.Log
import com.abhay.voicecommandshoppinglist.BuildConfig
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

class NLPProcessor {

    private val client = OkHttpClient()
    private val apiUrl = "https://api-inference.huggingface.co/models/facebook/bart-large-mnli"
    private val apiKey = "Bearer ${BuildConfig.HUGGING_FACE_API_KEY}"
    private val labels = listOf("AddItem", "RemoveItem", "Unknown")

    fun processText(userInput: String): String {
        val requestBody = createRequestBody(userInput)
        val request = buildRequest(requestBody)

        return try {
            val response = client.newCall(request).execute()
            handleResponse(response, userInput)
        } catch (e: Exception) {
            "Error processing text: ${e.message}"
        }
    }

    private fun createRequestBody(userInput: String): RequestBody {
        val jsonPayload = JSONObject().apply {
            put("inputs", userInput)
            put("parameters", JSONObject().apply { put("candidate_labels", JSONArray(labels)) })
        }.toString()

        return jsonPayload.toRequestBody("application/json".toMediaType())
    }

    private fun buildRequest(requestBody: RequestBody): Request {
        return Request.Builder()
            .url(apiUrl)
            .post(requestBody)
            .addHeader("Authorization", apiKey)
            .build()
    }

    private fun handleResponse(response: Response, userInput: String): String {
        val responseBody = response.body?.string() ?: return "Error processing text"
        val jsonResponse = JSONObject(responseBody)
        val labelsArray = jsonResponse.getJSONArray("labels")
        val topIntent = labelsArray.getString(0)

        val (item, quantity) = extractItem(userInput)
        Log.d("NLPProcessor", "topIntent: $topIntent, item: $item, quantity: $quantity")

        return when (topIntent) {
            "AddItem" -> "Intent: AddItem, Extracted Item: $item, Quantity: $quantity"
            "RemoveItem" -> "Intent: RemoveItem, Extracted Item: $item, Quantity: $quantity"
            else -> "Intent: Unknown"
        }
    }

    private fun extractItem(text: String): Pair<String, Int> {
        val words = text.split(" ")
        var quantity = 1
        var item = "Unknown Item"

        for (i in words.indices) {
            if (words[i].toIntOrNull() != null) {
                quantity = words[i].toInt()
                if (i + 1 < words.size) {
                    item = words[i + 1]
                }
                break
            }
        }

        if (quantity == 1 && item == "Unknown Item") {
            item = words.lastOrNull() ?: "Unknown Item"
        }

        return Pair(item, quantity)
    }
}
