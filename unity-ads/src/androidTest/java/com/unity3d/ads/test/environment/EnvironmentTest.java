package com.unity3d.ads.test.environment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import android.telephony.TelephonyManager;

import com.unity3d.services.ads.adunit.AdUnitActivity;
import com.unity3d.services.core.request.WebRequest;
import com.unity3d.ads.test.TestUtilities;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class EnvironmentTest extends ActivityTestRule<AdUnitActivity> {

	public EnvironmentTest () {
		super(AdUnitActivity.class);
	}

	@Test
	public void testWifiEnabled () {
		ConnectivityManager mConnectivity;
		boolean isWifi = false;

		mConnectivity = (ConnectivityManager) InstrumentationRegistry.getInstrumentation().getTargetContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		TelephonyManager mTelephony;
		mTelephony = (TelephonyManager)InstrumentationRegistry.getInstrumentation().getTargetContext().getSystemService(Context.TELEPHONY_SERVICE);

		// Skip if no connection, or background data disabled
		if (mConnectivity != null) {
			NetworkInfo info = mConnectivity.getActiveNetworkInfo();
			int netType = info.getType();
			if (netType == ConnectivityManager.TYPE_WIFI && info.isConnected()) {
				isWifi = true;
			}

			if (!mConnectivity.getBackgroundDataSetting() || !mConnectivity.getActiveNetworkInfo().isConnected() || mTelephony == null) {
				isWifi = false;
			}
		}

		assertTrue("Device doesn't have WiFi", isWifi);
	}

	@Test
	public void testRequest () throws Exception {
		WebRequest request = new WebRequest(TestUtilities.getTestServerAddress() + "/testconfig.json", "GET", null);
		String data = request.makeRequest();

		assertNotNull("Data should not be null", data);
		assertFalse("Data should not be empty", data.isEmpty());
		assertTrue("Data doesn't have enough data", data.length() > 10);
	}

	@Test
	public void testStorageAccess () {
		File cacheDir = getCacheDirectory(InstrumentationRegistry.getInstrumentation().getTargetContext());
		assertNotNull("Cache directory is null", cacheDir);
		assertTrue("Cannot read cache directory: " + cacheDir, cacheDir.canRead());
		assertTrue("Cannot write to cache directory: " + cacheDir, cacheDir.canWrite());
	}

	@Test
	public void testStorageSpace () {
		File cacheDir = getCacheDirectory(InstrumentationRegistry.getInstrumentation().getTargetContext());
		assertNotNull("Cache directory is null", cacheDir);
		assertTrue("Target cache (" + cacheDir + ") doesn't have enough space left", cacheDir.getFreeSpace() > 2000000);
		assertTrue("Cannot read cache directory: " + cacheDir, cacheDir.canRead());
		assertTrue("Cannot write to cache directory: " + cacheDir, cacheDir.canWrite());
	}

	public File getCacheDirectory (Context context) {
		File filesDir = context.getFilesDir();
		File cacheDirectory = null;
		// If device storage is full, filesDir might be null
		if (filesDir != null) {
			cacheDirectory = new File(filesDir.getPath());
		}

		if (Build.VERSION.SDK_INT > 18) {
			File externalCacheFile = context.getExternalCacheDir();
			// If device storage is full, external cachedir might be null
			if (externalCacheFile != null) {
				String absoluteCachePath;
				absoluteCachePath = externalCacheFile.getAbsolutePath();
				cacheDirectory = new File(absoluteCachePath);
				if (cacheDirectory.mkdirs()) {
					// Successfully created cache
				}
			}
		}

		if (cacheDirectory == null || !cacheDirectory.isDirectory()) {
			// Creating cache dir failed
		}

		return cacheDirectory;
	}
}
