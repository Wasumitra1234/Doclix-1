package com.doclix.autofill.data

data class UserProfile(
    val fullName:String="", val firstName:String="", val middleName:String="", val lastName:String="",
    val email:String="", val phone:String="", val dob:String="", val gender:String="",
    val address:String="", val city:String="", val city1:String="", val city2:String="", val city3:String="",
    val state:String="", val pincode:String="", val nationality:String="Indian", val category:String="",
    val casteAuthority:String="", val casteSerial:String="",
    val tenthBoard:String="", val tenthSchool:String="", val tenthMaths:String="", val tenthTotal:String="",
    val tenthMax:String="", val tenthYear:String="", val tenthPercent:String="",
    val eleventhBoard:String="", val eleventhSchool:String="", val eleventhMaths:String="", val eleventhTotal:String="",
    val eleventhMax:String="", val eleventhYear:String="", val eleventhPercent:String="",
    val twelfthBoard:String="", val twelfthSchool:String="", val twelfthMaths:String="", val twelfthTotal:String="",
    val twelfthMax:String="", val twelfthYear:String="", val twelfthPercent:String=""
){
    fun isEmpty()=fullName.isBlank()&&firstName.isBlank()&&email.isBlank()&&phone.isBlank()
}