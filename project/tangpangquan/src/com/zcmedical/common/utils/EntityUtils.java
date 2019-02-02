package com.zcmedical.common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.zcmedical.common.constant.APIKey;
import com.zcmedical.tangpangquan.entity.BannerEntity;
import com.zcmedical.tangpangquan.entity.CircleEntity;
import com.zcmedical.tangpangquan.entity.CommentEntity;
import com.zcmedical.tangpangquan.entity.MedicalRecordEntity;
import com.zcmedical.tangpangquan.entity.MedicalRecordPicEntity;
import com.zcmedical.tangpangquan.entity.PicEntity;
import com.zcmedical.tangpangquan.entity.PlanDetailEntity;
import com.zcmedical.tangpangquan.entity.PlanEntity;
import com.zcmedical.tangpangquan.entity.PostCollectionEntity;
import com.zcmedical.tangpangquan.entity.PostsEntity;
import com.zcmedical.tangpangquan.entity.UserEntity;

public class EntityUtils {
    public static PostsEntity getPostsEntity(Map<String, Object> postsMap) {
        if (null != postsMap) {
            PostsEntity posts = new PostsEntity();

            posts.setId(TypeUtil.getId(postsMap.get(APIKey.COMMON_ID)));
            posts.setTitle(TypeUtil.getString(postsMap.get(APIKey.THREAD_TITLE)));
            posts.setContent(TypeUtil.getString(postsMap.get(APIKey.THREAD_CONTENT)));
            posts.setCreatedAt(TypeUtil.getString(postsMap.get(APIKey.COMMON_CREATED_AT)));
            posts.setViewsCount(TypeUtil.getInteger(postsMap.get(APIKey.THREAD_VIEWS_COUNT), 0));
            posts.setCommentCount(TypeUtil.getInteger(postsMap.get(APIKey.THREAD_COMMENTS_COUNT), 0));
            posts.setCollecttionCount(TypeUtil.getInteger(postsMap.get(APIKey.THREAD_COLLECTIONS_COUNT), 0));
            posts.setLikesCount(TypeUtil.getInteger(postsMap.get(APIKey.THREAD_LIKES_COUNT), 0));
            int top = TypeUtil.getInteger(postsMap.get(APIKey.THREAD_TOP), 0);
            int hot = TypeUtil.getInteger(postsMap.get(APIKey.THREAD_HOT), 0);
            int essence = TypeUtil.getInteger(postsMap.get(APIKey.THREAD_ESSENCE), 0);

            posts.setHot(hot == 1);
            posts.setTop(top == 1);
            posts.setEssence(essence == 1);

            List<PicEntity> pics = getPostsPicEntityList(TypeUtil.getList(postsMap.get(APIKey.THREAD_PICS)));
            if (null != pics) {
                posts.setPostsPicUrls(pics);
            }

            UserEntity user = getUserEntity(TypeUtil.getMap(postsMap.get(APIKey.USER)));
            if (null != user) {
                posts.setUser(user);
            }

            CircleEntity circle = getCircleEntity(TypeUtil.getMap(postsMap.get(APIKey.FORUM)));
            if (null != circle) {
                posts.setCircle(circle);
            }

            return posts;
        }

        return null;
    }

    public static List<PostsEntity> getPostsEntityList(List<Map<String, Object>> rawPostsList) {
        List<PostsEntity> postsEntityList = new ArrayList<PostsEntity>();
        if (null != rawPostsList) {
            for (int i = 0; i < rawPostsList.size(); i++) {
                Map<String, Object> rawPosts = rawPostsList.get(i);
                PostsEntity posts = getPostsEntity(rawPosts);
                if (null != posts) {
                    postsEntityList.add(posts);
                }
            }
        }
        return postsEntityList;
    }

    public static UserEntity getUserEntity(Map<String, Object> userMap) {
        if (null != userMap) {
            UserEntity user = new UserEntity();

            user.setId(TypeUtil.getId(userMap.get(APIKey.COMMON_ID)));
            user.setUsername(TypeUtil.getString(userMap.get(APIKey.USER_USERNAME)));
            user.setNickname(TypeUtil.getString(userMap.get(APIKey.USER_NICKNAME)));
            user.setMobile(TypeUtil.getString(userMap.get(APIKey.USER_MOBILE)));
            user.setHeadPic(TypeUtil.getString(userMap.get(APIKey.USER_HEAD_PIC)));
            user.setBirthday(TypeUtil.getString(userMap.get(APIKey.USER_BIRTHDAY)));
            user.setCity(TypeUtil.getString(userMap.get(APIKey.USER_CITY)));
            user.setWeixin(TypeUtil.getString(userMap.get(APIKey.USER_WEIXIN)));
            user.setWeibo(TypeUtil.getString(userMap.get(APIKey.USER_WEIBO)));
            user.setQq(TypeUtil.getString(userMap.get(APIKey.USER_QQ)));
            user.setCreatedAt(TypeUtil.getString(userMap.get(APIKey.USER_CREATED_AT)));
            user.setLevel(TypeUtil.getInteger(userMap.get(APIKey.USER_LEVEL), 1));
            user.setMaritalStatus(TypeUtil.getInteger(userMap.get(APIKey.USER_MARITAL_STATUS), -1));
            user.setSex(TypeUtil.getInteger(userMap.get(APIKey.USER_SEX), 0));
            user.setCollectionsCount(TypeUtil.getInteger(userMap.get(APIKey.USER_COLLECTIONS_COUNT), 0));
            user.setFollowsCount(TypeUtil.getInteger(userMap.get(APIKey.USER_FOLLOWS_COUNT), 0));
            user.setFansCount(TypeUtil.getInteger(userMap.get(APIKey.USER_FANS_COUNT), 0));
            user.setPostsCount(TypeUtil.getInteger(userMap.get(APIKey.USER_THREADS_COUNT), 0));
            user.setCircleCount(TypeUtil.getInteger(userMap.get(APIKey.USER_FORUMS_COUNT), 0));
            user.setHeight(TypeUtil.getInteger(userMap.get(APIKey.USER_HEIGHT), 0));
            user.setTargetWeight(TypeUtil.getInteger(userMap.get(APIKey.USER_TARGET_WEIGHT), 0));
            user.setIntegral(TypeUtil.getInteger(userMap.get(APIKey.USER_INTEGRAL), 0));

            return user;
        }
        return null;

    }

    public static List<UserEntity> getUserEntityList(List<Map<String, Object>> rawUserList) {
        List<UserEntity> userEntityList = new ArrayList<UserEntity>();
        if (null != rawUserList) {
            for (int i = 0; i < rawUserList.size(); i++) {
                Map<String, Object> rawUser = rawUserList.get(i);
                UserEntity user = getUserEntity(rawUser);
                if (null != user) {
                    userEntityList.add(user);
                }
            }

        }
        return userEntityList;

    }

    public static CircleEntity getCircleEntity(Map<String, Object> rawCircle) {
        if (null != rawCircle) {
            CircleEntity circle = new CircleEntity();

            circle.setId(TypeUtil.getId(rawCircle.get(APIKey.COMMON_ID)));
            circle.setTitle(TypeUtil.getString(rawCircle.get(APIKey.FORUM_TITLE)));
            circle.setDescription(TypeUtil.getString(rawCircle.get(APIKey.FORUM_DESCRIPTION)));
            circle.setForumLogoUrl(TypeUtil.getString(rawCircle.get(APIKey.FORUM_LOGO)));
            circle.setCreatedAt(TypeUtil.getString(rawCircle.get(APIKey.COMMON_CREATED_AT)));
            circle.setPostsCount(TypeUtil.getInteger(rawCircle.get(APIKey.FORUM_THREADS_COUNT), 0));
            circle.setUserCount(TypeUtil.getInteger(rawCircle.get(APIKey.FORUM_USERS_COUNT), 0));

            UserEntity user = getUserEntity(TypeUtil.getMap(rawCircle.get(APIKey.USER)));
            if (null != user) {
                circle.setCircleAdmin(user);
            }

            return circle;
        }

        return null;
    }

    public static List<CircleEntity> getCircleEntityList(List<Map<String, Object>> rawCircleList) {
        List<CircleEntity> circleEntityList = new ArrayList<CircleEntity>();
        if (null != rawCircleList) {
            for (int i = 0; i < rawCircleList.size(); i++) {
                Map<String, Object> rawCircle = rawCircleList.get(i);
                CircleEntity cicle = getCircleEntity(rawCircle);
                if (null != cicle) {
                    circleEntityList.add(cicle);
                }
            }

        }
        return circleEntityList;

    }

    public static CommentEntity getCommentEntity(Map<String, Object> rawComment) {
        if (null != rawComment) {
            CommentEntity comment = new CommentEntity();

            comment.setId(TypeUtil.getId(rawComment.get(APIKey.COMMON_ID)));
            comment.setUserId(TypeUtil.getId(rawComment.get(APIKey.USER_ID)));
            comment.setThreadId(TypeUtil.getId(rawComment.get(APIKey.THREAD_ID)));
            comment.setContent(TypeUtil.getString(rawComment.get(APIKey.COMMENT_CONTENT)));
            comment.setCommentStatus(TypeUtil.getInteger(rawComment.get(APIKey.COMMENT_STATUS), 1));
            comment.setCreatedAt(TypeUtil.getString(rawComment.get(APIKey.COMMON_CREATED_AT)));
            comment.setCommentLikeCount(TypeUtil.getInteger(rawComment.get(APIKey.COMMENT_LIKES_COUNT), 0));

            List<PicEntity> pics = getCommentPicEntityList(TypeUtil.getList(rawComment.get(APIKey.THREADS_COMMENT_PICS)));
            if (null != pics) {
                comment.setCommentPics(pics);
            }

            UserEntity user = getUserEntity(TypeUtil.getMap(rawComment.get(APIKey.USER)));
            if (null != user) {
                comment.setUser(user);
            }

            CommentEntity parentComment = getCommentEntity(TypeUtil.getMap(rawComment.get(APIKey.THREAD_COMMENT)));
            if (null != parentComment) {
                comment.setParentComment(parentComment);
            }

            PostsEntity post = getPostsEntity(TypeUtil.getMap(rawComment.get(APIKey.THREAD)));
            if (null != post) {
                comment.setPosts(post);
            }

            return comment;
        }

        return null;
    }

    public static List<CommentEntity> getCommentEntityList(List<Map<String, Object>> rawCommentList) {
        List<CommentEntity> commentEntityList = new ArrayList<CommentEntity>();
        if (null != rawCommentList) {
            for (int i = 0; i < rawCommentList.size(); i++) {
                Map<String, Object> rawCircle = rawCommentList.get(i);
                CommentEntity comment = getCommentEntity(rawCircle);
                if (null != comment) {
                    commentEntityList.add(comment);
                }
            }
        }
        return commentEntityList;

    }

    private static PicEntity getCommentPicEntity(Map<String, Object> rawPic) {
        PicEntity pic = new PicEntity();
        if (null != rawPic) {
            pic.setId((TypeUtil.getId(rawPic.get(APIKey.COMMON_ID))));
            pic.setCommentId(TypeUtil.getId(rawPic.get(APIKey.PIC_THREAD_COMMENT_ID)));
            pic.setCreatedAt(TypeUtil.getString(rawPic.get(APIKey.COMMON_CREATED_AT)));
            pic.setPicUrl(TypeUtil.getString(rawPic.get(APIKey.PIC_THREAD_COMMENT_PIC)));

            return pic;
        }
        return null;
    }

    public static List<PicEntity> getCommentPicEntityList(List<Map<String, Object>> rawPicList) {
        List<PicEntity> picEntityList = new ArrayList<PicEntity>();
        if (null != rawPicList) {
            for (int i = 0; i < rawPicList.size(); i++) {
                Map<String, Object> rawPic = rawPicList.get(i);
                PicEntity pic = getCommentPicEntity(rawPic);
                if (null != pic) {
                    picEntityList.add(pic);
                }
            }
        }
        return picEntityList;

    }

    private static PicEntity getPostsPicEntity(Map<String, Object> rawPic) {
        if (null != rawPic) {
            PicEntity pic = new PicEntity();
            pic.setId(TypeUtil.getId(rawPic.get(APIKey.COMMON_ID)));
            pic.setPostsId(TypeUtil.getId(rawPic.get(APIKey.PIC_THREAD_ID)));
            pic.setCreatedAt(TypeUtil.getString(rawPic.get(APIKey.COMMON_CREATED_AT)));
            pic.setPicUrl(TypeUtil.getString(rawPic.get(APIKey.PIC_THREAD_PIC)));

            return pic;
        }
        return null;
    }

    public static List<PicEntity> getPostsPicEntityList(List<Map<String, Object>> rawPicList) {
        List<PicEntity> picEntityList = new ArrayList<PicEntity>();
        if (null != rawPicList) {
            for (int i = 0; i < rawPicList.size(); i++) {
                Map<String, Object> rawPic = rawPicList.get(i);
                PicEntity pic = getPostsPicEntity(rawPic);
                if (null != pic) {
                    picEntityList.add(pic);
                }
            }

        }
        return picEntityList;

    }

    public static UserEntity getCircleMemberEntity(Map<String, Object> forumUserMap) {
        if (null != forumUserMap) {
            Map<String, Object> userMap = TypeUtil.getMap(forumUserMap.get(APIKey.USER));
            if (null != userMap) {
                return getUserEntity(userMap);
            }
        }

        return null;
    }

    public static List<UserEntity> getCircleMemberEntityList(List<Map<String, Object>> rawForumUserList) {
        List<UserEntity> userList = new ArrayList<UserEntity>();

        for (int i = 0; i < rawForumUserList.size(); i++) {
            Map<String, Object> forumUserMap = rawForumUserList.get(i);
            if (null != forumUserMap) {
                UserEntity user = getCircleMemberEntity(forumUserMap);
                if (null != user) {
                    userList.add(user);
                }
            }
        }

        return userList;
    }

    public static UserEntity getMyFollowEntity(Map<String, Object> followUserMap) {
        if (null != followUserMap) {
            Map<String, Object> userMap = TypeUtil.getMap(followUserMap.get(APIKey.USER));
            if (null != userMap) {
                return getUserEntity(userMap);
            }
        }

        return null;
    }

    public static List<UserEntity> getMyFollowEntityList(List<Map<String, Object>> myFollowUserList) {
        List<UserEntity> userList = new ArrayList<UserEntity>();

        for (int i = 0; i < myFollowUserList.size(); i++) {
            Map<String, Object> followUserMap = myFollowUserList.get(i);
            if (null != followUserMap) {
                UserEntity user = getMyFollowEntity(followUserMap);
                if (null != user) {
                    userList.add(user);
                }
            }
        }

        return userList;
    }

    public static UserEntity getMyFansEntity(Map<String, Object> fansUserMap) {
        if (null != fansUserMap) {
            Map<String, Object> userMap = TypeUtil.getMap(fansUserMap.get(APIKey.FOLLOWER));
            if (null != userMap) {
                return getUserEntity(userMap);
            }
        }

        return null;
    }

    public static List<UserEntity> getMyFansEntityList(List<Map<String, Object>> myFansUserList) {
        List<UserEntity> userList = new ArrayList<UserEntity>();

        for (int i = 0; i < myFansUserList.size(); i++) {
            Map<String, Object> fansUserMap = myFansUserList.get(i);
            if (null != fansUserMap) {
                UserEntity user = getMyFansEntity(fansUserMap);
                if (null != user) {
                    userList.add(user);
                }
            }
        }

        return userList;
    }

    public static CircleEntity getMyFollowCircle(Map<String, Object> forumUserMap) {
        Map<String, Object> forum = TypeUtil.getMap(forumUserMap.get(APIKey.FORUM));
        if (null != forum) {
            CircleEntity circle = EntityUtils.getCircleEntity(forum);

            return circle;
        }

        return null;
    }

    public static List<CircleEntity> getMyFollowCircleList(List<Map<String, Object>> forumUsers) {
        List<CircleEntity> myFollowCircleList = new ArrayList<CircleEntity>();

        for (int i = 0; i < forumUsers.size(); i++) {
            Map<String, Object> forumUserMap = forumUsers.get(i);
            if (null != forumUserMap) {
                CircleEntity circle = getMyFollowCircle(forumUserMap);
                if (null != circle) {
                    myFollowCircleList.add(circle);
                }
            }
        }

        return myFollowCircleList;
    }

    public static PlanEntity getPlanEntity(Map<String, Object> planMap) {
        PlanEntity plan = null;
        if (null != planMap) {
            plan = new PlanEntity();
            plan.setId(TypeUtil.getId(planMap.get(APIKey.COMMON_ID)));
            plan.setCons(TypeUtil.getString(planMap.get(APIKey.PLAN_CONS)));
            plan.setPros(TypeUtil.getString(planMap.get(APIKey.PLAN_PROS)));
            plan.setDescription(TypeUtil.getString(planMap.get(APIKey.PLAN_DESCRIPTION)));
            plan.setPreview(TypeUtil.getString(planMap.get(APIKey.PLAN_PREVIEW)));
            plan.setTitle(TypeUtil.getString(planMap.get(APIKey.PLAN_TITLE)));
            plan.setCreatedAt(TypeUtil.getString(planMap.get(APIKey.COMMON_CREATED_AT)));
            plan.setDayCount(TypeUtil.getInteger(planMap.get(APIKey.PLAN_DAY_COUNT), 0));
            plan.setFollowCount(TypeUtil.getInteger(planMap.get(APIKey.PLAN_FOLLOW_COUNT), 0));
        }

        return plan;
    }

    public static List<PlanEntity> getPlanEntityList(List<Map<String, Object>> rawPlanList) {
        List<PlanEntity> planList = new ArrayList<PlanEntity>();

        for (int i = 0; i < rawPlanList.size(); i++) {
            Map<String, Object> planMap = rawPlanList.get(i);
            if (null != planMap) {
                PlanEntity plan = getPlanEntity(planMap);
                if (null != plan) {
                    planList.add(plan);
                }
            }
        }

        return planList;
    }

    public static PostCollectionEntity getPostCollectionEntity(Map<String, Object> postCollectionMap) {
        PostCollectionEntity postCollection = null;

        if (null != postCollectionMap) {
            postCollection = new PostCollectionEntity();

            postCollection.setId(TypeUtil.getId(postCollectionMap.get(APIKey.COMMON_ID)));
            postCollection.setCreatedAt(TypeUtil.getString(postCollectionMap.get(APIKey.COMMON_CREATED_AT)));

            UserEntity user = getUserEntity(TypeUtil.getMap(postCollectionMap.get(APIKey.USER)));
            if (null != user) {
                postCollection.setUser(user);
            }

            PostsEntity post = getPostsEntity(TypeUtil.getMap(postCollectionMap.get(APIKey.THREAD)));
            if (null != post) {
                postCollection.setPost(post);
            }
        }

        return postCollection;
    }

    public static List<PostCollectionEntity> getPostCollectionEntityList(List<Map<String, Object>> postCollectionMapList) {
        List<PostCollectionEntity> postCollectionList = new ArrayList<PostCollectionEntity>();

        for (int i = 0; i < postCollectionMapList.size(); i++) {
            Map<String, Object> postCollectionMap = postCollectionMapList.get(i);
            if (null != postCollectionMap) {
                PostCollectionEntity postCollection = getPostCollectionEntity(postCollectionMap);
                if (null != postCollection) {
                    postCollectionList.add(postCollection);
                }
            }
        }

        return postCollectionList;
    }

    public static PlanDetailEntity getPlanDetailEntity(Map<String, Object> planDetailMap) {
        PlanDetailEntity planDetailEntity = null;

        if (null != planDetailMap) {
            planDetailEntity = new PlanDetailEntity();

            planDetailEntity.setId(TypeUtil.getId(planDetailMap.get(APIKey.COMMON_ID)));
            planDetailEntity.setPlanId(TypeUtil.getId(planDetailMap.get(APIKey.PLAN_ID)));
            planDetailEntity.setDay(TypeUtil.getInteger(planDetailMap.get(APIKey.PLAN_DAY), 0));
            planDetailEntity.setContent(TypeUtil.getString(planDetailMap.get(APIKey.PLAN_CONTENT), ""));
            planDetailEntity.setCreatedAt(TypeUtil.getString(planDetailMap.get(APIKey.COMMON_CREATED_AT), ""));
        }

        return planDetailEntity;
    }

    public static List<PlanDetailEntity> getPlanDetailEntityList(List<Map<String, Object>> planDetailMapList) {
        List<PlanDetailEntity> planDetailEntityList = new ArrayList<PlanDetailEntity>();

        for (int i = 0; i < planDetailMapList.size(); i++) {
            Map<String, Object> planDetailMap = planDetailMapList.get(i);
            if (null != planDetailMap) {
                PlanDetailEntity planDetail = getPlanDetailEntity(planDetailMap);
                if (null != planDetail) {
                    planDetailEntityList.add(planDetail);
                }
            }
        }

        return planDetailEntityList;
    }

    public static MedicalRecordEntity getMedicalRecordEntity(Map<String, Object> medicalRecordMap) {
        MedicalRecordEntity medicalRecordEntity = null;

        if (null != medicalRecordMap) {
            medicalRecordEntity = new MedicalRecordEntity();

            medicalRecordEntity.setId(TypeUtil.getId(medicalRecordMap.get(APIKey.COMMON_ID)));
            medicalRecordEntity.setUserId(TypeUtil.getId(medicalRecordMap.get(APIKey.USER_ID)));
            medicalRecordEntity.setCreatedAt(TypeUtil.getString(medicalRecordMap.get(APIKey.COMMON_CREATED_AT), ""));
            medicalRecordEntity.setOperationTime(TypeUtil.getString(medicalRecordMap.get(APIKey.MEDICAL_RECORD_OPERATION_TIME), ""));

            medicalRecordEntity.setLeukocyte(TypeUtil.getFloat(medicalRecordMap.get(APIKey.MEDICAL_RECORD_LEUKOCYTE), 0f));
            medicalRecordEntity.setLeukomonocyte(TypeUtil.getFloat(medicalRecordMap.get(APIKey.MEDICAL_RECORD_LEUKOMONOCYTE), 0f));
            medicalRecordEntity.setErythrocyte(TypeUtil.getFloat(medicalRecordMap.get(APIKey.MEDICAL_RECORD_ERYTHROCYTE), 0f));
            medicalRecordEntity.setHemoglobin(TypeUtil.getFloat(medicalRecordMap.get(APIKey.MEDICAL_RECORD_HEMOGLOBIN), 0f));
            medicalRecordEntity.setPrealbumin(TypeUtil.getFloat(medicalRecordMap.get(APIKey.MEDICAL_RECORD_PREALBUMIN), 0f));
            medicalRecordEntity.setSiderophilin(TypeUtil.getFloat(medicalRecordMap.get(APIKey.MEDICAL_RECORD_SIDEROPHILIN), 0f));
            medicalRecordEntity.setFastingBloodGlucose(TypeUtil.getFloat(medicalRecordMap.get(APIKey.MEDICAL_RECORD_FASTING_BLOOD_GLUCOSE), 0f));
            medicalRecordEntity.setTwoBloodGlucose(TypeUtil.getFloat(medicalRecordMap.get(APIKey.MEDICAL_RECORD_TWO_BLOOD_GLUCOSE), 0f));
            medicalRecordEntity.setGlycosylatedHemoglobin(TypeUtil.getFloat(medicalRecordMap.get(APIKey.MEDICAL_RECORD_GLYCOSYLATED_HEMOGLOBIN), 0f));
            medicalRecordEntity.setFastingInsulin(TypeUtil.getFloat(medicalRecordMap.get(APIKey.MEDICAL_RECORD_FASTING_INSULIN), 0f));
            medicalRecordEntity.setFastingCPeptide(TypeUtil.getFloat(medicalRecordMap.get(APIKey.MEDICAL_RECORD_FASTING_C_PEPTIDE), 0f));
            medicalRecordEntity.setTwoInsulin(TypeUtil.getFloat(medicalRecordMap.get(APIKey.MEDICAL_RECORD_TWO_INSULIN), 0f));
            medicalRecordEntity.setTwoCPeptide(TypeUtil.getFloat(medicalRecordMap.get(APIKey.MEDICAL_RECORD_TWO_C_PEPTIDE), 0f));
            medicalRecordEntity.setAlbumin(TypeUtil.getFloat(medicalRecordMap.get(APIKey.MEDICAL_RECORD_ALBUMIN), 0f));
            medicalRecordEntity.setGlycerinTrilaurate(TypeUtil.getFloat(medicalRecordMap.get(APIKey.MEDICAL_RECORD_GLYCERIN_TRILAURATE), 0f));
            medicalRecordEntity.setCholesterol(TypeUtil.getFloat(medicalRecordMap.get(APIKey.MEDICAL_RECORD_CHOLESTEROL), 0f));
            medicalRecordEntity.setLdl(TypeUtil.getFloat(medicalRecordMap.get(APIKey.MEDICAL_RECORD_LDL), 0f));
            medicalRecordEntity.setHdl(TypeUtil.getFloat(medicalRecordMap.get(APIKey.MEDICAL_RECORD_HDL), 0f));
            medicalRecordEntity.setUricAcid(TypeUtil.getFloat(medicalRecordMap.get(APIKey.MEDICAL_RECORD_URIC_ACID), 0f));
            medicalRecordEntity.setGpt(TypeUtil.getFloat(medicalRecordMap.get(APIKey.MEDICAL_RECORD_GPT), 0f));
            medicalRecordEntity.setGot(TypeUtil.getFloat(medicalRecordMap.get(APIKey.MEDICAL_RECORD_GOT), 0f));
            medicalRecordEntity.setGgt(TypeUtil.getFloat(medicalRecordMap.get(APIKey.MEDICAL_RECORD_GGT), 0f));
            medicalRecordEntity.setUseaNitrogen(TypeUtil.getFloat(medicalRecordMap.get(APIKey.MEDICAL_RECORD_USEA_NITROGEN), 0f));
            medicalRecordEntity.setCreatinine(TypeUtil.getFloat(medicalRecordMap.get(APIKey.MEDICAL_RECORD_CREATININE), 0f));
            medicalRecordEntity.setUrineTraceAlbumin(TypeUtil.getFloat(medicalRecordMap.get(APIKey.MEDICAL_RECORD_URINE_TRACE_ALBUMIN), 0f));
        }

        return medicalRecordEntity;
    }

    public static List<MedicalRecordEntity> getMedicalRecordEntityList(List<Map<String, Object>> medicalRecordMapList) {
        List<MedicalRecordEntity> medicalRecordEntityList = new ArrayList<MedicalRecordEntity>();
        if (null != medicalRecordMapList) {
            for (int i = 0; i < medicalRecordMapList.size(); i++) {
                Map<String, Object> medicalRecordMap = medicalRecordMapList.get(i);
                if (null != medicalRecordMap) {
                    MedicalRecordEntity medicalRecordEntity = getMedicalRecordEntity(medicalRecordMap);
                    if (null != medicalRecordEntity) {
                        medicalRecordEntityList.add(medicalRecordEntity);
                    }
                }
            }
        }

        return medicalRecordEntityList;
    }

    public static MedicalRecordPicEntity getMedicalRecordPicEntity(Map<String, Object> medicalRecordPicMap) {
        MedicalRecordPicEntity medicalRecordPicEntity = null;

        if (null != medicalRecordPicMap) {
            medicalRecordPicEntity = new MedicalRecordPicEntity();

            medicalRecordPicEntity.setId(TypeUtil.getId(medicalRecordPicMap.get(APIKey.COMMON_ID)));
            medicalRecordPicEntity.setMedicalRecordPic(TypeUtil.getString(medicalRecordPicMap.get(APIKey.MEDICAL_RECORD_PIC), ""));
            medicalRecordPicEntity.setCreatedAt(TypeUtil.getString(medicalRecordPicMap.get(APIKey.COMMON_CREATED_AT), ""));
            medicalRecordPicEntity.setUserId(TypeUtil.getString(medicalRecordPicMap.get(APIKey.USER_ID), ""));

            Map<String, Object> userMap = TypeUtil.getMap(medicalRecordPicMap.get(APIKey.USER));
            if (null != userMap) {
                UserEntity user = getUserEntity(userMap);
                if (null != user) {
                    medicalRecordPicEntity.setUser(user);
                }

            }
        }

        return medicalRecordPicEntity;
    }

    public static List<MedicalRecordPicEntity> getMedicalRecordPicEntityList(List<Map<String, Object>> medicalRecordPicMapList) {
        List<MedicalRecordPicEntity> medicalRecordPicEntityList = new ArrayList<MedicalRecordPicEntity>();
        if (null != medicalRecordPicMapList) {
            for (int i = 0; i < medicalRecordPicMapList.size(); i++) {
                Map<String, Object> medicalRecordPicMap = medicalRecordPicMapList.get(i);
                if (null != medicalRecordPicMap) {
                    MedicalRecordPicEntity medicalRecordPicEntity = getMedicalRecordPicEntity(medicalRecordPicMap);
                    if (null != medicalRecordPicEntity) {
                        medicalRecordPicEntityList.add(medicalRecordPicEntity);
                    }
                }
            }
        }

        return medicalRecordPicEntityList;
    }

    public static List<BannerEntity> getBannerEntityList(List<Map<String, Object>> bannerMapList) {
        List<BannerEntity> bannerEntityList = new ArrayList<BannerEntity>();
        if (null != bannerMapList) {
            for (int i = 0; i < bannerMapList.size(); i++) {
                Map<String, Object> bannerMap = bannerMapList.get(i);
                if (null != bannerMap) {
                    BannerEntity bannerEntity = getBannerEntity(bannerMap);
                    if (null != bannerEntity) {
                        bannerEntityList.add(bannerEntity);
                    }
                }
            }
        }

        return bannerEntityList;
    }

    private static BannerEntity getBannerEntity(Map<String, Object> bannerMap) {
        BannerEntity bannerEntity = null;
        if (null != bannerMap) {
            bannerEntity = new BannerEntity();

            bannerEntity.setId(TypeUtil.getId(bannerMap.get(APIKey.COMMON_ID)));
            bannerEntity.setBannerTitle(TypeUtil.getString(bannerMap.get(APIKey.FORUM_BANNER_TITLE)));
            bannerEntity.setBannerBgUrl(TypeUtil.getString(bannerMap.get(APIKey.FORUM_BANNER_URL)));
            bannerEntity.setContentUrl(TypeUtil.getString(bannerMap.get(APIKey.FORUM_CONTENT_URL)));
            bannerEntity.setCreatedAt(TypeUtil.getString(bannerMap.get(APIKey.COMMON_CREATED_AT)));
            bannerEntity.setStatus(TypeUtil.getInteger(bannerMap.get(APIKey.COMMON_STATUS), 0));
        }
        return bannerEntity;
    }

}
