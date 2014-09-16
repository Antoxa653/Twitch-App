package com.github.twitch.app.gui;

public final class GuiConstants {
	public static final String START_ACTIVE_16_ICON = "/arrow-green-16.png";
	public static final String START_ACTIVE_18_ICON = "/arrow-green-18.png";
	public static final String START_ACTIVE_32_ICON = "/arrow-green-32.png";
	public static final String START_ACTIVE_34_ICON = "/arrow-green-34.png";
	public static final String START_ACTIVE_64_ICON = "/arrow-green-64.png";
	public static final String START_ACTIVE_66_ICON = "/arrow-green-66.png";
	public static final String START_UNACTIVE_16_ICON = "/arrow-red-16.png";
	public static final String START_UNACTIVE_18_ICON = "/arrow-red-18.png";
	public static final String START_UNACTIVE_32_ICON = "/arrow-red-32.png";
	public static final String START_UNACTIVE_34_ICON = "/arrow-red-34.png";
	public static final String START_UNACTIVE_64_ICON = "/arrow-red-64.png";
	public static final String START_UNACTIVE_66_ICON = "/arrow-red-66.png";
	public static final String DELETE_16_ICON = "/delete-16.png";
	public static final String DELETE_18_ICON = "/delete-18.png";
	public static final String DELETE_32_ICON = "/delete-32.png";
	public static final String DELETE_34_ICON = "/delete-34.png";
	public static final String DELETE_64_ICON = "/delete-64.png";
	public static final String DELETE_66_ICON = "/delete-66.png";
	public static final String SAVE_ICON = "/save.png";
	public static final String ADD_ICON = "/add.png";
	public static final String PREFERENCES_ICON = "/settings.png";
	public static final String UPDATE_ICON = "/update.png";

	private GuiConstants() {

	}

	public static enum StreamPanelShow {
		ALL, ACTIVE, UNACTIVE;

	}

	public static enum StreamPanelSize {
		SMALL(25), MEDIUM(50), LARGE(75);

		int size;

		private StreamPanelSize(int size) {
			this.size = size;
		}

	}

	public static enum StatusType {

		WAIT() {
			@Override
			public String getStatus() {
				return "Please wait...";
			}
		},

		UPDATE() {
			@Override
			public String getStatus() {

				return "Streams updated";
			}
		},

		ADD() {
			@Override
			public String getStatus() {
				return "Stream added";
			}
		},

		SAVE() {
			@Override
			public String getStatus() {
				return "Streams saved";
			}
		},

		NONE() {
			@Override
			public String getStatus() {
				return "";
			}
		},

		ADD_EROR() {
			@Override
			public String getStatus() {
				return "Wrong stream URL";
			}
		},
		LIVESTREAMER_NOT_SPECIFIED() {

			@Override
			public String getStatus() {
				return "Livestreamer path not specified yet!!!";
			}

		};
		public abstract String getStatus();
	}

}
