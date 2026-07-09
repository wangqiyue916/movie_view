package com.example.movie.video.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.movie.video.entity.InterpretationVideo;
import com.example.movie.video.mapper.InterpretationVideoMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideoService {

    private final InterpretationVideoMapper interpretationVideoMapper;

    public VideoService(InterpretationVideoMapper interpretationVideoMapper) {
        this.interpretationVideoMapper = interpretationVideoMapper;
    }

    public List<InterpretationVideo> getVideosByMovie(Long movieId) {
        LambdaQueryWrapper<InterpretationVideo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InterpretationVideo::getMovieId, movieId)
                .eq(InterpretationVideo::getStatus, "ONLINE")
                .orderByDesc(InterpretationVideo::getHeatScore);
        return interpretationVideoMapper.selectList(wrapper);
    }

    public InterpretationVideo getVideoById(Long id) {
        return interpretationVideoMapper.selectById(id);
    }

    public InterpretationVideo createVideo(InterpretationVideo video) {
        interpretationVideoMapper.insert(video);
        return video;
    }

    public InterpretationVideo updateVideo(InterpretationVideo video) {
        interpretationVideoMapper.updateById(video);
        return video;
    }

    public void deleteVideo(Long id) {
        interpretationVideoMapper.deleteById(id);
    }

    public void recordClick(Long videoId) {
        InterpretationVideo video = interpretationVideoMapper.selectById(videoId);
        if (video != null) {
            video.setClickCount((video.getClickCount() == null ? 0L : video.getClickCount()) + 1);
            interpretationVideoMapper.updateById(video);
        }
    }

    public IPage<InterpretationVideo> pageVideos(Integer page, Integer pageSize, String status, String keyword) {
        LambdaQueryWrapper<InterpretationVideo> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isBlank()) {
            wrapper.eq(InterpretationVideo::getStatus, status);
        }
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(InterpretationVideo::getTitle, keyword);
        }
        wrapper.orderByDesc(InterpretationVideo::getCreatedAt);
        return interpretationVideoMapper.selectPage(new Page<>(page, pageSize), wrapper);
    }
}
