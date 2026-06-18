const { sql } = require("../config/db");

const extractSqlErrorMessage = (err) => {
  return (
    err?.originalError?.info?.message ||
    err?.precedingErrors?.[0]?.message ||
    err?.message ||
    "Loi server"
  );
};

const toDateString = (value) => {
  if (typeof value !== "string") {
    return null;
  }

  const normalized = value.trim();
  if (!/^\d{4}-\d{2}-\d{2}$/.test(normalized)) {
    return null;
  }

  const [year, month, day] = normalized.split("-").map(Number);
  const parsedDate = new Date(`${normalized}T00:00:00Z`);

  if (Number.isNaN(parsedDate.getTime())) {
    return null;
  }

  if (
    parsedDate.getUTCFullYear() !== year ||
    parsedDate.getUTCMonth() + 1 !== month ||
    parsedDate.getUTCDate() !== day
  ) {
    return null;
  }

  return normalized;
};

const updateUserCustomerInfo = async (req, res) => {
  console.log("updateUserCustomerInfo called", req.params, req.body);
  try {
    const userID = Number.parseInt(req.params.userId, 10);
    const { FullName, Phone, Email } = req.body;

    if (
      !Number.isInteger(userID) ||
      userID <= 0 ||
      !FullName ||
      !Phone ||
      !Email
    ) {
      return res.status(400).json({
        error: "Du lieu khong hop le. Can userId, FullName, Phone, Email",
      });
    }

    await sql.query`
      EXEC sp_UpdateUserCustomerInfo
        @UserID=${userID},
        @FullName=${FullName},
        @Phone=${Phone},
        @Email=${Email}
    `;

    return res.json({ message: "Cap nhat thong tin khach hang thanh cong" });
  } catch (err) {
    console.error("updateUserCustomerInfo Error:", err);
    const message = extractSqlErrorMessage(err);
    return res.status(400).json({ error: message });
  }
};

const changePassword = async (req, res) => {
  console.log("changePassword called", req.params, req.body);
  try {
    const userID = Number.parseInt(req.params.userId, 10);
    const { oldPassword, newPassword } = req.body;

    if (!Number.isInteger(userID) || userID <= 0 || !oldPassword || !newPassword) {
      return res.status(400).json({
        error: "Du lieu khong hop le. Can userId, oldPassword, newPassword",
      });
    }

    await sql.query`
      EXEC sp_ChangePassword
        @UserID=${userID},
        @OldPassword=${oldPassword},
        @NewPassword=${newPassword}
    `;

    return res.json({ message: "Doi mat khau thanh cong" });
  } catch (err) {
    console.error("changePassword Error:", err);
    const message = extractSqlErrorMessage(err);
    return res.status(400).json({ error: message });
  }
};

module.exports = {
  updateUserCustomerInfo,
  changePassword,
};