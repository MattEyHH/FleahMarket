package de.feine_medien.flohmarkt.model

data class Event(val date: String?, val time: Long?, val dayofweek: String?, val id: Int?,
                 val title: String?, val text: String?, val fulltext: String?,
                 val lng: Double?, val lat: Double?, val tstart: Long?, val tend: Long?,
                 val opt: Int?, val holiday: Int?, val params: Any?)