package com.zcmedical.common.utils;

import com.zcmedical.tangpangquan.R;

import android.content.Context;

public class LevelUtils {

    /**
     * 根据积分获取对应的等级
     * 
     * @param context
     * @param integral
     *            积分
     * @return
     */
    public static int getLevelByIntegral(Context context, int integral) {
        int level = 1;

        int[] levelArray = context.getResources().getIntArray(R.array.levelArray);

        for (int i = 0; i < levelArray.length; i++) {
            if (integral >= levelArray[i]) {
                level++;
            } else {
                break;
            }
        }

        return level;
    }

    /**
     * 获取下一级需要的积分
     * 
     * @param context
     * @param currentLevel
     *            当前等级
     * @return
     */
    public static int getNextLevelIntegral(Context context, int currentLevel) {
        int[] levelArray = context.getResources().getIntArray(R.array.levelArray);

        int nextLevelIntegral = 0;
        if (currentLevel > 0 && (currentLevel - 1) < levelArray.length) {
            nextLevelIntegral = levelArray[currentLevel - 1];
        } else if ((currentLevel - 1) >= levelArray.length) {
            nextLevelIntegral = levelArray[levelArray.length - 1];
        }

        return nextLevelIntegral;
    }

}
