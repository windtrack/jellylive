package com.oo58.jelly.manager.face;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;


import com.oo58.jelly.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhongxf
 * @Description 表情的数据管理
 * @Date 2016/6/25.
 */
public class FaceData {

    public static FaceData instance = null;


    private Context mContext;//上下文
    public Pattern mPattern;//正则匹配
    private String[] mResArrayText;//表情文字的数组
    public Map<String, Integer> mResToIcons;//文字和表情图片对应的map
    private List<FaceVo> data = new ArrayList<FaceVo>();//所有表情数据的列表
    private Map<String, Integer> gif_map = new HashMap<String, Integer>();//表情文字和表情图片放置的map

    public FaceData(Context context) {
        mContext = context;
        mResArrayText = context.getResources().getStringArray(Smily.DEFAULT_SMILY_TEXT);//获取表情文字
        mResToIcons = buileResToDrawableMap();//将表情的文字和表情图片防止到一个hashmap中
        mPattern = buildPattern();//建立正则的规则对象
        data = buileMap();//将别情数据初始化到一个List中
    }

    public static FaceData getInstance(Context context){
        if(instance == null){
            instance = new FaceData(context);
        }
        return instance ;
    }


    private HashMap<String, Integer> buileResToDrawableMap() {
        if (mResArrayText.length != Smily.DEFAULT_SMILY_ICONS.length) {
            throw new IllegalStateException("length is Illegal");
        }
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        for (int i = 0; i < mResArrayText.length; i++) {
            map.put(mResArrayText[i], Smily.DEFAULT_SMILY_ICONS[i]);
        }
        return map;
    }


    static class Smily {
        public static final int DEFAULT_SMILY_TEXT = R.array.faces;
        private static final int[] DEFAULT_SMILY_ICONS = {R.mipmap.p0,
                R.mipmap.p1, R.mipmap.p2, R.mipmap.p3, R.mipmap.p4,
                R.mipmap.p5, R.mipmap.p6, R.mipmap.p7, R.mipmap.p8,
                R.mipmap.p9, R.mipmap.p10, R.mipmap.p11, R.mipmap.p12,
                R.mipmap.p13, R.mipmap.p14, R.mipmap.p15, R.mipmap.p16,
                R.mipmap.p17, R.mipmap.p18, R.mipmap.p19, R.mipmap.p20,
                R.mipmap.p21, R.mipmap.p22, R.mipmap.p23, R.mipmap.p24,
                R.mipmap.p25, R.mipmap.p26, R.mipmap.p27, R.mipmap.p28,
                R.mipmap.p29, R.mipmap.p30, R.mipmap.p31, R.mipmap.p32,
                R.mipmap.p33, R.mipmap.p34, R.mipmap.p35, R.mipmap.p36,
                R.mipmap.p37, R.mipmap.p38, R.mipmap.p39, R.mipmap.p40,
                R.mipmap.p41, R.mipmap.p42, R.mipmap.p43, R.mipmap.p44,
                R.mipmap.p45, R.mipmap.p46, R.mipmap.p47, R.mipmap.p48,
                R.mipmap.p49, R.mipmap.p50, R.mipmap.p51, R.mipmap.p52,
                R.mipmap.p53, R.mipmap.p54, R.mipmap.p55, R.mipmap.p56,
                R.mipmap.p57, R.mipmap.p58, R.mipmap.p59, R.mipmap.p60,
                R.mipmap.p61, R.mipmap.p62, R.mipmap.p63, R.mipmap.p64,
                R.mipmap.p65, R.mipmap.p66, R.mipmap.p67, R.mipmap.p68,
                R.mipmap.p69, R.mipmap.p70, R.mipmap.p71, R.mipmap.p72,
                R.mipmap.p73, R.mipmap.p74, R.mipmap.p75, R.mipmap.p76,
                R.mipmap.p77, R.mipmap.p78, R.mipmap.p79, R.mipmap.p80,
                R.mipmap.p81, R.mipmap.p82, R.mipmap.p83, R.mipmap.p84,
                R.mipmap.p85, R.mipmap.p86, R.mipmap.p87, R.mipmap.p88,
                R.mipmap.p89, R.mipmap.p90,

                R.mipmap.d0, R.mipmap.d1, R.mipmap.d2, R.mipmap.d3,
                R.mipmap.d4, R.mipmap.d5, R.mipmap.d6, R.mipmap.d7,
                R.mipmap.d8, R.mipmap.d9, R.mipmap.d10, R.mipmap.d11,
                R.mipmap.d12, R.mipmap.d13, R.mipmap.d14, R.mipmap.d15,
                R.mipmap.d16, R.mipmap.d17, R.mipmap.d18, R.mipmap.d19,
                R.mipmap.d20, R.mipmap.d21, R.mipmap.d22, R.mipmap.d23,
                R.mipmap.d24, R.mipmap.d25, R.mipmap.d26, R.mipmap.d28,
                R.mipmap.d29, R.mipmap.d30, R.mipmap.d31, R.mipmap.d32,
                R.mipmap.d33, R.mipmap.d34, R.mipmap.d35, R.mipmap.d36,
                R.mipmap.d37, R.mipmap.d38, R.mipmap.d39,

                R.mipmap.m0, R.mipmap.m1, R.mipmap.m2, R.mipmap.m3,
                R.mipmap.m4, R.mipmap.m5, R.mipmap.m6, R.mipmap.m7,
                R.mipmap.m8, R.mipmap.m9, R.mipmap.m10, R.mipmap.m11,
                R.mipmap.m12, R.mipmap.m13, R.mipmap.m14, R.mipmap.m15,
                R.mipmap.m16, R.mipmap.m17, R.mipmap.m18, R.mipmap.m19,
                R.mipmap.m20, R.mipmap.m21, R.mipmap.m22, R.mipmap.m23,
                R.mipmap.m24, R.mipmap.m25,

                R.mipmap.t1, R.mipmap.t2, R.mipmap.t3, R.mipmap.t4,
                R.mipmap.t5, R.mipmap.t6, R.mipmap.t7, R.mipmap.t8,
                R.mipmap.t9, R.mipmap.t10, R.mipmap.t11, R.mipmap.t12,
                R.mipmap.t13, R.mipmap.t14, R.mipmap.t15, R.mipmap.t16,
                R.mipmap.t17, R.mipmap.t18, R.mipmap.t19, R.mipmap.t20,
                R.mipmap.t21, R.mipmap.t22, R.mipmap.t23, R.mipmap.t24,
                R.mipmap.t25, R.mipmap.t26, R.mipmap.t27, R.mipmap.t28,
                R.mipmap.t29, R.mipmap.t30, R.mipmap.t31, R.mipmap.t32,
                R.mipmap.t33, R.mipmap.t34, R.mipmap.t35, R.mipmap.t36,
                R.mipmap.t37, R.mipmap.t38,

                R.mipmap.bff, R.mipmap.bf2, R.mipmap.bf3, R.mipmap.bf4,
                R.mipmap.bf5, R.mipmap.bf6, R.mipmap.bf7, R.mipmap.bf8,
                R.mipmap.bf9, R.mipmap.bf10, R.mipmap.bf11,
                R.mipmap.bf12, R.mipmap.bf13, R.mipmap.bf14,
                R.mipmap.bf15, R.mipmap.bf16, R.mipmap.bf17,
                R.mipmap.bf18, R.mipmap.bf19, R.mipmap.bf20,
                R.mipmap.bf21, R.mipmap.bf22, R.mipmap.bf23,
                R.mipmap.bf24, R.mipmap.bf25,

                R.mipmap.gzt1, R.mipmap.gzt2, R.mipmap.gzt3,
                R.mipmap.gzt4, R.mipmap.gzt5, R.mipmap.gzt6,
                R.mipmap.gzt7, R.mipmap.gzt8, R.mipmap.gzt9,
                R.mipmap.gzt10, R.mipmap.gzt11, R.mipmap.gzt12,
                R.mipmap.gzt13, R.mipmap.gzt14, R.mipmap.gzt15,
                R.mipmap.gzt16, R.mipmap.gzt17, R.mipmap.gzt18,
                R.mipmap.gzt19, R.mipmap.gzt20, R.mipmap.gzt21,
                R.mipmap.gzt22, R.mipmap.gzt23, R.mipmap.gzt24,
                R.mipmap.gzt25,

                R.mipmap.meml1, R.mipmap.meml2, R.mipmap.meml3,
                R.mipmap.meml4, R.mipmap.meml5, R.mipmap.meml6,
                R.mipmap.meml7, R.mipmap.meml8, R.mipmap.meml9,
                R.mipmap.meml10, R.mipmap.meml11, R.mipmap.meml12,
                R.mipmap.meml13, R.mipmap.meml14, R.mipmap.meml15,
                R.mipmap.meml16, R.mipmap.meml17, R.mipmap.meml18,
                R.mipmap.meml19, R.mipmap.meml20, R.mipmap.meml21,
                R.mipmap.meml22, R.mipmap.meml23,

                R.mipmap.pdd1, R.mipmap.pdd2, R.mipmap.pdd3,
                R.mipmap.pdd4, R.mipmap.pdd5, R.mipmap.pdd6,
                R.mipmap.pdd7, R.mipmap.pdd8, R.mipmap.pdd9,
                R.mipmap.pdd10, R.mipmap.pdd11, R.mipmap.pdd12,
                R.mipmap.pdd13, R.mipmap.pdd14, R.mipmap.pdd15,
                R.mipmap.pdd16, R.mipmap.pdd17, R.mipmap.pdd18,
                R.mipmap.pdd19, R.mipmap.pdd20, R.mipmap.pdd21,
                R.mipmap.pdd22, R.mipmap.pdd23, R.mipmap.pdd24,
                R.mipmap.pdd25,

                R.mipmap.yct1, R.mipmap.yct2, R.mipmap.yct3,
                R.mipmap.yct4, R.mipmap.yct5, R.mipmap.yct6,
                R.mipmap.yct7, R.mipmap.yct8, R.mipmap.yct9,
                R.mipmap.yct10, R.mipmap.yct11, R.mipmap.yct12,
                R.mipmap.yct13, R.mipmap.yct14, R.mipmap.yct15,
                R.mipmap.yct16, R.mipmap.yct17, R.mipmap.yct18,
                R.mipmap.yct19, R.mipmap.yct20, R.mipmap.yct21,
                R.mipmap.yct22, R.mipmap.yct23, R.mipmap.yct24,
                R.mipmap.yct25,

                R.mipmap.xj1, R.mipmap.xj2, R.mipmap.xj3, R.mipmap.xj4,
                R.mipmap.xj6, R.mipmap.xj7, R.mipmap.xj8, R.mipmap.xj9,
                R.mipmap.xj12, R.mipmap.xj13, R.mipmap.xj14,
                R.mipmap.xj15, R.mipmap.xj17, R.mipmap.xj19,
                R.mipmap.xj21, R.mipmap.xj22, R.mipmap.xj23,
                R.mipmap.xj24, R.mipmap.xj25, R.mipmap.xj26,
                R.mipmap.xj27, R.mipmap.xj28, R.mipmap.xj29,
                R.mipmap.xj30, R.mipmap.xj31, R.mipmap.xj32,
                R.mipmap.xj33, R.mipmap.xj35, R.mipmap.xj36,
                R.mipmap.xj37, R.mipmap.xj38, R.mipmap.xj39,
                R.mipmap.xj41, R.mipmap.xj42, R.mipmap.xj44,
                R.mipmap.xj45, R.mipmap.xj46, R.mipmap.xj47,
                R.mipmap.xj48, R.mipmap.xj49, R.mipmap.xj51,
                R.mipmap.xj52, R.mipmap.xj53, R.mipmap.xj54,
                R.mipmap.xj55, R.mipmap.xj56, R.mipmap.xj57,
                R.mipmap.xj58, R.mipmap.xj59,

                R.mipmap.qbl2, R.mipmap.qbl3, R.mipmap.qbl4,
                R.mipmap.qbl6, R.mipmap.qbl7, R.mipmap.qbl8,
                R.mipmap.qbl9, R.mipmap.qbl10, R.mipmap.qbl11,
                R.mipmap.qbl12, R.mipmap.qbl13, R.mipmap.qbl14,
                R.mipmap.qbl15, R.mipmap.qbl16, R.mipmap.qbl17,
                R.mipmap.qbl19, R.mipmap.qbl20, R.mipmap.qbl21,
                R.mipmap.qbl22, R.mipmap.qbl23, R.mipmap.qbl24,
                R.mipmap.qbl26, R.mipmap.qbl27, R.mipmap.qbl28,
                R.mipmap.qbl32, R.mipmap.qbl33, R.mipmap.qbl34,
                R.mipmap.qbl35, R.mipmap.qbl38, R.mipmap.qbl40,
                R.mipmap.qbl42, R.mipmap.qbl43, R.mipmap.qbl46,
                R.mipmap.qbl47, R.mipmap.qbl48, R.mipmap.qbl51,
                R.mipmap.qbl52, R.mipmap.qbl53, R.mipmap.qbl56,
                R.mipmap.qbl57, R.mipmap.qbl58, R.mipmap.qbl60,
                R.mipmap.qbl61, R.mipmap.qbl63, R.mipmap.qbl66,
                R.mipmap.qbl68, R.mipmap.qbl69, R.mipmap.qbl70,
                R.mipmap.qbl71, R.mipmap.qbl72, R.mipmap.qbl73,
                R.mipmap.qbl74, R.mipmap.qbl76, R.mipmap.qbl77,
                R.mipmap.qbl78, R.mipmap.qbl79, R.mipmap.qbl80,
                R.mipmap.qbl81, R.mipmap.qbl82, R.mipmap.qbl83,

        };
    }

    //将表情数据初始化到一个列表中
    private List<FaceVo> buileMap() {
        List<FaceVo> listMap = new ArrayList<FaceVo>();
        for (int i = 0; i < mResArrayText.length; i++) {
            FaceVo entry = new FaceVo();
            entry.setText(mResArrayText[i]);
            entry.setResId(Smily.DEFAULT_SMILY_ICONS[i]);
            listMap.add(entry);
        }
        return listMap;
    }

    //解析表情的正则
    private Pattern buildPattern() {
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        for (int i = 0; i < mResArrayText.length; i++) {
            sb.append(Pattern.quote(mResArrayText[i]));
            sb.append('|');
        }
        sb.replace(sb.length() - 1, sb.length(), ")");
        return Pattern.compile(sb.toString());
    }

    //获取表情的数据的列表
    public List<FaceVo> getData() {
        return data;
    }

    //解析表情数据
    public CharSequence compileStringToDisply(String text) {
        SpannableStringBuilder sb = new SpannableStringBuilder(text);
        Matcher m = mPattern.matcher(text);
        while (m.find()) {
            int resId = mResToIcons.get(m.group());
            sb.setSpan(new ImageSpan(mContext, resId), m.start(), m.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return sb;
    }
    //获取表情的map
    public Map<String, Integer> getGif_map() {
        return gif_map;
    }
    public void setGif_map(Map<String, Integer> gif_map) {
        this.gif_map = gif_map;
    }
}
