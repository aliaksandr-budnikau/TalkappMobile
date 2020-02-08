package talkapp.org.talkappmobile.activity;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.model.NewWordSetDraftQRObject;
import talkapp.org.talkappmobile.service.impl.ServiceFactoryBean;

@EActivity(R.layout.activity_word_set_qr_export)
public class WordSetQRExportActivity extends BaseActivity {

    public static final String WORD_SET_MAPPING = "wordSet";

    @ViewById(R.id.qrCode)
    ImageView qrCode;

    @Extra(WORD_SET_MAPPING)
    NewWordSetDraftQRObject qrObject;

    @AfterViews
    public void init() {
        generateQR();
    }

    @Background
    public void generateQR() {
        ObjectMapper mapper = ServiceFactoryBean.getInstance(getApplication()).getMapper();
        String text = null;
        try {
            text = mapper.writeValueAsString(qrObject);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            HashMap<EncodeHintType, String> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 500, 500, hints);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            showQR(bitmap);
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }

    @UiThread
    public void showQR(Bitmap bitmap) {
        qrCode.setImageBitmap(bitmap);
    }
}