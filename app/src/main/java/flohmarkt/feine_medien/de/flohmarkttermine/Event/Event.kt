package flohmarkt.feine_medien.de.flohmarkttermine.Event

import flohmarkt.feine_medien.de.flohmarkttermine.model.Market

data class OnLoadAllEventsSuccessfulEvent(val events: Any)

data class OnMarketClickedEvent(val market: Market)