package cooleye.eot.kotlin.data

import java.io.Serializable

/**
 * Created by cool on 16-11-25.
 */
data class Riding(
        var passenger: Passenger,
        var peopleCount: Int,
        var ridingTime: Long,
        var startCity: String?,
        var endCity: String?,
        var startAddress: String,
        var endAddress: String,
        var startLongitude: Double? = null,
        var startLatitude: Double?,
        var mark: String?) : Serializable {

    fun composeAddress(): String {
        return "$startAddress－$endAddress"
    }

    fun peopleCount(): String {
        return "$peopleCount 人"
    }
}