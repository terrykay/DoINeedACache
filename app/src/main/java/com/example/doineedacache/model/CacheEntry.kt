package uk.themeadow.doineedacache.model

import androidx.annotation.Keep
import java.util.*

@Keep
class CacheEntry(var dateFound: Date?=null, var name: String="") {
    override fun equals(other: Any?): Boolean {
       if (dateFound==null || other==null)
           return false
        if (!(other is CacheEntry))
            return false
        return (dateFound!!.day == other.dateFound!!.day) && (dateFound!!.month == other.dateFound!!.month)
    }

    override fun hashCode(): Int {
        return dateFound.hashCode()
    }
}