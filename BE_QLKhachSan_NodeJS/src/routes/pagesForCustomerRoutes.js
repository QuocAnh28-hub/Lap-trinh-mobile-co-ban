const express = require("express");
const router = express.Router();

const {
  updateUserCustomerInfo,
  changePassword,
} = require("../controllers/pagesForCustomerController");

router.put("/user/:userId", updateUserCustomerInfo);
router.put("/user/:userId/password", changePassword);

module.exports = router;
