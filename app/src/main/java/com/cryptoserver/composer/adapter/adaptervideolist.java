package com.cryptoserver.composer.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.interfaces.AdapterItemClick;
import com.cryptoserver.composer.models.video;
import com.cryptoserver.composer.utils.costomvideoview;

import java.util.ArrayList;

/**
 * Created by devesh on 6/8/18.
 */

public class adaptervideolist extends  RecyclerView.Adapter<adaptervideolist.myViewHolder> {

    Context context;
    ArrayList<video> arrayvideolist = new ArrayList<video>();
    AdapterItemClick adapter;

    public class myViewHolder extends RecyclerView.ViewHolder {
        public TextView tvvideoname,tvvideocreatedate,tvvideoduration,tvvideodescription;
        public ImageView imgshareicon,imgdeleteicon,img_videothumbnail;
        public VideoView simpleVideoView;
      //  public ImageView imgvideothumbnail,imgshareicon,imgdeleteicon,img_videothumbnail,img_play;
       // public costomvideoview videoviewthumbnail;

        public myViewHolder(View view) {
            super(view);
            tvvideoname = (TextView) view.findViewById(R.id.tv_videoname);
            tvvideocreatedate = (TextView) view.findViewById(R.id.tv_videocreatedate);
            tvvideoduration = (TextView) view.findViewById(R.id.tv_videoduration);
            tvvideodescription = (TextView) view.findViewById(R.id.tv_videodescription);
            imgshareicon = (ImageView) view.findViewById(R.id.img_shareicon);
            imgdeleteicon = (ImageView) view.findViewById(R.id.img_deleteicon);
            img_videothumbnail = (ImageView) view.findViewById(R.id.img_videothumbnail);
            simpleVideoView = (VideoView) view.findViewById(R.id.simpleVideoView);
        }
    }

    public adaptervideolist(Context context, ArrayList<video> arrayvideolist, AdapterItemClick adapter){
        this.context = context;
        this.arrayvideolist = arrayvideolist;
        this.adapter = adapter;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_videolist, parent, false);

        return new myViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final myViewHolder holder, final int position) {

        holder.tvvideoname.setText(arrayvideolist.get(position).getName());
        holder.tvvideocreatedate.setText(arrayvideolist.get(position).getCreatedate());
        holder.tvvideoduration.setText("Duration : " +arrayvideolist.get(position).getDuration());
        //holder.tvvideodescription.setText(arrayvideolist.get(position).getMd5());

        /*holder.img_videothumbnail.setVisibility(View.GONE);

        holder.videoviewthumbnail.setVideoPath(arrayvideolist.get(position).getPath());
        holder.videoviewthumbnail.seekTo(100);


        holder.videoviewthumbnail.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setVolume(0f,0f);
            }
        });


        holder.img_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                holder.videoviewthumbnail.start();

            }
        });
*/

        // set the uri for the video view
        holder.simpleVideoView.setVideoPath(arrayvideolist.get(position).getPath());
        holder.simpleVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                // start a video
                //holder.simpleVideoView.start();


            }
        });

        // implement on completion listener on video view
        holder.simpleVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(context, "Thank You...!!!", Toast.LENGTH_LONG).show(); // display a toast when an video is completed
            }
        });
        holder.simpleVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(context, "Oops An Error Occur While Playing Video...!!!", Toast.LENGTH_LONG).show(); // display a toast when an error is occured while playing an video
                return false;
            }
        });

        Bitmap bitmap= ThumbnailUtils.createVideoThumbnail(arrayvideolist.get(position).getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
        holder.img_videothumbnail.setImageBitmap(bitmap);

        holder.imgshareicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.onItemClicked(arrayvideolist.get(position),1);
            }
        });

        holder.imgdeleteicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.onItemClicked(arrayvideolist.get(position),2);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayvideolist.size();
    }
}
