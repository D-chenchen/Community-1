package com.community.utils;

public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    // 关注的目标
    private static final String PREFIX_FOLLOWEE = "followee";
    // 被关注目标的粉丝
    private static final String PREFIX_FOLLOWER = "follower";

    /**
     * 某个实体的赞
     * like:entity:entityType:entityId -> set(userId)
     */
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 某个用户的赞
     * like:user:userId -> int
     */
    public static String getUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    /**
     * 某个用户关注的实体(某个用户关注了某个实体),存在有序列表 zset 中,以当前时间排序
     * followee:userId:entityType -> zset(entityId, now)
     */
    public static String getFolloweeKey(int userId, int entityType) {
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    /**
     * 某个用户拥有的粉丝
     * follower:entityType:entityId -> zset(userId, now)
     */
    public static String getFollowerKey(int entityType, int entityId) {
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }
}