<template>
  <div class="merchandise-page">
    <section class="merch-hero">
      <div>
        <p class="eyebrow">MOVIE GOODS</p>
        <h1>电影周边商城</h1>
        <p class="hero-copy">
          展示电影海报、手办、服饰、徽章、收藏品等周边信息，后续点击商品将跳转到第三方电商购买页面。
        </p>
      </div>
    </section>

    <section class="coming-panel">
      <div class="panel-heading">
        <span></span>
        <h2>周边商品</h2>
      </div>
      <div class="placeholder-grid">
        <article v-for="item in placeholders" :key="item.title" class="placeholder-card">
          <div class="placeholder-image"></div>
          <div>
            <strong>{{ item.title }}</strong>
            <p>{{ item.desc }}</p>
          </div>
        </article>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
const placeholders = [
  { title: '主题海报', desc: '按电影关联展示海报、明信片与收藏画册。' },
  { title: '角色手办', desc: '展示商品图片、价格、类型和外部购买链接。' },
  { title: '服饰徽章', desc: '支持按电影、商品类型、价格区间筛选。' },
]
</script>

<style scoped>
.merchandise-page {
  min-height: calc(100vh - 64px);
  padding: 44px max(22px, calc((100vw - 1280px) / 2)) 78px;
  color: #f7edd5;
}

.merch-hero {
  display: flex;
  align-items: center;
  min-height: 260px;
  padding: 42px;
  border: 1px solid rgb(214 176 95 / 22%);
  border-radius: 8px;
  background:
    linear-gradient(90deg, rgb(0 0 0 / 72%), rgb(0 0 0 / 20%)),
    url('https://images.unsplash.com/photo-1460881680858-30d872d5b530?auto=format&fit=crop&w=1600&q=85') center / cover;
  box-shadow: 0 22px 64px rgb(0 0 0 / 42%);
}

.eyebrow {
  margin: 0 0 10px;
  color: #e8c16d;
  font-size: 13px;
  letter-spacing: 0;
}

.merch-hero h1 {
  margin: 0;
  color: #fff8e6;
  font-family: "Noto Serif SC", "Songti SC", SimSun, serif;
  font-size: clamp(34px, 5vw, 58px);
  font-weight: 800;
  text-shadow: 0 0 20px rgb(214 176 95 / 30%);
}

.hero-copy {
  max-width: 620px;
  margin: 16px 0 0;
  color: #d8c69b;
  font-size: 16px;
  line-height: 1.8;
}

.coming-panel {
  margin-top: 34px;
}

.panel-heading {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 18px;
}

.panel-heading span {
  width: 36px;
  height: 1px;
  background: #d6b05f;
  box-shadow: 0 0 12px rgb(214 176 95 / 72%);
}

.panel-heading h2 {
  margin: 0;
  color: #e8c16d;
  font-family: "Noto Serif SC", "Songti SC", SimSun, serif;
  font-size: 28px;
}

.placeholder-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.placeholder-card {
  min-height: 210px;
  overflow: hidden;
  border: 1px solid rgb(214 176 95 / 18%);
  border-radius: 8px;
  background: linear-gradient(180deg, rgb(255 255 255 / 6%), rgb(255 255 255 / 2%));
  box-shadow: 0 12px 28px rgb(0 0 0 / 34%);
}

.placeholder-image {
  height: 120px;
  background:
    linear-gradient(135deg, rgb(214 176 95 / 22%), transparent),
    linear-gradient(180deg, #201a0f, #0b0b0b);
}

.placeholder-card div:last-child {
  padding: 14px 16px 18px;
}

.placeholder-card strong {
  color: #fff8e6;
  font-size: 17px;
}

.placeholder-card p {
  margin: 8px 0 0;
  color: #b9ab90;
  font-size: 13px;
  line-height: 1.7;
}

@media (max-width: 820px) {
  .merch-hero {
    padding: 30px 24px;
  }

  .placeholder-grid {
    grid-template-columns: 1fr;
  }
}
</style>
