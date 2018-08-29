package de.feine_medien.flohmarkt.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Addr(val id: Int?, val plz: String?, val name: String?, val street: String?, val location: String?,
                val lat: Double?, val lng: Double?) : Parcelable