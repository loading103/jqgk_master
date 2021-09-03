package com.daqsoft.module_statistics.repository

import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.module_statistics.repository.pojo.vo.*
import com.daqsoft.mvvmfoundation.base.BaseModel
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

/**
 * @package name：com.daqsoft.module_mine.repository
 * @date 6/11/2020 下午 2:09
 * @author zp
 * @describe
 */
class StatisticsRepository @Inject constructor(private val statisticsApiService: StatisticsApiService) : BaseModel(),StatisticsApiService {
    override fun getTicketSales(
        timeType: String?,
        statisticsTime: String?,
        endTime: String?,
        quarter: String?
    ): Observable<AppResponse<TicketSales>> {
        return statisticsApiService.getTicketSales(timeType, statisticsTime, endTime, quarter)
    }

    override fun getTicketChannel(
        timeType: String?,
        statisticsTime: String?,
        endTime: String?,
        quarter: String?
    ): Observable<AppResponse<TicketChannel>> {
        return statisticsApiService.getTicketChannel(timeType, statisticsTime, endTime, quarter)
    }

    override fun getTicketTimePeriod(
        timeType: String?,
        statisticsTime: String?,
        endTime: String?,
        quarter: String?
    ): Observable<AppResponse<TicketTimePeriod>> {
        return statisticsApiService.getTicketTimePeriod(timeType, statisticsTime, endTime, quarter)
    }

    override fun getTicketType(
        timeType: String?,
        statisticsTime: String?,
        endTime: String?,
        quarter: String?
    ): Observable<AppResponse<TicketType>> {
        return statisticsApiService.getTicketType(timeType, statisticsTime, endTime, quarter)
    }

    override fun getTicketTypeSingle(
        timeType: String?,
        statisticsTime: String?,
        endTime: String?,
        quarter: String?,
        name: String?
    ): Observable<AppResponse<TicketTypeSingle>> {
        return statisticsApiService.getTicketTypeSingle(timeType, statisticsTime, endTime, quarter, name)
    }

    override fun getTicketHoliday(
        timeType: String?,
        statisticsTime: String?,
        endTime: String?,
        quarter: String?
    ): Observable<AppResponse<TicketHoliday>> {
        return statisticsApiService.getTicketHoliday(timeType, statisticsTime, endTime, quarter)
    }

    override fun getTicketHolidaySingle(
        timeType: String?,
        statisticsTime: String?,
        endTime: String?,
        quarter: String?,
        holiday: String?
    ): Observable<AppResponse<TicketHolidaySingle>> {
        return statisticsApiService.getTicketHolidaySingle(timeType, statisticsTime, endTime, quarter, holiday)
    }

    override fun getHolidayOptions(statisticsTime : String?): Observable<AppResponse<HolidayOption>> {
        return statisticsApiService.getHolidayOptions(statisticsTime)
    }

    override fun getPassengerFlowTimePeriod(
        timeType: String?,
        statisticsTime: String?,
        endTime: String?,
        quarter: String?,
        companyId: String?
    ): Observable<AppResponse<PassengerFlowTimePeriod>> {
        return statisticsApiService.getPassengerFlowTimePeriod(timeType, statisticsTime, endTime, quarter, companyId)
    }

    override fun getPassengerFlowPortrait(
        timeType: String?,
        statisticsTime: String?,
        endTime: String?,
        quarter: String?,
        companyId: String?
    ): Observable<AppResponse<PassengerFlowPortrait>> {
        return statisticsApiService.getPassengerFlowPortrait(timeType, statisticsTime, endTime, quarter, companyId)
    }

    override fun getPassengerFlowAttractionPreference(
        timeType: String?,
        statisticsTime: String?,
        endTime: String?,
        quarter: String?,
        spot: String?,
        companyId : String?
    ): Observable<AppResponse<PassengerFlowAttractionPreference>> {
        return statisticsApiService.getPassengerFlowAttractionPreference(timeType, statisticsTime, endTime, quarter, spot,companyId)
    }

    override fun getAttraction(): Observable<AppResponse<Attraction>> {
        return statisticsApiService.getAttraction()
    }

    override fun getHolidayMenu(timeType: String?, statisticsTime: String?): Observable<AppResponse<HolidayMenu>> {
        return statisticsApiService.getHolidayMenu(timeType, statisticsTime)
    }

    override fun getPassengerFlowHoliday(
        timeType: String?,
        statisticsTime: String?,
        endTime: String?,
        quarter: String?,
        holidayId: String?,
        holidayName: String?
    ): Observable<AppResponse<PassengerFlowHoliday>> {
        return statisticsApiService.getPassengerFlowHoliday(
            timeType,
            statisticsTime,
            endTime,
            quarter,
            holidayId,
            holidayName
        )
    }

    override fun getVehicleSource(
        timeType: String?,
        statisticsTime: String?,
        endTime: String?,
        quarter: String?,
        model: String?,
        city: String?
    ): Observable<AppResponse<VehicleSource>> {
        return statisticsApiService.getVehicleSource(timeType, statisticsTime, endTime, quarter, model, city)
    }

    override fun getVehicleTimePeriod(
        timeType: String?,
        statisticsTime: String?,
        endTime: String?,
        quarter: String?,
        companyId: String?
    ): Observable<AppResponse<VehicleTimePeriod>> {
        return statisticsApiService.getVehicleTimePeriod(timeType, statisticsTime, endTime, quarter, companyId)
    }

    override fun getVehicleLengthOfStay(
        timeType: String?,
        statisticsTime: String?,
        endTime: String?,
        quarter: String?
    ): Observable<AppResponse<VehicleLengthOfStay>> {
        return statisticsApiService.getVehicleLengthOfStay(timeType, statisticsTime, endTime, quarter)
    }

    override fun getParkingLot(): Observable<AppResponse<ParkingLot>> {
        return statisticsApiService.getParkingLot()
    }

    override fun getVehicleParkingLot(
        timeType: String?,
        statisticsTime: String?,
        endTime: String?,
        quarter: String?,
        carPark: String?,
        companyId: String?,
    ): Observable<AppResponse<VehicleParkingLot>> {
        return statisticsApiService.getVehicleParkingLot(timeType, statisticsTime, endTime, quarter, carPark, companyId)
    }

    override fun getVehicleHoliday(
        timeType: String?,
        statisticsTime: String?,
        endTime: String?,
        quarter: String?,
        holidayId: String?
    ): Observable<AppResponse<VehicleHoliday>> {
        return statisticsApiService.getVehicleHoliday(timeType, statisticsTime, endTime, quarter, holidayId)
    }

    override fun getTodayTouristCount(): Observable<AppResponse<TodayTourist>> {
        return statisticsApiService.getTodayTouristCount()
    }

    override fun getTodayTicketCount(): Observable<AppResponse<TodayTicket>> {
        return statisticsApiService.getTodayTicketCount()
    }

    override fun getTodayVehicleCount(): Observable<AppResponse<TodayVehicle>> {
        return statisticsApiService.getTodayVehicleCount()
    }

    override fun getScenicSpotComfort(): Observable<AppResponse<ScenicSpotComfort>> {
        return statisticsApiService.getScenicSpotComfort()
    }

}