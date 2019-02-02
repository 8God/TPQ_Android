package com.zcmedical.tangpangquan.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

import com.zcmedical.common.base.BaseActivity;
import com.zcmedical.common.base.TpqApplication;
import com.zcmedical.common.component.ActivityManager;
import com.zcmedical.common.component.BasicDialog;
import com.zcmedical.common.component.PictureUploader;
import com.zcmedical.common.constant.APIKey;
import com.zcmedical.common.constant.CommonConstant;
import com.zcmedical.common.constant.InterfaceConstant;
import com.zcmedical.common.network.CommonRequest;
import com.zcmedical.common.utils.EntityUtils;
import com.zcmedical.common.utils.TypeUtil;
import com.zcmedical.common.utils.UserHeadPicUploader;
import com.zcmedical.common.utils.UserHeadPicUploader.OnUserHeadPicUpdateCallback;
import com.zcmedical.common.utils.Validator;
import com.zcmedical.common.widget.ListDialogBuilder;
import com.zcmedical.common.widget.citylist.CityListActivity;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.entity.UserEntity;

public class UserInfoDetailActivity extends BaseActivity implements OnClickListener {
    private static final int REQUEST_CODE_UPDATE_NICKNAME = 671;
    private static final int REQUEST_CODE_UPDATE_USERNAME = 672;
    private static final int REQUEST_CODE_SELECT_CITY = 64;

    private SimpleDateFormat birthdayFormat = new SimpleDateFormat("yyyy年MM月dd日");

    private UserEntity user;
    private UserHeadPicUploader userHeadPicUploader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_info_detail);

        init();
    }

    private void init() {
        initToolbar("我的资料");
        initUI();
//        fetchNewestUserInfo();
    }

    private void fetchNewestUserInfo() {
        String userId = TpqApplication.getInstance().getUserId();
        CommonRequest fetchUserInfo = new CommonRequest();
        fetchUserInfo.setRequestApiName(InterfaceConstant.API_USER_FETCH);
        fetchUserInfo.setRequestID(InterfaceConstant.REQUEST_ID_USER_FETCH);
        fetchUserInfo.addRequestParam(APIKey.COMMON_ID, userId);

        addRequestAsyncTask(fetchUserInfo);
    }

    private void initUI() {
        setViewClickListener(R.id.rl_user_head, this);
        setViewClickListener(R.id.rl_nick_name, this);
        setViewClickListener(R.id.rl_account_safety, this);
        setViewClickListener(R.id.rl_my_level, this);
        setViewClickListener(R.id.rl_username, this);
        setViewClickListener(R.id.rl_sex, this);
        setViewClickListener(R.id.rl_marital_status, this);
        setViewClickListener(R.id.rl_birthday, this);
        setViewClickListener(R.id.rl_city, this);
        setViewClickListener(R.id.btn_logout, this);
        initUserInfo();
    }

    private void initUserInfo() {
        user = TpqApplication.getInstance().getUser();
        if (null != user) {
            Log.i("cth", "user.getHeadPic() = " + user.getHeadPic());
            setImageView(R.id.cimv_user_head, user.getHeadPic(), R.drawable.common_icon_default_user_head);

            String sex = "未知";
            switch (user.getSex()) {
                case APIKey.SEX_MALE:
                    sex = "男";
                    break;
                case APIKey.SEX_FEMALE:
                    sex = "女";
                    break;
                default:
                    break;
            }
            String maritalStatus = "未婚";
            switch (user.getMaritalStatus()) {
                case APIKey.MARITAL_STATUS_UNMARRIED:
                    maritalStatus = getContext().getString(R.string.unmarried);
                    break;
                case APIKey.MARITAL_STATUS_MARRIED:
                    maritalStatus = getContext().getString(R.string.married);
                    break;
                default:
                    maritalStatus = getContext().getString(R.string.unknown);
                    break;
            }

            String birthday = "";
            try {
                birthday = user.getBirthday();
                if (!TextUtils.isEmpty(birthday)) {
                    Date birthDate = CommonConstant.serverTimeFormat.parse(birthday);
                    birthday = birthdayFormat.format(birthDate);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            setTextView(R.id.tv_nick_name, user.getNickname());
            setTextView(R.id.tv_account_safety, user.getMobile());
            setTextView(R.id.tv_my_level, "Lv." + user.getLevel());
            setTextView(R.id.tv_username, user.getUsername());
            setTextView(R.id.tv_sex, sex);
            setTextView(R.id.tv_marital_status, maritalStatus);
            setTextView(R.id.tv_birthday, birthday);
            setTextView(R.id.tv_city, user.getCity());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_user_head:
                if (null == userHeadPicUploader) {
                    userHeadPicUploader = new UserHeadPicUploader(getContext());

                    userHeadPicUploader.setOnUserHeadPicUpdateCallback(new OnUserHeadPicUpdateCallback() {

                        @Override
                        public void onUserHeadPicUpdate(UserEntity userEntity) {
                            if (null != userEntity) {
                                setImageView(R.id.cimv_user_head, userEntity.getHeadPic(), R.drawable.common_icon_default_user_head);
                                if (null != user) {
                                    user.setHeadPic(userEntity.getHeadPic());
                                }
                            }
                        }
                    });
                }
                userHeadPicUploader.showPickerPicDialog();
                break;
            case R.id.rl_nick_name:
                Intent updateNickName = new Intent(getContext(), NickNameSettingActivity.class);
                startActivityForResult(updateNickName, REQUEST_CODE_UPDATE_NICKNAME);
                break;
            case R.id.rl_account_safety:
                Intent openAccountSatety = new Intent(getContext(), AccountSafetyActivity.class);
                startActivity(openAccountSatety);
                break;
            case R.id.rl_my_level:
                startActivity(new Intent(getContext(), MyLevelActivity.class));
                break;
            case R.id.rl_username:
                Intent updateUserName = new Intent(getContext(), UserNameSettingActivity.class);
                startActivityForResult(updateUserName, REQUEST_CODE_UPDATE_USERNAME);
                break;
            case R.id.rl_sex:
                List<String> sexList = new ArrayList<String>();
                sexList.add("男性");
                sexList.add("女性");
                ListDialogBuilder<String> sexlistDialogBuilder = new ListDialogBuilder<String>(getContext(), sexList, "我的性别");
                final BasicDialog sexDialog = sexlistDialogBuilder.create();
                sexlistDialogBuilder.setmOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        int sex = APIKey.SEX_MALE;
                        String sexStr = "男";
                        if (position == 0) {
                            sex = APIKey.SEX_MALE;
                            sexStr = "男";
                        } else if (position == 1) {
                            sex = APIKey.SEX_FEMALE;
                            sexStr = "女";
                        }
                        user.setSex(sex);

                        setTextView(R.id.tv_sex, sexStr);
                        sexDialog.dismiss();

                    }
                });
                sexDialog.show();
                break;
            case R.id.rl_marital_status:
                List<String> maritalList = new ArrayList<String>();
                maritalList.add("未婚");
                maritalList.add("已婚");
                ListDialogBuilder<String> maritalListDialogBuilder = new ListDialogBuilder<String>(getContext(), maritalList, "婚姻状况");
                final BasicDialog maritalDialog = maritalListDialogBuilder.create();
                maritalListDialogBuilder.setmOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        int maritalStatus = APIKey.MARITAL_STATUS_UNMARRIED;
                        String maritalStr = "未婚";
                        if (position == 0) {
                            maritalStatus = APIKey.MARITAL_STATUS_UNMARRIED;
                            maritalStr = "未婚";
                        } else if (position == 1) {
                            maritalStatus = APIKey.MARITAL_STATUS_MARRIED;
                            maritalStr = "已婚";
                        }
                        user.setMaritalStatus(maritalStatus);

                        setTextView(R.id.tv_marital_status, maritalStr);
                        maritalDialog.dismiss();

                    }
                });
                maritalDialog.show();
                break;
            case R.id.rl_birthday:
                showBirthdaySelectorDialog();
                break;
            case R.id.rl_city:
                Intent openCityList = new Intent(getContext(), CityListActivity.class);
                startActivityForResult(openCityList, REQUEST_CODE_SELECT_CITY);
                break;
            case R.id.btn_logout:
                Logout();
                break;

            default:
                break;
        }
    }

    /**
     * 显示选择生日Dialog
     */
    private void showBirthdaySelectorDialog() {
        //初始化日期
        Date birthday = new Date();
        if (null != user) {
            String birthStr = user.getBirthday();
            try {
                birthday = CommonConstant.serverTimeFormat.parse(birthStr);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(birthday);
        int year = calendar.get(Calendar.YEAR);
        int monthOfYear = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                Date birthday = calendar.getTime();
                String birthStr = CommonConstant.serverTimeFormat.format(birthday);
                user.setBirthday(birthStr);

                birthStr = birthdayFormat.format(birthday);
                setTextView(R.id.tv_birthday, birthStr);

            }
        }, year, monthOfYear, dayOfMonth);

        datePickerDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_UPDATE_NICKNAME) {
            if (resultCode == RESULT_OK) {
                String nickname = data.getStringExtra(APIKey.USER_NICKNAME);
                if (!TextUtils.isEmpty(nickname)) {
                    setTextView(R.id.tv_nick_name, nickname);
                    if (null != user) {
                        user.setNickname(nickname);
                    }
                }
            }
        } else if (requestCode == REQUEST_CODE_UPDATE_USERNAME) {
            if (resultCode == RESULT_OK) {
                String username = data.getStringExtra(APIKey.USER_USERNAME);
                if (!TextUtils.isEmpty(username)) {
                    setTextView(R.id.tv_username, username);
                    if (null != user) {
                        user.setUsername(username);
                    }
                }
            }
        } else if (requestCode == REQUEST_CODE_SELECT_CITY) {
            if (resultCode == RESULT_OK) {
                String city = data.getStringExtra(APIKey.USER_CITY);
                if (!TextUtils.isEmpty(city)) {
                    setTextView(R.id.tv_city, city);
                    if (null != user) {
                        user.setCity(city);
                    }
                }
            }
        } else if (requestCode == PictureUploader.REQUESTCODE_TAKE_PHOTO || requestCode == PictureUploader.REQUESTCODE_PICK_PHOTO) {
            userHeadPicUploader.uploadPicture(requestCode, resultCode, data);
        }
    }

    private void updateUserInfo() {
        if (null != user) {
            CommonRequest updateUserInfo = new CommonRequest();
            updateUserInfo.setRequestApiName(InterfaceConstant.API_USER_INFO_UPDATE);
            updateUserInfo.addRequestParam(APIKey.COMMON_ID, user.getId());
            if (!TextUtils.isEmpty(user.getUsername())) {
                updateUserInfo.addRequestParam(APIKey.USER_USERNAME, user.getUsername());
            }
            if (!TextUtils.isEmpty(user.getBirthday())) {
                updateUserInfo.addRequestParam(APIKey.USER_BIRTHDAY, user.getBirthday());
            }
            if (!TextUtils.isEmpty(user.getCity())) {
                updateUserInfo.addRequestParam(APIKey.USER_CITY, user.getCity());
            }
            updateUserInfo.addRequestParam(APIKey.USER_SEX, user.getSex());
            updateUserInfo.addRequestParam(APIKey.USER_MARITAL_STATUS, user.getMaritalStatus());

            addRequestAsyncTask(updateUserInfo);
        }
    }

    private void Logout() {
        TpqApplication.getInstance().setLogon(false);

        ActivityManager.getInstance().clearAllActivity();
        
        removeThirdLogonAuth();

        Intent openLogon = new Intent(getContext(), LogonActivity.class);
        startActivity(openLogon);
    }

    private void removeThirdLogonAuth() {
        Platform qq =  ShareSDK.getPlatform(QQ.NAME);
        if(qq.isValid()) {
            qq.removeAccount();
        }
        
        Platform wechat =  ShareSDK.getPlatform(Wechat.NAME);
        if(wechat.isValid()) {
            wechat.removeAccount();
        }
        
        Platform weibo =  ShareSDK.getPlatform(SinaWeibo.NAME);
        if(weibo.isValid()) {
            weibo.removeAccount();
        }
    }

    @Override
    protected void onResponseAsyncTaskRender(int status, String message, int toBeContinued, Map<String, Object> resultMap, String requestID, Map<String, Object> additionalArgsMap) {
        super.onResponseAsyncTaskRender(status, message, toBeContinued, resultMap, requestID, additionalArgsMap);
        if (InterfaceConstant.REQUEST_ID_USER_FETCH.equals(requestID)) {
            if (status == APIKey.STATUS_SUCCESSFUL) {
                if (null != resultMap) {
                    List<Map<String, Object>> userMapList = TypeUtil.getList(resultMap.get(APIKey.USERS));
                    if (null != userMapList && userMapList.size() > 0) {
                        List<UserEntity> userList = EntityUtils.getUserEntityList(userMapList);
                        if (null != userList && userList.size() > 0) {
                            UserEntity user = userList.get(0);
                            if (null != user) {
                                String myUserId = TpqApplication.getInstance().getUserId();
                                String userId = user.getId();
                                if (Validator.isIdValid(myUserId) && Validator.isIdValid(myUserId) && myUserId.equals(userId)) {
                                    TpqApplication.getInstance().setUser(user);
                                    initUserInfo();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void finish() {
        if (TpqApplication.getInstance().isLogon()) {//登录状态才更新用户数据
            TpqApplication.getInstance().setUser(user);
            updateUserInfo(); //退出该界面把修改的信息更新到服务器
        }

        super.finish();
    }

}
