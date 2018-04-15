package club.psarda.dynamicinstantcircumengine.database.FireBase

import club.psarda.dynamicinstantcircumengine.database.DataHolders.GlobalStatsData
import club.psarda.dynamicinstantcircumengine.database.DataHolders.SettingsData
import club.psarda.dynamicinstantcircumengine.database.DataHolders.StatsData
import club.psarda.dynamicinstantcircumengine.database.DataHolders.StorageClasses.StatsDataStorage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport

/**
 * Created by pfsar on 25/09/2017.
 */
class DataStorage private constructor() {

    private object Holder {
        val INSTANCE = DataStorage()
    }

    companion object {
        val INSTANCE: DataStorage by lazy {
            Holder.INSTANCE
        }

        val SETTINGS_LOADED: String = "SETTINGS LOADED"
        val GLOABAL_STATS_LOADED : String = "GLOBAL_TATS_LOADED"
    }

     var SettingsLoaded: Boolean = false
        private set

    private var _settingsData : SettingsData? = null
    private var _curSettingsListner: ValueEventListener? = null
    private var _curSettingsQuery: Query? = null
    protected var _propertyChangeSupport: PropertyChangeSupport = PropertyChangeSupport(this)

    var LoadedGlobalStatsData : GlobalStatsData? = null
        private set


    val Settings: SettingsData?
        get(){
            return _settingsData
        }

    init {
        FirebaseAuth.getInstance().addAuthStateListener {

            if(FirebaseAuth.getInstance().currentUser != null){
                if(_curSettingsListner != null && _curSettingsQuery != null){
                    _curSettingsQuery!!.removeEventListener(_curSettingsListner)
                }

                loadSettingsData()
            }
        }
    }

    fun addStatsData(toAdd : StatsData?){
        if(toAdd != null){
            FireBaseHelper.instance.writeNewStatsData(toAdd, FirebaseAuth.getInstance().currentUser!!.uid)
        }
    }

    fun loadGlobalStats(sides : Int){

        val loadGlobalStatsListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                var sum = 0.0
                var totalRolls : Long = 0
                var length : Long = 0

                for(child in dataSnapshot.children){

                    for(statsChild in child.children){
                        val statsData = statsChild.getValue(StatsDataStorage::class.java)

                        if(statsData != null && statsData.Sides == sides){
                            sum += statsData.AverageRoll
                            totalRolls += statsData.NumberOfRolls
                            length++
                        }
                    }

                }

                LoadedGlobalStatsData = GlobalStatsData(sum / length, totalRolls, length)
                _propertyChangeSupport.firePropertyChange(GLOABAL_STATS_LOADED, false, true);
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }

        val allStatsQuery = FireBaseHelper.instance.StatsDbRef
        allStatsQuery.addListenerForSingleValueEvent(loadGlobalStatsListener)
    }

    private val loadSettings = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {

            _settingsData = dataSnapshot.getValue(SettingsData::class.java)

            if(_settingsData == null){
                _settingsData = SettingsData()

                if(FirebaseAuth.getInstance().currentUser != null && !FirebaseAuth.getInstance().currentUser!!.isAnonymous){
                    FireBaseHelper.instance.writeSettings(_settingsData as SettingsData, FirebaseAuth.getInstance().currentUser!!.uid)
                }
            }

            SettingsLoaded = true
            _propertyChangeSupport.firePropertyChange(SETTINGS_LOADED, false, true);
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    }

    fun loadSettingsData(){
        val userID = FirebaseAuth.getInstance().currentUser!!.uid

        _curSettingsQuery = FireBaseHelper.instance.userSettingsQuery(userID)

        _curSettingsQuery!!.ref.addValueEventListener(loadSettings)
    }

    fun addPropertyChangeListener(listener: PropertyChangeListener) {
        _propertyChangeSupport.addPropertyChangeListener(listener)
    }

    fun removePropertyChangeListener(listener: PropertyChangeListener){
        _propertyChangeSupport.removePropertyChangeListener(listener)
    }
}