package com.doclix.autofill.service;

import static org.junit.Assert.assertEquals;

import android.text.InputType;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 34)
public class DoclixAutofillServiceTest {
    private static FieldClassifier.FieldKey classify(String[] hints, String id,
                                                      Map<String, String> html, int inputType) {
        return DoclixAutofillService.classifyMetadataForTest(
                hints, id, "", "", html, inputType);
    }

    private static FieldClassifier.FieldKey byId(String id) {
        return classify(null, id, Collections.emptyMap(), InputType.TYPE_CLASS_TEXT);
    }

    @Test public void completeFormTextFieldMappingsPass() {
        Map<String, FieldClassifier.FieldKey> expected = new LinkedHashMap<>();
        expected.put("fullName", FieldClassifier.FieldKey.FULL_NAME);
        expected.put("firstName", FieldClassifier.FieldKey.FIRST_NAME);
        expected.put("middleName", FieldClassifier.FieldKey.MIDDLE_NAME);
        expected.put("lastName", FieldClassifier.FieldKey.LAST_NAME);
        expected.put("email", FieldClassifier.FieldKey.EMAIL);
        expected.put("confirmEmail", FieldClassifier.FieldKey.EMAIL);
        expected.put("dob", FieldClassifier.FieldKey.DOB);
        expected.put("gender", FieldClassifier.FieldKey.GENDER);
        expected.put("nationality", FieldClassifier.FieldKey.NATIONALITY);
        expected.put("category", FieldClassifier.FieldKey.CATEGORY);
        expected.put("casteAuthority", FieldClassifier.FieldKey.CASTE_AUTHORITY);
        expected.put("casteSerial", FieldClassifier.FieldKey.CASTE_SERIAL);

        expected.put("tenthBoard", FieldClassifier.FieldKey.TENTH_BOARD);
        expected.put("tenthSchool", FieldClassifier.FieldKey.TENTH_SCHOOL);
        expected.put("tenthMaths", FieldClassifier.FieldKey.TENTH_MATHS);
        expected.put("tenthTotal", FieldClassifier.FieldKey.TENTH_TOTAL);
        expected.put("tenthMax", FieldClassifier.FieldKey.TENTH_MAX);
        expected.put("tenthYear", FieldClassifier.FieldKey.TENTH_YEAR);
        expected.put("tenthPercent", FieldClassifier.FieldKey.TENTH_PERCENT);

        expected.put("eleventhBoard", FieldClassifier.FieldKey.ELEVENTH_BOARD);
        expected.put("eleventhSchool", FieldClassifier.FieldKey.ELEVENTH_SCHOOL);
        expected.put("eleventhMaths", FieldClassifier.FieldKey.ELEVENTH_MATHS);
        expected.put("eleventhTotal", FieldClassifier.FieldKey.ELEVENTH_TOTAL);
        expected.put("eleventhMax", FieldClassifier.FieldKey.ELEVENTH_MAX);
        expected.put("eleventhYear", FieldClassifier.FieldKey.ELEVENTH_YEAR);
        expected.put("eleventhPercent", FieldClassifier.FieldKey.ELEVENTH_PERCENT);

        expected.put("twelfthBoard", FieldClassifier.FieldKey.TWELFTH_BOARD);
        expected.put("twelfthSchool", FieldClassifier.FieldKey.TWELFTH_SCHOOL);
        expected.put("twelfthMaths", FieldClassifier.FieldKey.TWELFTH_MATHS);
        expected.put("twelfthTotal", FieldClassifier.FieldKey.TWELFTH_TOTAL);
        expected.put("twelfthMax", FieldClassifier.FieldKey.TWELFTH_MAX);
        expected.put("twelfthYear", FieldClassifier.FieldKey.TWELFTH_YEAR);
        expected.put("twelfthPercent", FieldClassifier.FieldKey.TWELFTH_PERCENT);

        for (Map.Entry<String, FieldClassifier.FieldKey> entry : expected.entrySet()) {
            assertEquals("Field: " + entry.getKey(), entry.getValue(), byId(entry.getKey()));
        }
    }

    @Test public void cityVariantsMapSeparately() {
        assertEquals(FieldClassifier.FieldKey.CITY_1, byId("city1"));
        assertEquals(FieldClassifier.FieldKey.CITY_2, byId("city2"));
        assertEquals(FieldClassifier.FieldKey.CITY_3, byId("city3"));
    }

    @Test public void emailVariantsMapToEmail() {
        assertEquals(FieldClassifier.FieldKey.EMAIL, byId("email"));
        assertEquals(FieldClassifier.FieldKey.EMAIL, byId("email_id"));
        assertEquals(FieldClassifier.FieldKey.EMAIL, byId("confirm_email_id"));
    }

    @Test public void htmlPrimaryEmailNameMapsToEmail() {
        Map<String, String> html = new LinkedHashMap<>();
        html.put("name", "email");
        assertEquals(FieldClassifier.FieldKey.EMAIL,
                classify(null, "", html, InputType.TYPE_CLASS_TEXT));
    }

    @Test public void htmlAutocompleteEmailMapsToEmail() {
        Map<String, String> html = new LinkedHashMap<>();
        html.put("autocomplete", "email");
        assertEquals(FieldClassifier.FieldKey.EMAIL,
                classify(null, "", html, InputType.TYPE_CLASS_TEXT));
    }

    @Test public void htmlEmailTypeMapsToEmail() {
        Map<String, String> html = new LinkedHashMap<>();
        html.put("type", "email");
        assertEquals(FieldClassifier.FieldKey.EMAIL,
                classify(null, "", html, InputType.TYPE_CLASS_TEXT));
    }

    @Test public void emailInputTypesMapToEmail() {
        int email = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
        int webEmail = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS;
        assertEquals(FieldClassifier.FieldKey.EMAIL,
                classify(null, "", Collections.emptyMap(), email));
        assertEquals(FieldClassifier.FieldKey.EMAIL,
                classify(null, "", Collections.emptyMap(), webEmail));
    }

    @Test public void autofillEmailHintMapsToEmail() {
        assertEquals(FieldClassifier.FieldKey.EMAIL,
                classify(new String[]{"emailAddress"}, "unknown",
                        Collections.emptyMap(), InputType.TYPE_CLASS_TEXT));
    }

    @Test public void auxiliaryContactsRemainBlocked() {
        assertEquals(FieldClassifier.FieldKey.NONE,
                classify(new String[]{"phone"}, "whatsapp_no",
                        Collections.emptyMap(), InputType.TYPE_CLASS_PHONE));
        assertEquals(FieldClassifier.FieldKey.NONE,
                classify(new String[]{"phone"}, "alt_mobile",
                        Collections.emptyMap(), InputType.TYPE_CLASS_PHONE));
        assertEquals(FieldClassifier.FieldKey.NONE,
                classify(new String[]{"emailAddress"}, "alternate_email",
                        Collections.emptyMap(), InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS));
    }

    @Test public void primaryStandardFieldsPass() {
        assertEquals(FieldClassifier.FieldKey.FULL_NAME, byId("full_name"));
        assertEquals(FieldClassifier.FieldKey.FIRST_NAME, byId("first_name"));
        assertEquals(FieldClassifier.FieldKey.LAST_NAME, byId("last_name"));
        assertEquals(FieldClassifier.FieldKey.PHONE, byId("mobile"));
        assertEquals(FieldClassifier.FieldKey.DOB, byId("dob"));
        assertEquals(FieldClassifier.FieldKey.CATEGORY, byId("category"));
    }
}
