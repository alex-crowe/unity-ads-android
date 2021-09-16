package com.unity3d.services.ads.operation;

import android.os.ConditionVariable;

import com.unity3d.services.core.configuration.Configuration;
import com.unity3d.services.core.webview.bridge.IWebViewSharedObject;

import java.util.UUID;

public class OperationState  implements IWebViewSharedObject {
	private static String _emptyPlacementId = "";

	public String id;
	public String placementId;
	public Configuration configuration;
	public ConditionVariable timeoutCV;

	public OperationState(String placementId, Configuration configuration) {
		this.placementId = placementId == null ? _emptyPlacementId : placementId;
		this.configuration = configuration;
		this.timeoutCV = new ConditionVariable();
		id = UUID.randomUUID().toString();
	}

	@Override
	public String getId() {
		return id;
	}
}
