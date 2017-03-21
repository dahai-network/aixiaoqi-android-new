package de.blinkt.openvpn.model;

import android.content.Intent;

public class IntentEntity {
		private Intent authorityIntent;
		private Intent shadeIntent;

		public IntentEntity(Intent authorityIntent, Intent shadeIntent) {
			this.authorityIntent = authorityIntent;
			this.shadeIntent = shadeIntent;
		}

		public Intent getAuthorityIntent() {
			return authorityIntent;
		}

		public void setAuthorityIntent(Intent authorityIntent) {
			this.authorityIntent = authorityIntent;
		}

		public Intent getShadeIntent() {
			return shadeIntent;
		}

		public void setShadeIntent(Intent shadeIntent) {
			this.shadeIntent = shadeIntent;
		}
	}