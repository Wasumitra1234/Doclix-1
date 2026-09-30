package com.doclix.autofill.data
import android.content.Context

class ProfileRepository(context:Context){
 private val p=context.getSharedPreferences("doclix_profile",Context.MODE_PRIVATE)

 fun save(x:UserProfile){p.edit().apply{
  putString("full_name",x.fullName);putString("first_name",x.firstName);putString("middle_name",x.middleName);putString("last_name",x.lastName)
  putString("email",x.email);putString("phone",x.phone);putString("dob",x.dob);putString("gender",x.gender)
  putString("address",x.address);putString("city",x.city);putString("city1",x.city1);putString("city2",x.city2);putString("city3",x.city3)
  putString("state",x.state);putString("pincode",x.pincode);putString("nationality",x.nationality);putString("category",x.category)
  putString("caste_authority",x.casteAuthority);putString("caste_serial",x.casteSerial)
  putString("tenth_board",x.tenthBoard);putString("tenth_school",x.tenthSchool);putString("tenth_maths",x.tenthMaths);putString("tenth_total",x.tenthTotal);putString("tenth_max",x.tenthMax);putString("tenth_year",x.tenthYear);putString("tenth_percent",x.tenthPercent)
  putString("eleventh_board",x.eleventhBoard);putString("eleventh_school",x.eleventhSchool);putString("eleventh_maths",x.eleventhMaths);putString("eleventh_total",x.eleventhTotal);putString("eleventh_max",x.eleventhMax);putString("eleventh_year",x.eleventhYear);putString("eleventh_percent",x.eleventhPercent)
  putString("twelfth_board",x.twelfthBoard);putString("twelfth_school",x.twelfthSchool);putString("twelfth_maths",x.twelfthMaths);putString("twelfth_total",x.twelfthTotal);putString("twelfth_max",x.twelfthMax);putString("twelfth_year",x.twelfthYear);putString("twelfth_percent",x.twelfthPercent)
  apply()
 }}
 fun load()=UserProfile(
  p.getString("full_name","")?:"",p.getString("first_name","")?:"",p.getString("middle_name","")?:"",p.getString("last_name","")?:"",
  p.getString("email","")?:"",p.getString("phone","")?:"",p.getString("dob","")?:"",p.getString("gender","")?:"",
  p.getString("address","")?:"",p.getString("city","")?:"",p.getString("city1","")?:"",p.getString("city2","")?:"",p.getString("city3","")?:"",
  p.getString("state","")?:"",p.getString("pincode","")?:"",p.getString("nationality","Indian")?:"Indian",p.getString("category","")?:"",
  p.getString("caste_authority","")?:"",p.getString("caste_serial","")?:"",
  p.getString("tenth_board","")?:"",p.getString("tenth_school","")?:"",p.getString("tenth_maths","")?:"",p.getString("tenth_total","")?:"",
  p.getString("tenth_max","")?:"",p.getString("tenth_year","")?:"",p.getString("tenth_percent","")?:"",
  p.getString("eleventh_board","")?:"",p.getString("eleventh_school","")?:"",p.getString("eleventh_maths","")?:"",p.getString("eleventh_total","")?:"",
  p.getString("eleventh_max","")?:"",p.getString("eleventh_year","")?:"",p.getString("eleventh_percent","")?:"",
  p.getString("twelfth_board","")?:"",p.getString("twelfth_school","")?:"",p.getString("twelfth_maths","")?:"",p.getString("twelfth_total","")?:"",
  p.getString("twelfth_max","")?:"",p.getString("twelfth_year","")?:"",p.getString("twelfth_percent","")?:""
 )
}