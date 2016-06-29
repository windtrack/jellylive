package com.oo58.jelly.manager;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.oo58.jelly.R;
import com.oo58.jelly.entity.ChatMsgVo;
import com.oo58.jelly.manager.face.FaceData;
import com.oo58.jelly.util.UIUtil;
import com.oo58.jelly.view.CircleImageView;

import org.w3c.dom.Text;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author zhongxf
 * @Description 弹幕的管理类
 * @Date 2016/6/27.
 */
public class DanMuManager {

    //弹幕效果的所有的View
    private int SCREEN_WIDTH;//屏幕的宽度

    private RelativeLayout dmTop;//弹幕top
    private RelativeLayout dmBottom;//弹幕bottom

    private TextView topNick;
    private TextView bottomNick;


    private CircleImageView topFace;
    private CircleImageView bottomFace;

    private TextView topContent;
    private TextView bottomContent;


    private int dmTopFlag = 0;//弹幕可以的标志(0,可以运动  1.不可以运动)
    private int dmBottomFlag = 0;//弹幕可以的标志（0,可以运动  1.不可以运动）
    private TranslateAnimation topAnim;//平移动画
    private TranslateAnimation bottomAnim;//平移动画
    private Context context;//上下文

    private BlockingQueue<ChatMsgVo> queue;//存放消息的队列
    private final static int MAX_QUEUE = 100;//消息队列的最大尺寸

    private Timer timer;//从队列中获取消息的定时器

    private int DIS = 0;//弹幕内容的宽度
    private final static int ANIM_TIME = 4000;//动画运动时间
    private final static int GET_MSG_TIME = 1000;//从队列中获取消息的时间间隔

    private DisplayImageOptions mOptions;
    private final static int GET_MSG = 1001;//处理消息的标志
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_MSG:
                    ChatMsgVo chat = (ChatMsgVo) msg.obj;
                    if (dmTopFlag == 0) {

                        GlobalData.getmImageLoader(context).displayImage(chat.getIcon(), topFace, mOptions);
                        CharSequence tips = FaceData.getInstance(context).compileStringToDisply(chat.getContent());
                        topContent.setText(tips);
                        topNick.setText(chat.getName());
                        dmTop.setVisibility(View.VISIBLE);
                        dmTop.startAnimation(topAnim);
                        break;
                    }
                    if (dmBottomFlag == 0) {
                        GlobalData.getmImageLoader(context).displayImage(chat.getIcon(), bottomFace, mOptions);
                        CharSequence tips = FaceData.getInstance(context).compileStringToDisply(chat.getContent());
                        bottomContent.setText(tips);
                        bottomNick.setText(chat.getName());
                        dmBottom.setVisibility(View.VISIBLE);
                        dmBottom.startAnimation(bottomAnim);
                        break;
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public DanMuManager(Context context) {
        this.context = context;
        DIS = UIUtil.dip2px(context, 200);
        timer = new Timer();
        queue = new ArrayBlockingQueue<ChatMsgVo>(MAX_QUEUE);
        SCREEN_WIDTH = context.getResources().getDisplayMetrics().widthPixels;


        mOptions = new DisplayImageOptions.Builder()
                .showStubImage(R.mipmap.testpic).cacheInMemory()
                .showImageOnFail(R.mipmap.testpic)
                .showImageForEmptyUri(R.mipmap.testpic)
                .showImageOnLoading(R.mipmap.testpic)
                .cacheOnDisc().build();
    }

    public void init(final RelativeLayout dmTop, final RelativeLayout dmBottom, CircleImageView topFace, CircleImageView bottomFace, TextView topContent, TextView bottomContent, TextView topNick, TextView bottomNick) {
        this.dmTop = dmTop;
        this.dmBottom = dmBottom;
        this.topFace = topFace;
        this.bottomFace = bottomFace;
        this.topContent = topContent;
        this.bottomContent = bottomContent;

        this.topNick = topNick;
        this.bottomNick = bottomNick;

        topAnim = new TranslateAnimation(DIS, -SCREEN_WIDTH, 0, 0);
        topAnim.setDuration(ANIM_TIME);
        topAnim.setInterpolator(new LinearInterpolator());
        topAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                dmTopFlag = 1;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                dmTopFlag = 0;
                dmTop.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        bottomAnim = new TranslateAnimation(DIS, -SCREEN_WIDTH, 0, 0);
        bottomAnim.setDuration(ANIM_TIME);
        bottomAnim.setInterpolator(new LinearInterpolator());
        bottomAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                dmBottomFlag = 1;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                dmBottomFlag = 0;
                dmBottom.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    //开始
    public void start() {
        if (timer == null) {
            try {
                throw new DanMuException();
            } catch (DanMuException e) {
                e.printStackTrace();
            }
        } else {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
//                    Log.e("unfind", "定时器执行,消息队列中的消息个数：" + queue.size());
                    if (dmTopFlag == 0 || dmBottomFlag == 0) {//有动画空闲的时候，才从队列获取消息
                        ChatMsgVo chat = queue.poll();
                        if (chat != null) {
                            Message msg = new Message();
                            msg.what = GET_MSG;
                            msg.obj = chat;
                            mHandler.sendMessage(msg);
                        }
                    }

                }
            }, 1000, GET_MSG_TIME);//每秒从队列中获取一条消息
        }
    }

    //销毁
    public void destory() {
        if (timer != null) {
            timer.cancel();
        }
        queue.clear();
    }

    //将弹幕消息放入到队列中
    public void addMsg(ChatMsgVo vo) {
        if (queue != null) {
            if (queue.size() < MAX_QUEUE) {
                queue.add(vo);
            } else {
                try {
                    throw new DanMuException();
                } catch (DanMuException e) {
                    e.printStackTrace();
                    Log.e("JellyLive", "队列已经满了");
                }
            }

        } else {
            try {
                throw new DanMuException();
            } catch (DanMuException e) {
                e.printStackTrace();
                Log.e("JellyLive", "队列没有初始化");
            }
        }
    }


}
