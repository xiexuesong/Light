package com.light.example;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.light.body.Light;
import com.light.body.LightConfig;
import com.light.core.Utils.MemoryComputeUtil;
import com.light.core.Utils.UriParser;
import com.light.core.Utils.http.HttpDownLoader;
import com.light.core.listener.OnCompressFinishListener;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
	ImageView ivCompress;
	TextView tvInfo;
	Uri imageUri;
	String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/pic22.jpg";
	final static String info = "原图片:\n高度：%d，宽度：%d，占用内存：%dKB\n显示的图片(压缩后)：\n高度：%d, 宽度：%d，占用内存：%dKB";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ivCompress = findViewById(R.id.image_compress);
		tvInfo = findViewById(R.id.tv_info);
		LightConfig config = new LightConfig();
		config.setNeedIgnoreSize(true);
		Light.getInstance().setConfig(config);

		new Thread(new Runnable() {
			@Override
			public void run() {
				Uri uri = Uri.parse("http://pic4.nipic.com/20091217/3885730_124701000519_2.jpg");
				Light.getInstance().compress(uri, path);
//				HttpDownLoader.downloadImage(uri, new OnCompressFinishListener() {
//
//					@Override
//					public void onFinish(final byte[] bytes) {
//						runOnUiThread(new Runnable() {
//							@Override
//							public void run() {
//								Bitmap compressBitmap = Light.getInstance().compress(bytes);
//								ivCompress.setImageBitmap(compressBitmap);
//							}
//						});
//					}
//				});
			}
		}).start();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 1 && imageUri != null) {
			//效果同下
//			Light.setImage(ivCompress, imageUri);
			Bitmap compressBitmap = Light.getInstance().compress(imageUri);
			ivCompress.setImageBitmap(compressBitmap);

			//系统获取图片的方法
			String path = UriParser.getPathFromContentUri(imageUri);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, options);
			Bitmap bitmap2 = BitmapFactory.decodeFile(path);
			tvInfo.setText(String.format(Locale.CHINA, info, options.outHeight, options.outWidth,
					MemoryComputeUtil.getMemorySize(bitmap2), compressBitmap.getHeight(),
					compressBitmap.getWidth(), MemoryComputeUtil.getMemorySize(compressBitmap)));
			bitmap2.recycle();
		}else if(requestCode == 2 && data != null){
			Uri imageUri = data.getData();
			String path = UriParser.getPathFromContentUri(imageUri);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, options);
			Bitmap compressBitmap = Light.getInstance().compress(imageUri);
			ivCompress.setImageBitmap(compressBitmap);
			Bitmap bitmap2 = BitmapFactory.decodeFile(path);
			tvInfo.setText(String.format(Locale.CHINA, info, options.outHeight, options.outWidth,
					MemoryComputeUtil.getMemorySize(bitmap2), compressBitmap.getHeight(),
					compressBitmap.getWidth(), MemoryComputeUtil.getMemorySize(compressBitmap)));
			bitmap2.recycle();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("相册");
		menu.add("拍照");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if("相册".equals(item.getTitle())){
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_PICK);
			intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intent, 2);
		}else if("拍照".equals(item.getTitle())){
			imageUri = getContentResolver().insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					new ContentValues());
			Intent takePhotoIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			takePhotoIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri);
			startActivityForResult(takePhotoIntent, 1);
		}
		return true;
	}
}
