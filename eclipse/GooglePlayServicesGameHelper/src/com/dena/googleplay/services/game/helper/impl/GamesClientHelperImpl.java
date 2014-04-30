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

package com.dena.googleplay.services.game.helper.impl;

import android.app.Activity;
import android.app.Dialog;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.util.Log;

import com.dena.googleplay.services.game.helper.GamesClientHelper;
import com.dena.googleplay.services.game.helper.GamesClientNotConnectedException;
import com.google.android.gms.appstate.AppStateManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;

/**
 * Google Play Services に Game Client として接続するためのヘルパークラスの実装クラスです。<br>
 * 
 */
public class GamesClientHelperImpl implements GamesClientHelper,
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener {

	protected GoogleApiClient googleApiClient = null;

	protected GoogleApiClient.ApiOptions gamesApiOptions = null;
	protected GoogleApiClient.ApiOptions plusApiOptions = null;
	protected GoogleApiClient.ApiOptions appStateApiOptions = null;

	protected Activity activity;

	protected GamesClientHelperListener listener;

	public GamesClientHelperImpl(Activity activity,
			GamesClientHelperListener listener) {
		this.activity = activity;
		this.listener = listener;

		GoogleApiClient.Builder builder = new GoogleApiClient.Builder(
				this.activity, this, this);

		builder.addApi(Games.API, gamesApiOptions);
		builder.addScope(Games.SCOPE_GAMES);

		builder.addApi(Plus.API, plusApiOptions);
		builder.addScope(Plus.SCOPE_PLUS_LOGIN);

		builder.addApi(AppStateManager.API, appStateApiOptions);
		builder.addScope(AppStateManager.SCOPE_APP_STATE);

		googleApiClient = builder.build();
	}

	@Override
	public synchronized void connect() {
		if (googleApiClient.isConnecting()) {
			return;
		}
		if (googleApiClient.isConnected()) {
			listener.onConnected(null);
			return;
		}

		googleApiClient.connect();
	}

	@Override
	public boolean isConnected() {
		return googleApiClient.isConnected();
	}

	@Override
	public boolean isConnecting() {
		return googleApiClient.isConnecting();
	}

	@Override
	public void disconnect() {
		googleApiClient.disconnect();
	}

	@Override
	public void openAchievementUI() {
		if (!isConnected()) {
			throw new GamesClientNotConnectedException();
		}
		this.activity.startActivityForResult(
				Games.Achievements.getAchievementsIntent(googleApiClient),
				REQUEST_ACHIEVEMENTS);
	}

	@Override
	public void unlockAchievement(String id) {
		if (!isConnected()) {
			throw new GamesClientNotConnectedException();
		}
		Games.Achievements.unlock(googleApiClient, id);
	}

	@Override
	public void incrementAchievement(String id, int numSteps) {
		Games.Achievements.increment(googleApiClient, id, numSteps);
	}

	@Override
	public void openAllLeaderboardsUI() {
		if (!isConnected()) {
			throw new GamesClientNotConnectedException();
		}
		this.activity.startActivityForResult(
				Games.Leaderboards.getAllLeaderboardsIntent(googleApiClient),
				REQUEST_LEADERBOARD);
	}

	@Override
	public void openLeaderboardUI(String leaderboardId) {
		if (!isConnected()) {
			throw new GamesClientNotConnectedException();
		}
		this.activity.startActivityForResult(
				Games.Leaderboards.getLeaderboardIntent(googleApiClient, leaderboardId),
				REQUEST_LEADERBOARD);
	}

	@Override
	public void submitScore(String leaderboardId, long score) {
		if (!isConnected()) {
			throw new GamesClientNotConnectedException();
		}
		Games.Leaderboards.submitScore(googleApiClient, leaderboardId, score);
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		Log.d(this.getClass().getSimpleName(), "onConnected");
		listener.onConnected(connectionHint);
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.d(this.getClass().getSimpleName(), "onConnectionFailed: errorCode="
				+ result.getErrorCode());
		handleConnectionResult(result);
	}

	@Override
	public void onConnectionSuspended(int cause) {
		if (cause == CAUSE_NETWORK_LOST) {
			Log.d(this.getClass().getSimpleName(),
					"onConnectionSuspended: CAUSE_NETWORK_LOST");
		} else if (cause == CAUSE_SERVICE_DISCONNECTED) {
			Log.d(this.getClass().getSimpleName(),
					"onConnectionSuspended: CAUSE_SERVICE_DISCONNECTED");
		}
	}

	private void handleConnectionResult(ConnectionResult result) {
		if (result.hasResolution()) {
			try {
				result.startResolutionForResult(activity,
						REQUEST_START_RESOLUTION_FOR_RESULT);
			} catch (SendIntentException e) {
				Log.e(this.getClass().getSimpleName(),
						"unexpected exception for startResolutionForResult", e);
			}
			return;
		}
		listener.onError(result);
		Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
				result.getErrorCode(), activity, REQUEST_GET_ERROR_DIALOG);
		if (errorDialog != null) {
			errorDialog.show();
		}

	}
}
