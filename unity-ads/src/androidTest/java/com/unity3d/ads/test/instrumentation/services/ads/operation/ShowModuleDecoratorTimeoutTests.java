package com.unity3d.ads.test.instrumentation.services.ads.operation;

import androidx.test.rule.ActivityTestRule;

import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsShowOptions;
import com.unity3d.ads.test.TestUtilities;
import com.unity3d.ads.test.instrumentation.InstrumentationTestActivity;
import com.unity3d.services.ads.operation.show.IShowModule;
import com.unity3d.services.ads.operation.show.ShowModule;
import com.unity3d.services.ads.operation.show.ShowModuleDecoratorTimeout;
import com.unity3d.services.ads.operation.show.ShowOperationState;
import com.unity3d.services.core.request.ISDKMetricSender;
import com.unity3d.services.core.webview.bridge.CallbackStatus;
import com.unity3d.services.core.webview.bridge.IWebViewBridgeInvoker;
import com.unity3d.services.core.webview.bridge.invocation.WebViewBridgeInvocation;
import com.unity3d.services.core.webview.bridge.invocation.WebViewBridgeInvocationRunnable;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.lang.reflect.Method;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

public class ShowModuleDecoratorTimeoutTests {
	private static String placementId = "TestPlacementId";
	private static UnityAdsShowOptions showOptions = new UnityAdsShowOptions();
	private ISDKMetricSender sdkMetricSender;


	private static int showTimeout = 50;
	private static int uiThreadDelay = 150;

	private IUnityAdsShowListener showListenerMock;
	private IShowModule showModule;

	@Rule
	public final ActivityTestRule<InstrumentationTestActivity> _activityRule = new ActivityTestRule<>(InstrumentationTestActivity.class);

	@Before
	public void beforeEachTest() {
		showListenerMock = mock(IUnityAdsShowListener.class);
		sdkMetricSender = mock(ISDKMetricSender.class);
		// We need a real instance since ShowModule will create the Operation object (which holds the State with Listener ID)
		showModule = new ShowModule(sdkMetricSender);
	}

	@After
	public void afterEachTest() {
		//Allow for any timeout threads to complete before starting the next test to prevent inaccurate mock counts
		TestUtilities.SleepCurrentThread(showTimeout);
	}

	@Test
	public void testShowModuleDecoratorTimeout() {
		ShowModuleDecoratorTimeout showModuleDecoratorTimeout = new ShowModuleDecoratorTimeout(showModule);
		ShowOperationState showOperationState = new ShowOperationState(placementId, showListenerMock, _activityRule.getActivity(), showOptions, OperationTestUtilities.createConfigurationWithShowTimeout(showTimeout));
		showModuleDecoratorTimeout.executeAdOperation(mock(IWebViewBridgeInvoker.class), showOperationState);
		TestUtilities.SleepCurrentThread(uiThreadDelay);
		Mockito.verify(showListenerMock, times(1)).onUnityAdsShowFailure(placementId, UnityAds.UnityAdsShowError.INTERNAL_ERROR, "[UnityAds] Timeout while trying to show TestPlacementId");
	}

	@Test
	public void testShowModuleDecoratorShowConsentNoTimeout() {
		ShowModuleDecoratorTimeout showModuleDecoratorTimeout = new ShowModuleDecoratorTimeout(showModule);
		ShowOperationState showOperationState = new ShowOperationState(placementId, showListenerMock, _activityRule.getActivity(), showOptions, OperationTestUtilities.createConfigurationWithShowTimeout(showTimeout));
		IWebViewBridgeInvoker webViewBridgeInvoker = mock(IWebViewBridgeInvoker.class);
		when(webViewBridgeInvoker.invokeMethod(anyString(), anyString(), any(Method.class), any())).thenReturn(true);

		showModuleDecoratorTimeout.executeAdOperation(webViewBridgeInvoker, showOperationState);
		WebViewBridgeInvocationRunnable.onInvocationComplete(CallbackStatus.OK);
		showModuleDecoratorTimeout.onUnityAdsShowConsent(showOperationState.getId());
		TestUtilities.SleepCurrentThread(uiThreadDelay);
		Mockito.verify(showListenerMock, times(0)).onUnityAdsShowFailure(anyString(), any(UnityAds.UnityAdsShowError.class), anyString());
	}
}
