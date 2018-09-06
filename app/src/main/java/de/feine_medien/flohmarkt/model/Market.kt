package de.feine_medien.flohmarkt.model

import android.os.Parcelable
import de.feine_medien.flohmarkt.bookmarks.BookmarkNotice
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Market(val id: Int?, val category: Category?, val province: Province?, val event: Event?,
                  val organizer: Organizer?, val addr: Addr?, var bookmarkNotice: Int?) : Parcelable