package com.evilratt.flutter_zoom_sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

import us.zoom.sdk.CameraControlRequestResult;
import us.zoom.sdk.CameraControlRequestType;
import us.zoom.sdk.ChatMessageDeleteType;
import us.zoom.sdk.FreeMeetingNeedUpgradeType;
import us.zoom.sdk.ICameraControlRequestHandler;
import us.zoom.sdk.IMeetingArchiveConfirmHandler;
import us.zoom.sdk.IMeetingInputUserInfoHandler;
import us.zoom.sdk.IRequestLocalRecordingPrivilegeHandler;
import us.zoom.sdk.InMeetingAudioController;
import us.zoom.sdk.InMeetingChatController;
import us.zoom.sdk.InMeetingChatMessage;
import us.zoom.sdk.InMeetingEventHandler;
import us.zoom.sdk.InMeetingServiceListener;

import us.zoom.sdk.CustomizedNotificationData;
import us.zoom.sdk.InMeetingNotificationHandle;
import us.zoom.sdk.InMeetingService;
import us.zoom.sdk.JoinMeetingOptions;
import us.zoom.sdk.JoinMeetingParams;
import us.zoom.sdk.LocalRecordingRequestPrivilegeStatus;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.MeetingStatus;
import us.zoom.sdk.MeetingViewsOptions;
import us.zoom.sdk.MobileRTCFocusModeShareType;
import us.zoom.sdk.SDKNotificationServiceError;
import us.zoom.sdk.StartMeetingOptions;
import us.zoom.sdk.StartMeetingParams4NormalUser;
import us.zoom.sdk.VideoQuality;
import us.zoom.sdk.ZoomAuthenticationError;
import us.zoom.sdk.ZoomError;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKAuthenticationListener;
import us.zoom.sdk.ZoomSDKFileReceiver;
import us.zoom.sdk.ZoomSDKFileSender;
import us.zoom.sdk.ZoomSDKFileTransferInfo;
import us.zoom.sdk.ZoomSDKInitParams;
import us.zoom.sdk.ZoomSDKInitializeListener;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * FlutterZoomPlugin
 */
public class FlutterZoomSdkPlugin implements FlutterPlugin, MethodChannel.MethodCallHandler, ActivityAware {
    Activity activity;
    private Result pendingResult;

    private MeetingService meetingService;
    private MethodChannel methodChannel;
    private Context context;
    private EventChannel meetingStatusChannel;
    private InMeetingService inMeetingService;


    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        this.activity = binding.getActivity();
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        this.activity = binding.getActivity();
    }

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        context = flutterPluginBinding.getApplicationContext();
        methodChannel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "com.evilratt/zoom_sdk");
        methodChannel.setMethodCallHandler(this);

        meetingStatusChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), "com.evilratt/zoom_sdk_event_stream");
    }

    @Override
    public void onMethodCall(@NonNull MethodCall methodCall, @NonNull final Result result) {
        switch (methodCall.method) {
            case "init":
                init(methodCall, result);
                break;
            case "login":
                login(methodCall, result);
                break;
            case "logout":
                logout();
                break;
            case "join":
                joinMeeting(methodCall, result);
                break;
            case "return":
                returnToMeeting();
                break;
            case "startNormal":
                startMeetingNormal(methodCall, result);
                break;
            case "meeting_status":
                meetingStatus(result);
                break;
            case "meeting_details":
                meetingDetails(result);
                break;
            default:
                result.notImplemented();
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        methodChannel.setMethodCallHandler(null);
    }

    private void sendReply(List data) {
        if (this.pendingResult == null) {
            return;
        }
        this.pendingResult.success(data);
        this.clearPendingResult();
    }

    private void clearPendingResult() {
        this.pendingResult = null;
    }

    //Initializing the Zoom SDK for Android
    private void init(final MethodCall methodCall, final Result result) {
        Map<String, String> options = methodCall.arguments();

        ZoomSDK zoomSDK = ZoomSDK.getInstance();

        if (zoomSDK.isInitialized()) {
            List<Integer> response = Arrays.asList(0, 0);
            result.success(response);
            return;
        }

        ZoomSDKInitParams initParams = new ZoomSDKInitParams();
        initParams.jwtToken = options.get("jwtToken");
        initParams.domain = options.get("domain");
        initParams.enableLog = true;

        final InMeetingNotificationHandle handle = (context, intent) -> {
            intent = new Intent(context, FlutterZoomSdkPlugin.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            if (context == null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            intent.setAction(InMeetingNotificationHandle.ACTION_RETURN_TO_CONF);
            assert context != null;
            context.startActivity(intent);
            return true;
        };

        //Set custom Notification fro android
        final CustomizedNotificationData data = new CustomizedNotificationData();
        data.setContentTitleId(R.string.app_name_zoom_local);
        data.setLargeIconId(R.drawable.zm_mm_type_emoji);
        data.setSmallIconId(R.drawable.zm_mm_type_emoji);
        data.setSmallIconForLorLaterId(R.drawable.zm_mm_type_emoji);

        ZoomSDKInitializeListener listener = new ZoomSDKInitializeListener() {
            /**
             * @param errorCode {@link us.zoom.sdk.ZoomError#ZOOM_ERROR_SUCCESS} if the SDK has been initialized successfully.
             */
            @Override
            public void onZoomSDKInitializeResult(int errorCode, int internalErrorCode) {
                List<Integer> response = Arrays.asList(errorCode, internalErrorCode);

                if (errorCode != ZoomError.ZOOM_ERROR_SUCCESS) {
                    System.out.println("Failed to initialize Zoom SDK");
                    result.success(response);
                    return;
                }

                ZoomSDK zoomSDK = ZoomSDK.getInstance();
                ZoomSDK.getInstance().getMeetingSettingsHelper().enableShowMyMeetingElapseTime(true);
                ZoomSDK.getInstance().getMeetingSettingsHelper().setCustomizedNotificationData(data, handle);

                MeetingService meetingService = zoomSDK.getMeetingService();
                meetingStatusChannel.setStreamHandler(new StatusStreamHandler(meetingService));
                result.success(response);
            }

            @Override
            public void onZoomAuthIdentityExpired() {
            }
        };
        zoomSDK.initialize(context, listener, initParams);
    }

    //Perform start meeting function with logging in to the zoom account
    private void login(final MethodCall methodCall, final Result result) {
        this.pendingResult = result;
        Map<String, String> options = methodCall.arguments();

        ZoomSDK zoomSDK = ZoomSDK.getInstance();

        if (!zoomSDK.isInitialized()) {
            System.out.println("Not initialized!!!!!!");
            result.success(Arrays.asList("SDK ERROR", "001"));
            return;
        }

        ZoomSDKAuthenticationListener authenticationListener = new ZoomSDKAuthenticationListener() {

            @Override
            public void onZoomSDKLoginResult(long results) {
                if (results != ZoomAuthenticationError.ZOOM_AUTH_ERROR_SUCCESS) {
                    sendReply(Arrays.asList("LOGIN ERROR", String.valueOf(results)));
                    return;
                }
                startMeeting(methodCall);
            }

            @Override
            public void onZoomSDKLogoutResult(long l) {

            }

            @Override
            public void onZoomIdentityExpired() {

            }

            @Override
            public void onZoomAuthIdentityExpired() {

            }

            @Override
            public void onNotificationServiceStatus(SDKNotificationServiceStatus sdkNotificationServiceStatus, SDKNotificationServiceError sdkNotificationServiceError) {

            }
        };

        if (!zoomSDK.isLoggedIn()) {
            zoomSDK.tryAutoLoginZoom();
            zoomSDK.addAuthenticationListener(authenticationListener);
        }

        if (zoomSDK.isLoggedIn()) {
            startMeeting(methodCall);
        }

    }

    //Join Meeting with passed Meeting ID and Passcode
    private void joinMeeting(MethodCall methodCall, Result result) {

        Map<String, String> options = methodCall.arguments();

        ZoomSDK zoomSDK = ZoomSDK.getInstance();

        if (!zoomSDK.isInitialized()) {
            System.out.println("Not initialized!!!!!!");

            result.success(false);
            return;
        }

        zoomSDK.getInMeetingService().addListener(new InMeetingServiceListener() {
            @Override
            public void onMeetingNeedPasswordOrDisplayName(boolean b, boolean b1, InMeetingEventHandler inMeetingEventHandler) {

            }

            @Override
            public void onWebinarNeedRegister(String s) {

            }

            @Override
            public void onJoinMeetingNeedUserInfo(IMeetingInputUserInfoHandler iMeetingInputUserInfoHandler) {

            }

            @Override
            public void onJoinWebinarNeedUserNameAndEmail(InMeetingEventHandler inMeetingEventHandler) {
                inMeetingEventHandler.setRegisterWebinarInfo(
                        options.get("userId"), options.get("userEmail"), false
                );
            }

            @Override
            public void onWebinarNeedInputScreenName(InMeetingEventHandler inMeetingEventHandler) {

            }

            @Override
            public void onMeetingNeedCloseOtherMeeting(InMeetingEventHandler inMeetingEventHandler) {

            }

            @Override
            public void onMeetingFail(int i, int i1) {

            }

            @Override
            public void onMeetingLeaveComplete(long l) {

            }

            @Override
            public void onMeetingUserJoin(List<Long> list) {

            }

            @Override
            public void onMeetingUserLeave(List<Long> list) {

            }

            @Override
            public void onMeetingUserUpdated(long l) {

            }

            @Override
            public void onInMeetingUserAvatarPathUpdated(long l) {

            }

            @Override
            public void onMeetingHostChanged(long l) {

            }

            @Override
            public void onMeetingCoHostChange(long l, boolean b) {

            }

            @Override
            public void onActiveVideoUserChanged(long l) {

            }

            @Override
            public void onActiveSpeakerVideoUserChanged(long l) {

            }

            @Override
            public void onHostVideoOrderUpdated(List<Long> list) {

            }

            @Override
            public void onFollowHostVideoOrderChanged(boolean b) {

            }

            @Override
            public void onSpotlightVideoChanged(List<Long> list) {

            }

            @Override
            public void onUserVideoStatusChanged(long l, VideoStatus videoStatus) {

            }

            @Override
            public void onSinkMeetingVideoQualityChanged(VideoQuality videoQuality, long l) {

            }

            @Override
            public void onMicrophoneStatusError(InMeetingAudioController.MobileRTCMicrophoneError mobileRTCMicrophoneError) {

            }

            @Override
            public void onUserAudioStatusChanged(long l, AudioStatus audioStatus) {

            }

            @Override
            public void onHostAskUnMute(long l) {

            }

            @Override
            public void onHostAskStartVideo(long l) {

            }

            @Override
            public void onUserAudioTypeChanged(long l) {

            }

            @Override
            public void onMyAudioSourceTypeChanged(int i) {

            }

            @Override
            public void onLowOrRaiseHandStatusChanged(long l, boolean b) {

            }

            @Override
            public void onChatMessageReceived(InMeetingChatMessage inMeetingChatMessage) {

            }

            @Override
            public void onChatMsgDeleteNotification(String s, ChatMessageDeleteType chatMessageDeleteType) {

            }

            @Override
            public void onShareMeetingChatStatusChanged(boolean b) {

            }

            @Override
            public void onSilentModeChanged(boolean b) {

            }

            @Override
            public void onFreeMeetingReminder(boolean b, boolean b1, boolean b2) {

            }

            @Override
            public void onMeetingActiveVideo(long l) {

            }

            @Override
            public void onSinkAttendeeChatPriviledgeChanged(int i) {

            }

            @Override
            public void onSinkAllowAttendeeChatNotification(int i) {

            }

            @Override
            public void onSinkPanelistChatPrivilegeChanged(InMeetingChatController.MobileRTCWebinarPanelistChatPrivilege mobileRTCWebinarPanelistChatPrivilege) {

            }

            @Override
            public void onUserNamesChanged(List<Long> list) {

            }

            @Override
            public void onFreeMeetingNeedToUpgrade(FreeMeetingNeedUpgradeType freeMeetingNeedUpgradeType, String s) {

            }

            @Override
            public void onFreeMeetingUpgradeToGiftFreeTrialStart() {

            }

            @Override
            public void onFreeMeetingUpgradeToGiftFreeTrialStop() {

            }

            @Override
            public void onFreeMeetingUpgradeToProMeeting() {

            }

            @Override
            public void onClosedCaptionReceived(String s, long l) {

            }

            @Override
            public void onRecordingStatus(RecordingStatus recordingStatus) {

            }

            @Override
            public void onLocalRecordingStatus(long l, RecordingStatus recordingStatus) {

            }

            @Override
            public void onInvalidReclaimHostkey() {

            }

            @Override
            public void onPermissionRequested(String[] strings) {

            }

            @Override
            public void onAllHandsLowered() {

            }

            @Override
            public void onLocalVideoOrderUpdated(List<Long> list) {

            }

            @Override
            public void onLocalRecordingPrivilegeRequested(IRequestLocalRecordingPrivilegeHandler iRequestLocalRecordingPrivilegeHandler) {

            }

            @Override
            public void onSuspendParticipantsActivities() {

            }

            @Override
            public void onAllowParticipantsStartVideoNotification(boolean b) {

            }

            @Override
            public void onAllowParticipantsRenameNotification(boolean b) {

            }

            @Override
            public void onAllowParticipantsUnmuteSelfNotification(boolean b) {

            }

            @Override
            public void onAllowParticipantsShareWhiteBoardNotification(boolean b) {

            }

            @Override
            public void onMeetingLockStatus(boolean b) {

            }

            @Override
            public void onRequestLocalRecordingPrivilegeChanged(LocalRecordingRequestPrivilegeStatus localRecordingRequestPrivilegeStatus) {

            }

            @Override
            public void onAICompanionActiveChangeNotice(boolean b) {

            }

            @Override
            public void onParticipantProfilePictureStatusChange(boolean b) {

            }

            @Override
            public void onCloudRecordingStorageFull(long l) {

            }

            @Override
            public void onUVCCameraStatusChange(String s, UVCCameraStatus uvcCameraStatus) {

            }

            @Override
            public void onFocusModeStateChanged(boolean b) {

            }

            @Override
            public void onFocusModeShareTypeChanged(MobileRTCFocusModeShareType mobileRTCFocusModeShareType) {

            }

            @Override
            public void onVideoAlphaChannelStatusChanged(boolean b) {

            }

            @Override
            public void onAllowParticipantsRequestCloudRecording(boolean b) {

            }

            @Override
            public void onSinkJoin3rdPartyTelephonyAudio(String s) {

            }

            @Override
            public void onUserConfirmToStartArchive(IMeetingArchiveConfirmHandler iMeetingArchiveConfirmHandler) {

            }

            @Override
            public void onCameraControlRequestReceived(long l, CameraControlRequestType cameraControlRequestType, ICameraControlRequestHandler iCameraControlRequestHandler) {

            }

            @Override
            public void onCameraControlRequestResult(long l, boolean b) {

            }

            @Override
            public void onCameraControlRequestResult(long l, CameraControlRequestResult cameraControlRequestResult) {

            }

            @Override
            public void onFileSendStart(ZoomSDKFileSender zoomSDKFileSender) {

            }

            @Override
            public void onFileReceived(ZoomSDKFileReceiver zoomSDKFileReceiver) {

            }

            @Override
            public void onFileTransferProgress(ZoomSDKFileTransferInfo zoomSDKFileTransferInfo) {

            }

            @Override
            public void onMuteOnEntryStatusChange(boolean b) {

            }

            @Override
            public void onMeetingTopicChanged(String s) {

            }
        });

        meetingService = zoomSDK.getMeetingService();

        JoinMeetingOptions opts = new JoinMeetingOptions();
        opts.no_invite = parseBoolean(options, "disableInvite");
        opts.no_share = parseBoolean(options, "disableShare");
        opts.no_titlebar = parseBoolean(options, "disableTitlebar");
        opts.no_driving_mode = parseBoolean(options, "disableDrive");
        opts.no_dial_in_via_phone = parseBoolean(options, "disableDialIn");
        opts.no_disconnect_audio = parseBoolean(options, "noDisconnectAudio");
        opts.no_audio = parseBoolean(options, "noAudio");
        opts.no_webinar_register_dialog = true;
        boolean view_options = parseBoolean(options, "viewOptions");
        if (view_options) {
            opts.meeting_views_options = MeetingViewsOptions.NO_TEXT_MEETING_ID + MeetingViewsOptions.NO_TEXT_PASSWORD;
        }

        JoinMeetingParams params = new JoinMeetingParams();

        params.displayName = options.get("userId");
        params.meetingNo = options.get("meetingId");
        params.password = options.get("meetingPassword");

        meetingService.joinMeetingWithParams(context, params, opts);

        result.success(true);
    }

    private void returnToMeeting() {
        meetingService.returnToMeeting(context);
    }

    // Basic Start Meeting Function called on startMeeting triggered via login function
    private void startMeeting(MethodCall methodCall) {

        Map<String, String> options = methodCall.arguments();

        ZoomSDK zoomSDK = ZoomSDK.getInstance();

        if (!zoomSDK.isInitialized()) {
            System.out.println("Not initialized!!!!!!");
            sendReply(Arrays.asList("SDK ERROR", "001"));
            return;
        }

        if (!zoomSDK.isLoggedIn()) {
            System.out.println("Not LoggedIn!!!!!!");
            sendReply(Arrays.asList("LOGIN REQUIRED", "001"));
            return;
        }

        MeetingService meetingService = zoomSDK.getMeetingService();
        StartMeetingOptions opts = new StartMeetingOptions();
        opts.no_invite = parseBoolean(options, "disableInvite");
        opts.no_share = parseBoolean(options, "disableShare");
        opts.no_driving_mode = parseBoolean(options, "disableDrive");
        opts.no_dial_in_via_phone = parseBoolean(options, "disableDialIn");
        opts.no_disconnect_audio = parseBoolean(options, "noDisconnectAudio");
        opts.no_audio = parseBoolean(options, "noAudio");
        opts.no_titlebar = parseBoolean(options, "disableTitlebar");
        boolean view_options = parseBoolean(options, "viewOptions");
        if (view_options) {
            opts.meeting_views_options = MeetingViewsOptions.NO_TEXT_MEETING_ID + MeetingViewsOptions.NO_TEXT_PASSWORD;
        }

        meetingService.startInstantMeeting(context, opts);
        inMeetingService = zoomSDK.getInMeetingService();
        sendReply(Arrays.asList("MEETING SUCCESS", "200"));
    }

    //Perform start meeting function with logging in to the zoom account (Only for passed meeting id)
    private void startMeetingNormal(final MethodCall methodCall, final Result result) {
        this.pendingResult = result;
        Map<String, String> options = methodCall.arguments();

        ZoomSDK zoomSDK = ZoomSDK.getInstance();

        if (!zoomSDK.isInitialized()) {
            System.out.println("Not initialized!!!!!!");
            sendReply(Arrays.asList("SDK ERROR", "001"));
            return;
        }

        if (zoomSDK.isLoggedIn()) {
            startMeetingNormalInternal(methodCall);
        }

        ZoomSDKAuthenticationListener authenticationListener = new ZoomSDKAuthenticationListener() {

            @Override
            public void onZoomSDKLoginResult(long results) {
                //Log.d("Zoom Flutter", String.format("[onLoginError] : %s", results));
                if (results != ZoomAuthenticationError.ZOOM_AUTH_ERROR_SUCCESS) {
                    sendReply(Arrays.asList("LOGIN ERROR", String.valueOf(results)));
                    return;
                }
                startMeetingNormalInternal(methodCall);
            }

            @Override
            public void onZoomSDKLogoutResult(long l) {

            }

            @Override
            public void onZoomIdentityExpired() {

            }

            @Override
            public void onZoomAuthIdentityExpired() {

            }

            @Override
            public void onNotificationServiceStatus(SDKNotificationServiceStatus sdkNotificationServiceStatus, SDKNotificationServiceError sdkNotificationServiceError) {

            }
        };

        if (!zoomSDK.isLoggedIn()) {
            zoomSDK.tryAutoLoginZoom();
            zoomSDK.addAuthenticationListener(authenticationListener);
        }
    }

    // Meeting ID passed Start Meeting Function called on startMeetingNormal triggered via startMeetingNormal function
    private void startMeetingNormalInternal(MethodCall methodCall) {
        Map<String, String> options = methodCall.arguments();

        ZoomSDK zoomSDK = ZoomSDK.getInstance();

        if (!zoomSDK.isInitialized()) {
            System.out.println("Not initialized!!!!!!");
            sendReply(Arrays.asList("SDK ERROR", "001"));
            return;
        }

        if (zoomSDK.isLoggedIn()) {
            MeetingService meetingService = zoomSDK.getMeetingService();
            StartMeetingOptions opts = new StartMeetingOptions();
            opts.no_invite = parseBoolean(options, "disableInvite");
            opts.no_share = parseBoolean(options, "disableShare");
            opts.no_driving_mode = parseBoolean(options, "disableDrive");
            opts.no_dial_in_via_phone = parseBoolean(options, "disableDialIn");
            opts.no_disconnect_audio = parseBoolean(options, "noDisconnectAudio");
            opts.no_audio = parseBoolean(options, "noAudio");
            opts.no_titlebar = parseBoolean(options, "disableTitlebar");
            boolean view_options = parseBoolean(options, "viewOptions");
            if (view_options) {
                opts.meeting_views_options = MeetingViewsOptions.NO_TEXT_MEETING_ID + MeetingViewsOptions.NO_TEXT_PASSWORD;
            }

            StartMeetingParams4NormalUser params = new StartMeetingParams4NormalUser();
            params.meetingNo = options.get("meetingId");

            meetingService.startMeetingWithParams(context, params, opts);
            inMeetingService = zoomSDK.getInMeetingService();
            sendReply(Arrays.asList("MEETING SUCCESS", "200"));
        }
    }

    //Helper Function for parsing string to boolean value
    private boolean parseBoolean(Map<String, String> options, String property) {
        return options.get(property) != null && Boolean.parseBoolean(options.get(property));
    }

    //Get Meeting Details Programmatically after Starting the Meeting
    private void meetingDetails(Result result) {
        ZoomSDK zoomSDK = ZoomSDK.getInstance();

        if (!zoomSDK.isInitialized()) {
            System.out.println("Not initialized!!!!!!");
            result.success(Arrays.asList("MEETING_STATUS_UNKNOWN", "SDK not initialized"));
            return;
        }
        MeetingService meetingService = zoomSDK.getMeetingService();

        if (meetingService == null) {
            result.success(Arrays.asList("MEETING_STATUS_UNKNOWN", "No status available"));
            return;
        }
        MeetingStatus status = meetingService.getMeetingStatus();

        result.success(status != null ? Arrays.asList(inMeetingService.getCurrentMeetingNumber(), inMeetingService.getMeetingPassword()) : Arrays.asList("MEETING_STATUS_UNKNOWN", "No status available"));
    }

    //Listen to meeting status on joinning and starting the mmeting
    private void meetingStatus(Result result) {

        ZoomSDK zoomSDK = ZoomSDK.getInstance();

        if (!zoomSDK.isInitialized()) {
            System.out.println("Not initialized!!!!!!");
            result.success(Arrays.asList("MEETING_STATUS_UNKNOWN", "SDK not initialized"));
            return;
        }
        MeetingService meetingService = zoomSDK.getMeetingService();

        if (meetingService == null) {
            result.success(Arrays.asList("MEETING_STATUS_UNKNOWN", "No status available"));
            return;
        }

        MeetingStatus status = meetingService.getMeetingStatus();
        result.success(status != null ? Arrays.asList(status.name(), "") : Arrays.asList("MEETING_STATUS_UNKNOWN", "No status available"));
    }

    public void logout() {
        ZoomSDK zoomSDK = ZoomSDK.getInstance();
        zoomSDK.logoutZoom();
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        this.activity = null;
    }

    @Override
    public void onDetachedFromActivity() {
        this.activity = null;
    }
}
