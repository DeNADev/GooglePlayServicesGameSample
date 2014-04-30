/**
 * The MIT License (MIT)
 * Copyright (c) 2014 DeNA Co., Ltd.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.dena.googleplay.services.game.helper;

import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;

/**
 * Google Play Services に Game Client として接続するためのヘルパークラスのインターフェースです。
 * 
 */
public interface GamesClientHelper {

	/**
	 * ヘルパークラスからの通知を受け取るリスナーです。
	 * 
	 */
	interface GamesClientHelperListener {
		/**
		 * {@link GamesClientHelper#connect()} が呼ばれて接続が成功した場合に呼び出されます。<br>
		 * Google Play Services のライブラリーの仕様の関係で、
		 * {@link GamesClientHelper#connect()} が呼ばれても通知されない場合はあります。<br>
		 * 接続状態を確実に判別するためには、{@link GamesClientHelper#isConnected()} を利用してください。
		 * 
		 * @param connectionHint
		 */
		void onConnected(Bundle connectionHint);

		/**
		 * {@link GamesClientHelper#connect()} が呼ばれて接続が失敗した場合に呼び出されます。<br>
		 * Google Play Services のライブラリーの仕様の関係で、
		 * {@link GamesClientHelper#connect()} が呼ばれても通知されない場合はあります。<br>
		 * 特に自動リトライなどはせず、ユーザーへエラーになった旨を表示し、{@link GamesClientHelper#connect()}
		 * からやり直してください。
		 * 
		 * @param result
		 *            コネクション結果
		 */
		void onError(ConnectionResult result);
	}

	/**
	 * Google ログイン完了の ActivityResult の RequestCode です。
	 */
	int REQUEST_START_RESOLUTION_FOR_RESULT = 10001;

	/**
	 * Google ログイン失敗時にエラーダイアログの ActivityResult の RequestCode です。
	 */
	int REQUEST_GET_ERROR_DIALOG = 10002;

	/**
	 * リーダーボードの UI を閉じた際の ActivityResult の RequestCode です。
	 */
	int REQUEST_LEADERBOARD = 10003;

	/**
	 * アチーブメントの UI を閉じた際の ActivityResult の RequestCode です。
	 */
	int REQUEST_ACHIEVEMENTS = 10004;

	/**
	 * Google Play Services に Games Client として接続します。<br>
	 * このメソッドを呼ぶと、必要な場合に認可を求める Google Play Services の UI が表示されます。<br>
	 * 接続に成功した場合は、{@link GamesClientHelperListener#onConnected(Bundle)}
	 * が呼び出されます。<br>
	 * 接続に失敗した場合は、{@link GamesClientHelperListener#onError(ConnectionResult)}
	 * が呼び出されます。<br>
	 * Google Play Servicesを利用するために Google アカウントを選択するUIが表示された場合は、<br>
	 * 接続処理は中断されてしまうため Activity の onActivityResult で再度このメソッドをコールする必要があります。
	 */
	void connect();

	/**
	 * 接続中のコネクションを切断します。 <br>
	 * Activity が停止する際に接続中のコネクションを切断する用途で使用します。
	 */
	void disconnect();

	/**
	 * Google Play Services に Games Client として接続済か否かを返します。
	 * 
	 * @return 接続済の場合は true
	 */
	boolean isConnected();

	/**
	 * Google Play Services に Games Client として接続処理中か否かを返します。
	 * 
	 * @return 接続処理中の場合は true
	 */
	boolean isConnecting();

	/**
	 * アチーブメントの UI を開きます。 <br>
	 * 内部的には Intent が発行され、Google Play Services の Activity が呼び出されます。
	 * 
	 * @throws GamesClientNotConnectedException
	 *             Google Play Services に Games Client として接続が完了していない場合
	 */
	void openAchievementUI() throws GamesClientNotConnectedException;

	/**
	 * アチーブメントの ID を指定して、対象のアチーブメントを解放します。
	 * 
	 * @param id
	 *            アチーブメントの ID (Google Play Developer Console で発行される)
	 * @throws GamesClientNotConnectedException
	 *             Google Play Services に Games Client として接続が完了していない場合
	 */
	void unlockAchievement(String id) throws GamesClientNotConnectedException;

	/**
	 * アチーブメントの ID を指定して、対象のアチーブメントのステップを積みます。
	 * 
	 * @param id
	 *            アチーブメントの ID (Google Play Developer Console で発行される)
	 * @param numSteps
	 *            ステップ数 (Google Play Developer Console で必要なステップ数を設定可能)
	 * @throws GamesClientNotConnectedException
	 *             Google Play Services に Games Client として接続が完了していない場合
	 */
	void incrementAchievement(String id, int numSteps)
			throws GamesClientNotConnectedException;

	/**
	 * すべてのリーダーボードを表示する UI を開きます。<br>
	 * 内部的には Intent が発行され、Google Play Services の Activity が呼び出されます。
	 * 
	 * @throws GamesClientNotConnectedException
	 *             Google Play Services に Games Client として接続が完了していない場合
	 */
	void openAllLeaderboardsUI() throws GamesClientNotConnectedException;

	/**
	 * リーダーボード ID を指定して、対象のリーダボードの UI を開きます。<br>
	 * 内部的には Intent が発行され、Google Play Services の Activity が呼び出されます。
	 * 
	 * @param leaderboardId
	 *            リーダーボード ID (Google Play Developer Console で発行される)
	 * @throws GamesClientNotConnectedException
	 *             Google Play Services に Games Client として接続が完了していない場合
	 */
	void openLeaderboardUI(String leaderboardId)
			throws GamesClientNotConnectedException;

	/**
	 * リーダーボード ID を指定して、対象のリーダーボードのスコアを登録します。
	 * 
	 * @param leaderboardId
	 *            リーダーボード ID (Google Play Developer Console で発行される)
	 * @param score
	 *            スコア（Google Play Developer Console で定義したスコア）
	 * @throws GamesClientNotConnectedException
	 *             Google Play Services に Games Client として接続が完了していない場合
	 */
	void submitScore(String leaderboardId, long score)
			throws GamesClientNotConnectedException;

}
