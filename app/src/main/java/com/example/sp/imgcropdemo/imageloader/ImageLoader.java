package com.example.sp.imgcropdemo.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import com.example.sp.imgcropdemo.R;
import com.example.sp.imgcropdemo.Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by sunpeng on 2016/12/28.
 */

public class ImageLoader {
    private static final String TAG = "ImageLoader";

    public static final int MESSAGE_POST_RESULT = 1;

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    private static final int CORE_POOL_SIZE = CPU_COUNT+1;

    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2+1;
    private static final long KEEP_ALIVE = 10L;

    private static final int TAG_KEY_URI = R.id.imageloader_uri;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r,"ImageLoader#"+mCount.getAndIncrement());
        }
    };

    public static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
            CORE_POOL_SIZE,MAXIMUM_POOL_SIZE,
            KEEP_ALIVE, TimeUnit.SECONDS,
            new LinkedBlockingDeque<Runnable>(),sThreadFactory
    );

    private Handler mMainHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            LoaderResult result = (LoaderResult) msg.obj;
            ImageView imageView = result.imageView;
//            imageView.setImageBitmap(result.bitmap);
            String uri = (String) imageView.getTag(TAG_KEY_URI);
            if(uri.equals(result.uri)){
                imageView.setImageBitmap(result.bitmap);
            }else {
                Log.w(TAG,"set image bitmap, but url has changed, ignored!");
            }
        }
    };

    private Context mContext;
    private ImageResizer imageResizer = new ImageResizer();
    private LruCache<String,Bitmap> mMemoryCache;

    private ImageLoader(Context context){
        mContext = context.getApplicationContext();
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory /8;
        mMemoryCache = new LruCache<String,Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight() / 1024;
            }
        };
    }

    public static ImageLoader build(Context context){
        return new ImageLoader(context);
    }

    private void addBitmapToMemoryCache(String key, Bitmap bitmap){
        if(getBitmapFromMemeCache(key) == null){
            mMemoryCache.put(key,bitmap);
        }
    }

    private Bitmap getBitmapFromMemeCache(String key){
        return mMemoryCache.get(key);
    }

    public void bindBitmap(final String uri,final ImageView imageView){
        bindBitmap(uri,imageView,0,0);
    }

    public void bindBitmap(final String uri,final ImageView imageView,final int reqWidth,final int reqHeight){
        imageView.setTag(TAG_KEY_URI,uri);
        Bitmap bitmap = loadBitmapFromMemCache(uri);
        if(bitmap != null){
            imageView.setImageBitmap(bitmap);
            return;
        }
        Runnable loadBitmapTask = new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap1 = loadBitmap(uri,reqWidth,reqHeight);
                if(bitmap1 != null){
                    String key = hashKeyFormUri(uri);
                    addBitmapToMemoryCache(key,bitmap1);
                    LoaderResult result = new LoaderResult(uri,bitmap1,imageView);
                    mMainHandler.obtainMessage(MESSAGE_POST_RESULT,result).sendToTarget();
                }
            }
        };
        THREAD_POOL_EXECUTOR.execute(loadBitmapTask);
    }

    private Bitmap loadBitmap(String uri, int reqWidth, int reqHeight) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(uri,bmOptions);

        int scaleFactor = Utils.calculateInSampleSize(bmOptions,reqWidth,reqHeight);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(uri,bmOptions);
        return bitmap;
    }

    private Bitmap loadBitmapFromMemCache(String uri){
        final String key = hashKeyFormUri(uri);
        Bitmap bitmap = getBitmapFromMemeCache(key);
        return bitmap;
    }

    private String hashKeyFormUri(String uri){
        String cacheKey;
        try{
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(uri.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            cacheKey = String.valueOf(uri.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes){
        StringBuilder sb = new StringBuilder();
        for(int i =0; i <bytes.length;i++){
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if(hex.length() == 1){
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
