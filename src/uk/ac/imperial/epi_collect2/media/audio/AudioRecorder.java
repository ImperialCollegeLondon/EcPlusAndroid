package uk.ac.imperial.epi_collect2.media.audio;

import java.io.File;
import java.io.IOException;

import uk.ac.imperial.epi_collect2.EntryNote;
//import uk.ac.imperial.epi_collect2.EntryNote2;
import uk.ac.imperial.epi_collect2.R;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

	@SuppressLint("NewApi")
	public class AudioRecorder {

		  MediaRecorder recorder;// = new MediaRecorder(); 
		  MediaPlayer player; // = new MediaPlayer();
		  boolean recording = false, playing = false;
		  ImageButton playButton, recordButton, stopButton;
		  String oldtext;
		  TextView tview;
		  EntryNote calling_enote;
		  //EntryNote2 calling_enote2;
		  
		  public AudioRecorder() {			  
			  
		  }
		  
		  /**
		   * Starts a new recording.
		   */
		  
		  public void record(String path, TextView tv, ImageButton rbutton, ImageButton pbutton, ImageButton sbutton) throws IOException {
			  
			  if(playing){ //player.isPlaying()){
				  return;
			  }
			  
			  recordButton = rbutton;
			  playButton = pbutton;
			  stopButton = sbutton;
			  
			  recorder = new MediaRecorder();
			  //recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			  //recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); //THREE_GPP);
			  //recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			  //recorder.setAudioEncodingBitRate(16);
			  //recorder.setAudioSamplingRate(44100);
			  
			  recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			  if (Build.VERSION.SDK_INT >= 10) {
			      recorder.setAudioSamplingRate(44100);
			      recorder.setAudioEncodingBitRate(96000);
			      recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			      recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
			  } else {
			      // older version of Android, use crappy sounding voice codec
			      recorder.setAudioSamplingRate(8000);
			      recorder.setAudioEncodingBitRate(12200);
			      recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			      recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			  }
			  			  
			  recorder.setOutputFile(path);
			  recorder.prepare();
			  recorder.start();
			  recording = true;
			  playButton.setEnabled(false);
			  recordButton.setEnabled(false);
			  stopButton.setEnabled(true);
			  tv.setTextColor(Color.RED);
			  tv.setText(R.string.recording); //"RECORDING");
		  }

		  public void play(EntryNote en, String path, TextView tv, ImageButton pbutton, ImageButton rbutton, ImageButton sbutton) throws IOException {

			  calling_enote = en;
			  tview = tv;
			  playButton = pbutton;
			  recordButton = rbutton;
			  stopButton = sbutton;
			  oldtext = tv.getText().toString();
			  
			  if(recording){
				  return;
			//	  stop();
			//	  recording = false;
			  }
			  
			  player = new MediaPlayer();
			  
			  player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
	  		    	//@Override
					public void onCompletion(MediaPlayer mp) {
						player.reset();
						player.release();
						playing = false;
						playButton.setEnabled(true);
						recordButton.setEnabled(true);
						stopButton.setEnabled(false);
						tview.setTextColor(Color.WHITE);
						tview.setText(oldtext);
						// Ensures previous/next buttons work when playback stops
						calling_enote.audioactive = false;
					}
	  		           
	  		    }); 
			  
			  //player = new MediaPlayer();
			  player.reset();
			  player.setDataSource(path);
			  player.prepare();
			  
			  player.start();
			  playing = true;
			  playButton.setEnabled(false);
			  recordButton.setEnabled(false);
			  stopButton.setEnabled(true);
			  tv.setTextColor(Color.BLUE);
			  tv.setText(R.string.playing); //"PLAYING");
			  /*recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			  recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			  recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			  recorder.setOutputFile(path);
			  recorder.prepare();
			  recorder.start();*/
			  }
		  
		 /* public void play(EntryNote2 en, String path, TextView tv, ImageButton pbutton, ImageButton rbutton, ImageButton sbutton) throws IOException {

			  calling_enote2 = en;
			  tview = tv;
			  playButton = pbutton;
			  recordButton = rbutton;
			  stopButton = sbutton;
			  oldtext = tv.getText().toString();
			  
			  if(recording){
				  return;
			  }
			  
			  player = new MediaPlayer();
			  
			  player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
	  		    	//@Override
					public void onCompletion(MediaPlayer mp) {
						player.reset();
						player.release();
						playing = false;
						playButton.setEnabled(true);
						recordButton.setEnabled(true);
						stopButton.setEnabled(false);
						tview.setTextColor(Color.WHITE);
						tview.setText(oldtext);
						// Ensures previous/next buttons work when playback stops
						calling_enote2.audioactive = false;
					}
	  		           
	  		    }); 
			  
			  player.reset();
			  player.setDataSource(path);
			  player.prepare();
			  
			  player.start();
			  playing = true;
			  playButton.setEnabled(false);
			  recordButton.setEnabled(false);
			  stopButton.setEnabled(true);
			  tv.setTextColor(Color.BLUE);
			  tv.setText(R.string.playing); //"PLAYING");
			  } */
		  
		  /**
		   * Stops a recording that has been previously started.
		   */
		  public void stop(TextView tv) throws IOException {
			  if(recording){
				  recorder.stop();
				  recorder.reset();
				  recorder.release();
				  recording = false;
				  playButton.setEnabled(true);
				  recordButton.setEnabled(true);
				  stopButton.setEnabled(false);
				  tv.setTextColor(Color.WHITE);
				  tv.setText(R.string.audio_available); //"Audio Available");
			  }
			  else if(playing){
				  player.stop();
				  player.reset();
				  player.release();
				  playing = false;
				  playButton.setEnabled(true);
				  recordButton.setEnabled(true);
				  stopButton.setEnabled(false);
				  tv.setTextColor(Color.WHITE);
				  tv.setText(oldtext);
			  }
		  }
		  
		  //public void stop() throws IOException {
			//  player.stop();
			//  player.release();
		  //}
		  

		}


