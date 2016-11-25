package com.itheima.googleplay.fragment;

import android.graphics.Color;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.itheima.googleplay.base.BaseFragment;
import com.itheima.googleplay.base.LoadingPager;
import com.itheima.googleplay.utils.UIUtils;

import java.util.Random;

/**
 * Created by user1370 on 2016/11/23.
 */
public class RecommendFragment extends BaseFragment {

    /**
     * 在子线程真正加载数据
     * 在tiggerLoadData()方法调用的时候
     * @return
     */
    @Override
    public LoadingPager.LoadedResult initData() {
        SystemClock.sleep(2000);       //模拟耗时的网络请求

        Random random = new Random();
        int index = random.nextInt(3);

        LoadingPager.LoadedResult[] loadedResults = {LoadingPager.LoadedResult.SUCCESS, LoadingPager.LoadedResult.EMPTY, LoadingPager.LoadedResult.ERROR};
        //随机返回一种情况
        return loadedResults[index];    //数据加载完成后的状态
    }

    /**
     * @return
     * @des 决定成功视图长什么样子(需要定义成功视图)
     * @des 数据和视图的绑定过程
     * @called triggerLoadData()方法被调用, 而且数据加载完成了, 而且数据加载成功
     */
    @Override
    public View initSuccessView() {
        TextView successView = new TextView(UIUtils.getContext());
        successView.setGravity(Gravity.CENTER);
        successView.setTextColor(Color.RED);

        //data
        String data = this.getClass().getSimpleName();
        //data+view
        successView.setText(data);

        return successView;
    }
}
