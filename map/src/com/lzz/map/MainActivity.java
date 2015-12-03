package com.lzz.map;

import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.lzz.map.MyOrientationListener.OnOrientationListener;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements InfoWindow.OnInfoWindowClickListener{

	protected static final String TAG = "lzz";
	// 定位控件
	private LocationClient mLocationClient;
	private MyLocationListener myLocationListener;
	private boolean isFirst = true;
	private double latitude;
	private double longitude;
	private Context context;
	
	private BitmapDescriptor mMaker;
	private RelativeLayout mMarklayout;


	//
	private BitmapDescriptor mBitmapDescriptor;
	private LocationMode mLocationMode;
	private MyOrientationListener myOrientationListener;
	private float currentX;
	InfoWindow infowindow;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 在使用SDK各组件之前初始化context信息，传入ApplicationContext
		// 注意该方法要再setContentView方法之前实现
		SDKInitializer.initialize(getApplicationContext());
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		initMap();
		initLocation();
		this.context = this;
		initMake();
		
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			
			@Override
			public boolean onMarkerClick(Marker mark) {
				Bundle mBundle = mark.getExtraInfo();
				Info info = (Info) mBundle.getSerializable("info");
				TextView tv_name = (TextView) mMarklayout.findViewById(R.id.id_name_z);
				TextView tv_distance = (TextView) mMarklayout.findViewById(R.id.id_distance);
				TextView tv_zan = (TextView) mMarklayout.findViewById(R.id.id_zan_count);
				ImageView img_zan = (ImageView) mMarklayout.findViewById(R.id.id_img);
				
				tv_name.setText(info.getName());
				tv_distance.setText(info.getDistance());
				tv_zan.setText(info.getZan()+"");
				img_zan.setImageResource(info.getmImgId());
				
				TextView tv = new TextView(context);
				tv.setBackgroundResource(R.drawable.location_tips);
				tv.setPadding(30, 20, 30, 50);
				tv.setText(info.getName());
				tv.setTextColor(Color.parseColor("#ffffff"));
				
				LatLng mLatLng = mark.getPosition();
				/*Point point = mBaiduMap.getProjection().toScreenLocation(mLatLng);
				point.y-=47;
				LatLng ll = mBaiduMap.getProjection().fromScreenLocation(point);*/
				infowindow = new InfoWindow(tv, mLatLng, -47);
				mBaiduMap.showInfoWindow(infowindow);
				mMarklayout.setVisibility(View.VISIBLE);
				return true;
			}
		});
		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {
			
			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void onMapClick(LatLng arg0) {
				mMarklayout.setVisibility(View.GONE);
				mBaiduMap.hideInfoWindow();
			}
		});
		
	}
	@Override
	public void onInfoWindowClick() {
		
	}

	private void initMake() {
		mMaker = BitmapDescriptorFactory.fromResource(R.drawable.maker);
		mMarklayout = (RelativeLayout) findViewById(R.id.id_markly);
	}

	private void initLocation() {
		mLocationMode = LocationMode.NORMAL;
		mLocationClient = new LocationClient(this);
		myLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(myLocationListener);
		LocationClientOption option = new LocationClientOption();
		option.setCoorType("bd09ll");// 设置坐标类型
		option.setIsNeedAddress(true);
		option.setOpenGps(true);
		option.setScanSpan(1000);// 设置定位的时间间隔
		mLocationClient.setLocOption(option);

		mBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked);
		myOrientationListener = new MyOrientationListener(this);
		myOrientationListener.setOrientationListener(new OnOrientationListener() {
			
			@Override
			public void onOrientationChanged(float x) {
				// TODO Auto-generated method stub
				currentX = x;
				Log.d(TAG,"当前的X是："+currentX);
			}
		});
	}
	
	

	private MapView mMapView;
	BaiduMap mBaiduMap;

	private void initMap() {
		// TODO Auto-generated method stub
		mMapView = (MapView) findViewById(R.id.id_bmapView);
		mBaiduMap = mMapView.getMap();
	}

	@Override
	protected void onStart() {
		mBaiduMap.setMyLocationEnabled(true);
		if (!mLocationClient.isStarted()) {
			mLocationClient.start();
		}
		myOrientationListener.start();
		super.onStart();
	}

	@Override
	protected void onStop() {
		mBaiduMap.setMyLocationEnabled(false);
		mLocationClient.stop();
		myOrientationListener.stop();
		super.onStop();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mMapView.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mMapView.onDestroy();
	}

	class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			MyLocationData data = new MyLocationData.Builder()//
					.direction(currentX)//
					.accuracy(location.getRadius())//
					.latitude(location.getLatitude())//
					.longitude(location.getLongitude())//
					.build();
			mBaiduMap.setMyLocationData(data);
			MyLocationConfiguration config = new MyLocationConfiguration(mLocationMode,true,mBitmapDescriptor);
			mBaiduMap.setMyLocationConfigeration(config);
			latitude = location.getLatitude();
			longitude = location.getLongitude();
			if (isFirst) {
				LatLng mLatLng = new LatLng(location.getLatitude(), location.getLongitude());
				MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(mLatLng);
				mBaiduMap.animateMapStatus(update);
				isFirst = false;
				Toast.makeText(context, location.getAddrStr(), Toast.LENGTH_SHORT).show();
			}

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		case R.id.id_weixing:
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
			break;
		case R.id.id_luxian:
			if (mBaiduMap.isTrafficEnabled()) {
				mBaiduMap.setTrafficEnabled(false);
				item.setTitle("实时交通（off）");
			} else {
				mBaiduMap.setTrafficEnabled(true);
				item.setTitle("实时交通（on）");
			}
			break;
		case R.id.id_nomal:
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
			break;
		case R.id.id_position:
			LatLng position = new LatLng(latitude, longitude);
			MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(position);
			mBaiduMap.animateMapStatus(update);
			break;
		case R.id.id_normal:
			mLocationMode = LocationMode.NORMAL;
			break;
		case R.id.id_compass:
			mLocationMode = LocationMode.COMPASS;
			break;
		case R.id.id_following:
			mLocationMode = LocationMode.FOLLOWING;
			break;
		case R.id.id_cover:
			addOverlay(Info.infos);
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void addOverlay(List<Info> infos) {
		mBaiduMap.clear();
		LatLng mLatLng = null;
		Marker marker = null;
		OverlayOptions options;
		for(Info info:infos){
			mLatLng = new LatLng(info.getLatitude(), info.getLongitude());
			options = new MarkerOptions().position(mLatLng).icon(mMaker).zIndex(5);
			marker = (Marker) mBaiduMap.addOverlay(options);
			Bundle mBundle = new Bundle();
			mBundle.putSerializable("info", info);
			marker.setExtraInfo(mBundle);
		}
		MapStatusUpdate pos = MapStatusUpdateFactory.newLatLng(mLatLng);
		mBaiduMap.animateMapStatus(pos);
		
	}

}
