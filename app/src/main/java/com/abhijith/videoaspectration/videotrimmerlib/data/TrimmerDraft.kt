package com.abhijith.videoaspectration.videotrimmerlib.data

import android.os.Parcel
import android.os.Parcelable

data class TrimmerDraft(
    val path: String,
    val rawStartMillis: Long,
    val rawEndMillis: Long,
    val offsetMillis: Long,
    val framePosition: Int = 0,
    val frameOffset: Int = 0,
    val createdTime: Long = System.currentTimeMillis()
): Parcelable, Comparable<TrimmerDraft> {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readLong()
    ) {
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TrimmerDraft

        return path == other.path
    }

    override fun hashCode(): Int {
        return path.hashCode()
    }

    override fun compareTo(other: TrimmerDraft): Int {
        return when {
            this === other -> 0
            this.createdTime == other.createdTime -> 0
            this.createdTime < other.createdTime -> 1
            else -> -1
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(path)
        parcel.writeLong(rawStartMillis)
        parcel.writeLong(rawEndMillis)
        parcel.writeLong(offsetMillis)
        parcel.writeInt(framePosition)
        parcel.writeInt(frameOffset)
        parcel.writeLong(createdTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TrimmerDraft> {
        override fun createFromParcel(parcel: Parcel): TrimmerDraft {
            return TrimmerDraft(parcel)
        }

        override fun newArray(size: Int): Array<TrimmerDraft?> {
            return arrayOfNulls(size)
        }
    }
}