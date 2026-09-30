package com.doclix.autofill.service;

import android.text.InputType;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class FieldClassifier {
    public enum FieldKey {
        NONE, FULL_NAME, FIRST_NAME, MIDDLE_NAME, LAST_NAME, EMAIL, PHONE, DOB,
        GENDER, ADDRESS, CITY, CITY_1, CITY_2, CITY_3, STATE, PINCODE, NATIONALITY, CATEGORY,
        CASTE_AUTHORITY, CASTE_SERIAL,
        TENTH_BOARD, TENTH_SCHOOL, TENTH_MATHS, TENTH_TOTAL, TENTH_MAX, TENTH_YEAR, TENTH_PERCENT,
        ELEVENTH_BOARD, ELEVENTH_SCHOOL, ELEVENTH_MATHS, ELEVENTH_TOTAL, ELEVENTH_MAX, ELEVENTH_YEAR, ELEVENTH_PERCENT,
        TWELFTH_BOARD, TWELFTH_SCHOOL, TWELFTH_MATHS, TWELFTH_TOTAL, TWELFTH_MAX, TWELFTH_YEAR, TWELFTH_PERCENT
    }

    private static final List<String> AUXILIARY_CONTACT_FIELDS =
            Collections.unmodifiableList(Arrays.asList(
                    "whatsapp", "whatsapp_no", "whatsapp_number", "alt_mobile",
                    "alternate_mobile", "alternate_phone", "emergency_contact",
                    "emergency_phone", "alternate_email", "alternate_email_id",
                    "secondary_email", "secondary_email_id"));

    private FieldClassifier() {}

    public static FieldKey classify(String[] autofillHints, String idEntry, String hint,
                                    String contentDescription,
                                    List<Pair<String, String>> htmlAttributes,
                                    int inputType) {
        String combined = normalize(join(idEntry, hint, contentDescription,
                htmlToSearchText(htmlAttributes),
                autofillHints == null ? "" : String.join(" ", autofillHints)));

        if (isAuxiliaryContactField(combined)) return FieldKey.NONE;

        FieldKey result = classifyAutofillHints(autofillHints);
        if (result != FieldKey.NONE) return result;

        result = classifyStandardMetadata(normalize(idEntry), normalize(hint),
                normalize(contentDescription));
        if (result != FieldKey.NONE) return result;

        result = classifyHtmlAttributes(htmlAttributes);
        if (result != FieldKey.NONE) return result;

        return classifyInputType(inputType);
    }

    public static FieldKey classify(String[] autofillHints, String idEntry, String hint,
                                    String contentDescription, Map<String, String> htmlAttributes,
                                    int inputType) {
        return classify(autofillHints, idEntry, hint, contentDescription,
                toPairs(htmlAttributes), inputType);
    }

    private static FieldKey classifyAutofillHints(String[] hints) {
        if (hints == null) return FieldKey.NONE;
        for (String raw : hints) {
            String hint = normalize(raw);
            switch (hint) {
                case "name":
                case "personname":
                case "personfullname": return FieldKey.FULL_NAME;
                case "persongivenname":
                case "givenname":
                case "firstname": return FieldKey.FIRST_NAME;
                case "personmiddlename":
                case "middlename":
                case "additionalname": return FieldKey.MIDDLE_NAME;
                case "personfamilyname":
                case "familyname":
                case "lastname":
                case "surname": return FieldKey.LAST_NAME;
                case "emailaddress":
                case "email": return FieldKey.EMAIL;
                case "phone":
                case "phonenumber":
                case "phonenational": return FieldKey.PHONE;
                case "birthdate":
                case "birthdatefull": return FieldKey.DOB;
                case "gender": return FieldKey.GENDER;
                case "postaladdress":
                case "streetaddress": return FieldKey.ADDRESS;
                case "addresslocality":
                case "postaladdresslocality": return FieldKey.CITY;
                case "addressregion":
                case "postaladdressregion": return FieldKey.STATE;
                case "postalcode":
                case "postaladdresspostalcode": return FieldKey.PINCODE;
                case "nationality": return FieldKey.NATIONALITY;
                default: break;
            }
        }
        return FieldKey.NONE;
    }

    private static FieldKey classifyStandardMetadata(String id, String hint,
                                                      String contentDescription) {
        String metadata = normalize(join(id, hint, contentDescription));

        if (containsAny(metadata, "email", "mail", "email_id", "emailid", "confirm_email_id",
                "confirmemailid", "email_address", "emailaddress", "e_mail"))
            return FieldKey.EMAIL;
        if (containsAny(metadata, "mobile", "mobile_no", "mobile_number",
                "phone", "phone_no", "phone_number", "contact_number", "contact_no"))
            return FieldKey.PHONE;
        if (containsAny(metadata, "dob", "date_of_birth", "dateofbirth",
                "birth_date", "birthdate", "birthday"))
            return FieldKey.DOB;
        if (containsAny(metadata, "first_name", "firstname", "given_name",
                "givenname", "person_given_name"))
            return FieldKey.FIRST_NAME;
        if (containsAny(metadata, "middle_name", "middlename",
                "additional_name", "additionalname"))
            return FieldKey.MIDDLE_NAME;
        if (containsAny(metadata, "last_name", "lastname", "family_name",
                "familyname", "surname"))
            return FieldKey.LAST_NAME;
        if (containsAny(metadata, "full_name", "fullname", "person_name",
                "personname", "applicant_name", "candidate_name", "student_name"))
            return FieldKey.FULL_NAME;
        if (containsAny(metadata, "gender", "sex")) return FieldKey.GENDER;
        if (containsAny(metadata, "address", "street_address", "streetaddress")
                && !containsAny(metadata, "email")) return FieldKey.ADDRESS;
        if (containsAny(metadata, "city1", "city_1")) return FieldKey.CITY_1;
        if (containsAny(metadata, "city2", "city_2")) return FieldKey.CITY_2;
        if (containsAny(metadata, "city3", "city_3")) return FieldKey.CITY_3;
        if (containsAny(metadata, "city", "district", "locality", "town"))
            return FieldKey.CITY;
        if (containsAny(metadata, "state", "province", "region"))
            return FieldKey.STATE;
        if (containsAny(metadata, "pincode", "pin_code", "postal_code",
                "postalcode", "zip_code", "zipcode", "zip"))
            return FieldKey.PINCODE;
        if (containsAny(metadata, "nationality", "citizenship", "citizen"))
            return FieldKey.NATIONALITY;
        if (containsAny(metadata, "category", "reservation_category", "caste_category"))
            return FieldKey.CATEGORY;
        if (containsAny(metadata, "caste_authority", "casteauthority", "caste_issuing_authority"))
            return FieldKey.CASTE_AUTHORITY;
        if (containsAny(metadata, "caste_serial", "casteserial", "caste_certificate_serial"))
            return FieldKey.CASTE_SERIAL;

        if (containsAny(metadata, "10th_board", "tenth_board", "class10_board"))
            return FieldKey.TENTH_BOARD;
        if (containsAny(metadata, "10th_school", "tenth_school", "class10_school"))
            return FieldKey.TENTH_SCHOOL;
        if (containsAny(metadata, "10th_maths", "10th_math", "tenth_maths", "tenth_math", "class10_maths"))
            return FieldKey.TENTH_MATHS;
        if (containsAny(metadata, "10th_total", "tenth_total", "class10_total"))
            return FieldKey.TENTH_TOTAL;
        if (containsAny(metadata, "10th_max", "tenth_max", "class10_max"))
            return FieldKey.TENTH_MAX;
        if (containsAny(metadata, "10th_year", "tenth_year", "class10_year"))
            return FieldKey.TENTH_YEAR;
        if (containsAny(metadata, "10th_percent", "10th_percentage",
                "tenth_percent", "tenth_percentage", "class10_percent"))
            return FieldKey.TENTH_PERCENT;

        if (containsAny(metadata, "11th_board", "eleventh_board", "class11_board"))
            return FieldKey.ELEVENTH_BOARD;
        if (containsAny(metadata, "11th_school", "eleventh_school", "class11_school"))
            return FieldKey.ELEVENTH_SCHOOL;
        if (containsAny(metadata, "11th_maths", "11th_math", "eleventh_maths", "eleventh_math", "class11_maths"))
            return FieldKey.ELEVENTH_MATHS;
        if (containsAny(metadata, "11th_total", "eleventh_total", "class11_total"))
            return FieldKey.ELEVENTH_TOTAL;
        if (containsAny(metadata, "11th_max", "eleventh_max", "class11_max"))
            return FieldKey.ELEVENTH_MAX;
        if (containsAny(metadata, "11th_year", "eleventh_year", "class11_year"))
            return FieldKey.ELEVENTH_YEAR;
        if (containsAny(metadata, "11th_percent", "11th_percentage",
                "eleventh_percent", "eleventh_percentage", "class11_percent"))
            return FieldKey.ELEVENTH_PERCENT;

        if (containsAny(metadata, "12th_board", "twelfth_board", "class12_board"))
            return FieldKey.TWELFTH_BOARD;
        if (containsAny(metadata, "12th_school", "twelfth_school", "class12_school"))
            return FieldKey.TWELFTH_SCHOOL;
        if (containsAny(metadata, "12th_maths", "12th_math", "twelfth_maths", "twelfth_math", "class12_maths"))
            return FieldKey.TWELFTH_MATHS;
        if (containsAny(metadata, "12th_total", "twelfth_total", "class12_total"))
            return FieldKey.TWELFTH_TOTAL;
        if (containsAny(metadata, "12th_max", "twelfth_max", "class12_max"))
            return FieldKey.TWELFTH_MAX;
        if (containsAny(metadata, "12th_year", "twelfth_year", "class12_year"))
            return FieldKey.TWELFTH_YEAR;
        if (containsAny(metadata, "12th_percent", "12th_percentage",
                "twelfth_percent", "twelfth_percentage", "class12_percent"))
            return FieldKey.TWELFTH_PERCENT;
        return FieldKey.NONE;
    }

    private static FieldKey classifyHtmlAttributes(List<Pair<String, String>> attributes) {
        if (attributes == null) return FieldKey.NONE;
        for (Pair<String, String> attribute : attributes) {
            if (attribute == null) continue;
            String key = normalize(attribute.first);
            String value = normalize(attribute.second);
            if ("type".equals(key)) {
                if ("email".equals(value)) return FieldKey.EMAIL;
                if ("tel".equals(value)) return FieldKey.PHONE;
                if ("date".equals(value)) return FieldKey.DOB;
            }
            if ("name".equals(key) || "id".equals(key) || "autocomplete".equals(key)) {
                FieldKey result = classifyStandardMetadata(value, "", "");
                if (result != FieldKey.NONE) return result;
            }
        }
        return FieldKey.NONE;
    }

    private static FieldKey classifyInputType(int inputType) {
        int variation = inputType & InputType.TYPE_MASK_VARIATION;
        if (variation == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                || variation == InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS)
            return FieldKey.EMAIL;
        return FieldKey.NONE;
    }

    private static boolean isAuxiliaryContactField(String metadata) {
        String normalized = normalize(metadata);
        for (String field : AUXILIARY_CONTACT_FIELDS) {
            String f = normalize(field);
            if (normalized.equals(f) || normalized.contains("_" + f + "_")
                    || normalized.contains(" " + f + " ")
                    || normalized.startsWith(f + "_") || normalized.endsWith("_" + f))
                return true;
        }
        return normalized.contains("whatsapp")
                || normalized.contains("alt_mobile")
                || normalized.contains("alternate_mobile")
                || normalized.contains("emergency_contact")
                || normalized.contains("alternate_email")
                || normalized.contains("secondary_email");
    }

    private static String htmlToSearchText(List<Pair<String, String>> attributes) {
        if (attributes == null) return "";
        StringBuilder builder = new StringBuilder();
        for (Pair<String, String> attribute : attributes) {
            if (attribute == null) continue;
            if (attribute.first != null) builder.append(attribute.first).append(' ');
            if (attribute.second != null) builder.append(attribute.second).append(' ');
        }
        return builder.toString();
    }

    private static List<Pair<String, String>> toPairs(Map<String, String> attributes) {
        if (attributes == null || attributes.isEmpty()) return Collections.emptyList();
        List<Pair<String, String>> result = new ArrayList<>();
        for (Map.Entry<String, String> entry : attributes.entrySet())
            result.add(new Pair<>(entry.getKey(), entry.getValue()));
        return result;
    }

    private static boolean containsAny(String value, String... candidates) {
        if (value == null || value.isEmpty()) return false;
        for (String candidate : candidates) {
            String normalizedCandidate = normalize(candidate);
            if (value.equals(normalizedCandidate) || value.contains(normalizedCandidate))
                return true;
        }
        return false;
    }

    private static String join(String... values) {
        StringBuilder builder = new StringBuilder();
        for (String value : values)
            if (value != null && !value.trim().isEmpty()) builder.append(value).append(' ');
        return builder.toString();
    }

    private static String normalize(String value) {
        if (value == null) return "";
        return value.trim()
                .replaceAll("([a-z0-9])([A-Z])", "$1_$2")
                .toLowerCase(Locale.US)
                .replace('-', '_').replace(':', '_').replace('/', '_').replace('.', '_')
                .replaceAll("[^a-z0-9_]+", "_").replaceAll("_+", "_")
                .replaceAll("^_", "").replaceAll("_$", "");
    }
}
