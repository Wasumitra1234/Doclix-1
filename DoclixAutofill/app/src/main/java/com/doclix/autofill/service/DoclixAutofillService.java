package com.doclix.autofill.service;

import android.app.assist.AssistStructure;
import android.os.Build;
import android.os.CancellationSignal;
import android.service.autofill.AutofillService;
import android.service.autofill.Dataset;
import android.service.autofill.Field;
import android.service.autofill.FillCallback;
import android.service.autofill.FillContext;
import android.service.autofill.FillRequest;
import android.service.autofill.FillResponse;
import android.service.autofill.Presentations;
import android.service.autofill.SaveCallback;
import android.service.autofill.SaveRequest;
import android.util.Log;
import android.util.Pair;
import android.view.ViewStructure;
import android.view.autofill.AutofillId;
import android.view.autofill.AutofillValue;
import android.widget.RemoteViews;

import com.doclix.autofill.data.ProfileRepository;
import com.doclix.autofill.data.UserProfile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DoclixAutofillService extends AutofillService {
    private static final String TAG = "DoclixAutofill";
    private ProfileRepository profileRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        profileRepository = new ProfileRepository(getApplicationContext());
    }

    @Override
    public void onFillRequest(FillRequest request, CancellationSignal cancellationSignal,
                               FillCallback callback) {
        if (callback == null) return;
        if (cancellationSignal != null && cancellationSignal.isCanceled()) return;
        UserProfile profile = profileRepository.load();
        if (profile == null || profile.isEmpty()) {
            callback.onSuccess(null);
            return;
        }

        List<FillContext> contexts = request == null ? null : request.getFillContexts();
        if (contexts == null || contexts.isEmpty()) {
            callback.onSuccess(null);
            return;
        }

        FillContext lastContext = contexts.get(contexts.size() - 1);
        if (lastContext == null || lastContext.getStructure() == null) {
            callback.onSuccess(null);
            return;
        }

        List<AutofillField> fields = new ArrayList<>();
        parseStructure(lastContext.getStructure(), fields);
        if (fields.isEmpty()) {
            callback.onSuccess(null);
            return;
        }

        LinkedHashMap<AutofillId, AutofillField> routed =
                routeFocusedField(lastContext.getFocusedId(), fields);

        Dataset.Builder datasetBuilder = new Dataset.Builder();
        int populated = 0;

        for (AutofillField field : routed.values()) {
            String value = valueForField(field.classification, profile);
            if (value == null || value.trim().isEmpty()) continue;

            AutofillValue autofillValue = AutofillValue.forText(value);
            RemoteViews presentation = createPresentation(value);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                try {
                    Field.Builder fieldBuilder = new Field.Builder();
                    fieldBuilder.setValue(autofillValue);
                    fieldBuilder.setPresentations(
                            new Presentations.Builder()
                                    .setMenuPresentation(presentation)
                                    .build());
                    datasetBuilder.setField(field.id, fieldBuilder.build());
                    populated++;
                    continue;
                } catch (RuntimeException e) {
                    Log.w(TAG, "API 33 field path failed; using legacy setValue", e);
                }
            }

            try {
                datasetBuilder.setValue(field.id, autofillValue, presentation);
                populated++;
            } catch (RuntimeException e) {
                Log.e(TAG, "Unable to add autofill value", e);
            }
        }

        if (populated == 0) {
            callback.onSuccess(null);
            return;
        }

        callback.onSuccess(new FillResponse.Builder()
                .addDataset(datasetBuilder.build())
                .build());
    }

    @Override
    public void onSaveRequest(SaveRequest request, SaveCallback callback) {
        if (callback != null) callback.onSuccess();
    }

    private LinkedHashMap<AutofillId, AutofillField> routeFocusedField(
            AutofillId focusedId, List<AutofillField> fields) {
        LinkedHashMap<AutofillId, AutofillField> result = new LinkedHashMap<>();
        if (focusedId != null) {
            for (AutofillField field : fields) {
                if (focusedId.equals(field.id)) {
                    result.put(field.id, field);
                    break;
                }
            }
        }
        for (AutofillField field : fields) result.putIfAbsent(field.id, field);
        return result;
    }

    private void parseStructure(AssistStructure structure, List<AutofillField> output) {
        for (int i = 0; i < structure.getWindowNodeCount(); i++) {
            AssistStructure.WindowNode window = structure.getWindowNodeAt(i);
            if (window != null && window.getRootViewNode() != null)
                walkNode(window.getRootViewNode(), output);
        }
    }

    private void walkNode(AssistStructure.ViewNode node, List<AutofillField> output) {
        if (node == null) return;

        AutofillId id = node.getAutofillId();
        if (id != null && node.getAutofillType() == android.view.View.AUTOFILL_TYPE_TEXT) {
            FieldClassifier.FieldKey key = classify(node);
            if (key != FieldClassifier.FieldKey.NONE)
                output.add(new AutofillField(id, key));
        }

        for (int i = 0; i < node.getChildCount(); i++) walkNode(node.getChildAt(i), output);
    }

    private FieldClassifier.FieldKey classify(AssistStructure.ViewNode node) {
        if (node == null) return FieldClassifier.FieldKey.NONE;
        CharSequence description = node.getContentDescription();
        return FieldClassifier.classify(
                node.getAutofillHints(),
                node.getIdEntry(),
                node.getHint() == null ? "" : node.getHint().toString(),
                description == null ? "" : description.toString(),
                extractHtmlAttributes(node.getHtmlInfo()),
                node.getInputType());
    }

    static FieldClassifier.FieldKey classifyMetadataForTest(
            String[] autofillHints, String idEntry, String hint, String contentDescription,
            Map<String, String> htmlAttributes, int inputType) {
        return FieldClassifier.classify(autofillHints, idEntry, hint, contentDescription,
                htmlAttributes, inputType);
    }

    List<Pair<String, String>> extractHtmlAttributes(ViewStructure.HtmlInfo htmlInfo) {
        if (htmlInfo == null || htmlInfo.getAttributes() == null)
            return Collections.emptyList();

        List<Pair<String, String>> result = new ArrayList<>();
        for (Pair<String, String> attribute : htmlInfo.getAttributes()) {
            if (attribute == null || attribute.first == null) continue;
            result.add(new Pair<>(
                    attribute.first.toLowerCase(Locale.US),
                    attribute.second == null ? "" : attribute.second));
        }
        return result;
    }

    private String valueForField(FieldClassifier.FieldKey key, UserProfile p) {
        switch (key) {
            case FULL_NAME: return p.getFullName();
            case FIRST_NAME: return p.getFirstName();
            case MIDDLE_NAME: return p.getMiddleName();
            case LAST_NAME: return p.getLastName();
            case EMAIL: return p.getEmail();
            case PHONE: return p.getPhone();
            case DOB: return p.getDob();
            case GENDER: return p.getGender();
            case ADDRESS: return p.getAddress();
            case CITY: return p.getCity();
            case STATE: return p.getState();
            case PINCODE: return p.getPincode();
            case NATIONALITY: return p.getNationality();
            case CATEGORY: return p.getCategory();
            case TENTH_BOARD: return p.getTenthBoard();
            case TENTH_PERCENT: return p.getTenthPercent();
            case ELEVENTH_BOARD: return p.getEleventhBoard();
            case ELEVENTH_PERCENT: return p.getEleventhPercent();
            case TWELFTH_BOARD: return p.getTwelfthBoard();
            case TWELFTH_PERCENT: return p.getTwelfthPercent();
            default: return null;
        }
    }

    private RemoteViews createPresentation(String value) {
        RemoteViews views = new RemoteViews(getPackageName(), android.R.layout.simple_list_item_1);
        views.setTextViewText(android.R.id.text1, "Doclix: " + value);
        return views;
    }

    private static final class AutofillField {
        final AutofillId id;
        final FieldClassifier.FieldKey classification;

        AutofillField(AutofillId id, FieldClassifier.FieldKey classification) {
            this.id = id;
            this.classification = classification;
        }
    }
}
