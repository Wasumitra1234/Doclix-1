package com.doclix.autofill.service
import android.app.assist.AssistStructure
import android.os.CancellationSignal
import android.service.autofill.*
import android.view.autofill.AutofillId
import android.view.autofill.AutofillValue
import android.widget.RemoteViews
import com.doclix.autofill.data.*

class DoclixAutofillService:AutofillService(){
 private lateinit var repo:ProfileRepository
 override fun onCreate(){super.onCreate();repo=ProfileRepository(applicationContext)}
 override fun onFillRequest(r:FillRequest,c:CancellationSignal,cb:FillCallback){
  val p=repo.load();if(p.isEmpty()){cb.onSuccess(null);return}
  val s=r.fillContexts.lastOrNull()?.structure?:run{cb.onSuccess(null);return}
  val fs=mutableListOf<F>();parse(s,fs);if(fs.isEmpty()){cb.onSuccess(null);return}
  val b=Dataset.Builder();var n=0
  for(f in fs){val value=match(f,p)?:continue;val rv=RemoteViews(packageName,android.R.layout.simple_list_item_1);rv.setTextViewText(android.R.id.text1,"Doclix: "+value);b.setValue(f.id,AutofillValue.forText(value),rv);n++}
  if(n==0){cb.onSuccess(null);return};cb.onSuccess(FillResponse.Builder().addDataset(b.build()).build())
 }
 override fun onSaveRequest(r:SaveRequest,cb:SaveCallback){cb.onSuccess()}
 private data class F(val id:AutofillId,val hints:List<String>,val key:String)
 private fun parse(s:AssistStructure,o:MutableList<F>){for(i in 0 until s.windowNodeCount)s.getWindowNodeAt(i).rootNode?.let{walk(it,o)}}
 private fun walk(v:AssistStructure.ViewNode,o:MutableList<F>){
  val id=v.autofillId;if(id!=null&&v.autofillType==android.view.View.AUTOFILL_TYPE_TEXT){
   val name=v.htmlInfo?.attributes.orEmpty().firstOrNull{it.first=="name"}?.second?:""
   o+=F(id,v.autofillHints?.toList().orEmpty(),(v.idEntry+" "+name+" "+(v.hint?:" ")+" "+(v.text?:"")).lowercase().replace(Regex("[^a-z0-9]")," "))
  };for(i in 0 until v.childCount)walk(v.getChildAt(i),o)
 }
 private fun match(f:F,p:UserProfile):String?{
  for(h in f.hints){val x=when(h){
   "name","personName"->p.fullName;"personGivenName","given-name"->p.firstName;"personFamilyName","family-name"->p.lastName
   "personMiddleName","additional-name"->p.middleName;"emailAddress","email"->p.email;"phone","phoneNumber","phoneNational"->p.phone
   "birthDate","birthDateFull"->p.dob;"postalAddress","streetAddress"->p.address;"addressLocality","postalAddressLocality"->p.city
   "addressRegion","postalAddressRegion"->p.state;"postalCode","postalAddressPostalCode"->p.pincode;"gender"->p.gender;else->""};if(x.isNotBlank())return x}
  val k=f.key
  return when{
   k.contains("full name")||k.contains("fullname")||(k.contains("name")&&!k.contains("first")&&!k.contains("last")&&!k.contains("middle")&&!k.contains("user")&&!k.contains("file"))->p.fullName
   k.contains("first name")||k.contains("firstname")||k.contains("given name")->p.firstName
   k.contains("middle name")||k.contains("middlename")->p.middleName
   k.contains("last name")||k.contains("lastname")||k.contains("surname")||k.contains("family name")->p.lastName
   k.contains("email")||k.contains("e mail")->p.email;k.contains("phone")||k.contains("mobile")||k.contains("contact")->p.phone
   k.contains("dob")||k.contains("date of birth")||k.contains("birth date")||k.contains("birthday")->p.dob
   k.contains("gender")||k.contains("sex")->p.gender;k.contains("address")&&!k.contains("email")->p.address
   k.contains("city")||k.contains("district")->p.city;k.contains("state")||k.contains("province")->p.state
   k.contains("pincode")||k.contains("pin code")||k.contains("zip")||k.contains("postal")->p.pincode
   k.contains("nationality")||k.contains("citizen")->p.nationality;k.contains("category")||k.contains("caste")->p.category
   k.contains("10th")&&k.contains("board")->p.tenthBoard;k.contains("10th")&&(k.contains("percent")||k.contains("cgpa"))->p.tenthPercent
   k.contains("11th")&&k.contains("board")->p.eleventhBoard;k.contains("11th")&&(k.contains("percent")||k.contains("cgpa"))->p.eleventhPercent
   k.contains("12th")&&k.contains("board")->p.twelfthBoard;k.contains("12th")&&(k.contains("percent")||k.contains("cgpa"))->p.twelfthPercent
   else->null
  }.takeIf{it.isNotBlank()}
 }
}