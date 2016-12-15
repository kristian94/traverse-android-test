package com.cborum.traverse

import com.auth0.android.result.UserProfile
import com.cborum.traverse.backend.*
import com.cborum.traverse.backend.User
import com.github.salomonbrys.kotson.jsonObject
import com.google.gson.JsonObject

import org.junit.Test
import java.util.*

/**
 * Created by ChristopherBorum on 25/10/16.
 */

class TraverseBackendTest {
    val taw = TraverseApiWrapper.instance

    /*@Test
    fun testGetUser() {
        val user = taw.getUser("test1")
        if (user == null) {
            assert(false)
            return
        }
        assert(user.username == "test1")
        assert(user.user_id == "testuserid1")
    }*/

//    @Test
//    fun testGetPlaces() {
//        val places = taw.fetchPlaces(55.702143, 12.577659)
//        if (places == null) {
//            assert(false)
//            return
//        }
//        assert(places.results.size > 10)
//    }

    /*@Test
    fun testCreateUser() {
        val uuid = UUID.randomUUID().toString()
        val obj: JsonObject = jsonObject(
                "user_id" to "user_id" + uuid,
                "username" to "username" + uuid,
                "email" to "email" + uuid,
                "country" to "country" + uuid,
                "state" to "state" + uuid
        )
        println(obj.toString())
        val newUser = taw.postUser(obj)
        if (newUser == null) {
            assert(false)
            return
        }
        println(newUser.user_id)
        println(newUser._id)
    }*/

    @Test
    fun testIfUserNotFoundCreateNew() {
        // todo
    }

    /*@Test
    fun testTraverseCallbackSuccess() {
        user = null
        taw.traverseUser(null, "test1", mCallback)
        Thread.sleep(1000)
        assert(user != null)
    }*/

    /*@Test
    fun testTraverseCallbackError() {
        error = null
        taw.traverseUser("", mCallback)
        Thread.sleep(1000)
        assert(error != null)
        assert(error == 1)
    }*/
}

var success: Int? = null
var error: Int? = null

private val mCallback = object : TraverseCallback() {
    override fun onSuccess(callbackSuccess: Int) {
        success = callbackSuccess
    }

    override fun onError(callbackError: Int) {
        error = callbackError
    }
}