package com.ashomok.ocrme.ocr_result.tab_fragments.text.translate.translate_task;

import android.util.Log;
import android.util.Pair;

import com.ashomok.ocrme.ocr_result.tab_fragments.text.translate.translate_task.translate_task.SupportedLanguagesResponse;
import com.ashomok.ocrme.ocr_result.tab_fragments.text.translate.translate_task.translate_task.TranslateHttpClient;
import com.ashomok.ocrme.ocr_result.tab_fragments.text.translate.translate_task.translate_task.TranslateResponse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import com.ashomok.ocrme.utils.LogHelper;

/**
 * Created by iuliia on 9/6/17.
 */
public class TranslateHttpClientTest {
    private TranslateHttpClient client;
    private static final String TAG = LogHelper.makeLogTag(TranslateHttpClientTest.class);

    @Before
    public void init() {
        client = TranslateHttpClient.getInstance();
    }

    @Test
    public void getSupportedLanguages() {
        SupportedLanguagesResponse response =
                client.getSupportedLanguages("en").blockingGet();

        Assert.assertTrue(response.getSupportedLanguages().size() > 0);
        Assert.assertTrue(response.getStatus().equals(SupportedLanguagesResponse.Status.OK));
    }

    @Test
    public void translate() {
        TranslateResponse responce =
                client.translate("de", "наша мама добрая", null).blockingGet();

        Assert.assertTrue(responce.getStatus().equals(TranslateResponse.Status.OK));
        TranslateResponse.TranslateResult translateResult = responce.getTranslateResult();
        Assert.assertTrue(translateResult.getSourceLanguageCode().equals("ru"));
        Assert.assertTrue(translateResult.getTargetLanguageCode().equals("de"));
        Assert.assertTrue(translateResult.getTextResult().length() > 0);
    }

    @Test
    public void translate2() {
        TranslateResponse responce =
                client.translate("de", "русский длинный текст русский длинный текструсский длинный текструсский длинный текструсский длинный текструсский длинный текструсский длинный текструсский длинный текструсский длинный текструсский длинный текструсский длинный текструсский длинный текструсский длинный текструсский длинный текструсский длинный текструсский длинный текструсский длинный текструсский длинный текструсский длинный текструсский длинный текструсский длинный текструсский длинный текструсский длинный текструсский длинный текструсский длинный текст",
   null).blockingGet();

        Assert.assertTrue(responce.getStatus().equals(TranslateResponse.Status.OK));
        TranslateResponse.TranslateResult translateResult = responce.getTranslateResult();
        Assert.assertTrue(translateResult.getSourceLanguageCode().equals("ru"));
        Assert.assertTrue(translateResult.getTargetLanguageCode().equals("de"));
        Assert.assertTrue(translateResult.getTextResult().length() > 0);
    }

    @Test
    public void callInParallel() throws InterruptedException {

        Single<SupportedLanguagesResponse> supportedLanguagesResponceSingle =
                client.getSupportedLanguages("en")
                        .doOnEvent((supportedLanguagesResponce, throwable) -> {
                                    LogHelper.d(TAG, "getSupportedLanguages thread = "
                                            + Thread.currentThread().getName());
                                }
                        ).subscribeOn(Schedulers.io());

        Single<TranslateResponse> translateResponseSingle =
                client.translate("de", "наша мама добрая", null)
                        .doOnEvent((supportedLanguagesResponce, throwable) -> {
                                    LogHelper.d(TAG, "getSupportedLanguages thread = "
                                            + Thread.currentThread().getName());
                                }
                        ).subscribeOn(Schedulers.io());

        Single<Pair<SupportedLanguagesResponse, TranslateResponse>> zipped =
                Single.zip(
                        supportedLanguagesResponceSingle,
                        translateResponseSingle,
                        (a, b) -> new Pair<>(a, b))
                        .observeOn(AndroidSchedulers.mainThread());// Will switch to Main-Thread when finished

        zipped.subscribe(myData -> {
            LogHelper.d(TAG, "zipped called with " + myData.toString()
                    + "in thread " + Thread.currentThread().getName());
        }, throwable -> {
            LogHelper.e(TAG, throwable.getMessage());
        });

        Thread.sleep(3000);
        Pair<SupportedLanguagesResponse, TranslateResponse> result = zipped.blockingGet();

        SupportedLanguagesResponse supportedLanguagesResponse = result.first;
        TranslateResponse translateResponse = result.second;

        Assert.assertEquals(supportedLanguagesResponse.getStatus(), SupportedLanguagesResponse.Status.OK);
        Assert.assertEquals(translateResponse.getStatus(), TranslateResponse.Status.OK);
    }
}