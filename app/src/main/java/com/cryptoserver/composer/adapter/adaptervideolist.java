package com.cryptoserver.composer.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.activity.FullScreenVideoActivity;
import com.cryptoserver.composer.interfaces.adapteritemclick;
import com.cryptoserver.composer.models.video;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import java.util.ArrayList;

/**
 * Created by devesh on 6/8/18.
 */

public class adaptervideolist extends RecyclerSwipeAdapter<adaptervideolist.myViewHolder> {

    Context context;
    ArrayList<video> arrayvideolist = new ArrayList<video>();
    adapteritemclick adapter;

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        public TextView tvvideoname,tvvideocreatedate,tvvideoduration,tvvideodescription;
        EditText edtvideoname;
        public ImageView imgshareicon,imgdeleteicon,img_videothumbnail,img_full_screen,img_play_pause;
        public SwipeLayout swipelayout;
        public Button btnedit;
      //  public ImageView imgvideothumbnail,imgshareicon,imgdeleteicon,img_videothumbnail,img_play;
       // public costomvideoview videoviewthumbnail;

        public myViewHolder(View view) {
            super(view);
            swipelayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            tvvideoname = (TextView) view.findViewById(R.id.tv_videoname);
            edtvideoname = (EditText) view.findViewById(R.id.edt_videoname);
            tvvideocreatedate = (TextView) view.findViewById(R.id.tv_videocreatedate);
            tvvideoduration = (TextView) view.findViewById(R.id.tv_videoduration);
            tvvideodescription = (TextView) view.findViewById(R.id.tv_videodescription);
            imgshareicon = (ImageView) view.findViewById(R.id.img_shareicon);
            imgdeleteicon = (ImageView) view.findViewById(R.id.img_deleteicon);
            img_videothumbnail = (ImageView) view.findViewById(R.id.img_videothumbnail);
            btnedit = (Button) view.findViewById(R.id.btn_edit);
        }
    }

    public adaptervideolist(Context context, ArrayList<video> arrayvideolist, adapteritemclick adapter){
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

        // set the uri for the video view
        holder.swipelayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        //drag from right
        holder.swipelayout.addDrag(SwipeLayout.DragEdge.Right, holder.swipelayout.findViewById(R.id.bottom_wraper));

        //handling different event when swiping
        holder.swipelayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {
                //when the SurfaceView totally cover the BottomView.
            }

            @Override
            public void onOpen(SwipeLayout layout) {
                //when the BottomView totally show.
            }

            @Override
            public void onStartClose(SwipeLayout layout) {

            }

            @Override
            public void onClose(SwipeLayout layout) {
                //when the SurfaceView totally cover the BottomView.
            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                //you are swiping.
            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                //when user's hand released.
            }
        });

        holder.swipelayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });


        holder.btnedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.swipelayout.close();

                holder.tvvideoname.setVisibility(View.VISIBLE);
                /*holder.edtvideoname.setVisibility(View.VISIBLE);
                holder.edtvideoname.setText(holder.tvvideoname.getText());
                holder.edtvideoname.setSelection(holder.edtvideoname.getText().length());
                holder.edtvideoname.requestFocus();*/


               /* String path = arrayvideolist.get(position).getPath();

                Log.e("selected video path = ", "" + path);

                File sourceFile = new File(path);
                File filedirectory = sourceFile.getParentFile();
                String filename=  sourceFile.getName();

                File from = new File(filedirectory,filename);
                File to = new File(filedirectory,"demo.mp4");
                from.renameTo(to);*/

            }
        });


       /* // set the uri for the video view
        holder.simpleVideoView.setVideoPath(arrayvideolist.get(position).getPath());

        holder.img_play_pause.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        holder.img_full_screen.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

        holder.simpleVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                // start a video
                //holder.simpleVideoView.start();
                mediaPlayer.setVolume(0,0);
                //holder.img_videothumbnail.setVisibility(View.VISIBLE);
                if(holder.simpleVideoView.isPlaying())
                {
                   // Toast.makeText(context, "Playing", Toast.LENGTH_SHORT).show();
                }
                else
                {
                   // Toast.makeText(context, "Not Playing", Toast.LENGTH_SHORT).show();
                    holder.img_videothumbnail.setVisibility(View.VISIBLE);
                    holder.simpleVideoView.setVisibility(View.VISIBLE);
                    holder.img_play_pause.setImageResource(R.drawable.icon_play);
                }


               // Toast.makeText(context, "Prepared...", Toast.LENGTH_SHORT).show(); // display a toast when an video is completed
            }
        });

        holder.simpleVideoView.setPlayPauseListener(new costomvideoview.PlayPauseListener() {

            @Override
            public void onPlay() {
                System.out.println("Play!");
                holder.img_play_pause.setImageResource(R.drawable.icon_pause);
            }
            @Override
            public void onPause() {
                System.out.println("Pause!");
                holder.img_play_pause.setImageResource(R.drawable.icon_play);
            }
        });

        holder.img_play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.img_videothumbnail.setVisibility(View.GONE);
                holder.simpleVideoView.setVisibility(View.VISIBLE);
                if(holder.simpleVideoView.isPlaying())
                    holder.simpleVideoView.pause();
                else
                    holder.simpleVideoView.start();
            }
        });

        holder.img_full_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in=new Intent(context, FullScreenVideoActivity.class);
                in.putExtra("videopath",arrayvideolist.get(position).getPath());
                context.startActivity(in);
            }
        });

        // implement on completion listener on video view
        holder.simpleVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                holder.img_videothumbnail.setVisibility(View.VISIBLE);
                //holder.simpleVideoView.setVisibility(View.GONE);
                holder.img_play_pause.setImageResource(R.drawable.icon_play);
              //  Toast.makeText(context, "Thank You...!!!", Toast.LENGTH_LONG).show(); // display a toast when an video is completed
            }
        });
        holder.simpleVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
             //   Toast.makeText(context, "Oops An Error Occur While Playing Video...!!!", Toast.LENGTH_LONG).show(); // display a toast when an error is occured while playing an video
                return false;
            }
        });

        holder.simpleVideoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START: {
                        holder.img_videothumbnail.setVisibility(View.GONE);
                    //    Toast.makeText(context, "Render start", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
                return false;
            }
        });*/

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


        holder.img_videothumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in=new Intent(context, FullScreenVideoActivity.class);
                in.putExtra("videopath",arrayvideolist.get(position).getPath());
                context.startActivity(in);
            }
        });

        mItemManger.bindView(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return arrayvideolist.size();
    }
}
