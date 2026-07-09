package com.example.movie.video.controller;

import com.example.movie.common.response.ApiResponse;
import com.example.movie.video.entity.InterpretationVideo;
import com.example.movie.video.service.VideoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @GetMapping
    public ApiResponse<List<InterpretationVideo>> list(@RequestParam(required = false) Long movieId) {
        List<InterpretationVideo> videos = null;
        if (movieId != null) {
            videos = videoService.getVideosByMovie(movieId);
        }
        if (videos == null || videos.isEmpty()) {
            videos = buildMockVideos(movieId);
        }
        return ApiResponse.success(videos);
    }

    @GetMapping("/movie/{movieId}")
    public ApiResponse<List<InterpretationVideo>> listByMovie(@PathVariable Long movieId) {
        return list(movieId);
    }

    @GetMapping("/{id}")
    public ApiResponse<InterpretationVideo> detail(@PathVariable Long id) {
        InterpretationVideo video = videoService.getVideoById(id);
        if (video == null) {
            List<InterpretationVideo> mockList = buildAllMockVideos();
            if (id >= 1 && id <= mockList.size()) {
                InterpretationVideo mock = mockList.get((int) (id - 1));
                mock.setClickCount((mock.getClickCount() == null ? 0L : mock.getClickCount()) + 1);
                return ApiResponse.success(mock);
            }
            return ApiResponse.error(404, "视频不存在");
        }
        videoService.recordClick(id);
        video.setClickCount((video.getClickCount() == null ? 0L : video.getClickCount()) + 1);
        return ApiResponse.success(video);
    }

    @PostMapping("/{id}/click")
    public ApiResponse<Void> recordClick(@PathVariable Long id) {
        videoService.recordClick(id);
        return ApiResponse.success();
    }

    private List<InterpretationVideo> buildMockVideos(Long movieId) {
        List<InterpretationVideo> all = buildAllMockVideos();
        if (movieId == null) {
            return all;
        }
        return all.stream().filter(v -> movieId.equals(v.getMovieId())).toList();
    }

    private List<InterpretationVideo> buildAllMockVideos() {
        List<InterpretationVideo> list = new ArrayList<>();

        InterpretationVideo v1 = new InterpretationVideo();
        v1.setId(1L);
        v1.setMovieId(1L);
        v1.setTitle("星际穿越深度解读：时间与爱的终极命题");
        v1.setPlatform("Bilibili");
        v1.setHeatScore(1500);
        v1.setClickCount(0L);
        v1.setStatus("ONLINE");
        v1.setDescription("深入解读诺兰科幻巨制，探讨时间、引力与爱的终极命题。");
        v1.setCoverUrl("https://images.unsplash.com/photo-1446776811953-b23d57bd21aa?auto=format&fit=crop&w=900&q=85");
        list.add(v1);

        InterpretationVideo v2 = new InterpretationVideo();
        v2.setId(2L);
        v2.setMovieId(1L);
        v2.setTitle("《星际穿越》科学顾问谈黑洞与五维空间");
        v2.setPlatform("YouTube");
        v2.setHeatScore(1200);
        v2.setClickCount(0L);
        v2.setStatus("ONLINE");
        v2.setDescription("物理学家基普·索恩谈电影中的黑洞、引力透镜与五维空间理论。");
        v2.setCoverUrl("https://images.unsplash.com/photo-1462331940025-496dfbfc7564?auto=format&fit=crop&w=900&q=85");
        list.add(v2);

        InterpretationVideo v3 = new InterpretationVideo();
        v3.setId(3L);
        v3.setMovieId(1L);
        v3.setTitle("诺兰电影宇宙：从盗梦空间到星际穿越");
        v3.setPlatform("Bilibili");
        v3.setHeatScore(980);
        v3.setClickCount(0L);
        v3.setStatus("ONLINE");
        v3.setDescription("回顾诺兰导演的电影宇宙，从盗梦空间到星际穿越的叙事风格演变。");
        v3.setCoverUrl("https://images.unsplash.com/photo-1536440136628-849c177e76a1?auto=format&fit=crop&w=900&q=85");
        list.add(v3);

        InterpretationVideo v4 = new InterpretationVideo();
        v4.setId(4L);
        v4.setMovieId(2L);
        v4.setTitle("盗梦空间结局解析：陀螺到底停没停");
        v4.setPlatform("YouTube");
        v4.setHeatScore(2000);
        v4.setClickCount(0L);
        v4.setStatus("ONLINE");
        v4.setDescription("深度分析盗梦空间开放式结局，陀螺到底有没有停止旋转？");
        v4.setCoverUrl("https://images.unsplash.com/photo-1485846234645-a62644f84728?auto=format&fit=crop&w=900&q=85");
        list.add(v4);

        InterpretationVideo v5 = new InterpretationVideo();
        v5.setId(5L);
        v5.setMovieId(2L);
        v5.setTitle("盗梦空间的多层梦境结构全解析");
        v5.setPlatform("Bilibili");
        v5.setHeatScore(1600);
        v5.setClickCount(0L);
        v5.setStatus("ONLINE");
        v5.setDescription("逐层解析盗梦空间的多重梦境结构，理解诺兰的精妙叙事。");
        v5.setCoverUrl("https://images.unsplash.com/photo-1500462918059-b1a0cb512f1d?auto=format&fit=crop&w=900&q=85");
        list.add(v5);

        return list;
    }
}
