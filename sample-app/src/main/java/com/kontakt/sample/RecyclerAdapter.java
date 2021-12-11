package com.kontakt.sample;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>
{
    String[] data;
    Context context;

    public RecyclerAdapter(Context context, String[] data) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.custome_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder holder, int position) {
        holder.textView.setText(data[holder.getAdapterPosition()]);

        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundAndColor(holder);
                Intent intent = new Intent(context, Directions.class);
                intent.putExtra("cel", data[holder.getAdapterPosition()]);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        CardView myCardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.roomNames);
            myCardView = itemView.findViewById(R.id.myCardView);
        }
    }

    public void soundAndColor(RecyclerAdapter.ViewHolder holder){
        MediaPlayer mMediaPlayer;
        mMediaPlayer = MediaPlayer.create(context, R.raw.bubble);
        mMediaPlayer.start();

        String text = "Wybrano "+ data[holder.getAdapterPosition()];
        SpannableStringBuilder biggerText = new SpannableStringBuilder(text);

        //size of toast font message
        biggerText.setSpan(new RelativeSizeSpan(1.8f), 0, text.length(), 0);
        Toast.makeText(context, biggerText, Toast.LENGTH_LONG).show();
        //holder.myCardView.setCardBackgroundColor(context.getResources().getColor(R.color.mediumindigo));
        //Toast.makeText(context, "Wybrano "+ data[holder.getAdapterPosition()], Toast.LENGTH_SHORT).show();
    }

}
