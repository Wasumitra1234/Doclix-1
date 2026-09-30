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

    @Test public void emailIdMapsToEmail() {
        assertEquals(FieldClassifier.FieldKey.EMAIL,
                classify(null, "email_id", Collections.emptyMap(), InputType.TYPE_CLASS_TEXT));
    }

    @Test public void confirmEmailIdMapsToEmail() {
        assertEquals(FieldClassifier.FieldKey.EMAIL,
                classify(null, "confirm_email_id", Collections.emptyMap(), InputType.TYPE_CLASS_TEXT));
    }

    @Test public void htmlEmailTypeMapsToEmail() {
        Map<String, String> html = new LinkedHashMap<>();
        html.put("type", "email");
        assertEquals(FieldClassifier.FieldKey.EMAIL,
                classify(null, "", html, InputType.TYPE_CLASS_TEXT));
    }

    @Test public void htmlPrimaryEmailNameMapsToEmail() {
        Map<String, String> html = new LinkedHashMap<>();
        html.put("name", "email");
        assertEquals(FieldClassifier.FieldKey.EMAIL,
                classify(null, "", html, InputType.TYPE_CLASS_TEXT));
    }

    @Test public void htmlEmailNameMapsToEmail() {
        Map<String, String> html = new LinkedHashMap<>();
        html.put("name", "email_id");
        assertEquals(FieldClassifier.FieldKey.EMAIL,
                classify(null, "", html, InputType.TYPE_CLASS_TEXT));
    }

    @Test public void htmlAutocompleteEmailMapsToEmail() {
        Map<String, String> html = new LinkedHashMap<>();
        html.put("autocomplete", "email");
        assertEquals(FieldClassifier.FieldKey.EMAIL,
                classify(null, "", html, InputType.TYPE_CLASS_TEXT));
    }

    @Test public void emailInputTypeMapsToEmail() {
        int inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
        assertEquals(FieldClassifier.FieldKey.EMAIL,
                classify(null, "", Collections.emptyMap(), inputType));
    }

    @Test public void webEmailInputTypeMapsToEmail() {
        int inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS;
        assertEquals(FieldClassifier.FieldKey.EMAIL,
                classify(null, "", Collections.emptyMap(), inputType));
    }

    @Test public void emailAutofillHintMapsToEmail() {
        assertEquals(FieldClassifier.FieldKey.EMAIL,
                classify(new String[]{"emailAddress"}, "unknown", Collections.emptyMap(),
                        InputType.TYPE_CLASS_TEXT));
    }

    @Test public void whatsappNoIsBlocked() {
        assertEquals(FieldClassifier.FieldKey.NONE,
                classify(new String[]{"phone"}, "whatsapp_no", Collections.emptyMap(),
                        InputType.TYPE_CLASS_PHONE));
    }

    @Test public void alternateMobileIsBlocked() {
        assertEquals(FieldClassifier.FieldKey.NONE,
                classify(new String[]{"phone"}, "alt_mobile", Collections.emptyMap(),
                        InputType.TYPE_CLASS_PHONE));
    }

    @Test public void alternateEmailIsBlocked() {
        assertEquals(FieldClassifier.FieldKey.NONE,
                classify(new String[]{"emailAddress"}, "alternate_email", Collections.emptyMap(),
                        InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS));
    }

    @Test public void primaryFullNamePasses() {
        assertEquals(FieldClassifier.FieldKey.FULL_NAME,
                classify(null, "full_name", Collections.emptyMap(), InputType.TYPE_CLASS_TEXT));
    }

    @Test public void primaryFirstNamePasses() {
        assertEquals(FieldClassifier.FieldKey.FIRST_NAME,
                classify(new String[]{"personGivenName"}, "", Collections.emptyMap(),
                        InputType.TYPE_CLASS_TEXT));
    }

    @Test public void primaryLastNamePasses() {
        assertEquals(FieldClassifier.FieldKey.LAST_NAME,
                classify(new String[]{"personFamilyName"}, "", Collections.emptyMap(),
                        InputType.TYPE_CLASS_TEXT));
    }

    @Test public void mobilePasses() {
        assertEquals(FieldClassifier.FieldKey.PHONE,
                classify(null, "mobile", Collections.emptyMap(), InputType.TYPE_CLASS_PHONE));
    }

    @Test public void dobPasses() {
        assertEquals(FieldClassifier.FieldKey.DOB,
                classify(null, "dob", Collections.emptyMap(), InputType.TYPE_CLASS_TEXT));
    }

    @Test public void categoryPasses() {
        assertEquals(FieldClassifier.FieldKey.CATEGORY,
                classify(null, "category", Collections.emptyMap(), InputType.TYPE_CLASS_TEXT));
    }
}
