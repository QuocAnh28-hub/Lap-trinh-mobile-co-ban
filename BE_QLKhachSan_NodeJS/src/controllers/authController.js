const { sql } = require("../config/db");
const crypto = require("crypto");

const hashToSha256 = (raw) => crypto.createHash("sha256").update(raw).digest("hex");

const getSqlErrorNumber = (err) =>
  err?.number || err?.originalError?.info?.number || err?.originalError?.code || null;

const getSqlErrorMessage = (err) =>
  err?.originalError?.info?.message || err?.precedingErrors?.[0]?.message || err?.message || "";

const parseRequestBody = (req) => {
  if (req?.body && typeof req.body === "object" && !Array.isArray(req.body)) {
    return req.body;
  }

  return null;
};

const login = async (req, res) => {
  console.log("login called", req.body);

  try {
    const body = parseRequestBody(req);
    if (!body) {
      return res.status(400).json({
        error: "Body phải là JSON hợp lệ. Hãy chọn Body -> raw -> JSON trong Postman.",
      });
    }

    const { Email, PasswordHash, Password } = body;
    const email = (Email || "").trim();
    const passwordHash = (PasswordHash || "").trim();
    const password = (Password || "").trim();

    if (!email || (!passwordHash && !password)) {
      return res.status(400).json({
        error: "Email và mật khẩu (Password hoặc PasswordHash) là bắt buộc",
      });
    }

    // Sử dụng PasswordHash nếu có, nếu không thì hash Password thành SHA256
    let finalPasswordHash = passwordHash;
    if (!finalPasswordHash && password) {
      finalPasswordHash = hashToSha256(password);
    }

    console.log(
      `Trying stored proc GetAccountInfo with Email: ${email}, PasswordHash: ${finalPasswordHash}`,
    );
    const result = await sql.query`
      EXEC GetAccountInfo
        @Email=${email},
        @PasswordHash=${finalPasswordHash}
    `;

    // stored proc có 2 result set (Customers + Receptionists)
    const candidateRecordsets = result.recordsets || [];
    let account = null;
    for (const rs of candidateRecordsets) {
      if (rs && rs.length > 0) {
        account = rs[0];
        break;
      }
    }

    if (!account) {
      console.log("No account found, returning 401");
      return res.status(401).json({
        error: "Đăng nhập không thành công, Email hoặc mật khẩu sai.",
      });
    }

    console.log("Account found:", account);
    const { PasswordHash: _, ...payload } = account;

    return res.json({ message: "Đăng nhập thành công", account: payload });
  } catch (err) {
    console.error("login Error:", err);
    return res.status(500).json({ error: "Lỗi server", detail: err.message });
  }
};

const loginCustomer = async (req, res) => {
  console.log("loginCustomer called", req.body);

  try {
    const body = parseRequestBody(req);
    if (!body) {
      return res.status(400).json({
        error: "Body phải là JSON hợp lệ. Hãy chọn Body -> raw -> JSON trong Postman.",
      });
    }

    const { Email, PasswordHash, Password } = body;
    const email = (Email || "").trim();
    const passwordHash = (PasswordHash || "").trim();
    const password = (Password || "").trim();

    if (!email || (!passwordHash && !password)) {
      return res.status(400).json({
        error: "Email và mật khẩu (Password hoặc PasswordHash) là bắt buộc",
      });
    }

    const finalPasswordHash = passwordHash || hashToSha256(password);

    const result = await sql.query`
      EXEC GetAccountInfoCustomer
        @Email=${email},
        @PasswordHash=${finalPasswordHash}
    `;

    const account = result.recordset?.[0];
    if (!account) {
      return res.status(401).json({
        error: "Đăng nhập khách hàng không thành công, Email hoặc mật khẩu sai.",
      });
    }

    const { PasswordHash: _, ...payload } = account;
    return res.json({
      message: "Đăng nhập khách hàng thành công",
      account: payload,
    });
  } catch (err) {
    console.error("loginCustomer Error:", err);
    return res.status(500).json({ error: "Lỗi server", detail: err.message });
  }
};

const registerCustomer = async (req, res) => {
  console.log("registerCustomer called", req.body);

  try {
    const body = parseRequestBody(req);
    if (!body) {
      return res.status(400).json({
        error: "Body phải là JSON hợp lệ. Hãy chọn Body -> raw -> JSON trong Postman.",
      });
    }

    const { FullName, Phone, Email, PasswordHash, Password } = body;
    const fullName = (FullName || "").trim();
    const phone = (Phone || "").trim();
    const email = (Email || "").trim();
    const rawPasswordHash = (PasswordHash || "").trim();
    const password = (Password || "").trim();

    if (!fullName || !phone || !email) {
      return res.status(400).json({
        error: "FullName, Phone và Email là bắt buộc",
      });
    }

    if (!rawPasswordHash && !password) {
      return res.status(400).json({
        error: "Password hoặc PasswordHash là bắt buộc",
      });
    }

    const finalPasswordHash = rawPasswordHash || hashToSha256(password);

    await sql.query`
      EXEC sp_RegisterCustomer
        @FullName=${fullName},
        @Phone=${phone},
        @Email=${email},
        @PasswordHash=${finalPasswordHash}
    `;

    return res.status(201).json({
      message: "Đăng ký tài khoản khách hàng thành công",
      data: {
        FullName: fullName,
        Phone: phone,
        Email: email,
      },
    });
  } catch (err) {
    console.error("registerCustomer Error:", err);

    const sqlNumber = getSqlErrorNumber(err);
    const sqlMessage = getSqlErrorMessage(err);

    if (sqlNumber === 50000 || sqlNumber === 2627 || sqlNumber === 2601) {
      return res.status(400).json({
        error: sqlMessage || "Email đã tồn tại hoặc dữ liệu không hợp lệ",
      });
    }

    return res.status(500).json({
      error: "Lỗi server",
      detail: sqlMessage || err.message,
    });
  }
};

module.exports = { login, loginCustomer, registerCustomer };
