const express = require("express");
const router = express.Router();

const {
  getCustomersFullInfo,
  insertCustomer,
  getReservationsByUser,
  getCustomerInfoByUserID,
  updateCustomerProfile,
  updateCustomer,
} = require("../controllers/customerController");

router.get("/", getCustomersFullInfo);
router.get("/info/:userId", getCustomerInfoByUserID);
router.get("/:userId/reservations", getReservationsByUser);
router.put("/profile/:userId", updateCustomerProfile);
router.post("/", insertCustomer);
router.put("/:id", updateCustomer);

module.exports = router;
