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

package com.dena.googleplay.services.game.sample;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.dena.googleplay.services.game.helper.GamesClientHelper;
import com.dena.googleplay.services.game.helper.GamesClientHelper.GamesClientHelperListener;
import com.dena.googleplay.services.game.helper.impl.GamesClientHelperImpl;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.games.GamesActivityResultCodes;

public class MainActivity extends Activity implements OnClickListener, GamesClientHelperListener {

	/**
	 * Google Play Services に Game Client として接続するためのヘルパークラスです。
	 */
	private GamesClientHelper helper;

	/**
	 * Google Play Services に Game Client として接続済みかのアプリケーション側のフラグです。<br>
	 * Activity が消されても残るように static スコープで保持します。<br>
	 * プロセスが終了してもいいようにストレージに書いておいてもいいです。
	 */
	private static boolean isGooglePlayServicesConnected;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(this.getClass().getSimpleName(), "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViewById(R.id.sign_in_button).setOnClickListener(this);
		findViewById(R.id.disconnect).setOnClickListener(this);
		findViewById(R.id.isConnected).setOnClickListener(this);
		findViewById(R.id.isConnecting).setOnClickListener(this);
		findViewById(R.id.openAchievementUI).setOnClickListener(this);
		findViewById(R.id.unlockAchievement).setOnClickListener(this);
		findViewById(R.id.incrementAchievement).setOnClickListener(this);
		findViewById(R.id.openAllLeaderboardsUI).setOnClickListener(this);
		findViewById(R.id.openLeaderboardUI).setOnClickListener(this);
		findViewById(R.id.submitScore).setOnClickListener(this);

		// ヘルパークラスは onCreate でインスタンスを生成する
		helper = new GamesClientHelperImpl(this, this);
	}

	@Override
	protected void onStart() {
		Log.i(this.getClass().getSimpleName(), "onStart");
		super.onStart();
		// もし過去にログインしていたら、セッション切れを防ぐために自動接続する
		if (isGooglePlayServicesConnected) {
			helper.connect();
		}
	}

	@Override
	protected void onStop() {
		Log.i(this.getClass().getSimpleName(), "onStop");
		super.onStop();
		// 接続中にアプリを落とされた場合はコネクションを閉じるのを忘れない
		if (helper.isConnecting()) {
			helper.disconnect();
		}
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.sign_in_button) {
			helper.connect();
			return;
		}
		if (view.getId() == R.id.disconnect) {
			helper.disconnect();
			findViewById(R.id.sign_in_button).setEnabled(true);
			return;
		}
		if (view.getId() == R.id.isConnected) {
			boolean isConnected = helper.isConnected();
			Toast.makeText(this, "isConnected: " + isConnected, Toast.LENGTH_SHORT).show();
			return;
		}
		if (view.getId() == R.id.isConnecting) {
			boolean isConnecting = helper.isConnecting();
			Toast.makeText(this, "isConnecting: " + isConnecting, Toast.LENGTH_SHORT).show();
			return;
		}

		if (!helper.isConnected()) {
			new AlertDialog.Builder(this).setTitle("Sign-in to Google?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					helper.connect();
				}
			}).setNegativeButton("No", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			}).create().show();
			return;
		}

		if (view.getId() == R.id.openAchievementUI) {
			helper.openAchievementUI();
			return;
		}

		if (view.getId() == R.id.unlockAchievement) {
			String id = getString(R.string.achievement_bronze);
			helper.unlockAchievement(id);
			return;
		}

		if (view.getId() == R.id.incrementAchievement) {
			String id = getString(R.string.achievement_silver);
			int numSteps = 1;
			helper.incrementAchievement(id, numSteps);
			return;
		}

		if (view.getId() == R.id.openAchievementUI) {
			helper.openAchievementUI();
			return;
		}
		if (view.getId() == R.id.openAllLeaderboardsUI) {
			helper.openAllLeaderboardsUI();
			return;
		}
		if (view.getId() == R.id.openLeaderboardUI) {
			String leaderboardId = getString(R.string.leaderboard_score);
			helper.openLeaderboardUI(leaderboardId);
			return;
		}
		if (view.getId() == R.id.submitScore) {
			String leaderboardId = getString(R.string.leaderboard_score);
			long score = 100;
			helper.submitScore(leaderboardId, score);
			return;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(this.getClass().getSimpleName(), "onActivityResult: requestCode=" + requestCode + ",resultCode=" + resultCode);
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == GamesClientHelper.REQUEST_START_RESOLUTION_FOR_RESULT) {
			if (resultCode == RESULT_OK) {
				// Google アカウントでのログインが成功すると処理がここにくるので再度connectを呼ぶ、ここは重要
				helper.connect();
			}
		}
		if (requestCode == GamesClientHelper.REQUEST_ACHIEVEMENTS) {
			// アチーブメントの UI から戻った場合にここにくるので必要であれば処理
		}
		if (requestCode == GamesClientHelper.REQUEST_LEADERBOARD) {
			// リーダーボードの UI から戻った場合にここにくるので必要であれば処理
		}

		if((requestCode == GamesClientHelper.REQUEST_ACHIEVEMENTS || 
			requestCode == GamesClientHelper.REQUEST_LEADERBOARD) && 
			resultCode == GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED) {
            if (helper.isConnected()) {
                helper.disconnect();
    			findViewById(R.id.sign_in_button).setEnabled(true);
            }
        }
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		Log.i(this.getClass().getSimpleName(), "onConnected");
		Toast.makeText(this, "onConnected", Toast.LENGTH_SHORT).show();
		isGooglePlayServicesConnected = true;
		findViewById(R.id.sign_in_button).setEnabled(false);
	}

	@Override
	public void onError(ConnectionResult result) {
		Log.e(this.getClass().getSimpleName(), "onError");
		Toast.makeText(this, "onError: ErrorCode" + result.getErrorCode(), Toast.LENGTH_SHORT).show();
	}
}
