package com.cborum.traverse


import android.support.v4.app.Fragment
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.cborum.traverse.backend.TraverseApiWrapper
import com.cborum.traverse.backend.User
import com.cborum.traverse.profile_views.Achievements
import com.cborum.traverse.profile_views.Details
import com.cborum.traverse.profile_views.ProfileViewManager
import com.cborum.traverse.utils.CredentialsManager
import java.util.*

/**
 * Created by Kristian Nielsen on 15-12-2016.
 */

class ProfileTab: Fragment() {

    private val TAG: String = "ProfileTab"
    lateinit private var profileImage: ImageView
    lateinit private var username: TextView
    lateinit private var travelsTab: RelativeLayout
    lateinit private var achievementsTab: RelativeLayout
    lateinit private var detailsTab: RelativeLayout
    private val tabs: List<RelativeLayout> = ArrayList()
    lateinit private var wrapper: TraverseApiWrapper
    lateinit private var user: User
    lateinit private var viewContainer: ViewGroup

//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View {
//        return inflater.inflate(R.layout.profile_tab, container, false);
//    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if(inflater != null){
            return inflater.inflate(R.layout.profile_tab, container, false)
        }
        return null
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initKeys()
        setValues()

        ProfileViewManager.activity = activity
        ProfileViewManager.context = context
        ProfileViewManager.fragment = this
        ProfileViewManager.inflater = LayoutInflater.from(context)
        ProfileViewManager.container = viewContainer

        ProfileViewManager.addView(Achievements.getInstance(R.layout.profile_achievements, achievementsTab, R.id.achievementsHeader));
        ProfileViewManager.addView(Details.getInstance(R.layout.profile_details, detailsTab, R.id.detailsHeader));

        ProfileViewManager.changeView(1);
    }

    private fun initKeys(){
        wrapper = TraverseApiWrapper.instance
        var tempUser: User? = wrapper.user
        if(tempUser != null){
            user = tempUser
        }
        profileImage = activity.findViewById(R.id.profileImage) as ImageView
        username = activity.findViewById(R.id.username) as TextView
//        travelsTab = activity.findViewById(R.id.travelsTab) as TextView
        achievementsTab = activity.findViewById(R.id.achievementsTab) as RelativeLayout
        detailsTab = activity.findViewById(R.id.detailsTab) as RelativeLayout
        viewContainer = activity.findViewById(R.id.viewContainer) as ViewGroup


    }

    private fun setValues() {
        if(user.username != null){
            username.setText(user.username)
        }
    }

    override fun onDestroy() {
        ProfileViewManager.inflater = null
        super.onDestroy()
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun logout() {
        CredentialsManager.deleteCredentials(context)
        startActivity(Intent(context, LoginActivity::class.java))
        activity.finish()
    }

}


