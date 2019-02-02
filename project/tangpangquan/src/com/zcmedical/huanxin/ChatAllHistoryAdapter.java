/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zcmedical.huanxin;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.BufferType;

import com.easemob.chat.EMContact;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.DateUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;
import com.zcmedical.common.constant.CommonConstant;
import com.zcmedical.common.constant.InterfaceConstant;
import com.zcmedical.common.utils.JsonUtils;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.entity.Doctor;

/**
 * 显示所有聊天记录adpater
 * 
 */
public class ChatAllHistoryAdapter extends ArrayAdapter<EMConversation> {

    private final static String TAG = "ChatAllHistoryAdapter";
    private LayoutInflater inflater;
    private List<EMConversation> conversationList;
    private List<EMConversation> copyConversationList;
    private ConversationFilter conversationFilter;
    private boolean notiyfyByFilter;
    private Map<String, Doctor> maps = new HashMap<String, Doctor>();
    private AsyncHttpClient client;
    private Context context;

    public ChatAllHistoryAdapter(Context context, int textViewResourceId, List<EMConversation> objects) {
        super(context, textViewResourceId, objects);
        this.conversationList = objects;
        this.context = context;
        copyConversationList = new ArrayList<EMConversation>();
        copyConversationList.addAll(objects);
        //inflater = LayoutInflater.from(context);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //        if (convertView == null) {
        //            //convertView = inflater.inflate(R.layout.row_chat_history, parent, false);
        //            //convertView = inflater.inflate(R.layout.item_chat_history, parent, false);
        //            convertView = inflater.inflate(R.layout.item_chat_history, null);
        //        }
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_chat_history, null);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.unreadLabel = (TextView) convertView.findViewById(R.id.unread_msg_number);
            holder.message = (TextView) convertView.findViewById(R.id.message);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
            holder.msgState = convertView.findViewById(R.id.msg_state);
            holder.list_item_layout = (RelativeLayout) convertView.findViewById(R.id.list_item_layout);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.list_item_layout.setBackgroundResource(R.drawable.mm_listitem);
        //        if (position % 2 == 0) {
        //            holder.list_item_layout.setBackgroundResource(R.drawable.mm_listitem);
        //        } else {
        //            holder.list_item_layout.setBackgroundResource(R.drawable.mm_listitem_grey);
        //        }

        // 获取与此用户/群组的会话
        final EMConversation conversation = getItem(position);
        // 获取用户username或者群组groupid
        String username = conversation.getUserName();
        List<EMGroup> groups = EMGroupManager.getInstance().getAllGroups();
        EMContact contact = null;
        boolean isGroup = false;
        for (EMGroup group : groups) {
            if (group.getGroupId().equals(username)) {
                isGroup = true;
                contact = group;
                break;
            }
        }
        if (isGroup) {
            // 群聊消息，显示群聊头像
            holder.avatar.setImageResource(R.drawable.group_icon);
            holder.name.setText(contact.getNick() != null ? contact.getNick() : username);
        } else {
            if (username.equals(CommonConstant.GROUP_USERNAME)) {
                holder.name.setText("群聊");

            } else if (username.equals(CommonConstant.NEW_FRIENDS_USERNAME)) {
                holder.name.setText("申请与通知");
            }
            UserUtils.setUserAvatar(getContext(), username, holder.avatar);
            holder.name.setText(username);
            if (!maps.containsKey(conversation.getUserName())) {
                client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                params.add("id", username);
                Log.d(TAG, "username(id) : " + username);
                client.post(InterfaceConstant.DOCTOR_FETCH, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        Log.d(TAG, "DoctorJsonHttpResponseHandler.re : " + response.toString());
                        Type listType = new TypeToken<LinkedList<Doctor>>() {
                        }.getType();
                        Gson gson = new Gson();
                        LinkedList<Doctor> doctors = gson.fromJson((JsonUtils.getOjectString((response.toString()), "doctors")), listType);
                        if (doctors.size() > 0) {
                            //UserUtils.setUserAvatar(getContext(), doctors.get(0).getHead_pic(), holder.avatar);
                            Picasso.with(getContext()).load(doctors.get(0).getHead_pic()).placeholder(R.drawable.default_avatar).into(holder.avatar);
                            holder.name.setText(doctors.get(0).getNickname());
                            maps.put(conversation.getUserName(), doctors.get(0));
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        //Log.d(TAG, "JsonHttpResponseHandler.re : " + errorResponse.toString());
                        Toast.makeText(context, "当前网络状况不好，请稍后再试", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                if (!TextUtils.isEmpty(maps.get(conversation.getUserName()).getHead_pic())) {
                    Picasso.with(getContext()).load(maps.get(conversation.getUserName()).getHead_pic()).placeholder(R.drawable.default_avatar).into(holder.avatar);
                }
                holder.name.setText(maps.get(conversation.getUserName()).getNickname());
            }
        }

        if (conversation.getUnreadMsgCount() > 0) {
            // 显示与此用户的消息未读数
            holder.unreadLabel.setText(String.valueOf(conversation.getUnreadMsgCount()));
            holder.unreadLabel.setVisibility(View.VISIBLE);
        } else {
            holder.unreadLabel.setVisibility(View.INVISIBLE);
        }

        if (conversation.getMsgCount() != 0) {
            // 把最后一条消息的内容作为item的message内容
            EMMessage lastMessage = conversation.getLastMessage();
            holder.message.setText(SmileUtils.getSmiledText(getContext(), getMessageDigest(lastMessage, (this.getContext()))), BufferType.SPANNABLE);

            holder.time.setText(DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));
            if (lastMessage.direct == EMMessage.Direct.SEND && lastMessage.status == EMMessage.Status.FAIL) {
                holder.msgState.setVisibility(View.VISIBLE);
            } else {
                holder.msgState.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    /**
     * 根据消息内容和消息类型获取消息内容提示
     * 
     * @param message
     * @param context
     * @return
     */
    private String getMessageDigest(EMMessage message, Context context) {
        String digest = "";
        switch (message.getType()) {
        case LOCATION: // 位置消息
            if (message.direct == EMMessage.Direct.RECEIVE) {
                // 从sdk中提到了ui中，使用更简单不犯错的获取string的方法
                // digest = EasyUtils.getAppResourceString(context,
                // "location_recv");
                digest = getStrng(context, R.string.location_recv);
                digest = String.format(digest, message.getFrom());
                return digest;
            } else {
                // digest = EasyUtils.getAppResourceString(context,
                // "location_prefix");
                digest = getStrng(context, R.string.location_prefix);
            }
            break;
        case IMAGE: // 图片消息
            ImageMessageBody imageBody = (ImageMessageBody) message.getBody();
            digest = getStrng(context, R.string.picture) + imageBody.getFileName();
            break;
        case VOICE:// 语音消息
            digest = getStrng(context, R.string.voice);
            break;
        case VIDEO: // 视频消息
            digest = getStrng(context, R.string.video);
            break;
        case TXT: // 文本消息
            if (!message.getBooleanAttribute(CommonConstant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
                TextMessageBody txtBody = (TextMessageBody) message.getBody();
                digest = txtBody.getMessage();
            } else {
                TextMessageBody txtBody = (TextMessageBody) message.getBody();
                digest = getStrng(context, R.string.voice_call) + txtBody.getMessage();
            }
            break;
        case FILE: // 普通文件消息
            digest = getStrng(context, R.string.file);
            break;
        default:
            System.err.println("error, unknow type");
            return "";
        }

        return digest;
    }

    private static class ViewHolder {
        /** 和谁的聊天记录 */
        TextView name;
        /** 消息未读数 */
        TextView unreadLabel;
        /** 最后一条消息的内容 */
        TextView message;
        /** 最后一条消息的时间 */
        TextView time;
        /** 用户头像 */
        ImageView avatar;
        /** 最后一条消息的发送状态 */
        View msgState;
        /** 整个list中每一行总布局 */
        RelativeLayout list_item_layout;

    }

    String getStrng(Context context, int resId) {
        return context.getResources().getString(resId);
    }

    @Override
    public Filter getFilter() {
        if (conversationFilter == null) {
            conversationFilter = new ConversationFilter(conversationList);
        }
        return conversationFilter;
    }

    private class ConversationFilter extends Filter {
        List<EMConversation> mOriginalValues = null;

        public ConversationFilter(List<EMConversation> mList) {
            mOriginalValues = mList;
        }

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                mOriginalValues = new ArrayList<EMConversation>();
            }
            if (prefix == null || prefix.length() == 0) {
                results.values = copyConversationList;
                results.count = copyConversationList.size();
            } else {
                String prefixString = prefix.toString();
                final int count = mOriginalValues.size();
                final ArrayList<EMConversation> newValues = new ArrayList<EMConversation>();

                for (int i = 0; i < count; i++) {
                    final EMConversation value = mOriginalValues.get(i);
                    String username = value.getUserName();

                    EMGroup group = EMGroupManager.getInstance().getGroup(username);
                    if (group != null) {
                        username = group.getGroupName();
                    }

                    // First match against the whole ,non-splitted value
                    if (username.startsWith(prefixString)) {
                        newValues.add(value);
                    } else {
                        final String[] words = username.split(" ");
                        final int wordCount = words.length;

                        // Start at index 0, in case valueText starts with space(s)
                        for (int k = 0; k < wordCount; k++) {
                            if (words[k].startsWith(prefixString)) {
                                newValues.add(value);
                                break;
                            }
                        }
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            conversationList.clear();
            conversationList.addAll((List<EMConversation>) results.values);
            if (results.count > 0) {
                notiyfyByFilter = true;
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }

        }

    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if (!notiyfyByFilter) {
            copyConversationList.clear();
            copyConversationList.addAll(conversationList);
            notiyfyByFilter = false;
        }
    }
}
