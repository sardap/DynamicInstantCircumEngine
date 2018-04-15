package club.psarda.dynamicinstantcircumengine.database.DataHolders

/**
 * Created by pfsar on 6/10/2017.
 */

class GlobalStatsData(){

    var AverageRoll : Double = 0.0
        private set

    var TotalRolls : Long = 0
        private set

    var TotalNumberOfStats : Long = 0
        private set

    constructor(averageRoll : Double, totalRolls : Long, totalNumberOfStats: Long) : this(){
        AverageRoll = averageRoll
        TotalRolls = totalRolls
        TotalNumberOfStats = totalNumberOfStats
    }
}