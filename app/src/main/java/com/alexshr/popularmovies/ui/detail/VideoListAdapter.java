package com.alexshr.popularmovies.ui.detail;

import android.databinding.DataBindingComponent;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.alexshr.popularmovies.R;
import com.alexshr.popularmovies.binding.DataBoundListAdapter;
import com.alexshr.popularmovies.data.Video;
import com.alexshr.popularmovies.databinding.VideoItemBinding;

import java.util.Objects;

public class VideoListAdapter extends DataBoundListAdapter<Video, VideoItemBinding> {
    private final DataBindingComponent dataBindingComponent;
    private final VideoClickCallback videoClickCallback;

    public VideoListAdapter(DataBindingComponent bindingComponent,
                            VideoClickCallback clickCallback) {
        dataBindingComponent = bindingComponent;
        videoClickCallback = clickCallback;
    }

    @Override
    protected VideoItemBinding createBinding(ViewGroup parent) {
        VideoItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.video_item,
                        parent, false, dataBindingComponent);
        binding.getRoot().setOnClickListener(v -> {
            Video video = binding.getVideo();

            videoClickCallback.onClick(video.getVideoUrl());
        });
        return binding;
    }

    @Override
    protected void bind(VideoItemBinding binding, Video item) {
        binding.setVideo(item);
    }

    @Override
    protected boolean areItemsTheSame(Video oldItem, Video newItem) {
        return Objects.equals(oldItem.getId(), newItem.getId());
    }

    @Override
    protected boolean areContentsTheSame(Video oldItem, Video newItem) {
        return oldItem.equals(newItem);
    }

    public interface VideoClickCallback {
        void onClick(String videoUrl);
    }
}
