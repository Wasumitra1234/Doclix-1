package com.doclix.autofill.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.autofill.AutofillManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.doclix.autofill.R
import com.doclix.autofill.data.ProfileRepository
import com.doclix.autofill.data.UserProfile

class MainActivity : AppCompatActivity() {
    private lateinit var repo: ProfileRepository
    private lateinit var fields: Array<EditText>
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        repo = ProfileRepository(this)

        fields = arrayOf(
            R.id.etFullName, R.id.etFirstName, R.id.etMiddleName, R.id.etLastName,
            R.id.etEmail, R.id.etPhone, R.id.etDob, R.id.etGender,
            R.id.etAddress, R.id.etCity, R.id.etCity1, R.id.etCity2, R.id.etCity3,
            R.id.etState, R.id.etPincode, R.id.etNationality, R.id.etCategory,
            R.id.etCasteAuthority, R.id.etCasteSerial,
            R.id.etTenthBoard, R.id.etTenthSchool, R.id.etTenthMaths, R.id.etTenthTotal,
            R.id.etTenthMax, R.id.etTenthYear, R.id.etTenthPercent,
            R.id.etEleventhBoard, R.id.etEleventhSchool, R.id.etEleventhMaths, R.id.etEleventhTotal,
            R.id.etEleventhMax, R.id.etEleventhYear, R.id.etEleventhPercent,
            R.id.etTwelfthBoard, R.id.etTwelfthSchool, R.id.etTwelfthMaths, R.id.etTwelfthTotal,
            R.id.etTwelfthMax, R.id.etTwelfthYear, R.id.etTwelfthPercent
        ).map { findViewById<EditText>(it) }.toTypedArray()

        status = findViewById(R.id.tvStatus)
        loadProfile()

        findViewById<Button>(R.id.btnSave).setOnClickListener { saveProfile() }
        findViewById<Button>(R.id.btnEnableAutofill).setOnClickListener { enableAutofill() }
    }

    override fun onResume() {
        super.onResume()
        val manager = getSystemService(AutofillManager::class.java)
        status.text = if (manager?.isAutofillSupported == true) {
            "Autofill supported — select Doclix in Settings."
        } else {
            "Autofill unavailable on this device."
        }
    }

    private fun loadProfile() {
        val p = repo.load()
        val values = arrayOf(
            p.fullName, p.firstName, p.middleName, p.lastName,
            p.email, p.phone, p.dob, p.gender,
            p.address, p.city, p.city1, p.city2, p.city3,
            p.state, p.pincode, p.nationality, p.category,
            p.casteAuthority, p.casteSerial,
            p.tenthBoard, p.tenthSchool, p.tenthMaths, p.tenthTotal, p.tenthMax, p.tenthYear, p.tenthPercent,
            p.eleventhBoard, p.eleventhSchool, p.eleventhMaths, p.eleventhTotal, p.eleventhMax, p.eleventhYear, p.eleventhPercent,
            p.twelfthBoard, p.twelfthSchool, p.twelfthMaths, p.twelfthTotal, p.twelfthMax, p.twelfthYear, p.twelfthPercent
        )
        values.forEachIndexed { index, value -> fields[index].setText(value) }
    }

    private fun saveProfile() {
        val v = fields.map { it.text.toString().trim() }
        repo.save(UserProfile(
            fullName=v[0], firstName=v[1], middleName=v[2], lastName=v[3],
            email=v[4], phone=v[5], dob=v[6], gender=v[7],
            address=v[8], city=v[9], city1=v[10], city2=v[11], city3=v[12],
            state=v[13], pincode=v[14], nationality=v[15].ifBlank { "Indian" }, category=v[16],
            casteAuthority=v[17], casteSerial=v[18],
            tenthBoard=v[19], tenthSchool=v[20], tenthMaths=v[21], tenthTotal=v[22], tenthMax=v[23], tenthYear=v[24], tenthPercent=v[25],
            eleventhBoard=v[26], eleventhSchool=v[27], eleventhMaths=v[28], eleventhTotal=v[29], eleventhMax=v[30], eleventhYear=v[31], eleventhPercent=v[32],
            twelfthBoard=v[33], twelfthSchool=v[34], twelfthMaths=v[35], twelfthTotal=v[36], twelfthMax=v[37], twelfthYear=v[38], twelfthPercent=v[39]
        ))
        Toast.makeText(this, R.string.profile_saved, Toast.LENGTH_SHORT).show()
    }

    private fun enableAutofill() {
        val intent = Intent(Settings.ACTION_REQUEST_SET_AUTOFILL_SERVICE).apply {
            data = Uri.parse("package:$packageName")
        }
        try {
            startActivity(intent)
        } catch (_: Exception) {
            startActivity(Intent(Settings.ACTION_SETTINGS))
        }
    }
}
