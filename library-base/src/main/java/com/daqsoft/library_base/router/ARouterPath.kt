package com.daqsoft.library_base.router

/**
 * @package name：com.daqsoft.library_base.router
 * @date 28/10/2020 下午 3:19
 * @author zp
 * @describe
 */
class ARouterPath {

    /**
     * 全局
     */
    object Global{

    }

    /**
     * 主业务组件
     */
    object Main {
        private const val MAIN = "/main"
        /**主业务界面*/
        const val PAGER_MAIN = "$MAIN/Main"
        const val PAGER_WELCOME = "$MAIN/Welcome"
    }


    /**
     * 任务组件
     */
    object Task {
        private const val TASK = "/task"
        /**任务页面*/
        const val PAGER_TASK = "$TASK/Task"
        /**任务列表*/
        const val PAGER_TASK_LIST = "$TASK/TaskList"
        /**每周总结列表*/
        const val PAGER_WEEKLY_SUMMARY_LIST = "$TASK/WeeklySummaryList"
        /**每周总结*/
        const val PAGER_WEEKLY_SUMMARY = "$TASK/WeeklySummary"

    }

    /**
     * 工作台
     */
    object Workbench {
        private const val WORKBENCH = "/workbench"
        /**工作台页面*/
        const val PAGER_WORKBENCH= "${Workbench.WORKBENCH}/Workbench"
        /**请假列表页面*/
        const val PAGER_LEAVE_LIST= "${Workbench.WORKBENCH}/LeaveList"
        /**发起请假申请页面*/
        const val ADD_LEAVE_APPLY= "${Workbench.WORKBENCH}/AddLeaveApply"
        /**请假申请详情页面*/
        const val ADD_LEAVE_APPLY_INFO= "${Workbench.WORKBENCH}/AddLeaveApplyInfo"
        /**通讯录*/
        const val PAGER_ADDRESS_BOOK= "${Workbench.WORKBENCH}/AddressBook"
        /**部门*/
        const val PAGER_DEPARTMENT= "${Workbench.WORKBENCH}/Department"
        /**考勤*/
        const val PAGER_ATTENDANCE= "${Workbench.WORKBENCH}/Attendance"
        /**打卡*/
        const val PAGER_CLOCK= "${PAGER_ATTENDANCE}/Clock"
        /**排班*/
        const val PAGER_SCHEDULING= "${PAGER_ATTENDANCE}/Scheduling"
        /**统计*/
        const val PAGER_STATISTICS= "${PAGER_ATTENDANCE}/Statistics"
        /**打卡月历*/
        const val PAGER_ATTENDANCE_MONTHLY_CALENDAR = "${PAGER_ATTENDANCE}/AttendanceMonthlyCalendar"

        /**补卡列表*/
        const val PAGER_SUPPLEMENT_CARD_LIST = "${WORKBENCH}/SupplementCardList"
        /**新增补卡*/
        const val PAGER_ADD_SUPPLEMENT_CARD = "${WORKBENCH}/AddSupplementCard"
        /**补卡申请详情*/
        const val SUPPLEMENT_CARD_APPLY_INFO= "${WORKBENCH}/SupplementCardApplyInfo"
        /**审批管理*/
        const val PAGER_APPROVAL_MANAGEMENT= "${WORKBENCH}/ApprovalManagement"

        /**视频监控列表*/
        const val PAGER_VIDEO_SURVEILLANCE_LIST = "${WORKBENCH}/VideoSurveillanceList"

        /**视频监控详情*/
        const val PAGER_VIDEO_SURVEILLANCE_DETAIL = "${WORKBENCH}/VideoSurveillanceDetail"

        /**事件上报*/
        const val PAGER_INCIDENT_REPORT = "${WORKBENCH}/IncidentReport"
        /**事件上报列表*/
        const val PAGER_INCIDENT_LIST = "${WORKBENCH}/IncidentList"
        /**事件上报详情*/
        const val PAGER_INCIDENT_Detail = "${WORKBENCH}/IncidentDetail"


        /**组织架构*/
        const val PAGER_ORGANIZATION_CONTAINER = "${WORKBENCH}/OrganizationContainer"
        const val PAGER_ORGANIZATION = "${WORKBENCH}/Organization"

        /**监测报警*/
        const val PAGER_MONITOR_FORECAST = "${WORKBENCH}/MonitorForecast"
        /**告警*/
        const val PAGER_ALARM = "${WORKBENCH}/Alarm"
        /**告警列表*/
        const val PAGER_ALARM_LIST = "${WORKBENCH}/AlarmList"

        /**告警详情页面*/
        const val PAGER_ALARM_DETAILS = "${WORKBENCH}/AlarmDetails"
        /**告警处理页面*/
        const val PAGER_ALARM_HANDLE = "${WORKBENCH}/AlarmHandle"

        const val PAGER_WEB = "${WORKBENCH}/Webview"
    }

    /**
     * 聚合组件
     */
    object Home {
        private const val HOME = "/home"
        /**聚合首页*/
        const val PAGER_HOME= "$HOME/Home"
    }
    /**
     * 数据组件
     */
    object Statistics {
        private const val STATISTICS = "/statistics"

        /**数据统计*/
        const val PAGER_STATISTICS = "$STATISTICS/Statistics"
        /**票务统计*/
        const val PAGER_TICKET_STATISTICS = "$STATISTICS/TicketStatistics"
        /**票务 销售统计*/
        const val PAGER_TICKET_SALES = "$STATISTICS/TicketSales"
        /**票务 渠道分析*/
        const val PAGER_TICKET_CHANNEL = "$STATISTICS/TicketChannel"
        /**票务 时段分析*/
        const val PAGER_TICKET_TIME_PERIOD = "$STATISTICS/TicketTimePeriod"
        /**票务 票种分析*/
        const val PAGER_TICKET_TYPE = "$STATISTICS/TicketType"
        /**柱状图 全屏模式*/
        const val PAGER_BAR_CHART_FULL_SCREEN = "$STATISTICS/BarChartFullScreen"
        /**票务 节假日分析*/
        const val PAGER_TICKET_HOLIDAY = "$STATISTICS/TicketHoliday"
        /**客流统计*/
        const val PAGER_PASSENGER_FLOW_STATISTICS = "$STATISTICS/PassengerFlowStatistics"
        /**客流 时段分析*/
        const val PAGER_PASSENGER_FLOW_TIME_PERIOD = "$STATISTICS/PassengerFlowTimePeriod"
        /**客流 游客画像*/
        const val PAGER_PASSENGER_FLOW_PORTRAIT = "$STATISTICS/PassengerFlowPortrait"
        /**客流 景点偏好*/
        const val PAGER_PASSENGER_FLOW_ATTRACTION_PREFERENCE = "$STATISTICS/PassengerFlowAttractionPreference"
        /**客流 节假日分析*/
        const val PAGER_PASSENGER_FLOW_HOLIDAY = "$STATISTICS/PassengerFlowHoliday"
        /**车辆统计*/
        const val PAGER_VEHICLE_STATISTICS = "$STATISTICS/VehicleStatistics"
        /**车辆  来源地*/
        const val PAGER_VEHICLE_SOURCE = "$STATISTICS/VehicleSource"
        /**车辆  时段分析*/
        const val PAGER_VEHICLE_TIME_PERIOD = "$STATISTICS/VehicleTimePeriod"
        /**车辆  停留时长*/
        const val PAGER_VEHICLE_LENGTH_OF_STAY = "$STATISTICS/VehicleLengthOfStay"
        /**车辆  停车场分析*/
        const val PAGER_VEHICLE_PARKING_LOT = "$STATISTICS/VehicleParkingLot"
        /**车辆 节假日分析*/
        const val PAGER_VEHICLE_HOLIDAY = "$STATISTICS/VehicleHoliday"
    }

    /**
     * 用户组件
     */
    object Mine {
        private const val MINE = "/mine"
        /**我的页面*/
        const val PAGER_MINE = "$MINE/Mine"
        /**个人信息*/
        const val PAGER_PERSONAL_INFO = "$MINE/PersonalInfo"
        /**登录界面*/
        const val PAGER_LOGIN = "${MINE}/Login"
        /**景区列表界面*/
        const val PAGER_SCENIC_LIST = "${MINE}/ScenicList"
        /**设置界面*/
        const val PAGER_SET_UP = "${MINE}/SetUp"
    }
}
