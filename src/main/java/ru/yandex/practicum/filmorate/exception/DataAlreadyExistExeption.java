package ru.yandex.practicum.filmorate.exception;

public class DataAlreadyExistExeption extends RuntimeException {

    public DataAlreadyExistExeption(String message) {
        super(message);
    }
}
