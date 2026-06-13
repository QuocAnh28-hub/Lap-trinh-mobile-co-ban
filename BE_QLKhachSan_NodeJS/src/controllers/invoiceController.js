const { sql } = require("../config/db");

const toPositiveInt = (value) => {
  const parsed = Number.parseInt(value, 10);
  if (!Number.isInteger(parsed) || parsed <= 0) {
    return null;
  }
  return parsed;
};

const toVatPercent = (value) => {
  const parsed = Number.parseFloat(value);
  if (!Number.isFinite(parsed) || parsed < 0 || parsed > 100) {
    return null;
  }
  return parsed;
};

const toPaymentMethod = (value) => {
  if (typeof value !== "string") {
    return null;
  }

  const normalized = value.trim();
  if (!normalized || normalized.length > 20) {
    return null;
  }

  return normalized;
};

const getPendingInvoices = async (req, res) => {
  console.log("getPendingInvoices called");
  try {
    const request = new sql.Request();
    const result = await request.execute("sp_GetPendingInvoices");

    return res.json(result.recordset || []);
  } catch (err) {
    console.error("getPendingInvoices Error:", err);
    return res.status(500).json({ error: "Lỗi server", detail: err.message });
  }
};

const getInvoiceHistory = async (req, res) => {
  console.log("getInvoiceHistory called");
  try {
    const request = new sql.Request();
    const result = await request.execute("sp_GetInvoiceHistory");

    return res.json(result.recordset || []);
  } catch (err) {
    console.error("getInvoiceHistory Error:", err);
    return res.status(500).json({ error: "Lỗi server", detail: err.message });
  }
};

const getFullInvoiceByStayID = async (req, res) => {
  console.log("getFullInvoiceByStayID called", req.params, req.query);
  try {
    const stayID = toPositiveInt(req.params.stayId || req.query.stayId);
    if (!stayID) {
      return res.status(400).json({ error: "StayID không hợp lệ" });
    }

    const request = new sql.Request();
    request.input("StayID", sql.Int, stayID);
    const result = await request.execute("sp_GetFullInvoice_ByStayID2");

    const invoice = result.recordsets?.[0]?.[0] || null;
    const details = result.recordsets?.[1] || [];

    return res.json({ invoice, details });
  } catch (err) {
    console.error("getFullInvoiceByStayID Error:", err);
    const sqlMessage = err?.originalError?.info?.message || err?.message;

    if (err?.originalError?.info?.message || err?.precedingErrors?.length) {
      return res.status(400).json({ error: sqlMessage });
    }

    return res.status(500).json({ error: "Lỗi server", detail: sqlMessage });
  }
};

const createAndPayInvoice = async (req, res) => {
  console.log("createAndPayInvoice called", req.body);
  try {
    const stayID = toPositiveInt(req.body.StayID || req.body.stayId);
    const method = toPaymentMethod(
      req.body.Method || req.body.method || req.body.PaymentMethod,
    );
    const vat = toVatPercent(req.body.VAT || req.body.vat);

    if (!stayID || !method || vat == null) {
      return res.status(400).json({
        error: "Thiếu hoặc sai tham số: StayID, Method, VAT(0-100)",
      });
    }

    const request = new sql.Request();
    request.input("StayID", sql.Int, stayID);
    request.input("Method", sql.NVarChar(20), method);
    request.input("VAT", sql.Decimal(5, 2), vat);

    const result = await request.execute("sp_CreateAndPayInvoice");
    const payload = result.recordset?.[0] || null;

    return res
      .status(201)
      .json(payload || { message: "Tạo và thanh toán hóa đơn thành công" });
  } catch (err) {
    console.error("createAndPayInvoice Error:", err);
    const sqlMessage = err?.originalError?.info?.message || err?.message;

    if (err?.originalError?.info?.message || err?.precedingErrors?.length) {
      return res.status(400).json({ error: sqlMessage });
    }

    return res.status(500).json({ error: "Lỗi server", detail: sqlMessage });
  }
};

module.exports = {
  getPendingInvoices,
  getInvoiceHistory,
  getFullInvoiceByStayID,
  createAndPayInvoice,
};
