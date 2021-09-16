package com.unity3d.ads.test.legacy;

import android.app.Activity;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.Looper;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.properties.AdsProperties;
import com.unity3d.services.core.properties.ClientProperties;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class ClientPropertiesTest {

	@BeforeClass
	public static void prepareTests () {
		ClientProperties.setApplicationContext(InstrumentationRegistry.getInstrumentation().getTargetContext());
	}

	@Before
	public void resetTests () {
		ClientProperties.setApplicationContext(null);
		ClientProperties.setActivity(null);
		ClientProperties.setGameId(null);
		this.removeListeners();
	}

	private void removeListeners() {
		for (IUnityAdsListener listener : AdsProperties.getListeners()) {
			AdsProperties.removeListener(listener);
		}
	}

	@Test
	public void testSetApplicationContext () {
		ClientProperties.setApplicationContext(InstrumentationRegistry.getInstrumentation().getTargetContext());
		assertEquals("Application context was not the same as expected", InstrumentationRegistry.getInstrumentation().getTargetContext(), ClientProperties.getApplicationContext());
	}

	@Test
	public void testSetActivity () throws InterruptedException {
		final ConditionVariable cv = new ConditionVariable();

		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(new Runnable() {
			@Override
			public void run() {
				MockActivity act = new MockActivity();
				ClientProperties.setActivity(act);
				cv.open();
			}
		});

		boolean success = cv.block(10000);
		assertTrue("ConditionVariable was not opened!", success);
		assertNotNull("Activity should not be null after setting it", ClientProperties.getActivity());
	}

	@Test
	public void testSetGameId ()  {
		String gameId = "TestGameId";
		ClientProperties.setGameId(gameId);
		assertEquals("GameID was not the same as expected", gameId, ClientProperties.getGameId());
	}

	@Test
	public void testSetListener ()  {
		IUnityAdsListener listener = new IUnityAdsListener() {
			@Override
			public void onUnityAdsReady(String placementId) {
				// Testing, do nothing
			}
			@Override
			public void onUnityAdsStart(String placementId) {
				// Testing, do nothing
			}
			@Override
			public void onUnityAdsFinish(String placementId, UnityAds.FinishState result) {
				// Testing, do nothing
			}
			@Override
			public void onUnityAdsError(UnityAds.UnityAdsError error, String message) {
				// Testing, do nothing
			}
		};

		AdsProperties.addListener(listener);
		assertEquals("Listener was not the same as expected", new HashSet<>(Arrays.asList(listener)), AdsProperties.getListeners());
	}

	public class MockActivity extends Activity {
		public MockActivity() {

		}
	}
}
