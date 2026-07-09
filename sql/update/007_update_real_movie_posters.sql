SET NAMES utf8mb4;
SET time_zone = '+08:00';
USE `movie`;

START TRANSACTION;

-- 使用 IMDb 图片服务返回的对应电影真实海报链接，仅保存外链，不下载图片文件。
CREATE TEMPORARY TABLE `tmp_movie_posters` (
  movie_id BIGINT PRIMARY KEY,
  poster_url VARCHAR(500) NOT NULL
);

INSERT INTO `tmp_movie_posters` (movie_id, poster_url) VALUES
(1, 'https://m.media-amazon.com/images/M/MV5BYzdjMDAxZGItMjI2My00ODA1LTlkNzItOWFjMDU5ZDJlYWY3XkEyXkFqcGc@._V1_.jpg'),
(2, 'https://m.media-amazon.com/images/M/MV5BMzg5MTNkZGUtNGViNS00NzFlLTg1NDYtN2Y2NzY3NWNmNzg2XkEyXkFqcGc@._V1_.jpg'),
(3, 'https://m.media-amazon.com/images/M/MV5BMjAxMzY3NjcxNF5BMl5BanBnXkFtZTcwNTI5OTM0Mw@@._V1_.jpg'),
(101, 'https://m.media-amazon.com/images/M/MV5BMDAyY2FhYjctNDc5OS00MDNlLThiMGUtY2UxYWVkNGY2ZjljXkEyXkFqcGc@._V1_.jpg'),
(102, 'https://m.media-amazon.com/images/M/MV5BYjMzZjcwNzItNTYwZC00NzAxLWE3MmMtYmU3ZjdkMWMxNDMwXkEyXkFqcGc@._V1_.jpg'),
(103, 'https://m.media-amazon.com/images/M/MV5BYzYyN2FiZmUtYWYzMy00MzViLWJkZTMtOGY1ZjgzNWMwN2YxXkEyXkFqcGc@._V1_.jpg'),
(104, 'https://m.media-amazon.com/images/M/MV5BNTEyNmEwOWUtYzkyOC00ZTQ4LTllZmUtMjk0Y2YwOGUzYjRiXkEyXkFqcGc@._V1_.jpg'),
(105, 'https://m.media-amazon.com/images/M/MV5BNGRkYTNhOWQtYmI0Ni00MjZhLWJmMzAtMTA2Mjg4NGNiNDU0XkEyXkFqcGc@._V1_.jpg'),
(106, 'https://m.media-amazon.com/images/M/MV5BNDYwNzVjMTItZmU5YS00YjQ5LTljYjgtMjY2NDVmYWMyNWFmXkEyXkFqcGc@._V1_.jpg'),
(107, 'https://m.media-amazon.com/images/M/MV5BNzA3ZjZlNzYtMTdjMy00NjMzLTk5ZGYtMTkyYzNiOGM1YmM3XkEyXkFqcGc@._V1_.jpg'),
(108, 'https://m.media-amazon.com/images/M/MV5BOTMyMjEyNzIzMV5BMl5BanBnXkFtZTgwNzIyNjU0NzE@._V1_.jpg'),
(109, 'https://m.media-amazon.com/images/M/MV5BYjk1Y2U4MjQtY2ZiNS00OWQyLWI3MmYtZWUwNmRjYWRiNWNhXkEyXkFqcGc@._V1_.jpg'),
(110, 'https://m.media-amazon.com/images/M/MV5BMjExMTg5OTU0NF5BMl5BanBnXkFtZTcwMjMxMzMzMw@@._V1_.jpg'),
(111, 'https://m.media-amazon.com/images/M/MV5BMjI2NDQ5Mzg0NF5BMl5BanBnXkFtZTgwMDM3NjI2MDE@._V1_.jpg'),
(112, 'https://m.media-amazon.com/images/M/MV5BNDg0ZTU4YTItZTI3My00ODNjLTllMTYtMzI3ZWNjYjU1NGM1XkEyXkFqcGc@._V1_.jpg');

UPDATE `movies` m
JOIN `tmp_movie_posters` p ON p.movie_id = m.id
SET m.poster_url = p.poster_url,
    m.updated_at = CURRENT_TIMESTAMP;

UPDATE `long_reviews` lr
JOIN `tmp_movie_posters` p ON p.movie_id = lr.movie_id
SET lr.cover_url = p.poster_url,
    lr.updated_at = CURRENT_TIMESTAMP
WHERE lr.id BETWEEN 20000 AND 29999;

UPDATE `review_images` ri
JOIN `long_reviews` lr ON lr.id = ri.review_id
JOIN `tmp_movie_posters` p ON p.movie_id = lr.movie_id
SET ri.image_url = p.poster_url
WHERE ri.review_id BETWEEN 20000 AND 29999;

DROP TEMPORARY TABLE `tmp_movie_posters`;

COMMIT;
