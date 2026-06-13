const { sql } = require("../config/db");
const crypto = require("crypto");

const REPLACEMENT_CHAR_PATTERN = /\uFFFD/g;
const MOJIBAKE_MARKER_PATTERN = /\u00C3|\u00C6|\u00C2|\u00EF\u00BF\u00BD/g;

const normalizeMessage = (value) => String(value || "").replace(/\s+/g, " ").trim();

const countBrokenEncodingMarkers = (value) => {
  if (!value) return Number.POSITIVE_INFINITY;

  const replacementCharCount = (value.match(REPLACEMENT_CHAR_PATTERN) || []).length;
  const mojibakeCharCount = (value.match(MOJIBAKE_MARKER_PATTERN) || []).length;
  return replacementCharCount * 5 + mojibakeCharCount * 3;
};

const tryRepairMojibake = (value) => {
  const message = normalizeMessage(value);
  if (!message) return "";

  try {
    return normalizeMessage(Buffer.from(message, "latin1").toString("utf8"));
  } catch {
    return message;
  }
};

const pickReadableMessage = (value) => {
  const original = normalizeMessage(value);
  const repaired = tryRepairMojibake(original);

  return countBrokenEncodingMarkers(repaired) < countBrokenEncodingMarkers(original)
    ? repaired
    : original;
};

const resolveFallbackMessage = (err, message) => {
  const sqlNumber =
    err?.number || err?.originalError?.info?.number || err?.originalError?.code;
  const normalized = normalizeMessage(message).toLowerCase();

  if (sqlNumber === 2627 || sqlNumber === 2601 || /unique key|duplicate/.test(normalized)) {
    return "Dữ liệu đã tồn tại";
  }

  if (sqlNumber === 50000) {
    return "Dữ liệu không hợp lệ theo quy tắc nghiệp vụ";
  }

  if (countBrokenEncodingMarkers(message) > 0) {
    return "Có lỗi dữ liệu từ SQL Server";
  }

  return "";
};

const extractSqlErrorMessage = (err) => {
  const raw =
    err?.originalError?.info?.message ||
    err?.precedingErrors?.[0]?.message ||
    err?.message ||
    "";
  const readable = pickReadableMessage(raw);
  const fallback = resolveFallbackMessage(err, readable);

  return fallback || readable || "Lỗi server";
};

const toPositiveInt = (value) => {
  const parsed = Number.parseInt(value, 10);
  if (!Number.isInteger(parsed) || parsed <= 0) {
    return null;
  }

  return parsed;
};

const toNullableTrimmedString = (value) => {
  if (value === undefined || value === null) {
    return null;
  }

  const trimmed = String(value).trim();
  return trimmed.length > 0 ? trimmed : null;
};

const getCustomersFullInfo = async (req, res) => {
  console.log("getCustomersFullInfo called");
  try {
    const result = await sql.query`EXEC sp_GetCustomersFullInfo`;
    return res.json(result.recordset || []);
  } catch (err) {
    console.error("getCustomersFullInfo Error:", err);
    return res
      .status(500)
      .json({ error: "Lỗi server", detail: extractSqlErrorMessage(err) });
  }
};

const insertCustomer = async (req, res) => {
  console.log("insertCustomer called", req.body);
  try {
    const { FullName, Phone, CCCD, Email } = req.body;

    if (!FullName || !Phone || !CCCD || !Email) {
      return res.status(400).json({
        error: "Thiếu tham số bắt buộc: FullName, Phone, CCCD, Email",
      });
    }

    await sql.query`
      EXEC sp_Customers_Insert
        @FullName=${FullName},
        @Phone=${Phone},
        @CCCD=${CCCD},
        @Email=${Email}
    `;

    return res
      .status(201)
      .json({ message: "Thêm khách hàng thành công" });
  } catch (err) {
    console.error("insertCustomer Error:", err);
    const message = extractSqlErrorMessage(err);
    return res.status(400).json({ error: message });
  }
};

const getReservationsByUser = async (req, res) => {
  console.log("getReservationsByUser called", req.params, req.query);
  try {
    const userID = toPositiveInt(req.params.userId || req.query.userId || req.query.UserID);

    if (!userID) {
      return res.status(400).json({ error: "UserID không hợp lệ" });
    }

    const request = new sql.Request();
    request.input("UserID", sql.Int, userID);

    const result = await request.execute("sp_GetReservationsByUser2");
    return res.json(result.recordset || []);
  } catch (err) {
    console.error("getReservationsByUser Error:", err);
    return res
      .status(500)
      .json({ error: "Lỗi server", detail: extractSqlErrorMessage(err) });
  }
};

const getCustomerInfoByUserID = async (req, res) => {
  console.log("getCustomerInfoByUserID called", req.params, req.query);
  try {
    const userID = toPositiveInt(req.params.userId || req.query.userId || req.query.UserID);

    if (!userID) {
      return res.status(400).json({ error: "UserID không hợp lệ" });
    }

    const request = new sql.Request();
    request.input("UserID", sql.Int, userID);

    const result = await request.execute("sp_GetCustomerInfoByUserID2");
    const customerInfo = result.recordset?.[0];

    if (!customerInfo) {
      return res.status(404).json({ error: "Không tìm thấy thông tin khách hàng" });
    }

    return res.json(customerInfo);
  } catch (err) {
    console.error("getCustomerInfoByUserID Error:", err);
    return res
      .status(500)
      .json({ error: "Lỗi server", detail: extractSqlErrorMessage(err) });
  }
};

const updateCustomerProfile = async (req, res) => {
  console.log("updateCustomerProfile called", req.params, req.body);
  try {
    const userID = toPositiveInt(req.params.userId || req.body?.userId || req.body?.UserID);
    if (!userID) {
      return res.status(400).json({ error: "UserID không hợp lệ" });
    }

    const email = toNullableTrimmedString(req.body?.Email);
    const fullName = toNullableTrimmedString(req.body?.FullName);
    const phone = toNullableTrimmedString(req.body?.Phone);
    const cccd = toNullableTrimmedString(req.body?.CCCD);
    const password = toNullableTrimmedString(req.body?.Password);
    const passwordHashInput = toNullableTrimmedString(req.body?.PasswordHash);
    const passwordHash = passwordHashInput || (password ? crypto.createHash("sha256").update(password).digest("hex") : null);

    if (!email && !fullName && !phone && !cccd && !passwordHash) {
      return res.status(400).json({
        error: "Cần ít nhất 1 trường để cập nhật: Email, PasswordHash/Password, FullName, Phone, CCCD",
      });
    }

    const checkRequest = new sql.Request();
    checkRequest.input("UserID", sql.Int, userID);
    const checkResult = await checkRequest.query(`
      SELECT TOP 1 c.UserID
      FROM Customers c
      WHERE c.UserID = @UserID
    `);

    if (!checkResult.recordset?.length) {
      return res.status(404).json({ error: "Không tìm thấy khách hàng" });
    }

    const request = new sql.Request();
    request.input("UserID", sql.Int, userID);
    request.input("Email", sql.NVarChar(150), email);
    request.input("PasswordHash", sql.NVarChar(255), passwordHash);
    request.input("FullName", sql.NVarChar(150), fullName);
    request.input("Phone", sql.NVarChar(20), phone);
    request.input("CCCD", sql.NVarChar(20), cccd);

    await request.execute("sp_UpdateCustomerProfile2");

    return res.json({
      message: "Cập nhật thông tin khách hàng thành công",
      data: {
        UserID: userID,
        Email: email,
        FullName: fullName,
        Phone: phone,
        CCCD: cccd,
      },
    });
  } catch (err) {
    console.error("updateCustomerProfile Error:", err);
    const message = extractSqlErrorMessage(err);

    if (/email/i.test(message) && /ton tai|trung|exist/i.test(message)) {
      return res.status(400).json({ error: "Email đã tồn tại" });
    }

    return res.status(400).json({ error: message || "Lỗi server" });
  }
};

const updateCustomer = async (req, res) => {
  console.log("updateCustomer called", req.params, req.body);
  try {
    const customerID = Number.parseInt(req.params.id, 10);
    const { FullName, Phone, CCCD, Email } = req.body;

    if (
      !Number.isInteger(customerID) ||
      customerID <= 0 ||
      !FullName ||
      !Phone ||
      !CCCD ||
      !Email
    ) {
      return res.status(400).json({
        error: "Dữ liệu không hợp lệ. Cần id, FullName, Phone, CCCD, Email",
      });
    }

    await sql.query`
      EXEC sp_Customers_Update
        @CustomerID=${customerID},
        @FullName=${FullName},
        @Phone=${Phone},
        @CCCD=${CCCD},
        @Email=${Email}
    `;

    return res.json({ message: "Cập nhật khách hàng thành công" });
  } catch (err) {
    console.error("updateCustomer Error:", err);
    const message = extractSqlErrorMessage(err);
    return res.status(400).json({ error: message });
  }
};

module.exports = {
  getCustomersFullInfo,
  insertCustomer,
  getReservationsByUser,
  getCustomerInfoByUserID,
  updateCustomerProfile,
  updateCustomer,
};
