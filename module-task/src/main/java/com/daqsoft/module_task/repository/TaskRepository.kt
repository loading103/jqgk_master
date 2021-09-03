package com.daqsoft.module_task.repository

import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_common.bean.AddressBook
import com.daqsoft.library_common.bean.Options
import com.daqsoft.module_task.repository.pojo.dto.TaskRequest
import com.daqsoft.module_task.repository.pojo.vo.Task
import com.daqsoft.module_task.repository.pojo.vo.TaskCount
import com.daqsoft.module_task.repository.pojo.vo.WeeklySummary
import com.daqsoft.mvvmfoundation.base.BaseModel
import io.reactivex.rxjava3.core.Observable
import timber.log.Timber
import javax.inject.Inject

/**
 * @package name：com.daqsoft.module_mine.repository
 * @date 6/11/2020 下午 2:09
 * @author zp
 * @describe
 */
class TaskRepository @Inject constructor(private val taskApiService:TaskApiService) : BaseModel(),TaskApiService {

    override fun getEmployee(): Observable<AppResponse<AddressBook>> {
        return taskApiService.getEmployee()
    }

    override fun getTaskList(body: TaskRequest): Observable<AppResponse<Task>> {
        return taskApiService.getTaskList(body)
    }

    override fun getTaskCount(): Observable<AppResponse<TaskCount>> {
        return taskApiService.getTaskCount()
    }

    override fun getWeeklySummaryList(
        page: Int,
        size: Int
    ): Observable<AppResponse<WeeklySummary>> {
        return taskApiService.getWeeklySummaryList(page, size)
    }

    override fun getWeeklySummary(id: String): Observable<AppResponse<WeeklySummary>> {
        return taskApiService.getWeeklySummary(id)
    }

    override fun getOptions(): Observable<AppResponse<Options>> {
        return taskApiService.getOptions()
    }
}