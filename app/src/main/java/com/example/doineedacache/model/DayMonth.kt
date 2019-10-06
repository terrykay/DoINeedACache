package com.example.doineedacache.model

import android.os.Parcel
import android.os.Parcelable

data class DayMonth(val day: Int, val month: Int) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel?, flags: Int) {
        parcel?.writeInt(day)
        parcel?.writeInt(month)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DayMonth> {
        override fun createFromParcel(parcel: Parcel): DayMonth {
            return DayMonth(parcel)
        }

        override fun newArray(size: Int): Array<DayMonth?> {
            return arrayOfNulls(size)
        }
    }


}