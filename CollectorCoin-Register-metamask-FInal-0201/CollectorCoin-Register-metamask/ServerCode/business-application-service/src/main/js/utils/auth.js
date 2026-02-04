// ❌ 删除这就话，它是循环依赖的罪魁祸首！
// const store = require("../app/configureStore");

function getAuthHeader() {
  // ✅ 直接从浏览器缓存读取用户信息，避开 Redux Store 依赖
  const userStr = localStorage.getItem("user");
  
  if (userStr) {
    try {
      const user = JSON.parse(userStr);
      // 确保 user 对象存在且有 accessToken
      if (user && user.accessToken) {
        return { Authorization: `Bearer ${user.accessToken}` };
      }
    } catch (e) {
      console.error("Error parsing user from localStorage", e);
    }
  }
  
  return {};
}

module.exports = {
  getAuthHeader,
};