package com.hashsoft.audiotape.logic

/**
 * Composeのrememberの中で、プリミティブ型を参照で保持するためのクラス。
 *
 * @param T 保持する値の型
 * @property value 保持する値
 */
class RefValue<T>(var value: T)
