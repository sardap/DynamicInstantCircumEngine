package club.psarda.dynamicinstantcircumengine.database.DataHolders

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.Exclude

/**
 * Created by pfsar on 25/09/2017.
 */
class SettingsData() : Parcelable {

    companion object {
        val DEFUALT_TARGET = "4+"

        val SHAKE_THRESHOLD_MIN: Int = 9
        val SHAKE_THRESHOLD_MAX: Int = 18

        @JvmField
        val CREATOR: Parcelable.Creator<SettingsData> = object : Parcelable.Creator<SettingsData> {
            override fun createFromParcel(source: Parcel): SettingsData = SettingsData(source)
            override fun newArray(size: Int): Array<SettingsData?> = arrayOfNulls(size)
        }
    }

    var DefualtSides: Long = 6
        get() = field
        set(value) {
            if (value > 0)
                field = value
        }

    var DefualtReroll: Long = 0
        get() = field
        set(value) {
            // Use of The amazing range operator
            if (value in 0..DefualtSides) {
                field = value
            }
        }

    var DefualtNumberOfDice: Long = 150
        get() = field
        set(value) {
            if (value > 0) {
                field = value
            }
        }

    var Targets: ArrayList<TargetData> = ArrayList()
        get() = field
        set(value) {
            field = value
        }

    @Exclude
    var TargetsAsStrings: ArrayList<String> = ArrayList()
        get() {
            val result = ArrayList<String>()

            for (target in Targets) {
                result.add(target.GetString())
            }

            return result
        }


    // Default value is the middle
    var ShakeThreseHold: Int = (SHAKE_THRESHOLD_MIN + SHAKE_THRESHOLD_MAX) / 2
        get() = field
        set(value){
            if(value >= SHAKE_THRESHOLD_MIN && value <= SHAKE_THRESHOLD_MAX){
                field = value;
            }
        }

    init {
        Targets.add(TargetData("4+"))
        Targets.add(TargetData("3+"))
    }

    constructor(source: Parcel) : this(
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {}


}