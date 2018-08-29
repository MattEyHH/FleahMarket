package de.feine_medien.flohmarkt.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Organizer(val company: String?, val firstname: String?, val lastname: String?, val street: String?,
                     val plz: String?, val city: String?, val phone: String?, val id: Int?) : Parcelable