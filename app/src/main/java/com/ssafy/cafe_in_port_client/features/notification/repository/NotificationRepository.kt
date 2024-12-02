package com.ssafy.cafe_in_port_client.features.notification.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ssafy.cafe_in_port_client.data.model.dto.NotificationItem

object NotificationRepository {
    private val _notifications = MutableLiveData<MutableList<NotificationItem>>()
    val notifications: LiveData<MutableList<NotificationItem>>
        get() = _notifications

    fun addNotification(notificationItem: NotificationItem) {
        val currentList = _notifications.value ?: mutableListOf()
        currentList.add(notificationItem)
        _notifications.postValue(currentList)
    }

    fun getNotification(index: Int):NotificationItem? {
        return _notifications.value?.get(index)
    }

    fun removeNotification(idx: Int) {
        val currentList = _notifications.value ?: return
        if (currentList.size <= idx) {
            return
        }
        currentList.removeAt(idx)
        _notifications.postValue(currentList)
    }

    fun clearNotifications() {
        _notifications.postValue(mutableListOf())
    }
}