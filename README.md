# java-filmorate
## Database schema

![Database schema](/docs/db.png)


В базе данных реализована модель пользователей, фильмов и системы дружбы.

users - пользователи
friendship - связь дружбы между пользователями
film - фильмы
genre/film_genre - жанры фильмов (many-to-many)
mpa - возрастной рейтинг фильма
film_like - лайки пользователей фильмам

Дружба хранится одной записью на пару пользователей
requester_id - кто инициировал
status:
    PENDING - заявка отправлена
    CONFIRMED - заявка подтверждена

Все связи между таблицами реализованы через внешние ключи с каскадным удалением,
что обеспечивает целостность данных.

### Примеры базовых запросов

Добавление пользователя:
```sql
INSERT INTO users (name, login, email, birthday)
VALUES ('Test', 'login', 'test@mail.com', '1990-01-01');
```

Добавление фильма:
```sql
INSERT INTO film (name, description, mpa_id, release_date, duration)
VALUES ('Film', 'Film', 1, '2010-11-31', 136);
```

Получение всех фильмов (Без жанров):
```sql
SELECT f.id, f.name, f.description, f.release_date, f.duration, m.name AS mpa
FROM film f
JOIN mpa m ON f.mpa_id = m.id
ORDER BY f.id;
```

Добавление лайка:
```sql
INSERT INTO film_like (film_id, user_id)
VALUES (1, 1);
```

Удаление лайка:
```sql
DELETE FROM film_like
WHERE film_id = 1 AND user_id = 1;
```

Добавление в друзья:
```sql
INSERT INTO friendship (user1_id, user2_id, requester_id, status)
VALUES (1, 2, 1, 'PENDING');
```