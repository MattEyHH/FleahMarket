package de.feine_medien.flohmarkt.model

data class Market(val id: Int?, val category: Category?, val province: Province?, val event: Event?,
                  val organizer: Organizer?, val addr: Addr?)