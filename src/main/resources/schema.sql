drop table if exists
    ratings,
    genres,
    films,
    users,
    film_genres,
    likes,
    friends,
    directors,
    film_directors,
    reviews,
    grades_reviews;


CREATE TABLE IF NOT EXISTS ratings
(
    rating_id
    INTEGER
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    rating_name
    VARCHAR
(
    10
) NOT NULL
    );

CREATE TABLE IF NOT EXISTS genres
(
    genre_id
    INTEGER
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    name
    VARCHAR
(
    50
) NOT NULL
    );

CREATE TABLE IF NOT EXISTS directors
(
    id
    INTEGER
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    name
    VARCHAR
(
    50
) NOT NULL
    );

CREATE TABLE IF NOT EXISTS films
(
    film_id
    INTEGER
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    name
    VARCHAR
(
    100
) NOT NULL,
    description VARCHAR
(
    200
),
    release_date DATE NOT NULL,
    duration INTEGER NOT NULL,
    rating_id INTEGER REFERENCES ratings
(
    rating_id
)
    );

CREATE TABLE IF NOT EXISTS film_directors
(
    film_id
    INTEGER
    NOT
    NULL,
    director_id
    INTEGER
    NOT
    NULL,
    PRIMARY
    KEY
(
    film_id,
    director_id
),
    FOREIGN KEY
(
    film_id
) REFERENCES films
(
    film_id
) ON DELETE RESTRICT,
    FOREIGN KEY
(
    director_id
) REFERENCES directors
(
    id
)
  ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS users
(
    user_id
    INTEGER
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    name
    VARCHAR
(
    100
) NOT NULL,
    login VARCHAR
(
    50
) NOT NULL,
    email VARCHAR
(
    100
) NOT NULL,
    birthday DATE NOT NULL
    );


CREATE TABLE IF NOT EXISTS film_genres
(
    film_id
    INTEGER
    NOT
    NULL,
    genre_id
    INTEGER
    NOT
    NULL,
    PRIMARY
    KEY
(
    film_id,
    genre_id
),
    FOREIGN KEY
(
    film_id
) REFERENCES films
(
    film_id
) ON DELETE RESTRICT,
    FOREIGN KEY
(
    genre_id
) REFERENCES genres
(
    genre_id
)
  ON DELETE RESTRICT
    );


CREATE TABLE IF NOT EXISTS likes
(
    user_id
    INTEGER
    NOT
    NULL,
    film_id
    INTEGER
    NOT
    NULL,
    PRIMARY
    KEY
(
    user_id,
    film_id
),
    FOREIGN KEY
(
    user_id
) REFERENCES users
(
    user_id
) ON DELETE CASCADE,
    FOREIGN KEY
(
    film_id
) REFERENCES films
(
    film_id
)
  ON DELETE CASCADE
    );


CREATE TABLE IF NOT EXISTS friends
(
    user_id
    INTEGER
    NOT
    NULL,
    friend_id
    INTEGER
    NOT
    NULL,
    FOREIGN
    KEY
(
    user_id
) REFERENCES users
(
    user_id
) ON DELETE CASCADE
    );

    CREATE TABLE IF NOT EXISTS reviews
    (
        review_id
        BIGINT
        GENERATED
        BY
        DEFAULT AS
        IDENTITY
        PRIMARY
        KEY,
        content
        VARCHAR(255)
        NOT NULL,
        is_positive
        BOOLEAN
        NOT NULL,
        user_id
        BIGINT
        NOT NULL,
        film_id
        BIGINT
        NOT NULL,
        useful
        BIGINT
        DEFAULT 0
        );

    CREATE TABLE IF NOT EXISTS grades_reviews
         (
         id
         BIGINT
         GENERATED
         BY
         DEFAULT AS
         IDENTITY
         PRIMARY
         KEY,
         user_id
         BIGINT
         NOT NULL,
         grade
         VARCHAR(10)
         NOT NULL,
         review_id
         BIGINT
         NOT NULL
         );



