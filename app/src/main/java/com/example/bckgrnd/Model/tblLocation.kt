package com.example.bckgrnd.Model

import java.util.*

class tblLocation(
    var name: String? = null,
    var description: String? = null,
    var latitude: Float? = null,
    var longtitude: Float? = null,
    var tags: List<Tag>?,
    var photos: List<Photo>? = null
) {

}