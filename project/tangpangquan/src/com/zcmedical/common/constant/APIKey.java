package com.zcmedical.common.constant;

public class APIKey {

    public static final int SEX_MALE = 0; //男
    public static final int SEX_FEMALE = 1; //女
    public static final int MARITAL_STATUS_UNMARRIED = 0; //未婚
    public static final int MARITAL_STATUS_MARRIED = 1; //已婚

    public static final int STATUS_SUCCESSFUL = 0;//返回结果成功的状态值
    public static final int COMMON_STATUS_LEGAL = 1;//所有数据合法的状态，用于网络请求
    public static final String SORT_ASC = "asc"; //递增排序
    public static final String SORT_DESC = "desc"; //递减排序

    public static final String COMMON_ID = "id";
    public static final String COMMON_STATUS = "status";
    public static final String COMMON_MESSAGE = "message";
    public static final String COMMON_RESULT = "result";
    public static final String COMMON_OFFSET = "offset";
    public static final String COMMON_PAGE_SIZE = "page_size";
    public static final String COMMON_SORT_TYPES = "sort_types";
    public static final String COMMON_SORT_FIELDS = "sort_fields";
    public static final String COMMON_CREATED_AT = "created_at"; //创建时间
    public static final String COMMON_TO_BE_CONTINUED = "to_be_continued"; //创建时间
    public static final String COMMON_KEYWORD = "keyword"; // 搜索关键字
    public static final String COMMON_SEARCH_FIELDS = "search_fields"; // 搜索字段

    //帖子字段
    public static final String THREAD = "thread";
    public static final String THREADS = "threads";
    public static final String THREAD_ID = "thread_id";
    public static final String THREAD_TITLE = "title";
    public static final String THREAD_CONTENT = "content";
    public static final String THREAD_THREAD_PIC = "thread_pic"; //帖子图片 
    public static final String THREAD_STATUS = "thread_status"; //帖子状态
    public static final String THREAD_VIEWS_COUNT = "views_count";
    public static final String THREAD_COMMENTS_COUNT = "comments_count";
    public static final String THREAD_COLLECTIONS_COUNT = "collections_count";
    public static final String THREAD_LIKES_COUNT = "likes_count";
    public static final String THREAD_ESSENCE = "essence";
    public static final String THREAD_HOT = "hot";
    public static final String THREAD_TOP = "top";
    public static final String THREAD_REPORT_TYPE = "report_type";
    public static final String THREAD_COLLECTIONS = "thread_collections";

    //回复
    public static final String THREAD_COMMENT_ID = "thread_comment_id";
    public static final String THREAD_COMMENT = "thread_comment";
    public static final String THREAD_COMMENTS = "thread_comments";
    public static final String COMMENT_CONTENT = "content";
    public static final String COMMENT_STATUS = "comment_status";
    public static final String THREADS_COMMENT_PIC = "thread_comment_pic";
    public static final String THREADS_COMMENT_PICS = "thread_comment_pics";
    public static final String COMMENT_LIKES_COUNT = "likes_count";

    //圈子
    public static final String FORUM_ID = "forum_id";
    public static final String FORUM = "forum";
    public static final String FORUMS = "forums";
    public static final String FORUM_TITLE = "title";
    public static final String FORUM_DESCRIPTION = "description";
    public static final String FORUM_LOGO = "forum_logo";
    public static final String FORUM_STATUS = "forum_status";
    public static final String FORUM_USERS = "forum_users";
    public static final String FORUM_THREADS_COUNT = "threads_count";
    public static final String FORUM_USERS_COUNT = "users_count";
    public static final String FORUM_BANNERS = "forum_banners";
    public static final String FORUM_BANNER_TITLE = "banner_title";
    public static final String FORUM_BANNER_URL = "banner_url";
    public static final String FORUM_CONTENT_URL = "content_url";

    //用户字段
    public static final String USER = "user";
    public static final String USERS = "users";
    public static final String USER_ID = "user_id";
    public static final String USER_NICKNAME = "nickname";
    public static final String USER_PASSWORD = "password";
    public static final String USER_MOBILE = "mobile";
    public static final String USER_HEAD_PIC = "head_pic";
    public static final String USER_USER_HEAD_PIC = "user_head_pic";
    public static final String USER_USERNAME = "username";
    public static final String USER_BIRTHDAY = "birthday";
    public static final String USER_CITY = "city";
    public static final String USER_WEIXIN = "weixin";
    public static final String USER_WEIXIN_IDENTIFY = "weixin_identify";
    public static final String USER_QQ = "qq";
    public static final String USER_QQ_IDENTIFY = "qq_identify";
    public static final String USER_WEIBO = "weibo";
    public static final String USER_WEIBO_IDENTIFY = "weibo_identify";
    public static final String USER_CREATED_AT = "created_at";
    public static final String USER_LEVEL = "level";
    public static final String USER_SEX = "sex";
    public static final String USER_MARITAL_STATUS = "marital_status";
    public static final String USER_COLLECTIONS_COUNT = "collections_count";
    public static final String USER_FANS_COUNT = "fans_count";
    public static final String USER_FOLLOWS_COUNT = "follows_count";
    public static final String USER_THREADS_COUNT = "threads_count";
    public static final String USER_FORUMS_COUNT = "forums_count";
    public static final String USER_FOLLOWERS = "user_follows";
    public static final String USER_FOLLOWER = "user_follow";
    public static final String USER_IDENTIFYING_CODE = "identifying_code";
    public static final String FOLLOWER = "follower";
    public static final String USER_HEIGHT = "height";
    public static final String USER_TARGET_WEIGHT = "target_weight";
    public static final String USER_INTEGRAL = "integral";
    public static final String USER_THIRD_LOGON_NAME = "third_logon_name";

    //图片信息
    public static final String PIC_THREAD_COMMENT_ID = "thread_comment_id";
    public static final String PIC_THREAD_COMMENT_PIC = "thread_comment_pic";
    public static final String PIC_THREAD_ID = "thread_id";
    public static final String PIC_THREAD_PIC = "thread_pic";
    public static final String THREAD_PICS = "thread_pics";

    //版本
    public static final String VERSION_NAME = "version_name";
    public static final String VERSION_CODE = "version_code";
    public static final String PACKAGE_NAME = "package_name";
    public static final String DOWNLOAD_URL = "download_url";
    public static final String VERSION_DES = "version_des";
    public static final String VERSIONS = "versions";

    //意见反馈
    public static final String FEEDBACK_CONTENT = "content";

    //计划
    public static final String PLAN_ID = "plan_id";
    public static final String PLAN_TITLE = "title";
    public static final String PLAN_DAY_COUNT = "day_count";
    public static final String PLAN_FOLLOW_COUNT = "follow_count";
    public static final String PLAN_PROS = "pros";
    public static final String PLAN_CONS = "cons";
    public static final String PLAN_DESCRIPTION = "description";
    public static final String PLAN_PREVIEW = "preview";
    public static final String PLANS = "plans";
    public static final String PLAN = "plan";
    public static final String PLAN_DAY = "day";
    public static final String PLAN_CONTENT = "content";
    public static final String PLAN_DETAILS = "plan_details";

    public static final String PLAN_FOLLOW = "plan_follow";
    public static final String PLAN_FOLLOWS = "plan_follows";

    //体检
    public static final String MEDICAL_RECORD_PICS = "medical_record_pics";
    public static final String MEDICAL_RECORD = "medical_record";
    public static final String MEDICAL_RECORDS = "medical_records";
    public static final String MEDICAL_RECORD_ID = "medical_record_id";
    public static final String MEDICAL_RECORD_PIC = "medical_record_pic";

    public static final String MEDICAL_RECORD_LEUKOCYTE = "leukocyte";
    public static final String MEDICAL_RECORD_LEUKOMONOCYTE = "leukomonocyte";
    public static final String MEDICAL_RECORD_ERYTHROCYTE = "erythrocyte";
    public static final String MEDICAL_RECORD_HEMOGLOBIN = "hemoglobin";
    public static final String MEDICAL_RECORD_PREALBUMIN = "prealbumin";
    public static final String MEDICAL_RECORD_SIDEROPHILIN = "siderophilin";
    public static final String MEDICAL_RECORD_FASTING_BLOOD_GLUCOSE = "fasting_blood_glucose";
    public static final String MEDICAL_RECORD_TWO_BLOOD_GLUCOSE = "two_blood_glucose";
    public static final String MEDICAL_RECORD_GLYCOSYLATED_HEMOGLOBIN = "glycosylated_hemoglobin";
    public static final String MEDICAL_RECORD_FASTING_INSULIN = "fasting_insulin";
    public static final String MEDICAL_RECORD_FASTING_C_PEPTIDE = "fasting_c_peptide";
    public static final String MEDICAL_RECORD_TWO_INSULIN = "two_insulin";
    public static final String MEDICAL_RECORD_TWO_C_PEPTIDE = "two_c_peptide";
    public static final String MEDICAL_RECORD_ALBUMIN = "albumin";
    public static final String MEDICAL_RECORD_GLYCERIN_TRILAURATE = "glycerin_trilaurate";
    public static final String MEDICAL_RECORD_CHOLESTEROL = "cholesterol";
    public static final String MEDICAL_RECORD_LDL = "ldl";
    public static final String MEDICAL_RECORD_HDL = "hdl";
    public static final String MEDICAL_RECORD_URIC_ACID = "uric_acid";
    public static final String MEDICAL_RECORD_GPT = "gpt";
    public static final String MEDICAL_RECORD_GOT = "got";
    public static final String MEDICAL_RECORD_GGT = "ggt";
    public static final String MEDICAL_RECORD_USEA_NITROGEN = "usea_nitrogen";
    public static final String MEDICAL_RECORD_CREATININE = "creatinine";
    public static final String MEDICAL_RECORD_URINE_TRACE_ALBUMIN = "urine_trace_albumin";
    public static final String MEDICAL_RECORD_OPERATION_TIME = "operation_time";

}
