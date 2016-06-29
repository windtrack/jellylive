package com.oo58.jelly.entity.room;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.oo58.jelly.R;
import com.oo58.jelly.manager.GlobalData;
import com.oo58.jelly.manager.LocalImageManager;
import com.oo58.jelly.manager.face.FaceData;
import com.oo58.jelly.util.BaseViewHolder;
import com.oo58.jelly.util.ToastManager;
import com.oo58.jelly.util.Util;

import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;


/**
 * Desc: 聊天消息适配
 * Created by sunjinfang on 2016/5/17 .
 */
public class ChatAdapter extends BaseAdapter {
    private Context context;
    private List<ChatMessage> messages;
    public static DisplayImageOptions mOptions;
    private ImageLoader mImageLoader = null;

    private OnNameClickListener onNameClickListener;

    public ChatAdapter(Context context, List<ChatMessage> messages, OnNameClickListener listener) {
        super();
        this.context = context;
        this.messages = messages;
        this.onNameClickListener = listener;

        mOptions = new DisplayImageOptions.Builder()
                .showStubImage(R.mipmap.icon).cacheInMemory()
                .cacheOnDisc().build();
        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(ImageLoaderConfiguration.createDefault(context));
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return messages.get(position);
    }

    @Override
    public int getViewTypeCount() {
        // TODO Auto-generated method stub
        return 5;
    }

    @Override
    public int getItemViewType(int position) {
        // TODO Auto-generated method stub
        if (messages.get(position).getTid() == 30
                || messages.get(position).getTid() == 29
                || messages.get(position).getTid() == 8
                || messages.get(position).getTid() == 3
                || messages.get(position).getTid() == 230) {
            return 0;
        } else if (messages.get(position).getTid() == 7
                || messages.get(position).getTid() == 42
                || messages.get(position).getTid() == 43) {
            return 2;
        } else if (messages.get(position).getTid() == 33) {
            return 4;
        } else {
            return 1;
        }
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = View.inflate(context, R.layout.chat_message, null);
        }

        //文本框
        TextView textView_name = BaseViewHolder.get(convertView, R.id.message_content);
        // 当前消息
        ChatMessage curMessage = messages.get(position);
        //杂事不考虑表情

        int tid = getItemViewType(position);

        switch (tid) {
            case 0: {
            }
            break;
            case 1: {
                //整体布局    图片等级  名字  内容
                final String uid = curMessage.getS_id();

                int lev = curMessage.getVip_lv();//等级
                final String name = curMessage.getSname() + ":";//名称
                String say = curMessage.getSay();//谁说话
                final String content = curMessage.getContent();//说话内容

                //完整说话内容
                String realSay = lev + name + content;
                SpannableString msp = new SpannableString(realSay);

                //等级所占长度
                int levStart = 0;
                int levEnd = lev > 10 ? 2 : 1;
                if (lev >= 100) {
                    levEnd = 3;
                }
                int nameStart = realSay.indexOf(name);
                int nameEnd = name.length()+nameStart;
                int contendEnd = realSay.length();
                lev = Math.abs(new Random().nextInt() % 30);
                Drawable drawable = LocalImageManager.getWealthLevDrawable(context, lev);
                drawable.setBounds(-2, -2, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                msp.setSpan(new ImageSpan(drawable), levStart, levEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                //名字触摸监听
                msp.setSpan(new ClickableSpan() {
                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(ds.linkColor);
                        ds.setUnderlineText(false);
                    }

                    @Override
                    public void onClick(View widget) {
//                        ToastManager.makeToast(context, name);
                        if (onNameClickListener != null) {
                            onNameClickListener.OnClickName(uid);
                        }
                    }
                }, nameStart, nameEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                //更改名字颜色
                msp.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.loginbtnnormal)), nameStart, nameEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                //其他文本的颜色
                msp.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.loginbg)), nameEnd + 1, contendEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                textView_name.setText(msp);

                //表情解析
//                CharSequence charSequence = sp.compileStringToDisply(content);
//                SpannableStringBuilder sb = new SpannableStringBuilder(text);
                Matcher m = FaceData.getInstance(context).mPattern.matcher(realSay);
                while (m.find()) {
                    int resId = FaceData.getInstance(context).mResToIcons.get(m.group());
                    msp.setSpan(new ImageSpan(context, resId), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                textView_name.setText(msp);
                textView_name.setMovementMethod(LinkMovementMethod.getInstance());
            }
            break;
            case 2://系统消息
            {
                if (curMessage.getTid() == 7) {
                    // 如果是游客
                    if (!Util.isEmpty(curMessage.getUser_type()) && "guest"
                            .equals(curMessage.getUser_type())) {

                    } else {
                        {
                            final String uid = curMessage.getS_id();

                            String say = curMessage.getSay();
                            final String name = curMessage.getSname();
                            SpannableString s3 = new SpannableString(say);
                            String nickname = GlobalData.getUName(context);
                            if (nickname.equals(curMessage.getSname())) {


                                s3.setSpan(new ClickableSpan() {
                                               @Override
                                               public void updateDrawState(TextPaint ds) {
                                                   super.updateDrawState(ds);
                                                   ds.setColor(ds.linkColor);
                                                   ds.setUnderlineText(false);
                                               }

                                               @Override
                                               public void onClick(View arg0) {
                                                   // TODO Auto-generated method stub
                                                   if (onNameClickListener != null) {
                                                       onNameClickListener.OnClickName(uid);
                                                   }
                                               }
                                           }, "欢迎  ".length(),
                                        ("欢迎  ".length() + curMessage.getSname().length()),
                                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


                                s3.setSpan(
                                        new ForegroundColorSpan(context
                                                .getResources().getColor(
                                                        R.color.loginbtnnormal)), "欢迎  "
                                                .length(),
                                        ("欢迎  ".length() + curMessage.getSname().length()),
                                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            } else {
                                s3.setSpan(new ClickableSpan() {
                                               @Override
                                               public void updateDrawState(TextPaint ds) {
                                                   super.updateDrawState(ds);
                                                   ds.setColor(ds.linkColor);
                                                   ds.setUnderlineText(false);
                                               }

                                               @Override
                                               public void onClick(View arg0) {
                                                   // TODO Auto-generated method stub
                                                   if (onNameClickListener != null) {
                                                       onNameClickListener.OnClickName(uid);
                                                   }
                                               }
                                           }, "欢迎  ".length(),
                                        ("欢迎  ".length() + curMessage.getSname().length()),
                                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                s3.setSpan(
                                        new ForegroundColorSpan(context.getResources().getColor(R.color.loginbtnnormal)),
                                        "欢迎  ".length(),
                                        ("欢迎  ".length() + curMessage.getSname().length()),
                                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                            textView_name.setText(s3);
                            textView_name.setMovementMethod(LinkMovementMethod.getInstance());

                        }
                    }

                } else {
                    //整体布局   内容
                    final String content = curMessage.getContent();//说话内容
                    String realSay = content;
                    SpannableString msp = new SpannableString(realSay);
                    int contendEnd = realSay.length();
                    msp.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.red_words)), 0, contendEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    textView_name.setText(msp);
                }


            }
            break;
            case 4: {

            }
            break;

        }


        return convertView;
    }


    public void doAutoDelete() {
        if (getCount() >= 30) {
            messages.remove(0);
        }
    }

    class NoUnderlineSpan extends UnderlineSpan {

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(ds.linkColor);
            ds.setUnderlineText(false);
        }
    }

    public interface OnNameClickListener {
        public void OnClickName(String uid);
    }


}
