package com.app.gong4.DTO

data class ResponseStudygroupinfoBody(
    val location: Any,
    val msg: Any,
    val data: StduyGroupDetailItem
)

data class StduyGroupDetailItem(
    var createdAt : Long,
    var expiredAt : Long,
    var name : String,
    var isCam: Boolean,
    var groupUID : Int,
    var code : String,
    var minStudyHour : String,
    var categories : List<StudyCategory>
)