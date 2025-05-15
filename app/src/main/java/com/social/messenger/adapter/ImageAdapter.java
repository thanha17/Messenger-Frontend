package com.social.messenger.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.social.messenger.R;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private final List<String> imagePaths;
    private String selectedImagePath = null;
    private OnMediaSelectedCallback listener;
    private String mediaType;

    public ImageAdapter(List<String> imagePaths, String mediaType) {
        this.imagePaths = imagePaths;
        this.mediaType = mediaType;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String path = imagePaths.get(position);
        Glide.with(holder.imageView.getContext()).load(path).into(holder.imageView);

        boolean isSelected = path.equals(selectedImagePath);
        holder.overlay.setVisibility(isSelected ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            if (path.equals(selectedImagePath)) {
                // Gửi ảnh ở đây
                // TODO: Thực hiện gửi ảnh, ví dụ log/logcat hoặc callback
                System.out.println("Gửi ảnh: " + path);
                if (listener != null) {
                    listener.onMediaSelectedCallback(path, mediaType); // Gọi callback
                }
                selectedImagePath = null;
                notifyDataSetChanged();
            } else {
                selectedImagePath = path;
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return imagePaths.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        View overlay;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            overlay = itemView.findViewById(R.id.overlayView);
        }
    }
    public interface OnMediaSelectedCallback {
        void onMediaSelectedCallback(String imagePath, String mediaType);
    }
    public void setOnMediaSelectedCallback(OnMediaSelectedCallback listener) {
        this.listener = listener;
    }

}
