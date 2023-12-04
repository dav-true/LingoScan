package com.lingoscan.utils


fun Boolean?.otherwiseFalse(): Boolean {
    return this ?: false
}