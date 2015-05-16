package cn.zhanglubo.imagePicker;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {
	private Button mCamera, mAlbum;
	private ImageView mImageView;
	private TextView mInfo;
	static final int REQUEST_IMAGE_CAMERA = 1;
	static final int REQUEST_IMAGE_ALBUM = 2;
	private Uri uri;  //图片保存uri
	private File scaledFile;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
	}

	/**
	 * 初始化视图
	 */
	private void initView() {
		mCamera = (Button) findViewById(R.id.btn_camera);
		mAlbum = (Button) findViewById(R.id.btn_album);
		mImageView = (ImageView) findViewById(R.id.image);
		mInfo = (TextView) findViewById(R.id.image_info);
		mCamera.setOnClickListener(this);
		mAlbum.setOnClickListener(this);
	}

	/**
	 * 打开相机
	 */
	private void openCamera(){
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		uri = PhotoUtil.createImageFile();
	    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {//相机被卸载时不会崩溃
	            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
	                    uri);
	            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAMERA);
	    }
	}

	/**
	 * 打开相册
	 */
	private void openAlbum() {
		Intent takePictureIntent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {// 相机被卸载时不会崩溃
			startActivityForResult(takePictureIntent, REQUEST_IMAGE_ALBUM);
		}
	}
	/**
	 * 压缩拍照得到的图片
	 */
	private void dealTakePhoto() {
		scaledFile = ImageCompress.scal(uri);
	    Bitmap bitmap = BitmapFactory.decodeFile(scaledFile.getAbsolutePath());
	    mImageView.setImageBitmap(bitmap);
		float oldSize = (float)new File(uri.getPath()).length()/1024/1024;   //以文件的形式
		float newSize = (float)scaledFile.length()/1024;
		String mCurrentPhotoPath = uri.getPath();
	    mInfo.setText("图片路径:"+"\n"+mCurrentPhotoPath+"\n"+"文件大小:"+oldSize+"M\n"+"压缩后的大小:"+newSize+"KB"+"宽度："+bitmap.getWidth()+"高度："+bitmap.getHeight());
	}
	/**
	 * 压缩拍照得到的图片
	 */
	private void dealAlbum(Intent data) {
		uri = data.getData();
		String[] proj = { MediaStore.Images.Media.DATA };
    	Cursor cursor = getContentResolver().query(uri, proj, null, null,null);
    	int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        System.out.println("path:"+path);
        File imageFile = new File(path);
        uri = Uri.fromFile(imageFile);
		scaledFile = ImageCompress.scal(uri);
	    Bitmap bitmap = BitmapFactory.decodeFile(scaledFile.getAbsolutePath());
	    mImageView.setImageBitmap(bitmap);
		float oldSize = (float)new File(uri.getPath()).length()/1024/1024;   //以文件的形式
		float newSize = (float)scaledFile.length()/1024;
		String mCurrentPhotoPath = uri.getPath();
	    mInfo.setText("图片路径:"+"\n"+mCurrentPhotoPath+"\n"+"文件大小:"+oldSize+"M\n"+"压缩后的大小:"+newSize+"KB"+"宽度："+bitmap.getWidth()+"高度："+bitmap.getHeight());
	}
	
	/**
	 * 回调函数
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_IMAGE_CAMERA && resultCode == RESULT_OK) {
			dealTakePhoto();
		} else if (requestCode == REQUEST_IMAGE_ALBUM && resultCode == RESULT_OK) {
			uri = data.getData();
			if(uri!=null){
				System.out.println(uri.getPath());
				dealAlbum(data);
			}else{
				System.out.println("uri为空");
			}
		} else {
			
		}
	}
	

	/**
	 * 响应点击事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_camera:
			openCamera();
			break;
		case R.id.btn_album:
			openAlbum();
			break;
		default:
			break;
		}
	}
}
