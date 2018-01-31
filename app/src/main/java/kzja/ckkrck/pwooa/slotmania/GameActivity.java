package kzja.ckkrck.pwooa.slotmania;


import org.cocos2d.nodes.CCDirector;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.opengl.CCTexture2D;
import org.cocos2d.transitions.CCFadeTransition;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.google.ads.InterstitialAd;
import kzja.ckkrck.pwooa.Layer.LogoLayer;
import kzja.ckkrck.pwooa.Layer.TitleLayer;
import kzja.ckkrck.pwooa.Other.ScoreManager;
import kzja.ckkrck.pwooa.R;
import kzja.ckkrck.pwooa.utils.Actions;
import kzja.ckkrck.pwooa.utils.Random;
import com.vungle.sdk.VunglePub;
import com.vungle.sdk.VunglePub.EventListener;


public class GameActivity extends Activity implements AdListener, EventListener{
	private CCGLSurfaceView mGLSurfaceView;	
	private InterstitialAd interstitialAd;
	private boolean startState ;

	//@Override 
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
        getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN,
                LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(LayoutParams.FLAG_KEEP_SCREEN_ON,
                LayoutParams.FLAG_KEEP_SCREEN_ON);

        mGLSurfaceView = new CCGLSurfaceView(this);        
        if(!startState){
        	CCDirector.sharedDirector().setScreenSize(CCDirector.sharedDirector().winSize().width, 
	        CCDirector.sharedDirector().winSize().height);
	        CCDirector.sharedDirector().setDeviceOrientation(CCDirector.kCCDeviceOrientationLandscapeLeft);
		    CCDirector.sharedDirector().getActivity().setContentView(mGLSurfaceView, createLayoutParams());
		    CCDirector.sharedDirector().attachInView(mGLSurfaceView);       
	        CCDirector.sharedDirector().setAnimationInterval(1.0f / 60);
	        CCDirector.sharedDirector().setDisplayFPS(false);
	        CCTexture2D.setDefaultAlphaPixelFormat(Config.ARGB_8888);  
//	        getAdmob();
	        
//		    getInterstitialAd();
//		    getVungleAd();
			InitParam();
			CCDirector.sharedDirector().runWithScene( LogoLayer.scene());
			startState = true;
        }
	    
    }
	public void getAdmob(){
		LinearLayout.LayoutParams adParams = new LinearLayout.LayoutParams(getWindowManager().getDefaultDisplay().getWidth() + getWindowManager().getDefaultDisplay().getWidth() - (int) G._getX(640), getWindowManager().getDefaultDisplay().getHeight()
                + getWindowManager().getDefaultDisplay().getHeight()
                - getWindowManager().getDefaultDisplay().getHeight() * 2 - 120);

	    AdView adView = new AdView(GameActivity.this, AdSize.BANNER, getResources().getString(R.string.admob_id));
	    AdRequest adRequest = new AdRequest();
		adView.loadAd(adRequest);    	    
	    CCDirector.sharedDirector().getActivity().addContentView(adView, adParams);
	}
	public void getInterstitialAd(){       	
		interstitialAd = new InterstitialAd(this, getResources().getString(R.string.admob_id));
        interstitialAd.setAdListener(this);
        AdRequest adRequest = new AdRequest();
        interstitialAd.loadAd(adRequest);	  
	}
    public void getVungleAd(){
    	VunglePub.init(this, getResources().getString(R.string.vungle_id));
    	VunglePub.setEventListener(this);
    }
    
    //@Override 
    public void onStart() {
        super.onStart();       

    }    
   
    @Override
	public void onBackPressed() {
//    	if(!G.titleState)
//    		getInterstitialAd();
    	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
    	    @Override
    	    public void onClick(DialogInterface dialog, int which) {
    	        switch (which){
    	        case DialogInterface.BUTTON_POSITIVE:
    	        	if(G.titleState){
    					G.titleState = false;
    					CCDirector.sharedDirector().replaceScene(CCFadeTransition.transition(0.5f, TitleLayer.scene()));								
    				}else{
    					G.stopSound();    					
    					CCDirector.sharedDirector().end();
    			        ScoreManager.releaseScoreManager();
    			        finish();
    				}	
    	            break;

    	        case DialogInterface.BUTTON_NEGATIVE:
    	            //No button clicked
    	            break;
    	        }
    	    }
    	};

    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage("Are you sure? You may lose all your coins.").setPositiveButton("Yes", dialogClickListener)
    	    .setNegativeButton("No", dialogClickListener).show();
	}
    
    
	private void InitParam() { 		
		G.g_Context = this;		
		G.curLevel = 1;
		G.curLine = 1;
		G.maxline = 1;
		G.bet = 1;		
		
	}	
	@Override public void onPause() {
	      super.onPause();
	      VunglePub.onPause();
	      CCDirector.sharedDirector().pause();
	      G.pauseSound();
	        
	 }

	 @Override public void onResume() {
	     super.onResume();
	     CCDirector.sharedDirector().resume();
	     VunglePub.onResume();
	     G.resumeSound();
	     review();
	  }

	  @Override public void onDestroy() {
	       super.onDestroy();
	       G.stopSound();
	       CCDirector.sharedDirector().end();
	       ScoreManager.releaseScoreManager();
	  }
   
	
    private LayoutParams createLayoutParams() {
        final DisplayMetrics pDisplayMetrics = new DisplayMetrics();
		CCDirector.sharedDirector().getActivity().getWindowManager().getDefaultDisplay().getMetrics(pDisplayMetrics);
		
		//final float mRatio = (float)G.DEFAULT_W / G.DEFAULT_H;
		final float mRatio = (float)G.DEFAULT_W / G.DEFAULT_H;
		final float realRatio = (float)pDisplayMetrics.widthPixels / pDisplayMetrics.heightPixels;

		final int width;
		final int height;
		if(realRatio < mRatio) {
			width = pDisplayMetrics.widthPixels;
			height = Math.round(width / mRatio);
		} else {
			height = pDisplayMetrics.heightPixels;
			width = Math.round(height * mRatio);
		}

		final LayoutParams layoutParams = new LayoutParams(width, height);

		layoutParams.gravity = Gravity.CENTER;
		return layoutParams;
	}

	@Override
	public void onDismissScreen(Ad arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLeaveApplication(Ad arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPresentScreen(Ad arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReceiveAd(Ad ad) {
		// TODO Auto-generated method stub
		Log.i("slot_machine", "Received ad activity");
		if (ad == interstitialAd) {				
			interstitialAd.show();
			
		}
	}
	@Override
	public void onVungleAdEnd() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onVungleAdStart() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onVungleView(double watchedSeconds, double totalAdSeconds) {
		// TODO Auto-generated method stub
		final double watchedPercent = watchedSeconds/totalAdSeconds;
		if(watchedPercent >=1f){
			G.allCoin += 250;
			G.saveSetting();
		}		
	}
	
	/**
	 * Review
	 */
	private void review() {
		int random = Random.random.nextInt(100);
		if (random < 15) {
			GameActivity.this.showReviewDialog();
		}
	}

    /**
     * Set Review Dialog
     */
    public void showReviewDialog() {
    	String message = "Do you Like this app? Get FREE 200 coins by giving us 5 STARS review or SHARE this app to your friends!";
		
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
            .setPositiveButton("Give Review!", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                	G.allCoin += 200;
                	G.saveSetting();
                    Actions.giveUsReview(GameActivity.this);                    
                }
            })
            .setNegativeButton("Share with Friends!", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					G.allCoin += 200;
					G.saveSetting();
					Actions.shareApp(GameActivity.this);
				}
            });
        builder.create().show();
    }	
	
}