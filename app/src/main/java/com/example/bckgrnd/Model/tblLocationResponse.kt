package com.example.bckgrnd.Model

class tblLocationResponse(
    var ID: String? = null,
    var Name: String? = null,
    var Description: String? = null,
    var Latitude: Float? = null,
    var Longtitude: Float? = null,
    var Tags: List<Tag>?,
    var Photos: List<PhotoResponse>? = null
) {

}