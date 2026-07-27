package com.findyourpet.app.data.remote

data class BackendSyncState<T>(
    val data: T,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isFromCache: Boolean = false,
    val hasPendingWrites: Boolean = false,
    val isRemoteBackend: Boolean = true
) {
    val hasError: Boolean get() = errorMessage != null

    companion object {
        fun <T> loading(data: T, isRemoteBackend: Boolean = true): BackendSyncState<T> =
            BackendSyncState(data = data, isLoading = true, isRemoteBackend = isRemoteBackend)

        fun <T> data(
            data: T,
            isFromCache: Boolean,
            hasPendingWrites: Boolean,
            isRemoteBackend: Boolean = true
        ): BackendSyncState<T> =
            BackendSyncState(
                data = data,
                isFromCache = isFromCache,
                hasPendingWrites = hasPendingWrites,
                isRemoteBackend = isRemoteBackend
            )

        fun <T> error(
            data: T,
            message: String,
            isFromCache: Boolean = false,
            hasPendingWrites: Boolean = false,
            isRemoteBackend: Boolean = true
        ): BackendSyncState<T> =
            BackendSyncState(
                data = data,
                errorMessage = message,
                isFromCache = isFromCache,
                hasPendingWrites = hasPendingWrites,
                isRemoteBackend = isRemoteBackend
            )
    }
}
