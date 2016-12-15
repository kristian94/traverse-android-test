package com.cborum.traverse.backend

import android.content.Context
import android.util.Log
import com.auth0.android.result.UserProfile
import com.cborum.traverse.backend.ITraverseCallback
import com.cborum.traverse.utils.CredentialsManager
import java.net.URL
import java.util.*
import java.io.IOException
import javax.net.ssl.HttpsURLConnection

import com.github.salomonbrys.kotson.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.jetbrains.anko.doAsync
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * Created by ChristopherBorum on 08/10/16.
 */

val baseUrl = "https://api.bonier.dk/api"
val gson = Gson()
val TEMPAPIKEY = "AIzaSyAF5p8_MfW9EIM7WEey9EGxBt0fKcmqOOk" //todo make photo url on backend (getPhotoUrl() method, line 169)

class TraverseApiWrapper private constructor() {
    val TAG: String = javaClass.simpleName

    var lastKnownLocation: android.location.Location? = null
    var places: List<Place>? = null
    var user: User? = null


    init {
        val sc = SSLContext.getInstance("SSL")
        sc.init(null, trustAllCerts, java.security.SecureRandom())
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)
    }

    private object Holder {
        val INSTANCE = TraverseApiWrapper()
    }

    companion object {
        val instance: TraverseApiWrapper by lazy { Holder.INSTANCE }
    }

    /**
     * for login and signup where you have the auth0 userprofile, so it can be created on traverse
     */
    fun traverseUser(userProfile: UserProfile, token: String, callback: ITraverseCallback): Unit {
        val result = doHttpRequest("$baseUrl/users/", userProfile, "POST", token) ?: return callback.onError(1) // dunno
        val user: User? = gson.fromJson<User>(result)
        //validate?
        if (user != null) {
            this.user = user
            if (user.country == null) {
                // if country is not informed yet
                callback.onSuccess(2)
            } else {
                // if everything is fine
                callback.onSuccess(1)
            }
        } else {
            callback.onError(2) // dunno
        }
    }

    /**
     * when reopening the app and the user is null
     */
    fun traverseUser(token: String, callback: ITraverseCallback): Unit {
        val result = doHttpRequest("$baseUrl/users/", "GET", token) ?: return callback.onError(1) // dunno
        val user: User? = gson.fromJson<User>(result)
        //validate?
        if (user != null) {
            this.user = user
            Log.d(TAG, "users country = " + user.country)
            if (user.country == null) {
                // if country is not informed yet
                callback.onSuccess(2)
            } else {
                // if everything is fine
                callback.onSuccess(1)
            }
        } else {
            callback.onError(2) // dunno
        }
    }

    fun updateUser(token: String, user: User, callback: ITraverseCallback): Unit {
        val result = doHttpRequest("$baseUrl/users/", user, "PUT", token) ?: return callback.onError(1) // dunno
        val user: User? = gson.fromJson<User>(result)
        //validate?
        if (user != null) {
            this.user = user
            if (user.country == null) {
                // if country is not informed yet
                callback.onSuccess(2)
            } else {
                // if everything is fine
                callback.onSuccess(1)
            }
        } else {
            callback.onError(2) // dunno
        }
    }

    fun fetchPlaces(lat: Double, lng: Double, token: String): Boolean {
        val result = doHttpRequest("$baseUrl/places/nearby?lat=$lat&lng=$lng", null, token)
        Log.d(TAG, "" + result)
        if (result != null) {
            val placesResults = gson.fromJson<List<Place>>(result)
            this.places = placesResults
            return true
        }
        return false
    }

    fun checkin(payload: CheckinRequest, token: String): Boolean { // todo different response
        val result = doHttpRequest("$baseUrl/achievements/checkin", payload, "POST", token)
        if (result != null) {
            val parsedResult = gson.fromJson<User>(result)
            this.user = parsedResult
            Log.d(TAG, "after check in points: " + user!!.achievement_points)
            return true
        }
        return false
    }

    private fun doHttpRequest(url: String, payload: Any, rm: String?, token: String): String? {
        val connection: HttpsURLConnection = URL(url).openConnection() as HttpsURLConnection
        try {
            connection.requestMethod = rm ?: "GET"
            connection.setRequestProperty("Authorization", "Bearer " + token)
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json");
            connection.outputStream.write(gson.toJson(payload).toByteArray())
            val responseCode = connection.responseCode
            Log.d(TAG, responseCode.toString())
            return connection.inputStream.bufferedReader().readText()
        } catch (e: IOException) {
            Log.d(TAG, "Error - Http status code: " + connection.responseCode)
            Log.d(TAG, e.message)
            return null
        } finally {
            connection.disconnect()
        }
    }

    private fun doHttpRequest(url: String, rm: String?, token: String): String? {
        val connection: HttpsURLConnection = URL(url).openConnection() as HttpsURLConnection
        try {
            connection.requestMethod = rm ?: "GET"
            connection.setRequestProperty("Authorization", "Bearer " + token)
            return connection.inputStream.bufferedReader().readText()
        } catch (e: IOException) {
            Log.d(TAG, "Error - Http status code: " + connection.responseCode)
            Log.d(TAG, e.message)
            return null
        } finally {
            connection.disconnect()
        }
    }

    fun getPhotoUrl(ref: String): String {
        return String.format(Locale.ENGLISH,
                "https://maps.googleapis.com/maps/api/place/photo?maxheight=200&photoreference=%s&key=%s",
                ref,
                TEMPAPIKEY);
    }

    fun getPlacesSize(): Int {
        if (places == null) {
            return 0
        }
        return places!!.size
    }
}

internal var trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
    override fun getAcceptedIssuers(): Array<X509Certificate?> {
        return arrayOfNulls(0)
    }

    override fun checkClientTrusted(
            certs: Array<java.security.cert.X509Certificate>, authType: String) {
    }

    override fun checkServerTrusted(
            certs: Array<java.security.cert.X509Certificate>, authType: String) {
    }
})

class CheckinRequest(val id: String,
                     val title: String,
                     val lat: Double,
                     val lng: Double)

//class CheckinResponse(val user: User,
//                      val additionalInfo: String)

class PlacesResult(val html_attributions: List<String>,
                   val results: List<Place>,
                   val status: String,
                   val next_page_token: String)

class Place(val geometry: Geometry,
            val name: String,
            val visited: Boolean,
            val photos: List<Photo>?,
            val place_id: String,
            val types: List<String>,
            val vicinity: String)

class Geometry(val location: Location)

class Location(val lat: Double,
               val lng: Double)

class Photo(val height: Int,
            val width: Int,
            val html_attributions: List<String>,
            val photo_reference: String)

class User(val _id: String,
           val user_id: String,
           val username: String,
           val email: String,
           var country: String?,
           var achievements: List<Achievement>,
           var achievement_points: Int,
           val created_at: Date,
           val updated_at: Date)

class Achievement(val _id: String,
                  val lat: Float,
                  val lng: Float,
                  val title: String,
                  val id: String,
                  val achievement_type: String,
                  val created_at: Date)
