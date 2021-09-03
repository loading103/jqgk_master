package com.daqsoft.module_work.repository.pojo.dto

import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.module_work.repository.pojo.vo.File
import com.google.gson.annotations.Expose
import com.luck.picture.lib.entity.LocalMedia

/**
 * @package name：com.daqsoft.module_work.repository.pojo.dto
 * @date 27/5/2021 下午 2:46
 * @author zp
 * @describe
 */
data class AlarmHandleRequest(

    // eventId,必填
    var eventId : String,
    // 办理结果,必填
    var detail : String,
    // 处理方式 1:任务指派,2:直接处理,3:无效报警,4:确认办结,5:重新处理,6:办理,7:办结,8:退回
    var method : Int,
    // 指派人员Id,没有就不填
    var receiver : String? = null,
    // 手动上传的附件
    var files: List<File>? = null,
    // 本地文件
    @Expose
    var localFiles: List<LocalMedia>? = null,
)
