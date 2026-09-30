package com.doclix.autofill.data
data class UserProfile(
 val fullName:String="", val firstName:String="", val middleName:String="", val lastName:String="",
 val email:String="", val phone:String="", val dob:String="", val gender:String="",
 val address:String="", val city:String="", val state:String="", val pincode:String="",
 val nationality:String="Indian", val category:String="",
 val tenthBoard:String="", val tenthPercent:String="", val eleventhBoard:String="", val eleventhPercent:String="",
 val twelfthBoard:String="", val twelfthPercent:String=""
){ fun isEmpty()=fullName.isBlank()&&firstName.isBlank()&&email.isBlank()&&phone.isBlank() }