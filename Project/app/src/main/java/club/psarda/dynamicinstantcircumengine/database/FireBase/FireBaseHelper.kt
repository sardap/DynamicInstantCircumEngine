package club.psarda.dynamicinstantcircumengine.database.FireBase

import club.psarda.dynamicinstantcircumengine.database.DataHolders.SettingsData
import club.psarda.dynamicinstantcircumengine.database.DataHolders.StatsData
import club.psarda.dynamicinstantcircumengine.database.DataHolders.StorageClasses.StatsDataStorage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


/**
 * Created by pfsar on 24/09/2017.
 */
class FireBaseHelper private constructor() {

    private object Holder {
        var INSTANCE = FireBaseHelper()
    }

    companion object {
        val instance: FireBaseHelper by lazy {
            Holder.INSTANCE
        }

        val SETTING_NAME = "Settings"
        val STATS_NAME = "StatsData"
    }


    val DatabaseRoot : DatabaseReference = FirebaseDatabase.getInstance().reference
    val StatsDbRef : DatabaseReference = DatabaseRoot.child(STATS_NAME)
    val SettingsDbRef : DatabaseReference = DatabaseRoot.child(SETTING_NAME)

    init {
        DatabaseRoot.keepSynced(true)
    }

    fun userSettingsQuery(uid : String) : Query{
        return SettingsDbRef.child(uid)
    }


    fun allStatsDataForUser(uid : String) : Query{
        return  DatabaseRoot.child(STATS_NAME).child(uid)
    }

    fun writeNewStatsData(statsData : StatsData?, uid : String){
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if(userId != null && statsData != null){
            val statsDataStorage = StatsDataStorage(statsData)

            DatabaseRoot.child(STATS_NAME).child(uid).push().setValue(statsDataStorage)
        }
    }

    fun writeSettings(settingsData: SettingsData, uid: String){
      SettingsDbRef.child(uid).setValue(settingsData)
    }

}