/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.ar.core.examples.java.augmentedimage;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.ar.core.examples.java.augmentedimage.random_number;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.ar.core.Anchor;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Camera;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.examples.java.augmentedimage.rendering.AugmentedImageRenderer;
import com.google.ar.core.examples.java.common.helpers.CameraPermissionHelper;
import com.google.ar.core.examples.java.common.helpers.DisplayRotationHelper;
import com.google.ar.core.examples.java.common.helpers.FullScreenHelper;
import com.google.ar.core.examples.java.common.helpers.SnackbarHelper;
import com.google.ar.core.examples.java.common.helpers.TrackingStateHelper;
import com.google.ar.core.examples.java.common.rendering.BackgroundRenderer;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * This app extends the HelloAR Java app to include image tracking functionality.
 *
 * <p>In this example, we assume all images are static or moving slowly with a large occupation of
 * the screen. If the target is actively moving, we recommend to check
 * AugmentedImage.getTrackingMethod() and render only when the tracking method equals to
 * FULL_TRACKING. See details in <a
 * href="https://developers.google.com/ar/develop/java/augmented-images/">Recognize and Augment
 * Images</a>.
 * このアプリは、HelloARのJavaアプリを拡張し、画像追跡機能を搭載しています。
 *  *
 *  <p>この例では、すべての画像が静止しているか、またはゆっくりと移動していると仮定しています。
 *  * 画面が表示されます。ターゲットが活発に動いている場合は
 *  * AugmentedImage.getTrackingMethod()を使用し、トラッキングメソッドが等しくなった場合にのみレンダリングします。
 *  * FULL_TRACKING. 詳細は <a
 *  * href="https://developers.google.com/ar/develop/java/augmented-images/">認識して増強する
 *  * 画像</a>.
 */
public class AugmentedImageActivity<rand> extends AppCompatActivity implements GLSurfaceView.Renderer,random_number {
  private static final String TAG = AugmentedImageActivity.class.getSimpleName();
  public static String kekka;
  private static String TAG1 = "dbg1";
  // Rendering. The Renderers are created here, and initialized when the GL surface is created.
  //レンダリングです。レンダラはここで作成され、GLサーフェスの作成時に初期化されます。
  private GLSurfaceView surfaceView;
  private ImageView fitToScanView;
  private RequestManager glideRequestManager;

  private boolean installRequested;
  private Session session;
  private final SnackbarHelper messageSnackbarHelper = new SnackbarHelper();
  private DisplayRotationHelper displayRotationHelper;
  private final TrackingStateHelper trackingStateHelper = new TrackingStateHelper(this);

  private final BackgroundRenderer backgroundRenderer = new BackgroundRenderer();
  private final AugmentedImageRenderer augmentedImageRenderer = new AugmentedImageRenderer();

  private boolean shouldConfigureSession = false;

  // Augmented image configuration and rendering.
  // Load a single image (true) or a pre-generated image database (false).
  //拡張画像の設定とレンダリング.
  //  // 単一の画像を読み込む(true)か、事前に生成された画像データベースを読み込む(false)かを指定します。
  //private final boolean useSingleImage = false;
  private final boolean useSingleImage = true;
  // Augmented image and its associated center pose anchor, keyed by index of the augmented image in
  // the
  // database.
  /*
  データベース内の拡張画像のインデックスがキーとなる、拡張画像とそれに関連する中央のポーズアンカー。
   */
  private final Map<Integer, Pair<AugmentedImage, Anchor>> augmentedImageMap = new HashMap<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    surfaceView = findViewById(R.id.surfaceview);
    displayRotationHelper = new DisplayRotationHelper(/*context=*/ this);

    // Set up renderer.
    surfaceView.setPreserveEGLContextOnPause(true);
    surfaceView.setEGLContextClientVersion(2);
    surfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0); // Alpha used for plane blending.
    surfaceView.setRenderer(this);
    surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    surfaceView.setWillNotDraw(false);

    fitToScanView = findViewById(R.id.image_view_fit_to_scan);
    glideRequestManager = Glide.with(this);
    glideRequestManager
        .load(Uri.parse("file:///android_asset/fit_to_scan.png"))
        .into(fitToScanView);

    installRequested = false;
    randomfunc();
    count_check();
  }

  @Override
  protected void onDestroy() {
    if (session != null) {
      // Explicitly close ARCore Session to release native resources.
      // Review the API reference for important considerations before calling close() in apps with
      // more complicated lifecycle requirements:
      // https://developers.google.com/ar/reference/java/arcore/reference/com/google/ar/core/Session#close()
      /*
      ネイティブリソースを解放するために、明示的にARCore Sessionをクローズします。より複雑なライフサイクル要件を持つアプリケーションで close() を呼び出す前の重要な注意点については、API リファレンスを参照してください。
https://developers.google.com/ar/reference/java/arcore/reference/com/google/ar/core/Session#close()
       */
      session.close();
      session = null;
    }

    super.onDestroy();
  }

  @Override
  protected void onResume() {
    super.onResume();

    if (session == null) {
      Exception exception = null;
      String message = null;
      try {
        switch (ArCoreApk.getInstance().requestInstall(this, !installRequested)) {
          case INSTALL_REQUESTED:
            installRequested = true;
            return;
          case INSTALLED:
            break;
        }

        // ARCore requires camera permissions to operate. If we did not yet obtain runtime
        // permission on Android M and above, now is a good time to ask the user for it.
        /*
        ARCoreの動作にはカメラの許可が必要です。
        Android M以上でまだランタイムパーミッションを取得していないのであれば、今がチャンスです。
         */
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
          CameraPermissionHelper.requestCameraPermission(this);
          return;
        }

        session = new Session(/* context = */ this);
      } catch (UnavailableArcoreNotInstalledException
          | UnavailableUserDeclinedInstallationException e) {
        message = "Please install ARCore";
        exception = e;
      } catch (UnavailableApkTooOldException e) {
        message = "Please update ARCore";
        exception = e;
      } catch (UnavailableSdkTooOldException e) {
        message = "Please update this app";
        exception = e;
      } catch (Exception e) {
        message = "This device does not support AR";
        exception = e;
      }

      if (message != null) {
        messageSnackbarHelper.showError(this, message);
        Log.e(TAG, "Exception creating session", exception);
        return;
      }

      shouldConfigureSession = true;
    }

    if (shouldConfigureSession) {
      configureSession();
      shouldConfigureSession = false;
    }

    // Note that order matters - see the note in onPause(), the reverse applies here.
    try {
      session.resume();
    } catch (CameraNotAvailableException e) {
      messageSnackbarHelper.showError(this, "Camera not available. Try restarting the app.");
      session = null;
      return;
    }
    surfaceView.onResume();
    displayRotationHelper.onResume();

    fitToScanView.setVisibility(View.VISIBLE);
  }

  @Override
  public void onPause() {
    super.onPause();
    if (session != null) {
      // Note that the order matters - GLSurfaceView is paused first so that it does not try
      // to query the session. If Session is paused before GLSurfaceView, GLSurfaceView may
      // still call session.update() and get a SessionPausedException.
      /*
      GLSurfaceViewは最初に一時停止されるので、セッションに問い合わせをしようとしないことに
      注意してください。GLSurfaceViewよりも先にSessionが一時停止されている場合、
      GLSurfaceViewはsession.update()を呼び出してSessionPausedExceptionを取得する可能性があります。
       */
      displayRotationHelper.onPause();
      surfaceView.onPause();
      session.pause();
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
    super.onRequestPermissionsResult(requestCode, permissions, results);
    if (!CameraPermissionHelper.hasCameraPermission(this)) {
      Toast.makeText(
              this, "Camera permissions are needed to run this application", Toast.LENGTH_LONG)
          .show();
      if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(this)) {
        // Permission denied with checking "Do not ask again".
        CameraPermissionHelper.launchPermissionSettings(this);
      }
      finish();
    }
  }

  @Override
  public void onWindowFocusChanged(boolean hasFocus) {
    super.onWindowFocusChanged(hasFocus);
    FullScreenHelper.setFullScreenOnWindowFocusChanged(this, hasFocus);
  }

  @Override
  public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

    // Prepare the rendering objects. This involves reading shaders, so may throw an IOException.
    //レンダリングオブジェクトを準備します。これはシェーダの読み込みを伴うので、IOExceptionが発生する可能性があります。
    try {
      // Create the texture and pass it to ARCore session to be filled during update().
      //テクスチャを作成して、それをARCoreセッションに渡し、update()で埋めます。
      backgroundRenderer.createOnGlThread(/*context=*/ this);
      augmentedImageRenderer.createOnGlThread(/*context=*/ this);
    } catch (IOException e) {
      Log.e(TAG, "Failed to read an asset file", e);
    }
  }

  @Override
  public void onSurfaceChanged(GL10 gl, int width, int height) {
    displayRotationHelper.onSurfaceChanged(width, height);
    GLES20.glViewport(0, 0, width, height);
  }

  @Override
  public void onDrawFrame(GL10 gl) {
    // Clear screen to notify driver it should not load any pixels from previous frame.
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

    if (session == null) {
      return;
    }
    // Notify ARCore session that the view size changed so that the perspective matrix and
    // the video background can be properly adjusted.
    displayRotationHelper.updateSessionIfNeeded(session);

    try {
      session.setCameraTextureName(backgroundRenderer.getTextureId());

      // Obtain the current frame from ARSession. When the configuration is set to
      // UpdateMode.BLOCKING (it is by default), this will throttle the rendering to the
      // camera framerate.
      Frame frame = session.update();
      Camera camera = frame.getCamera();

      // Keep the screen unlocked while tracking, but allow it to lock when tracking stops.
      trackingStateHelper.updateKeepScreenOnFlag(camera.getTrackingState());

      // If frame is ready, render camera preview image to the GL surface.
      backgroundRenderer.draw(frame);

      // Get projection matrix.
      float[] projmtx = new float[16];
      camera.getProjectionMatrix(projmtx, 0, 0.1f, 100.0f);

      // Get camera matrix and draw.
      float[] viewmtx = new float[16];
      camera.getViewMatrix(viewmtx, 0);

      // Compute lighting from average intensity of the image.
      final float[] colorCorrectionRgba = new float[4];
      frame.getLightEstimate().getColorCorrection(colorCorrectionRgba, 0);

      // Visualize augmented images.
      drawAugmentedImages(frame, projmtx, viewmtx, colorCorrectionRgba);
    } catch (Throwable t) {
      // Avoid crashing the application due to unhandled exceptions.
      Log.e(TAG, "Exception on the OpenGL thread", t);
    }
  }

  private void configureSession() {
    Config config = new Config(session);
    config.setFocusMode(Config.FocusMode.AUTO);
    if (!setupAugmentedImageDatabase(config)) {
      messageSnackbarHelper.showError(this, "Could not setup augmented image database");
    }
    session.configure(config);
  }

  private void drawAugmentedImages(
      Frame frame, float[] projmtx, float[] viewmtx, float[] colorCorrectionRgba) {
    Collection<AugmentedImage> updatedAugmentedImages =
        frame.getUpdatedTrackables(AugmentedImage.class);

    // Iterate to update augmentedImageMap, remove elements we cannot draw.
    //augmentedImageMapの更新を繰り返し、描画できない要素を削除します。
    for (AugmentedImage augmentedImage : updatedAugmentedImages) {
      switch (augmentedImage.getTrackingState()) {
        case PAUSED:
          // When an image is in PAUSED state, but the camera is not PAUSED, it has been detected,
          // but not yet tracked.
          //画像がPAUSED状態にあるが、カメラがPAUSED状態ではない場合、
          // 画像は検出されているが、まだ追跡されていない。
          String text = String.format("Detected Image %d", augmentedImage.getIndex());
          messageSnackbarHelper.showMessage(this, text);
          break;

        case TRACKING:
          // Have to switch to UI Thread to update View.
          //UIスレッドに切り替えてViewを更新する必要があります。
          this.runOnUiThread(
              new Runnable() {
                @Override
                public void run() {
                  fitToScanView.setVisibility(View.GONE);
                }
              });

          // Create a new anchor for newly found images.
          //新しく見つけた画像のアンカーを作成します。
          if (!augmentedImageMap.containsKey(augmentedImage.getIndex())) {
            Anchor centerPoseAnchor = augmentedImage.createAnchor(augmentedImage.getCenterPose());
            augmentedImageMap.put(
                augmentedImage.getIndex(), Pair.create(augmentedImage, centerPoseAnchor));
          }
          break;

        case STOPPED:
          augmentedImageMap.remove(augmentedImage.getIndex());
          break;

        default:
          break;
      }
    }

    // Draw all images in augmentedImageMap
    //全ての画像を augmentedImageMap で描画
    for (Pair<AugmentedImage, Anchor> pair : augmentedImageMap.values()) {
      AugmentedImage augmentedImage = pair.first;
      Anchor centerAnchor = augmentedImageMap.get(augmentedImage.getIndex()).second;
      switch (augmentedImage.getTrackingState()) {
        case TRACKING:
          augmentedImageRenderer.draw(
              viewmtx, projmtx, augmentedImage, centerAnchor, colorCorrectionRgba);
          break;
        default:
          break;
      }
    }
  }

  private boolean setupAugmentedImageDatabase(Config config) {
    AugmentedImageDatabase augmentedImageDatabase;

    // There are two ways to configure an AugmentedImageDatabase:
    // 1. Add Bitmap to DB directly
    // 2. Load a pre-built AugmentedImageDatabase
    // Option 2) has
    // * shorter setup time
    // * doesn't require images to be packaged in apk.
    /*
    AugmentedImageDatabaseを設定するには2つの方法があります。
    // ビットマップを直接DBに追加する
    // AugmentedImageDatabaseの読み込み
    // オプション2)は
    // 設定時間を短縮します。
    // apkでパッケージ化されている画像は必要ありません。
     */
    if (useSingleImage) {
      Bitmap augmentedImageBitmap = loadAugmentedImageBitmap();
      if (augmentedImageBitmap == null) {
        return false;
      }

      augmentedImageDatabase = new AugmentedImageDatabase(session);
      augmentedImageDatabase.addImage("image_name", augmentedImageBitmap);
      // If the physical size of the image is known, you can instead use:
      //     augmentedImageDatabase.addImage("image_name", augmentedImageBitmap, widthInMeters);
      // This will improve the initial detection speed. ARCore will still actively estimate the
      // physical size of the image as it is viewed from multiple viewpoints.
      /*
      画像の物理的なサイズがわかっている場合は、次のようにします：
      augmentedImageDatabase.addImage("image_name", augmentedImageBitmap, widthInMeters)。
      これにより、初期検出速度が向上します。
      ARCoreでは、複数の視点から見た場合でも、画像の物理的なサイズを積極的に推定します。
       */
    } else {
      // This is an alternative way to initialize an AugmentedImageDatabase instance,
      // load a pre-existing augmented image database.
      /*
      これは AugmentedImageDatabase インスタンスを初期化する別の方法です。
      既存の拡張画像データベースをロードします。
       */
      try (InputStream is = getAssets().open("sample_database.imgdb")) {
        augmentedImageDatabase = AugmentedImageDatabase.deserialize(session, is);
      } catch (IOException e) {
        Log.e(TAG, "IO exception loading augmented image database.", e);
        return false;
      }
    }

    config.setAugmentedImageDatabase(augmentedImageDatabase);
    return true;
  }

  private Bitmap loadAugmentedImageBitmap() {
    try (InputStream is = getAssets().open("test2.png")) {
      return BitmapFactory.decodeStream(is);
    } catch (IOException e) {
      Log.e(TAG, "IO exception loading augmented image bitmap.", e);
    }
    return null;
  }
  public int rand = random_number;
  public void randomfunc(){
  }

  public void onClick(View v){//次の画面へ遷移するボタン
    Intent intent = new Intent(this,SubActivity.class);
    intent.putExtra("kekka",rand);
    startActivity(intent);
    findViewById(R.id.button5).setVisibility(View.GONE);//次回まで使えなくするため、待機画面表示
    findViewById(R.id.button).setVisibility(View.GONE);
  }
  public void startButton(View v){//初期画面の非表示
    //おみくじスタートボタンを押下すると、初期画面が非表示になりカメラの画面が表れます。
    findViewById(R.id.button).setVisibility(View.VISIBLE);
    findViewById(R.id.start_layout).setVisibility(View.GONE);
  }

  public void method_random() {
    //string score = SessionConfig.AugmentedImageDatabase[0].Quality;
  }
  public void count_check() {
    //一度おみくじ行ったらtest2.txtに時間を出力、２回目以降起動時にtest2.txtを確認して、
    // 時間が出力されていたら起動画面の「おみくじスタート」ボタンを非表示にしてます。
    //仕組みとして完成してないですが、放送予定時間を登録したデータベースと照合してボタンの表示非表示を行うのもいいなと思いました。
    String str1 = "";
    int check_num = 0;
    try{
      FileInputStream in = openFileInput( "test2.txt" );
      BufferedReader reader = new BufferedReader( new InputStreamReader( in , "UTF-8") );

      String tmp;
      while( (tmp = reader.readLine()) != null ){
        str1 = str1 + tmp +"\n";
      }
      System.out.println("str1::"+str1);
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
      Date nextTV_date = sdf.parse(str1);
      String check_a = "A\n";
      if(str1.equals(check_a)){
        //System.out.println("ここまではきてる?");
        //findViewById(R.id.button5).setVisibility(View.GONE);
      }
      Date d = new Date();
      SimpleDateFormat d1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
      d1.format(d);
      if (nextTV_date.after(d)){
        System.out.println("ここまではきてる!");
        findViewById(R.id.button5).setVisibility(View.GONE);
      }

      reader.close();
    }catch(IOException | ParseException e ){
      System.out.println("str1:"+str1);
      e.printStackTrace();
    }

  }

}
