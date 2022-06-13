package proyecto.red_chart_v1.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;

import proyecto.red_chart_v1.R;

public class ShowImageOrVideoActivity extends AppCompatActivity {

    ImageView mImageViewBack;
    ImageView mImageViewPicture;
    ImageView mImageViewVideo;
    FrameLayout mFrameLayoutVideo;
    VideoView mVideoView;
    View mView;

    String mExtraUrl;
    String mExtraType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image_or_video);
        setStatusBarColor();

        mImageViewBack = findViewById(R.id.imageViewBack);
        mImageViewPicture = findViewById(R.id.imageViewPicture);
        mImageViewVideo = findViewById(R.id.imageViewVideo);
        mFrameLayoutVideo = findViewById(R.id.frameLayoutVideo);
        mVideoView = findViewById(R.id.videoView);
        mView = findViewById(R.id.viewVideo);

        mExtraType = getIntent().getStringExtra("type");
        mExtraUrl = getIntent().getStringExtra("url");

        //Muestra la imagen
        if(mExtraType.equals("imagen")) {
            mFrameLayoutVideo.setVisibility(View.GONE);
            mImageViewPicture.setVisibility(View.VISIBLE);
            Picasso.get().load(mExtraUrl).into(mImageViewPicture);

            //Muestra el video
        } else {
            mFrameLayoutVideo.setVisibility(View.VISIBLE);
            mImageViewPicture.setVisibility(View.GONE);

            //Reproduce el video
            Uri uri = Uri.parse(mExtraUrl);
            mVideoView.setVideoURI(uri);
        }

        //Reproduce el video o lo pausa
        mFrameLayoutVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Si el video no se esta reproduciendo
                if(!mVideoView.isPlaying()){
                    mView.setVisibility(View.GONE);
                    mImageViewVideo.setVisibility(View.GONE);
                    mVideoView.start();

                    //Si el video no se esta reproduciendo
                } else {
                    mView.setVisibility(View.VISIBLE);
                    mImageViewVideo.setVisibility(View.VISIBLE);
                    mVideoView.pause();
                }
            }
        });

        //Si pincha en la flecha
        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        //Si el video se esta reproduciendo
        if(mVideoView.isPlaying()){
            mVideoView.pause();
        }
    }

    //Cambia el color de la barra de estado del movil cuando aparece el fragment de enviar imagenes
    private void setStatusBarColor(){
        //Si la version de Android en la que esta la app instalada es mayor a M
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorFullBlack, this.getTheme()));
        }
        //Si la version de Android en la que esta la app instalada es mayor a LOLLIPOP
        else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorFullBlack));
        }
    }
}