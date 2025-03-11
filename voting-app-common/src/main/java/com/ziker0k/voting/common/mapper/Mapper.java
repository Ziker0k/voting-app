package com.ziker0k.voting.common.mapper;

public interface Mapper<F, T> {

    T map(F object);
}