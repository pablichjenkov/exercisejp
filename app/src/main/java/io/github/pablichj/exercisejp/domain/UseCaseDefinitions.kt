package io.github.pablichj.exercisejp.domain

import kotlinx.coroutines.flow.Flow

interface MapUseCase<I, O> {
    fun execute(input: I): Flow<O>
}

interface SourceUseCase<O> {
    fun execute(): Flow<O>
}

interface MapOneUseCase<I, O> {
    suspend fun execute(input: I): O
}

interface SourceOneUseCase<I, O> {
    suspend fun execute(): O
}

sealed class UseCaseResult<out T> {
    class Success<U>(val value: U):UseCaseResult<U>()
    class Error(val error: String = "no error info"):UseCaseResult<Nothing>()
}
