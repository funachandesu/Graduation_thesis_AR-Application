AR omikuji application developed for my graduation thesis.
An application for Android using ARCore.
By recognizing the marker on the TV screen, the result of the omikuji will be displayed in 3D characters.
Users can get a LINE coupon by sharing the result of the fortune on their LINE timeline. (The coupon can be a product of a sponsor related to the TV program.)
The aim is to increase viewership and improve the management of programs produced by local commercial broadcasters.
A demo video has been uploaded. (demo.mp4)

・卒業論文で開発した AR おみくじアプリ.
・このアプリは Google の ARCore、Augmented Images のサンプルを元に構築してます。
・ARCore を利用した Android 版アプリ.
・テレビ画面に設置されたマーカーを認識することで、おみくじの結果が 3D 文字で表示される.
・ユーザーはおみくじの結果を LINE のタイムラインで共有することで,LINE クーポンが入手できる仕組み.(クーポンはテレビ番組に関連したスポンサーの商品など)
・上記の仕組みによって,地方民放局の制作した番組の視聴率増加,経営改善につながるのではないかという狙い.
・demo 動画もアップしています.(demo.mp4)

実装機能
・初期画面構築
・乱数生成、結果の画面間共有
・3D 文字アニメーション表示(OpenGL は ObjectRendrer.java 内)
・結果表示画面構築
・クーポン発行画面構築
