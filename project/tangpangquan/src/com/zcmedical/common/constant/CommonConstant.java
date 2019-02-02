package com.zcmedical.common.constant;

import java.text.SimpleDateFormat;

public class CommonConstant {

    public static final String HTTP_HOST = "http://121.40.148.142";

    /**
     * 首页网站地址
     */
    public static final String HOME_PAGE = "http://www.tpq.com/";

    public static final int MSG_PAGE_SIZE = 20;

    public static final boolean IS_DEBUG_MODE = true;

    public static final SimpleDateFormat refreshTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat serverTimeFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    public static final SimpleDateFormat serverDateFormat = new SimpleDateFormat("yyyyMMdd");
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");

    public static int CURRENT_VERSION_CODE = 0;

    public static final String KEY_TO_BE_CONTINUED = "KEY_TO_BE_CONTINUED";
    public static final String KEY_DATA_LIST = "KEY_DATA_LIST";

    /**
     * 帖子标题最小长度
     */
    public static final int POST_TITLE_MIN_LENGTH = 8;
    /**
     * 帖子内容最大长度
     */
    public static final int POST_CONTENT_MAX_LENGTH = 5000;
    /**
     * 回复内容最大长度
     */
    public static final int COMMENT_MAX_LENGTH = 200;

    public static final String NEW_FRIENDS_USERNAME = "item_new_friends";
    public static final String GROUP_USERNAME = "item_groups";
    public static final String MESSAGE_ATTR_IS_VOICE_CALL = "is_voice_call";
    public static final String MESSAGE_ATTR_IS_VIDEO_CALL = "is_video_call";
    public static final String ACCOUNT_REMOVED = "account_removed";

    /*
     * 提醒
     */
    public static final int REMIND_WEIGHT = 12000;
    public static final int REMIND_BLOOD_SUGAR_1 = 12001;
    public static final int REMIND_BLOOD_SUGAR_2 = 12002;
    public static final int REMIND_BLOOD_SUGAR_3 = 12003;
    public static final int REMIND_BLOOD_SUGAR_4 = 12004;
    public static final int REMIND_BLOOD_SUGAR_5 = 12005;
    public static final int REMIND_BLOOD_SUGAR_6 = 12006;
    public static final int REMIND_BLOOD_SUGAR_7 = 12007;
    public static final int REMIND_BLOOD_SUGAR_8 = 12008;
    public static final int REMIND_EVERYDAY = 12009;
    public static final int REMIND_HUANXIN = 12010;
    public static final int REMIND_COMMUNITY = 12011;

}
