package com.unity3d.ads.test.instrumentation.services.ads.operation;

import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsLoadOptions;
import com.unity3d.ads.test.TestUtilities;
import com.unity3d.services.ads.operation.load.ILoadModule;
import com.unity3d.services.ads.operation.load.LoadOperationState;
import com.unity3d.services.ads.operation.load.LoadModuleDecoratorInitializationBuffer;
import com.unity3d.services.core.configuration.Configuration;
import com.unity3d.services.core.configuration.IInitializationNotificationCenter;
import com.unity3d.services.core.properties.SdkProperties;
import com.unity3d.services.core.webview.bridge.IWebViewBridgeInvoker;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

public class LoadModuleDecoratorInitializationBufferTests {
	private static String testPlacementId = "TestPlacementId";
	private static int uiThreadDelay = 50;

	private ILoadModule loadModuleMock = mock(ILoadModule.class);
	private IInitializationNotificationCenter initializationNotificationCenterMock;
	private IWebViewBridgeInvoker webViewBridgeInvokerMock;
	private LoadOperationState loadOperationStateMock;

	@Before
	public void beforeEachTest() {
		loadModuleMock = mock(ILoadModule.class);
		initializationNotificationCenterMock = mock(IInitializationNotificationCenter.class);
		webViewBridgeInvokerMock = mock(IWebViewBridgeInvoker.class);
		loadOperationStateMock = mock(LoadOperationState.class);
	}

	@Test
	public void executeAdOperationCallsLoadModuleExecuteAdOperationWhenSdkIsInitialized() {
		LoadModuleDecoratorInitializationBuffer loadModuleInitBuffer = new LoadModuleDecoratorInitializationBuffer(loadModuleMock, initializationNotificationCenterMock);
		SdkProperties.setInitializeState(SdkProperties.InitializationState.INITIALIZED_SUCCESSFULLY);

		loadModuleInitBuffer.executeAdOperation(webViewBridgeInvokerMock, loadOperationStateMock);

		Mockito.verify(loadModuleMock, times(1)).executeAdOperation(webViewBridgeInvokerMock, loadOperationStateMock);
	}

	@Test
	public void executeAdOperationCallsListenerOnUnityAdsFailedToLoadWhenInitHasFailed() {
		IUnityAdsLoadListener loadListenerMock = mock(IUnityAdsLoadListener.class);
		LoadOperationState loadOperationState = new LoadOperationState(testPlacementId, loadListenerMock, new UnityAdsLoadOptions(), new Configuration());
		LoadModuleDecoratorInitializationBuffer loadModuleInitBuffer = new LoadModuleDecoratorInitializationBuffer(loadModuleMock, initializationNotificationCenterMock);
		SdkProperties.setInitializeState(SdkProperties.InitializationState.INITIALIZED_FAILED);

		loadModuleInitBuffer.executeAdOperation(webViewBridgeInvokerMock, loadOperationState);
		TestUtilities.SleepCurrentThread(uiThreadDelay);

		Mockito.verify(loadListenerMock, times(1)).onUnityAdsFailedToLoad(testPlacementId, UnityAds.UnityAdsLoadError.INITIALIZE_FAILED, "[UnityAds] SDK Initialization Failed");
	}

	@Test
	public void executeAdOperationCallsInitNotificationCenterWhenSdkIsInitializing() {
		LoadModuleDecoratorInitializationBuffer loadModuleInitBuffer = new LoadModuleDecoratorInitializationBuffer(loadModuleMock, initializationNotificationCenterMock);
		SdkProperties.setInitializeState(SdkProperties.InitializationState.INITIALIZING);

		loadModuleInitBuffer.executeAdOperation(webViewBridgeInvokerMock, loadOperationStateMock);

		Mockito.verify(initializationNotificationCenterMock, times(1)).addListener(loadModuleInitBuffer);
	}

	@Test
	public void loadModuleExecuteAdOperationIsCalledWhenOnSdkInitializedIsCalledAfterCallingExecuteAdOperation() {
		LoadModuleDecoratorInitializationBuffer loadModuleInitBuffer = new LoadModuleDecoratorInitializationBuffer(loadModuleMock, initializationNotificationCenterMock);
		SdkProperties.setInitializeState(SdkProperties.InitializationState.INITIALIZING);

		loadModuleInitBuffer.executeAdOperation(webViewBridgeInvokerMock, loadOperationStateMock);
		Mockito.verify(initializationNotificationCenterMock, times(1)).addListener(loadModuleInitBuffer);

		loadModuleInitBuffer.onSdkInitialized();
		Mockito.verify(loadModuleMock, times(1)).executeAdOperation(webViewBridgeInvokerMock, loadOperationStateMock);
	}

	@Test
	public void loadModuleExecuteAdOperationIsNotCalledWhenOnSdkInitializedIsCalledAfterCallingExecuteAdOperationAndLoadEventStateNull() {
		LoadModuleDecoratorInitializationBuffer loadModuleInitBuffer = new LoadModuleDecoratorInitializationBuffer(loadModuleMock, initializationNotificationCenterMock);
		SdkProperties.setInitializeState(SdkProperties.InitializationState.INITIALIZING);

		loadModuleInitBuffer.executeAdOperation(webViewBridgeInvokerMock, null);
		Mockito.verify(initializationNotificationCenterMock, times(1)).addListener(loadModuleInitBuffer);

		loadModuleInitBuffer.onSdkInitialized();
		Mockito.verify(loadModuleMock, times(0)).executeAdOperation(webViewBridgeInvokerMock, loadOperationStateMock);
	}

	@Test
	public void onUnityAdsFailedToLoadIsCalledWhenOnSdkInitializationFailedIsCalledAfterCallingExecuteAdOperation() {
		IUnityAdsLoadListener loadListenerMock = mock(IUnityAdsLoadListener.class);
		LoadOperationState loadOperationStateMock = new LoadOperationState(testPlacementId, loadListenerMock, new UnityAdsLoadOptions(), new Configuration());
		LoadModuleDecoratorInitializationBuffer loadModuleInitBuffer = new LoadModuleDecoratorInitializationBuffer(loadModuleMock, initializationNotificationCenterMock);
		SdkProperties.setInitializeState(SdkProperties.InitializationState.INITIALIZING);

		loadModuleInitBuffer.executeAdOperation(webViewBridgeInvokerMock, loadOperationStateMock);
		TestUtilities.SleepCurrentThread(uiThreadDelay);
		Mockito.verify(initializationNotificationCenterMock, times(1)).addListener(loadModuleInitBuffer);

		loadModuleInitBuffer.onSdkInitializationFailed("UntestableMessage", 0);
		TestUtilities.SleepCurrentThread(uiThreadDelay);
		Mockito.verify(loadListenerMock, times(1)).onUnityAdsFailedToLoad(testPlacementId, UnityAds.UnityAdsLoadError.INITIALIZE_FAILED, "[UnityAds] SDK Initialization Failure");
	}
}
