package org.muellerpete.pebblebmc.ui;

import org.muellerpete.pebblebmc.R;

import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences_top);
		if (0 != (this.getActivity().getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE)) {
			addPreferencesFromResource(R.xml.debug);
		}
	}
}