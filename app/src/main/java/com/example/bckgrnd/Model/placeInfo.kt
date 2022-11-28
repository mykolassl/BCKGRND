package com.example.bckgrnd.Model

import com.beust.klaxon.*

private var klaxon = Klaxon()

data class placeInfo (
    var xid: String?,
    var name: String?,
    var address: Address?,
    var rate: String?,
    var wikidata: String?,
    var kinds: String?,
    var sources: Sources?,
    var otm: String?,
    var wikipedia: String?,
    var image: String?,
    var preview: Preview?,

    var wikipedia_extracts: WikipediaExtracts?,

    var point: Point?
) {

}

data class Address (
    var city: String,
    var house: String,
    var state: String,
    var suburb: String,
    var country: String,
    var postcode: String,

    @Json(name = "country_code")
    var countryCode: String?,

    @Json(name = "house_number")
    val houseNumber: String?,

    @Json(name = "state_district")
    val stateDistrict: String?
)

data class Point (
    var lon: Double,
    var lat: Double
)

data class Preview (
    var source: String?,
    var height: Long?,
    var width: Long?
)

data class Sources (
    var geometry: String,
    var attributes: List<String>
)

data class WikipediaExtracts (
    var title: String?,
    var text: String?,
    var html: String?
)