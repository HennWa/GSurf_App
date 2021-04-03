package com.example.gsurfexample.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gsurfexample.R;
import com.example.gsurfexample.source.local.historic.SurfSession;

public class SurfSessionAdapter extends ListAdapter<SurfSession, SurfSessionAdapter.SurfSessionHolder> {

    private onItemClickListener listener;
    private Context context;

    public SurfSessionAdapter(Context context) {
        super(DIFF_CALLBACK);
        this.context = context;
    }

    private static final DiffUtil.ItemCallback<SurfSession> DIFF_CALLBACK = new DiffUtil.ItemCallback<SurfSession>() {
        @Override
        public boolean areItemsTheSame(@NonNull SurfSession oldItem, @NonNull SurfSession newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull SurfSession oldItem, @NonNull SurfSession newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getLocation().equals(newItem.getLocation()) &&
                    oldItem.getDate().equals(newItem.getDate());
        }
    };

    @NonNull
    @Override
    public SurfSessionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.surfsession_item, parent, false);
        return new SurfSessionHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SurfSessionHolder holder, int position) {
        SurfSession currentSurfSession = getItem(position);
        holder.textViewTitle.setText(currentSurfSession.getTitle());
        holder.textViewLocation.setText(currentSurfSession.getLocation());
        holder.textViewDate.setText(String.valueOf(currentSurfSession.getDate()));
        holder.imageSession.setImageBitmap(getBitmapfromDrawable(R.drawable.test_image));
    }

    private Bitmap getBitmapfromDrawable(int drawableId){
        Bitmap bitmap = BitmapFactory.decodeResource( context.getResources(), drawableId);
        return bitmap;
    }

    public SurfSession getSurfSessionAt(int position){
      return getItem(position);
    };


    class SurfSessionHolder extends RecyclerView.ViewHolder{
        private TextView textViewTitle;
        private TextView textViewLocation;
        private TextView textViewDate;
        private ImageView imageSession;

        public SurfSessionHolder(View itemView){
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewLocation = itemView.findViewById(R.id.text_view_location);
            textViewDate = itemView.findViewById(R.id.text_view_date);
            imageSession = itemView.findViewById(R.id.image_view_session);

            Shader textShader = new LinearGradient(0, 0, 0, 100,
                    new int[]{ContextCompat.getColor(context, R.color.light_blue_transition),
                            ContextCompat.getColor(context, R.color.water_blue)},
                    new float[]{0, 1}, Shader.TileMode.CLAMP);
            textViewTitle.getPaint().setShader(textShader);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listener != null && position != RecyclerView.NO_POSITION){
                        listener.onItemClick(getItem(position));
                    }
                }
            });
        }
    }

    public interface onItemClickListener{
        void onItemClick(SurfSession surfSession);
    }

    public void setOnItemClickListener(onItemClickListener listener){
        this.listener = listener;
    }


}










