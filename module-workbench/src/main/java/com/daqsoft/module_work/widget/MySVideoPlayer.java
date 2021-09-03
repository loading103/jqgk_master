package com.daqsoft.module_work.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.daqsoft.module_work.R;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import moe.codeest.enviews.ENPlayView;

/**
 * @author zp
 * @package name：com.daqsoft.module_work.widget
 * @date 14/7/2021 下午 5:19
 * @describe
 */
public class MySVideoPlayer extends StandardGSYVideoPlayer {

    public MySVideoPlayer(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public MySVideoPlayer(Context context) {
        super(context);
    }

    public MySVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_my_video_player;
    }


    public ImageView startIcon ;

    @Override
    protected void init(Context context) {
        super.init(context);
        startIcon = findViewById(R.id.start_icon);
    }

    @Override
    protected void touchSurfaceMoveFullLogic(float absDeltaX, float absDeltaY) {
        super.touchSurfaceMoveFullLogic(absDeltaX, absDeltaY);
        //不给触摸快进，如果需要，屏蔽下方代码即可
        mChangePosition = false;
    }

    @Override
    protected void updateStartImage() {
        if (mStartButton != null) {
                if (mCurrentState == CURRENT_STATE_PLAYING) {
                    startIcon.setImageResource(R.mipmap.video_icon_pause);
                } else if (mCurrentState == CURRENT_STATE_ERROR) {
                    startIcon.setImageResource(R.mipmap.video_icon_play);
                } else {
                    startIcon.setImageResource(R.mipmap.video_icon_play);
                }
            }
    }
}
