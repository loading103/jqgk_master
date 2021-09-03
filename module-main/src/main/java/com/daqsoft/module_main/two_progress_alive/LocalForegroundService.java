package com.daqsoft.module_main.two_progress_alive;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.StringUtils;
import com.daqsoft.library_base.global.MMKVGlobal;
import com.daqsoft.library_base.net.AppDisposableObserver;
import com.daqsoft.library_base.net.AppResponse;
import com.daqsoft.library_base.net.RetrofitClient;
import com.daqsoft.library_base.pojo.LoginInfo;
import com.daqsoft.library_base.utils.MMKVUtils;
import com.daqsoft.module_main.R;
import com.daqsoft.module_main.repository.MainApiService;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

/**
 * 前台服务提权
 */
public class LocalForegroundService extends Service {

    public AMapLocationClient locationClient;
    public AMapLocationClientOption locationOption;
    private PowerManager.WakeLock wakeLock = null;
    private MainApiService mainApiService;
    private Disposable disposable;


    /**
     * 远程调用 Binder 对象
     */
    private MyBinder myBinder;

    /**
     * 连接对象
     */
    private Connection connection;

    /**
     * AIDL 远程调用接口
     * 其它进程调与该 RemoteForegroundService 服务进程通信时 , 可以通过 onBind 方法获取该 myBinder 成员
     * 通过调用该成员的 basicTypes 方法 , 可以与该进程进行数据传递
     */
    class MyBinder extends IMyAidlInterface.Stub {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {
            // 通信内容
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 创建 Binder 对象
        myBinder = new MyBinder();
        // 启动前台进程
//        startService();
        Timber.e("LocalForegroundService onCreate");
        initLocation();
        mainApiService = new RetrofitClient.Builder().build().create(MainApiService.class);
    }

    private void startService(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            // 开启前台进程 , API 26 以上无法关闭通知栏
            startForeground(2021, buildNotification());

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
            startForeground(2021, buildNotification());
            // API 18 ~ 25 以上的设备 , 启动相同 id 的前台服务 , 并关闭 , 可以关闭通知
            startService(new Intent(this, CancelNotificationService.class));

        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2){
            // 将该服务转为前台服务
            // 需要设置 ID 和 通知
            // 设置 ID 为 0 , 就不显示已通知了 , 但是 oom_adj 值会变成后台进程 11
            // 设置 ID 为 1 , 会在通知栏显示该前台服务
            // 8.0 以上该用法报错
            startForeground(2021, buildNotification());
        }
    }

    /**
     * 绑定 另外一个 服务
     * LocalForegroundService 与 RemoteForegroundService 两个服务互相绑定
     */
    private void bindService(){
        // 绑定 另外一个 服务
        // LocalForegroundService 与 RemoteForegroundService 两个服务互相绑定

        // 创建连接对象
        connection = new Connection();

        // 创建本地前台进程组件意图
        Intent bindIntent = new Intent(this, RemoteForegroundService.class);
        // 绑定进程操作
        bindService(bindIntent, connection, BIND_AUTO_CREATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 绑定另外一个服务
        bindService();
        Timber.e("LocalForegroundService onStartCommand");
        startLocation();
        return super.onStartCommand(intent, flags, startId);
    }

    class Connection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // 服务绑定成功时回调
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // 再次启动前台进程
            startService();
            // 绑定另外一个远程进程
            bindService();
        }
    }

    /**
     * API 18 ~ 25 以上的设备, 关闭通知到专用服务
     */
    public static class CancelNotificationService extends Service {
        public CancelNotificationService() {
        }

        @Override
        public void onCreate() {
            super.onCreate();
            startForeground(2021, new Notification());
            stopSelf();
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

    }

    @Override
    public void onDestroy() {
        Timber.e("LocalForegroundService onStartCommand");
        super.onDestroy();
        destroyLocation();
        releaseWakeLock();
        mainApiService = null;
        if(disposable != null && !disposable.isDisposed()){
            disposable.dispose();
            disposable = null;
        }
    }

    private void destroyLocation() {
        if (null != locationClient) {
            locationClient.disableBackgroundLocation(true);
            locationClient.stopLocation();
            locationClient.unRegisterLocationListener(locationListener);
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }

    private static final String NOTIFICATION_CHANNEL_NAME = "Location";
    private NotificationManager notificationManager;
    boolean isCreateChannel = false;
    private Notification buildNotification() {
        Notification.Builder builder = null;
        Notification notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (null == notificationManager) {
                notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            }
            String channelId = getPackageName();
            if (!isCreateChannel) {
                NotificationChannel notificationChannel = new NotificationChannel(channelId, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.enableLights(false);
                notificationChannel.setLightColor(Color.BLUE);
                notificationChannel.setShowBadge(true);
                notificationManager.createNotificationChannel(notificationChannel);
                isCreateChannel = true;
            }
            builder = new Notification.Builder(getApplicationContext(), channelId);
        } else {
            builder = new Notification.Builder(getApplicationContext());
        }
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(AppUtils.getAppName() + "后台定位服务")
                .setContentText("")
                .setWhen(System.currentTimeMillis());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notification = builder.build();
        } else {
            return builder.getNotification();
        }
        return notification;
    }


    private void initLocation() {
        int interval = 15;
        String loginInfoJson = MMKVUtils.INSTANCE.decodeString(MMKVGlobal.LOGIN_INFO);
        if (!StringUtils.isEmpty(loginInfoJson) ){
            LoginInfo loginInfo = GsonUtils.fromJson(loginInfoJson,LoginInfo.class);
            if (loginInfo != null){
                interval = loginInfo.getGpsTimeCycle();
            }
        }

        if (interval <= 0){
            interval = 5 ;
        }

        locationOption = new AMapLocationClientOption();
        //可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        locationOption.setGpsFirst(true);
        //可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        locationOption.setHttpTimeOut(30000);
        //可选，设置定位间隔。默认为2秒
        locationOption.setInterval(interval * 1000);
        //可选，设置是否返回逆地理地址信息。默认是true
        locationOption.setNeedAddress(true);
        //可选，设置是否单次定位。默认是false
        locationOption.setOnceLocation(false);
        //可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        locationOption.setOnceLocationLatest(false);
        //可选，设置是否使用传感器。默认是false
        locationOption.setSensorEnable(false);
        //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        locationOption.setWifiScan(true);
        //可选，设置是否使用缓存定位，默认为true
        locationOption.setLocationCacheEnable(true);
        //可选，设置逆地理信息的语言，默认值为默认语言（根据所在地区选择语言）
        locationOption.setGeoLanguage(AMapLocationClientOption.GeoLanguage.DEFAULT);
        locationClient = new AMapLocationClient(this.getApplicationContext());
        locationClient.setLocationOption(locationOption);
        locationClient.setLocationListener(locationListener);
        locationClient.enableBackgroundLocation(2021,buildNotification());

    }


    private void startLocation() {
        startAlarm();
        acquireWakeLock();
        assert locationClient != null;
        if (!locationClient.isStarted()) {
            locationClient.startLocation();
        }
    }


    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (null != location) {
                // errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if (location.getErrorCode() == 0) {
                    String loginInfoJson = MMKVUtils.INSTANCE.decodeString(MMKVGlobal.LOGIN_INFO);
                    if (StringUtils.isTrimEmpty(loginInfoJson)){
                        return;
                    }
                    LoginInfo loginInfo = GsonUtils.fromJson(loginInfoJson,LoginInfo.class);
                    if (loginInfo == null){
                        return;
                    }
                    Timber.e("location" + location.toString());
                    uploadLocation(location);
                }else {

                }
            }
        }
    };



    public void startAlarm() {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intentAlarm = new Intent(this, LocalForegroundService.class);
        PendingIntent pendSender = PendingIntent.getService(this, 0, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
        // AlarmManager.RTC_WAKEUP ;这个参数表示系统会唤醒进程；设置的间隔时间是 20 秒
        long triggerAtTime = System.currentTimeMillis() + 20 * 1000;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtTime, pendSender);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            am.setExact(AlarmManager.RTC_WAKEUP, triggerAtTime, pendSender);
//            am.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtTime,  1000, pendSender);
        } else {
            am.set(AlarmManager.RTC_WAKEUP, triggerAtTime, pendSender);
        }
    }

    /**
     * PARTIAL_WAKE_LOCK:保持CPU 运转，屏幕和键盘灯有可能是关闭的。
     * SCREEN_DIM_WAKE_LOCK：保持CPU 运转，允许保持屏幕显示但有可能是灰的，允许关闭键盘灯
     * SCREEN_BRIGHT_WAKE_LOCK：保持CPU 运转，允许保持屏幕高亮显示，允许关闭键盘灯
     * FULL_WAKE_LOCK：保持CPU 运转，保持屏幕高亮显示，键盘灯也保持亮度
     * ACQUIRE_CAUSES_WAKEUP：强制使屏幕亮起，这种锁主要针对一些必须通知用户的操作.
     * ON_AFTER_RELEASE：当锁被释放时，保持屏幕亮起一段时间
     */
    private void acquireWakeLock() {
        if (null == wakeLock) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, getClass().getCanonicalName());
            if (null != wakeLock) {
                wakeLock.acquire();
            }
        }
    }

    private void releaseWakeLock() {
        if (null != wakeLock && wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock = null;
        }
    }


    private void uploadLocation(AMapLocation location){
        HashMap<String,String> body = new HashMap<>();
        body.put("address",location.getAddress());
        body.put("lat",String.valueOf(location.getLatitude()));
        body.put("lng",String.valueOf(location.getLongitude()));
        mainApiService
                .uploadLocation(body)
                .subscribeOn(Schedulers.io())
                .subscribeWith(new AppDisposableObserver<AppResponse<Object>>() {
                    @Override
                    public void onSuccess(AppResponse<Object> ObjectAppResponse) {
                        Timber.e("uploadLocation  onSuccess");
                    }

                    @Override
                    public void onFail(@NotNull Throwable e) {
                        super.onFail(e);
                        Timber.e("uploadLocation  onFail");
                    }
                });
    }

}