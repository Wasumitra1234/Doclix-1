package com.doclix.autofill.data
import android.content.Context
class ProfileRepository(context:Context){
 private val p=context.getSharedPreferences("doclix_profile",Context.MODE_PRIVATE)
 fun save(x:UserProfile){p.edit().apply{
 putString("full_name",x.fullName);putString("first_name",x.firstName);putString("middle_name",x.middleName);putString("last_name",x.lastName)
 putString("email",x.email);putString("phone",x.phone);putString("dob",x.dob);putString("gender",x.gender)
 putString("address",x.address);putString("city",x.city);putString("state",x.state);putString("pincode",x.pincode)
 putString("nationality",x.nationality);putString("category",x.category);putString("tenth_board",x.tenthBoard);putString("tenth_percent",x.tenthPercent)
 putString("eleventh_board",x.eleventhBoard);putString("eleventh_percent",x.eleventhPercent);putString("twelfth_board",x.twelfthBoard);putString("twelfth_percent",x.twelfthPercent);apply()}}
 fun load()=UserProfile(
  p.getString("full_name","")?:"",p.getString("first_name","")?:"",p.getString("middle_name","")?:"",p.getString("last_name","")?:"",
  p.getString("email","")?:"",p.getString("phone","")?:"",p.getString("dob","")?:"",p.getString("gender","")?:"",
  p.getString("address","")?:"",p.getString("city","")?:"",p.getString("state","")?:"",p.getString("pincode","")?:"",
  p.getString("nationality","Indian")?:"Indian",p.getString("category","")?:"",p.getString("tenth_board","")?:"",p.getString("tenth_percent","")?:"",
  p.getString("eleventh_board","")?:"",p.getString("eleventh_percent","")?:"",p.getString("twelfth_board","")?:"",p.getString("twelfth_percent","")?:"")
}