package com.mi.unitydevlib;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.unity3d.player.UnityPlayerActivity;
import com.xiaomi.gamecenter.sdk.MiCommplatform;
import com.xiaomi.gamecenter.sdk.MiErrorCode;
import com.xiaomi.gamecenter.sdk.OnExitListner;
import com.xiaomi.gamecenter.sdk.OnLoginProcessListener;
import com.xiaomi.gamecenter.sdk.OnPayProcessListener;
import com.xiaomi.gamecenter.sdk.OnRealNameVerifyProcessListener;
import com.xiaomi.gamecenter.sdk.RoleData;
import com.xiaomi.gamecenter.sdk.entry.MiAccountInfo;
import com.xiaomi.gamecenter.sdk.entry.MiBuyInfo;

import java.util.UUID;

public class MainActivity extends UnityPlayerActivity {

    public static MainActivity instance;

    private static MiAccountInfo accountInfo;

    private String session;

    private LoginProgressListener loginProgressListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
    }

    @Override
    public void onBackPressed() {
        if (!TextUtils.isEmpty(session)) {
            MiCommplatform.getInstance().miAppExit(MainActivity.this, new OnExitListner() {
                @Override
                public void onExit(int code) {
                    if (code == MiErrorCode.MI_XIAOMI_EXIT) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                }
            });
        }
        super.onBackPressed();
    }

    /**************************** Unity 方法 start ****************************/

    /**
     * 调用登陆
     *
     * @param progressListener 登录回调
     */
    public void startLogin(LoginProgressListener progressListener) {
        this.loginProgressListener = progressListener;
        MiCommplatform.getInstance().miLogin(this, new OnLoginProcessListener() {
            @Override
            public void finishLoginProcess(int i, MiAccountInfo miAccountInfo) {
                if (MiErrorCode.MI_XIAOMI_PAYMENT_SUCCESS == i) {
                    accountInfo = miAccountInfo;
                    session = accountInfo.getSessionId();
                } else if (MiErrorCode.MI_XIAOMI_PAYMENT_ERROR_ACTION_EXECUTED == i) {
                    //重复调用登录
                } else {
                    //登录失败
                }
                loginProgressListener.loginFinished(i);
            }
        });
    }

    /**
     * 按计费码生成订单对象
     *
     * @param productCode        计费点
     * @param count              数量
     * @param cpOrderId          cp订单号
     * @param cpUserInfo         透传信息
     * @param payProcessListener 支付回调
     */
    public void productCodePay(String productCode, int count, String cpOrderId, String cpUserInfo, OnPayProcessListener payProcessListener) {
        MiCommplatform.getInstance().miUniPay(this, createMiBuyInfo(productCode, count, cpOrderId, cpUserInfo), payProcessListener);
    }

    /**
     * 按金额生成订单对象
     *
     * @param amount             计费金额，单位为分
     * @param cpOrderId          cp订单号
     * @param cpUserInfo         透传信息
     * @param payProcessListener 支付回调
     */
    public void amountPay(int amount, String cpOrderId, String cpUserInfo, OnPayProcessListener payProcessListener) {
        MiCommplatform.getInstance().miUniPay(this, createMiBuyInfo(amount, cpOrderId, cpUserInfo), payProcessListener);
    }

    /**
     * 调用实名制页面
     *
     * @param realNameVerifyProcessListener 实名制回调
     */
    public void showVerify(OnRealNameVerifyProcessListener realNameVerifyProcessListener) {
        if (TextUtils.isEmpty(session)) {
            Toast.makeText(this, "未登录，请先登录", Toast.LENGTH_SHORT).show();
            return;
        }
        MiCommplatform.getInstance().realNameVerify(MainActivity.this, realNameVerifyProcessListener);
    }

    /**
     * 上报角色信息
     *
     * @param level      等级
     * @param roleId     角色id
     * @param roleName   角色名称
     * @param serverId   服务器id
     * @param serverName 服务器名字
     * @param zoneId     区id
     * @param zoneName   区名字
     */
    public void submitRoleData(String level, String roleId, String roleName, String serverId, String serverName, String zoneId, String zoneName) {
        RoleData data = new RoleData();

        data.setLevel(level);
        data.setRoleId(roleId);
        data.setRoleName(roleName);
        data.setServerId(serverId);
        data.setServerName(serverName);
        data.setZoneId(zoneId);
        data.setZoneName(zoneName);
        MiCommplatform.getInstance().submitRoleData(MainActivity.this, data);
    }

    /**************************** Unity 方法 end ****************************/

    /**
     * 按计费码生成订单对象
     *
     * @param productCode 计费点
     * @param count       数量
     * @param cpOrderId   cp订单号
     * @param cpUserInfo  透传信息
     * @return 订单对象
     */
    private MiBuyInfo createMiBuyInfo(String productCode, int count, String cpOrderId, String cpUserInfo) {
        MiBuyInfo miBuyInfo = new MiBuyInfo();
        miBuyInfo.setProductCode(productCode);
        miBuyInfo.setCount(count);
        miBuyInfo.setCpOrderId(cpOrderId);
        miBuyInfo.setCpUserInfo(cpUserInfo);

        return miBuyInfo;
    }

    /**
     * 按金额生成订单对象
     *
     * @param amount     计费金额，单位为分
     * @param cpOrderId  cp订单号
     * @param cpUserInfo 透传信息
     * @return 订单对象
     */
    private MiBuyInfo createMiBuyInfo(int amount, String cpOrderId, String cpUserInfo) {
        MiBuyInfo miBuyInfo = new MiBuyInfo();
        miBuyInfo.setCpOrderId(UUID.randomUUID().toString());
        miBuyInfo.setCpUserInfo(cpUserInfo);
        miBuyInfo.setAmount(amount);

        return miBuyInfo;
    }
}
