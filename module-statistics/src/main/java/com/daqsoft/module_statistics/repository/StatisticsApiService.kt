package com.daqsoft.module_statistics.repository

import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.module_statistics.repository.pojo.vo.*
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.*

/**
 * @package name：com.daqsoft.module_mine.repository
 * @date 6/11/2020 下午 2:10
 * @author zp
 * @describe
 */
interface StatisticsApiService {


    /**
     * 票务 销售统计
     * @param timeType String? 周期：DATE-日,MONTH-月,QUARTER-季度,YEAR-年,可用值:DAY,MONTH,QUARTER,YEAR
     * @param statisticsTime String? 传递年，年月，年月日, 格式：日(yyyy-MM-dd),月(yyyy-MM),季度(yyyy),年(yyyy)
     * @param endTime String? 时间范围截止日期(yyyy-MM-dd,时间类型为DATE时可传入,表示时间范围)
     * @param quarter String? 如果为季度，此值传入哪个季度，如1代表1季度
     * @return Observable<AppResponse<TicketSales>>
     */
    @GET("v5/business/data/ticket/sales")
    fun getTicketSales(
        @Query("timeType") timeType : String?=null,
        @Query("statisticsTime") statisticsTime : String?=null,
        @Query("endTime") endTime : String?=null,
        @Query("quarter") quarter : String? = null,
    ): Observable<AppResponse<TicketSales>>

    /**
     * 票务 渠道统计
     * @param timeType String? 周期：DATE-日,MONTH-月,QUARTER-季度,YEAR-年,可用值:DAY,MONTH,QUARTER,YEAR
     * @param statisticsTime String? 传递年，年月，年月日, 格式：日(yyyy-MM-dd),月(yyyy-MM),季度(yyyy),年(yyyy)
     * @param endTime String? 时间范围截止日期(yyyy-MM-dd,时间类型为DATE时可传入,表示时间范围)
     * @param quarter String? 如果为季度，此值传入哪个季度，如1代表1季度
     * @return Observable<AppResponse<TicketChannel>>
     */
    @GET("v5/business/data/ticket/channel")
    fun getTicketChannel(
        @Query("timeType") timeType : String?=null,
        @Query("statisticsTime") statisticsTime : String?=null,
        @Query("endTime") endTime : String?=null,
        @Query("quarter") quarter : String? = null,
    ): Observable<AppResponse<TicketChannel>>


    /**
     * 票务 时段统计
     * @param timeType String? 周期：DATE-日,MONTH-月,QUARTER-季度,YEAR-年,可用值:DAY,MONTH,QUARTER,YEAR
     * @param statisticsTime String? 传递年，年月，年月日, 格式：日(yyyy-MM-dd),月(yyyy-MM),季度(yyyy),年(yyyy)
     * @param endTime String? 时间范围截止日期(yyyy-MM-dd,时间类型为DATE时可传入,表示时间范围)
     * @param quarter String? 如果为季度，此值传入哪个季度，如1代表1季度
     * @return Observable<AppResponse<TicketTimePeriod>>
     */
    @GET("v5/business/data/ticket/time/interval")
    fun getTicketTimePeriod(
        @Query("timeType") timeType : String?=null,
        @Query("statisticsTime") statisticsTime : String?=null,
        @Query("endTime") endTime : String?=null,
        @Query("quarter") quarter : String? = null,
    ): Observable<AppResponse<TicketTimePeriod>>

    /**
     * 票务 票种分析
     * @param timeType String? 周期：DATE-日,MONTH-月,QUARTER-季度,YEAR-年,可用值:DAY,MONTH,QUARTER,YEAR
     * @param statisticsTime String? 传递年，年月，年月日, 格式：日(yyyy-MM-dd),月(yyyy-MM),季度(yyyy),年(yyyy)
     * @param endTime String? 时间范围截止日期(yyyy-MM-dd,时间类型为DATE时可传入,表示时间范围)
     * @param quarter String? 如果为季度，此值传入哪个季度，如1代表1季度
     * @return Observable<AppResponse<TicketType>>
     */
    @GET("v5/business/data/ticket/ticket/name")
    fun getTicketType(
        @Query("timeType") timeType : String?=null,
        @Query("statisticsTime") statisticsTime : String?=null,
        @Query("endTime") endTime : String?=null,
        @Query("quarter") quarter : String? = null,
    ): Observable<AppResponse<TicketType>>


    /**
     * 票务 票种分析
     * @param timeType String? 周期：DATE-日,MONTH-月,QUARTER-季度,YEAR-年,可用值:DAY,MONTH,QUARTER,YEAR
     * @param statisticsTime String? 传递年，年月，年月日, 格式：日(yyyy-MM-dd),月(yyyy-MM),季度(yyyy),年(yyyy)
     * @param endTime String? 时间范围截止日期(yyyy-MM-dd,时间类型为DATE时可传入,表示时间范围)
     * @param quarter String? 如果为季度，此值传入哪个季度，如1代表1季度
     * @param name String? 票种名称
     * @return Observable<AppResponse<TicketTypeSingle>>
     */
    @GET("v5/business/data/ticket/ticket/name/only")
    fun getTicketTypeSingle(
        @Query("timeType") timeType : String?=null,
        @Query("statisticsTime") statisticsTime : String?=null,
        @Query("endTime") endTime : String?=null,
        @Query("quarter") quarter : String? = null,
        @Query("name") name : String? = null,
    ): Observable<AppResponse<TicketTypeSingle>>


    /**
     * 票务 节假日分析
     * @param timeType String? 周期：DATE-日,MONTH-月,QUARTER-季度,YEAR-年,可用值:DAY,MONTH,QUARTER,YEAR
     * @param statisticsTime String? 传递年，年月，年月日, 格式：日(yyyy-MM-dd),月(yyyy-MM),季度(yyyy),年(yyyy)
     * @param endTime String? 时间范围截止日期(yyyy-MM-dd,时间类型为DATE时可传入,表示时间范围)
     * @param quarter String? 如果为季度，此值传入哪个季度，如1代表1季度
     * @return Observable<AppResponse<TicketHoliday>>
     */
    @GET("v5/business/data/ticket/holiday")
    fun getTicketHoliday(
        @Query("timeType") timeType : String?=null,
        @Query("statisticsTime") statisticsTime : String?=null,
        @Query("endTime") endTime : String?=null,
        @Query("quarter") quarter : String? = null,
    ): Observable<AppResponse<TicketHoliday>>


    /**
     * 票务 节假日
     * @param timeType String? 周期：DATE-日,MONTH-月,QUARTER-季度,YEAR-年,可用值:DAY,MONTH,QUARTER,YEAR
     * @param statisticsTime String? 传递年，年月，年月日, 格式：日(yyyy-MM-dd),月(yyyy-MM),季度(yyyy),年(yyyy)
     * @param endTime String? 时间范围截止日期(yyyy-MM-dd,时间类型为DATE时可传入,表示时间范围)
     * @param quarter String? 如果为季度，此值传入哪个季度，如1代表1季度
     * @param holiday String? 节假日类型
     * @return Observable<AppResponse<TicketHolidaySingle>>
     */
    @GET("v5/business/data/ticket/holiday/only")
    fun getTicketHolidaySingle(
        @Query("timeType") timeType : String?=null,
        @Query("statisticsTime") statisticsTime : String?=null,
        @Query("endTime") endTime : String?=null,
        @Query("quarter") quarter : String? = null,
        @Query("holiday") holiday : String? = null,
    ): Observable<AppResponse<TicketHolidaySingle>>

    /**
     * 获取节假日
     * @return Observable<AppResponse<HolidayOption>>
     */
    @GET("v5/business/data/ticket/holiday/options")
    fun getHolidayOptions(@Query("statisticsTime") statisticsTime : String?=null,): Observable<AppResponse<HolidayOption>>


    /**
     * 客流 时段分析
     * @param timeType String? 周期：DATE-日,MONTH-月,QUARTER-季度,YEAR-年,可用值:DAY,MONTH,QUARTER,YEAR
     * @param statisticsTime String? 传递年，年月，年月日, 格式：日(yyyy-MM-dd),月(yyyy-MM),季度(yyyy),年(yyyy)
     * @param endTime String? 时间范围截止日期(yyyy-MM-dd,时间类型为DATE时可传入,表示时间范围)
     * @param quarter String? 如果为季度，此值传入哪个季度，如1代表1季度
     * @param companyId String? 公司的ID,没有的话不要传这个key值
     * @return Observable<AppResponse<PassengerFlowTimePeriod>>
     */
    @GET("v5/business/data/passenger/time")
    fun getPassengerFlowTimePeriod(
        @Query("timeType") timeType : String?=null,
        @Query("statisticsTime") statisticsTime : String?=null,
        @Query("endTime") endTime : String?=null,
        @Query("quarter") quarter : String? = null,
        @Query("companyId") companyId : String? = null,
    ): Observable<AppResponse<PassengerFlowTimePeriod>>

    /**
     * 客流 游客画像
     * @param timeType String? 周期：DATE-日,MONTH-月,QUARTER-季度,YEAR-年,可用值:DAY,MONTH,QUARTER,YEAR
     * @param statisticsTime String? 传递年，年月，年月日, 格式：日(yyyy-MM-dd),月(yyyy-MM),季度(yyyy),年(yyyy)
     * @param endTime String? 时间范围截止日期(yyyy-MM-dd,时间类型为DATE时可传入,表示时间范围)
     * @param quarter String? 如果为季度，此值传入哪个季度，如1代表1季度
     * @param companyId String? 公司的ID,没有的话不要传这个key值
     * @return Observable<AppResponse<PassengerFlowPortrait>>
     */
    @GET("v5/business/data/passenger/portrait-analysis")
    fun getPassengerFlowPortrait(
        @Query("timeType") timeType : String?=null,
        @Query("statisticsTime") statisticsTime : String?=null,
        @Query("endTime") endTime : String?=null,
        @Query("quarter") quarter : String? = null,
        @Query("companyId") companyId : String? = null,
    ): Observable<AppResponse<PassengerFlowPortrait>>

    /**
     * 客流 景点偏好
     * @param timeType String? 周期：DATE-日,MONTH-月,QUARTER-季度,YEAR-年,可用值:DAY,MONTH,QUARTER,YEAR
     * @param statisticsTime String? 传递年，年月，年月日, 格式：日(yyyy-MM-dd),月(yyyy-MM),季度(yyyy),年(yyyy)
     * @param endTime String? 时间范围截止日期(yyyy-MM-dd,时间类型为DATE时可传入,表示时间范围)
     * @param quarter String? 如果为季度，此值传入哪个季度，如1代表1季度
     * @param companyId String? 公司的ID,没有的话不要传这个key值
     * @return Observable<AppResponse<PassengerFlowAttractionPreference>>
     */
    @GET("v5/business/data/passenger/spot")
    fun getPassengerFlowAttractionPreference(
        @Query("timeType") timeType : String?=null,
        @Query("statisticsTime") statisticsTime : String?=null,
        @Query("endTime") endTime : String?=null,
        @Query("quarter") quarter : String? = null,
        @Query("spot") spot : String? = null,
        @Query("companyId") companyId : String? = null,
    ): Observable<AppResponse<PassengerFlowAttractionPreference>>

    /**
     * 获取 景点
     * @return Observable<AppResponse<Attraction>>
     */
    @GET("v5/business/data/passenger/spot-menus")
    fun getAttraction(): Observable<AppResponse<Attraction>>


    /**
     * 获取节假日
     * @return Observable<AppResponse<HolidayMenu>>
     */
    @GET("v5/business/data/passenger/holiday-menus")
    fun getHolidayMenu(
        @Query("timeType") timeType : String?=null,
        @Query("statisticsTime") statisticsTime : String?=null,
    ): Observable<AppResponse<HolidayMenu>>

    /**
     * 客流 节假日分析
     * @param holidayId String?
     * @param year String?
     * @return Observable<AppResponse<PassengerFlowHoliday>>
     */
    @GET("v5/business/data/passenger/holiday")
    fun getPassengerFlowHoliday(
        @Query("timeType") timeType : String?=null,
        @Query("statisticsTime") statisticsTime : String?=null,
        @Query("endTime") endTime : String?=null,
        @Query("quarter") quarter : String? = null,
        @Query("holidayId") holidayId : String?=null,
        @Query("holidayName") holidayName : String?=null,
    ): Observable<AppResponse<PassengerFlowHoliday>>


    /**
     * 车辆 来源地
     * @param timeType String? 周期：DATE-日,MONTH-月,QUARTER-季度,YEAR-年,可用值:DAY,MONTH,QUARTER,YEAR
     * @param statisticsTime String? 传递年，年月，年月日, 格式：日(yyyy-MM-dd),月(yyyy-MM),季度(yyyy),年(yyyy)
     * @param endTime String? 时间范围截止日期(yyyy-MM-dd,时间类型为DATE时可传入,表示时间范围)
     * @param quarter String? 如果为季度，此值传入哪个季度，如1代表1季度
     * @param model String? 0:总览 1:省内 2:省外
     * @param city String? 单来源地查询必填参数
     * @return Observable<AppResponse<VehicleSource>>
     */
    @GET("v5/business/statistics/vehicle/origin/city/data")
    fun getVehicleSource(
        @Query("timeType") timeType : String?=null,
        @Query("statisticsTime") statisticsTime : String?=null,
        @Query("endTime") endTime : String?=null,
        @Query("quarter") quarter : String? = null,
        @Query("model") model : String? = null,
        @Query("city") city : String? = null,
    ): Observable<AppResponse<VehicleSource>>


    /**
     * 车辆 时段分析
     * @param timeType String? 周期：DATE-日,MONTH-月,QUARTER-季度,YEAR-年,可用值:DAY,MONTH,QUARTER,YEAR
     * @param statisticsTime String? 传递年，年月，年月日, 格式：日(yyyy-MM-dd),月(yyyy-MM),季度(yyyy),年(yyyy)
     * @param endTime String? 时间范围截止日期(yyyy-MM-dd,时间类型为DATE时可传入,表示时间范围)
     * @param quarter String? 如果为季度，此值传入哪个季度，如1代表1季度
     * @param companyId String? 公司的ID,没有的话不要传这个key值
     * @return Observable<AppResponse<VehicleTimePeriod>>
     */
    @GET("v5/business/statistics/vehicle/time")
    fun getVehicleTimePeriod(
        @Query("timeType") timeType : String?=null,
        @Query("statisticsTime") statisticsTime : String?=null,
        @Query("endTime") endTime : String?=null,
        @Query("quarter") quarter : String? = null,
        @Query("companyId") companyId : String? = null,
    ): Observable<AppResponse<VehicleTimePeriod>>

    /**
     * 车辆 停留时长
     * @param timeType String? 周期：DATE-日,MONTH-月,QUARTER-季度,YEAR-年,可用值:DAY,MONTH,QUARTER,YEAR
     * @param statisticsTime String? 传递年，年月，年月日, 格式：日(yyyy-MM-dd),月(yyyy-MM),季度(yyyy),年(yyyy)
     * @param endTime String? 时间范围截止日期(yyyy-MM-dd,时间类型为DATE时可传入,表示时间范围)
     * @param quarter String? 如果为季度，此值传入哪个季度，如1代表1季度
     * @return Observable<AppResponse<VehicleTimePeriod>>
     */
    @GET("v5/business/statistics/vehicle/stay/time/data")
    fun getVehicleLengthOfStay(
        @Query("timeType") timeType : String?=null,
        @Query("statisticsTime") statisticsTime : String?=null,
        @Query("endTime") endTime : String?=null,
        @Query("quarter") quarter : String? = null,
    ): Observable<AppResponse<VehicleLengthOfStay>>


    /**
     * 获取 停车场
     * @return Observable<AppResponse<ParkingLot>>
     */
    @GET("v5/business/statistics/vehicle/car-park-menus")
    fun getParkingLot(): Observable<AppResponse<ParkingLot>>


    /**
     * 车辆 停车场分析
     * @param timeType String? 周期：DATE-日,MONTH-月,QUARTER-季度,YEAR-年,可用值:DAY,MONTH,QUARTER,YEAR
     * @param statisticsTime String? 传递年，年月，年月日, 格式：日(yyyy-MM-dd),月(yyyy-MM),季度(yyyy),年(yyyy)
     * @param endTime String? 时间范围截止日期(yyyy-MM-dd,时间类型为DATE时可传入,表示时间范围)
     * @param quarter String? 如果为季度，此值传入哪个季度，如1代表1季度
     * @param carPark String? 停车场名称(查询全部时不传)
     * @param companyId String? 公司的ID,没有的话不要传这个key值
     * @return Observable<AppResponse<VehicleParkingLot>>
     */
    @GET("v5/business/statistics/vehicle/car-park")
    fun getVehicleParkingLot(
        @Query("timeType") timeType : String?=null,
        @Query("statisticsTime") statisticsTime : String?=null,
        @Query("endTime") endTime : String?=null,
        @Query("quarter") quarter : String? = null,
        @Query("carPark") carPark : String? = null,
        @Query("companyId") companyId : String? = null,
    ): Observable<AppResponse<VehicleParkingLot>>


    /**
     * 车辆 节假日分析
     * @param timeType String? 周期：DATE-日,MONTH-月,QUARTER-季度,YEAR-年,可用值:DAY,MONTH,QUARTER,YEAR
     * @param statisticsTime String? 传递年，年月，年月日, 格式：日(yyyy-MM-dd),月(yyyy-MM),季度(yyyy),年(yyyy)
     * @param endTime String? 时间范围截止日期(yyyy-MM-dd,时间类型为DATE时可传入,表示时间范围)
     * @param quarter String? 如果为季度，此值传入哪个季度，如1代表1季度
     * @param holidayId String?
     * @return Observable<AppResponse<VehicleHoliday>>
     */
    @GET("v5/business/statistics/vehicle/holiday")
    fun getVehicleHoliday(
        @Query("timeType") timeType : String?=null,
        @Query("statisticsTime") statisticsTime : String?=null,
        @Query("endTime") endTime : String?=null,
        @Query("quarter") quarter : String? = null,
        @Query("holidayId") holidayId : String?=null,
    ): Observable<AppResponse<VehicleHoliday>>


    /**
     * 获取 今日游客
     * @return Observable<AppResponse<TodayTourist>>
     */
    @GET("v5/business/data/index/passenger/simple")
    fun getTodayTouristCount(): Observable<AppResponse<TodayTourist>>

    /**
     * 获取 今日票务
     * @return Observable<AppResponse<TodayTicket>>
     */
    @GET("v5/business/data/index/sales/simple")
    fun getTodayTicketCount(): Observable<AppResponse<TodayTicket>>

    /**
     * 获取 今日车辆
     * @return Observable<AppResponse<TodayVehicle>>
     */
    @GET("v5/business/data/index/vehicle/simple")
    fun getTodayVehicleCount(): Observable<AppResponse<TodayVehicle>>

    /**
     * 获取 景区舒适度
     * @return Observable<AppResponse<ScenicSpotComfort>>
     */
    @GET("v5/business/data/index/saturation")
    fun getScenicSpotComfort(): Observable<AppResponse<ScenicSpotComfort>>
}