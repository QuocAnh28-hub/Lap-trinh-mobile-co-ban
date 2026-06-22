package com.example.btl_datphongkhachsan.api;

import com.example.btl_datphongkhachsan.models.BookingRequest;
import com.example.btl_datphongkhachsan.models.BookingResponse;
import com.example.btl_datphongkhachsan.models.ChangePasswordRequest;
import com.example.btl_datphongkhachsan.models.CreateInvoiceRequest;
import com.example.btl_datphongkhachsan.models.CreateInvoiceResponse;
import com.example.btl_datphongkhachsan.models.CustomerInfo;
import com.example.btl_datphongkhachsan.models.InvoiceFullResponse;
import com.example.btl_datphongkhachsan.models.InvoiceHistory;
import com.example.btl_datphongkhachsan.models.LoginRequest;
import com.example.btl_datphongkhachsan.models.LoginResponse;
import com.example.btl_datphongkhachsan.models.MinibarUsage;
import com.example.btl_datphongkhachsan.models.OccupancyRateResponse;
import com.example.btl_datphongkhachsan.models.PenaltyUsage;
import com.example.btl_datphongkhachsan.models.PendingInvoice;
import com.example.btl_datphongkhachsan.models.RegisterRequest;
import com.example.btl_datphongkhachsan.models.RegisterResponse;
import com.example.btl_datphongkhachsan.models.Reservation;
import com.example.btl_datphongkhachsan.models.ReservationModifyRequest;
import com.example.btl_datphongkhachsan.models.RevenueThisMonthResponse;
import com.example.btl_datphongkhachsan.models.Room;
import com.example.btl_datphongkhachsan.models.RoomStatisticsResponse;
import com.example.btl_datphongkhachsan.models.RoomStatusSummaryResponse;
import com.example.btl_datphongkhachsan.models.RoomStayHistory;
import com.example.btl_datphongkhachsan.models.RoomType;
import com.example.btl_datphongkhachsan.models.SearchAvailableRequest;
import com.example.btl_datphongkhachsan.models.ServiceUsage;
import com.example.btl_datphongkhachsan.models.StayingCustomer;
import com.example.btl_datphongkhachsan.models.TodayCheckInOutResponse;
import com.example.btl_datphongkhachsan.models.WaitingCustomer;

import java.util.List;
import java.util.Map;

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

    // Invoice APIs
    @GET("api/invoices/pending")
    Call<List<PendingInvoice>> getPendingInvoices();

    @GET("api/invoices/history")
    Call<List<InvoiceHistory>> getInvoiceHistory();

    @GET("api/invoices/stays/{stayId}/full")
    Call<InvoiceFullResponse> getFullInvoiceDetail(@Path("stayId") int stayId);

    @POST("api/invoices/create-and-pay")
    Call<CreateInvoiceResponse> createAndPayInvoice(@Body CreateInvoiceRequest request);

    // Checkout/Payment APIs
    @GET("api/reservations/stays/{stayId}/room-stay-history-checkedout")
    Call<List<RoomStayHistory>> getRoomStayHistory(@Path("stayId") int stayId);

    @GET("api/reservations/stays/{stayId}/service-usages")
    Call<List<ServiceUsage>> getServiceUsages(@Path("stayId") int stayId);

    @GET("api/reservations/stays/{stayId}/minibar-usages")
    Call<List<MinibarUsage>> getMinibarUsages(@Path("stayId") int stayId);

    @GET("api/reservations/stays/{stayId}/penalties")
    Call<List<PenaltyUsage>> getPenalties(@Path("stayId") int stayId);

    // Room Status APIs
    @GET("api/rooms")
    Call<List<Room>> getAllRooms();

    @POST("api/rooms/{roomId}/clean")
    Call<Map<String, String>> cleanRoom(@Path("roomId") int roomId);

    // Staying Customers
    @GET("api/reservations/current-staying-customers")
    Call<List<StayingCustomer>> getCurrentStayingCustomers();

    // Waiting Check-in Customers
    @GET("api/reservations/waiting-checkin-customers")
    Call<List<WaitingCustomer>> getWaitingCheckinCustomers();
}
