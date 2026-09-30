package com.doclix.autofill.ui
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.autofill.AutofillManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.doclix.autofill.R
import com.doclix.autofill.data.*

class MainActivity:AppCompatActivity(){
 private lateinit var repo:ProfileRepository;private lateinit var e:Array<EditText>;private lateinit var status:TextView
 override fun onCreate(b:Bundle?){super.onCreate(b);setContentView(R.layout.activity_main);repo=ProfileRepository(this)
  e=arrayOf(R.id.etFullName,R.id.etFirstName,R.id.etMiddleName,R.id.etLastName,R.id.etEmail,R.id.etPhone,R.id.etDob,R.id.etGender,R.id.etAddress,R.id.etCity,R.id.etState,R.id.etPincode,R.id.etNationality,R.id.etCategory,R.id.etTenthBoard,R.id.etTenthPercent,R.id.etEleventhBoard,R.id.etEleventhPercent,R.id.etTwelfthBoard,R.id.etTwelfthPercent).map{findViewById(it)}
  status=findViewById(R.id.tvStatus);load();findViewById<Button>(R.id.btnSave).setOnClickListener{save()};findViewById<Button>(R.id.btnEnableAutofill).setOnClickListener{enable()}}
 override fun onResume(){super.onResume();status.text=if(getSystemService(AutofillManager::class.java)?.isAutofillSupported==true)"Autofill supported — select Doclix in Settings." else "Autofill unavailable on this device."}
 private fun load(){val p=repo.load();arrayOf(p.fullName,p.firstName,p.middleName,p.lastName,p.email,p.phone,p.dob,p.gender,p.address,p.city,p.state,p.pincode,p.nationality,p.category,p.tenthBoard,p.tenthPercent,p.eleventhBoard,p.eleventhPercent,p.twelfthBoard,p.twelfthPercent).forEachIndexed{i,x->e[i].setText(x)}}
 private fun save(){val v=e.map{it.text.toString().trim()};repo.save(UserProfile(v[0],v[1],v[2],v[3],v[4],v[5],v[6],v[7],v[8],v[9],v[10],v[11],v[12].ifBlank{"Indian"},v[13],v[14],v[15],v[16],v[17],v[18],v[19]));Toast.makeText(this,R.string.profile_saved,Toast.LENGTH_SHORT).show()}
 private fun enable(){try{startActivity(Intent(Settings.ACTION_REQUEST_SET_AUTOFILL_SERVICE).apply{data=Uri.parse("package:"+packageName)})}catch(e:Exception){startActivity(Intent(Settings.ACTION_SETTINGS))}}
}