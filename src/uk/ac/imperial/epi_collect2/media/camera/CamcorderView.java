package uk.ac.imperial.epi_collect2.media.camera;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CamcorderView extends SurfaceView implements SurfaceHolder.Callback {

	MediaRecorder recorder;
	SurfaceHolder holder;
	String outputFile = Environment.getExternalStorageDirectory()+"/EpiCollect/temp.mp4"; ///sdcard/epicollect_temp.mp4";

	public CamcorderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		holder = getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		// If video recording fails on a device use this default code
		/*recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA); //.DEFAULT);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT); //.MPEG_4);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT); //.AMR_NB);
		recorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT); //.MPEG_4_SP); */
		// To here
		
		// If video recording fails on a device comment out this code
		recorder = new MediaRecorder();
	    Method[] methods = recorder.getClass().getMethods();
	    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
	    recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
	    recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
	   // recorder.setVideoFrameRate(24);
	   // recorder.setVideoSize(720, 480);

	    for (Method method: methods){
	    	 try{
	        if (method.getName().equals("setAudioChannels")){
	                method.invoke(recorder, String.format("audio-param-number-of-channels=%d", 1));
	        } 
	        else if(method.getName().equals("setAudioEncodingBitRate")){
	                method.invoke(recorder,12200);
	            }
	          else if(method.getName().equals("setVideoEncodingBitRate")){
	            method.invoke(recorder, 3000000);
	        }
	          else if(method.getName().equals("setAudioSamplingRate")){
	            method.invoke(recorder,8000);
	            }
	          /*   else if(method.getName().equals("setVideoFrameRate")){
	            method.invoke(recorder,24);
	        } */
	    }catch (IllegalArgumentException e) {

	        e.printStackTrace();
	    } catch (IllegalAccessException e) {

	        e.printStackTrace();
	    } catch (InvocationTargetException e) {

		
	    	   e.printStackTrace();
	    }
	    }

	    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
	    recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);

		// To here
		
		
		
		/*MediaRecorder recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
		recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
		recorder.setOutputFile(outputFile);
		recorder.setVideoSize(720,480);
		recorder.setPreviewDisplay(holder.getSurface());*/
		
		/*MediaRecorder recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
		recorder.setOutputFile(outputFile);
		recorder.setPreviewDisplay(holder.getSurface()); */

		try {
			recorder.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		//recorder.setVideoSize(100, 100);
		// recorder.setVideoSize(480, 320);
		// recorder.setVideoFrameRate(24);
		// recorder.setMaxDuration(10000);
	}

	//public void setSize(int w, int h){
	//	recorder.setVideoSize(w, h);
	//}
	
	public void surfaceCreated(SurfaceHolder holder) {
		recorder.setOutputFile(outputFile);
		recorder.setPreviewDisplay(holder.getSurface());
		if (recorder != null) {
			try {
				recorder.prepare();
			} catch (IllegalStateException e) {
				Log.e("IllegalStateException", e.toString());
			} catch (IOException e) {
				Log.e("IOException", e.toString());
			}
		}
	} 
	
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
	}

	public void setOutputFile(String filename)
	{
		outputFile = filename;
		recorder.setOutputFile(filename);
	}
	
    public void startRecording()
    {
    	recorder.start();
    }
    
    public void stopRecording()
    {
    	recorder.stop();
    	recorder.release();
    }
}