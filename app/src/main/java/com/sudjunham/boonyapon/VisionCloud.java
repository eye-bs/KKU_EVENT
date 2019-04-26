package com.sudjunham.boonyapon;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

public class VisionCloud {
    public static Context context;
    public static CoordinatorLayout coordinatorLayout;
    public static ProgressDialog progressDialog;
    public static TextView cloud_response_data;

    String API = "AIzaSyB8mE2tqPJZiFkv4SsxTxHpEy38vqapwGg";
    public String LOG_TAG = "LOG";

    public VisionCloud(Context context,
                       ProgressDialog progressDialog, CoordinatorLayout coordinatorLayout, TextView cloud_response_data){
        this.context = context;
        this.progressDialog = progressDialog;
        this.coordinatorLayout = coordinatorLayout;
        this.cloud_response_data = cloud_response_data;
    }

    public void callCloudVision(final Bitmap bitmap) throws IOException {

        new AsyncTask<Object, Void, String>() {
            @Override
            protected void onPreExecute() {
                progressDialog = ProgressDialog.show(context, "Downloading Cloud Data", "Please wait", true);

            }

            @Override
            protected String doInBackground(Object... params) {
                try {
                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
                    VisionRequestInitializer requestInitializer =
                            new VisionRequestInitializer(API);
                    Vision.Builder builder = new Vision.Builder
                            (httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(requestInitializer);

                    Vision vision = builder.build();

                    List<Feature> featureList = new ArrayList<>();

                    Feature textDetection = new Feature();
                    textDetection.setType("TEXT_DETECTION");
                    featureList.add(textDetection);


                    List<AnnotateImageRequest> imageList = new ArrayList<>();
                    AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();
                    Image base64EncodedImage = getBase64EncodedJpeg(bitmap);
                    annotateImageRequest.setImage(base64EncodedImage);
                    annotateImageRequest.setFeatures(featureList);
                    imageList.add(annotateImageRequest);

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                            new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(imageList);

                    Vision.Images.Annotate annotateRequest =
                            vision.images().annotate(batchAnnotateImagesRequest);
                    annotateRequest.setDisableGZipContent(true);
                    Log.d(LOG_TAG, "sending request");

                    BatchAnnotateImagesResponse response = annotateRequest.execute();

                    return convertResponseToString(response);

                } catch (GoogleJsonResponseException e) {
                    Log.e(LOG_TAG, "Request failed: " + e.getContent());
                } catch (IOException e) {
                    Log.d(LOG_TAG, "Request failed: " + e.getMessage());
                }

                Snackbar.make(coordinatorLayout, R.string.failedconnection, Snackbar.LENGTH_INDEFINITE)
                        .setAction("Retry", new View.OnClickListener(){
                            @Override
                            public void onClick(View view) {
                                try {
                                    callCloudVision(bitmap);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).show();

                return "";
            }

            protected void onPostExecute(String result) {

                cloud_response_data.setText(result);
                progressDialog.dismiss();
            }
        }.execute();
    }

    public Image getBase64EncodedJpeg(Bitmap bitmap) {
        Image image = new Image();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        image.encodeContent(imageBytes);
        return image;
    }
    private String convertResponseToString(BatchAnnotateImagesResponse response) {
        StringBuilder message = new StringBuilder();

        List<EntityAnnotation> texts = response.getResponses().get(0)
                .getTextAnnotations();
        if (texts != null) {
            for (EntityAnnotation text : texts) {
                message.append(text.getDescription());
                break;
            }
        } else {
            message.append("nothing\n");
        }
        return message.toString();
    }


}
