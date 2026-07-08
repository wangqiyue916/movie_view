package com.example.movie.merchandise.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.movie.merchandise.entity.Merchandise;
import com.example.movie.merchandise.mapper.MerchandiseMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MerchandiseService {

    private final MerchandiseMapper merchandiseMapper;

    public MerchandiseService(MerchandiseMapper merchandiseMapper) {
        this.merchandiseMapper = merchandiseMapper;
    }

    public IPage<Merchandise> pageProducts(Integer page, Integer pageSize, Long movieId, String productType, String keyword) {
        LambdaQueryWrapper<Merchandise> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Merchandise::getStatus, "ONLINE");

        if (movieId != null) {
            wrapper.eq(Merchandise::getMovieId, movieId);
        }
        if (productType != null && !productType.isBlank()) {
            wrapper.eq(Merchandise::getProductType, productType);
        }
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(Merchandise::getName, keyword);
        }
        wrapper.orderByDesc(Merchandise::getCreatedAt);
        return merchandiseMapper.selectPage(new Page<>(page, pageSize), wrapper);
    }

    public Merchandise getProductById(Long id) {
        return merchandiseMapper.selectById(id);
    }

    public List<Merchandise> listByMovie(Long movieId) {
        LambdaQueryWrapper<Merchandise> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Merchandise::getStatus, "ONLINE")
                .eq(Merchandise::getMovieId, movieId)
                .last("LIMIT 6");
        return merchandiseMapper.selectList(wrapper);
    }

    public Merchandise createProduct(Merchandise m) {
        merchandiseMapper.insert(m);
        return m;
    }

    public Merchandise updateProduct(Merchandise m) {
        merchandiseMapper.updateById(m);
        return m;
    }

    public void deleteProduct(Long id) {
        merchandiseMapper.deleteById(id);
    }

    public void recordClick(Long id) {
        Merchandise m = merchandiseMapper.selectById(id);
        if (m != null) {
            m.setClickCount((m.getClickCount() == null ? 0L : m.getClickCount()) + 1);
            merchandiseMapper.updateById(m);
        }
    }
}
