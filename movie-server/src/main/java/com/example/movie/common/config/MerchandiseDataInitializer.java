package com.example.movie.common.config;

import com.example.movie.merchandise.entity.Merchandise;
import com.example.movie.merchandise.mapper.MerchandiseMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 启动时将全部20个商品写入云数据库。
 * 使用 JdbcTemplate 做物理删除（绕过 @TableLogic），再用 MyBatis-Plus 插入。
 */
@Component
public class MerchandiseDataInitializer implements CommandLineRunner {

    private final MerchandiseMapper mapper;
    private final JdbcTemplate jdbcTemplate;

    public MerchandiseDataInitializer(MerchandiseMapper mapper, JdbcTemplate jdbcTemplate) {
        this.mapper = mapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        try {
            Long count = mapper.selectCount(null);
            System.out.println("[MerchandiseInit] Current records: " + count);
            if (count != null && count >= 20) {
                System.out.println("[MerchandiseInit] Already has " + count + " records, skipping.");
                return;
            }

            // 物理删除所有旧数据（绕过 @TableLogic 的逻辑删除）
            int deleted = jdbcTemplate.update("DELETE FROM merchandise");
            System.out.println("[MerchandiseInit] Physically deleted " + deleted + " rows.");
            // 重置自增ID
            jdbcTemplate.update("ALTER TABLE merchandise AUTO_INCREMENT = 1");

            // 全部20个商品（不指定ID，由数据库自增）
            insert(2L, "流浪地球2鼠标垫", "数码", 3.00, "淘宝",
                "https://item.taobao.com/item.htm?ali_refid=a3_430673_1006%3A1613780005%3AH%3AOh18XfFayHNkRhT9xjRTkl9E3BEhcAfR%3Af59ff7ad021b802ccdc3d0e0aefb99b1&ali_trackid=318_f59ff7ad021b802ccdc3d0e0aefb99b1&id=698786584590&loginBonus=1&mi_id=0000wmLIcz6jfsga2V4D_N-iFT4ejU7AFJdF_-3W0a0o0f8&mm_sceneid=0_0_2430830031_0&priceTId=214780bf17834782223575470e0eaa&skuId=4947209101869&spm=a21n57.sem.item.5&utparam=%7B%22aplus_abtest%22%3A%22f8234a1e2672a0434575d29186ba1e08%22%7D&xxc=ad_ztc",
                "/merch-shubiao.png");
            insert(3L, "盗梦空间烟灰缸", "其他", 243.00, "淘宝",
                "https://item.taobao.com/item.htm?ali_refid=a3_430673_1006%3A1150766372%3AH%3ANeZZNP4J71P6xl%2BTGv9mDA%3D%3D%3A1fcb4ea9281317db7971821d504e5978&ali_trackid=282_1fcb4ea9281317db7971821d504e5978&id=1028630747640&loginBonus=1&mi_id=0000u4M9bjAnqEVI0peAgVVfydeHqE4Z3aYg4ML0fAFbQe0&mm_sceneid=1_0_45000339_0&priceTId=2147842717834799515015417e1127&skuId=6042704393330&spm=a21n57.sem.item.3&utparam=%7B%22aplus_abtest%22%3A%22a730259ab86fa579e7483c5e3df92fbb%22%7D&xxc=ad_ztc",
                "/merch-yanhuigang.png");
            insert(2L, "流浪地球2笨笨积木玩具", "模型", 599.00, "京东",
                "https://item.jd.com/10179164941794.html",
                "/merch-benben.png");
            insert(1L, "星际穿越塔斯机器人", "模型", 174.40, "京东",
                "https://item.jd.com/10219005177651.html",
                "/merch-tasi.png");
            insert(4L, "黑暗骑士合金战车", "模型", 148.00, "京东",
                "https://item.jd.com/10224017016041.html",
                "/merch-zhanchen.png");
            insert(5L, "蜘蛛侠2.0玩具手办", "模型", 183.33, "京东",
                "https://item.jd.com/100136091846.html",
                "/merch-zhizhu.png");
            insert(6L, "钢铁侠联名蓝牙耳机", "数码", 147.00, "淘宝",
                "https://item.taobao.com/item.htm?id=1058556026841",
                "/merch-erji.png");
            insert(6L, "钢铁侠玩具男孩手办", "模型", 368.00, "淘宝",
                "https://item.taobao.com/item.htm?id=679221673041",
                "/merch-gtxwj.png");
            insert(1L, "星际穿越主题海报", "海报", 39.90, "淘宝",
                "https://item.taobao.com/item.htm?id=698786584590",
                "/merch-shubiao.png");
            insert(3L, "盗梦空间陀螺摆件", "模型", 89.00, "淘宝",
                "https://item.taobao.com/item.htm?id=1028630747640",
                "/merch-yanhuigang.png");
            insert(4L, "暗夜骑士蝙蝠侠T恤", "服饰", 128.00, "京东",
                "https://item.jd.com/10224017016041.html",
                "/merch-zhanchen.png");
            insert(5L, "蜘蛛侠主题连帽卫衣", "服饰", 199.00, "京东",
                "https://item.jd.com/100136091846.html",
                "/merch-zhizhu.png");
            insert(101L, "肖申克的救赎明信片", "文创", 8.25, "淘宝",
                "https://item.taobao.com/item.htm?ali_refid=a3_430673_1006%3A1104093514%3AH%3AWppzSg1hJayu1DSs3BSlhrlnUEOOtfJP%3A08b3b7b61d990e71f8b8bf4547b90e6d&ali_trackid=282_08b3b7b61d990e71f8b8bf4547b90e6d&id=719169398123&loginBonus=1&mi_id=0000gJCXYL20RuPAATiSDZAd6qbZLzxZFcnpNbeoyhXRvIw&mm_sceneid=1_0_30632315_0&priceTId=2150418617835836059424565e0ed4&skuId=5187494635559&spm=a21n57.sem.item.1&utparam=%7B%22aplus_abtest%22%3A%2233312df83cf801ef507fff0c81351ed4%22%7D&xxc=ad_ztc",
                "/merch-mingxinpian.png");
            insert(101L, "肖申克的救赎吊坠", "其他", 8.71, "淘宝",
                "https://item.taobao.com/item.htm?abbucket=18&id=765065047281&loginBonus=1&mi_id=0000jWgtQ9HviE7DHII2EUr-O2lYP1cHt46v7GraH6HaJxo&ns=1&priceTId=2150418617835836059424565e0ed4&skuId=5434545031310&spm=a21n57.sem.item.4.265e3903HkAe1a&utparam=%7B%22aplus_abtest%22%3A%220df7748d1a8759602e46343b905afb25%22%7D&xxc=taobaoSearch",
                "/merch-diaozhui.png");
            insert(102L, "霸王别姬电影海报", "海报", 17.90, "京东",
                "https://item.jd.com/10191440135739.html",
                "/merch-bawanghaibao.png");
            insert(102L, "霸王别姬海报短袖", "服饰", 49.00, "京东",
                "https://item.jd.com/10176935020247.html",
                "/merch-bawangduanxiu.png");
            insert(103L, "泰坦尼克号模型豪华轮船", "模型", 187.42, "淘宝",
                "https://detail.tmall.com/item.htm?id=14682138762",
                "/merch-titanicmoxing.png");
            insert(103L, "泰坦尼克号流体漂流瓶", "其他", 13.50, "淘宝",
                "https://item.taobao.com/item.htm?id=899809170522",
                "/merch-piaoliuping.png");
            insert(104L, "宫崎骏动画绘本合集", "文创", 24.00, "京东",
                "https://item.jd.com/10224196787296.html",
                "/merch-gongqijunhuiben.png");
            insert(104L, "宫崎骏龙猫礼盒手办模型", "模型", 867.00, "京东",
                "https://item.jd.com/10189696229614.html",
                "/merch-longmaoshouban.png");

            // 验证插入结果
            Long finalCount = mapper.selectCount(null);
            System.out.println("[MerchandiseInit] Final count after insert: " + finalCount);
            if (finalCount == null || finalCount < 20) {
                System.err.println("[MerchandiseInit] WARNING: Expected 20 records but got " + finalCount);
            }
        } catch (Exception e) {
            System.err.println("[MerchandiseInit] Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void insert(Long movieId, String name, String productType,
                        double price, String platform, String externalUrl, String imageUrl) {
        Merchandise m = new Merchandise();
        // 不设置 id，让数据库 AUTO_INCREMENT 自动生成
        m.setMovieId(movieId);
        m.setName(name);
        m.setProductType(productType);
        m.setPrice(new BigDecimal(String.valueOf(price)));
        m.setPlatform(platform);
        m.setExternalUrl(externalUrl);
        m.setClickCount(0L);
        m.setStatus("ONLINE");
        m.setImageUrl(imageUrl);
        mapper.insert(m);
    }
}
