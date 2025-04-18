package com.example.bot_builder.domain.model.mapper;

import java.util.List;

public interface DataMapper<E, D> {
    E dtoToEntity(D data);

    List<E> dtoToEntity(List<D> dataList);

    D entityToDto(E entity);

    List<D> entityToDto(List<E> entity);
}