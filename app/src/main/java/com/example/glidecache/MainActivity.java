package com.example.glidecache;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.disklrucache.DiskLruCache;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.EmptySignature;

import java.io.File;
import java.io.IOException;

/**
 * 使用Glide时，通过特定的Url获取对应的缓存文件。
 * https://github.com/HuangShengHuan/GlideCache
 */
public class MainActivity extends AppCompatActivity {

//    private String url = "http://p2.so.qhimgs1.com/t012a9cf5811a54acfe.jpg";
    private String url = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1563880008810&di=8e8e19e3ca2b7daef2c6ae84465bb563&imgtype=0&src=http%3A%2F%2Fy3.ifengimg.com%2Fa%2F2016_03%2F6154e935f8a0fc6.jpg";
    private ImageView imgeView;
    private TextView text;
    private ImageView img;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img = (ImageView) this.findViewById(R.id.test);
        imgeView = (ImageView) this.findViewById(R.id.test2);
        text = (TextView) this.findViewById(R.id.cache_path);

        //4.0  -- DiskCacheStrategy.DATA/DiskCacheStrategy.AUTOMATIC/DiskCacheStrategy.ALL
        RequestOptions options = new RequestOptions();
        options.diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(this)
                .asBitmap()
                .load(url)
                .apply(options)
                .into(img);
//       Glide.with(this).load(url).into(img);

        //3.7 只有在DiskCacheStrategy.SOURCE时才能拿到缓存
//        Glide.with(this)
//                .load(url)
//                .asBitmap()
//                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                .into(img);
    }

    // 获取缓存
    public void test(View view) {
        File cacheFile = getCacheFile2(url);
        if (cacheFile != null){
            text.setText(cacheFile.getAbsolutePath());
        }else {
            Toast.makeText(this, "cacheFile = null", Toast.LENGTH_SHORT).show();
        }
        Glide.with(this)
                .load(cacheFile)
                .into(imgeView);
    }

    //4.0
    public File getCacheFile2(String url) {
        DataCacheKey dataCacheKey = new DataCacheKey(new GlideUrl(url), EmptySignature.obtain());
        SafeKeyGenerator safeKeyGenerator = new SafeKeyGenerator();
        String safeKey = safeKeyGenerator.getSafeKey(dataCacheKey);
        try {
            int cacheSize = 100 * 1000 * 1000;
            DiskLruCache diskLruCache = DiskLruCache.open(new File(getCacheDir(), DiskCache.Factory.DEFAULT_DISK_CACHE_DIR), 1, 1, cacheSize);
            DiskLruCache.Value value = diskLruCache.get(safeKey);
            if (value != null) {
                return value.getFile(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    //3.7
   /* public File getCacheFile(String id) {
        OriginalKey originalKey = new OriginalKey(id, EmptySignature.obtain());
        SafeKeyGenerator safeKeyGenerator = new SafeKeyGenerator();
        String safeKey = safeKeyGenerator.getSafeKey(originalKey);
        try {
            DiskLruCache diskLruCache = DiskLruCache.open(new File(getCacheDir(), DiskCache.Factory.DEFAULT_DISK_CACHE_DIR), 1, 1, DiskCache.Factory.DEFAULT_DISK_CACHE_SIZE);
            DiskLruCache.Value value = diskLruCache.get(safeKey);
            if (value != null) {
                return value.getFile(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }*/

}
