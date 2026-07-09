package com.example.movie.video.controller;

import com.example.movie.common.response.ApiResponse;
import com.example.movie.common.response.PageResult;
import com.example.movie.video.entity.InterpretationVideo;
import com.example.movie.video.service.VideoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/videos")
public class AdminVideoController {

    private final VideoService videoService;

    public AdminVideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @GetMapping
    public ApiResponse<PageResult<InterpretationVideo>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword
    ) {
        IPage<InterpretationVideo> result = videoService.pageVideos(page, pageSize, status, keyword);
        List<InterpretationVideo> records = result.getRecords();

        if (records.isEmpty()) {
            records = buildMockAdminVideoList();
            if (keyword != null && !keyword.isBlank()) {
                records = records.stream()
                        .filter(v -> v.getTitle().contains(keyword))
                        .toList();
            }
            if (status != null && !status.isBlank()) {
                records = records.stream()
                        .filter(v -> status.equals(v.getStatus()))
                        .toList();
            }
        }

        return ApiResponse.success(new PageResult<>(
                records,
                (int) result.getCurrent(),
                (int) result.getSize(),
                (long) records.size()
        ));
    }

    @PutMapping("/{id}/approve")
    public ApiResponse<InterpretationVideo> approve(@PathVariable Long id) {
        InterpretationVideo video = new InterpretationVideo();
        video.setId(id);
        video.setStatus("APPROVED");
        return ApiResponse.success(videoService.updateVideo(video));
    }

    @PutMapping("/{id}/reject")
    public ApiResponse<InterpretationVideo> reject(@PathVariable Long id, @RequestBody Map<String, String> body) {
        InterpretationVideo video = new InterpretationVideo();
        video.setId(id);
        video.setStatus("REJECTED");
        return ApiResponse.success(videoService.updateVideo(video));
    }

    @PutMapping("/{id}/publish")
    public ApiResponse<InterpretationVideo> publish(@PathVariable Long id) {
        InterpretationVideo video = new InterpretationVideo();
        video.setId(id);
        video.setStatus("ONLINE");
        return ApiResponse.success(videoService.updateVideo(video));
    }

    @PutMapping("/{id}/offline")
    public ApiResponse<InterpretationVideo> offline(@PathVariable Long id, @RequestBody Map<String, String> body) {
        InterpretationVideo video = new InterpretationVideo();
        video.setId(id);
        video.setStatus("OFFLINE");
        return ApiResponse.success(videoService.updateVideo(video));
    }

    private List<InterpretationVideo> buildMockAdminVideoList() {
        List<InterpretationVideo> list = new ArrayList<>();

        InterpretationVideo v1 = new InterpretationVideo();
        v1.setId(1L);
        v1.setMovieId(1L);
        v1.setTitle("星际穿越深度解读：时间与爱的终极命题");
        v1.setPlatform("Bilibili");
        v1.setHeatScore(1500);
        v1.setClickCount(3200L);
        v1.setStatus("ONLINE");
        v1.setDescription("深入解读诺兰科幻巨制。");
        v1.setCoverUrl("https://images.unsplash.com/photo-1446776811953-b23d57bd21aa?auto=format&fit=crop&w=900&q=85");
        v1.setCreatedAt(LocalDateTime.now().minusDays(30));
        list.add(v1);

        InterpretationVideo v2 = new InterpretationVideo();
        v2.setId(2L);
        v2.setMovieId(2L);
        v2.setTitle("盗梦空间结局解析：陀螺到底停没停");
        v2.setPlatform("YouTube");
        v2.setHeatScore(2000);
        v2.setClickCount(5800L);
        v2.setStatus("ONLINE");
        v2.setDescription("深度分析盗梦空间开放式结局。");
        v2.setCoverUrl("https://images.unsplash.com/photo-1485846234645-a62644f84728?auto=format&fit=crop&w=900&q=85");
        v2.setCreatedAt(LocalDateTime.now().minusDays(20));
        list.add(v2);

        InterpretationVideo v3 = new InterpretationVideo();
        v3.setId(3L);
        v3.setMovieId(3L);
        v3.setTitle("流浪地球2视效解析");
        v3.setPlatform("Bilibili");
        v3.setHeatScore(1800);
        v3.setClickCount(4200L);
        v3.setStatus("PENDING");
        v3.setDescription("解析流浪地球2的视觉特效制作。");
        v3.setCoverUrl("https://images.unsplash.com/photo-1536440136628-849c177e76a1?auto=format&fit=crop&w=900&q=85");
        v3.setCreatedAt(LocalDateTime.now().minusDays(5));
        list.add(v3);

        InterpretationVideo v4 = new InterpretationVideo();
        v4.setId(4L);
        v4.setMovieId(4L);
        v4.setTitle("暗夜骑士：蝙蝠侠的哲学思考");
        v4.setPlatform("YouTube");
        v4.setHeatScore(1100);
        v4.setClickCount(2600L);
        v4.setStatus("REJECTED");
        v4.setDescription("探讨暗夜骑士中的哲学主题。");
        v4.setCoverUrl("https://images.unsplash.com/photo-1509347528160-9a9e33742cdb?auto=format&fit=crop&w=900&q=85");
        v4.setCreatedAt(LocalDateTime.now().minusDays(10));
        list.add(v4);

        InterpretationVideo v5 = new InterpretationVideo();
        v5.setId(5L);
        v5.setMovieId(5L);
        v5.setTitle("肖申克的救赎：自由的真谛");
        v5.setPlatform("Bilibili");
        v5.setHeatScore(900);
        v5.setClickCount(1900L);
        v5.setStatus("OFFLINE");
        v5.setDescription("解读肖申克的救赎中的自由主题。");
        v5.setCoverUrl("https://images.unsplash.com/photo-1524985069026-dd778a71c7b4?auto=format&fit=crop&w=900&q=85");
        v5.setCreatedAt(LocalDateTime.now().minusDays(15));
        list.add(v5);

        return list;
    }
}
