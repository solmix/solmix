package org.solmix.sgt.client.fileloader;

public interface FileLoaderConstants {
	static String NOTE_QUEUED = "noteQueued";
	static String NOTE_CANCELED = "noteCanceled";

	static String MSG_SUCCESS = "msgSuccess";
	static String MSG_COULDNT_RETRIEVES_ERVER = "msgCouldntRetrieveServer";
	static String MSG_SERVER_SIDE_ERROR = "msgServerSideError";
	static String MSG_UPLOADING_FINISHED = "msgUploadingFinished";
	static String MSG_INVALID_FILE_CHOSEN = "msgInvalidFileChosen";
	static String MSG_NO_FILE_ADD = "msgNoFileAdd";

	static String LABEL_FILE_NAME = "labelFileName";
	static String LABEL_STATUS_CODE = "labelStatus";
	static String LABEL_NOTE = "labelNote";
	static String LABEL_ADD = "labelAdd";
	static String LABEL_REMOVE = "labelRemove";
	static String LABEL_RESET = "labelReset";
	static String LABEL_UPLOAD = "labelUpload";
	static String LABEL_STOP = "labelStop";
	static String LABEL_CLOSE = "labelClose";

	// Data Record
	static String RECORD_FIELD_FILE_ID = "fileId";
	static String RECORD_FIELD_FILE_NAME = "fileName";
	static String RECORD_FIELD_FOLDER_ID = "folderId";
	static String RECORD_FIELD_FILE_STATE = "fileState";
	static String RECORD_FIELD_FILE_INPUT = "fileInput";
	static String RECORD_FIELD_NOTE = "note";
	
	// CSS classes
	static String CSS_NEW_FILE_INPUT_CONTAINER = "fileuploader-newFileInputContainer";
	static String CSS_FILE_NAME_TIP = "fileuploader-fileNameTip";
	static String CSS_LAST_NEW_FILE_INPUT = "fileuploader-lastNewFileInput";

	// icons for buttons
	static String ICON_ADD = "fileuploader/icons/16/icon_add.gif";
	static String ICON_REMOVE = "fileuploader/icons/16/icon_remove.gif";
	static String ICON_RESET = "fileuploader/icons/16/icon_reset.gif";
	static String ICON_START = "fileuploader/icons/16/icon_start.gif";
	static String ICON_STOP = "fileuploader/icons/16/icon_stop.gif";

	static String IMG_STATUS_PREFIX = "fileuploader/status_";
	static String IMG_STATUS_SUFFIX = ".gif";

	static int MIN_WIDTH = 602;
	static int MIN_HEIGHT = 300;
	static int DEFAULT_HEIGHT_PROCESSING_BAR = 14;
	static int DEFAULT_HEIGHT_TOOL_STRIP = 20;

	static String JSON_KEY_SUCCESS = "success";
	static String JSON_KEY_REASON = "reason";
}
