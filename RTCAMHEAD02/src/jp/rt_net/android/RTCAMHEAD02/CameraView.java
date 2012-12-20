package jp.rt_net.android.RTCAMHEAD02;


import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

//�J�����̐���
public class CameraView extends SurfaceView implements SurfaceHolder.Callback{
    private int cameraId = 0;      //�J�����̎��
	private SurfaceHolder holder;  //�z���_�[
    private Camera        camera;  //�J����
    protected Context context; 
    
    //�R���X�g���N�^
    public CameraView(Context context, SurfaceView sv ) {
        super(context);
        this.context = context;
        //�T�[�t�F�C�X�z���_�[�̐���
        holder=sv.getHolder();
        holder.addCallback(this);
        
        //�v�b�V���o�b�b�t�@�̎w��
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    //�T�[�t�F�C�X�����C�x���g�̏���
    public void surfaceCreated(SurfaceHolder holder) {
        //�J�����̏�����
        try {
            camera = Camera.open(cameraId);
        	camera.setPreviewDisplay(holder);
             
        } catch (Exception e) {
        }
        
    }

    //�T�[�t�F�C�X�ύX�C�x���g�̏���
    @Override
    public void surfaceChanged(SurfaceHolder holder,int format,int width,int height) {
    	//�J�����v���r���[�̊J�n    	
		camera.startPreview();
    }
    
    //�T�[�t�F�C�X����C�x���g�̏���
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //�J�����̃v���r���[��~
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera=null;
    }
    
 
 
    //�J�����̐؂�ւ������鏈��
    public void cameraChange(){
    	//�T�|�[�g���Ă���J�����̐����m���߂�
    	if(Camera.getNumberOfCameras() >= 1 ){
    	
	    	if(cameraId == 0) cameraId = 1;
	        else cameraId = 0;
	        
	        camera.stopPreview();
	        camera.release();
	        camera=Camera.open(cameraId);
	        try {
				camera.setPreviewDisplay(holder);
			} catch (Exception e) {
					
			}
    	}
    	else{
    		Toast.makeText(context.getApplicationContext(), "�T�|�[�g���Ă���J�����͈��ł�", Toast.LENGTH_SHORT).show();	
    	}
    	camera.startPreview();
    }
        
}