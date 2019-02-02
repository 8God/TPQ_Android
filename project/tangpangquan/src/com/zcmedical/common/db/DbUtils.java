package com.zcmedical.common.db;

import java.util.List;

import android.content.Context;
import android.util.Log;

import com.zcmedical.common.base.TpqApplication;
import com.zcmedical.common.db.DaoMaster.DevOpenHelper;

import de.greenrobot.dao.query.QueryBuilder;

/**  

* @Title: DbUtils.java 

* @Package com.zcmedical.common.db 

* @Description: 数据库工具类，这里集合对所有库的增删改查

* @author issaclam  

* @date 2015-07-11 下午2:33:24 

* @version V1.0  

*/
public class DbUtils {

    private final static String TAG = "DbUtils";

    private BloodSugarDao bloodSugarDao;
    private WeightDao weightDao;
    private static DbUtils instance;
    private static Context mContext;
    private static DevOpenHelper helper;
    private static DaoMaster daoMaster;
    private static DaoSession daoSession;
    private static String DbName = "uid";
    private static final int ONE_DAY_TIMESTAMP = 86399;

    private DbUtils() {
        // TODO Auto-generated constructor stub
    }

    public static DbUtils getInstance(Context context) {
        if (!DbName.equals("uid" + TpqApplication.getInstance().getUserId())) {
            DbName = "uid" + TpqApplication.getInstance().getUserId();
            instance = null;
            daoSession = null;
            daoMaster = null;
        }
        if (instance == null) {
            synchronized (DbUtils.class) {
                if (instance == null) {
                    instance = new DbUtils();
                    if (mContext == null) {
                        mContext = context;
                    }
                    DaoSession daoSession = getDaoSession(mContext);
                    instance.bloodSugarDao = daoSession.getBloodSugarDao();
                    instance.weightDao = daoSession.getWeightDao();
                }
            }
        }
        return instance;
    }

    public static DaoMaster getDaoMaster(Context context) {
        if (daoMaster == null) {
            helper = new DaoMaster.DevOpenHelper(mContext, DbName, null);
            Log.d(TAG, "DbName : " + DbName);
            daoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return daoMaster;
    }

    public static DaoSession getDaoSession(Context context) {
        if (daoSession == null) {
            if (daoMaster == null) {
                daoMaster = getDaoMaster(context);
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }

    /** 清空数据库操作类实例 */
    public void exitDb() {
        instance = null;
        daoSession = null;
        daoMaster = null;
    }

    //体重
    /**
     * insert a new record with id = null
     */
    public void insertNewWeight(Weight weight) {
        weightDao.insert(weight);
    }

    public void insertSeriesWeight(List<Weight> weights) {
        weightDao.insertOrReplaceInTx(weights);
    }

    /**
     * query a record by timestamp,if no one create new,timestamp without hour,mintues,second
     */
    public Weight queryOneWeight(String timestamp) {
        QueryBuilder<Weight> qb = weightDao.queryBuilder();
        long start = Long.parseLong((timestamp.substring(0, 8) + "000000"));
        long end = Long.parseLong((timestamp.substring(0, 8) + "235959"));
        qb.where(WeightDao.Properties.Created_at.between(start, end)).build();
        if (qb.list().size() > 0) {
            return (Weight) qb.list().get(0);
        } else {
            return null;
        }
    }

    public List<Weight> getAllWeight() {
        QueryBuilder<Weight> qb = weightDao.queryBuilder();
        qb.orderAsc(WeightDao.Properties.Created_at);
        return qb.list();
    }

    /**
     * modify
     */
    public void modifyWeight(Weight weight) {
        weightDao.insertOrReplace(weight);
    }

    public List<BloodSugar> queryOneDayBloodSugar(int timestamp) {
        QueryBuilder<BloodSugar> qb = bloodSugarDao.queryBuilder();
//        long start = Long.parseLong((timestamp.substring(0, 8) + "000000"));
//        long end = Long.parseLong((timestamp.substring(0, 8) + "235959"));
        qb.where(BloodSugarDao.Properties.Measure_time.between(timestamp, timestamp+ONE_DAY_TIMESTAMP)).build();
        return qb.list();
    }
    
    /**
     * insert a new record with id = null
     */
    public void insertNewBloodSugar(BloodSugar bs) {
        bloodSugarDao.insert(bs);
    }
    
    public void insertSeriesBloodSugar(List<BloodSugar> bloodSugars) {
        bloodSugarDao.insertOrReplaceInTx(bloodSugars);
    }

}
