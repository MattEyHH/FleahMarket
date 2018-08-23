package de.feine_medien.flohmarkt.event

import de.feine_medien.flohmarkt.model.Market

data class OnLoadAllMarketsSuccessfulEvent(val markets: List<Market>)

data class OnMarketClickedEvent(val market: Market)

data class OnOrganizerButtonClickedEvent(val market: Market)

data class OnGeoLocationFoundEvent(val lat: Double?, val long: Double?)

class OnNoGeoPermissionGivenEvent()

class OnNoResultsFoundEvent()

class OnNoZipOrCitySelectedEvent()

class OnLocationProviderDisabledEvent()