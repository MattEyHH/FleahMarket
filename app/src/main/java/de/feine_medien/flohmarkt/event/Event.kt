package de.feine_medien.flohmarkt.event

import de.feine_medien.flohmarkt.model.Market

data class OnLoadAllMarketsSuccessfulEvent(val markets: List<Market>)

data class OnMarketClickedEvent(val market: Market)