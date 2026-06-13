const { sql } = require("../config/db");

const normalizeStatus = (value) => {
  if (value == null) {
    return null;
  }

  if (typeof value !== "string") {
    return null;
  }

  const normalized = value.trim().toUpperCase();
  return normalized === "TRUE" || normalized === "FALSE" ? normalized : null;
};

const getServices = async (req, res) => {
  console.log("getServices called");
  try {
    const result = await sql.query`EXEC sp_GetActiveServices`;

    if (!result.recordset) {
      return res.status(404).json({ error: "Không tìm thấy dữ liệu dịch vụ" });
    }

    return res.json(result.recordset);
  } catch (err) {
    console.error("getServices Error:", err);
    return res.status(500).json({ error: "Lỗi server", detail: err.message });
  }
};

const addService = async (req, res) => {
  console.log("addService called", req.body);
  try {
    const { ServiceName, Price, Status } = req.body;
    const normalizedStatus = Status == null ? "TRUE" : normalizeStatus(Status);

    if (
      !ServiceName ||
      typeof ServiceName !== "string" ||
      !ServiceName.trim()
    ) {
      return res.status(400).json({ error: "ServiceName không hợp lệ" });
    }

    if (Price == null || Number.isNaN(Number(Price)) || Number(Price) < 0) {
      return res
        .status(400)
        .json({ error: "Price phải là số hợp lệ va khong am" });
    }

    if (!normalizedStatus) {
      return res
        .status(400)
        .json({ error: "Status chỉ chấp nhận TRUE hoặc FALSE" });
    }

    await sql.query`
      EXEC sp_InsertService
        @ServiceName=${ServiceName.trim()},
        @Price=${Number(Price)},
        @Status=${normalizedStatus}
    `;

    return res.status(201).json({ message: "Thêm dịch vụ thành công" });
  } catch (err) {
    console.error("addService Error:", err);
    return res.status(500).json({ error: "Lỗi server", detail: err.message });
  }
};

const updateService = async (req, res) => {
  console.log("updateService called", req.params, req.body);
  try {
    const serviceID = Number.parseInt(req.params.id, 10);
    const { ServiceName, Price } = req.body;

    if (!Number.isInteger(serviceID) || serviceID <= 0) {
      return res.status(400).json({ error: "ServiceID không hợp lệ" });
    }

    if (
      !ServiceName ||
      typeof ServiceName !== "string" ||
      !ServiceName.trim()
    ) {
      return res.status(400).json({ error: "ServiceName không hợp lệ" });
    }

    if (Price == null || Number.isNaN(Number(Price)) || Number(Price) < 0) {
      return res
        .status(400)
        .json({ error: "Price phải là số hợp lệ va khong am" });
    }

    const currentServiceResult = await sql.query`
      SELECT TOP 1 ServiceID, Status
      FROM Services
      WHERE ServiceID = ${serviceID}
    `;

    if (currentServiceResult.recordset.length === 0) {
      return res
        .status(404)
        .json({ error: "Không tìm thấy dịch vụ cần cập nhật" });
    }

    const currentService = currentServiceResult.recordset[0];
    const normalizedStatus = normalizeStatus(currentService.Status);

    await sql.query`
      EXEC sp_UpdateService
        @ServiceID=${serviceID},
        @ServiceName=${ServiceName.trim()},
        @Price=${Number(Price)},
        @Status=${normalizedStatus}
    `;

    return res.json({ message: "Cập nhật dịch vụ thành công" });
  } catch (err) {
    console.error("updateService Error:", err);
    return res.status(500).json({ error: "Lỗi server", detail: err.message });
  }
};

const deleteService = async (req, res) => {
  console.log("deleteService called", req.params);
  try {
    const serviceID = Number.parseInt(req.params.id, 10);

    if (!Number.isInteger(serviceID) || serviceID <= 0) {
      return res.status(400).json({ error: "ServiceID không hợp lệ" });
    }

    await sql.query`
      EXEC sp_DeleteService
        @ServiceID=${serviceID}
    `;

    return res.json({ message: "Xóa dịch vụ thành công" });
  } catch (err) {
    console.error("deleteService Error:", err);
    return res.status(500).json({ error: "Lỗi server", detail: err.message });
  }
};

module.exports = { getServices, addService, updateService, deleteService };
