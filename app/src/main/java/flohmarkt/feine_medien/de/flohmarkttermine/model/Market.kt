package flohmarkt.feine_medien.de.flohmarkttermine.model

import java.util.*

data class Market(val id: Int?, val marketKey: Int?, val name: String?, val startDate: Date?, val endDate: Date?,
                  val title: String?, val description: String?, val location: String?, val street: String?, val zipCode: String?,
                  val city: String?, val province: String?, val latitude: Double?, val longitude: Double?, val distance: Double?,
                  val annotation: String?, val company: String?, val firstName: String?,
                  val lastName: String?, val companyStreet: String?, val companyZipCode: String?,
                  val companyCity: String?, val companyTelephone: String?)