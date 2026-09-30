package com.doclix.autofill.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.autofill.AutofillManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
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
            R.id.etFullName,
            R.id.etFirstName,
            R.id.etMiddleName,
            R.id.etLastName,
            R.id.etEmail,
            R.id.etPhone,
            R.id.etDob,
            R.id.etGender,
            R.id.etAddress,
            R.id.etCity,
            R.id.etState,
            R.id.etPincode,
            R.id.etNationality,
            R.id.etCategory,
            R.id.etTenthBoard,
            R.id.etTenthPercent,
            R.id.etEleventhBoard,
            R.id.etEleventhPercent,
            R.id.etTwelfthBoard,
            R.id.etTwelfthPercent
        ).map { findViewById<EditText>(it) }.toTypedArray()

        status = findViewById(R.id.tvStatus)

        loadProfile()

        findViewById<Button>(R.id.btnSave).setOnClickListener {
            saveProfile()
        }

        findViewById<Button>(R.id.btnEnableAutofill).setOnClickListener {
            enableAutofill()
        }
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
        val profile = repo.load()

        val values = arrayOf(
            profile.fullName,
            profile.firstName,
            profile.middleName,
            profile.lastName,
            profile.email,
            profile.phone,
            profile.dob,
            profile.gender,
            profile.address,
            profile.city,
            profile.state,
            profile.pincode,
            profile.nationality,
            profile.category,
            profile.tenthBoard,
            profile.tenthPercent,
            profile.eleventhBoard,
            profile.eleventhPercent,
            profile.twelfthBoard,
            profile.twelfthPercent
        )

        values.forEachIndexed { index, value ->
            fields[index].setText(value)
        }
    }

    private fun saveProfile() {
        val values = fields.map { it.text.toString().trim() }

        repo.save(
            UserProfile(
                fullName = values[0],
                firstName = values[1],
                middleName = values[2],
                lastName = values[3],
                email = values[4],
                phone = values[5],
                dob = values[6],
                gender = values[7],
                address = values[8],
                city = values[9],
                state = values[10],
                pincode = values[11],
                nationality = values[12].ifBlank { "Indian" },
                category = values[13],
                tenthBoard = values[14],
                tenthPercent = values[15],
                eleventhBoard = values[16],
                eleventhPercent = values[17],
                twelfthBoard = values[18],
                twelfthPercent = values[19]
            )
        )

        Toast.makeText(
            this,
            R.string.profile_saved,
            Toast.LENGTH_SHORT
        ).show()
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
