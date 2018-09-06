package de.feine_medien.flohmarkt.event

import android.location.Location
import de.feine_medien.flohmarkt.model.Market

data class OnLoadAllMarketsSuccessfulEvent(val markets: List<Market>)

data class OnOrganizerButtonClickedEvent(val market: Market)

data class OnGeoLocationFoundEvent(val location: Location)

data class OnRadiusValueHasChangedEvent(val progress: Int)

data class OnLoadSavedMarketsFromPreferencesEvent(val markets: List<Market>)

data class OnDeleteBookmarkEvent(val id: Int)

data class OnSearchedForMarketsByCityOrZipEvent(val cityOrZip: String)

data class OnSearchByLastSearchedCityEvent(val city: String)

class OnNoGeoPermissionGivenEvent

class OnNoResultsFoundEvent

class OnNoZipOrCitySelectedEvent

class OnAddMarketToBookmarksEvent

class OnAllBookmarksDeletedEvent

class OnBookmarksCountChangedEvent

class OnSearchedForMarketsByCurrentPositionEvent


