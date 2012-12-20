package jp.rt_net.android.RTCAMHEAD02;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class RTCAMHEAD02Activity extends Activity {
	
	//RT-ADKminiとのデータのやり取りで使うコマンド
	private static final int USBAccessoryWhat           = 0;
	public static final int PUSHBUTTON_STATUS_CHANGE	= 2;
   	public static final int SERVO_01 					= 7;
	public static final int SERVO_02					= 8;
	
    //ボタンの押下情報取得用	
	public static final int BUTTON_1_PRESSED			= 0x01;
	public static final int BUTTON_2_PRESSED			= 0x02;
	public static final int BUTTON_3_PRESSED			= 0x04;
	public static final int BUTTON_4_PRESSED			= 0x08;
	
	//サーボモータの角度情報	
	private static int servo01_pos = 0;
	private static int servo02_pos = 0;

	private USBAccessoryManager accessoryManager; 
	
	private Handler uiHandler;
	private SurfaceView mSurfaceView;    
	private CameraView mCameraView;
		
	//UI
	private SeekBar seekBar1, seekBar2; 
	private TextView value_of_seekbar1, value_of_seekbar2; 
	private Button button1, button2;
			
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main);
               	
        accessoryManager = new USBAccessoryManager(handler, USBAccessoryWhat);
        
       	// 作成したCameraViewクラスをインスタンス化
       	mSurfaceView = (SurfaceView)findViewById(R.id.surface_view);                  
	    mCameraView = new CameraView(this, mSurfaceView);
        
        /******* SeekBar1の定義 *******/
        seekBar1 = (SeekBar) findViewById(R.id.seekBar1);  
        value_of_seekbar1 = (TextView)findViewById(R.id.valueOfseekBar1);
        // シークバーの初期値をTextViewに表示
        value_of_seekbar1.setText("Current Value:"+seekBar1.getProgress());     
        //シークバーの現在値,初期値,セカンダリ値をセット  
        seekBar1.setMax(255);  
        seekBar1.setProgress(0);  
        seekBar1.setSecondaryProgress(0);  
        // SeekBar の値が変更されたときに呼び出されるコールバックを登録  
        seekBar1.setOnSeekBarChangeListener(  
        	new OnSeekBarChangeListener() {  
            
        	// SeekBar1 の値が変わったときの動作   
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) { 
        			servo01_pos	= progress;                                  //servo01の角度はseekBarのprogressで決まる
            		value_of_seekbar1.setText("Current Value:"+progress);    //textにseekbar1の値を表示
            		uiHandler = handler;
            		
            		Message servo01Update = Message.obtain(uiHandler, SERVO_01);
            		if(uiHandler != null) {
            			uiHandler.sendMessage(servo01Update);
            		}	
            }     
            // SeekBar のタッチの開始時の動作  
            public void onStartTrackingTouch(SeekBar seekBar) {  
            }        
            // SeekBar のタッチの終了時の動作  
            public void onStopTrackingTouch(SeekBar seekBar) {  
            }  
          }  
        );  
        
        /******* SeekBar2定義 *******/
        seekBar2 = (SeekBar)findViewById(R.id.seekBar2);  
        value_of_seekbar2 = (TextView)findViewById(R.id.valueOfseekBar2);
        // シークバーの初期値をTextViewに表示
        value_of_seekbar2.setText("Current Value:"+seekBar2.getProgress());     
        //シークバーの現在値,初期値,セカンダリ値をセット  
        seekBar2.setMax(255);  
        seekBar2.setProgress(0);  
        seekBar2.setSecondaryProgress(0);  
        // SeekBar の値が変更されたときに呼び出されるコールバックを登録  
        seekBar2.setOnSeekBarChangeListener(  
        	new OnSeekBarChangeListener() {  
            // SeekBar2 の値が変わったときの動作   
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) { 
        			servo02_pos	= progress;
            		value_of_seekbar2.setText("Current Value:"+progress);
            		uiHandler = handler;
            		
            		Message servo02Update = Message.obtain(uiHandler, SERVO_02);
            		if(uiHandler != null) {
            			uiHandler.sendMessage(servo02Update);
            		}
            }  
      
            // SeekBar のタッチの開始時の動作  
            public void onStartTrackingTouch(SeekBar seekBar) {  
            }        
            // SeekBar のタッチの終了時の動作  
            public void onStopTrackingTouch(SeekBar seekBar) {  
            }  
        });
      
        /******* originボタンの定義 *******/
	    button1 = (Button) findViewById(R.id.OriginButton);
        
	    button1.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {  
	            // originボタンがクリックされた時に呼び出される
        		seekBar1.setProgress(127);//サーボ01を強制的に原点に戻す
        		seekBar2.setProgress(127);//サーボ02を強制的に原点に戻す
	        }
	    });
       
	    /******* CamChangeボタンの定義 *******/ 
        button2 = (Button) findViewById(R.id.CamChangeButton);
        
	    button2.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {  
	             // CamChangeボタンがクリックされた時に呼び出される
	        	 mCameraView.cameraChange();     //カメラの内外を切り替える
	        }
	    }); 	
	
	}//onCreate 

	@Override
	public void onStart() {
		super.onStart();
		this.setTitle("RT-ADKmini デバイスが接続されていません");
		
	}
	
    @Override
    public void onResume() {
    	super.onResume();
        accessoryManager.enable(this,getIntent());
    }
        
    @Override
    public void onPause() {
    	super.onPause();
    	accessoryManager.disable(this);
    	disconnectAccessory();
    }
    
    //RT-ADKminiが未接続時に呼ばれる
    public void disconnectAccessory() {
    	this.setTitle("RT-ADKmini デバイスが接続されていません");    
    }
    
    /******* USB Manager thread　または UIからの messageを受け取り処理するhandler*******/
    private Handler handler = new Handler() {
    	@Override
    	public void handleMessage(Message msg) {
    		byte[] commandPacket = new byte[2];
    		
			switch(msg.what) {		
				case SERVO_01:
					if(accessoryManager.isConnected() == false) {
						return;
					}
	
					commandPacket[0] = SERVO_01;
					commandPacket[1] = (byte) servo01_pos;
					accessoryManager.write(commandPacket);			
					break;
				case SERVO_02:
					if(accessoryManager.isConnected() == false) {
						return;
					}
	 
					commandPacket[0] = SERVO_02;
					commandPacket[1] = (byte) servo02_pos;
					accessoryManager.write(commandPacket);			
					break;				
	
				case USBAccessoryWhat:
					switch(((USBAccessoryManagerMessage)msg.obj).type) {
						case READ:
							if(accessoryManager.isConnected() == false) {
								return;
							}
							
							while(true) {
								if(accessoryManager.available() < 2) {
									//コマンドの最大サイズは2byteなのでバッファのサイズが2btyeより小さくなったら抜ける
									break;
								}
							    
								//commandPacketにバッファのデータを書き込み,読み込んだデータを消去する
								accessoryManager.read(commandPacket);
								
								switch(commandPacket[0]) {
									//DIN0,1,2,3に繋がっているタクトスイッチが押された時の処理
								    case PUSHBUTTON_STATUS_CHANGE:
						    			if( (commandPacket[1] & BUTTON_1_PRESSED) == BUTTON_1_PRESSED ){
						    				servo01_pos +=10;  
						    				if(servo01_pos >= 255) servo01_pos = 255; 
						    				seekBar1.setProgress(servo01_pos);	
						    			}
						    			if( (commandPacket[1] & BUTTON_2_PRESSED) == BUTTON_2_PRESSED ){
					    					servo01_pos -=10;  
					    					if(servo01_pos <= 0) servo01_pos = 0; 
					    					seekBar1.setProgress(servo01_pos);	
					    			    }
						    			if( (commandPacket[1] & BUTTON_3_PRESSED) == BUTTON_3_PRESSED ){
					    					servo02_pos +=10;  
					    					if(servo02_pos >= 255) servo02_pos = 255; 
					    					seekBar2.setProgress(servo02_pos);	
					    			    }
					    			    if( (commandPacket[1] & BUTTON_4_PRESSED) == BUTTON_4_PRESSED ){
				    					    servo02_pos -=10;  
				    					    if(servo02_pos <= 0) servo02_pos = 0; 
				    					    seekBar2.setProgress(servo02_pos);	
				    			        }
						    		    break;			    						    			
								}
							}//while
							break;
						case CONNECTED:
							break;
						case READY:
							setTitle("RT-ADKmini デバイスが接続されました");
							break;
						case DISCONNECTED:
							disconnectAccessory();
							break;
					} //switch(~.type)				
	   				break;
				default:
					break;
			} //switch(msg.what)
    	} //handleMessage
    }; //handler
  
}