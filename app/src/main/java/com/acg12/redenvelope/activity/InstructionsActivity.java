package com.acg12.redenvelope.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Description
 * Created by chengwanying on 2018/2/6.
 * Company BeiJing kaimijiaoyu
 *
 * @author CU
 */

public class InstructionsActivity extends BaseActivity{

    public static void launch(Context context) {
        Intent in = new Intent(context,InstructionsActivity.class);
        context.startActivity(in);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void initView(Activity activity) {
        super.initView(activity);
    }

    @Override
    public synchronized void initData() {
        super.initData();
    }
}
