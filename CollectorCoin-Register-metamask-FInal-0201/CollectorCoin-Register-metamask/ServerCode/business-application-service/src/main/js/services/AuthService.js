const createBaseRequest = require("../common/http-common"); // 使用我们加强过的 http 工具

class AuthService {
  login(username, password) {
    console.log(">>> [AuthService] 正在尝试登录:", username);
    // 使用 createBaseRequest() 自动带上日志拦截器
    return createBaseRequest().post("/auth/signin", {
      username,
      password,
    });
  }

  logout() {
    return createBaseRequest().post("/auth/signout");
  }

  register(username, email, password, role) {
    return createBaseRequest().post("/auth/signup", {
      username,
      email,
      password,
      role,
    });
  }

  getCurrentUser() {
    return JSON.parse(localStorage.getItem('user'));;
  }
}

module.exports = new AuthService();