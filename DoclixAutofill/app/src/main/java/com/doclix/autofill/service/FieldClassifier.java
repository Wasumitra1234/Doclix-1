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
                normalize(contentDescription), combined);
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
                case "person_name":
                case "personfullname":
                case "person_fullname": return FieldKey.FULL_NAME;
                case "persongivenname":
                case "person_given_name":
                case "givenname":
                case "given_name":
                case "firstname":
                case "first_name": return FieldKey.FIRST_NAME;
                case "personmiddlename":
                case "person_middle_name":
                case "middlename":
                case "middle_name":
                case "additionalname":
                case "additional_name": return FieldKey.MIDDLE_NAME;
                case "personfamilyname":
                case "person_family_name":
                case "familyname":
                case "family_name":
                case "lastname":
                case "last_name":
                case "surname": return FieldKey.LAST_NAME;
                case "emailaddress":
                case "email_address":
                case "email": return FieldKey.EMAIL;
                case "phone":
                case "phonenumber":
                case "phone_number":
                case "phonenational": return FieldKey.PHONE;
                case "birthdate":
                case "birthdatefull":
                case "birth_date_full": return FieldKey.DOB;
                case "gender": return FieldKey.GENDER;
                case "postaladdress":
                case "postal_address":
                case "streetaddress":
                case "street_address": return FieldKey.ADDRESS;
                case "addresslocality":
                case "address_locality":
                case "postaladdresslocality":
                case "postal_address_locality": return FieldKey.CITY;
                case "addressregion":
                case "address_region":
                case "postaladdressregion":
                case "postal_address_region": return FieldKey.STATE;
                case "postalcode":
                case "postal_code":
                case "postaladdresspostalcode":
                case "postal_address_postal_code": return FieldKey.PINCODE;
                case "nationality": return FieldKey.NATIONALITY;
                default: break;
            }
        }
        return FieldKey.NONE;
    }

    private static FieldKey classifyStandardMetadata(String id, String hint,
                                                      String contentDescription,
                                                      String combined) {
        String metadata = normalize(join(id, hint, contentDescription));
        String all = (combined == null || combined.isEmpty()) ? metadata : combined;

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
        if (containsAny(metadata, "city1", "city_1", "preferred_city_1", "test_city_1"))
            return FieldKey.CITY_1;
        if (containsAny(metadata, "city2", "city_2", "preferred_city_2", "test_city_2"))
            return FieldKey.CITY_2;
        if (containsAny(metadata, "city3", "city_3", "preferred_city_3", "test_city_3"))
            return FieldKey.CITY_3;
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
        if (containsAny(metadata, "caste_authority", "casteauthority", "caste_issuing_authority",
                "issuing_authority", "certificate_authority"))
            return FieldKey.CASTE_AUTHORITY;
        if (containsAny(metadata, "caste_serial", "casteserial", "caste_certificate_serial",
                "certificate_serial", "certificate_no", "certificate_number"))
            return FieldKey.CASTE_SERIAL;

        FieldKey g10 = classifyGradeBlock(all, "10", "tenth", "ssc", "class10",
                FieldKey.TENTH_BOARD, FieldKey.TENTH_SCHOOL, FieldKey.TENTH_MATHS,
                FieldKey.TENTH_TOTAL, FieldKey.TENTH_MAX, FieldKey.TENTH_YEAR, FieldKey.TENTH_PERCENT);
        if (g10 != FieldKey.NONE) return g10;

        FieldKey g11 = classifyGradeBlock(all, "11", "eleventh", "class11", "class_11",
                FieldKey.ELEVENTH_BOARD, FieldKey.ELEVENTH_SCHOOL, FieldKey.ELEVENTH_MATHS,
                FieldKey.ELEVENTH_TOTAL, FieldKey.ELEVENTH_MAX, FieldKey.ELEVENTH_YEAR, FieldKey.ELEVENTH_PERCENT);
        if (g11 != FieldKey.NONE) return g11;

        FieldKey g12 = classifyGradeBlock(all, "12", "twelfth", "hsc", "class12",
                FieldKey.TWELFTH_BOARD, FieldKey.TWELFTH_SCHOOL, FieldKey.TWELFTH_MATHS,
                FieldKey.TWELFTH_TOTAL, FieldKey.TWELFTH_MAX, FieldKey.TWELFTH_YEAR, FieldKey.TWELFTH_PERCENT);
        if (g12 != FieldKey.NONE) return g12;

        return FieldKey.NONE;
    }

    private static FieldKey classifyGradeBlock(String all, String num, String word, String alt1, String alt2,
                                               FieldKey board, FieldKey school, FieldKey maths,
                                               FieldKey total, FieldKey max, FieldKey year, FieldKey percent) {
        boolean isGrade = containsAny(all, num + "th", num + "_th", "class_" + num, "class" + num,
                word, alt1, alt2);
        if (!isGrade && containsAny(all, num) && containsAny(all, "board", "school", "maths", "math",
                "total", "max", "marks", "percent", "percentage", "cgpa", "year", "pass", "overall"))
            isGrade = true;
        if (!isGrade) return FieldKey.NONE;

        if (containsAny(all, "board", "board_name", "exam_board")) return board;
        if (containsAny(all, "school", "institute", "college", "institution")) return school;
        if (containsAny(all, "maths", "math", "mathematics")) return maths;
        if (containsAny(all, "total_marks", "marks_obtained", "obtained", "total")
                && !containsAny(all, "max", "maximum", "out_of")) return total;
        if (containsAny(all, "max_marks", "maximum_marks", "maximum", "max", "out_of")) return max;
        if (containsAny(all, "year", "passing_year", "pass_year", "month_year", "completion")) return year;
        if (containsAny(all, "percent", "percentage", "cgpa", "gpa", "overall", "aggregate", "score")
                || all.contains("pct"))
            return percent;
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
            if ("name".equals(key) || "id".equals(key) || "autocomplete".equals(key)
                    || "placeholder".equals(key) || "aria_label".equals(key)) {
                FieldKey result = classifyStandardMetadata(value, "", "", value);
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
                .replace('%', '_')
                .replaceAll("[^a-z0-9_]+", "_").replaceAll("_+", "_")
                .replaceAll("^_", "").replaceAll("_$", "");
    }
}
