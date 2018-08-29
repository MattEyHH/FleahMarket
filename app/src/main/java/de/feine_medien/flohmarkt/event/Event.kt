package de.feine_medien.flohmarkt.event

import android.location.Location
import de.feine_medien.flohmarkt.model.Market

data class OnLoadAllMarketsSuccessfulEvent(val markets: List<Market>)

data class OnOrganizerButtonClickedEvent(val market: Market)

data class OnAddMarketToBookmarksEvent(val market: Market)

data class OnGeoLocationFoundEvent(val location: Location)

data class OnFirstProvinceLocatedEvent(val province: String)

data class OnRadiusValueHasChangedEvent(val progress: Int)

data class OnLoadSavedMarketsFromPreferencesEvent(val markets: List<Market>)

class OnNoGeoPermissionGivenEvent

class OnNoResultsFoundEvent

class OnNoZipOrCitySelectedEvent
