package com.example.movie.merchandise.controller;

import com.example.movie.common.response.ApiResponse;
import com.example.movie.common.response.PageResult;
import com.example.movie.merchandise.entity.Merchandise;
import com.example.movie.merchandise.service.MerchandiseService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/merchandise")
public class MerchandiseController {

    private final MerchandiseService merchandiseService;

    public MerchandiseController(MerchandiseService merchandiseService) {
        this.merchandiseService = merchandiseService;
    }

    @GetMapping
    public ApiResponse<PageResult<Merchandise>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long movieId,
            @RequestParam(required = false) String productType,
            @RequestParam(required = false) String keyword
    ) {
        IPage<Merchandise> result = merchandiseService.pageProducts(page, pageSize, movieId, productType, keyword);
        List<Merchandise> records = result.getRecords();

        return ApiResponse.success(new PageResult<>(
                records,
                (int) result.getCurrent(),
                (int) result.getSize(),
                result.getTotal()
        ));
    }

    @GetMapping("/{id}")
    public ApiResponse<Merchandise> detail(@PathVariable Long id) {
        Merchandise m = merchandiseService.getProductById(id);
        if (m == null) {
            return ApiResponse.error(404, "商品不存在");
        }
        merchandiseService.recordClick(id);
        m.setClickCount((m.getClickCount() == null ? 0L : m.getClickCount()) + 1);
        return ApiResponse.success(m);
    }

    @GetMapping("/by-movie/{movieId}")
    public ApiResponse<List<Merchandise>> listByMovie(@PathVariable Long movieId) {
        List<Merchandise> list = merchandiseService.listByMovie(movieId);
        return ApiResponse.success(list);
    }
}
