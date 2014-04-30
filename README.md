GooglePlayServicesGameSample
============================

こちらは、Google Play Game Services を利用するサンプルアプリです。
あくまでもサンプルですので、ご参考程度に利用して下さい。

前提条件
-------
下記URLを参照しGoogle Play Services SDKがセットアップされていること。
 
 https://developer.android.com/google/play-services/setup.html

利用方法
-------
1. GooglePlayServicesGameSampleをcloneして、Eclipseプロジェクトをインポートしてください。
 - GooglePlayServicesGameHelper: GooglePlayServiceのライブラリーを簡単に呼ぶ出すヘルパー
 - GooglePlayServicesGameSample: 上記Helperを利用したゲームサンプル
 
2. 依存関係は問題ないはずですが、各プロジェクトのAndroid設定で、Libraryが正しくリンクされているか確認してください。
 - GooglePlayServiceGameHelperは、google-play-services_libを参照
 - GooglePlayServicesGameSampleは、GooglePlayServicesGameHelperとgoogle-play-services_libを参照
 
3. GooglePlayServicesGameSample/res/values/strings.xmlの各種IDを自分のものに書き換えてください。
 
 ```
 <?xml version="1.0" encoding="utf-8"?>
 <resources>
    <string name="app_name">GooglePlayServicesGameSample</string>
    <string name="app_id">your app id</string>
    <string name="achievement_bronze">your achievement id</string>
    <string name="achievement_silver">your achievement id</string>
    <string name="achievement_gold">your achievement id</string>
    <string name="leaderboard_score">your leaderboard id</string>
 </resources>
 ```

4. GooglePlayServicesGameSampleをAndroid端末上で実行してください。登録したfingerprintと同じkeystoreで署名する必要あり、開発時は.android/debug.keystoreを使う等して下さい。
 
 参考：https://developers.google.com/games/services/android/quickstart#step_2_set_up_the_game_in_the_dev_console

補足
---
- GooglePlayServicesGameSample/AndroidManifest.xmlのようにmeta-dataの設定をお忘れなく行ってください。
 https://developer.android.com/google/play-services/setup.html#Setup
- GooglePlayServicesGameSample/proguard-project.txtのようにproguardの設定をお忘れなく行って下さい。
 https://developer.android.com/google/play-services/setup.html#Proguard
- サンプルではXMLレイアウトを使って、サインインボタンを用意していますが、ガイドラインに従えば独自で配置できます。
 https://developers.google.com/games/services/branding-guidelines

外部ファイルのライセンス
------------------------
下記3つのファイルは、[Google](https://developers.google.com/readme/policies/)が[こちら](https://developers.google.com/games/services/downloads/index)で配布しているもので、[Creative Commons 3.0 Attribution License](http://creativecommons.org/licenses/by/3.0/)によって提供されています。

The following image files are reproduced from work created and [shared by Google](https://developers.google.com/readme/policies/) and used according to terms described in the [Creative Commons 3.0 Attribution License](http://creativecommons.org/licenses/by/3.0/).

- eclipse/GooglePlayServicesGameSample/res/drawable/games_controller.png
- eclipse/GooglePlayServicesGameSample/res/drawable/games_controller_grey.png
- eclipse/GooglePlayServicesGameSample/res/drawable/games_controller_white.png

以上。
