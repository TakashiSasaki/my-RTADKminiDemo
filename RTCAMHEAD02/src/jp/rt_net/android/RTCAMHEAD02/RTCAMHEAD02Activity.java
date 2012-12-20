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
	
	//RT-ADKmini�Ƃ̃f�[�^�̂����Ŏg���R�}���h
	private static final int USBAccessoryWhat           = 0;
	public static final int PUSHBUTTON_STATUS_CHANGE	= 2;
   	public static final int SERVO_01 					= 7;
	public static final int SERVO_02					= 8;
	
    //�{�^���̉������擾�p	
	public static final int BUTTON_1_PRESSED			= 0x01;
	public static final int BUTTON_2_PRESSED			= 0x02;
	public static final int BUTTON_3_PRESSED			= 0x04;
	public static final int BUTTON_4_PRESSED			= 0x08;
	
	//�T�[�{���[�^�̊p�x���	
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
        
       	// �쐬����CameraView�N���X���C���X�^���X��
       	mSurfaceView = (SurfaceView)findViewById(R.id.surface_view);                  
	    mCameraView = new CameraView(this, mSurfaceView);
        
        /******* SeekBar1�̒�` *******/
        seekBar1 = (SeekBar) findViewById(R.id.seekBar1);  
        value_of_seekbar1 = (TextView)findViewById(R.id.valueOfseekBar1);
        // �V�[�N�o�[�̏����l��TextView�ɕ\��
        value_of_seekbar1.setText("Current Value:"+seekBar1.getProgress());     
        //�V�[�N�o�[�̌��ݒl,�����l,�Z�J���_���l���Z�b�g  
        seekBar1.setMax(255);  
        seekBar1.setProgress(0);  
        seekBar1.setSecondaryProgress(0);  
        // SeekBar �̒l���ύX���ꂽ�Ƃ��ɌĂяo�����R�[���o�b�N��o�^  
        seekBar1.setOnSeekBarChangeListener(  
        	new OnSeekBarChangeListener() {  
            
        	// SeekBar1 �̒l���ς�����Ƃ��̓���   
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) { 
        			servo01_pos	= progress;                                  //servo01�̊p�x��seekBar��progress�Ō��܂�
            		value_of_seekbar1.setText("Current Value:"+progress);    //text��seekbar1�̒l��\��
            		uiHandler = handler;
            		
            		Message servo01Update = Message.obtain(uiHandler, SERVO_01);
            		if(uiHandler != null) {
            			uiHandler.sendMessage(servo01Update);
            		}	
            }     
            // SeekBar �̃^�b�`�̊J�n���̓���  
            public void onStartTrackingTouch(SeekBar seekBar) {  
            }        
            // SeekBar �̃^�b�`�̏I�����̓���  
            public void onStopTrackingTouch(SeekBar seekBar) {  
            }  
          }  
        );  
        
        /******* SeekBar2��` *******/
        seekBar2 = (SeekBar)findViewById(R.id.seekBar2);  
        value_of_seekbar2 = (TextView)findViewById(R.id.valueOfseekBar2);
        // �V�[�N�o�[�̏����l��TextView�ɕ\��
        value_of_seekbar2.setText("Current Value:"+seekBar2.getProgress());     
        //�V�[�N�o�[�̌��ݒl,�����l,�Z�J���_���l���Z�b�g  
        seekBar2.setMax(255);  
        seekBar2.setProgress(0);  
        seekBar2.setSecondaryProgress(0);  
        // SeekBar �̒l���ύX���ꂽ�Ƃ��ɌĂяo�����R�[���o�b�N��o�^  
        seekBar2.setOnSeekBarChangeListener(  
        	new OnSeekBarChangeListener() {  
            // SeekBar2 �̒l���ς�����Ƃ��̓���   
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) { 
        			servo02_pos	= progress;
            		value_of_seekbar2.setText("Current Value:"+progress);
            		uiHandler = handler;
            		
            		Message servo02Update = Message.obtain(uiHandler, SERVO_02);
            		if(uiHandler != null) {
            			uiHandler.sendMessage(servo02Update);
            		}
            }  
      
            // SeekBar �̃^�b�`�̊J�n���̓���  
            public void onStartTrackingTouch(SeekBar seekBar) {  
            }        
            // SeekBar �̃^�b�`�̏I�����̓���  
            public void onStopTrackingTouch(SeekBar seekBar) {  
            }  
        });
      
        /******* origin�{�^���̒�` *******/
	    button1 = (Button) findViewById(R.id.OriginButton);
        
	    button1.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {  
	            // origin�{�^�����N���b�N���ꂽ���ɌĂяo�����
        		seekBar1.setProgress(127);//�T�[�{01�������I�Ɍ��_�ɖ߂�
        		seekBar2.setProgress(127);//�T�[�{02�������I�Ɍ��_�ɖ߂�
	        }
	    });
       
	    /******* CamChange�{�^���̒�` *******/ 
        button2 = (Button) findViewById(R.id.CamChangeButton);
        
	    button2.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {  
	             // CamChange�{�^�����N���b�N���ꂽ���ɌĂяo�����
	        	 mCameraView.cameraChange();     //�J�����̓��O��؂�ւ���
	        }
	    }); 	
	
	}//onCreate 

	@Override
	public void onStart() {
		super.onStart();
		this.setTitle("RT-ADKmini �f�o�C�X���ڑ�����Ă��܂���");
		
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
    
    //RT-ADKmini�����ڑ����ɌĂ΂��
    public void disconnectAccessory() {
    	this.setTitle("RT-ADKmini �f�o�C�X���ڑ�����Ă��܂���");    
    }
    
    /******* USB Manager thread�@�܂��� UI����� message���󂯎�菈������handler*******/
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
									//�R�}���h�̍ő�T�C�Y��2byte�Ȃ̂Ńo�b�t�@�̃T�C�Y��2btye��菬�����Ȃ����甲����
									break;
								}
							    
								//commandPacket�Ƀo�b�t�@�̃f�[�^����������,�ǂݍ��񂾃f�[�^����������
								accessoryManager.read(commandPacket);
								
								switch(commandPacket[0]) {
									//DIN0,1,2,3�Ɍq�����Ă���^�N�g�X�C�b�`�������ꂽ���̏���
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
							setTitle("RT-ADKmini �f�o�C�X���ڑ�����܂���");
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