const { sql } = require("../config/db");

const parseYearMonth = (req, res) => {
  const year = Number.parseInt(req.query.year, 10);
  const month = Number.parseInt(req.query.month, 10);

  if (!Number.isInteger(year) || year < 1900 || year > 9999) {
    res.status(400).json({ error: "Năm không hợp lệ" });
    return null;
  }

  if (!Number.isInteger(month) || month < 1 || month > 12) {
    res.status(400).json({ error: "Tháng không hợp lệ" });
    return null;
  }

  return { year, month };
};

const executeReportProcedure = async (req, res, procedureName, context) => {
  const params = parseYearMonth(req, res);
  if (!params) {
    return;
  }

  try {
    const request = new sql.Request();
    request.input("Year", sql.Int, params.year);
    request.input("Month", sql.Int, params.month);

    const result = await request.execute(procedureName);
    return res.json(result.recordset?.[0] || {});
  } catch (err) {
    console.error(`${context} Error:`, err);
    return res.status(500).json({ error: "Lỗi server", detail: err.message });
  }
};

const executeReportListProcedure = async (req, res, procedureName, context) => {
  const params = parseYearMonth(req, res);
  if (!params) {
    return;
  }

  try {
    const request = new sql.Request();
    request.input("Year", sql.Int, params.year);
    request.input("Month", sql.Int, params.month);

    const result = await request.execute(procedureName);
    return res.json(result.recordset || []);
  } catch (err) {
    console.error(`${context} Error:`, err);
    return res.status(500).json({ error: "Lỗi server", detail: err.message });
  }
};

const getRoomOccupancyByMonth = async (req, res) =>
  executeReportProcedure(
    req,
    res,
    "sp_GetRoomOccupancyByMonth",
    "getRoomOccupancyByMonth",
  );

const getNetRevenueByMonth = async (req, res) =>
  executeReportProcedure(
    req,
    res,
    "sp_GetNetRevenueByMonth",
    "getNetRevenueByMonth",
  );

const getGuestTypeByMonth = async (req, res) =>
  executeReportProcedure(
    req,
    res,
    "sp_GetGuestTypeByMonth",
    "getGuestTypeByMonth",
  );

const getReservationCountByMonth = async (req, res) =>
  executeReportProcedure(
    req,
    res,
    "sp_GetReservationCountByMonth",
    "getReservationCountByMonth",
  );

const getRevenueByDayInMonth = async (req, res) =>
  executeReportListProcedure(
    req,
    res,
    "sp_GetRevenueByDayInMonth",
    "getRevenueByDayInMonth",
  );

const getRevenueByCustomerType = async (req, res) =>
  executeReportListProcedure(
    req,
    res,
    "sp_GetRevenueByCustomerType",
    "getRevenueByCustomerType",
  );

const getRevenueByRoomTypeInMonth = async (req, res) =>
  executeReportListProcedure(
    req,
    res,
    "sp_GetRevenueByRoomTypeInMonth",
    "getRevenueByRoomTypeInMonth",
  );

const getRoomTypeUsagePercentInMonth = async (req, res) =>
  executeReportListProcedure(
    req,
    res,
    "sp_GetRoomTypeUsagePercentInMonth",
    "getRoomTypeUsagePercentInMonth",
  );

module.exports = {
  getRoomOccupancyByMonth,
  getNetRevenueByMonth,
  getGuestTypeByMonth,
  getReservationCountByMonth,
  getRevenueByDayInMonth,
  getRevenueByCustomerType,
  getRevenueByRoomTypeInMonth,
  getRoomTypeUsagePercentInMonth,
};
