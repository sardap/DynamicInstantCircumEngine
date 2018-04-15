package club.psarda.dynamicinstantcircumengine.database.DataHolders.StorageClasses

import club.psarda.dynamicinstantcircumengine.database.DataHolders.StatsData
import club.psarda.dynamicinstantcircumengine.database.DataHolders.TargetData
import com.google.firebase.database.Exclude

/**
 * Created by pfsar on 1/10/2017.
 */
class StatsDataStorage() {

    companion object {
        val NAME_KEY = "name"
        val SIDES_KEY = "sides"
        val AVERAGE_KEY = "averageRoll"
        val CREATEION_DATE_KEY = "creationDate"
        val AMOUNT_KEY = "numberOfRolls"
    }


    var Name : String = ""
    var Sides : Int = 0
    var CreationDate : Long  = 0
    var AverageRoll : Double = 0.0
    var NumberOfRolls : Long = 0
    var Seed : Long = 0
    var Targets = ArrayList<String>()

    /*
        takes in stats data and stores
        what needs to be in the database
    */
    constructor(statsData : StatsData) : this(){
        Name = statsData._name
        Sides = statsData._sides
        CreationDate = statsData._timeCreation
        AverageRoll = statsData._averageRoll
        NumberOfRolls = statsData._numberOfDice
        Seed = statsData._seed

        for(i in statsData._rollData){
            Targets.add(i._target)
        }
    }
}


