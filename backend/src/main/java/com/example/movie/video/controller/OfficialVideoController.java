package com.example.movie.video.controller;

import com.example.movie.common.config.LoginUserContext;
import com.example.movie.common.response.ApiResponse;
import com.example.movie.video.entity.InterpretationVideo;
import com.example.movie.video.service.VideoService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/official/videos")
public class OfficialVideoController {

    private final VideoService videoService;

    public OfficialVideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @PostMapping
    public ApiResponse<InterpretationVideo> submit(@RequestBody Map<String, Object> body) {
        InterpretationVideo video = new InterpretationVideo();
        video.setMovieId(body.get("movieId") != null ? ((Number) body.get("movieId")).longValue() : null);
        video.setTitle((String) body.get("title"));
        video.setCoverUrl((String) body.get("coverUrl"));
        video.setPlatform((String) body.get("platform"));
        video.setExternalUrl((String) body.get("externalUrl"));
        video.setDescription((String) body.get("description"));
        video.setStatus("PENDING");
        video.setHeatScore(0);
        video.setClickCount(0L);

        Long userId = LoginUserContext.getUserId();
        if (userId != null) {
            video.setSubmitterId(userId);
        }

        return ApiResponse.success(videoService.createVideo(video));
    }
}
