package com.example.btl_datphongkhachsan.api;

import com.example.btl_datphongkhachsan.models.BookingRequest;
import com.example.btl_datphongkhachsan.models.BookingResponse;
import com.example.btl_datphongkhachsan.models.ChangePasswordRequest;
import com.example.btl_datphongkhachsan.models.CustomerInfo;
import com.example.btl_datphongkhachsan.models.LoginRequest;
import com.example.btl_datphongkhachsan.models.LoginResponse;
import com.example.btl_datphongkhachsan.models.OccupancyRateResponse;
import com.example.btl_datphongkhachsan.models.RegisterRequest;
import com.example.btl_datphongkhachsan.models.RegisterResponse;
import com.example.btl_datphongkhachsan.models.Reservation;
import com.example.btl_datphongkhachsan.models.ReservationModifyRequest;
import com.example.btl_datphongkhachsan.models.RevenueThisMonthResponse;
import com.example.btl_datphongkhachsan.models.RoomStatisticsResponse;
import com.example.btl_datphongkhachsan.models.RoomStatusSummaryResponse;
import com.example.btl_datphongkhachsan.models.RoomType;
import com.example.btl_datphongkhachsan.models.SearchAvailableRequest;
import com.example.btl_datphongkhachsan.models.TodayCheckInOutResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @POST("api/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("api/login/register-customer")
    Call<RegisterResponse> register(@Body RegisterRequest registerRequest);

    @POST("api/get-room-types/search-available")
    Call<List<RoomType>> searchAvailable(@Body SearchAvailableRequest request);

    @GET("api/get-room-types")
    Call<List<RoomType>> getAllRoomTypes();

    @GET("api/get-room-types/with-price")
    Call<List<RoomType>> getRoomTypesWithPrice();

    @GET("api/customers/{userId}/reservations")
    Call<List<Reservation>> getCustomerReservations(@Path("userId") String userId);

    @GET("api/customers/info/{userId}")
    Call<CustomerInfo> getCustomerInfo(@Path("userId") String userId);

    @PUT("api/customers/profile/{userId}")
    Call<Void> updateCustomerProfile(@Path("userId") String userId, @Body CustomerInfo info);

    @PATCH("api/reservations/{reservationId}/cancel")
    Call<Void> cancelReservation(@Path("reservationId") int reservationId);

    @POST("api/reservations/book-room")
    Call<BookingResponse> bookRoom(@Body BookingRequest request);

    @PUT("api/reservations/{reservationId}")
    Call<Void> modifyReservation(@Path("reservationId") int reservationId, @Body ReservationModifyRequest request);

    @PUT("api/pages-for-customer/user/{userId}/password")
    Call<Void> changePassword(@Path("userId") String userId, @Body ChangePasswordRequest request);

    @PUT("api/pages-for-customer/user/{userId}")
    Call<Void> updateProfile(@Path("userId") String userId, @Body CustomerInfo info);

    // Dashboard APIs
    @GET("api/overview/room-statistics")
    Call<RoomStatisticsResponse> getRoomStatistics();

    @GET("api/overview/occupancy-rate")
    Call<OccupancyRateResponse> getOccupancyRate();

    @GET("api/overview/room-status-summary")
    Call<RoomStatusSummaryResponse> getRoomStatusSummary();

    @GET("api/overview/today-checkin-checkout")
    Call<TodayCheckInOutResponse> getTodayCheckInOut();

    @GET("api/overview/revenue-this-month")
    Call<RevenueThisMonthResponse> getRevenueThisMonth();
}
